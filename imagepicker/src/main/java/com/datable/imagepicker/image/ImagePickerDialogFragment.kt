package com.datable.imagepicker.image

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.FragmentManager
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatDialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.datable.imagepicker.R
import com.datable.imagepicker.image.editor.ImageEditorActivity
import com.datable.imagepicker.image.gallery.AlbumActivity
import com.datable.imagepicker.util.ImageFileUtil
import com.datable.imagepicker.util.ImageLocation
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImagePickerDialogFragment : AppCompatDialogFragment(), View.OnClickListener {

    var title: String? = null
    var existImage = false
    var maxImageCount = DEFAULT_MAX_IMAGE_COUNT
    var cropEnable = false
    var aspectX = -1f
    var aspectY = -1f
    var flagOfDirectAlbum = false
    var imageLocation = ImageLocation.EXTERNAL
    var isFromAlbumApp = true
    var deleteText = ""

    private var filePath: String = ""

    private var rootView: View? = null
    private var textView_title: TextView? = null
    private var textView_album: TextView? = null
    private var textView_camera: TextView? = null
    private var textView_delete: TextView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (flagOfDirectAlbum) {
            Handler().postDelayed({ pickImageFromAlbum() }, 600)
            rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_empty, null)
            return getDialog(rootView!!)
        }
        else {
            rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_image_picker, null)
            bindViews()
            initTitle(title ?: "")
            initDeleteText(deleteText)
            initExistImage(existImage)
            return getDialog(rootView!!)
        }
    }

    private fun bindViews() {
        textView_title = rootView!!.findViewById(R.id.textView_title)

        textView_album = rootView!!.findViewById(R.id.textView_album)
        textView_album!!.setOnClickListener(this)

        textView_camera = rootView!!.findViewById(R.id.textView_camera)
        textView_camera!!.setOnClickListener(this)

        textView_delete = rootView!!.findViewById(R.id.textView_delete)
        textView_delete!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (!isAdded)
            return

        when (view.id) {

            R.id.textView_album ->
                pickImageFromAlbum()

            R.id.textView_camera ->
                pickImageFromCamera()

            R.id.textView_delete -> {
                onImageDoneListener?.onDeleteImage()
                if (isAdded)
                    dismiss()
            }
        }
    }

    private fun pickImageFromAlbum() {
        if (1 == maxImageCount && isFromAlbumApp)
            pickOneImageFromAlbum()
        else
            pickManyImageFromAlbum(maxImageCount)
    }

    private fun pickOneImageFromAlbum() {
        if (!isAdded)
            return

        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_ONE_IMAGE)
        } catch (e: ActivityNotFoundException) {

            val intent = Intent(activity, AlbumActivity::class.java)
            intent.putExtra(AlbumActivity.KEY_MAX_IMAGE_COUNT, 1)
            startActivityForResult(intent, REQUEST_CODE_PICK_MANY_IMAGE)
        }

    }

    private fun pickManyImageFromAlbum(maxImageCount: Int) {
        if (!isAdded)
            return
        val intent = Intent(activity, AlbumActivity::class.java)
        intent.putExtra(AlbumActivity.KEY_MAX_IMAGE_COUNT, maxImageCount)
        startActivityForResult(intent, REQUEST_CODE_PICK_MANY_IMAGE)

        activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out_short)
    }

    private fun pickImageFromCamera() {
        try {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri_forCamera)

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        } catch (e: ActivityNotFoundException) {

            e.printStackTrace()
            Toast.makeText(activity, "카메라가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private var outputFileUri_forCamera: Uri? = null
        get() {
            if (null == field) {
                val directoryDCIM = ImageFileUtil.from(activity!!).externalParentPath_image
                val directory = File(directoryDCIM)

                if (!directory.exists())
                    directory.mkdirs()

                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val str = dateFormat.format(Date())
                val file = File(directory, "podfreeca_$str.jpg")

                filePath = "file:" + file.absolutePath
                field = FileProvider.getUriForFile(activity!!, activity!!.applicationContext.packageName + ".fileprovider", file)
            }
            return field
        }

    private fun initTitle(title: String) {
        if (!TextUtils.isEmpty(title))
            textView_title!!.text = title
    }

    private fun initDeleteText(deleteText: String) {
        if (!deleteText.isNullOrEmpty())
            textView_delete!!.text = deleteText
    }

    private fun initExistImage(existImage: Boolean) {
        if (existImage)
            textView_delete!!.visibility = View.VISIBLE
        else
            textView_delete!!.visibility = View.GONE
    }

    private fun getDialog(view: View): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setView(view)

        return builder.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {

            REQUEST_CODE_PICK_ONE_IMAGE ->

                if (Activity.RESULT_OK == resultCode) {

                    val selectedImagePath = getOriginalImagePath(data?.data)
                    if (null != selectedImagePath)
                        startImageEditorActivityOrFinish(selectedImagePath)
                }

            REQUEST_CODE_PICK_MANY_IMAGE ->

                if (Activity.RESULT_OK == resultCode) {

                    val selectedImagePaths = data?.getStringArrayListExtra(AlbumActivity.KEY_SELECTED_IMAGES)
                    if (null != selectedImagePaths)
                        startImageEditorActivityOrFinish(selectedImagePaths)
                }
                else
                    dismiss()

            REQUEST_CODE_CAMERA ->

                if (Activity.RESULT_OK == resultCode) {

                    val selectedImagePath = Uri.parse(filePath).path
                    activity?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filePath)))
                    if (null != selectedImagePath)
                        startImageEditorActivityOrFinish(selectedImagePath)
                }

            REQUEST_CODE_EDIT_IMAGE ->

                if (Activity.RESULT_OK == resultCode) {

                    val imageNames = data?.extras?.getStringArrayList(ImageEditorActivity.SAVED_IMAGE_NAMES)
                    if (null != imageNames) {
                        val imagePaths = imageNames.map { ImageFileUtil.from(activity!!).getInnerFilePath(it) }
                        onImageDoneListener?.onImagePicked(imagePaths as ArrayList<String>)
                    }
                    dismiss()
                }
        }
    }

    private fun getOriginalImagePath(thumbnailImageUri: Uri?): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity!!.contentResolver.query(thumbnailImageUri, filePathColumn, null, null, null)

        var selectedImagePath: String? = null
        if (null != cursor) {

            if (0 < cursor.count && cursor.moveToFirst()) {
                cursor.moveToFirst()
                selectedImagePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
            }
            else {
                val throwable = RuntimeException(if (null != thumbnailImageUri) thumbnailImageUri.path else "thumbnailImageUri is null")
            }
            cursor.close()
        }
        else {
            val throwable = RuntimeException(if (null != thumbnailImageUri) thumbnailImageUri.path else "thumbnailImageUri is null")
        }

        return selectedImagePath
    }

    private fun startImageEditorActivityOrFinish(selectedImagePath: String) {
        val selectedImagePaths = ArrayList<String>()
        selectedImagePaths.add(selectedImagePath)
        startImageEditorActivityOrFinish(selectedImagePaths)
    }

    private fun startImageEditorActivityOrFinish(selectedImagePaths: ArrayList<String>?) {
        if (cropEnable) {

            val intent = Intent(activity, ImageEditorActivity::class.java)
            intent.putExtra(ImageEditorActivity.IMAGE_PATHES, selectedImagePaths)
            intent.putExtra(ImageEditorActivity.ASPECT_X, aspectX)
            intent.putExtra(ImageEditorActivity.IMAGE_LOCATION, imageLocation.index)
            intent.putExtra(ImageEditorActivity.ASPECT_Y, aspectY)
            startActivityForResult(intent, REQUEST_CODE_EDIT_IMAGE)
        }
        else {

            if (null != selectedImagePaths)
                onImageDoneListener?.onImagePicked(selectedImagePaths)
            dismiss()
        }
    }


    /**
     * setter method
     */

    fun setTitle(title: String): ImagePickerDialogFragment {
        this.title = title
        return this
    }

    fun setExistImage(existImage: Boolean): ImagePickerDialogFragment {
        this.existImage = existImage
        return this
    }

    fun setDirectAlbum(flagOfDirectAlbum: Boolean): ImagePickerDialogFragment {
        this.flagOfDirectAlbum = flagOfDirectAlbum
        return this
    }

    fun setMaxImageCount(maxImageCount: Int): ImagePickerDialogFragment {
        if (0 < maxImageCount)
            this.maxImageCount = maxImageCount

        return this
    }

    fun setAspect(aspectX: Float, aspectY: Float): ImagePickerDialogFragment {
        if (0f < aspectX && 0f < aspectY) {
            this.aspectX = aspectX
            this.aspectY = aspectY
        }
        return this
    }

    fun setOtherAppToPickOne(otherAppToPickOne: Boolean): ImagePickerDialogFragment {
        this.isFromAlbumApp = otherAppToPickOne
        return this
    }

    fun setImageLocation(imageLocation: ImageLocation): ImagePickerDialogFragment {
        this.imageLocation = imageLocation
        return this
    }

    fun setCropable(cropEnable: Boolean): ImagePickerDialogFragment {
        this.cropEnable = cropEnable
        return this
    }

    /**
     */

    fun showDialog(fragmentManager: FragmentManager, onImageDoneListener: OnImageDoneListener) {
        ImagePickerDialogFragment.onImageDoneListener = onImageDoneListener
        show(fragmentManager, "ImagePickerDialogFragment")
    }

    /**
     */

    interface OnImageDoneListener {

        fun onImagePicked(imagePathList: ArrayList<String>)

        fun onDeleteImage()

    }

    companion object {

        const val REQUEST_CODE_PICK_ONE_IMAGE = 0x0101
        const val REQUEST_CODE_PICK_MANY_IMAGE = 0x0102
        const val REQUEST_CODE_CAMERA = 0x0103
        const val REQUEST_CODE_EDIT_IMAGE = 0x0104

        private const val DEFAULT_MAX_IMAGE_COUNT = 10

        private var onImageDoneListener: OnImageDoneListener? = null

    }

}
