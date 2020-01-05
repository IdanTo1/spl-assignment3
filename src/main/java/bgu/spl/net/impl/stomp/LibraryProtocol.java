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
        switch(msg.getCommand()) {
            case CONNECT:
                if(!msg.getHeader("accept-version").equals("1.2")){
                    _shouldTerminate = true;
                    _connections.send(_connectionId, new Frame("ERROR", "Wrong Version"));
                }
                else if((_client = clientsByLogin.get(msg.getHeader("login"))) == null) {
                    _client = new Client(msg.getHeader("host"), msg.getHeader("login"), msg.getHeader("passcode"));
                   clientsByLogin.put(_client.login, _client);
                   Frame frame = new Frame("CONNECTED");
                   frame.addHeader("version", "1.2");
                   _connections.send(_connectionId, frame);
                }
                else if(_client.passcode != msg.getHeader("passcode")) {
                    _shouldTerminate = true;
                    _connections.send(_connectionId, new Frame("ERROR", "Wrong password"));
                }
                break;
        }
    }

    @Override
    public boolean shouldTerminate() {
        return _shouldTerminate;
    }
}
