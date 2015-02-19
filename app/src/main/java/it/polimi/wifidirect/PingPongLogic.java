package it.polimi.wifidirect;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.util.Log;

import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PingPongList;

/**
 * Class that represents the logic/async task of PingPong.
 *
 * Created by Stefano Cappa on 01/02/15.
 */
public class PingPongLogic extends AsyncTask<Context, Void, Void> {

    private Activity activity;

    /**
     * Constructor of the class
     * @param activity Activity
     */
    public PingPongLogic(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(final Context... context) {

        Log.d("ping-pong" , System.currentTimeMillis() + " - Before delay");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("ping-pong" , System.currentTimeMillis() + " - After delay");

        P2PDevice pingDevice = PingPongList.getInstance().getPingDevice();
        P2PDevice pongDevice = PingPongList.getInstance().getPongDevice();

        Log.d("Pingdevice macaddress", "Pingdevice macaddress : " + pingDevice.getP2pDevice().deviceAddress);
        Log.d("Pongdevice macaddress", "Pongdevice macaddress : " + pongDevice.getP2pDevice().deviceAddress);

        if (PingPongList.getInstance().isPinponging()) {
            WifiP2pConfig config = new WifiP2pConfig();

            config.wps.setup = WpsInfo.PBC;
            //per poter diventare client (visto che pingpong e' un client che saltella tra due gruppi)
            //e non iniziare un nuovo gruppo come GO
            config.groupOwnerIntent = 0;

            //stabilisco a quale GO questo client si dovra' connettere
            if (PingPongList.getInstance().isUse_pongAddress()) {
                Log.d("pingpong_destination", "PingPong with pong : " + pongDevice.getP2pDevice().deviceAddress);

                config.deviceAddress = pongDevice.getP2pDevice().deviceAddress;

                PingPongList.getInstance().setUse_pongAddress(false);
            } else {
                Log.d("pingpong_destination", "PingPong with ping : " + pingDevice.getP2pDevice().deviceAddress);

                config.deviceAddress = pingDevice.getP2pDevice().deviceAddress;

                PingPongList.getInstance().setUse_pongAddress(true);
            }

            Log.d("ping-pong" , System.currentTimeMillis() + " - disconnect");

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    Log.d("Pingponglogic" , System.currentTimeMillis() + " - After delay");
                    ((WiFiDirectActivity) context[0]).disconnectPingPong();
                }
            });
        } else {
            Log.d("pingpong_destination", "Ping Pong disabled");
        }
        return null;
    }


    /**
     * Method to get WifiP2pConfig to reconnect.
     * @return WifiP2pConfig
     */
    public WifiP2pConfig getConfigToReconnect() {
        Log.d("getConfigToReconnect","getConfigToReconnect");

        if (PingPongList.getInstance().isPinponging()) {

            P2PDevice pingDevice = PingPongList.getInstance().getPingDevice();
            P2PDevice pongDevice = PingPongList.getInstance().getPongDevice();

            WifiP2pConfig config = new WifiP2pConfig();

            config.wps.setup = WpsInfo.PBC;
            //per poter diventare client (visto che pingpong e' un client che saltella tra due gruppi)
            //e non iniziare un nuovo gruppo come GO
            config.groupOwnerIntent = 0;

            //stabilisco a quale GO questo client si dovra' connettere
            if (PingPongList.getInstance().isUse_pongAddress()) {
                Log.d("pingpong_destination", "PingPong with pong : " + pongDevice.getP2pDevice().deviceAddress);

                config.deviceAddress = pongDevice.getP2pDevice().deviceAddress;

                PingPongList.getInstance().setUse_pongAddress(false);
            } else {
                Log.d("pingpong_destination", "PingPong with ping : " + pingDevice.getP2pDevice().deviceAddress);

                config.deviceAddress = pingDevice.getP2pDevice().deviceAddress;

                PingPongList.getInstance().setUse_pongAddress(true);
            }

            return config;

        }
        return null;
    }

}