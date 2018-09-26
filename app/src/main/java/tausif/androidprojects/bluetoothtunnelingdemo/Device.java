package tausif.androidprojects.bluetoothtunnelingdemo;

import android.bluetooth.BluetoothDevice;
import android.net.wifi.p2p.WifiP2pDevice;

public class Device {
    public int deviceType;
    public BluetoothDevice BTDevice;
    public WifiP2pDevice WDDevice;

    Device(int deviceType, BluetoothDevice BTDevice, WifiP2pDevice WDDevice) {
        this.deviceType = deviceType;
        this.BTDevice = BTDevice;
        this.WDDevice = WDDevice;
    }
}
