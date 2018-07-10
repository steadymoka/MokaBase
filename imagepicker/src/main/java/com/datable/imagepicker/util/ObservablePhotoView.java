package com.datable.imagepicker.util;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.github.chrisbanes.photoview.PhotoView;


public class ObservablePhotoView extends PhotoView {

    private GestureDetector gestureDetector;

    private Handler handler = new Handler();
    private Runnable runnable;

    private Listener listener;

    public ObservablePhotoView(Context context) {
        this(context, null);
    }

    public ObservablePhotoView(Context context, AttributeSet attr) {
        super(context, attr);
        gestureDetector = new GestureDetector(context, new GestureListener());
        runnable = new Runnable() {

            @Override
            public void run() {

                if (null != listener)
                    listener.onOneClick();
            }

        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            handler.postDelayed(runnable, 300);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            handler.removeCallbacks(runnable);
            if (null != listener)
                listener.onDoubleClick();
            return true;
        }
    }

    public void setListener(Listener listener) {

        this.listener = listener;
    }

    public interface Listener {

        void onOneClick();

        void onDoubleClick();

    }

}
