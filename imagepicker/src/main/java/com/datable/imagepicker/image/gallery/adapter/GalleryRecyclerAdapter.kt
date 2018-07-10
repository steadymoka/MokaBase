package com.datable.imagepicker.image.gallery.adapter


import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.datable.imagepicker.R
import com.datable.imagepicker.image.base.BaseAdapter
import com.datable.imagepicker.image.base.ItemData
import com.datable.imagepicker.image.base.RecyclerItemView
import com.datable.imagepicker.util.getWidthPixels
import kotlinx.android.synthetic.main.view_gallery_item.view.*
import java.io.File


class GalleryRecyclerAdapter constructor(private val context: Context) : BaseAdapter<GalleryRecyclerAdapter.GalleryItemData, RecyclerItemView<GalleryRecyclerAdapter.GalleryItemData>>() {

    var isOnePickMode: Boolean = false
    var selectedDataFromOnePickMode: GalleryItemData? = null

    private var onImageItemSelectedListener: OnImageItemSelectedListener? = null

    override fun onCreateContentItemViewHolder(parent: ViewGroup, contentViewType: Int): RecyclerView.ViewHolder {
        return GalleryItemView(context, parent)
    }

    fun setOnImageItemSelectedListener(onImageItemSelectedListener: OnImageItemSelectedListener) {
        this.onImageItemSelectedListener = onImageItemSelectedListener
    }

    /**
     */

    inner class GalleryItemView(context: Context, parent: ViewGroup) :
            RecyclerItemView<GalleryItemData>(context, LayoutInflater.from(context).inflate(R.layout.view_gallery_item, parent, false)) {

        private var size: Int = 0

        init {
            recyclerItemView.frameLayout_galleryItemView.setOnClickListener { toggleCheckedState() }

            size = getWidthPixels(context) / 3
            val layoutParams = itemView.layoutParams
            layoutParams.width = size
            layoutParams.height = size
            recyclerItemView.layoutParams = layoutParams
        }

        private fun toggleCheckedState() {
            val checked = data!!.isChecked

            if (checked) {
                data!!.isChecked = false

                onImageItemSelectedListener?.onImageDeselected(data!!)
                selectedDataFromOnePickMode = null
            }
            else {

                if (isOnePickMode) {
                    selectedDataFromOnePickMode?.isChecked = false
                    data!!.isChecked = true
                    selectedDataFromOnePickMode = data
                }
                else {
                    onImageItemSelectedListener?.onImageSelected(data!!)
                    data!!.isChecked = true
                    selectedDataFromOnePickMode = data
                }
            }
            notifyDataSetChanged()
        }

        override fun refreshView(data: GalleryItemData?) {
            val path = data!!.data
            Glide.with(context)
                    .load(File(path))
                    .apply(RequestOptions
                            .centerCropTransform()
                            .placeholder(R.drawable.loading_img)
                            .override(size, size))
                    .into(recyclerItemView.imageView_image)

            setImageCheck(data.isChecked)
        }

        private fun setImageCheck(check: Boolean) {
            if (check)
                recyclerItemView.imageView_checkImage.visibility = View.VISIBLE
            else
                recyclerItemView.imageView_checkImage.visibility = View.GONE
        }

    }

    interface OnImageItemSelectedListener {

        fun onImageSelected(galleryItemData: GalleryItemData): Boolean

        fun onImageDeselected(galleryItemData: GalleryItemData)

    }

    /**
     * item data
     */

    class GalleryItemData : ItemData {

        var data: String? = null
        var isChecked = false
        var imageBitmap: Bitmap? = null

        fun toggleCheckedState() {
            isChecked = !isChecked
        }

    }
}
