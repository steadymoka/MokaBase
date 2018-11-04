package io.moka.lib.base.util

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.moka.lib.base.adapter.BaseAdapter
import io.moka.lib.base.adapter.ItemData
import io.moka.lib.base.adapter.RecyclerItemView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor

fun <T : ItemData, V : RecyclerItemView<T>> RecyclerView.init(context: Context, adapter: BaseAdapter<T, V>) {
    this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    this.adapter = adapter
}

fun View.collapse(duration: Int, startHeight: Int, targetHeight: Int) {
    val valueAnimator = ValueAnimator.ofFloat(startHeight.toFloat(), targetHeight.toFloat())
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.addUpdateListener { animation ->
        val animatedValue = animation.animatedValue as Float
        this.layoutParams.height = animatedValue.toInt()
        this.post { this.requestLayout() }
    }
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration.toLong()
    valueAnimator.start()
}

fun View.expand(duration: Int, startHeight: Int, targetHeight: Int) {
    this.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    this.layoutParams.height = startHeight
    this.visibility = View.VISIBLE
    val valueAnimator = ValueAnimator.ofFloat(startHeight.toFloat(), targetHeight.toFloat())
    valueAnimator.addUpdateListener { animation ->
        val animatedValue = animation.animatedValue as Float
        this.layoutParams.height = animatedValue.toInt()
        this.post { this.requestLayout() }
    }
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration.toLong()
    valueAnimator.start()
}

inline fun View.visible() {
    this.visibility = View.VISIBLE
}

inline fun View.gone() {
    this.visibility = View.GONE
}

inline fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visibleFadeIn(duaration: Long = 0L, onFinish: (() -> Unit)? = null) {
    this.visible()
    val fadeInAnimation = AlphaAnimation(0f, 1f)
    fadeInAnimation.interpolator = AccelerateInterpolator()
    fadeInAnimation.duration = duaration

    fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            onFinish?.invoke()
        }

        override fun onAnimationStart(p0: Animation?) {
        }
    })
    postDelayed({ this.clearAnimation() }, fadeInAnimation.duration)
    this.startAnimation(fadeInAnimation)
}

fun View.invisibleFadeOut(duaration: Long = 0L, onFinish: (() -> Unit)? = null) {
    this.invisible()

    val fadeOutAnimation = AlphaAnimation(1f, 0f)
    fadeOutAnimation.interpolator = AccelerateInterpolator()
    fadeOutAnimation.duration = duaration

    fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            onFinish?.invoke()
        }

        override fun onAnimationStart(p0: Animation?) {
        }
    })
    postDelayed({ this.clearAnimation() }, fadeOutAnimation.duration)
    this.startAnimation(fadeOutAnimation)
}

fun View.goneFadeOut(duaration: Long = 0L, onFinish: (() -> Unit)? = null) {
    this.gone()

    val fadeOutAnimation = AlphaAnimation(1f, 0f)
    fadeOutAnimation.interpolator = AccelerateInterpolator()
    fadeOutAnimation.duration = duaration

    fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            onFinish?.invoke()
        }

        override fun onAnimationStart(p0: Animation?) {
        }
    })
    postDelayed({ this.clearAnimation() }, fadeOutAnimation.duration)
    this.startAnimation(fadeOutAnimation)
}

fun toGone(vararg views: View) {
    views.forEach { it.gone() }
}

fun toInvisible(vararg views: View) {
    views.forEach { it.invisible() }
}

fun toInVisibleFadeOut(vararg views: View) {
    views.forEach {
        if (it.visibility == View.VISIBLE)
            it.invisibleFadeOut()
    }
}

fun toVisible(vararg views: View) {
    views.forEach { it.visible() }
}

fun toVisibleFadeIn(vararg views: View) {
    views.forEach { it.visibleFadeIn() }
}

fun View.visibleOrGone(isVisible: Boolean) {
    if (isVisible)
        this.visible()
    else
        this.gone()
}

fun EditText.toCursorLast() {
    this.setSelection(this.text.length)
}

fun RecyclerView.fitHeight() {
    val params = layoutParams
    params.height = layoutManager!!.height

    layoutParams = params
}

fun View.leftMargin(dp: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin = dp
}

fun View.rightMargin(dp: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin = dp
}

fun View.topMargin(dp: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = dp
}

fun View.bottomMargin(dp: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = dp
}

/**
 * click
 *
 */

fun View.onClick(action: suspend (View) -> Unit) {
    val event = GlobalScope.actor<View>(Dispatchers.Main) {
        for (event in channel) {
            action(event)
        }
    }

    setOnClickListener {
        event.offer(it)
    }
}

