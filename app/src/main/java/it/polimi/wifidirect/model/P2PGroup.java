package it.polimi.wifidirect.model;

import android.net.wifi.p2p.WifiP2pGroup;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Stefano Cappa on 31/01/15.
 *
 * Class that represents a P2PGroup with inside a {@link android.net.wifi.p2p.WifiP2pDevice} and
 * other parameters like, {@link #persistent}, the go's {@link it.polimi.wifidirect.model.P2PDevice},
 * the go's ip address, and the entire list of clients, if this device is a GO.
 * It's an abstraction of a {@link android.net.wifi.p2p.WifiP2pGroup}.
 *
 */
public class P2PGroup {

    @Getter private List<P2PDevice> list; //in this list there is also the group owner.
    @Getter @Setter private P2PDevice groupOwner;
    @Getter @Setter private InetAddress groupOwnerIpAddress;
    @Getter @Setter private boolean persistent;

    @Getter @Setter private WifiP2pGroup group;

    /**
     * Constructor with the possibility to set if this group will be a persistent group or not.
     * @param persistent boolean that represents if the group is persistent or not.
     */
    public P2PGroup (boolean persistent) {
        this.list = new ArrayList<>();
        this.persistent = persistent;
    }

}
