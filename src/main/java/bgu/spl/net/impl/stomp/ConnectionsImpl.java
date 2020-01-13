package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl implements Connections<Frame> {

    private ConcurrentHashMap<Integer, ConnectionHandler<Frame>> _connections;
    // SimpleEntry used as a Pair object, _channels contains a map from the channel name to the pair
    // (User Id, Subscription Id) which is the unique key of a subscription (as stated in forum)
    private ConcurrentHashMap<String, List<AbstractMap.SimpleEntry<Integer, Integer>>> _channels;

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
        for(AbstractMap.SimpleEntry<Integer, Integer> sub : _channels.get(channel)) {
            this.send(sub.getKey(), addSubscriptionId(msg, sub.getValue())); // Send the message with the subscription header
        }
    }

    @Override
    public void disconnect(int connectionId) {
        for(List<AbstractMap.SimpleEntry<Integer, Integer>> topic : _channels.values()) {
            topic.remove(connectionId);
        }
        _connections.remove(connectionId);
    }

    @Override
    public void subscribe(String channel, int connectionId, int subscriptionId) {
        if(_channels.get(channel) == null) {
            synchronized (this) { // We sync on the entire object because the channel is null and we can't sync on null
                _channels.computeIfAbsent(channel, k -> new ArrayList<>());
            }
        }
        _channels.get(channel).add(new AbstractMap.SimpleEntry<>(connectionId, subscriptionId));
    }

    @Override
    public void connect(ConnectionHandler<Frame> connectionHandler, int connectionId) {
        _connections.put(connectionId, connectionHandler);
    }

    @Override
    public void unsubscribe(String channel, int connectionId, int subscriptionId) {
        // SimpleEntry is comparable and implements a deep compare
        _channels.get(channel).remove(new AbstractMap.SimpleEntry<>(connectionId, subscriptionId));
    }
}
