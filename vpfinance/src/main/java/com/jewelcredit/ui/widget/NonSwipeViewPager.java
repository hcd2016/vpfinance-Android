package com.jewelcredit.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Wang Gensheng on 2015/4/28.
 */
public class NonSwipeViewPager extends ViewPager {

    private boolean swipeAble = false;

    public void setSwipeAble(boolean swipeAble) {
        this.swipeAble = swipeAble;
    }

    public NonSwipeViewPager(Context context) {
        super(context);
    }

    public NonSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return swipeAble && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return swipeAble && super.onTouchEvent(ev);
    }
}
