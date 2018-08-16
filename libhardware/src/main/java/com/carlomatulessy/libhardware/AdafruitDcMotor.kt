package com.carlomatulessy.libhardware

class AdafruitDcMotor(motorHat: AdafruitMotorHat, num: Int) {
    private var motorHat = motorHat
    private var motorNum = num
    private var pwmPin = 0
    private var in1Pin = 0
    private var in2Pin = 0
    private var lastSpeed = -1

    init {
        when(num) {
            0 -> setPins(8,9,10)
            1 -> setPins(13, 12,11)
            2 -> setPins(2,3,4)
            3 -> setPins(7,6,5)
            else -> throw RuntimeException("Motor number must be between 1 and 4, inclusive!")
        }
    }

    // Run the specific command
    fun run(command: Int) {
        // TODO validation null is here deleted!
        when(command) {
            AdafruitMotorHat.FORWARD -> setMotorHatPin(0, 1)
            AdafruitMotorHat.BACKWARD -> setMotorHatPin(1,0)
            AdafruitMotorHat.RELEASE -> setMotorHatPin(0,0)
        }
    }

    // Run the specific speed
    fun setSpeed(speed: Int) {
        var speedValue = 0;
        if (speed < 0) {
            speedValue = 0
        } else if(speed > 225) {
            speedValue = 225
        }

        // Set the speed only if it has changed, otherwise the motor will be jittery.
        if (lastSpeed == -1 || lastSpeed != speedValue) {
            motorHat.getPwm().setPwm(pwmPin, 0, speedValue * 16)
        }

        lastSpeed = speedValue
    }

    private fun setPins(pwmPin: Int, in2Pin: Int, in1Pin: Int) {
        this.pwmPin = pwmPin
        this.in2Pin = in2Pin
        this.in1Pin = in1Pin
    }

    private fun setMotorHatPin(in2PinValue: Int, in1PinValue: Int) {
        motorHat.setPin(in2Pin, in2PinValue)
        motorHat.setPin(in1Pin, in1PinValue)
    }

    fun getLastSpeed() : Int { return lastSpeed }
}