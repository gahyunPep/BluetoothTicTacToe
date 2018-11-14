package com.chickeneater.tictactoe.core.data;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * This thread runs while listening for incoming connections. It behaves
 * like a server-side client. It runs until a connection is accepted
 * (or until cancelled).
 */
class AcceptThread extends Thread {
    private static final String TAG = AcceptThread.class.getName();

    private final TickTackBluetoothService mService;
    // The local server socket
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread(TickTackBluetoothService service) {
        mService = service;
        BluetoothServerSocket tmp = null;

        // Create a new listening server socket
        try {

            tmp = service.mAdapter.listenUsingRfcommWithServiceRecord(TickTackBluetoothService.NAME_SECURE,
                    TickTackBluetoothService.MY_UUID_SECURE);

        } catch (IOException e) {
            Log.e(TAG, "Socket Type: listen() failed", e);
        }
        mmServerSocket = tmp;
        mService.setState(TickTackBluetoothService.STATE_LISTEN);
    }

    public void run() {
        Log.d(TAG, "Socket Type: "+
                "BEGIN mAcceptThread" + this);
        setName("AcceptThread");

        BluetoothSocket socket = null;

        // Listen to the server socket if we're not connected
        while (mService.getState() != TickTackBluetoothService.STATE_CONNECTED) {
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: accept() failed", e);
                break;
            }

            // If a connection was accepted
            if (socket != null) {
                synchronized (mService) {
                    switch (mService.getState()) {
                        case TickTackBluetoothService.STATE_LISTEN:
                        case TickTackBluetoothService.STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            mService.connected(socket, socket.getRemoteDevice());
                            break;
                        case TickTackBluetoothService.STATE_NONE:
                        case TickTackBluetoothService.STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                    }
                }
            }
        }
        Log.i(TAG, "END mAcceptThread ");

    }

    public void cancel() {
        Log.d(TAG, "cancel " + this);
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of server failed", e);
        }
    }
}
