package io.moka.mokabaselib.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


public class SpreadTextView extends View {

    private TextPaint textPaint;
    private Rect textBoundRect;

    private float sunX;
    private float monX;
    private float tueX;
    private float wedX;
    private float thuX;
    private float friX;
    private float satX;

    private float dayY;
    private boolean isBold = false;

    private ArrayList<String> xDatas = new ArrayList<>();

    public SpreadTextView(Context context) {
        this(context, null);
    }

    public SpreadTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        textBoundRect = new Rect();

        textPaint = new TextPaint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setColor(0xFF616161);
        if (isBold)
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        xDatas.add("일");
        xDatas.add("월");
        xDatas.add("화");
        xDatas.add("수");
        xDatas.add("목");
        xDatas.add("금");
        xDatas.add("토");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float dayWidth = w / 7.0f;
        dayY = h / 2.0f;

        sunX = dayWidth / 2;
        monX = sunX + dayWidth;
        tueX = monX + dayWidth;
        wedX = tueX + dayWidth;
        thuX = wedX + dayWidth;
        friX = thuX + dayWidth;
        satX = friX + dayWidth;

        float textSize = h / 2.5f;
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas, xDatas.get(0), textPaint, sunX, dayY);
        drawText(canvas, xDatas.get(1), textPaint, monX, dayY);
        drawText(canvas, xDatas.get(2), textPaint, tueX, dayY);
        drawText(canvas, xDatas.get(3), textPaint, wedX, dayY);
        drawText(canvas, xDatas.get(4), textPaint, thuX, dayY);
        drawText(canvas, xDatas.get(5), textPaint, friX, dayY);
        drawText(canvas, xDatas.get(6), textPaint, satX, dayY);
    }

    private void drawText(Canvas canvas, String text, TextPaint textPaint, float centerX, float centerY) {
        textPaint.getTextBounds(text, 0, text.length(), textBoundRect);
        canvas.drawText(text, centerX, (int) (centerY + textBoundRect.height() / 2), textPaint);
    }

    public void setXDatas(ArrayList<String> xDatas) {
        this.xDatas = xDatas;

        invalidate();
    }

}
