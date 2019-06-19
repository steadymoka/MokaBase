package io.moka.mokabase

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.moka.lib.base.adapter.BaseAdapter
import io.moka.lib.base.adapter.ItemData
import io.moka.lib.base.adapter.RecyclerItemView
import kotlinx.android.synthetic.main.layout_item_view.view.*


class TestAdapter(private val context: Context) : BaseAdapter<TestAdapter.Data, RecyclerItemView<TestAdapter.Data>>() {

    /**
     * Data, ViewType
     *
     * createViews,
     * bindViews
     */

    data class Data(
        var type: Type,
        var title: String = ""
    ) : ItemData

    enum class Type {
        ITEM_VIEW;

        companion object {
            fun get(ordinal: Int): Type {
                return values().filter { it.ordinal == ordinal }[0]
            }
        }
    }

    override fun getContentItemViewType(position: Int): Int {
        return items[position].type.ordinal
    }

    override fun onCreateContentItemViewHolder(parent: ViewGroup, contentViewType: Int): RecyclerView.ViewHolder {
        return when (Type.get(contentViewType)) {
            Type.ITEM_VIEW -> CampaignItemView(context, parent)

        }
    }

}

class CampaignItemView(context: Context, parent: ViewGroup)
    : RecyclerItemView<TestAdapter.Data>(context, LayoutInflater.from(context).inflate(R.layout.layout_item_view, parent, false)) {

    public override fun refreshView(data: TestAdapter.Data?) {
        with(itemView) {
            textView_title.text = data?.title
        }
    }

}
