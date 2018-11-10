package com.chickeneater.tictactoe.core.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;


/**
 * Created by romanlee on 11/3/18.
 * To the power of Love
 */
public class BluetoothConnector {
    private static final String NAME = "AppNameWillBeHere";
    private static final UUID MY_UUID = UUID.randomUUID();
    private static final String TAG = BluetoothConnector.class.getName();
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
                mDiscoveredListener.onDeviceDiscovered(device);
            }
        }
    }

    public void stopDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    public void discoverDevices(Context context, BluetoothDiscoveryListener listener) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(new BluetoothDiscoveryBroadcastReceiver(listener), filter);

        mBluetoothAdapter.startDiscovery();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException ignored) {}
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }


    public interface BluetoothDiscoveryListener {
        void onDiscoveryStart();
        void onDiscoveryFinished();
        void onDeviceDiscovered(BluetoothDevice bluetoothDevice);
    }
}


//TODO use in the activity
/*
    private void bluetoothConnection() {
        mConnector = new BluetoothConnector(this, getLifecycle(), new BluetoothConnector.OnDeviceDiscoveredListener() {
            @Override
            public void onDeviceDiscovered(final BluetoothDevice device) {
                AppCompatButton button = new AppCompatButton(LobbyActivity.this);
                button.setText(device.getName() + " " + device.getAddress());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectToDevice(device);
                    }
                });
                String s = "";
            }
        });


        findViewById(R.id.discoverButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnector.discoverDevices();
            }
        });

        findViewById(R.id.discoverabilityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);

            }
        });

    }
    */
