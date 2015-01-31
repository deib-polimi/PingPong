package it.polimi.wifidirect;

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

public class PingPongDialog extends DialogFragment {

    static private Button ok, no;
    static private EditText macAddress;

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