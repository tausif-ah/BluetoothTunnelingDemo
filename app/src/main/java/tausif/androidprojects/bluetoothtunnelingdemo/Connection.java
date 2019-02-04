package tausif.androidprojects.bluetoothtunnelingdemo;

import java.net.Socket;

public class Connection {
    Socket connectedSocket;
    Device connectedDevice;

    Connection(Socket connectedSocket, Device connectedDevice) {
        this.connectedSocket = connectedSocket;
        this.connectedDevice = connectedDevice;
    }
}
