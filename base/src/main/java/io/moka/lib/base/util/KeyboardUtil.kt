package io.moka.lib.base.util

import android.app.Activity
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import io.moka.lib.base.MokaBase.context

fun hideSoftKey(activity: Activity) {
    val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocus = activity.currentFocus
    if (null != currentFocus)
        inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun hideSoftKey(view: View, context: Context) {
    if (null == view.windowToken)
        return
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun showSoftKey(activity: FragmentActivity) {
    val mgr = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocus = activity.currentFocus
    if (null != currentFocus)
        mgr.showSoftInput(currentFocus, InputMethodManager.SHOW_FORCED)
}

fun showSoftKey(view: View) {
    if (null == context)
        return
    val mgr = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}
