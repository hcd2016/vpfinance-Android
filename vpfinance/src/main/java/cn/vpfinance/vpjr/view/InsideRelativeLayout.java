package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 */
public class InsideRelativeLayout extends RelativeLayout{
    public InsideRelativeLayout(Context context) {
        super(context);
    }

    public InsideRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InsideRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int startY;
    private int disY;
    private boolean isDispatch = true;//是否自己处理（拦截）

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        requestDisallowInterceptTouchEvent(true);

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下的时候就要告诉最外层的ViewPager不要去拦截事件
                startY = (int) ev.getY();
                isDispatch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                disY = moveY - startY;

                if (isDispatch && disY > 0){//isDispatch false
                    //到顶部，向上滑
                    requestDisallowInterceptTouchEvent(false);
                }else if(isDispatch && disY < 0){
                    //底部，向下滑
                    requestDisallowInterceptTouchEvent(false);
                }else{
                    requestDisallowInterceptTouchEvent(true);
                }

                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否拦截触摸事件，自己处理
     * @param
     */
        public void setIsDispatchTouchEvent(boolean isDispatch){
        this.isDispatch = isDispatch;
    }
//
//    public static interface onDispatchListener {
//        public boolean onDispatch(boolean isDispatch);
//    }
//
//    private onDispatchListener onDispatchListener;
//    public void setOnTextCancleListener(onDispatchListener listener) {
//        this.onDispatchListener = listener;
//    }
}
