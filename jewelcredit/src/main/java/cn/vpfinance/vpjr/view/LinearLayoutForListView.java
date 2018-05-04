package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import cn.vpfinance.vpjr.util.Logger;

/**
 * Created by zzlz13 on 2017/4/13.
 */

public class LinearLayoutForListView extends LinearLayout {

    private BaseAdapter adapter;
//    private OnClickListener onClickListener = null;

    public LinearLayoutForListView(Context context) {
        super(context);
    }

    public LinearLayoutForListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutForListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        bindLinearLayout();
    }

    public void bindLinearLayout() {
        int count = adapter.getCount();
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);
//            v.setOnClickListener(this.onClickListener);
            addView(v, i);
        }
    }

}
