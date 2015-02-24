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

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.wifidirect.dialog.LocalDeviceDialog;
import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.utilities.DeviceStatus;
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

        CardView cardviewLocalDevice = (CardView) mContentView.findViewById(R.id.cardviewLocalDevice);
        cardviewLocalDevice.setOnClickListener(new OnClickListenerLocalDevice(this));


        if(LocalP2PDevice.getInstance().getLocalDevice()!=null &&
                LocalP2PDevice.getInstance().getLocalDevice().getP2pDevice()!=null) {
            this.updateThisDevice();
        }

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




    /**
     * Method to clear peer's list and to update the UI.
     */
    public void clearPeers() {
        PeerList.getInstance().getList().clear();
        this.getMAdapter().notifyDataSetChanged();
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
