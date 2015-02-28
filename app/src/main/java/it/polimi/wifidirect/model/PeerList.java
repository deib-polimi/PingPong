package it.polimi.wifidirect.model;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Class that represents the list of device discovered during the discovery phase.
 * <p></p>
 * Created by Stefano Cappa on 31/01/15.
 */
public class PeerList {

    @Getter
    private final List<P2PDevice> list;

    private static final PeerList instance = new PeerList();

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
     * This method adds all the elements of the collection in the {@link #list}.
     * This method requires an empty list.
     * @param collection {@link java.util.Collection} of {@link android.net.wifi.p2p.WifiP2pDevice}
     */
    public void addAllElements(Collection<WifiP2pDevice> collection) {
        P2PDevice device;
        for(WifiP2pDevice element : collection) {
            device = new P2PDevice(element);
            list.add(device);
        }
    }

    /**
     * Method to retrieve the {@link it.polimi.wifidirect.model.P2PDevice} using his mac address.
     * @param macAddress String that represents the mac address of
     *                   the @link it.polimi.wifidirect.model.P2PDevice}
     * @return The @link it.polimi.wifidirect.model.P2PDevice} with the specified mac address.
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
        Log.d("p2plist" , "---Begin list---");
        for(P2PDevice device : list) {
            Log.d("p2plist" , device.toString());
        }
        Log.d("p2plist" , "---End list---");
        return null;
    }
}
