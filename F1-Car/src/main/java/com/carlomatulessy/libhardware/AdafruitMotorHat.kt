package com.carlomatulessy.libhardware

class AdafruitMotorHat(deviceName: String, address: Int, debug: Boolean){

    companion object {
        const val MOTOR_FREQUENCY = 1600

        const val FORWARD = 1
        const val BACKWARD = 2
        const val BRAKE = 3
        const val RELEASE = 4

        const val SINGLE = 1
        const val DOUBLE = 2
        const val INTERLEAVE = 3
        const val MICROSTEP = 4
    }

    private var pwm: AdafruitPwm
    private lateinit var motors: ArrayList<AdafruitDcMotor>

    init {
        pwm = AdafruitPwm(deviceName, address, debug)
        pwm.setPwmFreq(MOTOR_FREQUENCY)
        motors = ArrayList(4)
        motors.add(AdafruitDcMotor(this, 0))
        motors.add(AdafruitDcMotor(this, 1))
        motors.add(AdafruitDcMotor(this, 2))
        motors.add(AdafruitDcMotor(this, 3))
    }

    // Get current PWM value
    fun getPwm() : AdafruitPwm { return pwm }

    fun setPin(pin: Int, value: Int) {
        if (pin < 0 || pin > 15) {
            throw RuntimeException("PWM pin must be between 0 and 15 inclusive")
        }
        if (value != 0 && value != 1) {
            throw RuntimeException("Pin value must be 0 or 1!")
        }
        if (value == 0) {
            pwm.setPwm(pin, 0, 4096)
        }
        if (value == 1) {
            pwm.setPwm(pin, 4096, 0)
        }
    }

    // Get specific motor
    fun getMotor(num: Int): AdafruitDcMotor {
        if (num < 1 || num > 4) {
            throw RuntimeException("MotorHAT Motor must be between 1 and 4 inclusive")
        }
        return motors.get(num)
    }

    fun close() { pwm.close() }

}