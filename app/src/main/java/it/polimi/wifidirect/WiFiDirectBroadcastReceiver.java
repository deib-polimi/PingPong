/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polimi.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private WiFiDirectActivity activity;

    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, WiFiDirectActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()) {

            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:

                // UI update to indicate wifi p2p status.
                //in state ho lo stato del wifi p2p adapter, cioe' se attivo o no
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi Direct mode is enabled
                    activity.setIsWifiP2pEnabled(true);
                } else {
                    activity.setIsWifiP2pEnabled(false);
                    activity.resetData();

                }
                Log.d("STATE_CHANGED_ACTION", "P2P state changed - " + state);
                break;

            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:

                // request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (manager != null) {
                    manager.requestPeers(channel, (PeerListListener) activity.getFragmentManager().findFragmentById(R.id.frag_list));
                }
                Log.d("PEERS_CHANGED_ACTION", "P2P peers changed");
                break;

            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:

                if (manager == null) {
                    return;
                }

                //NetworkInfo descrive lo stato della connessione
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {

                    // we are connected with the other device, request connection
                    // info to find group owner IP

                    DeviceDetailFragment fragment = (DeviceDetailFragment) activity.getFragmentManager().findFragmentById(R.id.frag_detail);
                    manager.requestConnectionInfo(channel, fragment);
                    manager.requestGroupInfo(channel, fragment);
                } else {
                    // It's a disconnect
                    activity.resetData();

                    //si diconnette
                    Log.d("Broadcast received", "non piu connesso");

                    //se la pingpong mode su questo device e' attiva, allora ricomincia subito la discovery in modo forzato e silenzioso
                    Log.d("verifica_pingpong", "Stato pingpong: " + LocalP2PDevice.getInstance().isPing_pong_mode());
                    if(LocalP2PDevice.getInstance().isPing_pong_mode()) {
                        activity.discoveryPingPong();
                    }

                }
                break;

            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:

                DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(R.id.frag_list);
                WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

                fragment.updateThisDevice(
                        new P2PDevice(
                                wifiP2pDevice
                        )
                );

                Log.d("THIS_DEVICE_CHANGED_ACTION" , "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                break;
        }
    }
}
