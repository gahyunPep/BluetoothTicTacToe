package com.chickeneater.tictactoe.core.data;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * This thread runs while attempting to make an outgoing connection
 * with a device. It runs straight through; the connection either
 * succeeds or fails.
 */
class ConnectingThread extends Thread {
    private static final String TAG = ConnectingThread.class.getName();

    private final TickTackBluetoothService mService;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public ConnectingThread(TickTackBluetoothService service, BluetoothDevice device) {
        mService = service;
        mmDevice = device;
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {

            tmp = device.createRfcommSocketToServiceRecord(
                    TickTackBluetoothService.MY_UUID_SECURE);

        } catch (IOException e) {
            Log.e(TAG, "create() failed", e);
        }
        mmSocket = tmp;
        mService.setState(TickTackBluetoothService.STATE_CONNECTING);
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectThread");
        setName("ConnectThread");

        // Always cancel discovery because it will slow down a connection
        if (mService.mAdapter != null) {
            mService.mAdapter.cancelDiscovery();
        }

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            mmSocket.connect();
        } catch (IOException e) {
            // Close the socket
            try {
                mmSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "unable to close() socket during connection failure", e2);
            }
            mService.connectionFailed();
            return;
        }

        // Reset the ConnectThread because we're done
        synchronized (mService) {
            mService.resetConnectThread();
        }

        // Start the connected thread
        mService.connected(mmSocket, mmDevice, false);
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}
