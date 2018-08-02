package io.moka.lib.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat


@SuppressLint("StaticFieldLeak")
object MokaPermission {

    internal lateinit var activity: Activity
    internal var callback: ((isGranted: Boolean) -> Unit)? = null

    fun with(activity: Activity): MokaPermission {
        this.activity = activity
        return this
    }

    fun check(permission: String, callback: ((isGranted: Boolean) -> Unit)? = null) {
        if (isGranted(permission)) {
            callback?.invoke(true)
            return
        }

        this.callback = callback
        val intent = Intent(activity, PermissionActivity::class.java)

        intent.putExtra(PermissionActivity.KEY_PERMISSION, permission)
        activity.startActivity(intent)
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     */

    private fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     */

    private fun checkWriteExternal(activity: Activity, callback: ((isGranted: Boolean) -> Unit)? = null) {
        with(activity)
                .check(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    callback?.invoke(it)
                }
    }

}