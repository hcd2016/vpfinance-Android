package cn.vpfinance.vpjr.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.util.StatusBarCompat1;

/**
 */
public class ActionBarPullDownLayout extends RelativeLayout {

    private Context mContext;

    private TextView mTitle;

    private ImageView mHeadBack;

    private TextView mActionLeft;

    private TextView mActionRight;

    private ImageView mImageButtonRight;

    private ImageView title_state;

    private boolean isShow = false;
    private RelativeLayout              mToolbarView;
    private RadioGroup                  radio_group;
    private View mFakeStatusBar;

    public ActionBarPullDownLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }
    public ActionBarPullDownLayout setFakeStatusBar(boolean isNeed){
        if (isNeed){
            mFakeStatusBar.setVisibility(VISIBLE);
            ViewGroup.LayoutParams layoutParams = mFakeStatusBar.getLayoutParams();
            layoutParams.height = StatusBarCompat1.getStatusBarHeight(mContext);
            mFakeStatusBar.setLayoutParams(layoutParams);
        }
        return this;
    }

    private void initView() {
        View.inflate(mContext, R.layout.layout_header_pulldown, this);
        mFakeStatusBar = findViewById(R.id.fake_status_bar);
        mTitle = (TextView) findViewById(R.id.title);
        mHeadBack = (ImageView) findViewById(R.id.headBack);
        mActionLeft = (TextView) findViewById(R.id.actionLeft);
        mActionRight = (TextView) findViewById(R.id.actionRight);
        mImageButtonRight = (ImageView) findViewById(R.id.imageButtonRight);
        title_state = (ImageView) findViewById(R.id.title_state);
        title_state.setBackgroundResource(R.drawable.arrow_down_state);
        mToolbarView = ((RelativeLayout) findViewById(R.id.toolbarView));
        radio_group = ((RadioGroup) findViewById(R.id.radio_group));

        mTitle.setVisibility(VISIBLE);
        mHeadBack.setVisibility(GONE);
        mActionLeft.setVisibility(GONE);
        mActionRight.setVisibility(GONE);
        mImageButtonRight.setVisibility(GONE);
        title_state.setVisibility(VISIBLE);
        radio_group.setVisibility(GONE);

        mHeadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof Activity) {
                    ((Activity) mContext).onBackPressed();
                }
            }
        });

    }
    public ActionBarPullDownLayout setColor(int color){
        mToolbarView.setBackgroundColor(color);
        return this;
    }

    public ActionBarPullDownLayout setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    public ActionBarPullDownLayout showPullDown(boolean isShow){
        this.isShow = isShow;
        title_state.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (!isShow){
            mTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            title_state.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        return this;
    }

    public ActionBarPullDownLayout setActionPullDown(View.OnClickListener pullDownAction){
        if (isShow){
            mTitle.setOnClickListener(pullDownAction);
            title_state.setOnClickListener(pullDownAction);
        }
        return this;
    }

    public ActionBarPullDownLayout setPullDownToUp(boolean isUp,String title){
        if (isShow){
            if (isUp){
                title_state.setBackgroundResource(R.drawable.arrow_up_state);
            }else{
                title_state.setBackgroundResource(R.drawable.arrow_down_state);
            }
            setTitle(title);
        }
        return this;
    }

    public ActionBarPullDownLayout setActionLeft(String actionCap, View.OnClickListener actionCallback) {
        if (!TextUtils.isEmpty(actionCap)) {
            mActionLeft.setVisibility(VISIBLE);
            mActionLeft.setText(actionCap);
            if (actionCallback != null) {
                mActionLeft.setOnClickListener(actionCallback);
            }
        }
        return this;
    }

    public ActionBarPullDownLayout setActionRight(String actionCap, View.OnClickListener actionCallback) {
        if (!TextUtils.isEmpty(actionCap)) {
            mActionRight.setVisibility(VISIBLE);
            mActionRight.setText(actionCap);
            if (actionCallback != null) {
                mActionRight.setOnClickListener(actionCallback);
            }
        }
        return this;
    }

    public ActionBarPullDownLayout setImageButtonRight(int resid, View.OnClickListener actionCallback) {
        if (actionCallback != null) {
            mImageButtonRight.setVisibility(VISIBLE);
            mImageButtonRight.setImageResource(resid);
            mImageButtonRight.setOnClickListener(actionCallback);
        }
        return this;
    }

    public ActionBarPullDownLayout setImageButtonLeft(int resid, View.OnClickListener actionCallback) {
        if (actionCallback != null) {
            mHeadBack.setVisibility(VISIBLE);
            mHeadBack.setImageResource(resid);
            mHeadBack.setOnClickListener(actionCallback);
        }
        return this;
    }

    public ActionBarPullDownLayout setHeadBackVisible(int visible) {
        setViewVisible(mHeadBack, visible);
        return this;
    }

    public ActionBarPullDownLayout setActionLeftVisible(int visible) {
        setViewVisible(mActionLeft, visible);
        return this;
    }

    public ActionBarPullDownLayout setActionRightVisible(int visible) {
        setViewVisible(mActionRight, visible);
        return this;
    }

    public ActionBarPullDownLayout setImageButtonRightVisible(int visible) {
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

    public ActionBarPullDownLayout reset() {
        mTitle.setVisibility(VISIBLE);
        mHeadBack.setVisibility(GONE);
        mActionLeft.setVisibility(GONE);
        mActionRight.setVisibility(GONE);
        mImageButtonRight.setVisibility(GONE);
        title_state.setVisibility(GONE);
        radio_group.setVisibility(GONE);
        radio_group.check(R.id.treasure_btn);
        showPullDown(false);
        return this;
    }

    public ActionBarPullDownLayout switchTab(int tab){
        if (tab == 1){
            radio_group.check(R.id.treasure_btn);
        }else if (tab == 2){
            radio_group.check(R.id.transfer_btn);
        }
        return this;
    }

    public ActionBarPullDownLayout setRadioGroupCheckedListener(RadioGroup.OnCheckedChangeListener listener){
        if (radio_group.getVisibility() == VISIBLE){
           radio_group.setOnCheckedChangeListener(listener);
        }
        return this;
    }

    public ActionBarPullDownLayout setRadioGroupVisible(int visible) {
        setViewVisible(radio_group, visible);
        return this;
    }
}
