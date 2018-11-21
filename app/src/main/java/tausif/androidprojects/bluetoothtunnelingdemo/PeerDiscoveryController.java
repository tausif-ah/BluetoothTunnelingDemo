package tausif.androidprojects.bluetoothtunnelingdemo;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class PeerDiscoveryController implements WifiP2pManager.ConnectionInfoListener{

    private Context context;
    private MainActivity mainActivity;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private PeerDiscoveryBroadcastReceiver peerDiscoveryBroadcastReceiver;
    private IntentFilter intentFilter;
    private ArrayList<Device> wifiDevices;

    PeerDiscoveryController(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
        peerDiscoveryBroadcastReceiver = new PeerDiscoveryBroadcastReceiver();
        peerDiscoveryBroadcastReceiver.setPeerDiscoveryController(this);
        intentFilter = new IntentFilter();
        configureWiFiDiscovery();
        context.registerReceiver(peerDiscoveryBroadcastReceiver, intentFilter);
        wifiDevices = new ArrayList<>();
        wifiP2pManager.discoverPeers(channel, null);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new controlPeerDiscovery(), 0, Constants.TIME_SLOT_LENGTH *1000);
    }

    private void configureWiFiDiscovery() {
        wifiP2pManager = (WifiP2pManager)context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(context, context.getMainLooper(), null);
        peerDiscoveryBroadcastReceiver.setWifiP2pManager(wifiP2pManager);
        peerDiscoveryBroadcastReceiver.setChannel(channel);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    void wifiDeviceDiscovered(WifiP2pDeviceList deviceList) {
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

    void createGrp() {
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Group created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(context, "Group creation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void joinGrp(Device device) {
        Log.d("inside", "join group");
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.WDDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0;
        wifiP2pManager.connect(channel, config, null);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        Log.d("group", "formed");
        Constants.groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
        Constants.isGroupOwner = wifiP2pInfo.isGroupOwner;
        if (Constants.isGroupOwner)
            Toast.makeText(context, "group owner", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "client", Toast.LENGTH_LONG).show();
    }
}
