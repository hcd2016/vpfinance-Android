package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.nineoldandroids.view.ViewHelper;

import cn.vpfinance.vpjr.view.wheelcity.WheelScroller;


/**
 */
public class HideTitleScrollView extends NestedScrollView{
    private View view;

    public HideTitleScrollView(Context context) {
        super(context);
    }

    public HideTitleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HideTitleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null){
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    private ScrollViewListener scrollViewListener;
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public interface ScrollViewListener{
        void onScrollChanged(HideTitleScrollView scrollView, int x, int y, int oldx, int oldy);
    }

}
