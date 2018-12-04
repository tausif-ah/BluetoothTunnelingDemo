package tausif.androidprojects.bluetoothtunnelingdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Device> devices;
    DeviceListAdapter deviceListAdapter;
    PeerDiscoveryController peerDiscoveryController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupDeviceList();
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

    void setupDeviceList() {
        devices = new ArrayList<>();
        ListView deviceList = findViewById(R.id.device_list_view);
        deviceListAdapter = new DeviceListAdapter(this, devices);
        deviceList.setAdapter(deviceListAdapter);
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
        Log.d("device name", device.WDDevice.deviceName);
        peerDiscoveryController.joinGrp(device);
    }

    public void connectButtonPressed(View view) {
        int tag = (int)view.getTag();
        Device device = devices.get(tag);
        peerDiscoveryController.connect(device);
    }
}
