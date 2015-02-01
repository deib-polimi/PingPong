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
    static private EditText macAddress;
    static private Spinner mac_spinner;

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
        macAddress = (EditText) v.findViewById(R.id.dialog_mac_address_edittext);


        //parto dalla lista di peer e rimuovo il local device e tutti quelli gia' nel gruppo se sono un group owner
        //ricorda comunque che la funzione ping pong deve essere fatta da un client e non da un group owner
        List<P2PDevice> peerList = PeerList.getInstance().getList();
        peerList.remove(LocalP2PDevice.getInstance().getLocalDevice());
        for(P2PDevice dev : P2PGroups.getInstance().getGroupList().get(0).getList()) {
            peerList.remove(dev);
        }


        ArrayList<SpinnerRow> macaddress = new ArrayList<>();
        for(P2PDevice device : peerList) {
            macaddress.add(new SpinnerRow(device.getP2pDevice().deviceAddress));
        }

        CustomSpinnerAdapter customAdapterShaAlgorithm = new CustomSpinnerAdapter(this.getActivity(), android.R.layout.simple_spinner_item,macaddress);

        mac_spinner = (Spinner) v.findViewById(R.id.device_spinner);
        mac_spinner.setAdapter(customAdapterShaAlgorithm);

        this.setListener();

        return v;
    }

    public void setListener() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("pingpong_dialog_button", "ok");
                Intent i = new Intent();
                Bundle extras=new Bundle();
//                extras.putString("macaddress_text",mac_spinner.getSelectedItem().toString());
                extras.putString("macaddress_text",macAddress.getText().toString());
                i.putExtras(extras);
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
    }

}