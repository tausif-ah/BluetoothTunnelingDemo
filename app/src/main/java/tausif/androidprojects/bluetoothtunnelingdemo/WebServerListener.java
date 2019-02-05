package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServerListener extends Thread {
    private ServerSocket serverSocket;
    private MainActivity mainActivity;

    WebServerListener(int listeningPort, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        try {
            serverSocket = new ServerSocket(listeningPort);
        } catch (Exception ex) {
            Log.e("server socket creation", ex.getMessage());
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setKeepAlive(true);
                mainActivity.clientSocketCreated(clientSocket);
            } catch (Exception ex) {
                Log.e("listening for client", ex.getMessage());
            }
        }
    }
}
