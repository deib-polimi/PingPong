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
import android.widget.Spinner;

import java.util.ArrayList;

import it.polimi.wifidirect.R;
import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.P2PGroups;
import it.polimi.wifidirect.model.PingPongList;
import it.polimi.wifidirect.spinner.CustomSpinnerAdapter;
import it.polimi.wifidirect.spinner.SpinnerRow;

/**
 * Created by Stefano Cappa on 30/01/15.
 *
 * Class that represents a DialogFragment used to choose the GO's mac address of the device which you want to do "pingpong".
 * Be careful, because, before that you can proceed to use this device as a "pingpong device", it must be connected to a GO.
 * In this Dialog there is the GO's mac address of the other group, where this device is not a peer/client, but he want to do "pingpong".
 *
 *
 */
public class PingPongDialog extends DialogFragment {

    static private Button ok, no;

    //spinner_ping is the device where this client is already connected, spinner_pong is the other GO.
    static private Spinner spinner_ping, spinner_pong;
    static private CheckBox testmode_checkbox;

    static ArrayList<SpinnerRow> list_ping, list_pong; //list_ping: list of starting device, list_pong: list of destinations

    /**
     * Method to obtain a new Fragment's instance.
     * @return This Fragment instance.
     */
    static public PingPongDialog newInstance() {
        return new PingPongDialog();
    }

    /**
     * Default Fragment constructor.
     */
    public PingPongDialog() {}


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
        for(P2PDevice pingpongDevice : PingPongList.getInstance().getPingponglist()) {
            list_pong.add(new SpinnerRow(pingpongDevice.getP2pDevice().deviceAddress));
        }


        this.setSpinnerAdapter(spinner_ping,list_ping);
        this.setSpinnerAdapter(spinner_pong,list_pong);

        //the ping device is forced to the actual GO of this client.
        spinner_ping.setEnabled(false);

        this.setListener();

        return v;
    }

    /**
     * Method to set the Spinner adapter.
     * @param spinner The Spinner to set
     * @param list ArrayList<SpinnerRow> of the element to fill the Spinner.
     */
    public void setSpinnerAdapter(Spinner spinner, ArrayList<SpinnerRow> list) {
        spinner.setAdapter(new CustomSpinnerAdapter(this.getActivity(),
                android.R.layout.simple_spinner_item,
                list));
    }

    /**
     * Method to set listeners on buttons and checkbox.
     */
    public void setListener() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent();
                Bundle extras=new Bundle();
                extras.putString("ping_address",spinner_ping.getSelectedItem().toString());
                extras.putString("pong_address",spinner_ping.getSelectedItem().toString());
                extras.putBoolean("testmode_checkbox_status", testmode_checkbox.isChecked());
                i.putExtras(extras);

                //notify to DeviceDetailFragment, onActivityResult's method.
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
            }
        });

        testmode_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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