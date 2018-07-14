package io.moka.lib.imagepicker.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import android.util.Log
import io.moka.lib.base.MokaBase
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("StaticFieldLeak")
object ImageFileUtil {

    private var APP_NAME = "dayday"
    private lateinit var context: Context

    private const val IMAGE_DIRECTORY = "/images/"
    private const val FILE_FORMAT_JPG = "jpg"

    private lateinit var innerParentPath: String
    private lateinit var externalParentPath: String

    fun init(context: Context, appName: String) {
        this.context = context
        this.APP_NAME = appName

        innerParentPath = context.filesDir.path
        externalParentPath = Environment.getExternalStorageDirectory().path + "/$APP_NAME/"
    }

    /**
     */

    fun getPath(locationType: LocationType): String {
        return when (locationType) {
            LocationType.INNER -> innerParentPath
            LocationType.EXTERNAL -> externalParentPath
            else -> externalParentPath
        }
    }

    fun getImagePath(locationType: LocationType): String {
        return when (locationType) {
            LocationType.INNER -> "$innerParentPath$IMAGE_DIRECTORY"
            LocationType.EXTERNAL -> "$externalParentPath$IMAGE_DIRECTORY"
            else -> "$externalParentPath$IMAGE_DIRECTORY"
        }
    }

    /**
     * About Inner
     */

    fun getInnerPath(directory: String? = null, fileName: String): String {
        return if (null != directory)
            "$innerParentPath/$directory/$fileName"
        else
            "$innerParentPath/$fileName"
    }

    /**
     * About External
     */

    fun getExternalPath(directory: String? = null, fileName: String): String {
        return if (null != directory)
            "$externalParentPath/$directory/$fileName"
        else
            "$externalParentPath/$fileName"
    }

    /**
     * Util Method
     */

    fun generateFileNameToShow(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault())
        val str = dateFormat.format(Date())
        return "${APP_NAME}_$str.jpg"
    }

    fun generateUUID(): String {
        val timestamp = System.currentTimeMillis() / 1000
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
