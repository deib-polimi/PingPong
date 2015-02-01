package it.polimi.wifidirect.model;

import android.net.wifi.p2p.WifiP2pGroup;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Stefano Cappa on 31/01/15.
 *
 * Classe che rappresenta il gruppo p2p di cui il Group Owner ne e' a capo con una certa lista di dispositivi connessi.
 *
 */
public class P2PGroup {

    @Getter private List<P2PDevice> list; //incluso anche il group owner
    @Getter @Setter private P2PDevice groupOwner;
    @Getter @Setter private boolean persistent;

    @Getter @Setter private WifiP2pGroup group;

    public P2PGroup (boolean persistent) {
        this.list = new ArrayList<>();
        this.persistent = persistent;
    }

}
