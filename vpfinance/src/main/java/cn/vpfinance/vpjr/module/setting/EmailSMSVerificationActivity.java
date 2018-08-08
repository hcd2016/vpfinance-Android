package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.view.CodeVerifyView;

public class EmailSMSVerificationActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    @Bind(R.id.tv_phone_num_desc)
    TextView tvPhoneNumDesc;
    @Bind(R.id.vCodeVerifyView)
    CodeVerifyView vCodeVerifyView;
    @Bind(R.id.tv_resend_desc)
    TextView tvResendDesc;
    @Bind(R.id.btn_voice_verify)
    TextView btnVoiceVerify;
    @Bind(R.id.tv_error_info)
    TextView tvErrorInfo;
    @Bind(R.id.ll_voice_code_desc_container)
    LinearLayout llVoiceCodeDescContainer;
    private HttpService mHttpService;
    private int btnResendStatus = 0;//重新发送按钮状态:0为获取验证码,1为倒计时中,2为重新发送

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sms_verification);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("短信验证").setHeadBackVisible(View.VISIBLE);
        mHttpService = new HttpService(this, this);
        vCodeVerifyView.setOnFullCodeListener(new CodeVerifyView.OnFullCodeListener() {
            @Override
            public void fullCodeListener() {
                //输入完成监听
                Utils.Toast("输入完成!");
                String code = vCodeVerifyView.getText().toString();
            }

            @Override
            public void restoreListener() {
//                tvErrorInfo.setVisibility(View.INVISIBLE);
            }
        });
    }

    //开启倒计时
    private void setCountDownTimer() {
        tvResendDesc.setTextColor(getResources().getColor(R.color.text_666666));
        tvResendDesc.setEnabled(false);
        CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResendDesc.setText("重新发送  " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvResendDesc.setTextColor(getResources().getColor(R.color.btn_blue));
                tvResendDesc.setText("重新发送");
                tvResendDesc.setEnabled(true);
                btnResendStatus = 2;
            }
        }.start();
    }

    @OnClick({R.id.tv_resend_desc, R.id.btn_voice_verify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_resend_desc://重新发送
                if (btnResendStatus == 0) {
                    //第一次获取验证码
                    // TODO: 2018/8/8 网络请求
                    tvPhoneNumDesc.setVisibility(View.VISIBLE);
                    llVoiceCodeDescContainer.setVisibility(View.VISIBLE);
                    btnResendStatus = 1;
                } else if (btnResendStatus == 2) {//重新发送按钮状态
                    // TODO: 2018/8/8 网络请求
                    btnResendStatus = 1;
                }
                setCountDownTimer();
                break;
            case R.id.btn_voice_verify://语音验证
                break;
        }
    }


    /**
     * 开启本页
     */
    public static void startEmailSMSVerificationActivity(Context context) {
        Intent intent = new Intent(context, EmailSMSVerificationActivity.class);
        context.startActivity(intent);
    }
}
