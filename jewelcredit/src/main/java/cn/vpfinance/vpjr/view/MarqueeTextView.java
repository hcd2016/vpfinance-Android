package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;


/**
 * 自定义跑马灯文本框，支持拖拽查看文本内容，点击暂停文字 先设置要显示文本，然后调用Start()方法运行跑马灯
 *
 * @author sy
 */
public class MarqueeTextView extends TextView implements Runnable,
        OnTouchListener {
    public MarqueeTextView(Context context) {
        super(context);
        mContext = context;
    }

    /** 是否停止滚动 */
    private boolean mStopMarquee;
    private String mText;
    public int mCoordinateX;
    int xOffset;
    private int mTextWidth;
    GestureDetector gestureDetector;
    private Context mContext;

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    /**
     * 开始滚动
     *
     * @param
     */
    public void Start() {
        this.setOnTouchListener(this);

        gestureDetector = new GestureDetector(getContext(),
                new OnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {
                        mCoordinateX += (int) distanceX;
                        scrollTo(mCoordinateX, 0);
                        // TODO:设置偏移量，distanceX为滑动距离
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        return false;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        // TODO Auto-generated method stub
                        return false;
                    }
                });

        xOffset = 0;
        mStopMarquee = false;
        mText = this.getText().toString();// 获取文本框文本
        mCoordinateX = 0;
        WindowManager systemService = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = systemService.getDefaultDisplay().getWidth();
//        mTextWidth = (int) Math.abs(getPaint().measureText(mText));
        mTextWidth = width;
        post(this);
    }

    @Override
    public void run() {

        if (!mStopMarquee) {
            mCoordinateX += 3;// 滚动速度
            scrollTo(mCoordinateX, 0);
            if (mCoordinateX > mTextWidth) {
                scrollTo(0, 0);
                mCoordinateX = 0;
            }
            postDelayed(this, 50);
        }

    }

    // 继续滚动
    public void Continue() {
        if (mStopMarquee) {
            mStopMarquee = false;
            post(this);
        }
    }

    // 暂停滚动
    public void Paush() {
        mStopMarquee = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
                Paush();
                break;
            default:
                Continue();
                break;
        }

        gestureDetector.onTouchEvent(event);
        return true;
    }

}