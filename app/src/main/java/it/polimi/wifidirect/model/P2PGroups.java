package it.polimi.wifidirect.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Ks89 on 01/02/15.
 * Classe che rappresenta i gruppi p2p a cui questo dispositivo appartiente.
 * Ad oggi Android non l permette quindi per ora resta solo un concetto astratto.
 */
public class P2PGroups {

    @Getter
    List<P2PGroup> groupList;

    private static P2PGroups instance = new P2PGroups();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     * @return istanza della classe.
     */
    public static P2PGroups getInstance() {
        return instance;
    }

    private P2PGroups () {
        this.groupList = new ArrayList<>();
    }
}
