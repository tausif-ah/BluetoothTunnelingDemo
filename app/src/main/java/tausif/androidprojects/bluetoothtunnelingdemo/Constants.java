package tausif.androidprojects.bluetoothtunnelingdemo;

import java.net.InetAddress;

class Constants {
    static String selfBluetoothName = "";
    static final String MY_UUID = "e439084f-b7f1-460c-8a3f-d4cc883413e2";

    static String selfWifiName = "";
    static String selfWifiAddress = "";
    static InetAddress groupOwnerAddress;
    static boolean isGroupOwner;
    static final int WD_UDP_LISTENING_PORT = 8500;
    static final int WD_WEB_SERVER_LISTENING_PORT = 8383;

    static final int BLUETOOTH_DEVICE = 0;
    static final int WIFI_DEVICE = 1;

    static final int TIME_SLOT_LENGTH = 10;    // in seconds

    static final int IP_MAC_SYNC = 104;
    static final int IP_MAC_SYNC_RET = 105;

    static final int WIFI_DIRECT_CONNECTION = 1000;
    static final int BLUETOOTH_CONNECTION = 1001;

    static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 2000;
    static final int REQUEST_CODE_LOCATION = 2001;
}
