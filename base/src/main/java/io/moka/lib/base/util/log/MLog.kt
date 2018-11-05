package io.moka.lib.base.util.log

import android.util.Log
import io.moka.lib.base.MokaBase.debuggable

object MLog {

    fun e(a: String, message: String) {
        if (debuggable)
            Log.e(a, message)
    }

    fun i(a: String, message: String) {
        if (debuggable)
            Log.i(a, message)
    }

    fun wtf(a: String, message: String) {
        if (debuggable)
            Log.wtf(a, message)
    }

    fun d(a: String, message: String) {
        if (debuggable)
            Log.d(a, message)
    }

    fun a(a: String, message: String) {
        if (debuggable)
            Log.println(Log.ASSERT, a, message)
    }

    @JvmStatic
    fun deb(message: String) {
        if (debuggable)
            Log.wtf("DayDay debugging..", message)
    }

    @JvmStatic
    fun printCurrentThread() {
        if (debuggable)
            Log.wtf("DayDay debugging.. ", "Current Thread :: ${Thread.currentThread().name}")
    }

    @JvmStatic
    fun foc(message: String) {
        Log.e("DayDay debugging..", message)
    }

}