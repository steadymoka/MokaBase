package io.moka.mokabaselib.util.log

import android.util.Log
import io.moka.mokabaselib.MokaBase.debuggable

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
            Log.wtf("debugging..", message)
    }

    @JvmStatic
    fun printCurrentThread() {
        if (debuggable)
            Log.wtf("debugging.. ", "Current Thread :: ${Thread.currentThread().name}")
    }

    @JvmStatic
    fun foc(message: String) {
        Log.e("debugging..", message)
    }

}