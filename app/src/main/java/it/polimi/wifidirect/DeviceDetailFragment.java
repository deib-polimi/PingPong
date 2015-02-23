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
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.wifidirect.dialog.PingPongDialog;
import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.P2PGroup;
import it.polimi.wifidirect.model.P2PGroups;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 *
 * Created by Stefano Cappa, based on google code samples
 */
public class DeviceDetailFragment extends Fragment implements
        WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.GroupInfoListener {

    private static final String TAG = "DDF_PingPong";
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;

    private P2PDevice device;
    private WifiP2pInfo info;

    private ProgressDialog progressDialog = null;
    private Fragment fragment = this;
    private static final int PINGPONG = 5; //constant number

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);

        //click connect's button
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.getP2pDevice().deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.getP2pDevice().deviceAddress, true, true
                );
                ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);


            }
        });


        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*, image/*");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                    }
                });


        //click on connect's button
        mContentView.findViewById(R.id.btn_start_ping_pong).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PingPongDialog pingPongDialog = (PingPongDialog) getFragmentManager().findFragmentByTag("pingPongDialog");

                if (pingPongDialog == null) {
                    pingPongDialog = PingPongDialog.newInstance();

                    pingPongDialog.setTargetFragment(fragment, PINGPONG);

                    pingPongDialog.show(getFragmentManager(), "pingPongDialog");
                    getFragmentManager().executePendingTransactions();
                }
            }
        });

        mContentView.findViewById(R.id.btn_start_ping_pong).setVisibility(View.GONE);

        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PINGPONG:

                if (resultCode == Activity.RESULT_OK) {
                    // After Ok code.
                    Bundle bundle = data.getExtras();
                    PingPongList.getInstance().setPing_macaddress(bundle.getString("ping_address"));
                    PingPongList.getInstance().setPong_macaddress(bundle.getString("pong_address"));
                    PingPongList.getInstance().setTestmode(bundle.getBoolean("testmode_checkbox_status"));

                    //to enable pingpong mode
                    PingPongList.getInstance().setPingponging(true);

                    P2PDevice pingDevice = PeerList.getInstance().getDeviceByMacAddress(PingPongList.getInstance().getPing_macaddress());
                    P2PDevice pongDevice = PeerList.getInstance().getDeviceByMacAddress(PingPongList.getInstance().getPong_macaddress());

                    PingPongList.getInstance().setPingDevice(pingDevice);
                    PingPongList.getInstance().setPongDevice(pongDevice);

                    Log.d(TAG, "I pressed on yes and the mac addresses received are, ping: "
                            + PingPongList.getInstance().getPing_macaddress()
                            + " and pong: " + PingPongList.getInstance().getPong_macaddress());

                    this.startPingponging();

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // After Cancel code.
                    Log.d(TAG, "I pressed NO");
                }

                break;
            default:
                // User has picked a file. Transfer it to group owner i.e peer using
                // FileTransferService.
                if(data!=null) {
                    Uri uri = data.getData();
                    Log.d(TAG, "Intent----------- " + uri);

                    TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
                    statusText.setText("Sending: " + uri);

                    Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
                    serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
                    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
                    serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                            P2PGroups.getInstance().getGroupList().get(0).getGroupOwnerIpAddress().getHostAddress());
                    serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                    getActivity().startService(serviceIntent);
                }
                break;

        }


    }

    /**
     * Method to start Pingponging.
     */
    private void startPingponging() {
        new PingPongLogic(this.getActivity()).execute();

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

        if(!P2PGroups.getInstance().getGroupList().contains(p2pGroup)) {
            P2PGroups.getInstance().getGroupList().add(p2pGroup);
            P2PGroups.getInstance().getGroupList().get(0).setGroupOwnerIpAddress(info.groupOwnerAddress);
        }

        P2PDevice client;
        for (WifiP2pDevice device : group.getClientList()) {
            client = new P2PDevice(device);
            client.setGroupOwner(false);
            p2pGroup.getList().add(client);
        }


        if(!group.isGroupOwner()) {
            //if i am a client, it's possible that i want to pingpong with another group owner.
            // For this reason, i set in the pingpong list all the possible group owners,
            // assuming that this devices are go of well formed groups.
            //If i am a group owner i can't pingpong with another device.

            //I set in the list my actual group owner.
            PingPongList.getInstance().getPingponglist().add(owner);

            //now i use the peerlist to fill the PingPongList. Obviously, i must remove my group owner and all my
            // brothers ;) (the other clients in my group).
            // And finally i need to check that every device in this list is a group owner.
            for(P2PDevice dev : PeerList.getInstance().getList()) {
                if(dev.isGroupOwner() && !p2pGroup.getList().contains(dev) &&
                        !PingPongList.getInstance().getPingponglist().contains(dev)) {
                    PingPongList.getInstance().getPingponglist().add(dev);
                }
            }
        }
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if(this.getView()!=null) {
            this.getView().setVisibility(View.VISIBLE);
        }

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            this.info = info;

            //as GO
            LocalP2PDevice.getInstance().getLocalDevice().setGroupOwner(true);

            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
                    .execute();
        } else if (info.groupFormed) {
            this.info = info;

            // The other device acts as the client. In this case, i enable the
            // get file button.
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);

        //enable ping pong button only if the local device is a client
        if (!LocalP2PDevice.getInstance().getLocalDevice().isGroupOwner()) {
            mContentView.findViewById(R.id.btn_start_ping_pong).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(P2PDevice device) {
        this.device = device;

        if(this.getView()!=null) {
            this.getView().setVisibility(View.VISIBLE);
        }
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.getP2pDevice().deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.getP2pDevice().toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);

        if(this.getView()!=null) {
            this.getView().setVisibility(View.GONE);
        }
    }
}
