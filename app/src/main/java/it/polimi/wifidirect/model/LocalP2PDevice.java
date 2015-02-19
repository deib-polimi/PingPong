package it.polimi.wifidirect.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * Created by Stefano Cappa on 31/01/15.
 *
 * Class that represents the P2PDevice associated to this device.
 * It contains the pingpong mode switch for this device.
 * This switch sets to true the ping_pong_mode's attribute.
 *
*/
public class LocalP2PDevice {

    private static LocalP2PDevice instance = new LocalP2PDevice();

    @Getter @Setter private P2PDevice localDevice;

    //this attribute is useful to restart discovery after every "disconnect" command.
    //If you want to use pingpong mode, you need to activate this attributes in every other device, except this device.
    @Getter @Setter private boolean ping_pong_mode;

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static LocalP2PDevice getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private LocalP2PDevice(){
        localDevice = new P2PDevice();
        ping_pong_mode = false;
    }

}
