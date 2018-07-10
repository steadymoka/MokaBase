package com.datable.imagepicker.image.viewer


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.datable.imagepicker.R
import com.datable.imagepicker.image.viewer.adapter.ImageItemData
import com.datable.imagepicker.image.viewer.adapter.ImageViewerAdapter
import com.datable.imagepicker.image.viewer.adapter.ImageViewerItemFragment
import kotlinx.android.synthetic.main.fragment_image_detail.*
import java.util.*


class ImageViewerFragment : Fragment(), ViewPager.OnPageChangeListener {

    private var imageViewerAdapter: ImageViewerAdapter? = null
    private lateinit var imageItemDatas: ArrayList<ImageItemData>

    var currentPosition: Int = 0

    val imageCount: Int
        get() = imageItemDatas.size

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPosition = activity!!.intent.extras.getInt(ImageViewerActivity.IMAGE_POSITION, 0)

        viewPager_imageViewer.adapter = getImageViewerAdapter()
        viewPager_imageViewer.addOnPageChangeListener(this)
        viewPager_imageViewer.currentItem = currentPosition
        viewPager_imageViewer.offscreenPageLimit = imageCount
    }

    /**
     */

    fun getImageViewerAdapter(): ImageViewerAdapter {
        if (null == imageViewerAdapter)
            imageViewerAdapter = ImageViewerAdapter(activity!!.supportFragmentManager, imageItemDatas, ImageViewerItemFragment.TOUCH_MODE, null)

        return imageViewerAdapter!!
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        currentPosition = position
        //        RxBus.on().sendPageSelected(currentPosition)
        //        OttoUtil.getInstance().postInMainThread(OnDestoryImageViewer(currentPosition))
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    fun setImageItemDatas(imageItemDatas: ArrayList<ImageItemData>) {
        this.imageItemDatas = imageItemDatas
    }

}
