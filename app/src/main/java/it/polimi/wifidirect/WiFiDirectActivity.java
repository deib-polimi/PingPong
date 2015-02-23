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
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Method;

import it.polimi.wifidirect.actionlisteners.CustomizableActionListener;
import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.P2PGroups;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;
import lombok.Getter;

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
public class WiFiDirectActivity extends ActionBarActivity implements
        WifiP2pManager.ChannelListener,
        DeviceListFragment.DeviceActionListener,
        WifiP2pManager.PeerListListener {


public static final String TAG = "P2P-PingPong";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private Toolbar toolbar;
    @Getter private DeviceListFragment listFragment;
    @Getter private DeviceDetailFragment detailFragment;

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

    /**
     * Method to setup the {@link android.support.v7.widget.Toolbar}
     * as supportActionBar in this {@link android.support.v7.app.ActionBarActivity}.
     */
    private void setupToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getResources().getString(R.string.app_name));
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.inflateMenu(R.menu.action_items);
            this.setSupportActionBar(toolbar);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.setupToolBar();

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
        listFragment = (DeviceListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_list);
        detailFragment = (DeviceDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
        if (listFragment != null) {
            listFragment.clearPeers();
        }
        if (detailFragment != null) {
            detailFragment.resetViews();
        }

        P2PGroups.getInstance().getGroupList().clear();

        if (detailFragment.getView() != null) {
            detailFragment.getView().findViewById(R.id.btn_start_ping_pong).setVisibility(View.GONE);
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(TAG, "onPeersAvailable");

        if (listFragment.getProgressDialog() != null && listFragment.getProgressDialog().isShowing()) {
            listFragment.getProgressDialog().dismiss();
        }

        PeerList.getInstance().getList().clear();
        PeerList.getInstance().addAllElements(peerList.getDeviceList());
        ((WiFiPeerListAdapter) listFragment.getMAdapter()).notifyDataSetChanged();
        if (PeerList.getInstance().getList().size() == 0) {
            Log.d(TAG, "No devices found");
            return;
        }


        if (PingPongList.getInstance().isPingponging() && !PingPongList.getInstance().isConnecting()) {

            //PINGPONG
            P2PDevice nextDeviceToConnect = PingPongList.getInstance().getNextDeviceToConnect();
            Log.d(TAG, "onPeersAvailable pingpong nextDeviceToConnect =  " + nextDeviceToConnect.getP2pDevice().deviceAddress);
            boolean found = false;

            for (P2PDevice device : PeerList.getInstance().getList()) {
                if (device.getP2pDevice()!=null && nextDeviceToConnect!=null
                        && device.getP2pDevice().deviceAddress.equals(nextDeviceToConnect.getP2pDevice().deviceAddress)) {
                    found = true;
                }
            }

            //now i verify if the device to use with Pingpong was found
            if (found) {

                PingPongList.getInstance().setConnecting(true);

                Log.d(TAG , System.currentTimeMillis() + " - connect");

                connect(new PingPongLogic(this).getConfigToReconnect());

            }

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
            case R.id.atn_direct_discover:
                if (!isWifiP2pEnabled) {
                    Toast.makeText(WiFiDirectActivity.this, R.string.p2p_off_warning, Toast.LENGTH_SHORT).show();
                    return true;
                }
                listFragment.onInitiateDiscovery();
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
        detailFragment.showDetails(device);

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
                "connect Failed with" + config.deviceAddress +  ". Retry"));
    }


    /**
     * Modified method to start a "silent disconnect" during the pingponging
     */
    public void disconnectPingPong() {
//        detailFragment = (DeviceDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
        detailFragment.resetViews();

        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {

                if (detailFragment.getView() != null) {
                    detailFragment.getView().setVisibility(View.GONE);
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
        detailFragment.resetViews();

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

                if (detailFragment.getView() != null) {
                    detailFragment.getView().setVisibility(View.GONE);
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
     * This method sets the name of this {@link it.polimi.wifidirect.model.LocalP2PDevice}
     * in the UI and inside the device. In this way, all other devices can see this updated name during the discovery phase.
     * Attention, WifiP2pManager uses ad annotation called @hide to hide the method called setDeviceName, in Android SDK.
     * This method uses Java reflection to call this hidden method.
     * @param deviceName String that represents the visible device name of a device, during discovery.
     */
    public void setDeviceNameWithReflection(String deviceName) {
        try {
            Method m = manager.getClass().getMethod(
                    "setDeviceName",
                    new Class[]{WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class});

            m.invoke(manager, channel, deviceName,
                    new CustomizableActionListener(
                            WiFiDirectActivity.this,
                            "setDeviceNameWithReflection",
                            "Device name changed",
                            "Device name changed",
                            "Error, device name not changed",
                            "Error, device name not changed"));
        } catch (Exception e) {
            Log.e(TAG, "Exception during setDeviceNameWithReflection" , e);
            Toast.makeText(WiFiDirectActivity.this, "Impossible to change the device name", Toast.LENGTH_SHORT).show();
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
