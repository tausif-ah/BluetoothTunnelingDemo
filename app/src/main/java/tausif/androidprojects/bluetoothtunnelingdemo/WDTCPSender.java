package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WDTCPSender extends Thread {

    Socket socket;
    private ServerMessage message;

    void setMessage(ServerMessage message) {
        this.message = message;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (Exception ex) {
            Log.e("output stream", ex.getMessage());
        }
    }
}
