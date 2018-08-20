package com.carlomatulessy.libsoftware

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import timber.log.Timber

/**
 *  Manages the bluetooth connection to the MOGA Pro controller
 */

class MOGAProConnection(context: Context, deviceAddress: String) {

    private var context: Context
    private var deviceAddress: String

    private var bluetoothAdapter: BluetoothAdapter

    init {
        this.context = context
        this.deviceAddress = deviceAddress
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Validation if bluetooth is enabled and supported on device
        if (bluetoothAdapter == null) {
            Timber.e("This device does not support Bluetooth")
        } else if(!bluetoothAdapter.isEnabled) {
            Timber.d("Bluetooth isn't enabled, enabling: %b", bluetoothAdapter.enable())
        }
    }

    fun getPairedDevices() : Set<BluetoothDevice> { return bluetoothAdapter.bondedDevices }

    fun getSelectedDevice() : BluetoothDevice {
        var pairedDevices = getPairedDevices()

        for(pairedDevice in pairedDevices) {
            if(isSelectedDevice(pairedDevice.address)) {
                return pairedDevice
            }
        }
    }

    fun startDiscovery() : Boolean {
        registerReceiver()
        return bluetoothAdapter.startDiscovery()
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private val receiver = BroadcastReceiver() {

    }

}