package cn.vpfinance.vpjr.module.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.util.Date;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.gusturelock.LockSetupActivity;
import cn.vpfinance.vpjr.util.CaptchaHelper;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.VerificationCodeDialog;

public class RegisterActivity extends BaseActivity implements OnClickListener, VerificationCodeDialog.SmsListener {

    private HttpService mHttpService = null;
    private EditText mPhone = null;
    private TextView mPassword = null;

    private TextView mPasscode = null;
    private TextView mProtocol = null;
    private TextView mProtocol2 = null;
    private TextView mProtocol3 = null;

    private TextView mInvitee;

    private Button mLoginBtn = null;
    private Button mPasscodeBtn = null;
    private TextView mVoiceCaptcha = null;
    private TextView tvPasswordStrength = null;

    private String rsaPhoneNum = "";
    private String rsaPasscode = "";
    private String rsaPassword = "";

    private boolean mPhoneExist = false;
    private String uid;
    private CaptchaHelper mCaptchaHelper;
    private static final int SMS_CAPTCHA = 1;
    private static final int VOICE_CAPTCHA = 2;
    private int captchaType = 0;//1 sms ;2 voice
    //    private CheckBox cbRisk;
    private CheckBox cbProtocol;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_register);
        mHttpService = new HttpService(this, this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptchaHelper.onRecycle();
    }

    protected void initView() {
        ActionBarWhiteLayout titlebar = (ActionBarWhiteLayout) findViewById(R.id.titleBar);
        titlebar.setTitle("注册").setHeadBackVisible(View.VISIBLE);

        mPhone = viewById(R.id.register_phone);
        mPassword = (TextView) findViewById(R.id.register_password);
        mPasscode = (TextView) findViewById(R.id.register_passcode);
        mProtocol = (TextView) findViewById(R.id.register_protocol);
        mProtocol2 = (TextView) findViewById(R.id.register_protocol2);
        mProtocol3 = (TextView) findViewById(R.id.register_protocol3);
        tvPasswordStrength = (TextView) findViewById(R.id.tvPasswordStrength);

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Utils.isMobile(s.toString())) {
                    mHttpService.isExistPhone(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strength = Common.checkPasswordStrength(s.toString());
                tvPasswordStrength.setText(strength);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPasscodeBtn = (Button) findViewById(R.id.register_fetch_passcode_btn);
        mVoiceCaptcha = (TextView) findViewById(R.id.voiceCaptcha);
        mCaptchaHelper = new CaptchaHelper(this, mPasscodeBtn, mVoiceCaptcha);
        mLoginBtn = (Button) findViewById(R.id.register_commit_btn);
        mInvitee = (TextView) findViewById(R.id.invitee);
        mPasscodeBtn.setOnClickListener(this);
        mVoiceCaptcha.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

        mProtocol.setOnClickListener(this);
        mProtocol2.setOnClickListener(this);
        mProtocol3.setOnClickListener(this);

//        cbRisk = ((CheckBox) findViewById(R.id.cb_risk));
//        cbRisk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (cbRisk.isChecked() && cbProtocol.isChecked()) {
//                    mLoginBtn.setEnabled(true);
//                } else {
//                    mLoginBtn.setEnabled(false);
//                }
//            }
//        });
        cbProtocol = ((CheckBox) findViewById(R.id.cb_protocol));
        cbProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbProtocol.isChecked()) {
                    mLoginBtn.setEnabled(true);
                } else {
                    mLoginBtn.setEnabled(false);
                }
            }
        });
    }


    public void onClick(View v) {
        if (v.getId() == R.id.register_fetch_passcode_btn) {

            captchaType = SMS_CAPTCHA;
            doFetchPasscode(captchaType);
            return;
        }
        if (v.getId() == R.id.voiceCaptcha) {
            captchaType = VOICE_CAPTCHA;
            doFetchPasscode(captchaType);
            return;
        }

        if (v.getId() == R.id.register_protocol) {
            gotoWeb("/registration/useragreement", "微品金融用户服务协议");
            return;
        }

        if (v.getId() == R.id.register_protocol2) {
            gotoWeb("/registration/riskForbidAgreement", "网络借贷风险和禁止性行为提示书");
            return;
        }

        if (v.getId() == R.id.register_protocol3) {
            gotoWeb("/registration/financeLegalAgreement", "资金来源合法承诺书");
            return;
        }

        if (v.getId() == R.id.register_commit_btn) {
            doRegister();
            return;
        }
    }


    private void doFetchPasscode(int captchaType) {
        rsaPhoneNum = mPhone.getText().toString().trim();
        rsaPassword = mPassword.getText().toString();

        rsaPasscode = mPasscode.getText().toString();


        if ("".equals(rsaPhoneNum)) {
            Utils.Toast(this, "手机号不能为空!");
            return;
        }

        if (!Utils.isMobile(rsaPhoneNum)) {
            Utils.Toast(this, "手机号格式不正确!");
            return;
        }

        if ("".equals(rsaPassword)) {
            Utils.Toast(this, "密码不能为空!");
            return;
        }

        if (rsaPassword.length() < 6) {
            Utils.Toast(this, "密码不能低于6位字符!");
            return;
        }

        String passwordPass = Common.isPasswordPass(rsaPassword);
        if (!TextUtils.isEmpty(passwordPass)){
            Utils.Toast(this, passwordPass);
            return;
        }

//        if (rsaPassword.length() > 16) {
//            Utils.Toast(this, "密码不能多于16位字符!");
//            return;
//        }


        if (mPhoneExist) {
            Utils.Toast(this, "手机号已存在");
            return;
        }

        if (captchaType == SMS_CAPTCHA) {
            mCaptchaHelper.smsStart(mHttpService, rsaPhoneNum, "1");
        } else if (captchaType == VOICE_CAPTCHA) {
            mCaptchaHelper.voiceStart(mHttpService, rsaPhoneNum, "1");
        }

    }

    public void showDialog() {
        VerificationCodeDialog codeDialog = VerificationCodeDialog.newInstance(rsaPhoneNum, captchaType);
        codeDialog.setSmsListener(this);
        codeDialog.show(getSupportFragmentManager(), "VerificationCodeDialog");
    }


    private void doRegister() {
        rsaPhoneNum = mPhone.getText().toString().trim();
        rsaPassword = mPassword.getText().toString();
        String inv = mInvitee.getText().toString().trim();
        rsaPasscode = mPasscode.getText().toString();

        if ("".equals(rsaPhoneNum)) {
            Utils.Toast(this, "手机号不能为空!");
            return;
        }
        if (!Utils.isMobile(rsaPhoneNum)) {
            Utils.Toast(this, "手机号格式不正确!");
            return;
        }
        if ("".equals(rsaPassword)) {
            Utils.Toast(this, "密码不能为空!");
            return;
        }
        if (rsaPassword.length() < 6) {
            Utils.Toast(this, "密码不能低于6位字符!");
            return;
        }

        String passwordPass = Common.isPasswordPass(rsaPassword);
        if (!TextUtils.isEmpty(passwordPass)){
            Utils.Toast(this, passwordPass);
            return;
        }

        if ("".equals(rsaPasscode)) {
            Utils.Toast(this, "验证码不能为空!");
            return;
        }
        if (mPhoneExist) {
            Utils.Toast(this, "手机号已存在");
            return;
        }

        Md5Algorithm md5 = Md5Algorithm.getInstance();
        rsaPassword = md5.md5Digest((rsaPassword + HttpService.LOG_KEY).getBytes());
        mHttpService.userRegister(inv, rsaPhoneNum, rsaPassword, rsaPhoneNum, rsaPasscode);//Md5Algorithm
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal()) {
            String passcode = mHttpService.onGetVerifyCode(json);
            String errmsg = mHttpService.getPasscodeErrmsg();
            int msg = json.optInt("msg", -1);
            if (5 == msg) {
                //状态为5,弹出验证码
                mCaptchaHelper.smsFinish();
                showDialog();
            } else if (6 == msg) {
                mCaptchaHelper.smsFinish();
            }
            Utils.Toast(this, errmsg);


//			mCaptchaHelper.smsFinish();
        }
        if (reqId == ServiceCmd.CmdId.CMD_Voice_Captcha.ordinal()) {
            String errmsg = mHttpService.onGetVoiceCaptcha(json);
            int msg = json.optInt("msg", -1);
            if (5 == msg) {
                mCaptchaHelper.voiceFinish();
                showDialog();
            } else if (6 == msg) {
                mCaptchaHelper.voiceFinish();
            }
            Utils.Toast(this, errmsg);
//			mCaptchaHelper.voiceFinish();
        }

        if (reqId == ServiceCmd.CmdId.CMD_userRegister.ordinal()) {
            mLoginBtn.setEnabled(true);

            String msg = mHttpService.onUserRegister(json);
            if (msg != "") {
                Utils.Toast(this, msg);
                if ("注册成功".equals(msg)) {
                    Utils.Toast(this, "注册成功!");

                    mHttpService.userLogin(rsaPhoneNum, rsaPassword);
                    OpenRedPacket openRedPacket = new OpenRedPacket();
                    openRedPacket.resTime = new Date().getTime();
                    openRedPacket.resPhone = rsaPhoneNum;
                    FinanceApplication application = (FinanceApplication) getApplication();
                    application.openRedPacket = openRedPacket;
                    application.isFirstRegieter = true;
                }
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_userLogin.ordinal()) {
            String msg = mHttpService.onUserLogin(this, json);
            if (!msg.equals("")) {
                Utils.Toast(this, msg);
                return;
            } else {
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, rsaPhoneNum + "");
                preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME, rsaPhoneNum + "");
                preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD, rsaPassword);

                uid = AppState.instance().getSessionCode();
                String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
                if (!TextUtils.isEmpty(uid) && !uid.equals(savedUid)) {
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID, uid);
                }
            }


            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            //清理login present标志
            HttpService.clearPresentLoginFlag();
            startActivity(new Intent(this, LockSetupActivity.class));

            finish();
        }

        if (reqId == ServiceCmd.CmdId.CMD_isExistPhone.ordinal()) {
            int errNo = mHttpService.onCheckPhoneExist(json);
            if (errNo == 1) {
                mPhoneExist = true;
                Utils.Toast(this, "手机号已存在");
            } else {
                mPhoneExist = false;
            }
        }
    }

    @Override
    public void smsStart(int type) {
        if (type == 1) {
            mCaptchaHelper.smsStart(null, "", "");
        } else if (type == 2) {
            mCaptchaHelper.voiceStart(null, "", "");
        }
    }


    public class OpenRedPacket {
        public long resTime;
        public String resPhone;
    }

    @Override
    public void onHttpError(int reqId, String msg) {
        if (reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal()) {
            mCaptchaHelper.smsFinish();
        }
        if (reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal()) {
            mCaptchaHelper.voiceFinish();
        }
        if (reqId == ServiceCmd.CmdId.CMD_userRegister.ordinal()) {
            mLoginBtn.setEnabled(true);
        }
    }

}
