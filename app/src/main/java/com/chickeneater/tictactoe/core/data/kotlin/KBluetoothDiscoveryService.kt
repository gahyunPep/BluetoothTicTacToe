package com.chickeneater.tictactoe.core.data.kotlin

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class KBluetoothDiscoveryService {
    private val TAG = "BluetoothDiscovery"

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    init {
        if (bluetoothAdapter == null) {
            Log.w(TAG, "This device does not support bluetooth")
        }
    }

    fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }

    fun startDiscovery(context: Context, listener: BluetoothDiscoveryListener) {
        context.registerReceiver(BluetoothDiscoveryBroadcastReceiver(listener), discoveryIntentFilter())

        getPreviouslyPairedDevices(listener)
    }

    private fun getPreviouslyPairedDevices(listener: BluetoothDiscoveryListener) {
        bluetoothAdapter?.bondedDevices?.let {
            listener.onPairedDevicesReceived(it)
        }
    }

    private fun discoveryIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
    }
}

private class BluetoothDiscoveryBroadcastReceiver(val listener: BluetoothDiscoveryListener): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> listener.onDiscoveryStart()
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                listener.onDiscoveryFinished()
                context.unregisterReceiver(this)
            }
            BluetoothDevice.ACTION_FOUND -> {
                listener.onDeviceDiscovered(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))
            }
        }
    }

}

interface BluetoothDiscoveryListener {
    fun onDiscoveryStart()
    fun onDiscoveryFinished()
    fun onPairedDevicesReceived(pairedDevices: Set<BluetoothDevice>)
    fun onDeviceDiscovered(bluetoothDevice: BluetoothDevice)
}