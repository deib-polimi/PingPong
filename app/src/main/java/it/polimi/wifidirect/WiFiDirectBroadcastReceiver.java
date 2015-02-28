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
import android.util.Log;

import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PingPongList;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 * <p></p>
 * Created by Stefano Cappa, based on google code samples.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiver";
    private final WifiP2pManager manager;
    private final Channel channel;
    private final WiFiDirectActivity activity;

    /**
     * Constructor of the class.
     * @param manager WifiP2pManager system service
     * @param channel WifiP2pChannel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, WiFiDirectActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()) {

            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:

                //UI update to indicate wifi p2p status.
                //in state i have the adapter's state: enabled or not.
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi Direct mode is enabled
                    activity.setIsWifiP2pEnabled(true);
                } else {
                    activity.setIsWifiP2pEnabled(false);
                    activity.resetData();

                }
                Log.d(TAG, "P2P state changed - " + state);
                break;

            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:

                // request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (manager != null) {
                    manager.requestPeers(channel, activity);
                }
                Log.d(TAG, "P2P peers changed");
                break;

            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:

                if (manager == null) {
                    return;
                }

                //NetworkInfo describe the connection's state
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {

                    // we are connected with the other device, request connection
                    // info to find group owner IP

                    manager.requestConnectionInfo(channel, activity);
                    manager.requestGroupInfo(channel, activity);

                    activity.showDetailFragment();

                    if (PingPongList.getInstance().isPingponging()) {
                        activity.startNewPingPongCycle();
                    }

                } else {
                    // It's a disconnect
                    activity.showListFragment();

                    activity.resetData();

                    Log.d(TAG, "Disconnected");

                    activity.discoveryPingPong();

                    //if the Pingpong mode is enabled, restarts the discovery
                    Log.d(TAG, "Check Ping pong state: " + LocalP2PDevice.getInstance().isPing_pong_mode());
                    if(LocalP2PDevice.getInstance().isPing_pong_mode()) {
//                        activity.discoveryPingPong();
                    } else {

                        if (PingPongList.getInstance().isPingponging()) {
                            activity.restartDiscoveryPingpongAfterDisconnect();
                        }
                    }

                    activity.hideLocalDeviceGoIcon();
                }
                break;

            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:

                WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

                P2PDevice thisDevice = new P2PDevice( wifiP2pDevice );

                LocalP2PDevice.getInstance().setLocalDevice(thisDevice);

                activity.getListFragment().updateThisDevice();

                Log.d(TAG , "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                break;
        }
    }
}
