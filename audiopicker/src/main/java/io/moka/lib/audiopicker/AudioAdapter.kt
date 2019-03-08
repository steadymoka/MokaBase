package io.moka.lib.audiopicker


import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.moka.lib.base.ColorDI
import io.moka.lib.base.adapter.BaseAdapter
import io.moka.lib.base.adapter.ItemData
import io.moka.lib.base.adapter.RecyclerItemView
import io.moka.lib.base.util.*
import kotlinx.android.synthetic.main.view_select_audio_item.view.*

enum class Mode {
    OnePick, MultiPick
}

class AudioAdapter constructor(private val context: Context, var mode: Mode = Mode.OnePick) : BaseAdapter<AudioAdapter.Data, AudioAdapter.ItemView>() {

    var onClickItem: ((Data) -> Unit)? = null
    var onClickPlayController: ((Data) -> Unit)? = null

    var selectedItem: Data? = null
    var playingItem: Data? = null

    override fun onCreateContentItemViewHolder(parent: ViewGroup, contentViewType: Int): RecyclerView.ViewHolder {
        return ItemView(context, parent)
    }

    /**
     * Content View
     */

    inner class ItemView(context: Context, parent: ViewGroup) :
            RecyclerItemView<Data>(context, LayoutInflater.from(context).inflate(R.layout.view_select_audio_item, parent, false)) {

        init {
            with(itemView) {
                imageView_check.setColorFilter(ColorDI.primaryDarkColor)

                view_back.setOnClickListener {
                    onClickItem?.invoke(data)

                    if (mode == Mode.OnePick) {
                        selectedItem = data
                        notifyDataSetChanged()
                    }
                    else {
                    }
                }

                imageView_playControl.setOnClickListener {
                    playingItem = data
                    data.isPlaying = !data.isPlaying
                    onClickPlayController?.invoke(data)
                    notifyDataSetChanged()
                }
            }
        }

        override fun refreshView(data: Data) {
            with(itemView) {
                resizeFontSizeAndExt()
                textView_string.text = data.title

                /* 재생중 표시 */
                if (playingItem != data) {
                    imageView_playControl.setImageResource(R.drawable.vc_play_black)
                }
                else {
                    if (data.isPlaying)
                        imageView_playControl.setImageResource(R.drawable.vc_pause_black)
                    else
                        imageView_playControl.setImageResource(R.drawable.vc_play_black)
                }

                /** 모드에 따라 ..  **/
                if (mode == Mode.OnePick) {
                    if (selectedItem != data) {
                        textView_string.setTextColor(color(R.color.black_04))
                        textView_string.typeface = Typeface.DEFAULT
                        imageView_check.invisible()
                        view_underLine.gone()
                    }
                    else {
                        textView_string.setTextColor(ColorDI.primaryDarkColor)
                        textView_string.typeface = Typeface.DEFAULT_BOLD
                        imageView_check.visible()
                        view_underLine.visible()
                    }
                }
                else {
                    if (data.isChecked) {
                        textView_string.setTextColor(ColorDI.primaryDarkColor)
                        textView_string.typeface = Typeface.DEFAULT_BOLD
                        imageView_check.visible()
                        view_underLine.visible()
                    }
                    else {
                        textView_string.setTextColor(color(R.color.black_04))
                        textView_string.typeface = Typeface.DEFAULT
                        imageView_check.invisible()
                        view_underLine.gone()
                    }
                }
            }
        }

        private fun resizeFontSizeAndExt() {
            with(itemView) {
                FontSizeKit.size(13f, textView_string)
            }
        }

    }

    data class Data(
            var title: String,
            var isPlaying: Boolean = false,
            var isChecked: Boolean = false
    ) : ItemData

}
