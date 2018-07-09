package io.moka.lib.util

import io.moka.lib.MokaBase.context
import io.moka.lib.base.R

fun dp2px(dip: Double): Double {
    return dip * context!!.resources.displayMetrics.densityDpi / 160.0
}

fun px2dp(pixel: Double): Double {
    return pixel * 160.0 / context!!.resources.displayMetrics.densityDpi
}

val statusBarSize: Int by lazy {
    val resourceId = context!!.resources.getIdentifier("status_bar_height", "dimen", "android")

    if (resourceId > 0)
        context!!.resources.getDimensionPixelSize(resourceId)
    else
        19
}

val toolBarSize: Int
    get() = context!!.resources.getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)

val deviceWidthPixel: Int by lazy { context!!.resources.displayMetrics.widthPixels }

val deviceHeightPixel: Int by lazy { context!!.resources.displayMetrics.heightPixels }
