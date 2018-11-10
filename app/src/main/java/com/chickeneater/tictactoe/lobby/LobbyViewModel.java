package com.chickeneater.tictactoe.lobby;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.chickeneater.tictactoe.core.data.BluetoothConnector;
import com.chickeneater.tictactoe.core.data.DeviceInList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, BluetoothDevice> mDevicesMap = new HashMap<>();
    private List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();

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
    public void onDeviceDiscovered(BluetoothDevice bluetoothDevice) {
        //If device is already in our map do nothing
        if (mDevicesMap.containsKey(bluetoothDevice.getAddress())) {
            return;
        }

        mDevicesMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
        mBluetoothDevices.add(bluetoothDevice);
        mDevicesData.setValue(map(mBluetoothDevices));
    }

    private List<DeviceInList> map(List<BluetoothDevice> devices) {
        List<DeviceInList> retVal = new ArrayList<>(devices.size());
        for (BluetoothDevice device : devices) {
            retVal.add(new DeviceInList(device.getName(), device.getAddress()));
        }

        Collections.sort(retVal);
        return retVal;
    }

    public void connectToDevice(DeviceInList device) {
        BluetoothDevice bluetoothDevice = mDevicesMap.get(device.getAddress());
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