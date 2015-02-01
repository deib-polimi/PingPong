package it.polimi.wifidirect.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Stefano Cappa on 01/02/15.
 * Classe che rappresenta la lista di mac address di Group Owner con cui un client vuole fare PingPong.
 */
public class PingPongList {

    @Getter private List<P2PDevice> pingponglist;

    @Getter @Setter private String ping_macaddress, pong_macaddress;
    @Getter @Setter private boolean testmode;

    @Getter @Setter private P2PDevice pingDevice, pongDevice;

    //e' in corso il pingpong e continuera' fino a che questo non diventa false
    @Getter @Setter private boolean pinponging;

    //usato per far si che una volta che il device si sta riconnettendo non devono continuamente essere
    //gestiti gli eventi che mantengono la discovery sempre attiva, ma la discovery puo' essere fermata, tanto poi
    //sara' il PingPongLogic con asynctask a riavviarla, dopo la disconnect.
    @Getter @Setter private boolean connecting;

    //se true deve usare il pong_macaddress, altrimenti usare il ping_macaddress
    @Getter @Setter private boolean use_pongAddress;

    private static PingPongList instance = new PingPongList();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     * @return istanza della classe.
     */
    public static PingPongList getInstance() {
        return instance;
    }


    private PingPongList () {
        this.pingponglist = new ArrayList<>();
        this.testmode = false;
        this.pinponging = false;
        this.use_pongAddress = true;
        this.connecting = false;
    }


    public P2PDevice getNextDeviceToConnect() {
        //stabilisco a quale GO questo client si dovra' connettere
        if (PingPongList.getInstance().isUse_pongAddress()) {
            PingPongList.getInstance().setUse_pongAddress(false);
            return pongDevice;
        } else {
            PingPongList.getInstance().setUse_pongAddress(true);
            return pingDevice;
        }
    }

}
