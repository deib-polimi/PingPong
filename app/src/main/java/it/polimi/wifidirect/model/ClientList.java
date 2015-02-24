package it.polimi.wifidirect.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 24/02/15.
 *
 * Class that represents the list of device connect to the group owner.
 * This list will be empty is this device is a client.
 */
public class ClientList {

    @Getter private final List<P2PDevice> list;

    private final static ClientList instance = new ClientList();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static ClientList getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private ClientList() {
        this.list = new ArrayList<>();
    }
}
