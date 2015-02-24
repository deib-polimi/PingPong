package it.polimi.wifidirect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.wifidirect.model.ClientList;
import it.polimi.wifidirect.model.P2PDevice;

/**
 * ListAdapter of {@link it.polimi.wifidirect.model.P2PDevice}.
 * Created by Stefano Cappa on 24/02/15.
 */
public class WiFiDetailClientListAdapter extends RecyclerView.Adapter<WiFiDetailClientListAdapter.ViewHolder> {

    /**
     * Constructor of the adapter
     */
    public WiFiDetailClientListAdapter() {
        setHasStableIds(true);
    }

    /**
     * The ViewHolder of this Adapter, useful to store e recycle element for performance reasons.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView macAddressText;

        public ViewHolder(View view) {
            super(view);

            nameText = (TextView) view.findViewById(R.id.device_name);
            macAddressText = (TextView) view.findViewById(R.id.device_address);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View v = layoutInflater.inflate(R.layout.row_client_device, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        P2PDevice device = ClientList.getInstance().getList().get(position);
        if (device != null) {
            if (viewHolder.nameText != null) {
                viewHolder.nameText.setText(device.getP2pDevice().deviceName);
            }
            if (viewHolder.macAddressText != null) {
                viewHolder.macAddressText.setText(device.getP2pDevice().deviceAddress);
            }
        }
    }


    @Override
    public int getItemCount() {
        return ClientList.getInstance().getList().size();
    }
}
