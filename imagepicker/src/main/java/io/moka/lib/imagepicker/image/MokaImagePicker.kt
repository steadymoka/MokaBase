package io.moka.lib.imagepicker.image

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.moka.lib.base.util.visibleOrGone
import io.moka.lib.imagepicker.R
import io.moka.lib.imagepicker.image.editor.ImageEditorActivity
import io.moka.lib.imagepicker.image.gallery.AlbumActivity
import io.moka.lib.imagepicker.util.LocationType
import kotlinx.android.synthetic.main.dialog_image_picker.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MokaImagePicker : AppCompatDialogFragment() {

    companion object {

        var APP_NAME = "dayday"

        const val REQUEST_CODE_PICK_ONE_IMAGE = 0x0101
        const val REQUEST_CODE_PICK_MANY_IMAGE = 0x0102
        const val REQUEST_CODE_CAMERA = 0x0103
        const val REQUEST_CODE_EDIT_IMAGE = 0x0104

        private const val DEFAULT_MAX_IMAGE_COUNT = 10
    }

    private val viewModel by lazy { ViewModel() }

    private var onImageSelected: ((imagePathList: ArrayList<String>) -> Unit)? = null
    private var onImageDeleted: (() -> Unit)? = null
    private var needPermission: ((callback: () -> Unit) -> Unit)? = null

    private var filePath: String = ""

    /**
     * LifeCycle Functions
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (viewModel.flagOfDirectAlbum) {
            Handler().postDelayed({ pickImageFromAlbum() }, 600)
            inflater.inflate(R.layout.dialog_empty, null)
        }
        else {
            inflater.inflate(R.layout.dialog_image_picker, null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!viewModel.flagOfDirectAlbum) {
            initViews()
            bindViews()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onResume() {
        super.onResume()
        dialog.window!!.setLayout((280 * context!!.resources.displayMetrics.densityDpi / 160.0).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**/

    private fun initViews() {
        viewModel.existImage = viewModel.existImage
        viewModel.title = viewModel.title
        viewModel.deleteText = viewModel.deleteText
        viewModel.useCamera = viewModel.useCamera
    }

    private fun bindViews() {
        textView_album.setOnClickListener {
            pickImageFromAlbum()
        }

        textView_camera.setOnClickListener {
            if (null == needPermission) {
                pickImageFromCamera()
            }
            else {
                needPermission?.invoke {
                    pickImageFromCamera()
                }
            }
        }

        textView_delete.setOnClickListener {
            onImageDeleted?.invoke()
            if (isAdded)
                dismiss()
        }
    }

    /**/

    private fun pickImageFromAlbum() {
        if (1 == viewModel.maxImageCount && viewModel.otherAppToPickOne)
            pickOneImageFromAlbum()
        else
            pickManyImageFromAlbum(viewModel.maxImageCount)
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputFileUri_forCamera())

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(activity, "카메라가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getOutputFileUri_forCamera(): Uri? {
        val directoryDCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        if (!directoryDCIM.exists())
            directoryDCIM.mkdirs()

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val str = dateFormat.format(Date())
        val file = File(directoryDCIM, "${APP_NAME}_$str.jpg")

        filePath = "file:" + file.absolutePath

        return FileProvider.getUriForFile(activity!!, activity!!.applicationContext.packageName + ".fileprovider", file)
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
                    val imagePaths = data?.extras?.getStringArrayList(ImageEditorActivity.SAVED_IMAGE_PATHS)

                    onImageSelected?.invoke(imagePaths as ArrayList<String>)
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
                RuntimeException(if (null != thumbnailImageUri) thumbnailImageUri.path else "thumbnailImageUri is null")
            }
            cursor.close()
        }
        else {
            RuntimeException(if (null != thumbnailImageUri) thumbnailImageUri.path else "thumbnailImageUri is null")
        }

        return selectedImagePath
    }

    private fun startImageEditorActivityOrFinish(selectedImagePath: String) {
        val selectedImagePaths = ArrayList<String>()
        selectedImagePaths.add(selectedImagePath)
        startImageEditorActivityOrFinish(selectedImagePaths)
    }

    private fun startImageEditorActivityOrFinish(selectedImagePaths: ArrayList<String>?) {
        if (viewModel.cropEnable) {

            val intent = Intent(activity, ImageEditorActivity::class.java)
            intent.putExtra(ImageEditorActivity.IMAGE_PATHES, selectedImagePaths)
            intent.putExtra(ImageEditorActivity.ASPECT_X, viewModel.aspectX)
            intent.putExtra(ImageEditorActivity.IMAGE_LOCATION, viewModel.saveLocationType.index)
            intent.putExtra(ImageEditorActivity.ASPECT_Y, viewModel.aspectY)
            startActivityForResult(intent, REQUEST_CODE_EDIT_IMAGE)
        }
        else {

            onImageSelected?.invoke(selectedImagePaths ?: arrayListOf())
            dismiss()
        }
    }


    /**
     * setter method
     */

    fun setAppName(appName: String): MokaImagePicker {
        APP_NAME = appName
        return this
    }

    fun setTitle(title: String): MokaImagePicker {
        viewModel.title = title
        return this
    }

    fun setExistImage(existImage: Boolean): MokaImagePicker {
        viewModel.existImage = existImage
        return this
    }

    fun setDirectAlbum(flagOfDirectAlbum: Boolean): MokaImagePicker {
        viewModel.flagOfDirectAlbum = flagOfDirectAlbum
        return this
    }

    fun setMaxImageCount(maxImageCount: Int): MokaImagePicker {
        if (0 < maxImageCount)
            viewModel.maxImageCount = maxImageCount

        return this
    }

    fun setAspect(aspectX: Float, aspectY: Float): MokaImagePicker {
        if (0f < aspectX && 0f < aspectY) {
            viewModel.aspectX = aspectX
            viewModel.aspectY = aspectY
        }
        return this
    }

    fun setOtherAppToPickOne(otherAppToPickOne: Boolean): MokaImagePicker {
        viewModel.otherAppToPickOne = otherAppToPickOne
        return this
    }

    fun setImageLocation(locationType: LocationType): MokaImagePicker {
        viewModel.saveLocationType = locationType
        return this
    }

    fun setCropable(cropEnable: Boolean): MokaImagePicker {
        viewModel.cropEnable = cropEnable
        return this
    }

    fun setUseCamera(useCamera: Boolean): MokaImagePicker {
        viewModel.useCamera = useCamera
        return this
    }

    /**/

    fun setOnNeedCameraPermission(needPermission: ((callback: () -> Unit) -> Unit)): MokaImagePicker {
        this.needPermission = needPermission
        return this
    }

    fun setOnDeleted(onDeleted: () -> Unit): MokaImagePicker {
        this.onImageDeleted = onDeleted
        return this
    }

    fun showDialog(fragmentManager: FragmentManager, onImageSelected: ((imagePathList: ArrayList<String>) -> Unit)?) {
        this.onImageSelected = onImageSelected
        show(fragmentManager, "MokaImagePicker")
    }

    /**/

    inner class ViewModel {

        var existImage by Delegates.observable(false) { _, _, value ->
            if (!isAdded)
                return@observable

            if (value)
                textView_delete.visibility = View.VISIBLE
            else
                textView_delete.visibility = View.GONE
        }

        var title by Delegates.observable("선택") { _, _, value ->
            if (!isAdded)
                return@observable

            textView_title.text = value
        }

        var deleteText by Delegates.observable("삭제하기") { _, _, value ->
            if (!isAdded)
                return@observable

            textView_delete.text = value
        }

        var useCamera by Delegates.observable(false) { _, _, value ->
            if (!isAdded)
                return@observable

            textView_camera.visibleOrGone(value)
        }

        var maxImageCount = DEFAULT_MAX_IMAGE_COUNT

        var cropEnable: Boolean = false

        var aspectX = -1f

        var aspectY = -1f

        var flagOfDirectAlbum = false

        var saveLocationType = LocationType.INNER

        var otherAppToPickOne = true

    }

}
