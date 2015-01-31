package it.polimi.wifidirect.model;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 31/01/15.
 */
public class PeerList {

    @Getter
    private List<P2PDevice> list;

    private static PeerList instance = new PeerList();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     *
     * @return istanza della classe.
     */
    public static PeerList getInstance() {
        return instance;
    }


    private PeerList () {
        this.list = new ArrayList<>();
    }

    public void addAllElements(Collection<WifiP2pDevice> collection) {
        //presuppone che la lista di p2pDevice sia vuota.

        P2PDevice device;

        for(WifiP2pDevice element : collection) {
            device = new P2PDevice(element);
            list.add(device);
        }
    }

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
