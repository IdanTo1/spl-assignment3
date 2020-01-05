package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionHandler;

/**
 * This is a wrapper class for a client. It is basically just a set of strings with an equals operator for comfort
 */
public class Client {
    public String host;
    public String login;
    public String passcode;

    public Client() {

    }

    public Client(String host, String login, String passcode) {
        this.host = host;
        this.login = login;
        this.passcode = passcode;
    }
    @Override
    public boolean equals(Object o) {
        Client other = (Client) o;
        return other.host == host && other.login == login && other.passcode == passcode;
    }

}
