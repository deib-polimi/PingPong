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

import android.net.wifi.p2p.WifiP2pGroup;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents a P2PGroup with inside a {@link android.net.wifi.p2p.WifiP2pDevice} and
 * other parameters like, {@link #persistent}, the go's {@link it.polimi.wifidirect.model.P2PDevice},
 * the go's ip address, and the entire list of clients, if this device is a GO.
 * It's an abstraction of a {@link android.net.wifi.p2p.WifiP2pGroup}.
 * <p></p>
 * Created by Stefano Cappa on 31/01/15.
 *
 */
public class P2PGroup {

    @Getter private final List<P2PDevice> list; //in this list there is also the group owner.
    @Getter @Setter private P2PDevice groupOwner;
    @Getter @Setter private InetAddress groupOwnerIpAddress;
    @Getter @Setter private boolean persistent;

    @Getter @Setter private WifiP2pGroup group;

    /**
     * Constructor with the possibility to set if this group will be a persistent group or not.
     * @param persistent boolean that represents if the group is persistent or not.
     */
    public P2PGroup (boolean persistent) {
        this.list = new ArrayList<>();
        this.persistent = persistent;
    }

}
