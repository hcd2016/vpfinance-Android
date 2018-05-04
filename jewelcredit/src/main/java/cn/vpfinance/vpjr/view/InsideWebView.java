package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 */
public class InsideWebView extends WebView {

    public InsideWebView(Context context) {
        super(context);
    }
    public InsideWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InsideWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float webViewContentHeight;
    private float webViewCurrentHeight;

    private int startY;
    private int disY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //请求父元素不要拦截事件  true
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下的时候就要告诉最外层的ViewPager不要去拦截事件

                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                disY = moveY - startY;
                //WebView的总高度
                webViewContentHeight = this.getContentHeight() * this.getScale();
                //WebView的现高度
                int scrollY = getScrollY();
                webViewCurrentHeight = (this.getHeight() + scrollY);
                if (scrollY == 0 && disY > 0){
                    //到顶部，向上滑
                    requestDisallowInterceptTouchEvent(false);
                }else if((webViewContentHeight - webViewCurrentHeight) < 2 && disY < 0){
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
}
