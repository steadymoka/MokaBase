package io.moka.lib.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
            Manifest.permission.READ_EXTERNAL_STORAGE ->
                checkReadExternalStorageWithPermissionCheck()

            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                checkWriteExternalStorageWithPermissionCheck()

            Manifest.permission.CAMERA ->
                checkCameraWithPermissionCheck()

            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION ->
                checkLocationWithPermissionCheck()
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
     *
     * Needs Permission
     */

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun checkReadExternalStorage() {
        MokaPermission.callback?.invoke(true)

        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun checkWriteExternalStorage() {
        MokaPermission.callback?.invoke(true)

        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun checkCamera() {
        MokaPermission.callback?.invoke(true)

        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun checkLocation() {
        MokaPermission.callback?.invoke(true)

        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     *
     * Denied Permission
     */

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun deniedReadExternalStorage() {
        showDenied()
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun deniedWriteExternalStorage() {
        showDenied()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun deniedCamera() {
        showDenied()
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun deniedLocation() {
        showDenied()
    }

    private fun showDenied() {
        Toast.makeText(this, "권한이 없어요", Toast.LENGTH_SHORT).show()

        MokaPermission.callback?.invoke(false)

        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     *
     * Never Ask Permission
     */

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onNeverAskReadExternalStorage() {
        onNeverAskAgain()
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onNeverAskWriteExternalStorage() {
        onNeverAskAgain()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onNeverAskCamera() {
        onNeverAskAgain()
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onNeverAskLocation() {
        onNeverAskAgain()
    }

    private fun onNeverAskAgain() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("권한이 없습니다")
                .setMessage("[설정]>[권한]에서 권한 설정을 확인해주세요")
                .setCancelable(false)
                .setNegativeButton("취소") { _, _ ->
                    MokaPermission.callback?.invoke(false)

                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                .setPositiveButton("세팅으로") { _, _ ->
                    goAppSetting()

                    MokaPermission.callback?.invoke(false)

                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
