package io.moka.lib.imagepicker.image.gallery.adapter


import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.moka.lib.base.adapter.BaseAdapter
import io.moka.lib.base.adapter.ItemData
import io.moka.lib.base.adapter.RecyclerItemView
import io.moka.lib.base.util.deviceWidthPixel
import io.moka.lib.imagepicker.R
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
            itemView.frameLayout_galleryItemView.setOnClickListener { toggleCheckedState() }

            size = deviceWidthPixel / 3
            val layoutParams = itemView.layoutParams
            layoutParams.width = size
            layoutParams.height = size
            itemView.layoutParams = layoutParams
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
                    .into(itemView.imageView_image)

            setImageCheck(data.isChecked)
        }

        private fun setImageCheck(check: Boolean) {
            if (check)
                itemView.imageView_checkImage.visibility = View.VISIBLE
            else
                itemView.imageView_checkImage.visibility = View.GONE
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
