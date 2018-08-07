package cn.vpfinance.vpjr.module.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.util.CaptchaHelper;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import cn.vpfinance.vpjr.view.VerificationCodeDialog;

/**
 * Created by Wang Gensheng on 2015/7/20.
 */
public class ChangeTradePwdFragment extends BaseFragment implements View.OnClickListener, VerificationCodeDialog.SmsListener {

    private static ChangeTradePwdFragment self;

    private Activity mHostActivity;

    private HttpService mHttpService;

    private EditText etPhoneNum;

    private EditTextWithDel etOldTradePwd;

    private EditTextWithDel etNewTradePwd;

    private EditTextWithDel etNewTradePwdAgain;

    private EditText etPhoneCode;

    private String mUserPhone;

    private Button btnGetCode;

    private Button btnSubmit;

    private boolean userHasTradePassword;

    private UserDao       userDao;
    private User          user;
    private TextView      mVoiceCaptcha;
    private CaptchaHelper mCaptchaHelper;
    private static final int SMS_CAPTCHA   = 1;
    private static final int VOICE_CAPTCHA = 2;
    private              int captchaType   = 0;//1 sms ;2 voice


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.layout_change_trade_pwd, null);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHostActivity = activity;
    }

    private void initView(View view) {
        mHttpService = new HttpService(mHostActivity, this);
        etPhoneNum = (EditText) view.findViewById(R.id.phoneNum);
        etOldTradePwd = (EditTextWithDel) view.findViewById(R.id.oldTradePwd);
        etNewTradePwd = (EditTextWithDel) view.findViewById(R.id.newTradePwd);
        etNewTradePwdAgain = (EditTextWithDel) view.findViewById(R.id.newTradePwdAgain);
        etPhoneCode = (EditText) view.findViewById(R.id.etPhoneCode);
        btnGetCode = (Button) view.findViewById(R.id.btnGetCode);
        btnSubmit = (Button) view.findViewById(R.id.submit);
        mVoiceCaptcha = (TextView) view.findViewById(R.id.voiceCaptcha);
        mVoiceCaptcha.setOnClickListener(this);
        mCaptchaHelper = new CaptchaHelper(getActivity(),btnGetCode,mVoiceCaptcha);
        DaoMaster daoMaster = new DaoMaster(new DaoMaster.DevOpenHelper(mHostActivity, Config.DB_NAME, null).getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();
        List<User> users = userDao.queryBuilder().list();
        if (users != null && users.size() > 0) {
            user = users.get(0);
            userHasTradePassword = user.getHasTradePassword();
            mUserPhone = user.getCellPhone();
        }
        if (!TextUtils.isEmpty(mUserPhone)) {
            etPhoneNum.setText(mUserPhone.substring(0, 3) + "****" + mUserPhone.substring(mUserPhone.length() - 4, mUserPhone.length()));
            etPhoneNum.setEnabled(false);
        }

        if (!userHasTradePassword) {
            etOldTradePwd.setVisibility(View.GONE);
            btnSubmit.setText("设  置");
        } else {
            btnSubmit.setText("修  改");
        }

        btnGetCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
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
            Utils.Toast(getActivity(), errmsg);
//            mCaptchaHelper.smsFinish();
        }
        if(reqId == ServiceCmd.CmdId.CMD_Voice_Captcha.ordinal())
        {
            String errmsg = mHttpService.onGetVoiceCaptcha(json);
            int msg = json.optInt("msg", -1);
            if (5 == msg) {
                //状态为5,弹出验证码
                mCaptchaHelper.smsFinish();
                showDialog();
            }else if (6 == msg){
                mCaptchaHelper.voiceFinish();
            }
            Utils.Toast(getActivity(), errmsg);
//            mCaptchaHelper.voiceFinish();
        }

        if (reqId == ServiceCmd.CmdId.CMD_resetTradePassword.ordinal()) {
            String msg = mHttpService.onResetTradePassword2(json);
            if (msg.contains("成功")) {
                if(user!=null)
                {
                    user.setHasTradePassword(true);
                    if(userDao!=null)
                    {
                        userDao.insertOrReplace(user);
                    }
                }
                Utils.Toast(mHostActivity, msg);
                mHostActivity.finish();
            } else {
                Utils.Toast(mHostActivity, msg);
            }
        }
    }

    public static Fragment newInstance() {
        if (self == null) {
            self = new ChangeTradePwdFragment();
        }
        return self;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCaptchaHelper.onRecycle();
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
            case R.id.submit:
                doSubmit();
                break;
        }
    }

    private void doGetCode(int captchaType) {
        String phone;
        if (!TextUtils.isEmpty(mUserPhone)) {
            phone = mUserPhone;
        } else {
            phone = etPhoneNum.getText().toString();
        }

        String old;
        String newP;
        String newP2;
        String code;

        if (!userHasTradePassword) {
            old = etNewTradePwd.getText().toString();
            newP = etNewTradePwd.getText().toString();
            newP2 = etNewTradePwdAgain.getText().toString();

            if (TextUtils.isEmpty(mUserPhone) && TextUtils.isEmpty(phone)) {
                etPhoneNum.requestFocus();
                Utils.Toast(getActivity(), "手机号不能为空");
                return;
            }
            if (TextUtils.isEmpty(newP)) {
                etNewTradePwd.requestFocus();
                Utils.Toast(getActivity(), "新交易密码不能为空");
                return;
            }
            if (TextUtils.isEmpty(newP2)) {
                etNewTradePwdAgain.requestFocus();
                Utils.Toast(getActivity(), "再次输入新密码");
                return;
            }
            if (!newP.equals(newP2)) {
                Utils.Toast(mHostActivity, "两次密码输入不一致");
                return;
            }
            if (!(newP.length() >= 6 && newP.length() <= 20)){
                Utils.Toast(getActivity(),"请输入6至20位密码");
                return;
            }


        } else {
            old = etOldTradePwd.getText().toString();
            newP = etNewTradePwd.getText().toString();
            newP2 = etNewTradePwdAgain.getText().toString();

            if (TextUtils.isEmpty(mUserPhone) && TextUtils.isEmpty(phone)) {
                etPhoneNum.requestFocus();
                Utils.Toast(getActivity(), "手机号不能为空");
                return;
            }
            if (TextUtils.isEmpty(old)) {
                etOldTradePwd.requestFocus();
                Utils.Toast(getActivity(), "输入原交易密码");
                return;
            }
            if (TextUtils.isEmpty(newP)) {
                etNewTradePwd.requestFocus();
                Utils.Toast(getActivity(), "输入新交易密码");
                return;
            }
            if (TextUtils.isEmpty(newP2)) {
                etNewTradePwdAgain.requestFocus();
                Utils.Toast(getActivity(), "再次输入新密码");
                return;
            }
            if (!newP.equals(newP2)) {
                Utils.Toast(mHostActivity, "两次密码输入不一致");
                return;
            }
        }

        if (!(newP.length() >= 6 && newP.length() <= 20)){
            Utils.Toast(getActivity(),"请输入6至20位密码");
            return;
        }

        if (captchaType == SMS_CAPTCHA){
            mCaptchaHelper.smsStart(mHttpService,phone,"0");
//			mHttpService.getVerifyCode(null, null, null, phone, null);
        }else if (captchaType == VOICE_CAPTCHA){
            mCaptchaHelper.voiceStart(mHttpService,phone,"0");
//			mHttpService.getVoiceCaptcha(phone);
        }
    }

    public void showDialog() {
        VerificationCodeDialog codeDialog = VerificationCodeDialog.newInstance(mUserPhone,captchaType);
        codeDialog.setSmsListener(this);
        codeDialog.show(getFragmentManager(),"VerificationCodeDialog");
    }

    private void doSubmit() {
        String phone;
        if (!TextUtils.isEmpty(mUserPhone)) {
            phone = mUserPhone;
        } else {
            phone = etPhoneNum.getText().toString();
        }

        String old;
        String newP;
        String newP2;
        String code;

        if (!userHasTradePassword) {
            old = etNewTradePwd.getText().toString();
            newP = etNewTradePwd.getText().toString();
            newP2 = etNewTradePwdAgain.getText().toString();
            code = etPhoneCode.getText().toString();

            if (TextUtils.isEmpty(mUserPhone) && TextUtils.isEmpty(phone)) {
                etPhoneNum.requestFocus();
                Utils.Toast(getActivity(), "手机号不能为空");
                return;
            }
            if (TextUtils.isEmpty(newP)) {
                etNewTradePwd.requestFocus();
                Utils.Toast(getActivity(), "输入新交易密码");
                return;
            }
            if (TextUtils.isEmpty(newP2)) {
                etNewTradePwdAgain.requestFocus();
                Utils.Toast(getActivity(), "再次输入新密码");
                return;
            }
            if (!newP.equals(newP2)) {
                Utils.Toast(mHostActivity, "两次密码输入不一致");
                return;
            }
            if (TextUtils.isEmpty(code)) {
                etPhoneCode.requestFocus();
                Utils.Toast(getActivity(), "输入验证码");
                return;
            }
            if (!(newP.length() >= 6 && newP.length() <= 20)){
                Utils.Toast(getActivity(),"请输入6至20位密码");
                return;
            }


            Md5Algorithm md5 = Md5Algorithm.getInstance();
            String old_md5 = md5.md5Digest(old.getBytes());
//            String newP_md5 = md5.md5Digest(newP.getBytes());
            mHttpService.resetTradePassword2("" + (userHasTradePassword ? 2 : 1), old_md5, "", code,
                    AppState.instance().getSessionCode());
        } else {
            old = etOldTradePwd.getText().toString();
            newP = etNewTradePwd.getText().toString();
            newP2 = etNewTradePwdAgain.getText().toString();
            code = etPhoneCode.getText().toString();

            if (TextUtils.isEmpty(mUserPhone) && TextUtils.isEmpty(phone)) {
                etPhoneNum.requestFocus();

                return;
            }
            if (TextUtils.isEmpty(old)) {
                etOldTradePwd.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(newP)) {
                etNewTradePwd.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(newP2)) {
                etNewTradePwdAgain.requestFocus();
                return;
            }
            if (!newP.equals(newP2)) {
                Utils.Toast(mHostActivity, "两次密码输入不一致");
                return;
            }
            if (TextUtils.isEmpty(code)) {
                etPhoneCode.requestFocus();
                return;
            }
            if (!(newP.length() >= 6 && newP.length() <= 20)){
                Utils.Toast(getActivity(), "请输入6至20位密码");
                return;
            }

            Md5Algorithm md5 = Md5Algorithm.getInstance();
            String old_md5 = md5.md5Digest(old.getBytes());
            String newP_md5 = md5.md5Digest(newP.getBytes());
            mHttpService.resetTradePassword2("" + (userHasTradePassword ? 2 : 1), old_md5, newP_md5, code,
                    AppState.instance().getSessionCode());
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
