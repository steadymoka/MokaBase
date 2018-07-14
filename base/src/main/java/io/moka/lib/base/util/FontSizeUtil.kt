package io.moka.lib.base.util

import android.util.TypedValue
import android.widget.TextView
import io.moka.lib.base.MokaBase

object FontSizeUtil {

    fun size(size: Float, vararg textViews: TextView) {
        when (MokaBase.fontSizeFlag) {
            0 -> textViews.forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
            }

            1 -> textViews.forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 1.6f)
            }

            else -> textViews.forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 2.5f)
            }
        }
    }

    fun size(size: Float, isHalf: Boolean, vararg textViews: TextView) {
        when (MokaBase.fontSizeFlag) {
            0 -> textViews.forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
            }

            1 -> textViews.forEach {
                if (isHalf)
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 1)
                else
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 2)
            }

            else -> textViews.forEach {
                if (isHalf)
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 1.6f)
                else
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 2.5f)
            }
        }
    }

    fun foSize(size: Float): Float {
        return when (MokaBase.fontSizeFlag) {
            0 -> size
            1 -> size + 2
            else -> size + 3
        }
    }

}