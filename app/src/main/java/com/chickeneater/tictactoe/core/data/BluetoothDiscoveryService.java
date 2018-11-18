package com.chickeneater.tictactoe.core.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.chickeneater.tictactoe.lobby.DeviceInList;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by romanlee on 11/3/18.
 * To the power of Love
 */
public class BluetoothDiscoveryService {
    @Nullable
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private class BluetoothDiscoveryBroadcastReceiver extends BroadcastReceiver {
        @NonNull
        private BluetoothDiscoveryListener mDiscoveredListener;

        public BluetoothDiscoveryBroadcastReceiver(@NonNull BluetoothDiscoveryListener discoveredListener) {
            mDiscoveredListener = discoveredListener;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts
                mDiscoveredListener.onDiscoveryStart();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes
                mDiscoveredListener.onDiscoveryFinished();
                context.unregisterReceiver(this);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDiscoveredListener.onDeviceDiscovered(new DeviceInList(device.getName(), device.getAddress()));
            }
        }
    }

    public void stopDiscovery() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    public void discoverDevices(Context context, @NonNull  BluetoothDiscoveryListener listener) {
        if (mBluetoothAdapter != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            context.registerReceiver(new BluetoothDiscoveryBroadcastReceiver(listener), filter);


            Set<DeviceInList> pairedDevices = new HashSet<>();
            for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
                pairedDevices.add(new DeviceInList(device.getName(), device.getAddress()));
            }
            listener.onPreviouslyPairedDevicesObtained(pairedDevices);


            mBluetoothAdapter.startDiscovery();
        } else {
            fakeDevices(listener);
        }
    }

    private void fakeDevices(@NonNull BluetoothDiscoveryListener listener) {
        final BluetoothDiscoveryListener listenerFinal = listener;
        listener.onDiscoveryStart();
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                } catch (InterruptedException ignored) {}
                //Mock data TODO replace with an error
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listenerFinal.onDeviceDiscovered(new DeviceInList.FakeDevice());
                        listenerFinal.onDeviceDiscovered(new DeviceInList.FakeDevice());
                        listenerFinal.onDeviceDiscovered(new DeviceInList.FakeDevice());
                        listenerFinal.onDeviceDiscovered(new DeviceInList.FakeDevice());
                        listenerFinal.onDeviceDiscovered(new DeviceInList.FakeDevice());
                        listenerFinal.onDiscoveryFinished();
                    }
                });
            }
        }).start();
    }


    public interface BluetoothDiscoveryListener {
        void onDiscoveryStart();
        void onDiscoveryFinished();
        void onPreviouslyPairedDevicesObtained(Set<DeviceInList> pairedDevices);
        void onDeviceDiscovered(DeviceInList bluetoothDevice);
    }
}