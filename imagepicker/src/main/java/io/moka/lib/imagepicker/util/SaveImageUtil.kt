package io.moka.lib.imagepicker.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class SaveImageUtil : Thread {

    private var selectedImagePaths: ArrayList<String>? = null
    private var imageBitmap: Bitmap? = null

    private var context: Context? = null
    private var onSaveImageListener: ((ArrayList<String>) -> Unit)? = null

    private var flag: Boolean = false
    private var locationType: LocationType = LocationType.EXTERNAL

    private val imagePaths = ArrayList<String>()

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
        return imagePaths
    }

    override fun run() {
        super.run()
        checkNoMediaFlagFile()

        if (flag) {

            for (i in selectedImagePaths!!.indices) {

                val option = BitmapFactory.Options()
                val imageBitmap = BitmapFactory.decodeFile(selectedImagePaths!![i], option)

                val directoryPath = ImageFileUtil.getImagePath(locationType)

                val imageName: String = if (locationType == LocationType.EXTERNAL)
                    ImageFileUtil.generateFileNameToShow()
                else
                    ImageFileUtil.generateUUID()

                val savedFile = storeEditedImage(imageBitmap, directoryPath, imageName)
                imagePaths.add(savedFile.path)

                /* */
                val uri = Uri.fromFile(savedFile)
                context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            }

            Handler(Looper.getMainLooper()).post { onSaveImageListener?.invoke(imagePaths) }
        }
        else {

            val directoryPath = ImageFileUtil.getImagePath(locationType)

            val imageName: String = if (locationType == LocationType.EXTERNAL)
                ImageFileUtil.generateFileNameToShow()
            else
                ImageFileUtil.generateUUID()

            val savedFile = storeEditedImage(imageBitmap!!, directoryPath, imageName)
            imagePaths.add(savedFile.path)

            /* */
            val uri = Uri.fromFile(savedFile)
            context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

            Handler(Looper.getMainLooper()).post { onSaveImageListener?.invoke(imagePaths) }
        }
    }

    private fun checkNoMediaFlagFile() {
        if (locationType == LocationType.EXTERNAL_NO_MEDIA) {
            val directoryPath = ImageFileUtil.getImagePath(locationType)

            val directory = File(directoryPath)
            if (!directory.exists())
                directory.mkdirs()

            val noMediaFile = File(directory, ".nomedia")
            noMediaFile.createNewFile()
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

    fun setImageLocation(locationType: LocationType = LocationType.INNER): SaveImageUtil {
        this.locationType = locationType
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
