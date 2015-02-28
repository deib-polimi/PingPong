package it.polimi.wifidirect;

import android.app.Activity;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.util.Log;

import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PingPongList;

/**
 * Class that represents the logic/async to pingpong.
 * <p></p>
 * Created by Stefano Cappa on 01/02/15.
 */
class PingPongLogic extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "ping-pong-logic";
    private final Activity activity;

    /**
     * Constructor of the class
     * @param activity Activity
     */
    public PingPongLogic(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(TAG , System.currentTimeMillis() + " - Before delay");

        try {
            Thread.sleep(5000); //if you have problems use 5000 here
        } catch (InterruptedException e) {
            Log.e(TAG, "Pingponglogic-doinbackground" , e);
        }

        Log.d(TAG , System.currentTimeMillis() + " - After delay");

        P2PDevice pingDevice = PingPongList.getInstance().getPingDevice();
        P2PDevice pongDevice = PingPongList.getInstance().getPongDevice();

        if(pingDevice!=null && pongDevice!=null && pingDevice.getP2pDevice()!=null && pongDevice.getP2pDevice()!=null ) {
            Log.d(TAG, "Pingdevice macaddress : " + pingDevice.getP2pDevice().deviceAddress);
            Log.d(TAG, "Pongdevice macaddress : " + pongDevice.getP2pDevice().deviceAddress);
        }

        if (PingPongList.getInstance().isPingponging()) {

            //attention if you call chooseGroupOwner 2 times you'll get the same config element
            //WifiP2pConfig config = this.chooseGroupOwner(pingDevice, pongDevice);

            Log.d(TAG , System.currentTimeMillis() + " - disconnect");

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d(TAG, "I am the UI thread");
                    Log.d(TAG , System.currentTimeMillis() + " - After delay");
                    ((WiFiDirectActivity) activity).disconnectPingPong();
                }
            });
        } else {
            Log.d(TAG, "Ping Pong disabled");
        }
        return null;
    }

    /**
     * Method to choose the correct device for the pingpong procedure.
     * IMPORANT: this method switches between ping and pong addresses with the attribute use_pongAddress.
     * Every call of this method, if the attribute use_pongAddress is true, will be replaced with false, and otherwise.
     * @param pingDevice Initial GroupOwner of the pingponging device
     * @param pongDevice Destination GroupOwner
     * @return The {@link android.net.wifi.p2p.WifiP2pConfig}
     */
    private WifiP2pConfig chooseGroupOwner(P2PDevice pingDevice, P2PDevice pongDevice) {
        WifiP2pConfig config = new WifiP2pConfig();

        config.wps.setup = WpsInfo.PBC;

        //this requests to be a client, because a pingpong device can be only a client to pingponging
        //between to groups.
        config.groupOwnerIntent = 0;

        //i choose the GroupOwner to connect.
        //IMPORANT: this switches between ping and pong addresses with the attribute use_pongAddress.
        //Every call of this, if the attribute use_pongAddress is true, will be replaced with false, and otherwise.
        if (PingPongList.getInstance().isUse_pongAddress()) {
            Log.d(TAG, "destination - PingPong with pong : " + pongDevice.getP2pDevice().deviceAddress);

            config.deviceAddress = pongDevice.getP2pDevice().deviceAddress;

            PingPongList.getInstance().setUse_pongAddress(false);
        } else {
            Log.d(TAG, "destination - PingPong with ping : " + pingDevice.getP2pDevice().deviceAddress);

            config.deviceAddress = pingDevice.getP2pDevice().deviceAddress;

            PingPongList.getInstance().setUse_pongAddress(true);
        }

        return config;
    }

    /**
     * Method to get {@link android.net.wifi.p2p.WifiP2pConfig} to reconnect.
     * Attention if this device is not pingponging and you call this method
     * the result is null!
     * @return The {@link android.net.wifi.p2p.WifiP2pConfig}
     */
    public WifiP2pConfig getConfigToReconnect() {
        Log.d(TAG,"getConfigToReconnect");

        P2PDevice pingDevice = PingPongList.getInstance().getPingDevice();
        P2PDevice pongDevice = PingPongList.getInstance().getPongDevice();

        if (PingPongList.getInstance().isPingponging()) {

            return this.chooseGroupOwner(pingDevice, pongDevice);

        }
        return null;
    }

}