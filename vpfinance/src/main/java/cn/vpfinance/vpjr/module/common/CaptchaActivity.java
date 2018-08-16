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
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.FormatUtils;
import de.greenrobot.event.EventBus;

/**
 * 发送验证码,企业&个人
 */
public class CaptchaActivity extends BaseActivity {

    public static final int REGISTER_PERSON = 1; //个人注册
    public static final int REGISTER_COMPANY = 2; //企业注册
    public static final int FORGET_LOGIN_PASSWORD_PERSON = 3; //个人忘记登录密码
    public static final int FORGET_LOGIN_PASSWORD_COMPANY = 4; //企业忘记登录密码
//    public static final int WEI_XIN_BIND_PHONE = 5; //微信绑定手机号码

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
    private UserRegisterBean userRegisterBean;

    public static void goThis(Context context, UserRegisterBean userRegisterBean) {
        Intent intent = new Intent(context, CaptchaActivity.class);
        intent.putExtra("userRegisterBean", userRegisterBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mHttpService = new HttpService(this, this);
        Intent intent = getIntent();
        if (null != intent) {
            userRegisterBean = (UserRegisterBean) intent.getSerializableExtra("userRegisterBean");
            if (userRegisterBean.getUserType()) {
                type = REGISTER_PERSON;
            } else {
                type = REGISTER_COMPANY;
            }
            phone = userRegisterBean.getPhoneNum();
        }
        initView();
    }

    @Override
    protected void initView() {
        if (userRegisterBean.getIsFromWeixin()) {//是否是微信绑定
            titleBar.setTitle("绑定手机号").setHeadBackVisible(View.VISIBLE);
        } else {
            titleBar.setTitle("短信验证").setHeadBackVisible(View.VISIBLE);
        }
        if (type == REGISTER_PERSON || type == FORGET_LOGIN_PASSWORD_PERSON) {//个人
            tvPhoneHint.setText("短信验证码已发送至您的手机 " + FormatUtils.hidePhone(phone));
        } else if (type == REGISTER_COMPANY || type == FORGET_LOGIN_PASSWORD_COMPANY) {//企业
            tvPhoneHint.setText("短信验证码已发送至经办人手机 " + FormatUtils.hidePhone(phone));
        }
//        startSms();3
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
        if (TextUtils.isEmpty(captcha)) {
            Utils.Toast("验证码不能为空");
            return;
        }
        mHttpService.checkSmsCode("", etCaptcha.getText().toString(), userRegisterBean.getPhoneNum());
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

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_REGISTER_FINISH)) {//注册完成
            finish();
        }
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_WEIXIN_REGISTER_SUCCESS)) {//微信注册成功
            finish();
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (reqId == ServiceCmd.CmdId.CMD_CHECK_SMS_CODE.ordinal()) {
            String msg = json.optString("msg");
            if (msg.equals("1")) {//校验成功
                if (null != userRegisterBean) {
                    if (userRegisterBean.getIsFromWeixin()) {//是微信绑定
                        if (userRegisterBean.getUserType()) {
                            mHttpService.bindWEIXIN(userRegisterBean.getUnionid(), userRegisterBean.getOpenid(), "1", userRegisterBean.getPhoneNum(), etCaptcha.getText().toString());
                        } else {
                            mHttpService.bindWEIXIN(userRegisterBean.getUnionid(), userRegisterBean.getOpenid(), "2", userRegisterBean.getEmail(), etCaptcha.getText().toString());
                        }
                    } else {
                        userRegisterBean.setCaptcha(etCaptcha.getText().toString());
                        userRegisterBean.setPwdSetType(LoginPasswordActivity.PASSWORD_SETTING);
                        if (type == REGISTER_PERSON) {
                            LoginPasswordActivity.goThis(this, userRegisterBean);
                        } else {
                            RegisterCompanyInfoActivity.goThis(this, userRegisterBean);
                        }
                    }
                }
            } else if (msg.equals("4")) {//超时
                Utils.Toast("请求超时");
            } else if (msg.equals("5")) {
                Utils.Toast("短信验证码错误");
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_REGISTER_CAPTCHA_SMS.ordinal()) {
            String msg = json.optString("msg");
        }
        if (reqId == ServiceCmd.CmdId.CMD_REGISTER_CAPTCHA_VOICE.ordinal()) {
            String msg = json.optString("msg");
        }
        if (reqId == ServiceCmd.CmdId.CMD_WEIXIN_BIND.ordinal()) {//绑定微信
            String msg = json.optString("msg");
            switch (msg) {
                case "0":
                    Utils.Toast("账号被锁定");
                    break;
                case "1":
                    Utils.Toast("登录成功");
                    Long uid = json.optLong("uid");
                    DBUtils.getUser(CaptchaActivity.this).setUserId(uid);
                    EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_WEIXIN_LOGIN_SUCCESS));//微信登录成功
                    finish();
                    break;
                case "2"://此用户为企业用户，不允许注册，请跳转到企业注册页面
                    Utils.Toast("请先注册");
                    RegisterCompanyInfoActivity.goThis(this, userRegisterBean);
                    break;
                case "3"://手机号未注册，跳转设置密码,再调用接口
                    userRegisterBean.setCaptcha(etCaptcha.getText().toString());
                    LoginPasswordActivity.goThis(this, userRegisterBean);
                    break;
                case "4":
                    Utils.Toast("账号已注销");
                    break;
                case "5":
                    Utils.Toast("验证码不正确");
                    break;
            }
        }
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
        EventBus.getDefault().unregister(this);
    }
}
