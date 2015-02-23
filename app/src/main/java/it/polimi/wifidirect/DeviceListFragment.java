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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import it.polimi.wifidirect.dialog.LocalDeviceDialog;
import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;
import lombok.Getter;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events.
 *
 * Created by Stefano Cappa, based on google code samples
 */
public class DeviceListFragment extends Fragment implements
        //DialogConfirmListener is the interface in LocalDeviceDialog. I use this to call
        //public void changeLocalDeviceName(String deviceName) in this class from the DialogFragment without to pass attributes or other stuff
        LocalDeviceDialog.DialogConfirmListener,
        //ItemClickListener is the interface in the adapter to intercept item's click events.
        //I use this to call itemClicked(v) in this class from WiFiPeerListAdapter.
        WiFiPeerListAdapter.ItemClickListener {

    private static final String TAG = "DeviceListFragment";
    @Getter private ProgressDialog progressDialog = null;
    private  View mContentView = null;
    private RecyclerView mRecyclerView;
    @Getter private WiFiPeerListAdapter mAdapter;

    /**
     * Method to obtain a new Fragment's instance.
     * @return This Fragment instance.
     */
    public static DeviceListFragment newInstance() {
        return new DeviceListFragment();
    }

    /**
     * Default Fragment constructor.
     */
    public DeviceListFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, container, false);
        mContentView.setTag(TAG);

        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // allows for optimizations if all item views are of the same size.
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new WiFiPeerListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final CheckBox pingpong_checkbox = (CheckBox) mContentView.findViewById(R.id.pingpong_checkbox);
        pingpong_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalP2PDevice.getInstance().setPing_pong_mode(pingpong_checkbox.isChecked());
            }
        });

        CardView cardviewLocalDevice = (CardView) mContentView.findViewById(R.id.cardviewLocalDevice);
        cardviewLocalDevice.setOnClickListener(new OnClickListenerLocalDevice(this));

        return mContentView;
    }

    /**
     * Method called by {@link it.polimi.wifidirect.WiFiPeerListAdapter}
     * with the {@link it.polimi.wifidirect.WiFiPeerListAdapter.ItemClickListener}
     * interface, when the user click on an element of the {@link android.support.v7.widget.RecyclerView}.
     * @param view {@link android.view.View} clicked.
     */
    @Override
    public void itemClicked(View view) {
        int clickedPosition = mRecyclerView.getChildPosition(view);
        Log.d(TAG, "Clicked position: " + clickedPosition);

        if(clickedPosition>=0) { //a little check :)
//            P2PDevice device = (P2PDevice) getListAdapter().getItem(clickedPosition);
            P2PDevice device = PeerList.getInstance().getList().get(clickedPosition);
            ((DeviceActionListener) getActivity()).showDetails(device);
        }
    }


    /**
     * Method to retrieve the device's status message using his code.
     * @param deviceStatus int that represents the status code
     * @return A String that represents the status message
     */
    private static String getDeviceStatus(int deviceStatus) {
        Log.d(WiFiDirectActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }


    /**
     * Update UI for this device.
     *
     * @param device The P2PDevice used to update the UI
     */
    public void updateThisDevice(P2PDevice device) {
        LocalP2PDevice.getInstance().setLocalDevice(device);
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.getP2pDevice().deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.getP2pDevice().status));
        view = (TextView) mContentView.findViewById(R.id.my_mac_address);
        view.setText(device.getP2pDevice().deviceAddress);
    }




    /**
     * Method to clear peer's list and to update the UI.
     */
    public void clearPeers() {
        PeerList.getInstance().getList().clear();
        ((WiFiPeerListAdapter) this.getMAdapter()).notifyDataSetChanged();
    }

    /**
     *  Method to manage the ProgressDialog during discovery procedures.
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }

    /**
     * Method to change the local device name and update the GUI element.
     * @param deviceName String that represents the device name.
     */
    @Override
    public void changeLocalDeviceName(String deviceName) {
        if(deviceName==null) {
            return;
        }

        if(mContentView!=null) {
            ((TextView) mContentView.findViewById(R.id.my_name)).setText(deviceName);
            ((WiFiDirectActivity) getActivity()).setDeviceNameWithReflection(deviceName);
        }
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {

        void showDetails(P2PDevice device);

        void connect(WifiP2pConfig config);

        void disconnect();

    }


    /**
     * Inner class that implements the Onclicklistener for the local device cardview.
     * It's useful to open the {@link it.polimi.wifidirect.dialog.LocalDeviceDialog}
     * after a click's event.
     */
    class OnClickListenerLocalDevice implements View.OnClickListener {

        private final Fragment fragment;

        public OnClickListenerLocalDevice(Fragment fragment1) {
            fragment = fragment1;
        }

        @Override
        public void onClick(View v) {
            LocalDeviceDialog localDeviceDialogFragment = (LocalDeviceDialog) getFragmentManager()
                    .findFragmentByTag("localDeviceDialogFragment");

            if (localDeviceDialogFragment == null) {
                localDeviceDialogFragment = LocalDeviceDialog.newInstance();
                localDeviceDialogFragment.setTargetFragment(fragment, 0);

                localDeviceDialogFragment.show(getFragmentManager(), "localDeviceDialogFragment");
                getFragmentManager().executePendingTransactions();
            }
        }
    }

}
