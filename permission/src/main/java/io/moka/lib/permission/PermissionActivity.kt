package io.moka.lib.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
class PermissionActivity : AppCompatActivity() {

    companion object {
        const val KEY_PERMISSION = "PermissionActivity.KEY_PERMISSION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permission = intent.getStringExtra(KEY_PERMISSION)

        when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE -> checkExternalStorageWithPermissionCheck()
            Manifest.permission.CAMERA -> checkCameraWithPermissionCheck()
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        super.onDestroy()
    }

    /**
     */

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun checkExternalStorage() {
        MokaPermission.callback?.invoke(true)
        onBackPressed()
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun checkCamera() {
        MokaPermission.callback?.invoke(true)
        onBackPressed()
    }

    /**
     */

    @OnPermissionDenied(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
    fun showDenied() {
        Toast.makeText(this, "권한이 없어요", Toast.LENGTH_SHORT).show()

        onBackPressed()
        MokaPermission.callback?.invoke(false)
    }

    @OnNeverAskAgain(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
    fun onNeverAskAgain() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("권한이 없습니다")
                .setMessage("[설정]>[권한]에서 권한 설정을 확인해주세요")
                .setCancelable(false)
                .setNegativeButton("취소") { _, _ ->
                    onBackPressed()
                    MokaPermission.callback?.invoke(false)
                }
                .setPositiveButton("세팅으로") { _, _ ->
                    goAppSetting()

                    onBackPressed()
                    MokaPermission.callback?.invoke(false)
                }
        builder.create().show()
    }

    private fun goAppSetting() {
        try {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + MokaPermission.activity.packageName)))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            startActivity(Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS))
        }
    }

}
