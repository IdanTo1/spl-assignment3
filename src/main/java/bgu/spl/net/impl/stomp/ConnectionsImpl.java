package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl implements Connections<Frame> {

    private ConcurrentHashMap<Integer, ConnectionHandler<Frame>> _connections;
    // SimpleEntry used as a Pair object
    private ConcurrentHashMap<String, List<AbstractMap.SimpleEntry<ConnectionHandler<Frame>, Integer>>> _channels;

    public ConnectionsImpl() {
        _connections = new ConcurrentHashMap<>();
        _channels = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, Frame msg) {
        try {
            _connections.get(connectionId).send(msg);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private Frame addSubscriptionId(Frame msg, Integer id) {
        Frame f = msg.clone();
        f.addHeader("subscription", id.toString());
        return f;
    }

    @Override
    public void send(String channel, Frame msg) {
        for(AbstractMap.SimpleEntry<ConnectionHandler<Frame>, Integer> sub : _channels.get(channel)) {
            sub.getKey().send(addSubscriptionId(msg, sub.getValue())); // Send the message with the subscription header
        }
    }

    @Override
    public void disconnect(int connectionId) {
        for(List<AbstractMap.SimpleEntry<ConnectionHandler<Frame>, Integer>> topic : _channels.values()) {
            topic.remove(connectionId);
        }
        _connections.remove(connectionId);
    }

    @Override
    public void subscribe(String channel, int connectionId, int subscriptionId) {
        synchronized (this) {
            _channels.computeIfAbsent(channel, k -> new ArrayList<>());
        }
        _channels.get(channel).add(new AbstractMap.SimpleEntry<>(_connections.get(connectionId), subscriptionId));
    }

    @Override
    public void connect(ConnectionHandler<Frame> connectionHandler, int connectionId) {
        _connections.put(connectionId, connectionHandler);
    }
}
