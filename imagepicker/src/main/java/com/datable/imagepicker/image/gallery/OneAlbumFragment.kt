package com.datable.imagepicker.image.gallery


import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.datable.imagepicker.R
import com.datable.imagepicker.image.gallery.adapter.AlbumItemData
import com.datable.imagepicker.image.gallery.adapter.GalleryRecyclerAdapter
import com.datable.imagepicker.image.gallery.adapter.GalleryRecyclerAdapter.GalleryItemData
import com.datable.imagepicker.image.gallery.adapter.GalleryRecyclerAdapter.OnImageItemSelectedListener
import kotlinx.android.synthetic.main.fragment_one_album.*
import java.util.*


class OneAlbumFragment : Fragment(), OnImageItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private val galleryAdapter by lazy { GalleryRecyclerAdapter(activity!!) }
    private var galleryItemDatas: ArrayList<GalleryItemData>? = null
    private var selectedGalleryItemDatas: ArrayList<GalleryItemData>? = null

    private var titleListener: AlbumActivity.TitleListener? = null
    private var albumData: AlbumItemData? = null
    private var maxImageCount: Int = 1
    private var isOnePickMode: Boolean = false
        get() = maxImageCount == 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_one_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val bucketId = albumData?.bucketid.toString()

        if (bucketId != "0") {
            return CursorLoader(
                    activity!!,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Images.Media.BUCKET_ID + " = ?", arrayOf(bucketId), MediaStore.Images.Media._ID + " DESC")
        }
        else {
            return CursorLoader(
                    activity!!,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null, null, MediaStore.Images.Media._ID + " DESC")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, curcor: Cursor?) {
        if (null == curcor)
            return
        getAllMediaThumbnailsPath(curcor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    /**
     */

    private fun init() {
        selectedGalleryItemDatas = ArrayList()
        galleryItemDatas = ArrayList()

        val layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = galleryAdapter
        galleryAdapter.setOnImageItemSelectedListener(this)
        galleryAdapter.isOnePickMode = isOnePickMode
    }

    private fun getAllMediaThumbnailsPath(c: Cursor) {
        if (c.count != 0) {
            c.moveToFirst()
            while (true) {

                val path = c.getString(c.getColumnIndex(MediaStore.Images.Thumbnails.DATA))
                if (c.isLast) {
                    val galleryItemData = GalleryItemData()
                    galleryItemData.data = path
                    galleryItemDatas!!.add(galleryItemData)
                    c.close()
                    break
                }
                else {
                    val galleryItemData = GalleryItemData()
                    galleryItemData.data = path
                    galleryItemDatas!!.add(galleryItemData)
                    c.moveToNext()
                }
            }
        }

        galleryAdapter.items = galleryItemDatas!!
    }

    /**
     */

    override fun onImageSelected(galleryItemData: GalleryItemData): Boolean {
        if (maxImageCount > selectedGalleryItemDatas?.size ?: 0) {

            selectedGalleryItemDatas?.add(galleryItemData)
            titleListener?.setTitle(selectedGalleryItemDatas!!.size)
            return true
        }
        else {

            Toast.makeText(activity, "최대 갯수를 넘었어요", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    override fun onImageDeselected(galleryItemData: GalleryItemData) {
        selectedGalleryItemDatas?.remove(galleryItemData)
        titleListener?.setTitle(selectedGalleryItemDatas!!.size)
    }

    fun onCompleteImageSelection() {
        if (isOnePickMode) {
            if (galleryAdapter.selectedDataFromOnePickMode == null) {
                Toast.makeText(activity, "사진을 선택해주세요", Toast.LENGTH_SHORT).show()
                return
            }
        }
        else {
            if (1 > selectedGalleryItemDatas!!.size) {
                Toast.makeText(activity, "하나 이상을 선택해야 되요", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val data = Intent()
        data.putStringArrayListExtra(AlbumActivity.KEY_SELECTED_IMAGES, getSelectedImagePaths())
        activity!!.setResult(Activity.RESULT_OK, data)
        activity!!.finish()
    }

    private fun getSelectedImagePaths(): ArrayList<String> {
        val selectedImagePaths = ArrayList<String>()

        for (data in galleryItemDatas!!)
            if (data.isChecked)
                selectedImagePaths.add(data.data!!)

        return selectedImagePaths
    }

    /**
     */

    fun setAlbumData(albumData: AlbumItemData) {
        this.albumData = albumData
    }

    fun setMaxImageCount(maxImageCount: Int) {
        this.maxImageCount = maxImageCount
    }

    fun setTitleListener(titleListener: AlbumActivity.TitleListener) {
        this.titleListener = titleListener
    }

//    fun setToolbar(toolbarLayout: ToolbarLayout) {
//        toolbarLayout.setMenuVisible(true)
//        toolbarLayout.setMenuText(MocaApplication.context.getString(R.string.image_select_menu))
//        toolbarLayout.setMenuListener { onCompleteImageSelection() }
//    }

}
