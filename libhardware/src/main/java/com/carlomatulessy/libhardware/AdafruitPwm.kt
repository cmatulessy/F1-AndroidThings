package com.carlomatulessy.libhardware

/**
 *  Kotlin port from Adafruit_PWM_Servo_Driver
 *  <p>https://github.com/adafruit/Adafruit-Motor-HAT-Python-Library/blob/master/Adafruit_MotorHAT/Adafruit_PWM_Servo_Driver.py
 */
class AdafruitPwm {

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