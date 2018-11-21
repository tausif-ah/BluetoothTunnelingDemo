package tausif.androidprojects.bluetoothtunnelingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Device> devices;

    DeviceListAdapter(Context context, ArrayList<Device> devices) {
        this.devices = devices;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return devices.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Device currentDevice = devices.get(i);
        view = inflater.inflate(R.layout.device_list_row, viewGroup, false);
        TextView device_name = view.findViewById(R.id.device_name_textview);
        if (currentDevice.deviceType == Constants.WD_DEVICE) {
            device_name.setText(currentDevice.WDDevice.deviceName);
        }
        Button joinGrp = view.findViewById(R.id.join_grp_button);
        joinGrp.setTag(i);
        return view;
    }
}
