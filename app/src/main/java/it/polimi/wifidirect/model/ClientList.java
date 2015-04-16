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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that represents the list of device connect to the group owner.
 * This list will be empty is this device is a client.
 * <p></p>
 * Created by Stefano Cappa on 24/02/15.
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
