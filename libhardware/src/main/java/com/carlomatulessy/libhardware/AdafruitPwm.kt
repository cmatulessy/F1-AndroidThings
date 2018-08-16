package com.carlomatulessy.libhardware

import android.util.Log
import com.google.android.things.pio.I2cDevice
import com.google.android.things.pio.PeripheralManager
import timber.log.Timber
import java.io.IOException
import java.io.InterruptedIOException
import kotlin.experimental.and
import kotlin.experimental.or

/**
 *  Kotlin port from Adafruit_PWM_Servo_Driver
 *  <p>https://github.com/adafruit/Adafruit-Motor-HAT-Python-Library/blob/master/Adafruit_MotorHAT/Adafruit_PWM_Servo_Driver.py
 */
class AdafruitPwm (deviceName: String, address: Int, debug: Boolean) {

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
        writeRegByteWrapped(MODE2, OUTDRV.toByte())
        writeRegByteWrapped(MODE1, ALLCALL.toByte())
        sleepWrapped(0.005) // wait for oscillator

        var model = readRegByteWrapped(MODE1)
        model = (model and SLEEP.inv().toByte()) // wake up (reset sleep)
        writeRegByteWrapped(MODE1, model)
        sleepWrapped(0.005) // wait for oscillator
    }

    // Close the device
    fun close() {
        try{
            i2c.close() //TODO validate if this works
        } catch(ex: IOException) {
            Timber.w(ex, "Unable to close I2C device.")
        }
    }

    // Sets the PWM frequency
    fun setPwmFreq(freq: Int) {
        var prescaleval = 25000000.0f // 25MHz
        prescaleval /= 4096.0f // 12-bit
        prescaleval /= freq.toFloat()
        prescaleval -= 1.0f

        if(debug) {
            Timber.d("Setting PWM frequency to %d Hz", freq)
            Timber.d("Estimated pre-scale: %f", prescaleval)
        }

        var prescale = Math.floor(prescaleval + 0.5)

        if(debug) {
            Timber.d("Final pre-scale: %f", prescale)
        }

        val oldmode = readRegByteWrapped(MODE1)
        val newmode = (oldmode and 0x7F or 0x10) // sleep
        writeRegByteWrapped(MODE1, newmode) // go to sleep
        writeRegByteWrapped(PRESCALE, Math.floor(prescale).toByte())
        writeRegByteWrapped(MODE1, oldmode)
        sleepWrapped(0.005)
        writeRegByteWrapped(MODE1, (oldmode or 0x80.toByte()))
    }

    // Set single PWM channel
    fun setPwm(channel: Int, on: Int, off: Int) {
        writeRegByteWrapped(LED0_ON_L + 4 * channel, (on and 0xFF).toByte())
        writeRegByteWrapped(LED0_ON_H + 4 * channel, (on shr 8).toByte())
        writeRegByteWrapped(LED0_OFF_L + 4 * channel, (off and 0xFF).toByte())
        writeRegByteWrapped(LED0_OFF_H + 4 * channel, (off shr 8).toByte())
    }

    // Set all PWM channels
    private fun setAllPwm(on: Int, off: Int) {
        writeRegByteWrapped(ALL_LED_ON_L, (on and 0xFF).toByte())
        writeRegByteWrapped(ALL_LED_ON_H, (on shr 8).toByte())
        writeRegByteWrapped(ALL_LED_OFF_L, (off and 0xFF).toByte())
        writeRegByteWrapped(ALL_LED_OFF_H, (off shr 8).toByte())
    }

    private fun sleepWrapped(seconds: Double) {
        try {
            Thread.sleep((seconds * 1000).toLong())
        } catch (ex: InterruptedException) {
            Timber.e("AdafruitPwm -> sleepWrapped failed.")
        }
    }

    private fun writeRegByteWrapped(reg: Int, data: Byte) {
        try {
            i2c.writeRegByte(reg, data)
        } catch (ex: IOException) {
            Timber.e(ex, "writeRegByte to 0x%02X failed.", reg)
            return
        }

        if (debug) {
            Timber.d("Wrote to register 0x%02X: 0x%02X", reg, data)
        }
    }

    private fun readRegByteWrapped(reg: Int) : Byte {
        var data: Byte = 0

        try {
            data = i2c.readRegByte(reg)
        } catch (e: IOException) {
            Timber.d(e, "readRegByte from 0x%02X failed.", reg)
        }


        if (debug) {
            Timber.d("Read from register 0x%02X: 0x%02X", reg, data)
        }

        return data
    }

}