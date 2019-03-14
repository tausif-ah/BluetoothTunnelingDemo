package tausif.androidprojects.bluetoothtunnelingdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
    BTConnectedSocketManager btConnectedSocketManager;
    boolean WDgroupFormed;
    ArrayList<ServerMessage> serverMessages;
    WDTCPSender wdtcpSender;

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
        Constants.isGroupFormed = false;
        Constants.isWebServer = false;
        WDgroupFormed = false;
        wdtcpSender = new WDTCPSender();
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
        Constants.selfBluetoothName = bluetoothAdapter.getName();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice pairedDevice: pairedDevices
                    ) {
                currentDevice = new Device(Constants.BLUETOOTH_DEVICE, null, pairedDevice);
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

    public void connectPressed(View view) {
        int tag = (int)view.getTag();
        Device device = devices.get(tag);
        peerDiscoveryController.connect(device);
    }

    public void makeSelfServerPressed(View view) {
        Constants.isWebServer = true;
        updateRoleText();
        ServerMessage makeSelfServer = new ServerMessage(Constants.SELF_SERVER_NOTIFIER, null, 0, Constants.selfWifiName);
        Socket socket = null;
        for (WDConnection client: WDConnections
        ) {
            if (client.groupOwnerConnection) {
                socket = client.connectedSocket;
                break;
            }
        }
        sendWDTCPPacket(makeSelfServer, socket);
    }

    public void sendToServerPressed(View view) {
        if (!Constants.isGroupOwner) {
            ServerMessage request = new ServerMessage(Constants.SERVER_REQUEST, null, Constants.WD_WEB_SERVER_LISTENING_PORT, Constants.selfWifiName);
            Socket socket = null;
            for (WDConnection client: WDConnections
                 ) {
                if (client.groupOwnerConnection) {
                    socket = client.connectedSocket;
                    break;
                }
            }
            sendWDTCPPacket(request, socket);
        }
    }

    public void connectionEstablished(int connectionType, BluetoothSocket connectedSocket) {
        if (connectionType == Constants.WIFI_DIRECT_CONNECTION) {
            WDConnections = new ArrayList<>();
            serverMessages = new ArrayList<>();
            if (Constants.isGroupOwner) {
                if (!WDgroupFormed) {
                    updateRoleText();
                    ServerConnectionListener serverConnectionListener = new ServerConnectionListener(Constants.WD_WEB_SERVER_LISTENING_PORT, this);
                    serverConnectionListener.start();
                    WDgroupFormed = true;
                }
            }
            else {
                updateRoleText();
                ServerConnector serverConnector = new ServerConnector(Constants.groupOwnerAddress, Constants.WD_WEB_SERVER_LISTENING_PORT, this);
                serverConnector.start();
            }
        }
        else {
            btConnectedSocketManager = new BTConnectedSocketManager(connectedSocket, this);
            btConnectedSocketManager.start();
        }
    }

    public void WDSocketCreated(Socket socket) {
        WDConnection client;
        if (Constants.isGroupOwner) {
            InetAddress srcAddr = socket.getInetAddress();
            int srcPort = socket.getPort();
            client = new WDConnection(srcAddr, srcPort, socket, this);
            client.start();
            WDConnections.add(client);
        }
        else {
            client = new WDConnection(Constants.groupOwnerAddress, socket, true, this);
            client.start();
            WDConnections.add(client);
        }
    }

    public void sendWDTCPPacket(ServerMessage message, Socket socket) {
        wdtcpSender = null;
        wdtcpSender = new WDTCPSender();
        wdtcpSender.setMessage(message);
        wdtcpSender.setSocket(socket);
        wdtcpSender.start();
    }

    public void BluetoothMessageReceived(ServerMessage message) {
        int msgType = message.type;
        if (msgType == Constants.SERVER_REQUEST) {
            showToast("request from " + message.data);
            for (WDConnection connection: WDConnections
                 ) {
                if (connection.isWebServerConnection) {
                    sendWDTCPPacket(message, connection.connectedSocket);
                    break;
                }
            }
        }
        else if (msgType == Constants.SERVER_RESPONSE) {
            showToast("response from " + message.data);
            Socket socket = null;
            for (WDConnection connection: WDConnections
                 ) {
                socket = connection.connectedSocket;
                break;
            }
            if (socket != null) {
                sendWDTCPPacket(message, socket);
            }
        }
    }

    public void messageInServerChannel(ServerMessage message) {
        int msgType = message.type;
        if (msgType == Constants.SERVER_REQUEST) {
            if (Constants.isGroupOwner) {
                serverMessages.add(message);
                showToast("request from " + message.data);
                boolean webServerFound = false;
                for (WDConnection connection: WDConnections
                ) {
                    if (connection.isWebServerConnection) {
                        webServerFound = true;
                        break;
                    }
                }
                if (!webServerFound) {
                    setUpBTConnection();
                    btConnectedSocketManager.sendMessage(message);
                }
            }
            else {
                showToast("request from " + message.data);
                serverMessages.add(message);
                ServerMessage response = new ServerMessage(Constants.SERVER_RESPONSE, null, 0, Constants.selfWifiName);
                response.setDestination(message.source);
                Socket socket = null;
                for (WDConnection connection: WDConnections
                     ) {
                    if (connection.groupOwnerConnection) {
                        socket = connection.connectedSocket;
                        break;
                    }
                }
                sendWDTCPPacket(response, socket);
            }
        }
        else if (msgType == Constants.SERVER_RESPONSE) {
            showToast("response from " + message.data);
            if (Constants.isGroupOwner) {
                btConnectedSocketManager.sendMessage(message);
            }
        }
        else if (msgType == Constants.SELF_SERVER_NOTIFIER) {
            if (Constants.isGroupOwner) {
                showToast(message.data + " marking himself as server");
                serverMessages.add(message);
                for (WDConnection connection: WDConnections
                ) {
                    if (message.source == connection.IPAddr) {
                        connection.isWebServerConnection = true;
                        break;
                    }
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

    public void updateRoleText() {
        String roleText = "";
        if (Constants.isGroupFormed) {
            if (Constants.isGroupOwner)
                roleText = "Group owner";
            else
                roleText = "Group client";
        }
        if (Constants.isWebServer) {
            if (roleText.length() > 0)
                roleText += "-Web server";
            else
                roleText += "Web server";
        }
        final String roleString = roleText;
        final TextView roleView = findViewById(R.id.role_textview);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roleView.setText(roleString);
            }
        });
    }
}
