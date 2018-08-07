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

public class TailView extends LinearLayout implements PullRefreshView.OnTailStateListener {

    ImageView ivHeaderDownArrow;
    ProgressBar ivHeaderLoading;
    TextView textView;


    private boolean isReach = false;
    private boolean isMore = true;

    public TailView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.head_view_layout, this, false);
        this.addView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        initView(layout);
        restore();
        this.setPadding(0, 20, 0, 30);
    }

    private void initView(View view){
        ivHeaderDownArrow = (ImageView)view.findViewById(R.id.iv_header_down_arrow);
        ivHeaderLoading = (ProgressBar)view.findViewById(R.id.iv_header_loading);
        textView = (TextView)view.findViewById(R.id.tv_header_state);
    }

    @Override
    public void onScrollChange(View tail, int scrollOffset, int scrollRatio) {
        if (isMore) {
            if (scrollRatio == 100 && !isReach) {
                textView.setText("松开加载");
                ivHeaderDownArrow.setRotation(0);
                isReach = true;
            } else if (scrollRatio != 100 && isReach) {
                textView.setText("上拉加载");
                isReach = false;
                ivHeaderDownArrow.setRotation(180);
            }
        }
    }

    @Override
    public void onRefreshTail(View tail) {
        if (isMore) {
            ivHeaderLoading.setVisibility(VISIBLE);
            ivHeaderDownArrow.setVisibility(GONE);
            textView.setText("正在加载");
        }
    }

    @Override
    public void onRetractTail(View tail) {
        if (isMore) {
            restore();
            isReach = false;
        }
    }

    @Override
    public void onNotMore(View tail) {
        ivHeaderLoading.setVisibility(GONE);
        ivHeaderDownArrow.setVisibility(GONE);
        textView.setText("已经全部加载完毕");
        isMore = false;
    }

    @Override
    public void onHasMore(View tail) {
        ivHeaderLoading.setVisibility(GONE);
        ivHeaderDownArrow.setVisibility(VISIBLE);
        textView.setText("上拉加载");
        isMore = true;
    }

    private void restore() {
        ivHeaderLoading.setVisibility(GONE);
        ivHeaderDownArrow.setVisibility(VISIBLE);
        ivHeaderDownArrow.setImageResource(R.drawable.arrow_down1);
        ivHeaderDownArrow.setRotation(180);
        textView.setText("上拉加载");
    }
}
