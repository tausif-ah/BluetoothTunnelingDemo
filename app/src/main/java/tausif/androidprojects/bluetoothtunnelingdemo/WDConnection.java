package tausif.androidprojects.bluetoothtunnelingdemo;

import java.net.InetAddress;
import java.net.Socket;

class WDConnection {
    public InetAddress IPAddr;
    public int port;
    private Socket connectedSocket;

    WDConnection(InetAddress ipAddr, int port, Socket connectedSocket) {
        this.IPAddr = ipAddr;
        this.port = port;
        this.connectedSocket = connectedSocket;
    }
}
