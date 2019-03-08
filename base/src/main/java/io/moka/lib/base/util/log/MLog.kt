package io.moka.lib.base.util.log

import android.util.Log
import io.moka.lib.base.MokaBase.DEBUG

object MLog {

    fun e(a: String, message: String) {
        if (DEBUG)
            Log.e(a, message)
    }

    fun i(a: String, message: String) {
        if (DEBUG)
            Log.i(a, message)
    }

    fun wtf(a: String, message: String) {
        if (DEBUG)
            Log.wtf(a, message)
    }

    fun d(a: String, message: String) {
        if (DEBUG)
            Log.d(a, message)
    }

    fun a(a: String, message: String) {
        if (DEBUG)
            Log.println(Log.ASSERT, a, message)
    }

    @JvmStatic
    fun deb(message: String) {
        if (DEBUG)
            Log.wtf("DayDay debugging..", message)
    }

    @JvmStatic
    fun printCurrentThread() {
        if (DEBUG)
            Log.wtf("DayDay debugging.. ", "Current Thread :: ${Thread.currentThread().name}")
    }

    @JvmStatic
    fun foc(message: String) {
        Log.e("DayDay debugging..", message)
    }

}