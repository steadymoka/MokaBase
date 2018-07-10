package com.datable.imagepicker.image.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.datable.imagepicker.util.HeaderFooterRecyclerViewAdapter

/**
 * BaseAdapter
 */
abstract class BaseAdapter<DATA : ItemData, in VIEW : RecyclerItemView<DATA>> : HeaderFooterRecyclerViewAdapter() {

    var headerData: DATA? = null
    var footerData: DATA? = null

    var headerView: View? = null
        private set
    var footerView: View? = null
        private set

    var items: MutableList<DATA> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getHeaderItemCount(): Int {
        return if (null == headerView) 0 else 1
    }

    override fun getFooterItemCount(): Int {
        return if (null == footerView) 0 else 1
    }

    override fun getContentItemCount(): Int {
        return items.size
    }

    /**
     */

    override fun onCreateHeaderItemViewHolder(parent: ViewGroup, headerViewType: Int): RecyclerView.ViewHolder {
        return HeaderFooter(headerView)
    }

    override fun onCreateFooterItemViewHolder(parent: ViewGroup, footerViewType: Int): RecyclerView.ViewHolder {
        return HeaderFooter(footerView)
    }

    abstract override fun onCreateContentItemViewHolder(parent: ViewGroup, contentViewType: Int): RecyclerView.ViewHolder

    /**
     */

    @Suppress("UNCHECKED_CAST")
    override fun onBindHeaderItemViewHolder(headerViewHolder: RecyclerView.ViewHolder, position: Int) {
        val headerItemView = headerViewHolder as VIEW
        headerItemView.data = headerData
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindFooterItemViewHolder(footerViewHolder: RecyclerView.ViewHolder, position: Int) {
        val footerItemView = footerViewHolder as VIEW
        footerItemView.data = footerData
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindContentItemViewHolder(contentViewHolder: RecyclerView.ViewHolder, position: Int) {
        val itemView = contentViewHolder as VIEW
        if (0 < position)
            itemView.preData = items[position - 1]
        else
            itemView.preData = null

        if (items.size > position + 1)
            itemView.afterData = items[position + 1]
        else
            itemView.afterData = null

        itemView.index = position
        itemView.data = items[position]
    }

    /**
     */

    fun add(data: DATA?) {
        if (null != data) {
            items.add(data)
            notifyDataSetChanged()
        }
    }

    fun add(index: Int, data: DATA?) {
        if (0 <= index && index <= items.size && null != data) {
            items.add(index, data)
            notifyDataSetChanged()
        }
    }

    fun addAfter(point: DATA, data: DATA) {
        items.add(items.indexOf(point) + 1, data)
        notifyDataSetChanged()
    }

    fun addAll(items: List<DATA>?) {
        if (null != items && items.isNotEmpty() && this.items.addAll(items))
            notifyDataSetChanged()
    }

    fun remove(item: DATA?) {
        if (null != item) {
            this.items.remove(item)
            notifyDataSetChanged()
        }
    }

    fun addHeaderView(headerView: View) {
        this.headerView = headerView
        notifyDataSetChanged()
    }

    fun addFooterView(footerView: View) {
        this.footerView = footerView
        notifyDataSetChanged()
    }

    fun removeHeaderView() {
        this.headerView = null
        notifyDataSetChanged()
    }

    fun removeFooterView() {
        this.footerView = null
        notifyDataSetChanged()
    }

    fun clear() {
        this.items = ArrayList<DATA>()
        notifyDataSetChanged()
    }

    fun notifyItemChanged(data: DATA?) {
        val index = items.indexOf(data)
        if (index < 0)
            return
        notifyContentItemChanged(index)
    }

    private class HeaderFooter(itemView: View?) : RecyclerView.ViewHolder(itemView)

}

/**
 * Base ItemData
 */
interface ItemData

/**
 * Base RecyclerItemView
 */
abstract class RecyclerItemView<DATA : ItemData>(
        val context: Context,
        val recyclerItemView: View) : RecyclerView.ViewHolder(recyclerItemView) {

    var index: Int = 0

    var data: DATA? = null
        set(data) {
            field = data
            refreshView(data)
        }
    var preData: DATA? = null
    var afterData: DATA? = null

    protected abstract fun refreshView(data: DATA?)

}