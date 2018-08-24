package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.view.CodeVerifyView;
import cn.vpfinance.vpjr.view.VerificationCodeDialog;
import de.greenrobot.event.EventBus;

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
    @Bind(R.id.ll_voice_code_desc_container)
    LinearLayout llVoiceCodeDescContainer;
    private HttpService mHttpService;
    private int btnResendStatus = 2;//重新发送按钮状态:1为倒计时中,2为重新发送
    private String phone;
    private boolean isPersonType;
    private String email;
    private String emailPass;
    private CountDownTimer smsCountDownTimer;
    private CountDownTimer voiceCountDownTimer;
    private String type;

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
        phone = getIntent().getStringExtra("phone");
        type = getIntent().getStringExtra("isPersonType");
        if(type.equals("1")) {
            isPersonType = true;
        }else  {
            isPersonType = false;
        }
        email = getIntent().getStringExtra("email");
        emailPass = getIntent().getStringExtra("emailPass");
        tvPhoneNumDesc.setText("验证码已发送至您手机  " + phone);
        mHttpService = new HttpService(this, this);
        vCodeVerifyView.setOnFullCodeListener(new CodeVerifyView.OnFullCodeListener() {
            @Override
            public void fullCodeListener() {
                //输入完成监听
                String code = vCodeVerifyView.getText().toString();
                if (isPersonType) {//个人用户
                    mHttpService.changePersonEmail(email, code);
                } else {//企业用户
                    mHttpService.changeCompanyEmail(email, DBUtils.getUser(EmailSMSVerificationActivity.this).getUserId() + "", code);
                }
            }

            @Override
            public void restoreListener() {
//                tvErrorInfo.setVisibility(View.INVISIBLE);
            }
        });
        startSms();
    }

//    //开启倒计时
//    private void setCountDownTimer(final TextView view, final boolean isSms) {
//        view.setTextColor(getResources().getColor(R.color.text_666666));
//        view.setEnabled(false);
//        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                view.setText("重新发送  " + millisUntilFinished / 1000 + "s");
//            }
//
//            @Override
//            public void onFinish() {
//                view.setTextColor(getResources().getColor(R.color.btn_blue));
//                if (isSms) {
//                    view.setText("重新发送");
//                } else {
//                    view.setText("语音验证码");
//                }
//                view.setEnabled(true);
//                btnResendStatus = 2;
//            }
//        }.start();
//    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (reqId == ServiceCmd.CmdId.CMD_CHANGE_EMAIL_PERSON.ordinal()) {//个人更换
            String msg = json.optString("status");//实际返回是 "status"
            switch (msg) {
                case "1"://
                    Utils.Toast("验证码超时");
                    break;
                case "2"://
                    Utils.Toast("验证码错误");
                    break;
                case "3"://成功
                    if (emailPass.equals("1")) {
                        Utils.Toast("您已成功更改绑定邮箱");
                    } else {
                        Utils.Toast("您已成功绑定邮箱");
                    }
                    BindOrChangeEmailActivity.startBindOrChangeEmailActivity(this);
                    break;
                case "4"://失败
                    Utils.Toast("失败");
                case "5":
                    Utils.Toast("邮箱格式不正确");
                    break;
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_CHANGE_EMAIL_COMPANY.ordinal()) {//企业更换
            String msg = json.optString("msg");
            switch (msg) {
                case "0"://失败
                    Utils.Toast("失败");
                    break;
                case "1"://成功
                    if (emailPass.equals("1")) {
                        Utils.Toast("您已成功更改绑定邮箱");
                    } else {
                        Utils.Toast("您已成功绑定邮箱");
                    }
                    BindOrChangeEmailActivity.startBindOrChangeEmailActivity(this);
                    break;
                case "2"://用户不存在
                    Utils.Toast("用户不存在");
                    break;
                case "4"://验证码超时
                    Utils.Toast("验证码超时");
                    break;
                case "5"://验证码错误
                    Utils.Toast("验证码错误");
                    break;
            }
        }

    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        errmsg.toString();
    }

    @OnClick({R.id.tv_resend_desc, R.id.btn_voice_verify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_resend_desc://重新发送
                if ("获取验证码".equals(tvResendDesc.getText().toString())) {
                    if(isPersonType) {
                        showCodeDialog(1,1);
                    }else {
                        showCodeDialog(1,3);
                    }
                } else {
                    Utils.Toast("请先稍等一下短信验证码");
                }
                break;
            case R.id.btn_voice_verify://语音验证
                if ("语音验证码".equals(btnVoiceVerify.getText().toString())) {
                    if(isPersonType) {
                        showCodeDialog(2,1);
                    }else {
                        showCodeDialog(2,3);
                    }
                } else {
                    Utils.Toast("请先稍等一下语音验证码");
                }
                break;
        }
    }

    private void startSms() {
        tvResendDesc.setEnabled(false);
        tvResendDesc.setTextColor(getResources().getColor(R.color.text_666666));
        smsCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResendDesc.setText("重新获取(" + (millisUntilFinished / 1000) + "s)");
            }

            @Override
            public void onFinish() {
                tvResendDesc.setTextColor(getResources().getColor(R.color.btn_blue));
                tvResendDesc.setEnabled(true);
                tvResendDesc.setText("获取验证码");
            }
        };
        smsCountDownTimer.start();
    }

    private void startVoice() {
        btnVoiceVerify.setEnabled(false);
        btnVoiceVerify.setTextColor(getResources().getColor(R.color.text_666666));
        mHttpService.getRegisterCaptchaVoice(phone);
        voiceCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnVoiceVerify.setText("重新获取(" + (millisUntilFinished / 1000) + "s)");
            }

            @Override
            public void onFinish() {
                btnVoiceVerify.setTextColor(getResources().getColor(R.color.btn_blue));
                btnVoiceVerify.setEnabled(true);
                btnVoiceVerify.setText("语音验证码");
            }
        };
        voiceCountDownTimer.start();
    }

    public void showCodeDialog(int codeType,int registerType) {
        VerificationCodeDialog codeDialog = VerificationCodeDialog.newInstance(phone, codeType,registerType);
        codeDialog.setSmsListener(new VerificationCodeDialog.SmsListener() {
            @Override
            public void smsStart(int type) {
//                mHttpService.getVerifyCode(null, null, null, phone, null,"0");
                if(type == 1) {
                    startSms();
                }else {
                    startVoice();
                }
            }
        });
        codeDialog.show(getSupportFragmentManager(), "VerificationCodeDialog");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != smsCountDownTimer) {
            smsCountDownTimer.cancel();
        }
        if (null != voiceCountDownTimer) {
            voiceCountDownTimer.cancel();
        }
    }

    /**
     * 开启本页
     *
     * @param phone        手机号
     * @param isPersonType 是个人还是企业用户  1为个人,2为企业
     */
    public static void startEmailSMSVerificationActivity(Context context, String phone, String isPersonType, String email, String emailPass) {
        Intent intent = new Intent(context, EmailSMSVerificationActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("isPersonType", isPersonType);
        intent.putExtra("email", email);
        intent.putExtra("emailPass", emailPass);
        context.startActivity(intent);
    }
}
