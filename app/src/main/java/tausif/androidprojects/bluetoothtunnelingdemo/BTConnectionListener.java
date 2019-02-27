package tausif.androidprojects.bluetoothtunnelingdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BTConnectionListener extends Thread {
    private MainActivity homeActivity;
    private final BluetoothServerSocket mmServerSocket;
    BTConnectionListener(MainActivity homeActivity) {
        BluetoothServerSocket tmp = null;
        try {
            this.homeActivity = homeActivity;
            // MY_UUID is the app's UUID string, also used by the client code.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("connection listener", UUID.fromString(Constants.MY_UUID));
        } catch (IOException e) {
            Log.e("bluetooth listening", e.getLocalizedMessage());
        }
        mmServerSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            if (socket != null) {
                homeActivity.connectionEstablished(Constants.BLUETOOTH_CONNECTION, socket);
            }
        }
    }
}
