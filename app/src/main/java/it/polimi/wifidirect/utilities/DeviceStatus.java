package it.polimi.wifidirect.utilities;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

/**
 * Utility class to retrieve the device's status message using his code.
 * <p></p>
 * Created by Stefano Cappa on 24/02/15.
 */
public class DeviceStatus {

    /**
     * Method to retrieve the device's status message using his code.
     * @param deviceStatus int that represents the status code
     * @return A String that  the status message
     */
    public static String getDeviceStatus(int deviceStatus) {
        Log.d("WiFiPeerListAdapter", "Peer status :" + deviceStatus);
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
