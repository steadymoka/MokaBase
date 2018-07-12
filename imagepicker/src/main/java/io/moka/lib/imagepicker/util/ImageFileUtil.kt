package io.moka.lib.imagepicker.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ImageFileUtil private constructor(context: Context) {
    private var context: Context? = null

    val innerParentPath: String
    val externalParentPath: String
    val externalParentPath_image: String
    val innerBackUpPath: String

    init {
        innerParentPath = context.filesDir.path + IMAGE_DIRECTORY
        innerBackUpPath = context.filesDir.path + "/backup/"
        externalParentPath = Environment.getExternalStorageDirectory().path + "/$APP_NAME/"
        externalParentPath_image = Environment.getExternalStorageDirectory().path + "/$APP_NAME/images/"
    }

    fun getParentPathToSaveImage(imageLocation: ImageLocation): String {
        return when (imageLocation) {
            ImageLocation.INNER -> innerParentPath
            ImageLocation.EXTERNAL -> externalParentPath_image
            else -> externalParentPath
        }
    }

    /**
     */

    fun isExistInInner(fileName: String?): Boolean {
        val file = getFileOfInner(fileName)
        return file.exists()
    }

    /**
     * About Inner
     */

    fun getFileOfInner(fileName: String?): File {
        return if (null != fileName)
            File(directoryOfInner, fileName)
        else
            File(directoryOfInner, "")
    }

    fun getUriOfInner(fileName: String): Uri {
        return Uri.fromFile(getFileOfInner(fileName))
    }

    fun getInnerFilePath(fileName: String?): String? {
        return if (!fileName.isNullOrEmpty())
            innerParentPath + fileName
        else
            null
    }

    val directoryOfInner: File
        get() {
            val directory = File(innerParentPath)

            if (!directory.exists())
                directory.mkdirs()

            return directory
        }

    /**
     * About External
     */

    fun getFileOfExternal(fileName: String?): File {
        if (null != fileName)
            return File(directoryOfExternal, fileName)
        else
            return File(directoryOfExternal, "")
    }

    fun getExternalFilePath(fileName: String): String? {
        if (!TextUtils.isEmpty(fileName))
            return externalParentPath + fileName
        else
            return null
    }

    fun getExternalOfImageFilePath(fileName: String): String? {
        if (!TextUtils.isEmpty(fileName))
            return externalParentPath_image + fileName
        else
            return null
    }

    val directoryOfExternal: File
        get() {
            val directory = File(externalParentPath)

            if (!directory.exists())
                directory.mkdirs()

            return directory
        }

    /**
     * Factory and setter Method
     */

    fun setContext(context: Context) {
        this.context = context
    }

    /**
     */

    companion object {

        private const val IMAGE_DIRECTORY = "/images/"
        private const val FILE_FORMAT_JPG = "jpg"
        private var APP_NAME = ""

        private var instance: ImageFileUtil? = null

        fun from(context: Context, appName: String = "image_v"): ImageFileUtil {
            APP_NAME = appName

            if (null == instance)
                instance = ImageFileUtil(context)

            instance!!.setContext(context)
            return instance!!
        }

        /**
         * Util Method
         */

        fun generateFileNameToShow(): String {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault())
            val str = dateFormat.format(Date())
            return "${APP_NAME}_$str.jpg"
        }

        fun generateFileName(): String {
            val timestamp = System.currentTimeMillis() / 1000;
            val uuid = UUID.randomUUID()

            return String.format("%d-%s.%s", timestamp, uuid, FILE_FORMAT_JPG)
        }

        fun getImageName(imagePath: String): String {
            return imagePath.substring(imagePath.lastIndexOf("/") + 1)
        }

        fun getBitmapOfWidth(filePath: String): Int {
            val bitmap = getBitmapByRotate(filePath)

            var width = 0
            if (null != bitmap) {

                width = bitmap.width
                bitmap.recycle()
            }

            return width
        }

        fun getBitmapOfHeight(filePath: String): Int {
            val bitmap = getBitmapByRotate(filePath)

            var height = 0
            if (null != bitmap) {

                height = bitmap.height
                bitmap.recycle()
            }

            return height
        }

        /**
         * About Rotate
         */

        fun getBitmapByRotate(imagePath: String): Bitmap? {
            var exif: ExifInterface? = null
            try {

                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = false
                val image = BitmapFactory.decodeFile(imagePath, options)

                exif = ExifInterface(imagePath)
                val exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
                val exifDegree = exifOrientationToDegrees(exifOrientation)

                return rotate(image, exifDegree)
            } catch (e: IOException) {

                Log.wtf("getBitmapByRotate", "here?")
                e.printStackTrace()
                return null
            }

        }

        private fun exifOrientationToDegrees(exifOrientation: Int): Int {
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
                return 90
            else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
                return 180
            else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
                return 270
            return 0
        }

        private fun rotate(bitmap: Bitmap?, degrees: Int): Bitmap {
            var bitmap = bitmap
            if (degrees != 0 && bitmap != null) {

                val m = Matrix()
                m.setRotate(degrees.toFloat(), bitmap.width.toFloat(), bitmap.height.toFloat())

                try {

                    val converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)

                    if (bitmap != converted) {

                        bitmap.recycle()
                        bitmap = converted
                    }
                } catch (ex: OutOfMemoryError) {
                    // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
                    Log.wtf("rotate", "here?")
                }

            }
            return bitmap!!
        }
    }

}
