package com.jewelcredit.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jewelcredit.util.Utils;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.util.StatusBarCompat1;
import cn.vpfinance.vpjr.view.ActionBarPullDownLayout;


/**
 * 标题栏封装接口，默认只显示了标题。
 */
public class ActionBarLayout extends RelativeLayout {

    private Context mContext;

    private TextView mTitle;

    private ImageView mHeadBack;

    private TextView mActionLeft;

    private TextView mActionRight;

    private ImageView mImageButtonRight;

    private RelativeLayout mToolbarView;

    private View mFakeStatusBar;

    public static final int TYPE_HIDDEN = 0;
    public static final int TYPE_DOWN = 1;
    public static final int TYPE_UP = 2;

    public ActionBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        View.inflate(mContext, R.layout.layout_header, this);
        mTitle = (TextView) findViewById(R.id.title);
        mFakeStatusBar = findViewById(R.id.fake_status_bar);
        mHeadBack = (ImageView) findViewById(R.id.headBack);
        mActionLeft = (TextView) findViewById(R.id.actionLeft);
        mActionRight = (TextView) findViewById(R.id.actionRight);
        mImageButtonRight = (ImageView) findViewById(R.id.imageButtonRight);
        mToolbarView = ((RelativeLayout) findViewById(R.id.toolbarView));

        mTitle.setVisibility(VISIBLE);
        mHeadBack.setVisibility(GONE);
        mActionLeft.setVisibility(GONE);
        mActionRight.setVisibility(GONE);
        mImageButtonRight.setVisibility(GONE);

        mHeadBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof Activity) {
                    ((Activity) mContext).onBackPressed();
                }
            }
        });
        setFakeStatusBar(true);
    }

    public ActionBarLayout setFakeStatusBar(boolean isNeed){
        if (isNeed){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mFakeStatusBar.setVisibility(VISIBLE);
                ViewGroup.LayoutParams layoutParams = mFakeStatusBar.getLayoutParams();
                layoutParams.height = StatusBarCompat1.getStatusBarHeight(mContext);
                mFakeStatusBar.setLayoutParams(layoutParams);
            }
        }else{
            mFakeStatusBar.setVisibility(GONE);
        }
        return this;
    }

    public ActionBarLayout setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    public ActionBarLayout setTitleClick(OnClickListener listener){
        mTitle.setEnabled(true);
        mTitle.setOnClickListener(listener);
        return this;
    }

    /**
     *
     * @param status 0 隐藏, 1 箭头向下, 2 箭头向上
     * @return
     */
    public ActionBarLayout setUpDown(int status){
        switch (status){
            case TYPE_HIDDEN:
                mTitle.setCompoundDrawables(null,null,null,null);
                break;
            case TYPE_DOWN:
                Drawable downDrawable = ContextCompat.getDrawable(mContext, R.drawable.toolbar_down);
                downDrawable.setBounds(0,0,downDrawable.getMinimumWidth(), downDrawable.getMinimumHeight());
                mTitle.setCompoundDrawables(null,null,downDrawable,null);
                break;
            case TYPE_UP:
                Drawable upDrawable = ContextCompat.getDrawable(mContext, R.drawable.toolbar_up);
                upDrawable.setBounds(0,0,upDrawable.getMinimumWidth(), upDrawable.getMinimumHeight());
//                mTitle.setCompoundDrawablePadding(Utils.dip2px(mContext,));
                mTitle.setCompoundDrawables(null,null,upDrawable,null);
                break;
        }
        return this;
    }



    public ActionBarLayout setColor(int color){
        mToolbarView.setBackgroundColor(color);
        mFakeStatusBar.setBackgroundColor(color);
        return this;
    }

    public ActionBarLayout setTransparent(){
        mToolbarView.setBackgroundColor(Color.TRANSPARENT);
        mFakeStatusBar.setBackgroundColor(Color.TRANSPARENT);
        return this;
    }

    public ActionBarLayout setActionLeft(String actionCap, OnClickListener actionCallback) {
        if (!TextUtils.isEmpty(actionCap)) {
            mActionLeft.setVisibility(VISIBLE);
            mActionLeft.setText(actionCap);
            if (actionCallback != null) {
                mActionLeft.setOnClickListener(actionCallback);
            }
        }
        return this;
    }

    public ActionBarLayout setActionLeftGone() {
        mActionLeft.setVisibility(GONE);
        return this;
    }

    public ActionBarLayout setActionRight(String actionCap, OnClickListener actionCallback) {
        if (!TextUtils.isEmpty(actionCap)) {
            mActionRight.setVisibility(VISIBLE);
            mActionRight.setText(actionCap);
            if (actionCallback != null) {
                mActionRight.setOnClickListener(actionCallback);
            }
        }
        return this;
    }

    public ActionBarLayout setImageButtonRight(int resid, OnClickListener actionCallback) {
        if (actionCallback != null) {
            mImageButtonRight.setVisibility(VISIBLE);
            mImageButtonRight.setImageResource(resid);
            mImageButtonRight.setOnClickListener(actionCallback);
        }
        return this;
    }

    public ActionBarLayout setImageButtonLeft(int resid, OnClickListener actionCallback) {
        if (actionCallback != null) {
            mHeadBack.setVisibility(VISIBLE);
            mHeadBack.setImageResource(resid);
            mHeadBack.setOnClickListener(actionCallback);
        }
        return this;
    }

    public ActionBarLayout setHeadBackVisible(int visible) {
        setViewVisible(mHeadBack, visible);
        return this;
    }

    public ActionBarLayout setHeadBackLinstener(OnClickListener actionCallback) {
        setViewVisible(mHeadBack, VISIBLE);
        mHeadBack.setOnClickListener(actionCallback);
        return this;
    }

    public ActionBarLayout setActionLeftVisible(int visible) {
        setViewVisible(mActionLeft, visible);
        return this;
    }

    public ActionBarLayout setActionRightVisible(int visible) {
        setViewVisible(mActionRight, visible);
        return this;
    }

    public ActionBarLayout setImageButtonRightVisible(int visible) {
        setViewVisible(mImageButtonRight, visible);
        return this;
    }

    private void setViewVisible(View view, int visible) {
        if (visible == VISIBLE) {
            view.setVisibility(VISIBLE);
        } else if (visible == GONE) {
            view.setVisibility(GONE);
        }
    }

    public ActionBarLayout reset() {
        mTitle.setVisibility(VISIBLE);
        mTitle.setEnabled(false);
        mHeadBack.setVisibility(GONE);
        mActionLeft.setVisibility(GONE);
        mActionRight.setVisibility(GONE);
        mImageButtonRight.setVisibility(GONE);
        setColor(getResources().getColor(R.color.main_color));
        setUpDown(TYPE_HIDDEN);
        return this;
    }
}
