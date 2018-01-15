package io.moka.mokabaselib

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object MokaBase {

    var context: Context? = null

    var debuggable: Boolean = true

    var fontSizeFlag: Int = 0

}