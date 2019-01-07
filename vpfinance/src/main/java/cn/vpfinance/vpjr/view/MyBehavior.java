package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyBehavior extends CoordinatorLayout.Behavior<View> {
    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }


    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        int followScrolled = target.getScrollY();
        Log.i("onScrollChange", "dxConsumed==========: "+dxConsumed);
        Log.i("onScrollChange", "dyConsumed==========: "+dyConsumed);
        Log.i("onScrollChange", "dxUnconsumed==========: "+dxUnconsumed);
        Log.i("onScrollChange", "dyUnconsumed==========: "+dyUnconsumed);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, boolean consumed) {
        int followScrolled = target.getScrollY();
        return true;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
        int followScrolled = target.getScrollY();
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        int followScrolled = target.getScrollY();
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }
}
