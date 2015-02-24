package it.polimi.wifidirect.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * Created by Stefano Cappa on 31/01/15.
 *
 * Class that represents the {@link it.polimi.wifidirect.model.P2PDevice} associated to this device.
 * It contains the {@link #ping_pong_mode} attribute, used to activate/deactivate the "pingpong mode" on this device.
 * Attention, this means that if ping_pong_mode==true this device can be part of one of the pingpong groups,
 * but it can't be the pingpong device. Otherwise, if false, it's possible to use this device as a
 * pingpong device, but its can't be part of a pingpong group
 *
*/
public class LocalP2PDevice {

    private static final LocalP2PDevice instance = new LocalP2PDevice();

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
