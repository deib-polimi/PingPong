package it.polimi.wifidirect.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Stefano Cappa on 31/01/15.
 *
 * Importante: questa classe rappresenta il gruppo p2p di cui il Group Owner ne e' a capo.
 * Quindi se il device e' un client non avra' questa istanta di P2PGroup settata e pronta, ma solo il suo
 * Owner!!! MOLTO IMPORTANTE!!!
 * Nonostante sia singleton, essendo solo del GO, va bene avere una sola copia, poiche' un GO non puo' fare
 * pingpong tra i gruppi o cadrebbero tutti i client a loro collegati
 *
 */
public class P2PGroup {

    @Getter private List<P2PDevice> list;
    @Getter @Setter private boolean persistent;


    private static P2PGroup instance = new P2PGroup();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     *
     * @return istanza della classe.
     */
    public static P2PGroup getInstance() {
        return instance;
    }

    private P2PGroup () {
        this.list = new ArrayList<>();
        this.persistent = true;
    }

}
