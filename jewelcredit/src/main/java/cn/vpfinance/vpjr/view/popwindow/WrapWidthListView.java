package cn.vpfinance.vpjr.view.popwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 */
public class WrapWidthListView extends ListView{

    public WrapWidthListView(Context context) {
        super(context);
    }

    public WrapWidthListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapWidthListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        for (int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);
            //MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)   size,mode
            //mode :UNSPECIFIED(未指定,子决定大小)
            // EXACTLY(完全,父元素决定大小)
            // AT_MOST(至多,子元素最多达到值)
            child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),heightMeasureSpec);
            int w = child.getMeasuredWidth();
            if (w > width)
                width = w;
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width + getPaddingLeft() + getPaddingRight(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
