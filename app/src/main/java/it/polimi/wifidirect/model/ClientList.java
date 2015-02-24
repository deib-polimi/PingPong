package it.polimi.wifidirect.model;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 24/02/15.
 *
 * Class that represents the list of device connect to the group owner.
 * This list will be empty is this device is a client.
 */
public class ClientList {

    @Getter private List<P2PDevice> list;

    private static ClientList instance = new ClientList();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static ClientList getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private ClientList() {
        this.list = new ArrayList<>();
    }
}
