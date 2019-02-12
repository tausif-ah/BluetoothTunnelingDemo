package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;

import java.net.InetAddress;
import java.net.Socket;

public class ServerConnector extends Thread {
    private InetAddress serverAddr;
    private int serverPort;
    private MainActivity mainActivity;

    ServerConnector(InetAddress serverAddr, int serverPort, MainActivity mainActivity) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try {
            Socket socketToServer = new Socket(serverAddr,serverPort);
            socketToServer.setKeepAlive(true);
            mainActivity.WDSocketCreated(socketToServer);
        } catch (Exception ex) {
            Log.e("server socket creation", ex.getMessage());
        }
    }
}
