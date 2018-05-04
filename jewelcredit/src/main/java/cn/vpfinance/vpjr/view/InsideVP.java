package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 */
public class InsideVP extends ViewPager{


    private boolean scroll = true;
    public InsideVP(Context context) {
        super(context);
    }
    public InsideVP(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (scroll){
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.onTouchEvent(ev);
        }else{
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scroll){
            return super.onInterceptTouchEvent(ev);
        }else{
            return true;
        }
    }

}
