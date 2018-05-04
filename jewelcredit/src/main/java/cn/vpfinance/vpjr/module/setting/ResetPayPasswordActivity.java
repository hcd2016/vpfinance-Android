package cn.vpfinance.vpjr.module.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.util.CaptchaHelper;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import cn.vpfinance.vpjr.view.VerificationCodeDialog;

/**
 * 找回交易密码
 */
public class ResetPayPasswordActivity extends BaseActivity implements View.OnClickListener, VerificationCodeDialog.SmsListener {

    private HttpService     mHttpService;
    private EditText        phone;
    private EditTextWithDel newPwd;
    private EditTextWithDel newPwdAgain;
    private EditText        confirmCode;
    private UserDao         userDao;
    private User            user;
    String mUserPhone;
    private Button btnGetCode;
    private Button btnSubmit;

    private TextView      mVoiceCaptcha;
    private CaptchaHelper mCaptchaHelper;
    private static final int SMS_CAPTCHA = 1;
    private static final int VOICE_CAPTCHA = 2;
    private int captchaType = 0;//1 sms ;2 voice

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pay_password);

        mHttpService = new HttpService(this, this);
        initView();
    }


    protected void initView() {
        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("找回交易密码").setHeadBackVisible(View.VISIBLE);
        phone = (EditText) findViewById(R.id.phone);
        newPwd = (EditTextWithDel) findViewById(R.id.newPwd);
        newPwdAgain = (EditTextWithDel) findViewById(R.id.newPwdAgain);
        confirmCode = (EditText) findViewById(R.id.confirmCode);
        btnGetCode = (Button) findViewById(R.id.btnGetCode);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        mVoiceCaptcha = (TextView) findViewById(R.id.voiceCaptcha);
        mVoiceCaptcha.setOnClickListener(this);
        mCaptchaHelper = new CaptchaHelper(this, btnGetCode, mVoiceCaptcha);
        btnGetCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        DaoMaster daoMaster = new DaoMaster(new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null).getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();
        List<User> users = userDao.queryBuilder().list();

        if (users != null && users.size() > 0) {
            user = users.get(0);
            mUserPhone = user.getCellPhone();
        }
        if (!TextUtils.isEmpty(mUserPhone)) {
            phone.setText(mUserPhone.substring(0, 3) + "****" + mUserPhone.substring(mUserPhone.length() - 4, mUserPhone.length()));
            phone.setEnabled(false);
        }

    }

    public void showDialog() {
        VerificationCodeDialog codeDialog = VerificationCodeDialog.newInstance(mUserPhone,captchaType);
        codeDialog.setSmsListener(this);
        codeDialog.show(getSupportFragmentManager(),"VerificationCodeDialog");
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal()) {
            String errmsg = mHttpService.onGetVerifyCode(json);
            int msg = json.optInt("msg", -1);
            if (5 == msg) {
                //状态为5,弹出验证码
                mCaptchaHelper.smsFinish();
                showDialog();
            }else if (6 == msg){
                mCaptchaHelper.smsFinish();
            }
            Utils.Toast(this, errmsg);
//            mCaptchaHelper.smsFinish();

        }
        if(reqId == ServiceCmd.CmdId.CMD_Voice_Captcha.ordinal())
        {
            String errmsg = mHttpService.onGetVoiceCaptcha(json);
            int msg = json.optInt("msg", -1);
            if (5 == msg) {
                //状态为5,弹出验证码
                mCaptchaHelper.voiceFinish();
                showDialog();
            }else if (6 == msg){
                mCaptchaHelper.voiceFinish();
            }
            Utils.Toast(this, errmsg);
//            mCaptchaHelper.voiceFinish();
        }

        if (reqId == ServiceCmd.CmdId.CMD_ResetPayPassword.ordinal()) {
//            String msg = mHttpService.onResetPayPassword(json);
//            if (true) {
//                Utils.Toast(this, msg);
//                this.finish();
//            } else {
//                Utils.Toast(this, msg);
//            }
//        }
            int errCode = json.optInt("msg");
            String errMsg = "";
            switch (errCode) {
                case 0:
                    errMsg = "用户不存在";
                    Utils.Toast(this, errMsg);
                    break;

                case 1:
                    errMsg = "操作成功";
                    Utils.Toast(this, errMsg);
                    finish();
                    break;

                case 2:
                    errMsg = "验证码错误";
                    Utils.Toast(this, errMsg);
                    break;

                case 4:
                    errMsg = "短信已超时";
                    Utils.Toast(this, errMsg);
                    break;

                case 5:
                    errMsg = "内部错误";
                    Utils.Toast(this, errMsg);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetCode:
                captchaType = SMS_CAPTCHA;
                doGetCode(SMS_CAPTCHA);
                break;
            case R.id.voiceCaptcha:
                captchaType = VOICE_CAPTCHA;
                doGetCode(VOICE_CAPTCHA);
                break;
            case R.id.btnSubmit:
                doSubmit();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptchaHelper.onRecycle();
    }

    private void doSubmit() {
        String mPhoneStr;
        if (!TextUtils.isEmpty(mUserPhone)) {
            mPhoneStr = mUserPhone;
        } else {
            mPhoneStr = phone.getText().toString();
        }

        String newP;
        String newP2;
        String code;

        newP = newPwd.getText().toString();
        newP2 = newPwdAgain.getText().toString();
        code = confirmCode.getText().toString();

        if (TextUtils.isEmpty(mUserPhone) && TextUtils.isEmpty(mPhoneStr)) {
            phone.requestFocus();
            Utils.Toast(this, "手机号不能为空");
            return;
        }
        if (TextUtils.isEmpty(newP)) {
            newPwd.requestFocus();
            Utils.Toast(this, "输入新交易密码");
            return;
        }
        if (TextUtils.isEmpty(newP2)) {
            newPwdAgain.requestFocus();
            Utils.Toast(this, "再次输入新密码");
            return;
        }
        if (!newP.equals(newP2)) {
            Utils.Toast(this, "两次密码输入不一致");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            confirmCode.requestFocus();
            Utils.Toast(this, "输入验证码");
            return;
        }
        if (!(newP.length() >= 6 && newP.length() <= 20)){
            Utils.Toast(this,"请输入6至20位密码");
            return;
        }

        Md5Algorithm md5 = Md5Algorithm.getInstance();
        String newP_md5 = md5.md5Digest(newP.getBytes());

        mHttpService.resetPayPassword(mPhoneStr,newP_md5,code);
    }

    private void doGetCode(int captchaType) {
        String phoneStr;
        if (!TextUtils.isEmpty(mUserPhone)) {
            phoneStr = mUserPhone;
        } else {
            phoneStr = phone.getText().toString();
        }

        String newP;
        String newP2;
        String code;

        newP = newPwd.getText().toString();
        newP2 = newPwdAgain.getText().toString();

        if (TextUtils.isEmpty(mUserPhone) && TextUtils.isEmpty(phoneStr)) {
            phone.requestFocus();
            Utils.Toast(this, "手机号不能为空");
            return;
        }
        if (TextUtils.isEmpty(newP)) {
            newPwd.requestFocus();
            Utils.Toast(this, "新交易密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(newP2)) {
            newPwdAgain.requestFocus();
            Utils.Toast(this, "再次输入新密码");
            return;
        }
        if (!newP.equals(newP2)) {
            Utils.Toast(this, "两次密码输入不一致");
            return;
        }
        if (!(newP.length() >= 6 && newP.length() <= 20)){
            Utils.Toast(this,"请输入6至20位密码");
            return;
        }

        if (captchaType == SMS_CAPTCHA){
            mCaptchaHelper.smsStart(mHttpService,phoneStr,"0");
//			mHttpService.getVerifyCode(null, null, null, phoneStr, null);
        }else if (captchaType == VOICE_CAPTCHA){
            mCaptchaHelper.voiceStart(mHttpService,phoneStr,"0");
//			mHttpService.getVoiceCaptcha(phoneStr);
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        if(reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal())
        {
            mCaptchaHelper.smsFinish();
        }
        if (reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal()){
            mCaptchaHelper.voiceFinish();
        }
    }

    @Override
    public void smsStart(int type) {
        if (type == 1) {
            mCaptchaHelper.smsStart(null, "", "");
        }else if (type == 2) {
            mCaptchaHelper.voiceStart(null, "", "");
        }
    }
}
