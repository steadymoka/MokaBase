package io.moka.lib.imagepicker.image.gallery.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.moka.lib.base.adapter.BaseAdapter
import io.moka.lib.base.adapter.RecyclerItemView
import io.moka.lib.base.util.deviceWidthPixel
import io.moka.lib.imagepicker.R
import kotlinx.android.synthetic.main.view_album_item.view.*


class AlbumAdapter(private val context: Context) : BaseAdapter<AlbumItemData, AlbumAdapter.AlbumItemView>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateContentItemViewHolder(parent: ViewGroup, contentViewType: Int): RecyclerView.ViewHolder {
        return AlbumItemView(context, parent)
    }

    /**
     */

    inner class AlbumItemView(context: Context, parent: ViewGroup) :
            RecyclerItemView<AlbumItemData>(context, LayoutInflater.from(context).inflate(R.layout.view_album_item, parent, false)) {

        init {
            initView()
        }

        private fun initView() {
            itemView.relativeLayout_album.setOnClickListener {
                onClickItem()
            }
        }

        override fun refreshView(data: AlbumItemData?) {
            itemView.imageView_album.setImageResource(R.drawable.loading_img)
            Glide.with(context)
                    .load(data!!.thumbnailPath)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.loading_img)
                            .override(deviceWidthPixel / 3))
                    .into(itemView.imageView_album)

            itemView.textView_album.text = data.bucketname
            itemView.textView_album_count.text = data.counter.toString()
        }

        /**
         */

        private fun onClickItem() {
            onItemClickListener?.onItemClick(data!!)
        }
    }

    interface OnItemClickListener {

        fun onItemClick(albumItemData: AlbumItemData)

    }
}
