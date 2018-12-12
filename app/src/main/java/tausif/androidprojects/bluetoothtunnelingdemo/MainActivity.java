package tausif.androidprojects.bluetoothtunnelingdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ArrayList<Device> devices;
    DeviceListAdapter deviceListAdapter;
    PeerDiscoveryController peerDiscoveryController;
    WDUDPSender udpSender;
    BTConnectedSocketManager btConnectedSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpPermissions();
        setupDeviceList();
        setUpBluetoothDataTransfer();
        Constants.isGroupOwner = false;
        initiateDeviceDiscovery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpPermissions() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_CODE_LOCATION);
    }

    void setupDeviceList() {
        devices = new ArrayList<>();
        ListView deviceList = findViewById(R.id.device_list_view);
        deviceListAdapter = new DeviceListAdapter(this, devices);
        deviceList.setAdapter(deviceListAdapter);
    }

    private void setUpBluetoothDataTransfer() {
        BTConnectionListener btConnectionListener = new BTConnectionListener(this);
        btConnectionListener.start();
    }

    public void setUpBTConnection() {
        BTSocketConnector socketConnector = new BTSocketConnector();
        Device currentDevice = null;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Constants.hostBluetoothName = bluetoothAdapter.getName();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice pairedDevice: pairedDevices
                    ) {
                Device device = new Device(Constants.BLUETOOTH_DEVICE, null, pairedDevice);
                currentDevice = device;
                break;
            }
        }
        socketConnector.setDevice(currentDevice);
        BluetoothSocket connectedSocket = socketConnector.createSocket();
        btConnectedSocketManager = null;
        if (connectedSocket!=null) {
            btConnectedSocketManager = new BTConnectedSocketManager(connectedSocket, this);
            btConnectedSocketManager.start();
        }
        btConnectedSocketManager.setDevice(currentDevice);
    }

    void initiateDeviceDiscovery() {
        peerDiscoveryController = new PeerDiscoveryController(this, this);
    }

    public void discoveryFinished(ArrayList<Device> WDDevices) {
        devices.clear();
        devices.addAll(WDDevices);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void crtGrpButtonPressed(View view) {
        peerDiscoveryController.createGrp();
    }

    public void joinGrpButtonPressed(View view) {
        int tag = (int)view.getTag();
        Device device = devices.get(tag);
        Log.d("device name", device.wifiDevice.deviceName);
        peerDiscoveryController.joinGrp(device);
    }

    public void connectButtonPressed(View view) {
        int tag = (int)view.getTag();
        Device device = devices.get(tag);
        peerDiscoveryController.connect(device);
    }

    public void sendButtonPressed(View view) {
        if (!Constants.isGroupOwner) {
            String pkt = PacketManager.createServerReqMsg(Constants.SERVER_REQ);
            udpSender = null;
            udpSender = new WDUDPSender();
            udpSender.createPkt(pkt, Constants.groupOwnerAddress);
            udpSender.setRunLoop(false);
            udpSender.start();
        }
    }

    public void connectionEstablished(int connectionType, BluetoothSocket connectedSocket) {
        if (connectionType == Constants.WIFI_DIRECT_CONNECTION) {
            WDUDPListener udpListener = new WDUDPListener(this);
            udpListener.start();
            if (!Constants.isGroupOwner)
                ipMacSync();
        }
        else {
            btConnectedSocketManager = new BTConnectedSocketManager(connectedSocket, this);
            btConnectedSocketManager.start();
        }
    }

    public void ipMacSync() {
        String pkt = PacketManager.createIpMacSyncPkt(Constants.IP_MAC_SYNC, Constants.hostWifiAddress);
        udpSender = null;
        udpSender = new WDUDPSender();
        udpSender.createPkt(pkt, Constants.groupOwnerAddress);
        udpSender.setRunLoop(false);
        udpSender.start();
    }

    public void matchIPToMac(InetAddress ipAddr, String macAddr) {
        for (Device device:devices
                ) {
            if (device.deviceType == Constants.WIFI_DEVICE) {
                if (device.wifiDevice.deviceAddress.equals(macAddr)){
                    device.IPAddress = ipAddr;
                    showToast("ip mac synced");
                    break;
                }
            }
        }
    }

    public void processReceivedWiFiPkt(InetAddress srcAddr, long receivingTime, String receivedPkt) {
        String pktCpy = receivedPkt;
        String splited[] = receivedPkt.split("#");
        int pktType = Integer.parseInt(splited[0]);
        if (pktType == Constants.IP_MAC_SYNC) {
            String pkt = PacketManager.createIpMacSyncPkt(Constants.IP_MAC_SYNC_RET, Constants.hostWifiAddress);
            udpSender = null;
            udpSender = new WDUDPSender();
            udpSender.createPkt(pkt, srcAddr);
            udpSender.setRunLoop(false);
            udpSender.start();
            matchIPToMac(srcAddr, splited[1]);
        }
        else if (pktType == Constants.IP_MAC_SYNC_RET)
            matchIPToMac(srcAddr, splited[1]);
        else if (pktType == Constants.SERVER_REQ) {
            if (Constants.isGroupOwner) {
                showToast(splited[1]);
                setUpBTConnection();
                btConnectedSocketManager.sendPkt(pktCpy);
            }
            else {
                showToast(splited[1]);
            }
        }
    }

    public void processReceivedBTPkt(byte[] readBuffer, long receivingTime) {
        final String receivedPkt = new String(readBuffer);
        String pktCpy = receivedPkt;
        String splited[] = receivedPkt.split("#");
        int pktType = Integer.parseInt(splited[0]);
        if (pktType == Constants.SERVER_REQ) {
            showToast(splited[1]);
            for (Device device: devices
                 ) {
                if (device.wifiDevice.deviceName.equals(Constants.serverName)) {
                    udpSender = null;
                    udpSender = new WDUDPSender();
                    udpSender.createPkt(pktCpy, device.IPAddress);
                    udpSender.setRunLoop(false);
                    udpSender.start();
                }
            }
        }
    }

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
