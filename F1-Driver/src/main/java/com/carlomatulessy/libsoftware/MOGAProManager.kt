package com.carlomatulessy.libsoftware

import android.view.KeyEvent
import java.util.*

class MOGAProManager() : KeyEvent.Callback {

    // Controller keycodes
    companion object {
        const val BUTTON_LEFT_CODE = KeyEvent.KEYCODE_DPAD_LEFT
        const val BUTTON_RIGHT_CODE = KeyEvent.KEYCODE_DPAD_RIGHT
        const val BUTTON_UP_CODE = KeyEvent.KEYCODE_DPAD_UP
        const val BUTTON_DOWN_CODE = KeyEvent.KEYCODE_DPAD_DOWN

        const val BUTTON_SELECT_CODE = KeyEvent.KEYCODE_BUTTON_SELECT
        const val BUTTON_START_CODE = KeyEvent.KEYCODE_BUTTON_START

        const val BUTTON_A_CODE = KeyEvent.KEYCODE_BUTTON_A
        const val BUTTON_B_CODE = KeyEvent.KEYCODE_BUTTON_B
        const val BUTTON_X_CODE = KeyEvent.KEYCODE_BUTTON_X
        const val BUTTON_Y_CODE = KeyEvent.KEYCODE_BUTTON_Y

        const val BUTTON_L1_CODE = KeyEvent.KEYCODE_BUTTON_L1
        const val BUTTON_L2_CODE = KeyEvent.KEYCODE_BUTTON_L2
        const val BUTTON_R1_CODE = KeyEvent.KEYCODE_BUTTON_R1
        const val BUTTON_R2_CODE = KeyEvent.KEYCODE_BUTTON_R2
    }

    // TODO fix null check
    private var history: Deque<Int>
    lateinit var listener: MOGAProListener

    init {
        history = ArrayDeque<Int>()
    }

    constructor(listener: MOGAProListener) : this() {
        this.listener = listener
    }

    override fun onKeyMultiple(keyCode: Int, count: Int, event: KeyEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        addKeyToHistory(keyCode)

        if (listener != null) {
            listener.onKeyPress(keyCode, true)
            return true
        }

        return false;
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if(listener != null) {
            listener.onKeyPress(keyCode, false)
            return true
        }

        return false
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun addKeyToHistory(keyCode: Int) {
        history.add(keyCode)

        if(history.size > 10) {
            history.pop()
        }
    }
}