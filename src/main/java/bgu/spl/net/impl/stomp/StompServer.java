package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {
        if(args.length != 3)
        {
           throw new IllegalArgumentException("Usage - StompServer.java host port server_type");
        }
        Server s = null;
        if(args[2].equals("tpc")) {
            s = Server.threadPerClient(Integer.parseInt(args[1]),
                    () -> new LibraryProtocol(),
                    ()-> new StompEncDec());
        }
        else if(args[2].equals("reactor")) {
            s = Server.reactor(4, // TODO: Check how many threads should be used
                    Integer.parseInt(args[1]),
                    () -> new LibraryProtocol(),
                    () -> new StompEncDec());
        }
        s.serve();

    }


}
