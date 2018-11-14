package com.chickeneater.tictactoe.lobby;

import android.content.Context;

import com.chickeneater.tictactoe.core.data.BluetoothDiscoveryService;
import com.chickeneater.tictactoe.core.data.TickTackBluetoothService;
import com.chickeneater.tictactoe.core.ui.Event;

import java.util.ArrayList;
import java.util.Collections;
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
public class LobbyViewModel extends ViewModel implements BluetoothDiscoveryService.BluetoothDiscoveryListener,
        TickTackBluetoothService.OnBluetoothConnectionServiceListener {
    private BluetoothDiscoveryService mDiscoveryService;
    private TickTackBluetoothService mBluetoothService;

    private MutableLiveData<Boolean> isDiscovering = new MutableLiveData<>();

    private Set<DeviceInList> mDevicesSet = new HashSet<>();
    private List<DeviceInList> mBluetoothDevices = new ArrayList<>();

    private MutableLiveData<List<DeviceInList>> mDevicesData = new MutableLiveData<>();

    private MutableLiveData<Event<String>> mDeviceConnectedEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Void>> mDeviceConnectionFailed = new MutableLiveData<>();

    private LobbyViewModel(Context context) {
        mDiscoveryService = new BluetoothDiscoveryService();
        mDiscoveryService.discoverDevices(context, this);
        mBluetoothService = TickTackBluetoothService.getInstance();
        mBluetoothService.addConnectionListener(this);
        mBluetoothService.start();
    }

    public LiveData<Boolean> getIsDiscovering() {
        return isDiscovering;
    }

    public LiveData<List<DeviceInList>> getDevicesData() {
        return mDevicesData;
    }

    public LiveData<Event<String>> getDeviceConnectedEvent() {
        return mDeviceConnectedEvent;
    }

    public MutableLiveData<Event<Void>> getDeviceConnectionFailed() {
        return mDeviceConnectionFailed;
    }

    public void restartDiscovery(Context context) {
        mDiscoveryService.stopDiscovery();
        mDiscoveryService.discoverDevices(context, this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mBluetoothService.removeConnectionListener(this);
        mDiscoveryService.stopDiscovery();
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
            Collections.sort(mBluetoothDevices);
            mDevicesData.setValue(mBluetoothDevices);
        }
    }

    public void connectToDevice(DeviceInList device) {
        mBluetoothService.connect(device.getAddress());
    }

    @Override
    public void onConnectedTo(String deviceName) {
        mDeviceConnectedEvent.setValue(new Event<>(deviceName));
    }

    @Override
    public void onConnectionFailed() {
        mDeviceConnectionFailed.setValue(new Event<Void>(null));
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