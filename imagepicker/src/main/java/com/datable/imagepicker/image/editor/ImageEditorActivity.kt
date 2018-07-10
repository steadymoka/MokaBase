package com.datable.imagepicker.image.editor


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.datable.imagepicker.R


class ImageEditorActivity : AppCompatActivity() {

    private var imageCropFragment: ImageCropFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_editor)

        if (null == imageCropFragment)
            imageCropFragment = ImageCropFragment()

        supportFragmentManager.beginTransaction().replace(R.id.frameLayout_container, imageCropFragment).commit()
    }

    companion object {

        val SAVED_IMAGE_NAMES = "ImageEditorActivity.SAVED_IMAGE_NAMES"
        val IMAGE_PATHES = "ImageEditorActivity.IMAGE_PATHES"
        val ASPECT_X = "aspectX"
        val ASPECT_Y = "aspectY"
        val IMAGE_LOCATION = "image_location"
    }

}
