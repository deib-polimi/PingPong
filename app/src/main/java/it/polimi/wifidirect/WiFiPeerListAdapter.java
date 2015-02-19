package it.polimi.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PeerList;

/**
 * ListAdapter of P2PDevice.
 * Created by Stefano Cappa on 31/01/15.
 */
public class WiFiPeerListAdapter extends ArrayAdapter<P2PDevice> {

    private Context context;

    /**
     * Constructor of the class
     * @param context Context
     * @param textViewResourceId Resource id
     */
    public WiFiPeerListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId, PeerList.getInstance().getList());
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_devices, null);
        }

        P2PDevice device = PeerList.getInstance().getList().get(position);
        if (device != null) {
            TextView name = (TextView) v.findViewById(R.id.device_name);
            TextView status = (TextView) v.findViewById(R.id.device_status);
            TextView macaddress = (TextView) v.findViewById(R.id.device_mac_address);
            if (name != null) {
                name.setText(device.getP2pDevice().deviceName);
            }
            if (status != null) {
                status.setText(getDeviceStatus(device.getP2pDevice().status));
            }
            if (macaddress != null) {
                macaddress.setText(device.getP2pDevice().deviceAddress);
            }
        }

        return v;

    }


    private static String getDeviceStatus(int deviceStatus) {
        Log.d("device_adapter", "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }
}
