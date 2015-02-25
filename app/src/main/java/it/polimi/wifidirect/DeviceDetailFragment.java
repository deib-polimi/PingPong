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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.polimi.wifidirect.dialog.PingPongDialog;
import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.P2PGroups;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;
import it.polimi.wifidirect.utilities.DeviceStatus;
import lombok.Getter;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 *
 * Created by Stefano Cappa, based on google code samples
 */
public class DeviceDetailFragment extends Fragment {

    private static final String TAG = "DDF_PingPong";
    private static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;

    @Getter private WiFiDetailClientListAdapter mAdapter;

    private P2PDevice device;

    @Getter private ProgressDialog progressDialog = null;
    private final Fragment fragment = this;
    private static final int PINGPONG = 5; //constant number

    /**
     * Method to obtain a new Fragment's instance.
     * @return This Fragment instance.
     */
    public static DeviceDetailFragment newInstance() {
        return new DeviceDetailFragment();
    }

    /**
     * Default Fragment constructor.
     */
    public DeviceDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, container, false);

        this.updateThisDevice();

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


        RecyclerView mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.clientRecyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // allows for optimizations if all item views are of the same size.
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new WiFiDetailClientListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        return mContentView;
    }

    /**
     * Update UI for this device.
     */
    public void updateThisDevice() {
        TextView myNameCardView = (TextView) mContentView.findViewById(R.id.my_name);
        TextView myStatusCardView = (TextView) mContentView.findViewById(R.id.my_status);
        TextView myMacAddressCardView = (TextView) mContentView.findViewById(R.id.my_mac_address);

        myNameCardView.setText(LocalP2PDevice.getInstance().getLocalDevice().getP2pDevice().deviceName);
        myStatusCardView.setText(DeviceStatus.getDeviceStatus(LocalP2PDevice.getInstance().getLocalDevice().getP2pDevice().status));
        myMacAddressCardView.setText(LocalP2PDevice.getInstance().getLocalDevice().getP2pDevice().deviceAddress);
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

    /**
     * Method to set the {@link it.polimi.wifidirect.model.P2PDevice}
     *
     * @param device the device to set
     */
    public void setP2pDevice(P2PDevice device) {
        this.device = device;
    }

    /**
     * Method to show a GO Icon inside the cardview in {@link it.polimi.wifidirect.DeviceDetailFragment}
     * of the connected device.
     * This is useful to identify which device is a GO.
     */
    public void showConnectedDeviceGoIcon(){
        if(getView() !=null && getView().findViewById(R.id.device_go_logo)!=null && getView().findViewById(R.id.device_i_am_your_go_textview)!=null) {
            ImageView deviceGoLogoImageView = (ImageView) getView().findViewById(R.id.device_go_logo);
            TextView device_i_am_a_go_textView = (TextView) getView().findViewById(R.id.device_i_am_your_go_textview);

            deviceGoLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.go_logo_black));
            deviceGoLogoImageView.setVisibility(View.VISIBLE);
            device_i_am_a_go_textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method to hide a GO Icon inside the cardview in {@link it.polimi.wifidirect.DeviceDetailFragment}
     * of the connected device.
     * This is useful to identify which device is a GO.
     */
    public void hideConnectedDeviceGoIcon() {
        if(getView()!=null && getView().findViewById(R.id.device_go_logo)!=null && getView().findViewById(R.id.device_i_am_your_go_textview)!=null) {
            ImageView deviceGoLogoImageView = (ImageView) getView().findViewById(R.id.device_go_logo);
            TextView device_i_am_a_go_textView = (TextView) getView().findViewById(R.id.device_i_am_your_go_textview);

            deviceGoLogoImageView.setVisibility(View.INVISIBLE);
            device_i_am_a_go_textView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Clears the UI fields in device cardview.
     */
    public void resetViews() {
        this.hideConnectedDeviceGoIcon();
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);

        TextView view = (TextView) mContentView.findViewById(R.id.device_name);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
    }
}
