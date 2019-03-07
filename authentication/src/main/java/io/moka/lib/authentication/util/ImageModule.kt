package io.moka.lib.authentication.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

internal fun ImageView.circle(context: Context, url: String?) {
    Glide.with(context)
            .load(url)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .into(this)
}

internal fun ImageView.circle(context: Context, resId: Int?) {
    Glide.with(context)
            .load(resId)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .into(this)
}