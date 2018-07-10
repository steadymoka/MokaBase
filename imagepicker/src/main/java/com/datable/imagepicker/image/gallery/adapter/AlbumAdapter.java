package com.datable.imagepicker.image.gallery.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.datable.imagepicker.R;
import com.datable.imagepicker.util.HeaderFooterRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class AlbumAdapter extends HeaderFooterRecyclerViewAdapter {

    private AlbumItemView.OnItemClickListener onItemClickListener;

    private List<AlbumItemData> items;

    private View headerView;
    private View footerView;

    private int gridCount;

    private Context context;

    public AlbumAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    @Override
    protected int getHeaderItemCount() {
        return (null == headerView) ? (0) : (1);
    }

    @Override
    protected int getFooterItemCount() {
        return (null == footerView) ? (0) : (1);
    }

    @Override
    protected int getContentItemCount() {
        return items.size();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        return new Header(headerView);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        return new Header(footerView);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_album_item, parent, false);
        AlbumItemView itemView = new AlbumItemView(context, view);
        itemView.setOnItemClickListener(onItemClickListener);

        return itemView;
    }

    @Override
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {

    }

    @Override
    protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {

    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
        AlbumItemView itemView = (AlbumItemView) contentViewHolder;
        itemView.setData(items.get(position));
    }

    public void setOnItemClickListener(AlbumItemView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setGridCount(int gridCount) {
        this.gridCount = gridCount;
    }

    public void setItems(List<AlbumItemData> items) {
        this.items.clear();

        if (null != items)
            this.items.addAll(items);

        notifyDataSetChanged();
    }

    public List<AlbumItemData> getItems() {
        return items;
    }

    public void add(AlbumItemData data) {
        if (null != data) {

            items.add(data);
            notifyDataSetChanged();
        }
    }

    public void add(int index, AlbumItemData data) {
        if (0 <= index && index <= items.size() && null != data) {

            items.add(index, data);
            notifyDataSetChanged();
        }
    }

    public void addAll(List<AlbumItemData> items) {
        if (null != items && 0 < items.size() && this.items.addAll(items))
            notifyDataSetChanged();
    }

    public void addAll(List<AlbumItemData> items, int location) {
        if (null != items && 0 < items.size() && this.items.addAll(location, items))
            notifyDataSetChanged();
    }

    public void remove(AlbumItemData item) {
        if (null != item) {

            this.items.remove(item);
            notifyDataSetChanged();
        }
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyDataSetChanged();
    }

    public void removeHeaderView() {
        headerView = null;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    private static class Header extends RecyclerView.ViewHolder {

        public Header(View itemView) {

            super(itemView);
        }

    }

}
