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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.polimi.wifidirect.actionlisteners.CustomizableActionListener;
import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.P2PGroups;
import it.polimi.wifidirect.model.PingPongList;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 * This activity manages the pingpong logic.
 * <p/>
 * Created by Stefano Cappa, based on google code samples
 */
public class WiFiDirectActivity extends Activity implements
        WifiP2pManager.ChannelListener,
        DeviceListFragment.DeviceActionListener {

    public static final String TAG = "P2P-PingPong";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

    /**
     * Method to set if wifidirect is enabled.
     *
     * @param isWifiP2pEnabled boolean to set the attribute {@link #isWifiP2pEnabled}
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    /**
     * Register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }

        P2PGroups.getInstance().getGroupList().clear();

        if (fragmentDetails.getView() != null) {
            fragmentDetails.getView().findViewById(R.id.btn_start_ping_pong).setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_direct_enable:
                if (manager != null && channel != null) {

                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.

                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "Channel or manager is null");
                }
                return true;

            case R.id.atn_direct_discover:
                if (!isWifiP2pEnabled) {
                    Toast.makeText(WiFiDirectActivity.this, R.string.p2p_off_warning, Toast.LENGTH_SHORT).show();
                    return true;
                }
                final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                fragment.onInitiateDiscovery();
                manager.discoverPeers(channel, new CustomizableActionListener(
                        WiFiDirectActivity.this,
                        "discoverPeers",
                        null,
                        "Discovery Initiated",
                        null,
                        "Discovery Failed"));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to show {@link it.polimi.wifidirect.DeviceDetailFragment}.
     *
     * @param device A {@link it.polimi.wifidirect.model.P2PDevice} to use in the fragment.
     */
    @Override
    public void showDetails(P2PDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);

    }

    @Override
    public void connect(WifiP2pConfig config) {
        Log.d(TAG, "Request connection");
        manager.connect(channel, config, new CustomizableActionListener(
                WiFiDirectActivity.this,
                "connect",
                "connect Success",
                null,
                "connect Failed",
                "connect Failed. Retry"));
    }


    /**
     * Modified method to start a "silent disconnect" during the pingponging
     */
    public void disconnectPingPong() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.resetViews();

        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {

                if (fragment.getView() != null) {
                    fragment.getView().setVisibility(View.GONE);
                }
            }

        });
    }


    /**
     * Method to restart discovery after a disconnect.
     */
    public void restartDiscoveryPingpongAfterDisconnect() {

        Log.d(TAG, System.currentTimeMillis() + " - Preparing to discovery");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, System.currentTimeMillis() + " - Discovery started");

        PingPongList.getInstance().setConnecting(false);

        this.discoveryPingPong();
    }

    /**
     * Method used by ping pong to start a new pingpong cycle.
     */
    public void startNewPingPongCycle() {
        Log.d(TAG, System.currentTimeMillis() + " - Reconnected, pingpong cycle completed, but i'm starting a new cycle.");
        new PingPongLogic(this).execute();
    }

    /**
     * Modified method to start a "silent discovery" without ProgessDialog or something else.
     */
    public void discoveryPingPong() {
        manager.discoverPeers(channel, new CustomizableActionListener(
                WiFiDirectActivity.this,
                "discoveryPingPong",
                null,
                "Discovery Initiated",
                null,
                "Discovery Failed"));
    }

    /**
     * Method to disconnect.
     */
    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.resetViews();

        P2PGroups.getInstance().getGroupList().clear();

        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                Toast.makeText(WiFiDirectActivity.this, "Disconnect Failed: " + reasonCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(WiFiDirectActivity.this, "Disconnect Success", Toast.LENGTH_SHORT).show();

                if (fragment.getView() != null) {
                    fragment.getView().setVisibility(View.GONE);
                }
            }

        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this, "Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.", Toast.LENGTH_LONG).show();
            P2PGroups.getInstance().getGroupList().clear();
        }
    }

    /**
     * Method to cancel the disconnect procedure.
     * Never used in this app, but can be useful in the future
     */
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            P2PDevice localDevice = LocalP2PDevice.getInstance().getLocalDevice();

            if (localDevice == null || localDevice.getP2pDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (localDevice.getP2pDevice().status == WifiP2pDevice.AVAILABLE || localDevice.getP2pDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new CustomizableActionListener(
                        WiFiDirectActivity.this,
                        "discoveryPingPong",
                        null,
                        "Aborting connection",
                        null,
                        "Connect abort request failed"));
            }
        }

    }
}
