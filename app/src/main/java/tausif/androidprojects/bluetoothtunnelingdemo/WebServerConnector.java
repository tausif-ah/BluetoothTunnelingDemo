package tausif.androidprojects.bluetoothtunnelingdemo;

import java.net.InetAddress;
import java.net.Socket;

public class WebServerConnector extends Thread {
    private InetAddress serverAddr;
    private int serverPort;
    private Socket clientSocket;

    WebServerConnector(InetAddress serverAddr, int serverPort) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(serverAddr,serverPort);
            clientSocket.setKeepAlive(true);
        } catch (Exception ex) {

        }
    }
}
