package com.chickeneater.tictactoe.core.data;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 */
class ConnectedThread extends Thread {
    private static final String TAG = ConnectedThread.class.getName();

    private final TickTackBluetoothService mService;
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(TickTackBluetoothService service, BluetoothSocket socket) {
        mService = service;
        Log.d(TAG, "create ConnectedThread: ");
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        mService.setState(TickTackBluetoothService.STATE_CONNECTED);
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");
        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (mService.getState() == TickTackBluetoothService.STATE_CONNECTED) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);

                // Send the obtained bytes to the UI Activity

                mService.mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
                mService.connectionLost();
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer) {
        try {
            mmOutStream.write(buffer);

            // Share the sent message back to the UI Activity
            /*
            mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                    .sendToTarget();
                    */
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}
