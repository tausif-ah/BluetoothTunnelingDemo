package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;

import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

class WDConnection extends Thread {
    InetAddress IPAddr;
    private int port;
    boolean groupOwnerConnection;
    Socket connectedSocket;
    private MainActivity mainActivity;
    boolean isWebServerConnection;

    WDConnection(InetAddress ipAddr, int port, Socket connectedSocket, MainActivity mainActivity) {
        this.IPAddr = ipAddr;
        this.port = port;
        this.connectedSocket = connectedSocket;
        this.groupOwnerConnection = false;
        this.mainActivity = mainActivity;
        this.isWebServerConnection = false;
    }

    WDConnection(InetAddress ipAddr, Socket connectedSocket, boolean groupOwnerConnection, MainActivity mainActivity) {
        this.IPAddr = ipAddr;
        this.connectedSocket = connectedSocket;
        this.groupOwnerConnection = groupOwnerConnection;
        this.mainActivity = mainActivity;
        this.isWebServerConnection = false;
    }

    @Override
    public void run() {
        while (connectedSocket != null && connectedSocket.isConnected()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(connectedSocket.getInputStream());
                ServerMessage message = (ServerMessage)ois.readObject();
                message.srcAddress = connectedSocket.getInetAddress();
                this.IPAddr = message.srcAddress;
                mainActivity.messageInServerChannel(message);
            } catch (Exception ex) {
                Log.e("input stream", ex.getMessage());
            }
        }
    }
}
