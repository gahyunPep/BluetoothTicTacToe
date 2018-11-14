package com.chickeneater.tictactoe.core.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by romanlee on 11/4/18.
 * To the power of Love
 */
public class TickTackBluetoothService {

    public interface OnBluetoothConnectionServiceListener {
        void onConnectedTo(String deviceName);
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }


    private static  TickTackBluetoothService sInstance;

    public static TickTackBluetoothService getInstance() {
        if (sInstance == null) {
            sInstance = new TickTackBluetoothService();
        }

        return sInstance;
    }

    static final String NAME_SECURE = "AppNameWillBeHere";
    static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private static final String TAG = TickTackBluetoothService.class.getName();

    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    final BluetoothAdapter mAdapter;

    private Set<OnBluetoothConnectionServiceListener> mConnectionServiceListeners = new HashSet<>();
    private Set<OnMessageReceivedListener> mOnMessageReceivedListeners = new HashSet<>();

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
     final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    for (OnBluetoothConnectionServiceListener listener : mConnectionServiceListeners) {
                        listener.onConnectedTo(deviceName);
                    }

                    break;

                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String s = "";
                    break;
            }
        }
    };

    private int mState;

    private TickTackBluetoothService() {
        mState = STATE_NONE;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    synchronized void setState(int state) {
        mState = state;
    }

    void resetConnectThread() {
        mConnectThread = null;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(this);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(this);
            mInsecureAcceptThread.start();
        }
        // Update UI title
        //updateUserInterfaceTitle();
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param address The BluetoothDevice to connect
     */
    public synchronized void connect(String address) {
        BluetoothDevice device = mAdapter.getRemoteDevice(address);

        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(this, device);
        mConnectThread.start();
        // Update UI title
        //updateUserInterfaceTitle();
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(this, socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity

        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Update UI title
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        mState = STATE_NONE;
        // Update UI title
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    public void write(String command) {
        if (getState() != STATE_CONNECTED) {
            //TODO Show Error
            //Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (command.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = command.getBytes();
            write(send);
        }

    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    void connectionFailed() {
        // Send a failure message back to the Activity
        /*
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        */

        mState = STATE_NONE;
        // Update UI title
        //updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        //BluetoothChatService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    void connectionLost() {
        // Send a failure message back to the Activity
        /*
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        */

        mState = STATE_NONE;
        // Update UI title
        //updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        start();
    }


    public void addConnectionListener(OnBluetoothConnectionServiceListener listener) {
        mConnectionServiceListeners.add(listener);
    }

    public void removeConnectionListener(OnBluetoothConnectionServiceListener listener) {
        mConnectionServiceListeners.remove(listener);
    }

    public void addMessageReceivedListener(OnMessageReceivedListener listener) {
        mOnMessageReceivedListeners.add(listener);
    }

    public void removeMessageReceivedListener(OnMessageReceivedListener listener) {
        mOnMessageReceivedListeners.remove(listener);
    }
}