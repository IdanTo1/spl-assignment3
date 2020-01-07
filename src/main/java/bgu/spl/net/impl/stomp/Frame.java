package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the frame that can be sent either way in the server-client connection.
 * It contains all the information represented by a generic stomp frame, as Java data structures
 */
public class Frame {
    public enum Command {
        SEND, MESSAGE, CONNECT, CONNECTED, RECEIPT, ERROR, SUBSCRIBE, UNSUBSCRIBE, DISCONNECT
    }

    private Command _command;
    private Map<String, String> _headers;
    private String _body;

    public Frame() {
        _headers = new HashMap<>();
    }

    public Frame(String command) {
        _command = Command.valueOf(command);
        _headers = new HashMap<>();
    }

    public Frame(String command, String body) { // Comfortable usage for quickly creating a Frame
        _command = Command.valueOf(command);
        _headers = new HashMap<>();
        _body = body;
    }

    public void setCommand(String command) {
        _command = Command.valueOf(command);
    }

    public void setCommand(Command command) {
        _command = command;
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

    /**
     *
     * @return The string representation of the Frame, as required to be sent by the server (without the null character
     * at the end, which will be added by the server
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(_command.name() + "\n");
        for (Map.Entry<String, String> header : _headers.entrySet()) {
            s.append(header.getKey()).append(":").append(header.getValue());
        }
        s.append(_body == null ? "" : _body);
        return s.toString();
    }

    public Frame clone() {
        Frame f = new Frame();
        f.setCommand(_command);
        f.addBody(_body);
        f.getHeaders().putAll(_headers);
        return f;
    }

}
