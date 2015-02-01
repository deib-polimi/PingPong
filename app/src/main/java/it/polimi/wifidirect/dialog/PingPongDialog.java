package it.polimi.wifidirect.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import it.polimi.wifidirect.R;
import it.polimi.wifidirect.model.LocalP2PDevice;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.P2PGroup;
import it.polimi.wifidirect.model.P2PGroups;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.model.PingPongList;
import it.polimi.wifidirect.spinner.CustomSpinnerAdapter;
import it.polimi.wifidirect.spinner.SpinnerRow;

/**
 *
 * Created by Stefano Cappa on 30/01/15.
 *
 * Classe che rappresenta il DialogFragment utilizzato per scegliere il mac address del GO del gruppo
 * con cui fare pingpong. Nota che prima di procedere al pingpong il dispositivo client deve appartenere gia' a un gruppo.
 * In questo Dialog viene fornito il mac address del GO dell'altro gruppo, a cui il Client non appartiene, ma vuole fare pingpong.
 *
 */
public class PingPongDialog extends DialogFragment {

    static private Button ok, no;
    static private Spinner spinner_ping, spinner_pong; //cioe' da dove iniziare pingpong a dove andare, prima di tornare nel gruppo iniziale
    static private CheckBox testmode_checkbox;

    static ArrayList<SpinnerRow> list_ping, list_pong;

    static public PingPongDialog newInstance() {
        return new PingPongDialog();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pingpong_dialog, container, false);

        this.getDialog().setTitle("Ping Pong");
        ok = (Button) v.findViewById(R.id.dialog_yes);
        no = (Button) v.findViewById(R.id.dialog_no);
        spinner_ping = (Spinner) v.findViewById(R.id.spinner_ping);
        spinner_pong = (Spinner) v.findViewById(R.id.spinner_pong);
        testmode_checkbox = (CheckBox) v.findViewById(R.id.testmode_checkbox);

        list_ping = new ArrayList<>();
        list_ping.add(new SpinnerRow(P2PGroups.getInstance().getGroupList().get(0).getGroupOwner().getP2pDevice().deviceAddress));

        list_pong = new ArrayList<>();
        for(P2PDevice pingpongdevice : PingPongList.getInstance().getPingponglist()) {
            list_pong.add(new SpinnerRow(pingpongdevice.getP2pDevice().deviceAddress));
        }


        this.setSpinnerAdapter(spinner_ping,list_ping);
        this.setSpinnerAdapter(spinner_pong,list_pong);

        //visto che il client e' gia' connesso quando avvio questa modalita' faccio si che la scelta di ping sia forzata
        //e non modificabile.
        spinner_ping.setEnabled(false);

        this.setListener();

        return v;
    }

    public void setSpinnerAdapter(Spinner spinner, ArrayList<SpinnerRow> list) {
        spinner.setAdapter(new CustomSpinnerAdapter(this.getActivity(),
                android.R.layout.simple_spinner_item,
                list));
    }

    public void setListener() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("pingpong_dialog_button", "ok");

                Intent i = new Intent();
                Bundle extras=new Bundle();
                extras.putString("ping_address",spinner_ping.getSelectedItem().toString());
                extras.putString("pong_address",spinner_ping.getSelectedItem().toString());
                extras.putBoolean("testmode_checkbox_status", testmode_checkbox.isChecked());
                i.putExtras(extras);

                //notifica a DeviceDetailFragment al metodo onActivityResult
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("pingpong_dialog_button", "no");
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
            }
        });

        testmode_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("testmode", "cliccato");

                if(testmode_checkbox.isChecked()) {
                    setSpinnerAdapter(spinner_pong,list_ping);

                    spinner_pong.setEnabled(false);
                } else {
                    setSpinnerAdapter(spinner_pong,list_pong);

                    spinner_pong.setEnabled(true);
                }
            }
        });
    }

}