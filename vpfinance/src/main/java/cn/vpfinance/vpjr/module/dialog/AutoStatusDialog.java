package cn.vpfinance.vpjr.module.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.vpfinance.android.R;

public class AutoStatusDialog extends TBaseDialog {
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.tv_no_tips)
    TextView tvNoTips;
    @Bind(R.id.tv_btnLeft)
    TextView tvBtnLeft;
    @Bind(R.id.view_line)
    View viewLine;
    @Bind(R.id.tv_btnRight)
    TextView tvBtnRight;
    private AutoStatusDialog.onAllListener listener;

    public AutoStatusDialog(Context context) {
        super(context, R.layout.dialog_auto_status);
        setWindowParam(0.8f, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, 0);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    public void setTvTitle(String title){
        tvTitle.setText(title);
    }

    public void setTvContent(String content){
        tvContent.setText(content);
    }




    @OnClick({R.id.tv_no_tips, R.id.tv_btnLeft, R.id.tv_btnRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_no_tips:
                if (listener != null){
                    listener.clickNoTips();
                }
                dismiss();
                break;
            case R.id.tv_btnLeft:
                if (listener != null){
                    listener.clickCancel();
                }
                dismiss();
                break;
            case R.id.tv_btnRight:
                if (listener != null){
                    listener.clickOk();
                }
                dismiss();
                break;
        }
    }

    public void setOnAllLinstener(AutoStatusDialog.onAllListener listener){
        this.listener = listener;
    }

    public interface onAllListener{
        void clickOk();
        void clickCancel();
        void clickNoTips();
    }
}
