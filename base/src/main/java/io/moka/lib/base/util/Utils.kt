package io.moka.lib.base.util

import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import io.moka.lib.base.MokaBase

fun color(resId: Int): Int {
    return ContextCompat.getColor(MokaBase.context!!, resId)
}

/**
 * first : first - text, second - isBold
 * second : ratio
 * third : first - color, second - alpha
 */
fun spannableText(vararg triple: Triple<Pair<String, Boolean>, Float, Pair<Int?, Float>>): CharSequence {
    val spannableStringBuilder = SpannableStringBuilder()

    triple.forEach {
        val text = it.first.first
        val isBold = it.first.second
        val ratio = it.second
        val color = it.third.first
        val alpha = it.third.second

        val textSpannable = SpannableString(text)

        /* color */
        if (null != color)
            textSpannable.setSpan(ForegroundColorSpan(Color.argb((alpha * 255).toInt(), Color.red(color), Color.green(color), Color.blue(color))), 0, textSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        /* ratio */
        textSpannable.setSpan(RelativeSizeSpan(ratio), 0, textSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        /* bold */
        if (isBold)
            textSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, textSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableStringBuilder.append(textSpannable)
    }

    return spannableStringBuilder
}

fun attr(text: String, ratio: Float = 1f, colorRes: Int? = null, alpha: Float = 1f, isBold: Boolean = false): Triple<Pair<String, Boolean>, Float, Pair<Int?, Float>> {
    return if (null != colorRes)
        Triple(Pair(text, isBold), ratio, Pair(color(colorRes), alpha))
    else
        Triple(Pair(text, isBold), ratio, Pair(null, alpha))
}