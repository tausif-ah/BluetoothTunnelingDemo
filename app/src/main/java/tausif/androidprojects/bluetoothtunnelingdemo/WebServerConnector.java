package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;

import java.net.InetAddress;
import java.net.Socket;

public class WebServerConnector extends Thread {
    private InetAddress serverAddr;
    private int serverPort;

    WebServerConnector(InetAddress serverAddr, int serverPort) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            Socket socketToServer = new Socket(serverAddr,serverPort);
            socketToServer.setKeepAlive(true);
        } catch (Exception ex) {
            Log.e("server socket creation", ex.getMessage());
        }
    }
}
