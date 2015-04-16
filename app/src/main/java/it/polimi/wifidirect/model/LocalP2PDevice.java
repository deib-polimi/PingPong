/*
Copyright 2015 Stefano Cappa, Politecnico di Milano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package it.polimi.wifidirect.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * Class that represents the {@link it.polimi.wifidirect.model.P2PDevice} associated to this device.
 * It contains the {@link #ping_pong_mode} attribute, used to activate/deactivate the "pingpong mode" on this device.
 * Attention, this means that if ping_pong_mode==true this device can be part of one of the pingpong groups,
 * but it can't be the pingpong device. Otherwise, if false, it's possible to use this device as a
 * pingpong device, but its can't be part of a pingpong group
 * <p></p>
 * Created by Stefano Cappa on 31/01/15.
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
