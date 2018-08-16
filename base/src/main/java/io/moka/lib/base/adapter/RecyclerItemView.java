package io.moka.lib.base.adapter;


import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public abstract class RecyclerItemView<DATA extends ItemData> extends RecyclerView.ViewHolder {

	private Context context;
	private View itemView;
	private int index;

	private DATA data;
	@Nullable private DATA preData;
	@Nullable private DATA afterData;

	public RecyclerItemView(Context context, View itemView) {
		super(itemView);
		this.context = context;
		this.itemView = itemView;
	}

	public void setData(DATA data) {
		this.data = data;
		refreshView(data);
	}

	public void setPreData(DATA preData) {
		this.preData = preData;
	}

	public void setAfterData(DATA afterData) {
		this.afterData = afterData;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public DATA getData() {
		return data;
	}

	@Nullable
	public DATA getPreData() {
		return preData;
	}

	@Nullable
	public DATA getAfterData() {
		return afterData;
	}

	public Context getContext() {
		return context;
	}

	public View getItemView() {
		return itemView;
	}

	public int getIndex() {
		return index;
	}

	public View findViewById(int resId) {
		return itemView.findViewById(resId);
	}

	protected abstract void refreshView(DATA data);

}
