package io.moka.lib.base.views


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import io.moka.lib.base.R
import io.moka.lib.base.util.invisibleFadeOut
import io.moka.lib.base.util.onClick
import io.moka.lib.base.util.visibleFadeIn
import kotlinx.android.synthetic.main.layout_icon_list_view.view.*

/*
 */

data class IconData(var iconRes: Int)

class IconListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private var iconViews: ArrayList<ImageView> = arrayListOf()
    private var iconDataList: ArrayList<IconData>? = null
    private var onClickIcon: ((index: Int) -> Unit)? = null

    init {
        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.layout_icon_list_view, this)

        iconViews.add(imageView_01)
        iconViews.add(imageView_02)
        iconViews.add(imageView_03)
        iconViews.add(imageView_04)
        iconViews.add(imageView_05)
        iconViews.add(imageView_06)
        iconViews.add(imageView_07)

        iconViews.forEachIndexed { index, imageView ->
            imageView.onClick { onClickIcon?.invoke(index) }
        }
    }

    private fun refresh(imageView: ImageView, data: IconData) {
        imageView.setImageResource(data.iconRes)
    }

    /**
     */

    fun setData(iconDataList: ArrayList<IconData>) {
        this.iconDataList = iconDataList

        iconDataList.forEachIndexed { i, data ->
            refresh(iconViews[i], data)
        }
    }

    fun clear() {
        iconViews.forEach {
            it.invisibleFadeOut()
        }
    }

    /* index: 0 ~ 6 : 월 ~ 일 */
    fun setIcon(index: Int, imageRes: Int) {
        iconViews[index].visibleFadeIn()
        iconViews[index].setImageResource(imageRes)
    }

}
