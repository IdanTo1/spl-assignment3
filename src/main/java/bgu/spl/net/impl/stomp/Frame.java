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

    public Frame(String command, String body) {
        _command = Command.valueOf(command);
        _headers = new HashMap<>();
        _body = body;
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

    public Map<String, String> getHeaders() {
        return _headers;
    }

    public String getHeader(String header) {
        return _headers.get(header);
    }

    public Command getCommand() {
        return _command;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(_command.name() + "\n");
        for (Map.Entry<String, String> header : _headers.entrySet()) {
            s.append(header.getKey()).append(":").append(header.getValue());
        }
        s.append(_body == null ? "" : _body);
        return s.toString();
    }

}
