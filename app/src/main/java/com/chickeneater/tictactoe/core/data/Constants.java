package com.chickeneater.tictactoe.core.data;

/**
 * Created by romanlee on 11/10/18.
 * To the power of Love
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_CONNECTION_FAILED = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String IS_HOST = "isHost";
    public static final String TOAST = "toast";

}

