package io.moka.lib.base.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


/**
 * The adapter to assist the {@link StickyHeaderDecoration} in creating and binding the header views.
 *
 * @param <T> the header view holder
 */
public interface StickyHeaderAdapter {

	/**
	 * Returns the header id for the item at the given position.
	 *
	 * @param position the item position
	 * @return the header id
	 */
	long getStickyId(int position);

	/**
	 * Creates a new header ViewHolder.
	 *
	 * @param parent the header's view parent
	 * @return a view holder for the created view
	 */
	RecyclerView.ViewHolder onCreateStickyView(ViewGroup parent);

	/**
	 * Updates the header view to reflect the header data for the given position
	 *
	 * @param viewholder the header view holder
	 * @param position   the header's item position
	 */
	void onBindStickyView(RecyclerView.ViewHolder viewholder, int position);
}