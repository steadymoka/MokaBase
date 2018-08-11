package io.moka.lib.base.handler;


import android.view.View;

import java.util.ArrayList;


public class VisibilityHandler {

    private ArrayList<View> visibleViews;
    private View visibleView;

    private ArrayList<View> ableViews;
    private View enableView;

    public VisibilityHandler() {
        visibleViews = new ArrayList<>();
        ableViews = new ArrayList<>();
    }

    public void addView(View view) {
        if (null != view) {
            view.setVisibility(View.INVISIBLE);
            visibleViews.add(view);
        }
    }

    public void addableView(View view) {
        if (null != view) {
            view.setEnabled(false);
            ableViews.add(view);
        }
    }

    public boolean removeView(View view) {
        return null != view && visibleViews.remove(view);
    }

    public boolean setVisible(View view) {
        if (null != view && visibleViews.contains(view)) {
            if (null != visibleView)
                visibleView.setVisibility(View.INVISIBLE);

            view.setVisibility(View.VISIBLE);
            visibleView = view;

            return true;
        }
        else {
            return false;
        }
    }

    public boolean setEnable(View view) {
        if (null != view && ableViews.contains(view)) {
            if (null != enableView)
                enableView.setEnabled(false);

            view.setEnabled(true);
            enableView = view;

            return true;
        }
        else {
            return false;
        }
    }

}
