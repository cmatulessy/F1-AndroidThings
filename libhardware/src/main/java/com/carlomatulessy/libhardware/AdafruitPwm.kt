package com.carlomatulessy.libhardware

import android.util.Log
import com.google.android.things.pio.I2cDevice
import com.google.android.things.pio.PeripheralManager
import timber.log.Timber
import java.io.IOException

/**
 *  Kotlin port from Adafruit_PWM_Servo_Driver
 *  <p>https://github.com/adafruit/Adafruit-Motor-HAT-Python-Library/blob/master/Adafruit_MotorHAT/Adafruit_PWM_Servo_Driver.py
 */
class AdafruitPwm (deviceName: String, address: Int, debug: Boolean) {

    private lateinit var i2c: I2cDevice
    private lateinit var debug: Boolean

    init {
        try {
            Timber.d("Connecting to I2C device %s @ 0x%02X.", deviceName, address)
            i2c = PeripheralManager.getInstance().openI2cDevice(deviceName, address)
        } catch (e: IOException) {
            Timber.e(e, "Unable to access I2C device")
        }

        this.debug = debug
        reset()
    }

    private fun reset() {
        if(debug) {
            Timber.d("Resetting PCA9685 MODE1 (without SLEEP) and MODE2")
        }

        setAllPwm(0,0)
        writeRegByteWrapper(MODE2, OUTDRV.toByte())
        writeRegByteWrapper(MODE1, ALLCALL.toByte())
        sleepWrapped(0.005) // wait for oscillator

        var model = readRegByteWrapped(MODE1)
        model = (model SLEEP)

    }


    companion object {
        // Registers
        const val MODE1 = 0x00
        const val MODE2 = 0X01
        const val SUBADR1 = 0x02
        const val SUBADR2 = 0x03
        const val SUBADR3 = 0x04
        const val PRESCALE = 0xFE
        const val LED0_ON_L = 0x06
        const val LED0_ON_H = 0x07
        const val LED0_OFF_L = 0x08
        const val LED0_OFF_H = 0x09
        const val ALL_LED_ON_L = 0xFA
        const val ALL_LED_ON_H = 0xFB
        const val ALL_LED_OFF_L = 0xFC
        const val ALL_LED_OFF_H = 0xFD

        // Bits
        const val RESTART = 0x80
        const val SLEEP = 0x10
        const val ALLCALL = 0x01
        const val INVRT = 0x10
        const val OUTDRV = 0x04
    }

}