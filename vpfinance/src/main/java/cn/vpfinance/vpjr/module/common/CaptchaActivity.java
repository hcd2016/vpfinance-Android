package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.gusturelock.LockSetupActivity;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.VerificationCodeDialog;
import de.greenrobot.dao.query.QueryBuilder;
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
    private UserDao dao = null;
    private User user;

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
        startSms();
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
        if(userRegisterBean.isWxPhoneRegister) {
            btnNext.setText("完成绑定");
        }else {
            btnNext.setText("下一步");
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
            tvPhoneHint.setText("短信验证码已发送至绑定手机 " + FormatUtils.hidePhone(phone));
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
                if ("获取验证码".equals(btnGetCaptcha.getText().toString())) {
                    showCodeDialog(1);
//                    mHttpService.getRegisterCaptchaSms(phone);
                } else {
                    Utils.Toast("请先稍等一下语音验证码");
                }
                break;
            case R.id.tvVoiceCaptcha:
                if ("语音验证码".equals(tvVoiceCaptcha.getText().toString())) {
                    showCodeDialog(2);
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
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_RESET_PWD_SUCCESS)) {//重置密码成功
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
                    if(userRegisterBean.getPwdSetType() == 1) {//是注册
                        if (userRegisterBean.getIsFromWeixin()) {//是微信绑定
                            if (userRegisterBean.getUserType()) {
                                mHttpService.bindWEIXIN(userRegisterBean.getUnionid(), userRegisterBean.getOpenid(), "1", userRegisterBean.getPhoneNum(), etCaptcha.getText().toString(),"");
                            } else {
                                mHttpService.bindWEIXIN(userRegisterBean.getUnionid(), userRegisterBean.getOpenid(), "2", userRegisterBean.getEmail(), etCaptcha.getText().toString(),"");
                            }
                        } else {
                            userRegisterBean.setCaptcha(etCaptcha.getText().toString());
                            if (type == REGISTER_PERSON) {
                                LoginPasswordActivity.goThis(this, userRegisterBean);
                            } else {
                                RegisterCompanyInfoActivity.goThis(this, userRegisterBean);
                            }
                        }
                    }else {//是修改密码
                        userRegisterBean.setCaptcha(etCaptcha.getText().toString());
                        LoginPasswordActivity.goThis(this, userRegisterBean);
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
                    Long uid = json.optLong("uid");
                    clearDB();
                    if (null != uid && !TextUtils.isEmpty(uid+"")) {
                        loginSucess(uid);
                        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                        String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
                        if (!uid.equals(savedUid)) {
                            preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID, uid+"");
                        }
                        Utils.Toast("登录成功!");
                        if(!TextUtils.isEmpty(userRegisterBean.getUnionid())) {//保存用户unionid
                            preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_WEIXIN_UNIONID,userRegisterBean.getUnionid());
                        }
                    }
                    App application = (App) getApplication();
                    application.isLogin = true;
                    SharedPreferencesHelper.getInstance(this).putBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, userRegisterBean.getUserType());
                    getUser();
                    mHttpService.getUserInfo();
                    break;
                case "2"://此用户为企业用户，不允许注册，请跳转到企业注册页面
                    Utils.Toast("该企业未注册，请前往企业注册页面");
                    userRegisterBean.setCaptcha(etCaptcha.getText().toString());
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
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            mHttpService.onGetUserInfo(json, user);

            String message = json.optString("message");
            if (!TextUtils.isEmpty(message) && message.contains("没有登陆")) {
            } else {
                String isNewUser = json.optString("isNewUser", "0");
                boolean isNewUserBoolean = "1".equals(isNewUser) ? true : false;
                SharedPreferencesHelper.getInstance(this).putBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER, isNewUserBoolean);
            }
            int needUpdatePwd = json.optInt("needUpdatePwd", 0);//1就是需要修改密码
            if (needUpdatePwd == 1) {
                ((App) getApplication()).isNeedUpdatePwd = true;
            }

            if (user != null) {
                if (dao != null && AppState.instance().logined()) {
                    dao.insertOrReplace(user);
                }
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                String username = user.getUserName();
                String cellPhone = user.getCellPhone();
                if(userRegisterBean.getUserType()) {//个人用户
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_CELL_PHONE, cellPhone);
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, username);
                }else {//企业用户
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_COMPANY_USER_NAME, username);//保存登录企业用户名
                }
                preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME, username);
                if (user != null) {
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_ID, "" + user.getId());
                }
                String uid = AppState.instance().getSessionCode();
                String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
                if (!TextUtils.isEmpty(uid) && !uid.equals(savedUid)) {
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID, uid);
                }

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                //清理login present标志
                HttpService.clearPresentLoginFlag();
                ((App) getApplication()).login = true;
                startActivity(new Intent(this, LockSetupActivity.class));
                EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_WEIXIN_LOGIN_SUCCESS));//微信登录成功
                finish();
            }
        }
    }

    public void loginSucess(long uid) {
        AppState.instance().setSessionCode("" + uid);
        User user = new User();
        user.setUserId(uid);
        user.setUserName("");
        user.setRealName("");
        user.setCellPhone("");
        user.setUserpass("");
        user.setIdentityCard("");
        user.setCashBalance(0d);
        user.setNetAsset(0d);
        user.setFrozenAmtN(0d);
        user.setPaying(0d);

        user.setDBid(0d);
        user.setDSum(0d);
        user.setInvest(0d);
        user.setPreIncome(0d);
        user.setHasTradePassword(false);

        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        UserDao dao;

        dbHelper = new DaoMaster.DevOpenHelper(CaptchaActivity.this, Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getUserDao();

        if (dao != null) {
            QueryBuilder<User> qb = dao.queryBuilder();
            qb.buildDelete().executeDeleteWithoutDetachingEntities();
            dao.insertInTx(user);
            db.close();
        }
    }

    public void getUser() {
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        if (dao == null) {
            dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
            db = dbHelper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            dao = daoSession.getUserDao();
        }

        if (dao != null) {
            QueryBuilder<User> qb = dao.queryBuilder();
            List<User> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                user = userList.get(0);
            }
        }
    }

    private void clearDB() {
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        BankCardDao bankDao;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        bankDao = daoSession.getBankCardDao();
        bankDao.deleteAll();
        daoSession.getUserDao().deleteAll();
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

    public void showCodeDialog(int codeType) {
        VerificationCodeDialog codeDialog = VerificationCodeDialog.newInstance(phone, codeType,1);
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
