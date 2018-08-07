package cn.vpfinance.vpjr.view.pullrefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.vpfinance.android.R;


/**
 * Created by zzlz13 on 2017/5/31.
 */

public class HeadView extends LinearLayout implements PullRefreshView.OnHeadStateListener {

    ImageView ivHeaderDownArrow;
    ProgressBar ivHeaderLoading;
    TextView textView;


    private boolean isReach = false;

    public HeadView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.head_view_layout, this, false);
        this.addView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        initView(layout);
        restore();
        this.setPadding(0, 30, 0, 20);
    }

    private void initView(View view){
        ivHeaderDownArrow = (ImageView)view.findViewById(R.id.iv_header_down_arrow);
        ivHeaderLoading = (ProgressBar)view.findViewById(R.id.iv_header_loading);
        textView = (TextView)view.findViewById(R.id.tv_header_state);
    }

    @Override
    public void onScrollChange(View head, int scrollOffset, int scrollRatio) {

        if (scrollRatio == 100 && !isReach) {
            textView.setText("松开刷新");
            ivHeaderDownArrow.setRotation(180);
            isReach = true;
        } else if (scrollRatio != 100 && isReach) {
            textView.setText("下拉刷新");
            ivHeaderDownArrow.setRotation(0);
            isReach = false;
        }
    }

    @Override
    public void onRefreshHead(View head) {
        ivHeaderLoading.setVisibility(VISIBLE);
        ivHeaderDownArrow.setVisibility(GONE);
        textView.setText("正在刷新");
    }

    @Override
    public void onRetractHead(View head) {
        restore();
        isReach = false;
    }

    private void restore() {
        ivHeaderLoading.setVisibility(GONE);
        ivHeaderDownArrow.setVisibility(VISIBLE);
        ivHeaderDownArrow.setImageResource(R.drawable.arrow_down1);
        ivHeaderDownArrow.setRotation(0);
        textView.setText("下拉刷新");
    }
}