package io.moka.authentication.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import io.moka.lib.imagepicker.util.GlideApp

fun ImageView.circle(context: Context, url: String?) {
    GlideApp.with(context)
            .load(url)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .into(this)
}

fun ImageView.circle(context: Context, resId: Int?) {
    GlideApp.with(context)
            .load(resId)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .into(this)
}