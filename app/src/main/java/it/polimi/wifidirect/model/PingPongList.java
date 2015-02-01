package it.polimi.wifidirect.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ks89 on 01/02/15.
 * Classe che rappresenta la lista di mac address di Group Owner con cui un client vuole fare PingPong.
 */
public class PingPongList {

    List<String> pingponglist;

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
    }

}
