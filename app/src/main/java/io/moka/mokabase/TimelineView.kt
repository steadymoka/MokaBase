package io.moka.mokabase

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import io.moka.lib.base.util.dp2px
import java.text.SimpleDateFormat
import java.util.*


fun timeStringToTimeline(timeString: String): Long { // ex) timeString : "0812"
    if (timeString.count() != 4) {
        return 0
    }

    val hour = timeString.substring(0, 1).toLong()
    val minute = timeString.substring(2, 3).toLong()

    return hour * 3600 + minute * 60
}

class TimelineView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private var datas: ArrayList<Data>? = null
    var date: Date = Date()

    data class Data(
            var startTimeline: Long, // 단위: second
            var endTimeline: Long
    ) {

        fun getDuration(): String {
            val durationSecond = endTimeline - startTimeline
            return String.format("%dh %02dm", durationSecond / 60, durationSecond % 60)
        }

    }

    /*

    0 ~ 86,400 second

     */

    private var progressPaint = Paint()
    private var backgroundPaint = Paint()
    private var rectF = RectF()

    init {
        progressPaint.color = 0xFFF48383.toInt()
        backgroundPaint.color = 0xFFE9E9E9.toInt()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (null == datas || datas!!.count() == 0)
            return

        /* 가로, 세로 길이 설정 */
        val width = measuredWidth
        val height = measuredHeight
        val barHeight = dp2px(16.0)

        /* */
        val offset = datas!!.first().startTimeline
        val endOfTotalTimeline = if (isToday()) {
            if (todayTimeline < datas!!.last().endTimeline)
                datas!!.last().endTimeline - offset
            else
                todayTimeline - offset
        }
        else {
            datas!!.last().endTimeline - offset
        }

        rectF.left = 0.toFloat()
        rectF.right = width.toFloat()
        rectF.top = (height / 2 - (barHeight / 2)).toFloat()
        rectF.bottom = (height / 2 + (barHeight / 2)).toFloat()
        canvas!!.drawRoundRect(rectF, 5f, 5f, backgroundPaint)

        datas!!.forEachIndexed { index, data ->
            rectF.left = ((data.startTimeline - offset).toFloat() / endOfTotalTimeline.toFloat()) * width
            rectF.right = ((data.endTimeline - offset).toFloat() / endOfTotalTimeline.toFloat()) * width
            rectF.top = (height / 2 - (barHeight / 2)).toFloat()
            rectF.bottom = (height / 2 + (barHeight / 2)).toFloat()

            canvas.drawRoundRect(rectF, 5f, 5f, progressPaint)
        }
    }

    /* */

    private val formatter by lazy { SimpleDateFormat("HHmm") }
    private val today by lazy { Calendar.getInstance().time }
    private val todayTimeline by lazy { timeStringToTimeline(formatter.format(today)) }

    private fun isToday(): Boolean {
        return formatter.format(today) == formatter.format(date)
    }

    fun setDatas(datas: ArrayList<Data>) {
        this.datas = datas
        invalidate()
    }

}
