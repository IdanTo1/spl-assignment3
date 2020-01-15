package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionHandler;

/**
 * This is a wrapper class for a client. It is basically just a set of strings with an equals operator for comfort
 */
public class Client {
    private String host;
    private String login;
    private String passcode;
    private boolean connected;

    public Client() {

    }

    public Client(String host, String login, String passcode) {
        this.host = host;
        this.login = login;
        this.passcode = passcode;
        this.connected = false;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Client)) return false;
        Client other = (Client) o;
        return other.host.equals(host) && other.login.equals(login) && other.passcode.equals(passcode);
    }

    public String getHost() {
        return host;
    }

    public String getLogin() {
        return login;
    }

    public String getPasscode() {
        return passcode;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


}
