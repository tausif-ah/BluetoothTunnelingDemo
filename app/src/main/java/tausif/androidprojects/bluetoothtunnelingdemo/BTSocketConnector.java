package tausif.androidprojects.bluetoothtunnelingdemo;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

class BTSocketConnector {
    private BluetoothSocket socket;
    private Device device;

    void setDevice(Device device) {
        this.device = device;
    }

    BluetoothSocket createSocket() {
        try {
            socket = device.bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(Constants.MY_UUID));
        }catch (IOException sktCrt) {
            Log.e("BT socket creation", sktCrt.getLocalizedMessage());
        }
        try {
            socket.connect();
        }catch (IOException sktCnct) {
            try {
                socket.close();
                return null;
            }catch (IOException sktClse) {
                Log.e("BT socket connect", sktCnct.getLocalizedMessage());
            }
        }
        return socket;
    }
}
