package io.moka.mokabase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.moka.lib.base.MokaBase
import io.moka.lib.base.util.log.MLog
import io.moka.lib.imagepicker.image.ImagePickerDialogFragment
import io.moka.lib.imagepicker.util.ImageFileUtil
import io.moka.lib.imagepicker.util.LocationType
import io.moka.lib.permission.MokaPermission

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MokaBase.context = this
        ImageFileUtil.init(this, "dayday")

        MokaPermission
                .with(this)
                .check(android.Manifest.permission.READ_EXTERNAL_STORAGE) { isGranted ->
                    MLog.deb("READ_EXTERNAL_STORAGE isGranted : ${isGranted}")

                    ImagePickerDialogFragment()
                            .setMaxImageCount(10)
                            .setDirectAlbum(true)
                            .setExistImage(false)
                            .setImageLocation(LocationType.EXTERNAL_NO_MEDIA)
                            .showDialog(supportFragmentManager) {
                                it
                            }
                }
    }

}