package io.moka.lib.base

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object MokaBase {

    var context: Context? = null

    var DEBUG: Boolean = true

}