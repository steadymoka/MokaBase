package com.datable.imagepicker.image.viewer.adapter


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
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
