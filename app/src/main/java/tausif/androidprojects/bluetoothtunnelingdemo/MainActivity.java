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
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ArrayList<Device> devices;
    ArrayList<WDConnection> WDConnections;
    DeviceListAdapter deviceListAdapter;
    PeerDiscoveryController peerDiscoveryController;
    WDUDPSender udpSender;
    BTConnectedSocketManager btConnectedSocketManager;
    boolean WDgroupFormed;

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
        WDgroupFormed = false;
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
        Constants.selfBluetoothName = bluetoothAdapter.getName();
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

    public void connectButtonPressed(View view) {
        int tag = (int)view.getTag();
        Device device = devices.get(tag);
        peerDiscoveryController.connect(device);
    }

    public void connectionEstablished(int connectionType, BluetoothSocket connectedSocket) {
        if (connectionType == Constants.WIFI_DIRECT_CONNECTION) {
            WDConnections = new ArrayList<>();
            if (Constants.isGroupOwner) {
                if (!WDgroupFormed) {
                    showGroupRole("Group owner");
                    ServerConnectionListener serverConnectionListener = new ServerConnectionListener(Constants.WD_WEB_SERVER_LISTENING_PORT, this);
                    serverConnectionListener.start();
                    WDgroupFormed = true;
                }
            }
            else {
                showGroupRole("Group client");
                ServerConnector serverConnector = new ServerConnector(Constants.groupOwnerAddress, Constants.WD_WEB_SERVER_LISTENING_PORT, this);
                serverConnector.start();
            }
        }
        else {
            btConnectedSocketManager = new BTConnectedSocketManager(connectedSocket, this);
            btConnectedSocketManager.start();
        }
    }

    public void ipMacSync() {
        String pkt = PacketManager.createIpMacSyncPkt(Constants.IP_MAC_SYNC, Constants.selfWifiAddress);
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

    public void WDSocketCreated(Socket socket) {
        WDConnection client;
        if (Constants.isGroupOwner) {
            InetAddress srcAddr = socket.getInetAddress();
            int srcPort = socket.getPort();
            client = new WDConnection(srcAddr, srcPort, socket);
            WDConnections.add(client);
            showWDConnections();
        }
        else {
            client = new WDConnection(Constants.groupOwnerAddress, socket, true);
            WDConnections.add(client);
            showWDConnections();
        }
    }

    public void processReceivedWiFiPkt(InetAddress srcAddr, long receivingTime, String receivedPkt) {
        String splited[] = receivedPkt.split("#");
        int pktType = Integer.parseInt(splited[0]);
        if (pktType == Constants.IP_MAC_SYNC) {
            String pkt = PacketManager.createIpMacSyncPkt(Constants.IP_MAC_SYNC_RET, Constants.selfWifiAddress);
            udpSender = null;
            udpSender = new WDUDPSender();
            udpSender.createPkt(pkt, srcAddr);
            udpSender.setRunLoop(false);
            udpSender.start();
            matchIPToMac(srcAddr, splited[1]);
        }
        else if (pktType == Constants.IP_MAC_SYNC_RET)
            matchIPToMac(srcAddr, splited[1]);
    }

    public void processReceivedBTPkt(byte[] readBuffer, long receivingTime) {
        final String receivedPkt = new String(readBuffer);
        String splited[] = receivedPkt.split("#");
        int pktType = Integer.parseInt(splited[0]);
    }

    public void showGroupRole(final String groupRoleText) {
        final TextView groupRole = findViewById(R.id.group_role_textview);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupRole.setText(groupRoleText);
            }
        });
    }

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showWDConnections() {
        Log.d("number of connections", String.valueOf(WDConnections.size()));
        for (int i = 0; i< WDConnections.size(); i++) {
            WDConnection client = WDConnections.get(i);
            Log.d("connection no", String.valueOf(i+1));
            Log.d("ip address", client.IPAddr.getHostAddress());
            Log.d("port no", String.valueOf(client.port));
        }
    }
}
