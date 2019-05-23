package io.moka.lib.base.util

import android.util.TypedValue
import android.widget.TextView

object FontSizeCompat {

    enum class OffSet(var flag: Int = 0) {
        NORMAL(0), SMALL(1), BIG(2);

        companion object {
            fun get(flag: Int): OffSet {
                return OffSet.values().filter { it.flag == flag }[0]
            }
        }
    }

    var offset: OffSet = FontSizeCompat.OffSet.SMALL

    /**
     * 기본은 SMALL 로 설정 한다.
     * 텍스트의 기본 크기는 '12dp' 로 설정 한다
     * 설정에서 1단계, 2단계, 3단계 로 크기를 키울수 있다.  + 1.6 / + 2.5
     */

    fun set(offset: OffSet) {
        this.offset = offset
    }

    fun set(flag: Int) {
        this.offset = OffSet.get(flag)
    }

    fun size(size: Float, vararg textViews: TextView) {
        when (offset) {
            FontSizeCompat.OffSet.SMALL -> textViews.forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
            }

            FontSizeCompat.OffSet.NORMAL -> textViews.forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 1.6f)
            }

            FontSizeCompat.OffSet.BIG -> textViews.forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 2.5f)
            }
        }
    }

    fun size(size: Float, isHalf: Boolean, vararg textViews: TextView) {
        when (offset) {
            FontSizeCompat.OffSet.SMALL -> textViews.forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
            }

            FontSizeCompat.OffSet.NORMAL -> textViews.forEach {
                if (isHalf)
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 1)
                else
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 1.6f)
            }

            FontSizeCompat.OffSet.BIG -> textViews.forEach {
                if (isHalf)
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 2)
                else
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size + 2.5f)
            }
        }
    }

    fun foSize(size: Float): Float {
        return when (offset) {
            FontSizeCompat.OffSet.SMALL -> size
            FontSizeCompat.OffSet.NORMAL -> size + 1.6f
            FontSizeCompat.OffSet.BIG -> size + 2.5f
        }
    }

}