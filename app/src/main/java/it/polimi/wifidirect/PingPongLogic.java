package it.polimi.wifidirect;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;

/**
 * Created by Stefano Cappa on 01/02/15.
 */
public class PingPongLogic extends AsyncTask<Context, Void, Void> {

    private Activity activity;

    public PingPongLogic(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(final Context... context) {

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
                Log.d("pingpong_destination", "PingPong con pong : " + pongDevice.getP2pDevice().deviceAddress);

                config.deviceAddress = pongDevice.getP2pDevice().deviceAddress;

                PingPongList.getInstance().setUse_pongAddress(false);
            } else {
                Log.d("pingpong_destination", "PingPong con ping : " + pingDevice.getP2pDevice().deviceAddress);

                config.deviceAddress = pingDevice.getP2pDevice().deviceAddress;

                PingPongList.getInstance().setUse_pongAddress(true);
            }

            ((WiFiDirectActivity)activity).runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    ((WiFiDirectActivity) context[0]).disconnectPingPong();
                }
            });

            Log.d("ping-pong", "disconnect");

//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            Log.d("ping-pong", "discovery");
//
//            PingPongList.getInstance().setConnecting(false);
//
//            ((WiFiDirectActivity) context[0]).discoveryPingPong();
//
//                    Log.d("ping-pong", "discovery");
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    ((WiFiDirectActivity) context[0]).connect(config);
//
//                    Log.d("ping-pong", "connect");
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
        } else {
            Log.d("pingpong_destination", "Ping Pong disabilitato");
        }
        return null;
    }


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
                Log.d("pingpong_destination", "PingPong con pong : " + pongDevice.getP2pDevice().deviceAddress);

                config.deviceAddress = pongDevice.getP2pDevice().deviceAddress;

                PingPongList.getInstance().setUse_pongAddress(false);
            } else {
                Log.d("pingpong_destination", "PingPong con ping : " + pingDevice.getP2pDevice().deviceAddress);

                config.deviceAddress = pingDevice.getP2pDevice().deviceAddress;

                PingPongList.getInstance().setUse_pongAddress(true);
            }

            return config;

        }
        return null;
    }

}