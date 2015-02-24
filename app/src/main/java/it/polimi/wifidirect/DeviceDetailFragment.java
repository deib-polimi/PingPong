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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.wifidirect.dialog.PingPongDialog;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.P2PGroups;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;
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

    private P2PDevice device;


    @Getter private ProgressDialog progressDialog = null;
    private Fragment fragment = this;
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



    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(P2PDevice device) {
        this.device = device;

        if(getView()!=null) {
            TextView view = (TextView) getView().findViewById(R.id.device_address);
            view.setText(device.getP2pDevice().deviceAddress);
            view = (TextView) getView().findViewById(R.id.device_name);
            view.setText(device.getP2pDevice().deviceName);

            view = (TextView) getView().findViewById(R.id.device_name);
            view.setText(device.getP2pDevice().deviceName);
        }

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_name);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
    }
}
