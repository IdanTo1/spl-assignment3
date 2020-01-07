package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> _connections;
    private ConcurrentHashMap<String, List<Integer>> _channels;

    public ConnectionsImpl() {
        _connections = new ConcurrentHashMap<>();
        _channels = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        try {
            _connections.get(connectionId).send(msg);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void send(String channel, T msg) {
        for(Integer i : _channels.get(channel)) {
            _connections.get(i).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        for(List<Integer> topic : _channels.values()) {
            topic.remove(connectionId);
        }
        _connections.remove(connectionId);
    }

    @Override
    public void subscribe(String channel, int connectionId) {
        synchronized (this) {
            _channels.computeIfAbsent(channel, k -> new ArrayList<>());
        }
        _channels.get(channel).add(connectionId);
    }

    @Override
    public void connect(ConnectionHandler<T> connectionHandler, int connectionId) {
        _connections.put(connectionId, connectionHandler);
    }
}
