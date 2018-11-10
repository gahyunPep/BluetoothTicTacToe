package com.chickeneater.tictactoe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder>{
    private List<LobbyActivity.DeviceInList> devices;
    private OnDeviceSelectedListener onDeviceSelectedListener;

    public DevicesRecyclerViewAdapter(List<LobbyActivity.DeviceInList> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //get elements
        final LobbyActivity.DeviceInList device = devices.get(position);

        viewHolder.deviceName.setText(device.getName());
        viewHolder.deviceAddress.setText(device.getAddress());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeviceSelectedListener.OnDeviceSelected(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView deviceName, deviceAddress;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceNameView);
            deviceAddress = itemView.findViewById(R.id.deviceAddressView);
        }
    }

    public interface OnDeviceSelectedListener{
        void OnDeviceSelected(LobbyActivity.DeviceInList deviceList);
    }

    public void setOnDeviceSelectedListener(OnDeviceSelectedListener onDeviceSelectedListener){
        this.onDeviceSelectedListener = onDeviceSelectedListener;
    }
}
