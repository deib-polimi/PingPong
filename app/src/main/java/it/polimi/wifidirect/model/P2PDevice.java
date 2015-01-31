package it.polimi.wifidirect.model;

import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Stefano Cappa on 31/01/15.
 *
 * Classe che rappresenta un dispositivo P2P incapsulando il concetto di WifiP2pDevice
 * ed aggiungendo il parametro groupOwner.
 * Questa classe e' utile soprattutto per permettere l'estensione delle funzionalita' di WifiP2pDevice,
 * in quanto ne rappresenta gia' una astrazione.
 *
 */
public class P2PDevice {

    @Getter private WifiP2pDevice p2pDevice;
    @Getter @Setter private boolean groupOwner;

    public P2PDevice (WifiP2pDevice p2pDevice) {
        this.p2pDevice = p2pDevice;
        groupOwner = false;
    }

    public P2PDevice() {
        groupOwner = false;
    }

    @Override
    public String toString() {
        return this.p2pDevice.deviceName + ", " + this.p2pDevice.deviceAddress + ", " + this.groupOwner + ", " + this.p2pDevice.status;
    }
}
