package com.carlomatulessy.libsoftware

interface MOGAProListener {
    abstract fun onKeyPress(@MOGAProManager.ButtonCode keyCode: Int, isDown: Boolean)
}