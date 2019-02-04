package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServerListener extends Thread {
    private int listeningPort;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    WebServerListener(int listeningPort) {
        this.listeningPort = listeningPort;
        try {
            serverSocket = new ServerSocket(this.listeningPort);
        } catch (Exception ex) {

        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                clientSocket.setKeepAlive(true);
                InetAddress srcAddr = clientSocket.getInetAddress();
                int srcPort = clientSocket.getPort();
                Log.d("src address TCP", srcAddr.getHostAddress());
                Log.d("src port", Integer.toString(srcPort));
            } catch (Exception ex) {

            }
        }
    }
}
