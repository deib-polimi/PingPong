package it.polimi.wifidirect.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * Created by Stefano Cappa on 31/01/15.
 *
 * Classe che rappresenta il P2PDevice associato al dispositivo in uso.
 * Essa contiene l'abilitazione della pingpong mode per il dispositivo corrente.
 * L'abilitazione richiesta con la GUI setta a true l'attributo ping_pong_mode.
 *
*/
public class LocalP2PDevice {

    private static LocalP2PDevice instance = new LocalP2PDevice();

    @Getter @Setter private P2PDevice localDevice;

    //fa si che ad ogni disconnect il dispositivo rientri subito in una discovery silenzionsa
    //per utilizzare la modalita' ping pong, tutti i client e tutti i go dei vari gruppi devono abilitare la modalita' ping pong
    @Getter @Setter private boolean ping_pong_mode;

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     * @return istanza della classe.
     */
    public static LocalP2PDevice getInstance() {
        return instance;
    }

    private LocalP2PDevice(){
        localDevice = new P2PDevice();
        ping_pong_mode = false;
    }

}
