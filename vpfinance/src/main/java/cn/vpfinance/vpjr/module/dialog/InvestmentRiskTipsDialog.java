package cn.vpfinance.vpjr.module.dialog;

import android.content.Context;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.vpfinance.android.R;

/**
 * 出借风险提示dialog
 */
public class InvestmentRiskTipsDialog extends TBaseDialog {
    @Bind(R.id.tv_desc1)
    TextView tvDesc1;
    @Bind(R.id.tv_desc2)
    TextView tvDesc2;
    @Bind(R.id.btn_check)
    TextView btnCheck;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_sure)
    TextView tvSure;

    public InvestmentRiskTipsDialog(Context context) {
        super(context, R.layout.dialog_invest_risk_tips);
    }

    @Override
    public void initView() {
        setWindowParam(0.8f, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, 0);
        btnCheck.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
    }

    @Override
    public void initData() {

    }

    //设置描述1内容
    public void setTvDesc1Content(String content) {
        tvDesc1.setText(content);
    }

    //设置描述2内容
    public void setTvDesc2Content(String content) {
        tvDesc2.setText(content);
    }

    //设置取消按钮文本
    public void setCancleText(String content) {
        tvCancel.setText(content);
    }

    @OnClick({R.id.btn_check, R.id.tv_cancel, R.id.tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_check://查看原因
                if(null != onCheckClickListner) {
                    onCheckClickListner.onCheckClick();
                }
                break;
            case R.id.tv_cancel://取消
                if(null != onCancleClickListner) {
                    onCancleClickListner.onCancleClick();
                }
                break;
            case R.id.tv_sure://确定
                if(null != onConfimClickListner) {
                    onConfimClickListner.onConfimClick();
                }
                break;
        }
        dismiss();
    }

    //设置回调
    public OnCheckClickListner onCheckClickListner;
    public OnConfimClickListner onConfimClickListner;
    public OnCancleClickListner onCancleClickListner;

    public OnCheckClickListner getOnCheckClickListner() {
        return onCheckClickListner;
    }

    public void setOnCheckClickListner(OnCheckClickListner onCheckClickListner) {
        this.onCheckClickListner = onCheckClickListner;
    }

    public OnConfimClickListner getOnConfimClickListner() {
        return onConfimClickListner;
    }

    public void setOnConfimClickListner(OnConfimClickListner onConfimClickListner) {
        this.onConfimClickListner = onConfimClickListner;
    }

    public OnCancleClickListner getOnCancleClickListner() {
        return onCancleClickListner;
    }

    public void setOnCancleClickListner(OnCancleClickListner onCancleClickListner) {
        this.onCancleClickListner = onCancleClickListner;
    }

    //查看原因
    public interface OnCheckClickListner {
        void onCheckClick();
    }

    //确定
    public interface OnConfimClickListner {
        void onConfimClick();
    }

    //取消
    public interface OnCancleClickListner {
        void onCancleClick();
    }
}
