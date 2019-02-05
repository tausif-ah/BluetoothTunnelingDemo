package tausif.androidprojects.bluetoothtunnelingdemo;

import java.net.InetAddress;
import java.net.Socket;

class WDClient {
    public InetAddress IPAddr;
    public int port;
    private Socket connectedSocket;

    WDClient(InetAddress ipAddr, int port, Socket connectedSocket) {
        this.IPAddr = ipAddr;
        this.port = port;
        this.connectedSocket = connectedSocket;
    }
}
