package bgu.spl.net.srv;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void send(String channel, T msg);

    void disconnect(int connectionId);

    // Required even for the most generic implementation
    void subscribe(String channel, int connectionId, int subscriptionId);

    // Required even for the most generic implementation
    void connect(ConnectionHandler<T> connectionHandler, int connectionId);

    void unsubscribe(String channel, int connectionId, int subscriptionId);
}
