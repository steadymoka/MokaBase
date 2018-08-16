package io.moka.lib.base.views;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;


public class LockableViewPager extends ViewPager {

    private boolean lockMode = true;

    public LockableViewPager(Context context) {
        this(context, null);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!lockMode)
            return super.onTouchEvent(event);

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!lockMode)
            return super.onInterceptTouchEvent(event);

        return false;
    }

    public void setLocked(boolean isLocked) {
        this.lockMode = isLocked;
    }

    public boolean isLocked() {
        return lockMode;
    }

}
