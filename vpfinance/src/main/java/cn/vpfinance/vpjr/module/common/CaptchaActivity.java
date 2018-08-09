package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.util.FormatUtils;

public class CaptchaActivity extends BaseActivity {

    public static final int REGISTER_PERSON = 1; //个人注册
    public static final int REGISTER_COMPANY = 2; //企业注册
    public static final int FORGET_LOGIN_PASSWORD_PERSON = 3; //个人忘记登录密码
    public static final int FORGET_LOGIN_PASSWORD_COMPANY = 4; //企业忘记登录密码
    public static final int WEI_XIN_BIND_PHONE = 5; //微信绑定手机号码

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Bind(R.id.tvPhoneHint)
    TextView tvPhoneHint;
    @Bind(R.id.tvVoiceCaptcha)
    TextView tvVoiceCaptcha;
    @Bind(R.id.etCaptcha)
    EditText etCaptcha;
    @Bind(R.id.btnGetCaptcha)
    Button btnGetCaptcha;
    @Bind(R.id.btnNext)
    Button btnNext;

    private int type = REGISTER_PERSON;
    private String phone;
    private CountDownTimer smsCountDownTimer;
    private CountDownTimer voiceCountDownTimer;
    private HttpService mHttpService;

    public static void goThis(Context context, int type, String phone) {
        Intent intent = new Intent(context, CaptchaActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);
        ButterKnife.bind(this);
        mHttpService = new HttpService(this, this);
        Intent intent = getIntent();
        if (null != intent) {
            type = intent.getIntExtra("type", 0);
            phone = intent.getStringExtra("phone");
        }
        initView();
    }

    @Override
    protected void initView() {
        titleBar.setTitle("短信验证").setHeadBackVisible(View.VISIBLE);

        if (type == REGISTER_PERSON || type == FORGET_LOGIN_PASSWORD_PERSON) {//个人
            tvPhoneHint.setText("短信验证码已发送至您的手机 " + FormatUtils.hidePhone(phone));
        } else if (type == REGISTER_COMPANY || type == FORGET_LOGIN_PASSWORD_COMPANY) {//企业
            tvPhoneHint.setText("短信验证码已发送至经办人手机 " + FormatUtils.hidePhone(phone));
        } else if (type == WEI_XIN_BIND_PHONE){
            tvPhoneHint.setText("短信验证码已发送至您的手机 " + FormatUtils.hidePhone(phone));
        }
        startSms();
    }

    @OnClick({R.id.btnNext, R.id.btnGetCaptcha, R.id.tvVoiceCaptcha})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                next();
                break;
            case R.id.btnGetCaptcha:
                if ("语音验证码".equals(tvVoiceCaptcha.getText().toString())) {
                    startSms();
                } else {
                    Utils.Toast("请先稍等一下语音验证码");
                }
                break;
            case R.id.tvVoiceCaptcha:
                if ("获取验证码".equals(btnGetCaptcha.getText().toString())) {
                    startVoice();
                } else {
                    Utils.Toast("请先稍等一下短信验证码");
                }
                break;
        }
    }

    private void next() {
        String captcha = etCaptcha.getText().toString().trim();
        if (TextUtils.isEmpty(captcha)){
            Utils.Toast("验证码不能为空");
            return;
        }
        mHttpService.getCheckCaptchaSmsVoice(phone,captcha);
//        if (type == REGISTER_PERSON) {
//            LoginPasswordActivity.goThis(this, LoginPasswordActivity.PASSWORD_SETTING);
//        } else {
//            RegisterCompanyInfoActivity.goThis(this);
//        }
    }

    private void startSms() {
        btnGetCaptcha.setEnabled(false);
        mHttpService.getRegisterCaptchaSms(phone);
        smsCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnGetCaptcha.setText("重新获取(" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                btnGetCaptcha.setEnabled(true);
                btnGetCaptcha.setText("获取验证码");
            }
        };
        smsCountDownTimer.start();
    }

    private void startVoice() {
        tvVoiceCaptcha.setEnabled(false);
        mHttpService.getRegisterCaptchaVoice(phone);
        voiceCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvVoiceCaptcha.setText("重新获取(" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                tvVoiceCaptcha.setEnabled(true);
                tvVoiceCaptcha.setText("语音验证码");
            }
        };
        voiceCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        if (null != smsCountDownTimer) {
            smsCountDownTimer.cancel();
        }
        if (null != voiceCountDownTimer) {
            voiceCountDownTimer.cancel();
        }
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
