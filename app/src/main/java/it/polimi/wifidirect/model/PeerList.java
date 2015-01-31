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
 * Classe che rappresenta la lista di dispositivi individuati nella fase di Discovery del protocollo
 * Wifi Direct. E' univoca e implementa Singleton.
 *
 */
public class PeerList {

    @Getter
    private List<P2PDevice> list;

    private static PeerList instance = new PeerList();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     * @return istanza della classe.
     */
    public static PeerList getInstance() {
        return instance;
    }


    private PeerList () {
        this.list = new ArrayList<>();
    }

    /**
     * Aggiunge tutti gli elementi di una Collection di WifiP2pDevice nella lista di P2PDevice
     * @param collection Collection di WiFiP2pDevice
     */
    public void addAllElements(Collection<WifiP2pDevice> collection) {
        //presuppone che la lista di p2pDevice sia vuota.
        P2PDevice device;
        for(WifiP2pDevice element : collection) {
            device = new P2PDevice(element);
            list.add(device);
        }
    }

    /**
     * Fornisce il P2PDevice dato il macaddress
     * @param macAddress Striga che rappresenta il mac address del dispositivo cercato
     * @return P2PDevice con il macaddress specificato, oppure null nel caso l'elemento non sia trovato.
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
