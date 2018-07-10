package com.datable.imagepicker.util

import android.content.Context
import android.util.DisplayMetrics

private var displayMetrics: DisplayMetrics? = null

private fun getDisplayMetrics(context: Context): DisplayMetrics {
    if (null == displayMetrics) {

        try {
            displayMetrics = context.resources.displayMetrics
        } catch (e: NullPointerException) {
        }
    }
    return displayMetrics!!
}

fun getWidthPixels(context: Context): Int {
    return getDisplayMetrics(context).widthPixels
}

fun getHeightPixels(context: Context): Int {
    return getDisplayMetrics(context).heightPixels
}