package com.jewelcredit.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.util.StatusBarCompat1;


/**
 * 标题栏封装接口，默认只显示了标题。
 */
public class ActionBarWhiteLayout extends RelativeLayout {

    private Context mContext;

    private TextView mTitle;

    private ImageView mHeadBack;

    private TextView mActionLeft;

    private TextView mActionRight;

    private ImageView mImageButtonRight;

    private RelativeLayout mToolbarView;

    private View mFakeStatusBar;

    public ActionBarWhiteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        View.inflate(mContext, R.layout.layout_header_white, this);
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

    public ActionBarWhiteLayout setFakeStatusBar(boolean isNeed) {
        if (isNeed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mFakeStatusBar.setVisibility(VISIBLE);
                ViewGroup.LayoutParams layoutParams = mFakeStatusBar.getLayoutParams();
                layoutParams.height = StatusBarCompat1.getStatusBarHeight(mContext);
                mFakeStatusBar.setLayoutParams(layoutParams);
            }
            //白底黑字
            try{
                StatusBarLightMode(((Activity) mContext));
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            mFakeStatusBar.setVisibility(GONE);
        }
        return this;
    }

    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity, true)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            }
        }
        return result;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * * 可以用来判断是否为Flyme用户
     * * @param window 需要设置的窗口
     * * @param dark 是否把状态栏文字及图标颜色设置为深色
     * * @return boolean 成功执行返回true *
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            } catch (Exception e) {

            }
        }
        return result;
    }

    public ActionBarWhiteLayout setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    public ActionBarWhiteLayout setColor(int color) {
        mToolbarView.setBackgroundColor(color);
        mFakeStatusBar.setBackgroundColor(color);
        return this;
    }

    public ActionBarWhiteLayout setTransparent() {
        mToolbarView.setBackgroundColor(Color.TRANSPARENT);
        mFakeStatusBar.setBackgroundColor(Color.TRANSPARENT);
        return this;
    }

    public ActionBarWhiteLayout setActionLeft(String actionCap, OnClickListener actionCallback) {
        if (!TextUtils.isEmpty(actionCap)) {
            mActionLeft.setVisibility(VISIBLE);
            mActionLeft.setText(actionCap);
            if (actionCallback != null) {
                mActionLeft.setOnClickListener(actionCallback);
            }
        }
        return this;
    }

    public ActionBarWhiteLayout setActionRight(String actionCap) {
        if (!TextUtils.isEmpty(actionCap)) {
            mActionRight.setVisibility(VISIBLE);
            mActionRight.setText(actionCap);
        }
        return this;
    }

    public ActionBarWhiteLayout setActionRight(String actionCap, OnClickListener actionCallback) {
        if (!TextUtils.isEmpty(actionCap)) {
            mActionRight.setVisibility(VISIBLE);
            mActionRight.setText(actionCap);
            if (actionCallback != null) {
                mActionRight.setOnClickListener(actionCallback);
            }
        }
        return this;
    }

    public ActionBarWhiteLayout setImageButtonRight(int resid, OnClickListener actionCallback) {
        if (actionCallback != null) {
            mImageButtonRight.setVisibility(VISIBLE);
            mImageButtonRight.setImageResource(resid);
            mImageButtonRight.setOnClickListener(actionCallback);
        }
        return this;
    }

    public ActionBarWhiteLayout setImageButtonLeft(int resid, OnClickListener actionCallback) {
        if (actionCallback != null) {
            mHeadBack.setVisibility(VISIBLE);
            mHeadBack.setImageResource(resid);
            mHeadBack.setOnClickListener(actionCallback);
        }
        return this;
    }

    public ActionBarWhiteLayout setHeadBackVisible(int visible) {
        setViewVisible(mHeadBack, visible);
        return this;
    }

    public ActionBarWhiteLayout setHeadBackLinstener(OnClickListener actionCallback) {
        setViewVisible(mHeadBack, VISIBLE);
        mHeadBack.setOnClickListener(actionCallback);
        return this;
    }

    public ActionBarWhiteLayout setActionLeftVisible(int visible) {
        setViewVisible(mActionLeft, visible);
        return this;
    }

    public ActionBarWhiteLayout setActionRightVisible(int visible) {
        setViewVisible(mActionRight, visible);
        return this;
    }

    public ActionBarWhiteLayout setImageButtonRightVisible(int visible) {
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

    public ActionBarWhiteLayout reset() {
        mTitle.setVisibility(VISIBLE);
        mHeadBack.setVisibility(GONE);
        mActionLeft.setVisibility(GONE);
        mActionRight.setVisibility(GONE);
        mImageButtonRight.setVisibility(GONE);
        setColor(getResources().getColor(R.color.white));
        return this;
    }
}
