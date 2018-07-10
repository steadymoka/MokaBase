package com.datable.imagepicker.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList


class SaveImageUtil : Thread {

    private var selectedImagePaths: ArrayList<String>? = null
    private var imageBitmap: Bitmap? = null

    private var context: Context? = null
    private var onSaveImageListener: ((ArrayList<String>) -> Unit)? = null

    private var flag: Boolean = false
    private var imageLocation: ImageLocation = ImageLocation.EXTERNAL

    private val imageNames = ArrayList<String>()

    private constructor(selectedImagePaths: ArrayList<String>? = ArrayList(), context: Context) {
        this.selectedImagePaths = selectedImagePaths
        this.context = context
        this.flag = true
    }

    private constructor(imageBitmap: Bitmap, context: Context) {
        this.imageBitmap = imageBitmap
        this.context = context
        this.flag = false
    }

    fun start(onSaveImageListener: ((ArrayList<String>) -> Unit)?) {
        super.start()
        this.onSaveImageListener = onSaveImageListener
    }

    fun performSync(): ArrayList<String> {
        run()
        return imageNames
    }

    override fun run() {
        super.run()

        if (flag) {

            for (i in selectedImagePaths!!.indices) {

                val option = BitmapFactory.Options()
                val imageBitmap = BitmapFactory.decodeFile(selectedImagePaths!![i], option)

                val filePath = ImageFileUtil.from(context!!).getParentPathToSaveImage(imageLocation)
                val imageName: String = if (imageLocation == ImageLocation.EXTERNAL)
                    ImageFileUtil.generateFileNameToShow()
                else
                    ImageFileUtil.generateFileName()

                val selectedImageFile = storeEditedImage(imageBitmap, filePath, imageName)
                imageNames.add(selectedImageFile.name)

                val uri = Uri.fromFile(selectedImageFile)
                context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            }

            launch(UI) { onSaveImageListener?.invoke(imageNames) }
        }
        else {

            val filePath = ImageFileUtil.from(context!!).getParentPathToSaveImage(imageLocation)
            val imageName: String = if (imageLocation == ImageLocation.EXTERNAL)
                ImageFileUtil.generateFileNameToShow()
            else
                ImageFileUtil.generateFileName()

            val fileToSave = storeEditedImage(imageBitmap!!, filePath, imageName)
            imageNames.add(fileToSave.name)

            val uri = Uri.fromFile(fileToSave)
            context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

            launch(UI) { onSaveImageListener?.invoke(imageNames) }
        }
    }

    private fun storeEditedImage(bitmap: Bitmap, filePath: String, fileName: String): File {
        val directory = File(filePath)

        if (!directory.exists())
            directory.mkdirs()

        val file = File(directory, fileName)
        val out: BufferedOutputStream

        try {

            directory.createNewFile()
            out = BufferedOutputStream(FileOutputStream(file))

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            bitmap.recycle()

            out.flush()
            out.close()
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }
        return file
    }

    fun setImageLocation(imageLocation: ImageLocation = ImageLocation.INNER): SaveImageUtil {
        this.imageLocation = imageLocation
        return this
    }

    /**
     */

    companion object {

        fun from(selectedImagePaths: ArrayList<String>, context: Context): SaveImageUtil {
            return SaveImageUtil(selectedImagePaths, context)
        }

        fun from(imageBitmap: Bitmap, context: Context): SaveImageUtil {
            return SaveImageUtil(imageBitmap, context)
        }
    }

}
