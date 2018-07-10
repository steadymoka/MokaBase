package com.datable.imagepicker.image.viewer.adapter


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.datable.imagepicker.R
import com.datable.imagepicker.util.ObservablePhotoView
import com.datable.imagepicker.util.getHeightPixels
import com.datable.imagepicker.util.getWidthPixels
import kotlinx.android.synthetic.main.fragment_image_view_item.*


class ImageViewerItemFragment : Fragment(), ObservablePhotoView.Listener {

    private var imageUrl: String? = null
    private var position: Int? = 0
    private var mode: Int? = 3

    private var onImageViewerListener: OnImageViewerListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_image_view_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (null != savedInstanceState) {
            imageUrl = savedInstanceState.getString(KEY_IMAGE_URL)
            position = savedInstanceState.getInt(KEY_POSITION)
            mode = savedInstanceState.getInt(KEY_MODE)
        }

        initMode(mode ?: 3)
        setImage(imageUrl)
    }

    /**
     */

    private fun initMode(mode: Int) {
        when (mode) {

            TOUCH_MODE -> {
            }

            NO_TOUCH_MODE -> {

                photoView_imageItem.setZoomable(false)
                photoView_imageItem.setListener(this)
                photoView_imageItem.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    private fun setImage(imageUrl: String?) {
        spinner.visibility = View.VISIBLE

        val targetWidth = getWidthPixels(activity!!)
        val targetHeight = getHeightPixels(activity!!)

        if (null != imageUrl) {

            Glide
                    .with(activity!!)
                    .load(imageUrl)
                    .apply(RequestOptions().override(targetWidth, targetHeight))
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            spinner.visibility = View.GONE
                            if (0 != position)
                                photoView_imageItem.scaleType = ImageView.ScaleType.FIT_CENTER

                            return true
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            spinner.visibility = View.GONE
                            photoView_imageItem.scaleType = ImageView.ScaleType.FIT_CENTER
                            photoView_imageItem.setImageResource(R.drawable.image_on_fail_full)
                            return true
                        }
                    })
                    .into(photoView_imageItem)
        }
    }

    /**
     * onClick Listener
     */

    override fun onOneClick() {
        onImageViewerListener?.onClickImage(position ?: 0)
    }

    override fun onDoubleClick() {
        onImageViewerListener?.onDoubleClickImage(position ?: 0)
    }

    /**
     */

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_IMAGE_URL, imageUrl)
        outState.putInt(KEY_POSITION, position ?: 0)
        outState.putInt(KEY_MODE, mode ?: 3)
    }

    fun setImageUrl(imageUrl: String): ImageViewerItemFragment {
        this.imageUrl = imageUrl
        return this
    }

    fun setPosition(position: Int): ImageViewerItemFragment {
        this.position = position
        return this
    }

    fun setMode(mode: Int): ImageViewerItemFragment {
        this.mode = mode
        return this
    }

    fun setOnImageViewerListener(onImageViewerListener: OnImageViewerListener?): ImageViewerItemFragment {
        this.onImageViewerListener = onImageViewerListener
        return this
    }

    /**
     * 뷰페이저의 아이템프레그먼트에서 photoview 의 리스너이다.
     * mode 가 No_Touch_Mode - 원클릭과, 더블클릭 이벤트를 받아서 처리해야한다.
     * mode 가 Touch_Mode - 리스너 등록 할필요 없다.
     */

    interface OnImageViewerListener {

        fun onClickImage(position: Int)

        fun onDoubleClickImage(position: Int)

    }

    companion object {

        private val KEY_IMAGE_URL = "ImageViewerItemFragment.KEY_IMAGE_URL"
        private val KEY_POSITION = "ImageViewerItemFragment.KEY_POSITION"
        private val KEY_MODE = "ImageViewerItemFragment.KEY_MODE"

        val TOUCH_MODE = 2
        val NO_TOUCH_MODE = 3

        fun newInstance(): ImageViewerItemFragment = ImageViewerItemFragment()

    }

}
