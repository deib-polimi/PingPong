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
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;

import it.polimi.wifidirect.actionlisteners.CustomizableActionListener;
import it.polimi.wifidirect.filetransfer.FileServerAsyncTask;
import it.polimi.wifidirect.model.ClientList;
import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.P2PGroup;
import it.polimi.wifidirect.model.P2PGroups;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;
import it.polimi.wifidirect.utilities.SleepAsyncTask;
import lombok.Getter;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 * This activity manages the pingpong logic.
 * <p></p>
 * Created by Stefano Cappa, based on google code samples
 */
public class WiFiDirectActivity extends ActionBarActivity implements
        WifiP2pManager.ChannelListener,
        DeviceListFragment.DeviceActionListener,
        WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.GroupInfoListener {


    private static final String TAG = "P2P-PingPong";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    @Getter
    private DeviceListFragment listFragment;
    @Getter
    private DeviceDetailFragment detailFragment;
    private WifiP2pInfo info;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        listFragment = DeviceListFragment.newInstance();
        detailFragment = DeviceDetailFragment.newInstance();

        //show DeviceListFragment replacing container_root's FrameLayout with the Fragment
        this.showListFragment();

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
        if (listFragment != null) {
            listFragment.clearPeers();
        }

        P2PGroups.getInstance().getGroupList().clear();
        ClientList.getInstance().getList().clear();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(TAG, "onPeersAvailable");

        PeerList.getInstance().getList().clear();
        PeerList.getInstance().addAllElements(peerList.getDeviceList());
        listFragment.getMAdapter().notifyDataSetChanged();
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
                if (device.getP2pDevice() != null
                        && device.getP2pDevice().deviceAddress.equals(nextDeviceToConnect.getP2pDevice().deviceAddress)) {
                    found = true;
                }
            }

            //now i verify if the device to use with Pingpong was found
            if (found) {

                PingPongList.getInstance().setConnecting(true);

                Log.d(TAG, System.currentTimeMillis() + " - connect");

                connect(new PingPongLogic(this).getConfigToReconnect());
                this.showDetailFragment();

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
                manager.discoverPeers(channel, new CustomizableActionListener(
                        WiFiDirectActivity.this,
                        "discoverPeers",
                        null,
                        "Discovery Initiated",
                        null,
                        "Discovery Failed"));

                return true;
            case R.id.atn_pingpong:
                //update pingpong menu item after a click and setPing_pong_mode attribute in LocalP2PDevice
                if (LocalP2PDevice.getInstance().isPing_pong_mode()) {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_action_pingpong_mode_disabled));
                    LocalP2PDevice.getInstance().setPing_pong_mode(false);
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_action_pingpong_mode_enabled));
                    LocalP2PDevice.getInstance().setPing_pong_mode(true);
                }
                return true;
            case R.id.cancelConnection:

                this.forcedCancelConnect();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to show {@link it.polimi.wifidirect.DeviceDetailFragment}, set p2pDevice, clear Clientlist
     * and finally update the {@link it.polimi.wifidirect.WiFiDetailClientListAdapter}.
     *
     * @param device A {@link it.polimi.wifidirect.model.P2PDevice} to use in the fragment.
     */
    @Override
    public void showDetails(P2PDevice device) {
        this.showDetailFragment();

        if (detailFragment == null) {
            detailFragment = (DeviceDetailFragment) getSupportFragmentManager().findFragmentByTag("detailFragment");
        }

        detailFragment.setP2pDevice(device);

        //add "device" as client and update the adapter of the client's recyclerview.
        ClientList.getInstance().getList().add(device);
        detailFragment.getMAdapter().notifyDataSetChanged();

    }

    /**
     * To connect to a device, specified in config.deviceAddress.
     * @param config The WifiP2pConfig to connect.
     */
    @Override
    public void connect(WifiP2pConfig config) {
        Log.d(TAG, "Request connection");

        manager.connect(channel, config, new CustomizableActionListener(
                WiFiDirectActivity.this,
                "connect",
                "connect Success",
                null,
                "connect Failed",
                "connect Failed with" + config.deviceAddress + ". Retry"));
    }


    /**
     * Method to disconnect.
     */
    @Override
    public void disconnect() {
        P2PGroups.getInstance().getGroupList().clear();

        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                Toast.makeText(WiFiDirectActivity.this, "Disconnect Failed: " + reasonCode, Toast.LENGTH_SHORT).show();

                //disconnect failed (this happens when you click on disconnect
                // button, but you aren't really connected). I must restore the listFragment.
                showListFragment();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(WiFiDirectActivity.this, "Disconnect Success", Toast.LENGTH_SHORT).show();
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
     *
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
            Log.e(TAG, "Exception during setDeviceNameWithReflection", e);
            Toast.makeText(WiFiDirectActivity.this, "Impossible to change the device name", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        Log.d(TAG, "Group informations available");

        //group.getOwner() can be this device of not, this is not important at the moment,
        //because with this method i obtain always the group owner.
        P2PDevice owner = new P2PDevice(group.getOwner());
        owner.setGroupOwner(true);

        P2PGroup p2pGroup = new P2PGroup(true);
        p2pGroup.setGroup(group);
        p2pGroup.setGroupOwner(owner);

        if (!P2PGroups.getInstance().getGroupList().contains(p2pGroup)) {
            P2PGroups.getInstance().getGroupList().add(p2pGroup);
            P2PGroups.getInstance().getGroupList().get(0).setGroupOwnerIpAddress(info.groupOwnerAddress);
        }

        P2PDevice client;
        for (WifiP2pDevice device : group.getClientList()) {
            client = new P2PDevice(device);
            client.setGroupOwner(false);
            p2pGroup.getList().add(client);
        }


        if (!group.isGroupOwner()) {
            //if i am a client, it's possible that i want to pingpong with another group owner.
            // For this reason, i set in the pingpong list all the possible group owners,
            // assuming that this devices are go of well formed groups.
            //If i am a group owner i can't pingpong with another device.

            //I set in the list my actual group owner.
            PingPongList.getInstance().getPingponglist().add(owner);

            //now i use the peerlist to fill the PingPongList. Obviously, i must remove my group owner and all my
            // brothers ;) (the other clients in my group).
            // And finally i need to check that every device in this list is a group owner.
            List<P2PDevice> peerlist = PeerList.getInstance().getList();
            for (P2PDevice dev : peerlist) {
                if (!p2pGroup.getList().contains(dev) &&
                        !PingPongList.getInstance().getPingponglist().contains(dev)) {
                    PingPongList.getInstance().getPingponglist().add(dev);
                }
            }


            //i'm NOT the go
            if(detailFragment.getView()!=null) {
                P2PDevice device = new P2PDevice(group.getOwner());
                ClientList.getInstance().getList().clear();
                ClientList.getInstance().getList().add(device);
                detailFragment.showConnectedDeviceGoIcon();
                detailFragment.setP2pDevice(device);

                //now i call this to show the "connected" text in the DetailFragment
                detailFragment.updateThisDevice();

                detailFragment.getMAdapter().notifyDataSetChanged();
            }
        } else {
            //i'm the go
            if(detailFragment.getView()!=null) {
                detailFragment.hideConnectedDeviceGoIcon();

                ClientList.getInstance().getList().clear();

                for(P2PDevice device : p2pGroup.getList()) {
                    ClientList.getInstance().getList().add(device);

                }
                detailFragment.getMAdapter().notifyDataSetChanged();

            }
        }
    }

    /**
     * Method to show the {@link it.polimi.wifidirect.DeviceDetailFragment}
     * and to hide all others fragments.
     */
    public void showDetailFragment() {
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_root, detailFragment, "detailFragment")
                .commit();
        this.getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Method to show the {@link it.polimi.wifidirect.DeviceListFragment}
     * and to hide all others fragments.
     */
    public void showListFragment() {
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_root, listFragment, "listFragment")
                .commit();
        this.getSupportFragmentManager().executePendingTransactions();

    }

    /**
     * Method to show a GO Icon inside the local device in {@link it.polimi.wifidirect.DeviceDetailFragment}.
     * This is useful to identify which device is a GO.
     */
    private void showLocalDeviceGoIcon(){
        if(detailFragment !=null && detailFragment.getView()!=null && detailFragment.getView().findViewById(R.id.go_logo)!=null) {
            ImageView goLogoImageView = (ImageView) detailFragment.getView().findViewById(R.id.go_logo);
            goLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.go_logo));
            goLogoImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method to hide a GO Icon inside the local device in {@link it.polimi.wifidirect.DeviceDetailFragment}.
     * This is useful to identify which device is a GO.
     */
    public void hideLocalDeviceGoIcon(){
        if(detailFragment !=null && detailFragment.getView()!=null && detailFragment.getView().findViewById(R.id.go_logo)!=null) {
            ImageView goLogoImageView = (ImageView) detailFragment.getView().findViewById(R.id.go_logo);
            goLogoImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (detailFragment.getProgressDialog() != null && detailFragment.getProgressDialog().isShowing()) {
            detailFragment.getProgressDialog().dismiss();
        }

        this.showDetailFragment();

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            this.info = info;

            //as GO
            LocalP2PDevice.getInstance().getLocalDevice().setGroupOwner(true);

            this.showLocalDeviceGoIcon();

            new FileServerAsyncTask(this).execute();
        } else if (info.groupFormed) {
            this.info = info;

            this.hideLocalDeviceGoIcon();

            // The other device acts as the client. In this case, i enable the
            // get file button.
            if(detailFragment.getView()!=null) {
                detailFragment.getView().findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            }
        }

        // hide the connect button
        if(detailFragment.getView()!=null) {
            detailFragment.getView().findViewById(R.id.btn_connect).setVisibility(View.GONE);
        }

        //enable ping pong button only if the local device is a client
        if (!LocalP2PDevice.getInstance().getLocalDevice().isGroupOwner()) {
            detailFragment.getView().findViewById(R.id.btn_start_ping_pong).setVisibility(View.VISIBLE);
        }
    }


    /**
     * Modified method to start a "silent disconnect" during the pingponging
     */
    public void disconnectPingPong() {
        detailFragment.resetViews();

        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "Disconnect failed. Reason :" + reasonCode);

                //disconnect failed (this happens when you click on disconnect
                // button, but you aren't really connected). I must restore the listFragment.
                showListFragment();
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "Disconnect success");
                //now look in WiFiDirectBroadcastReceiver, line "if(LocalP2PDevice.getInstance().isPing_pong_mode()) {"
            }
        });
    }


    /**
     * Method to restart discovery after a disconnect.
     */
    public void restartDiscoveryPingpongAfterDisconnect() {
        Log.d(TAG, System.currentTimeMillis() + " - Preparing to discovery");

        new SleepAsyncTask(this).execute();

//        this.sleepCompleted();
        //when SleepAsyncTask is complete, its executes sleepCompleted()
    }

    public void sleepCompleted() {
        Log.d(TAG, System.currentTimeMillis() + " - Discovery started");

        PingPongList.getInstance().setConnecting(false);

        this.discoveryPingPong();
    }

    /**
     * Method to cancel a pending connection, used by the MenuItem icon.
     */
    private void forcedCancelConnect() {
        manager.cancelConnect(channel,
                new CustomizableActionListener(
                        this,
                        TAG,
                        "forcedCancelConnect success",
                        "Cancel connect success",
                        "forcedCancelConnect failed",
                        "Cancel connect failed"
                ));
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

        manager.stopPeerDiscovery(channel , new CustomizableActionListener(
                WiFiDirectActivity.this,
                "stopPeerDiscovery",
                null,
                null,
                null,
                null));
        manager.discoverPeers(channel, new CustomizableActionListener(
                WiFiDirectActivity.this,
                "discoveryPingPong",
                null,
                "Discovery Initiated",
                null,
                "Discovery Failed"));
    }
}
