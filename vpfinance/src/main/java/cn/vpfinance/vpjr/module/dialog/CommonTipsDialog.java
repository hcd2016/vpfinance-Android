package cn.vpfinance.vpjr.module.dialog;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.vpfinance.android.R;

/**
 * <p>
 * Description：公用提示框
 * </p>
 * <p>
 * date 2017/3/21
 */

public class CommonTipsDialog extends TBaseDialog {


    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_tips)
    TextView tvTips;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_sure)
    TextView tvSure;
    private OnRightClickListener onRightClickListener;
    private OnLeftClickListener onLeftClickListener;

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
    }

    public CommonTipsDialog(Context context) {
        super(context, R.layout.public_dialog_common_tips);
        setWindowParam(0.8f, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, 0);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tv_cancel, R.id.tv_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel://取消（左侧按钮）
                if (onLeftClickListener != null) {
                    onLeftClickListener.onLeftClick();
                } else {
                    dismiss();
                }
                break;
            case R.id.tv_sure://确定(右侧按钮)
                if (onRightClickListener != null) {
                    onRightClickListener.onRightClick();
                } else {
                    dismiss();
                }
                break;
        }
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    /**
     * 设置提示文本
     */
    public void setTips(String tips) {
        tvTips.setText(tips);
        tvTips.setVisibility(View.VISIBLE);
    }

    /**
     * 设置左侧按钮文本
     *
     * @param text
     */
    public void setLeftBtnText(String text) {
        tvCancel.setText(text);
    }

    /**
     * 设置右侧按钮文本
     *
     * @param text
     */
    public void setRightBtnText(String text) {
        tvSure.setText(text);
    }

    /**
     * 设置左侧文本颜色
     *
     * @param color color资源，直接传R.color.xxx
     */
    public void setLeftBtnTextColor(@ColorRes int color) {
        tvCancel.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    /**
     * 设置右侧文本颜色，
     *
     * @param color color资源，直接传R.color.xxx
     */
    public void setRightBtnTextColor(@ColorRes int color) {
        tvSure.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    public interface OnRightClickListener {
        void onRightClick();
    }

    public interface OnLeftClickListener {
        void onLeftClick();
    }
}
