package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LibraryProtocol implements MessagingProtocol<Frame> {

    private boolean _shouldTerminate = false;
    private static ConcurrentHashMap<String, ConcurrentLinkedQueue<ConnectionHandler<String>>> clientsBySubscription;

    @Override
    public Frame process(Frame msg) {

        return null;
    }


    @Override
    public boolean shouldTerminate() {
        return _shouldTerminate;
    }
}
