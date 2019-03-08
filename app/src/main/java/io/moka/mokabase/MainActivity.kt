package io.moka.mokabase

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.moka.lib.adhelper.MokaAdCenter
import io.moka.lib.audiopicker.MokaAudioPicker
import io.moka.lib.audiopicker.MokaVolumePicker
import io.moka.lib.authentication.util.Contract
import io.moka.lib.base.ColorDI
import io.moka.lib.base.MokaBase
import io.moka.lib.base.util.color
import io.moka.lib.base.util.log.MLog
import io.moka.lib.base.util.onClick
import io.moka.lib.imagepicker.image.MokaImagePicker
import io.moka.lib.imagepicker.image.viewer.ImageViewerActivity
import io.moka.lib.imagepicker.util.ImageFileUtil
import io.moka.lib.imagepicker.util.LocationType
import io.moka.lib.imagepicker.util.SaveImageUtil
import io.moka.lib.permission.MokaPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val accountManager by lazy { AccountManager.get(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* */
        MokaBase.context = this

        ColorDI.primaryDarkColor = color(R.color.red_01)
        ColorDI.primaryColor = color(R.color.red_01)
        ImageFileUtil.init(this, "dayday")

        /* */
        MokaAdCenter.debuggable = MokaBase.DEBUG
        MokaAdCenter.initialize(this, "ca-app-pub-7847386025632674~4395347068")

        MokaAdCenter.AUDIENCE_TEST_CODE = "e0d43ce760c1b27fdddc26600a0c9649"
        MokaAdCenter.ADMOB_TEST_CODE = "B2E4DBF0638C3608DBC09D5FAE06DE10"

        /* */
        showPicker.setOnClickListener {
            MokaPermission
                    .with(this@MainActivity)
                    .check(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) { isGranted ->
                        MLog.deb("READ_EXTERNAL_STORAGE isGranted : ${isGranted}")

                        MokaImagePicker()
                                .setMaxImageCount(10)
                                .setDirectAlbum(true)
                                .setExistImage(false)
                                .setImageLocation(LocationType.EXTERNAL_NO_MEDIA)
                                .showDialog(supportFragmentManager) {

                                    MLog.deb("oh pathaa aa : ${it[0]}")

                                    Glide.with(this)
                                            .load(it[0])
                                            .into(imageView)

                                    Thread {
                                        val aa = SaveImageUtil.from(it, this@MainActivity)
                                                .setImageLocation(LocationType.EXTERNAL_NO_MEDIA)
                                                .performSync()

                                        aa.forEach { path ->
                                            MLog.deb("path aa : ${path}")
                                        }
                                    }.start()
                                }
                    }
        }

        Glide.with(this)
                .load("/storage/emulated/0/dayday/temps/1534411404-bb8636b6-6f8b-474f-9fb4-7f4f64e8656d.jpg")
                .into(imageView)


        textView.setOnClickListener {
            val list = arrayListOf(
                    "/storage/emulated/0/dayday/temps/1534413932-12cacd72-f155-4b55-a4f6-6d2fb3cdee2a.jpg",
                    "/storage/emulated/0/dayday/temps/1534413933-c934896d-582b-4f8b-a816-516bcbfbf70f.jpg")

            val intent = Intent(this@MainActivity, ImageViewerActivity::class.java)
            val imageUrlArray = ArrayList<String>()
            list.forEach {
                imageUrlArray.add(it)
            }
            intent.putExtra(ImageViewerActivity.IMAGE_URL_ARRAY, imageUrlArray)
            intent.putExtra(ImageViewerActivity.IMAGE_POSITION, 0)
            this@MainActivity.startActivity(intent)
        }

        /* */
        textView_signUp.onClick {
            accountManager.addAccount(Contract.ACCOUNT_TYPE, Contract.LABEL_ALARM, null, null, this, { future ->
                try {
                    val bundle = future.result
                    val token = bundle.getString("token")

                    Log.wtf("DayDay", "AddNewAccount Bundle is $bundle")
                    Log.wtf("DayDay", "token $token")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, null)
        }

        /* */
        textView_audioPicker.onClick {
            MokaAudioPicker()
                    .setAudioTitle(soundTitle)
                    .showDialog(supportFragmentManager) { _, soundTitle, soundUri ->

                        this@MainActivity.soundTitle = soundTitle
                        Toast.makeText(this@MainActivity, "${soundTitle} \n ${soundUri}", Toast.LENGTH_LONG).show()
                    }

            MokaVolumePicker()
                    .setVolume(volume)
                    .setTitle("testetst")
                    .showDialog(supportFragmentManager) { volume ->
                        this@MainActivity.volume = volume
                    }
        }
    }

    var soundTitle = ""
    var volume = 0f

}