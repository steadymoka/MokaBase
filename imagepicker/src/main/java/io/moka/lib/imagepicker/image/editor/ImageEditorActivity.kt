package io.moka.lib.imagepicker.image.editor


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.moka.lib.imagepicker.R


class ImageEditorActivity : AppCompatActivity() {

    private var imageCropFragment: ImageCropFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_editor)

        if (null == imageCropFragment)
            imageCropFragment = ImageCropFragment()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_container, imageCropFragment!!)
                .commit()
    }

    companion object {

        const val SAVED_IMAGE_PATHS = "ImageEditorActivity.SAVED_IMAGE_PATHS"
        const val IMAGE_PATHES = "ImageEditorActivity.IMAGE_PATHES"
        const val ASPECT_X = "aspectX"
        const val ASPECT_Y = "aspectY"
        const val IMAGE_LOCATION = "image_location"
    }

}
