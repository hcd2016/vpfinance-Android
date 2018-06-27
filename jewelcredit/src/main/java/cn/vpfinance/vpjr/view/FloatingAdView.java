package cn.vpfinance.vpjr.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import cn.vpfinance.android.R;

@SuppressLint("AppCompatCustomView")
public class FloatingAdView extends ImageView {

    private Scroller mScroller;
    private int mLastX;
    private int mLastY;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;
    private int mActionBarHeight;
    private final int MIN_MOVE_OFF = 8;
    private int mSelfHeight;
    private onFloadingAdClickListener mListener;

    public interface onFloadingAdClickListener {
        void onAdClick();
    }

    public FloatingAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public FloatingAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        //加载布局
//        RelativeLayout convertView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.floating_ad_view, this);
        //初始化控件
//        mFloatingAdImage = (ImageView) convertView.findViewById(R.id.iv_floating_ad);
//        mFloatingAdCloseImage = (ImageView) convertView.findViewById(R.id.iv_floating_ad_close);
        mSelfHeight = (int)(context.getResources().getDimension(R.dimen.floationadview_width_height));
//        mActionBarHeight = (int)(context.getResources().getDimension(R.dimen.bar_height));
//        mStatusBarHeight = (int)(context.getResources().getDimension(R.dimen.bar_height));
        //初始化Scroller
        mScroller = new Scroller(context);
        //获取屏幕宽度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //判断滑动是否完成
        if (mScroller.computeScrollOffset()) {
            //完成滑动，getCurrX()、getCurrY()为mScroller当前水平滚动的位置
            ((View) getParent()).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getX();
                mLastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = x - mLastX;
                int moveY = y - mLastY;
                //保证浮标在屏幕能移动
                //mActionBarHeight和底部tab栏高度一致
                if ((rawY - y - mStatusBarHeight - mActionBarHeight + moveY - mSelfHeight/2 < 0) || (rawY - y + moveY + getHeight() + mActionBarHeight > mScreenHeight)) {
                    ((View) getParent()).scrollBy(-moveX, 0);
//                    ((RelativeLayout) getParent()).updateViewLayout(this,);
                }else {
                    ((View) getParent()).scrollBy(-moveX, -moveY);
                }

                break;
            case MotionEvent.ACTION_UP:
                //手指离开时，滑动到屏幕一侧
                View viewGroup = ((View) getParent());
                //计算水平滚动的距离
                int offX = rawX > mScreenWidth / 2 ? -viewGroup.getScrollX() : rawX - x;
                //根据移动距离区分是点击事件还是滑动事件
                if (Math.abs(offX) < MIN_MOVE_OFF && Math.abs(viewGroup.getScaleY()) < MIN_MOVE_OFF) {
                    mListener.onAdClick();
                } else {
                    mScroller.startScroll(
                            viewGroup.getScrollX(),
                            viewGroup.getScrollY(),
                            offX,
                            0);
                    invalidate();
                }
                break;
        }
        return true;
    }

    public void setOnFloatingAdClickListener(onFloadingAdClickListener listener) {
        if (listener != null) {
            this.mListener = listener;
        }
    }

    public void setExtraHeight(int statusBarHeight, int actionBarHeight) {
        mStatusBarHeight = statusBarHeight;
        mActionBarHeight = actionBarHeight;
    }
}
