package tausif.androidprojects.bluetoothtunnelingdemo;

import java.net.InetAddress;
import java.net.Socket;

class WDConnection {
    public InetAddress IPAddr;
    public int port;
    public boolean groupOwnerConnection;
    private Socket connectedSocket;

    WDConnection(InetAddress ipAddr, int port, Socket connectedSocket) {
        this.IPAddr = ipAddr;
        this.port = port;
        this.connectedSocket = connectedSocket;
        this.groupOwnerConnection = false;
    }

    WDConnection(InetAddress ipAddr, Socket connectedSocket, boolean groupOwnerConnection) {
        this.IPAddr = ipAddr;
        this.connectedSocket = connectedSocket;
        this.groupOwnerConnection = groupOwnerConnection;
    }
}
