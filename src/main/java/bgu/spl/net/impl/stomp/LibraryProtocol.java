package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;

import java.util.concurrent.ConcurrentHashMap;

public class LibraryProtocol implements StompMessagingProtocol<Frame> {

    private boolean _shouldTerminate = false;
    // Created here for concurrency reasons
    private static ConcurrentHashMap<String, Client> clientsByLogin = new ConcurrentHashMap<>();
    private Client _client;
    private int _connectionId;
    private Connections<Frame> _connections;
    private Integer _messagesSent;

    @Override
    public void start(int connectionId, Connections<Frame> connections) {
        _connectionId = connectionId;
        _connections = connections;
    }

    @Override
    public void process(Frame msg) {
        switch (msg.getCommand()) {
            case CONNECT:
                handleConnect(msg);
                break;
            case SEND:
                handleSend(msg);
                break;
            case SUBSCRIBE:
                handleSubscribe(msg);
                break;
            case UNSUBSCRIBE:
                handleUnsubscribe(msg);
                break;
            case DISCONNECT:
                handleDisconnect(msg);
                break;
        }
    }

    private void handleDisconnect(Frame msg) {
        sendReceipt(msg);
        _connections.disconnect(_connectionId);
        _client.connected = false;
        _shouldTerminate = true;
    }

    /**
     *  This function checks for the existence of the header {@code header}, if it doesn't exist the function sends the
     *  proper error Frame
     * @param msg The Frame to check for the header
     * @param header The header to be checked for existence
     * @return Does the header exist
     */
    private boolean checkForHeader(Frame msg, String header, String info) {
        if(msg.getHeader(header) == null) {
            Frame f = createErrorFrame("MalFormed Frame - missing " + header +" header");
            if(info != null) addErrorBody(msg, f, info);
            _connections.send(_connectionId,
                    f);
            return false;
        }
        return true;
    }

    private boolean checkForHeader(Frame msg, String header) {
        return checkForHeader(msg, header, null);
    }

    private void handleSubscribe(Frame msg) {
        if(!checkForHeader(msg, "destination")) return;
        if(!checkForHeader(msg, "id")) return;
        _connections.subscribe(msg.getHeader("destination"), _connectionId, Integer.parseInt(msg.getHeader("id")));
        sendReceipt(msg);
    }

    private void handleUnsubscribe(Frame msg) {
        if(!checkForHeader(msg, "id")) return;
        try {
            _connections.unsubscribe(msg.getHeader("destination"), _connectionId, Integer.parseInt(msg.getHeader("id")));
        }
        // We ignore a NullPointer exception because one will be thrown if the subscriptionId doesn't exist
        catch (NullPointerException ignored) {}
        sendReceipt(msg);
    }

    /**
     * Adds a receipt id needed according to {@code msg}'s headers
     *
     * @param msg      the received message
     */
    private void sendReceipt(Frame msg) {
        String receiptId = msg.getHeader("receipt");
        if (receiptId != null) {
            Frame newFrame = new Frame("RECEIPT");
            newFrame.addHeader("receipt-id", "message-" + receiptId);
        }
    }

    private Frame createErrorFrame(String message) {
        Frame f = new Frame("ERROR");
        f.addHeader("message", message);
        return f;
    }

    private Frame createMessageFrame(Frame msg) {
        Frame f = new Frame("MESSAGE");
        f.addHeader("Message-id", _messagesSent.toString());
        _messagesSent++;
        f.addHeader("destination", msg.getHeader("destination"));
        f.addBody(msg.getBody());
        return f;
    }

    private void handleSend(Frame msg) {
        if (!checkForHeader(msg, "destination")) return;
        _connections.send(msg.getHeader("destination"), createMessageFrame(msg));
        sendReceipt(msg);
    }

    private void addErrorBody(Frame msg, Frame errorFrame, String info) {
        errorFrame.addBody("The message:\n-----"+msg.toString()+"\n-----\n"+info);
    }

    private void handleConnect(Frame msg) {
        Frame f = new Frame();
        if ((_client = clientsByLogin.get(msg.getHeader("login"))) == null) {
            _client = new Client(msg.getHeader("host"), msg.getHeader("login"), msg.getHeader("passcode"));
            clientsByLogin.put(_client.login, _client);
            f = createConnectedFrame();
        } else if(_client.connected) {
            f.setCommand("ERROR");
            f.addHeader("message", "User already logged in");
            String receiptId = null;
            if((receiptId = msg.getHeader("receipt")) != null) f.addHeader("receipt-id", receiptId);
            f.addBody("User already logged in");
            _connections.send(_connectionId, f);
            _shouldTerminate = true;
        } else if (_client.passcode.equals(msg.getHeader("passcode"))) {
            f = createConnectedFrame();
        } else {
            f.setCommand("ERROR");
            f.addHeader("message", "Wrong password");
            String receiptId = null;
            if((receiptId = msg.getHeader("receipt")) != null) f.addHeader("receipt-id", receiptId);
            f.addBody("Wrong Password");
            _connections.send(_connectionId, f);
            _shouldTerminate = true;
        }
        _client.connected = true;
        sendReceipt(msg);
        _connections.send(_connectionId, f);
    }

    private Frame createConnectedFrame() {
        Frame frame = new Frame("CONNECTED");
        frame.addHeader("version", "1.2");
        return frame;
    }

    @Override
    public boolean shouldTerminate() {
        return _shouldTerminate;
    }
}
