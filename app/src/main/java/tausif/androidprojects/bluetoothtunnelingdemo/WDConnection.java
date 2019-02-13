package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;

class WDConnection extends Thread {
    InetAddress IPAddr;
    int port;
    boolean groupOwnerConnection;
    Socket connectedSocket;

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

    @Override
    public void run() {
        while (connectedSocket != null && connectedSocket.isConnected()) {
            DataInputStream dataInputStream;
            try {
                dataInputStream = new DataInputStream(connectedSocket.getInputStream());
                byte[] buffer = new byte[100];
                while (dataInputStream.read(buffer) > 0) {
                    String message = new String(buffer);
                    Log.d("message from", message);
                }
            } catch (Exception ex) {
                Log.e("input stream", ex.getMessage());
            }

        }
    }
}
