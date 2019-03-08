package io.moka.lib.base.util

import android.util.TypedValue
import android.widget.TextView

object FontSizeKit {

    var sizeOffSet: Int = 0

    fun size(size: Float, vararg textViews: TextView) {
        when (sizeOffSet) {
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
        when (sizeOffSet) {
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
        return when (sizeOffSet) {
            0 -> size
            1 -> size + 2
            else -> size + 3
        }
    }

}