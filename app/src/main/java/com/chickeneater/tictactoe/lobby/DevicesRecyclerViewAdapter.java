package com.chickeneater.tictactoe.lobby;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chickeneater.tictactoe.R;


public class DevicesRecyclerViewAdapter extends ListAdapter<DeviceInList, DevicesRecyclerViewAdapter.ViewHolder> {
    private OnDeviceSelectedListener onDeviceSelectedListener;

    public DevicesRecyclerViewAdapter(@NonNull DiffUtil.ItemCallback<DeviceInList> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //get elements
        final DeviceInList device = getItem(position);

        viewHolder.deviceName.setText(device.getName());
        viewHolder.deviceAddress.setText(device.getAddress());
        if(device.getPaired()){
            viewHolder.pairedIcon.setVisibility(View.VISIBLE);
        }else{
            viewHolder.pairedIcon.setVisibility(View.INVISIBLE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeviceSelectedListener.OnDeviceSelected(device);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView deviceName, deviceAddress;
        ImageView pairedIcon;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceNameView);
            deviceAddress = itemView.findViewById(R.id.deviceAddressView);
            pairedIcon = itemView.findViewById(R.id.pairdImageView);
        }
    }

    public interface OnDeviceSelectedListener{
        void OnDeviceSelected(DeviceInList deviceList);
    }

    public void setOnDeviceSelectedListener(OnDeviceSelectedListener onDeviceSelectedListener){
        this.onDeviceSelectedListener = onDeviceSelectedListener;
    }
}
