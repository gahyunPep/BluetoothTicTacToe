package com.chickeneater.tictactoe.lobby;

import android.content.Context;

import com.chickeneater.tictactoe.core.data.BluetoothConnector;
import com.chickeneater.tictactoe.core.data.DeviceInList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by romanlee on 11/10/18.
 * To the power of Love
 */
public class LobbyViewModel extends ViewModel implements BluetoothConnector.BluetoothDiscoveryListener {
    private BluetoothConnector mConnector;

    private MutableLiveData<Boolean> isDiscovering = new MutableLiveData<>();

    private Set<DeviceInList> mDevicesSet = new HashSet<>();
    private List<DeviceInList> mBluetoothDevices = new ArrayList<>();

    private MutableLiveData<List<DeviceInList>> mDevicesData = new MutableLiveData<>();

    private LobbyViewModel(Context context) {
        mConnector = new BluetoothConnector();
        mConnector.discoverDevices(context, this);
    }

    public LiveData<Boolean> getIsDiscovering() {
        return isDiscovering;
    }

    public LiveData<List<DeviceInList>> getDevicesData() {
        return mDevicesData;
    }

    public void restartDiscovery(Context context) {
        mConnector.stopDiscovery();
        mConnector.discoverDevices(context, this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mConnector.stopDiscovery();
    }

    @Override
    public void onDiscoveryStart() {
        isDiscovering.setValue(true);
    }

    @Override
    public void onDiscoveryFinished() {
        isDiscovering.setValue(false);
    }

    @Override
    public void onDeviceDiscovered(DeviceInList bluetoothDevice) {
        //Try to add device into a set if can update LiveData
        if (mDevicesSet.add(bluetoothDevice)) {
            mBluetoothDevices.add(bluetoothDevice);
            mDevicesData.setValue(mBluetoothDevices);
        }
    }

    public void connectToDevice(DeviceInList device) {
        //TODO connection
    }

    public static class Factory implements ViewModelProvider.Factory {
        private Context mContext;

        Factory(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LobbyViewModel.class)) {
                //noinspection unchecked
                return (T) new LobbyViewModel(mContext);
            } else {
                throw new RuntimeException("Cannot create an instance of " + modelClass);
            }
        }
    }
}