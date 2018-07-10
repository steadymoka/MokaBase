package com.datable.imagepicker.image.gallery.adapter


import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.datable.imagepicker.R
import com.datable.imagepicker.image.base.RecyclerItemView
import com.datable.imagepicker.util.getWidthPixels
import kotlinx.android.synthetic.main.view_album_item.view.*


class AlbumItemView(context: Context, itemView: View) : RecyclerItemView<AlbumItemData>(context, itemView) {

    private var onItemClickListener: OnItemClickListener? = null

    init {
        initView()
    }

    private fun initView() {
        itemView.relativeLayout_album.setOnClickListener { onClickItem() }
    }

    override fun refreshView(data: AlbumItemData?) {
        itemView.imageView_album.setImageResource(R.drawable.loading_img)
        Glide.with(context)
                .load(data!!.thumbnailPath)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_img)
                        .override(getWidthPixels(context) / 3))
                .into(itemView.imageView_album)

        itemView.textView_album.text = data.bucketname
        itemView.textView_album_count.text = data.counter.toString()
    }

    /**
     */

    private fun onClickItem() {
        onItemClickListener?.onItemClick(data!!)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {

        fun onItemClick(albumItemData: AlbumItemData)

    }

}
