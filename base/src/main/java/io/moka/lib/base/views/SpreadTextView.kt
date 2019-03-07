package io.moka.lib.base.views


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.util.*


class SpreadTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var textPaint: TextPaint? = null
    private var textBoundRect: Rect? = null

    private var firstX: Float = 0.toFloat()

    private var dayY: Float = 0.toFloat()

    private var xDatas = ArrayList<SpreadData>()

    init {
        initView()
    }

    private fun initView() {
        textBoundRect = Rect()

        textPaint = TextPaint()
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.isAntiAlias = true

        xDatas.add(SpreadData("-"))
        xDatas.add(SpreadData("-"))
        xDatas.add(SpreadData("-"))
        xDatas.add(SpreadData("-"))
        xDatas.add(SpreadData("-"))
        xDatas.add(SpreadData("-"))
        xDatas.add(SpreadData("-"))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val dayWidth = w / xDatas.size.toFloat()
        dayY = h / 2.0f

        firstX = dayWidth / 2

        val textSize = h / 2.5f
        textPaint!!.textSize = textSize
    }

    override fun onDraw(canvas: Canvas) {
        xDatas.forEachIndexed { index, spreadData ->
            textPaint!!.color = spreadData.color
            if (spreadData.isBold)
                textPaint!!.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            else
                textPaint!!.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            drawText(canvas, spreadData.text, textPaint, firstX + (firstX * 2 * index), dayY)
        }
    }

    private fun drawText(canvas: Canvas, text: String, textPaint: TextPaint?, centerX: Float, centerY: Float) {
        textPaint!!.getTextBounds(text, 0, text.length, textBoundRect)
        canvas.drawText(text, centerX, (centerY + textBoundRect!!.height() / 2).toInt().toFloat(), textPaint)
    }

    /*
    Setter
     */

    fun setXDatas(xDatas: ArrayList<SpreadData>) {
        this.xDatas = xDatas

        invalidate()
    }

}
