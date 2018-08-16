package io.moka.lib.imagepicker.image.viewer.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import java.util.*


class ImageViewerAdapter(fm: FragmentManager,
                         private val listOfCheckedItem: ArrayList<ImageItemData>,
                         private val mode: Int,
                         private val onImageViewerListener: ImageViewerItemFragment.OnImageViewerListener?) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return listOfCheckedItem.size
    }

    override fun getItem(position: Int): Fragment {
        return ImageViewerItemFragment.newInstance()
                .setImageUrl(listOfCheckedItem[position].imageUrl)
                .setMode(mode)
                .setPosition(position)
                .setOnImageViewerListener(onImageViewerListener)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

}
