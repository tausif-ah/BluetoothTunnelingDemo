package tausif.androidprojects.bluetoothtunnelingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Device> devices;

    DeviceListAdapter(Context context, ArrayList<Device> devices) {
        this.context = context;
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
        device_name.setText(String.valueOf(currentDevice.deviceType));
        return view;
    }
}
