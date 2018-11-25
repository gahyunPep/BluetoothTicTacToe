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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by romanlee on 11/4/18.
 * To the power of Love
 */
public class TickTackBluetoothService implements OnBluetoothConnectionServiceListener, OnMessageReceivedListener {

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

    @Nullable
    private AcceptThread mSecureAcceptThread;
    @Nullable
    private ConnectThread mConnectThread;
    @Nullable
    private ConnectedThread mConnectedThread;

    @Nullable
    final BluetoothAdapter mAdapter;

    @NonNull
    private Set<OnBluetoothConnectionServiceListener> mConnectionServiceListeners = new HashSet<>();
    @NonNull
    private Set<OnMessageReceivedListener> mOnMessageReceivedListeners = new HashSet<>();

    // Constants that indicate the current connection state
    static final int STATE_NONE = 0;       // we're doing nothing
    static final int STATE_LISTEN = 1;     // now listening for incoming connections
    static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    final Handler mHandler = new BluetoothMessageHandler(this, this);

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
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param address The BluetoothDevice to connect
     */
    public synchronized void connect(String address) {
        if (mAdapter == null) {
            return;
        }
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
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, boolean isHost) {
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

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(this, socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity

        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        bundle.putBoolean(Constants.IS_HOST, isHost);

        sendMessage(Constants.MESSAGE_DEVICE_NAME, bundle);
    }

    private void sendMessage(int what, @Nullable Bundle bundle) {
        Message msg = mHandler.obtainMessage(what);
        if (bundle != null) {
            msg.setData(bundle);
        }
        mHandler.sendMessage(msg);
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
        mState = STATE_NONE;
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
        if (r != null) {
            r.write(out);
        }
    }

    public void write(String command) {
        if (getState() != STATE_CONNECTED) {
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
        sendMessage(Constants.MESSAGE_CONNECTION_FAILED, null);
        mState = STATE_NONE;
        // Start the service over to restart listening mode
        start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    void connectionLost() {
        sendMessage(Constants.MESSAGE_CONNECTION_LOST, null);
        mState = STATE_NONE;
        // Start the service over to restart listening mode
        start();
    }


    @Override
    public void onConnectedTo(String deviceName, boolean asHost) {
        for (OnBluetoothConnectionServiceListener listener : mConnectionServiceListeners) {
            listener.onConnectedTo(deviceName, asHost);
        }
    }

    @Override
    public void onConnectionFailed() {
        for (OnBluetoothConnectionServiceListener listener : mConnectionServiceListeners) {
            listener.onConnectionFailed();
        }
    }

    @Override
    public void onMessageReceived(String message) {
        for (OnMessageReceivedListener listener : mOnMessageReceivedListeners) {
            listener.onMessageReceived(message);
        }
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

