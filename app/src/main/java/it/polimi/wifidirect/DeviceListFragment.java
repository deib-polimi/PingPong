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

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;
import lombok.NonNull;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events.
 *
 * Created by Stefano Cappa, based on google code samples
 */
public class DeviceListFragment extends ListFragment implements PeerListListener {

    private ProgressDialog progressDialog = null;
    private  View mContentView = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices));

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);

        final CheckBox pingpong_checkbox = (CheckBox) mContentView.findViewById(R.id.pingpong_checkbox);
        pingpong_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalP2PDevice.getInstance().setPing_pong_mode(pingpong_checkbox.isChecked());
                Log.d("pingpong_checkbox", "Stato pingpongmode: " + LocalP2PDevice.getInstance().isPing_pong_mode());
            }
        });

        return mContentView;
    }


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
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        P2PDevice device = (P2PDevice) getListAdapter().getItem(position);
        ((DeviceActionListener) getActivity()).showDetails(device);
    }


    /**
     * Update UI for this device.
     *
     * @param device P2PDevice object
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

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d("onPeersAvailable", "onPeersAvailable");

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        for (WifiP2pDevice device1 : peerList.getDeviceList()) {
            Log.d("peerlist", device1.deviceAddress);
        }

        PeerList.getInstance().getList().clear();
        PeerList.getInstance().addAllElements(peerList.getDeviceList());
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (PeerList.getInstance().getList().size() == 0) {
            Log.d(WiFiDirectActivity.TAG, "No devices found");
            return;
        }


        if (PingPongList.getInstance().isPinponging() && !PingPongList.getInstance().isConnecting()) {

            //PINGPONG

            P2PDevice nextDeviceToConnect = PingPongList.getInstance().getNextDeviceToConnect();
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

                Log.d("ping-pong" , System.currentTimeMillis() + " - connect");

                ((WiFiDirectActivity) this.getActivity()).connect(new PingPongLogic(this.getActivity()).getConfigToReconnect());

            }

        }
    }


    /**
     * Method to clear peer's list and to update the UI.
     */
    public void clearPeers() {
        PeerList.getInstance().getList().clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
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
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {

        void showDetails(P2PDevice device);

        void cancelDisconnect();

        void connect(WifiP2pConfig config);

        void disconnect();

        void disconnectPingPong();

        void discoveryPingPong();
    }

}
