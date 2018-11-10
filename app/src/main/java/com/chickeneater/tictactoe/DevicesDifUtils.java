package com.chickeneater.tictactoe;

import com.chickeneater.tictactoe.core.data.DeviceInList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class DevicesDifUtils extends DiffUtil.ItemCallback<DeviceInList> {

    @Override
    public boolean areItemsTheSame(@NonNull DeviceInList oldItem, @NonNull DeviceInList newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull DeviceInList oldItem, @NonNull DeviceInList newItem) {
        return oldItem.getAddress().equals(newItem.getAddress());
    }
}
