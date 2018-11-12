package com.jewelcredit.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jewelcredit.util.Utils;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * 自定义startRefresh 的 HeaderView
 */
public class BaseRefreshHeaderView extends FrameLayout implements RefreshHeader {
    Context context;
    @Bind(R.id.iv_img)
    ImageView ivImg;
    @Bind(R.id.tv_refresh_time)
    TextView tvRefreshTime;
    @Bind(R.id.tv_refresh_desc)
    TextView tvRefreshDesc;
    @Bind(R.id.ll_desc_container)
    LinearLayout llDescContainer;
    private long lastUpdateTime = System.currentTimeMillis();

    public BaseRefreshHeaderView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public BaseRefreshHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BaseRefreshHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.base_refresh_header_view, null);
        ButterKnife.bind(this,view);
        addView(view);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//平移
    }

    @Override
    public void setPrimaryColors(int... colors) {
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if(!success) {
            Utils.Toast("刷新失败,请稍后重试!");
        }
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
        SharedPreferencesHelper.getInstance(context).putLongValue("last_update_time", System.currentTimeMillis());
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        long last_update_time = SharedPreferencesHelper.getInstance(context).getLongValue("last_update_time", System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String lastTime = dateFormat.format(new Date(last_update_time));
        switch (newState) {
            case None:
            case PullDownToRefresh:
                tvRefreshDesc.setText("下拉刷新");
                tvRefreshTime.setText("最后更新 "+lastTime);
                ivImg.setImageResource(R.mipmap.loading0);
                break;
            case Refreshing:
                tvRefreshDesc.setText("正在刷新");
                tvRefreshTime.setText("最后更新 "+lastTime);
                Glide.with(context).load(R.mipmap.loading2).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivImg);
                break;
            case ReleaseToRefresh:
                tvRefreshDesc.setText("松开刷新");
                tvRefreshTime.setText("最后更新 "+lastTime);
                ivImg.setImageResource(R.mipmap.loading1);
                break;
        }
    }
}
