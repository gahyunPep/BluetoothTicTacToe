package com.chickeneater.tictactoe.core.data;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class BluetoothMessageHandler extends Handler {
    @NonNull
    private OnBluetoothConnectionServiceListener mConnectionServiceListener;
    @NonNull
    private OnMessageReceivedListener mOnMessageReceivedListener;

    BluetoothMessageHandler(@NonNull OnBluetoothConnectionServiceListener connectionServiceListener,
                            @NonNull OnMessageReceivedListener onMessageReceivedListener) {
        mConnectionServiceListener = connectionServiceListener;
        mOnMessageReceivedListener = onMessageReceivedListener;
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                boolean isHost = msg.getData().getBoolean(Constants.IS_HOST);
                mConnectionServiceListener.onConnectedTo(deviceName, isHost);

                break;
            case Constants.MESSAGE_CONNECTION_FAILED:
                mConnectionServiceListener.onConnectionFailed();
                break;

            case Constants.MESSAGE_CONNECTION_LOST:
                mConnectionServiceListener.onConnectionFailed();
                break;

            case Constants.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                mOnMessageReceivedListener.onMessageReceived(readMessage);
                break;
        }
    }

}
