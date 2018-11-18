package com.chickeneater.tictactoe.core.data;

public interface OnBluetoothConnectionServiceListener {
    void onConnectedTo(String deviceName, boolean asHost);
    void onConnectionFailed();
}
