package io.moka.mokabaselib.util

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import io.moka.mokabaselib.MokaBase

fun color(resId: Int): Int {
    return ContextCompat.getColor(MokaBase.context!!, resId)
}

/**
 * first : text
 * second : ratio
 * third : first - color, second - alpha
 */
fun spannableText(vararg triple: Triple<String, Float, Pair<Int, Float>>): CharSequence {
    val spannableStringBuilder = SpannableStringBuilder()

    triple.forEach {
        val text = it.first
        val ratio = it.second
        val color = it.third.first
        val alpha = it.third.second

        val textSpannable = SpannableString(text)
        textSpannable.setSpan(ForegroundColorSpan(Color.argb((alpha * 255).toInt(), Color.red(color), Color.green(color), Color.blue(color))), 0, textSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textSpannable.setSpan(RelativeSizeSpan(ratio), 0, textSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.append(textSpannable)
    }

    return spannableStringBuilder
}

fun attr(text: String, ratio: Float = 1f, colorRes: Int, alpha: Float = 1f): Triple<String, Float, Pair<Int, Float>> {
    return Triple(text, ratio, Pair(color(colorRes), alpha))
}