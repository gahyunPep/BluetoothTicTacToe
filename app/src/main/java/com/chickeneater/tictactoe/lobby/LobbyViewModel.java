package com.chickeneater.tictactoe.lobby;

import android.content.Context;

import com.chickeneater.tictactoe.core.data.BluetoothDiscoveryService;
import com.chickeneater.tictactoe.core.data.OnBluetoothConnectionServiceListener;
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
        OnBluetoothConnectionServiceListener {
    private BluetoothDiscoveryService mDiscoveryService;
    private TickTackBluetoothService mBluetoothService = TickTackBluetoothService.getInstance();

    private MutableLiveData<Boolean> isDiscovering = new MutableLiveData<>();

    private Set<DeviceInList> mDevicesSet = new HashSet<>();

    private MutableLiveData<List<DeviceInList>> mDevicesData = new MutableLiveData<>();

    private MutableLiveData<Event<Device>> mDeviceConnectedEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Void>> mDeviceConnectionFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> locationPermissionDenied = new MutableLiveData<>();

    private LobbyViewModel(Context context) {
        mDiscoveryService = new BluetoothDiscoveryService();
        mDiscoveryService.discoverDevices(context, this);
        locationPermissionDenied.setValue(false);
    }

    public LiveData<Boolean> getIsDiscovering() {
        return isDiscovering;
    }

    public LiveData<List<DeviceInList>> getDevicesData() {
        return mDevicesData;
    }

    public LiveData<Event<Device>> getDeviceConnectedEvent() {
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
    public void onPreviouslyPairedDevicesObtained(Set<DeviceInList> pairedDevices) {
        boolean isAnyAdded = false;
        for (DeviceInList device: pairedDevices) {
            isAnyAdded |= mDevicesSet.add(device);
        }

        if (isAnyAdded) {
            displayDevices();
        }
    }

    private void displayDevices() {
        ArrayList<DeviceInList> devices = new ArrayList<>(mDevicesSet);
        Collections.sort(devices);
        mDevicesData.setValue(devices);
    }

    @Override
    public void onDeviceDiscovered(DeviceInList bluetoothDevice) {
        //Try to add device into a set if can update LiveData
        if (mDevicesSet.add(bluetoothDevice)) {
            displayDevices();
        }
    }

    public void connectToDevice(DeviceInList device) {
        if (!(device instanceof DeviceInList.FakeDevice)) {
            mBluetoothService.addConnectionListener(this);
            mBluetoothService.start();
            mBluetoothService.connect(device.getAddress());
        } else {
            onConnectedTo(device.getName(), true);
        }
    }

    @Override
    public void onConnectedTo(String deviceName, boolean asHost) {
        mDeviceConnectedEvent.setValue(new Event<>(new Device(deviceName, asHost)));
    }

    @Override
    public void onConnectionFailed() {
        mDeviceConnectionFailed.setValue(new Event<Void>(null));
    }

    public void setLocationPermissionDenied(boolean denied) {
        locationPermissionDenied.setValue(denied);
    }

    public LiveData<Boolean> getLocationPermissionDenied() {
        return locationPermissionDenied;
    }

    public class Device {
        public final String name;
        public final boolean isHost;

        public Device(String name, boolean isHost) {
            this.name = name;
            this.isHost = isHost;
        }
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