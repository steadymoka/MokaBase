package io.moka.mokabaselib.handler

import android.app.Activity
import android.widget.Toast

class BackPressedHandler constructor(private val activity: Activity) {

    private var intervalTime: Long = 2000
        set(intervalTime) {
            if (0 < intervalTime)
                field = intervalTime
        }

    private var backPressedTime: Long = 0

    private var toast: Toast? = null
    private var message: String? = null

    init {
        this.backPressedTime = 0

        setCustomToast(null)
        setMessage(DEFAULT_MESSAGE)
    }

    fun setMessage(messageResId: Int) {
        setMessage(activity.resources.getString(messageResId))
    }

    fun setMessage(message: String?) {
        if (null != message)
            this.message = message
    }

    fun getMessage(): String? {
        return message
    }

    fun setCustomToast(toast: Toast?) {
        this.toast = toast
    }

    fun getToast(): Toast {
        return if (null == toast)
            Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        else
            toast!!
    }

    fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (0 <= intervalTime && this.intervalTime >= intervalTime) {
            activity.finish()
        }
        else {
            backPressedTime = tempTime
            getToast().show()
        }
    }

    companion object {
        private val DEFAULT_MESSAGE = ""
    }

}
