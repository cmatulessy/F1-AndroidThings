package com.carlomatulessy.libsoftware

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
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

    fun getSelectedDevice() : BluetoothDevice? {
        var pairedDevices = getPairedDevices()

        for(pairedDevice in pairedDevices) {
            if(isSelectedDevice(pairedDevice.address)) {
                return pairedDevice
            }
        }

        return null
    }

    fun startDiscovery() : Boolean {
        registerReceiver()
        return bluetoothAdapter.startDiscovery()
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var action = intent.action

            // Discovery has found a device
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the bluetoothdevice object and the info from the intent
                var device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Timber.d("Discovery has found a device: %d/%s/%s", device.bondState, device.name, device.address)

                if(isSelectedDevice(device.address)) {
                    createBond(device)
                } else {
                    Timber.d("Unknown device, skipping bond attempt")
                }
            } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))  {
                val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                when(state) {
                    BluetoothDevice.BOND_NONE -> Timber.d("The remote device is not bonded")
                    BluetoothDevice.BOND_BONDING -> Timber.d("Bonding is in progress with the remote device")
                    BluetoothDevice.BOND_BONDED -> Timber.d("The remote device is bonded")
                    else -> Timber.d("Unknown remote device bonding state")
                }
            }
        }
    }

    // Register for broadcasts when a device is discovered
    fun registerReceiver() {
        var filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(receiver, filter)
    }

    fun cancelDiscovery() {
        bluetoothAdapter.cancelDiscovery()
        context.unregisterReceiver(receiver)
    }

    fun isSelectedDevice(foundAddress: String) : Boolean {
        // MAC address is set and recognized
        return !TextUtils.isEmpty(deviceAddress) && deviceAddress == foundAddress
    }

    // Pair with specific device
    fun createBond(device: BluetoothDevice) : Boolean {
        var result = device.createBond()
        Timber.d("Creating bond with: %s/%s/%b", device.name, device.address, result)
        return result
    }

    // TODO remove bond necessary?

}