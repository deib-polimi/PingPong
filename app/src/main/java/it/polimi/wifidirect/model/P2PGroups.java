package it.polimi.wifidirect.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that contain a list of the groups of this device.
 * At the moment Android can't use multiple groups, this is an abstract concept.
 * For this reason this list has only one element.
 * <p></p>
 * Created by Stefano Cappa on 01/02/15
 */
public class P2PGroups {

    @Getter private final List<P2PGroup> groupList;

    private static final P2PGroups instance = new P2PGroups();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static P2PGroups getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private P2PGroups () {
        this.groupList = new ArrayList<>();
    }
}
