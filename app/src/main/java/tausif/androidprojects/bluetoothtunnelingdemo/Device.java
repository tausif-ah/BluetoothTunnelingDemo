package tausif.androidprojects.bluetoothtunnelingdemo;

import android.bluetooth.BluetoothDevice;
import android.net.wifi.p2p.WifiP2pDevice;

class Device {
    int deviceType;
    WifiP2pDevice wifiDevice;
    BluetoothDevice bluetoothDevice;


    Device(int deviceType, WifiP2pDevice wifiDevice, BluetoothDevice bluetoothDevice) {
        this.deviceType = deviceType;
        this.wifiDevice = wifiDevice;
        this.bluetoothDevice = bluetoothDevice;
    }
}
