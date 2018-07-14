package io.moka.mokabase

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.moka.lib.base.MokaBase
import io.moka.lib.base.util.log.MLog
import io.moka.lib.imagepicker.image.ImagePickerDialogFragment
import io.moka.lib.imagepicker.util.ImageFileUtil
import io.moka.lib.imagepicker.util.LocationType
import io.moka.lib.permission.MokaPermission
import kotlinx.android.synthetic.main.activity_main.*

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
                            .setExistImage(false)
                            .setCropable(true)
                            .setImageLocation(LocationType.EXTERNAL)
                            .setAspect(100F, 100F)
                            .setOnDeleted {

                            }
                            .showDialog(supportFragmentManager) {
                                Glide
                                        .with(this)
                                        .load(it[0])
                                        .into(imageView)
                            }
                }
    }

}