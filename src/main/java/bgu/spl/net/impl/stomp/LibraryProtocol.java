package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LibraryProtocol implements StompMessagingProtocol<Frame> {

    private boolean _shouldTerminate = false;
    private static ConcurrentHashMap<String, Client> clientsByLogin = new ConcurrentHashMap<>();
    private Client _client;
    private int _connectionId;
    private Connections<Frame> _connections;

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
        }
    }


    private void handleSend(Frame msg) {
        // TODO: Complete
    }

    private Frame handleConnect(Frame msg) {
        Frame f = new Frame();
        if ((_client = clientsByLogin.get(msg.getHeader("login"))) == null) {
            _client = new Client(msg.getHeader("host"), msg.getHeader("login"), msg.getHeader("passcode"));
            clientsByLogin.put(_client.login, _client);
            f = createConnectedFrame();
        } else if (_client.passcode.equals(msg.getHeader("passcode"))) {
            f = createConnectedFrame();
        } else {
            f.setCommand("ERROR");
            f.addHeader("message", "Wrong password");
            _connections.send(_connectionId, f);
            _shouldTerminate = true;
        }
        return addReceipt(msg, f);
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
