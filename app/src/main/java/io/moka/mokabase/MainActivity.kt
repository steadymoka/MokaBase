package io.moka.mokabase

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.moka.lib.MokaBase
import io.moka.lib.imagepicker.image.ImagePickerDialogFragment
import io.moka.lib.permission.MokaPermission
import io.moka.lib.util.log.MLog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MokaBase.context = this

        MokaPermission
                .with(this)
                .check(android.Manifest.permission.READ_EXTERNAL_STORAGE) { isGranted ->
                    MLog.deb("READ_EXTERNAL_STORAGE isGranted : ${isGranted}")

                    ImagePickerDialogFragment()
                            .setExistImage(false)
                            .setOnNeedCameraPermission {

                                MokaPermission
                                        .with(this)
                                        .check(android.Manifest.permission.CAMERA) { isGranted -> if (isGranted) it() }
                            }
                            .setOnDeleted {

                            }
                            .showDialog(supportFragmentManager) {

                            }
                }
    }

}