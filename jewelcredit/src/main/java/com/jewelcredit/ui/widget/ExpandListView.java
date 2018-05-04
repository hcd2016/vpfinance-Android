package com.jewelcredit.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ListView;

import com.jewelcredit.animation.ExpandCollapseAnimation;

public class ExpandListView extends ListView {

    private static final int ANIMATION_DURATION = 350;

    private int mLastOpenPosition = -1;
    private View mLastOpenView = null;

    public ExpandListView(Context context) {
        this(context, null);
    }

    public ExpandListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void startExpandAnimation(View target, int position) {
        if (-1 == mLastOpenPosition) {
            Animation expandAnim = new ExpandCollapseAnimation(target, ExpandCollapseAnimation.EXPAND);
            expandAnim.setDuration(ANIMATION_DURATION);
            target.startAnimation(expandAnim);
            mLastOpenPosition = position;
            mLastOpenView = target;

            return;
        }

        if (mLastOpenPosition == position) {
            if (View.VISIBLE == target.getVisibility()) {
                Animation anim = new ExpandCollapseAnimation(target, ExpandCollapseAnimation.COLLAPSE);
                anim.setDuration(ANIMATION_DURATION);
                target.startAnimation(anim);
                mLastOpenView = null;
            } else if (View.INVISIBLE == target.getVisibility()) {
                Animation anim = new ExpandCollapseAnimation(target, ExpandCollapseAnimation.EXPAND);
                anim.setDuration(ANIMATION_DURATION);
                target.startAnimation(anim);
            }
        } else {
            if (null != mLastOpenView && mLastOpenPosition >= getFirstVisiblePosition()
                    && mLastOpenPosition <= getLastVisiblePosition()) {
                Animation collapseAnim = new ExpandCollapseAnimation(mLastOpenView, ExpandCollapseAnimation.COLLAPSE);
                collapseAnim.setDuration(ANIMATION_DURATION);
                mLastOpenView.startAnimation(collapseAnim);
            }

            Animation expandAnim = new ExpandCollapseAnimation(target, ExpandCollapseAnimation.EXPAND);
            expandAnim.setDuration(ANIMATION_DURATION);
            target.startAnimation(expandAnim);
            mLastOpenPosition = position;
            mLastOpenView = target;
        }
    }
}
