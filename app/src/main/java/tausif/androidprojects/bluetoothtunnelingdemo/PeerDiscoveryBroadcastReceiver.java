package tausif.androidprojects.bluetoothtunnelingdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.EditText;

public class PeerDiscoveryBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private PeerDiscoveryController peerDiscoveryController;
    private MainActivity mainActivity;

    public void setWifiP2pManager(WifiP2pManager wifiP2pManager) {
        this.wifiP2pManager = wifiP2pManager;
    }

    public void setChannel(WifiP2pManager.Channel channel) {
        this.channel = channel;
    }

    public void setPeerDiscoveryController(PeerDiscoveryController peerDiscoveryController) {
        this.peerDiscoveryController = peerDiscoveryController;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice hostDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Constants.hostWifiAddress = hostDevice.deviceAddress;
            Constants.hostWifiName = hostDevice.deviceName;
            mainActivity.deviceNameAvailable();
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                    peerDiscoveryController.wifiDeviceDiscovered(wifiP2pDeviceList);
                }
            };
            if (wifiP2pManager != null)
                wifiP2pManager.requestPeers(channel, peerListListener);
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected())
                wifiP2pManager.requestConnectionInfo(channel, peerDiscoveryController);
        }
    }
}
