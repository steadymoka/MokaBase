package com.datable.imagepicker.image.gallery


import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.datable.imagepicker.R
import com.datable.imagepicker.image.gallery.adapter.AlbumAdapter
import com.datable.imagepicker.image.gallery.adapter.AlbumItemData
import com.datable.imagepicker.image.gallery.adapter.AlbumItemView
import kotlinx.android.synthetic.main.fragment_albums.*
import java.util.*


class AlbumsFragment : Fragment(), View.OnTouchListener, AlbumItemView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    var onClickAlbum: ((AlbumItemData) -> Unit)? = null
    private var albumAdapter: AlbumAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater!!.inflate(R.layout.fragment_albums, container, false)
        rootView.setOnTouchListener(this)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
                activity!!,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATA),
                null, null, MediaStore.Images.Media.BUCKET_ID + " DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        if (null == cursor)
            return
        refreshView(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    /**
     */

    private fun init() {
        val layoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
    }

    val recyclerAdapter: AlbumAdapter
        get() {

            if (null == albumAdapter) {

                albumAdapter = AlbumAdapter(activity)
                albumAdapter!!.setOnItemClickListener(this)
            }
            return albumAdapter!!
        }

    private fun refreshView(imageCursor: Cursor) {
        var previousId: Long = 0

        val albumItemDatas = ArrayList<AlbumItemData>()

        val bucketColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val bucketColumnId = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)

        val albumItemDataOfTotal = AlbumItemData()
        albumItemDataOfTotal.bucketid = 0
        albumItemDataOfTotal.bucketname = "전체 사진"
        albumItemDataOfTotal.counter = 0

        albumItemDatas.add(albumItemDataOfTotal)

        var totalCounter = 0
        while (imageCursor.moveToNext()) {

            totalCounter++
            val bucketId = imageCursor.getInt(bucketColumnId).toLong()
            if (previousId != bucketId) {

                val albumItemData = AlbumItemData()
                albumItemData.bucketid = bucketId
                albumItemData.bucketname = imageCursor.getString(bucketColumn)
                albumItemData.thumbnailPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                albumItemData.plusCount()

                albumItemDatas.add(albumItemData)
                previousId = bucketId
            }
            else {

                if (albumItemDatas.size > 0) {
                    albumItemDatas[albumItemDatas.size - 1].plusCount()
                    albumItemDatas[albumItemDatas.size - 1].thumbnailPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                }
            }

            if (imageCursor.isLast)
                albumItemDatas[0].counter = totalCounter
        }
        imageCursor.close()

        albumAdapter!!.items = albumItemDatas
    }

    /**
     */

    override fun onItemClick(albumItemData: AlbumItemData) {
        onClickAlbum?.invoke(albumItemData)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return false
    }

    /**
     */

    companion object {

        fun newInstance() = AlbumsFragment()

    }

}
