package tausif.androidprojects.bluetoothtunnelingdemo;

import android.util.Log;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WDTCPSender extends Thread {

    private Socket socket;
    private Message message;

    void setMessage(Message message) {
        this.message = message;
    }

    void setSocket(Socket socket) {
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
