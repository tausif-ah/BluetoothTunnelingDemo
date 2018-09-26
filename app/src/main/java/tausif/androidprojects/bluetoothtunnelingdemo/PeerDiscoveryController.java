package tausif.androidprojects.bluetoothtunnelingdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class PeerDiscoveryController {

    private Context context;
    private MainActivity mainActivity;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
//    private PeerDiscoveryBroadcastReceiver peerDiscoveryBroadcastReceiver;
    private IntentFilter intentFilter;
    private ArrayList<Device> wifiDevices;

    PeerDiscoveryController(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
//        peerDiscoveryBroadcastReceiver = new PeerDiscoveryBroadcastReceiver();
//        peerDiscoveryBroadcastReceiver.setPeerDiscoveryController(this);
//        peerDiscoveryBroadcastReceiver.setSourceActivity(this.homeActivity);
        intentFilter = new IntentFilter();
        configureWiFiDiscovery();
//        context.registerReceiver(peerDiscoveryBroadcastReceiver, intentFilter);
        wifiDevices = new ArrayList<>();
        wifiP2pManager.discoverPeers(channel, null);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new controlPeerDiscovery(), 0, Constants.TIME_SLOT_LENGTH *1000);
    }

    private void configureWiFiDiscovery() {
        wifiP2pManager = (WifiP2pManager)context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(context, context.getMainLooper(), null);
//        peerDiscoveryBroadcastReceiver.setWifiP2pManager(wifiP2pManager);
//        peerDiscoveryBroadcastReceiver.setChannel(channel);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void wifiDeviceDiscovered(WifiP2pDeviceList deviceList) {
        if (wifiDevices.size()>0)
            wifiDevices.clear();
        for (WifiP2pDevice device: deviceList.getDeviceList()
                ) {
            Device newDevice = new Device(Constants.WD_DEVICE, null, device);
            wifiDevices.add(newDevice);
        }
    }

    private class controlPeerDiscovery extends TimerTask {
        @Override
        public void run() {
            mainActivity.discoveryFinished(wifiDevices);
        }
    }

//    @Override
//    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
//        Constants.groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
//        Constants.isGroupOwner = wifiP2pInfo.isGroupOwner;
//        homeActivity.connectionEstablished(Constants.WIFI_DIRECT_CONNECTION, null);
//    }
}
