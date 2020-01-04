package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;

public class Frame {
    public enum Command {
            SEND, MESSAGE, CONNECT, CONNECTED, RECEIPT, ERROR, SUBSCRIBE, UNSUBSCRIBE, DISCONNECT
    }
    private Command _command;
    private Map<String, String> _headers;
    private String _body;
    public Frame(String command) {
        _command = Command.valueOf(command);
        _headers = new HashMap<>();
    }

    public void addHeader(String header, String value) {
        _headers.put(header, value);
    }

    public void addBody(String body) {
        _body = body;
    }

    public String getBody() {
        return _body;
    }

    public Map<String, String> getHeader() {
        return _headers;
    }

    @Override
    public String toString() {
        String s = _command.name() + "\n";
        for(Pair<String, String> header : _headers.)
    }

}
