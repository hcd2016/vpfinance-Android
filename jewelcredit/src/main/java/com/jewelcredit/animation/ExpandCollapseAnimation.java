package com.jewelcredit.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ExpandCollapseAnimation extends Animation {

    public static final int COLLAPSE = 1; // view 收起
    public static final int EXPAND = 0; // view展开

    private View mAnimatedView = null;
    private int mEndHeight = 0;
    private LinearLayout.LayoutParams mLayoutParams = null;
    private int mType = EXPAND;

    public ExpandCollapseAnimation(View view, int type) {
        mAnimatedView = view;
        mEndHeight = mAnimatedView.getMeasuredHeight();
        mLayoutParams = ((LinearLayout.LayoutParams) view.getLayoutParams());
        mType = type;
        if (EXPAND == mType) {
            mLayoutParams.bottomMargin = -mEndHeight;
        } else if (COLLAPSE == mType) {
            mLayoutParams.bottomMargin = 0;
        }
        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
            if (EXPAND == mType) {
                mLayoutParams.bottomMargin = -mEndHeight + (int) (mEndHeight * interpolatedTime);
            } else if (COLLAPSE == mType) {
                mLayoutParams.bottomMargin = -(int) (mEndHeight * interpolatedTime);
            }
            mAnimatedView.requestLayout();
        } else {
            if (EXPAND == mType) {
                mLayoutParams.bottomMargin = 0;
                mAnimatedView.requestLayout();
            } else if (COLLAPSE == mType) {
                mLayoutParams.bottomMargin = -mEndHeight;
                mAnimatedView.setVisibility(View.INVISIBLE);
                mAnimatedView.requestLayout();
            }
        }
    }

}
