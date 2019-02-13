package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WDTCPSender extends Thread {

    String message;
    Socket socket;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            byte[] buffer = message.getBytes();
            outputStream.write(buffer, 0, buffer.length);
            outputStream.flush();
        } catch (Exception ex) {
            Log.e("output stream", ex.getMessage());
        }
    }
}
