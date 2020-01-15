package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {
        if(args.length != 2)
        {
            throw new IllegalArgumentException("Usage - StompServer.java port server_type");
        }
        Server s = null;
        if(args[1].equals("tpc")) {
            s = Server.threadPerClient(Integer.parseInt(args[1]),
                    () -> new LibraryProtocol(),
                    ()-> new StompEncDec(),
                    new ConnectionsImpl());
        }
        else if(args[1].equals("reactor")) {
            s = Server.reactor(4, // TODO: Check how many threads should be used
                    Integer.parseInt(args[0]),
                    () -> new LibraryProtocol(),
                    () -> new StompEncDec(),
                    new ConnectionsImpl());
        }
        s.serve();

    }


}
