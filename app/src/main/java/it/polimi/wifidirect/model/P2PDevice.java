package it.polimi.wifidirect.model;

import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Stefano Cappa on 31/01/15.
 *
 * Class that represents a P2pDevice with inside a WifiP2pDevice and the groupOwner attribute.
 * This class is usefull because can be used to extends the basic WifiP2pDevice's funcionalities.
 * It's an abstraction of a WifiP2pDevice.
 *
 */
public class P2PDevice {

    @Getter private WifiP2pDevice p2pDevice;
    @Getter @Setter private boolean groupOwner;


    /**
     * Constructor of the class
     * @param p2pDevice A WifiP2pDevice
     */
    public P2PDevice (WifiP2pDevice p2pDevice) {
        this.p2pDevice = p2pDevice;
        groupOwner = false;
    }

    /**
     * Another constructor of the class without parameters.
     */
    public P2PDevice() {
        groupOwner = false;
    }

    @Override
    public String toString() {
        return this.p2pDevice.deviceName + ", " + this.p2pDevice.deviceAddress + ", " + this.groupOwner + ", " + this.p2pDevice.status;
    }
}
