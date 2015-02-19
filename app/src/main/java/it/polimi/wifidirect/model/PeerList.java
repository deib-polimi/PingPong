package it.polimi.wifidirect.model;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 31/01/15.
 *
 * Class that represents the list of device discovered during the discovery phase.
 */
public class PeerList {

    @Getter
    private List<P2PDevice> list;

    private static PeerList instance = new PeerList();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static PeerList getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private PeerList () {
        this.list = new ArrayList<>();
    }

    /**
     * This method adds all the elements of the collection in the P2pDevice list.
     * This method requires an empty list.
     * @param collection Collection of WiFiP2pDevice
     */
    public void addAllElements(Collection<WifiP2pDevice> collection) {
        P2PDevice device;
        for(WifiP2pDevice element : collection) {
            device = new P2PDevice(element);
            list.add(device);
        }
    }

    /**
     * Method to retrieve the P2pDevice using his mac address.
     * @param macAddress Strig that represents the mac address of the P2pDevice
     * @return P2PDevice with the specified mac address.
     */
    public P2PDevice getDeviceByMacAddress(String macAddress) {
        for(P2PDevice device : list) {
            if(device.getP2pDevice().deviceAddress.equals(macAddress)) {
                return device;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        Log.d("p2plist" , "---Inizio lista---");
        for(P2PDevice device : list) {
            Log.d("p2plist" , device.toString());
        }
        Log.d("p2plist" , "---Fine lista---");
        return null;
    }
}
