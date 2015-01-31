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

import it.polimi.wifidirect.R;
import it.polimi.wifidirect.spinner.CustomSpinnerAdapter;
import it.polimi.wifidirect.spinner.SpinnerRow;

public class PingPongDialog extends DialogFragment {

    static private Button ok, no;
    static private EditText macAddress;
    static private Spinner mac_spinner;

    static public PingPongDialog newInstance() {
        return new PingPongDialog();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pingpong_dialog, container, false);

        this.getDialog().setTitle("Ping Pong");
        ok = (Button) v.findViewById(R.id.dialog_yes);
        no = (Button) v.findViewById(R.id.dialog_no);
        macAddress = (EditText) v.findViewById(R.id.dialog_mac_address_edittext);

        ArrayList<SpinnerRow> ListDevicePingPong = new ArrayList<>();
        ListDevicePingPong.add(new SpinnerRow("sha1"));
        ListDevicePingPong.add(new SpinnerRow("sha256"));
        ListDevicePingPong.add(new SpinnerRow("sha384"));
        ListDevicePingPong.add(new SpinnerRow("sha512"));
        ListDevicePingPong.add(new SpinnerRow("md5"));
        CustomSpinnerAdapter customAdapterShaAlgorithm = new CustomSpinnerAdapter(this.getActivity(), android.R.layout.simple_spinner_item,ListDevicePingPong);

        mac_spinner = (Spinner) v.findViewById(R.id.device_spinner);
        mac_spinner.setAdapter(customAdapterShaAlgorithm);

        this.setListener();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setListener() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("listener", "ok");
                Intent i = new Intent();
                Bundle extras=new Bundle();
                extras.putString("macaddress_text",macAddress.getText().toString());
                i.putExtras(extras);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("listener", "no");
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
            }
        });
    }

}