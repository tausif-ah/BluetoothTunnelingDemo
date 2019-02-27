package tausif.androidprojects.bluetoothtunnelingdemo;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BTConnectedSocketManager extends Thread {
    private final BluetoothSocket socket;
    private MainActivity homeActivity;

    BTConnectedSocketManager(BluetoothSocket socket, MainActivity homeActivity) {
        this.homeActivity = homeActivity;
        this.socket = socket;
    }

    void sendMessage(ServerMessage message) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (Exception ex) {
            Log.e("BT output stream", ex.getLocalizedMessage());
        }
    }

    public void run() {
        // Keep listening to the InputStream until an exception occurs.
        while (socket != null && socket.isConnected()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ServerMessage message = (ServerMessage)ois.readObject();
                // Read from the InputStream.
                homeActivity.BluetoothMessageReceived(message);
            } catch (Exception e) {
                Log.d("disconnection error",e.getLocalizedMessage());
                break;
            }
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("BT socket closing", e.getLocalizedMessage());
        }
    }
}
