package it.polimi.wifidirect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.polimi.wifidirect.model.P2PDevice;
import it.polimi.wifidirect.model.PeerList;
import it.polimi.wifidirect.utilities.DeviceStatus;

/**
 * ListAdapter of {@link it.polimi.wifidirect.model.P2PDevice}.
 * Created by Stefano Cappa on 23/02/15.
 */
public class WiFiDeviceListAdapter extends RecyclerView.Adapter<WiFiDeviceListAdapter.ViewHolder> {

    private final ItemClickListener itemClickListener;

    /**
     * Constructor of the adapter
     * @param itemClickListener ClickListener to obtain click actions over the recyclerview's elements.
     */
    public WiFiDeviceListAdapter(@NonNull ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        setHasStableIds(true);
    }

    /**
     * {@link it.polimi.wifidirect.DeviceListFragment} implements this interface
     */
    public interface ItemClickListener {
        void itemClicked(final View view);
    }


    /**
     * The ViewHolder of this Adapter, useful to store e recycle element for performance reasons.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View parent;
        private final TextView nameText;
        private final TextView statusText;
        private final TextView macAddressText;
        private final ImageView isGoImage;
        private final TextView iAmGoText;

        public ViewHolder(View view) {
            super(view);

            this.parent = view;

            nameText = (TextView) view.findViewById(R.id.device_name);
            statusText = (TextView) view.findViewById(R.id.device_status);
            macAddressText = (TextView) view.findViewById(R.id.device_mac_address);
            isGoImage = (ImageView) view.findViewById(R.id.peerlist_go_logo);
            iAmGoText = (TextView) view.findViewById(R.id.peerlist_i_am_a_go_textview);
        }


        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View v = layoutInflater.inflate(R.layout.row_devices, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        P2PDevice device = PeerList.getInstance().getList().get(position);

        if (device != null) {
            if (viewHolder.nameText != null) {
                viewHolder.nameText.setText(device.getP2pDevice().deviceName);
            }
            if (viewHolder.statusText != null) {
                viewHolder.statusText.setText(DeviceStatus.getDeviceStatus(device.getP2pDevice().status));
            }
            if (viewHolder.macAddressText != null) {
                viewHolder.macAddressText.setText(device.getP2pDevice().deviceAddress);
            }
            if (viewHolder.isGoImage != null) {
                if(device.getP2pDevice().isGroupOwner()) {
                    viewHolder.isGoImage.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.isGoImage.setVisibility(View.INVISIBLE);
                }
            }
            if (viewHolder.iAmGoText != null) {
                if(device.getP2pDevice().isGroupOwner()) {
                    viewHolder.iAmGoText.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.iAmGoText.setVisibility(View.INVISIBLE);
                }
            }
        }

        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v);
            }
        });
    }


    @Override
    public int getItemCount() {
        return PeerList.getInstance().getList().size();
    }


}
