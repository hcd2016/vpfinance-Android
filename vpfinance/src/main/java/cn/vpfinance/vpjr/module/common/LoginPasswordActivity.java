package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.gusturelock.LockSetupActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

public class LoginPasswordActivity extends BaseActivity {

    public static final int PASSWORD_SETTING = 1; //设置登录密码
    public static final int PASSWORD_RESET = 2; //重置登录密码

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Bind(R.id.et_first_pwd)
    EditTextWithDel etFirstPwd;
    @Bind(R.id.tv_psd_level)
    TextView tvPsdLevel;
    @Bind(R.id.et_second_pwd)
    EditTextWithDel etSecondPwd;
    @Bind(R.id.btn_regiter)
    Button btnRegiter;
    private HttpService httpService;
    private UserRegisterBean userRegisterBean;
    private UserDao dao = null;
    private User user;

    public static void goThis(Context context, UserRegisterBean userRegisterBean) {
        Intent intent = new Intent(context, LoginPasswordActivity.class);
        intent.putExtra("userRegisterBean", userRegisterBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("设置密码").setHeadBackVisible(View.VISIBLE);
        userRegisterBean = (UserRegisterBean) getIntent().getSerializableExtra("userRegisterBean");
        if(userRegisterBean.getPwdSetType() == 1) {//注册密码
            btnRegiter.setText("注册完成");
        }else {//(忘记密码)重置密码
            btnRegiter.setText("完成");
        }
        etFirstPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = Common.checkPasswordStrength(editable.toString());//检测密码强度
                if (!TextUtils.isEmpty(s)) {
                    String replace = s.replace("密码强度:", "");
                    tvPsdLevel.setText(replace);
                }
            }
        });
        httpService = new HttpService(this, this);
    }

    @OnClick(R.id.btn_regiter)
    public void onViewClicked() {//完成注册
        String firstPassword = etFirstPwd.getText().toString();
        String secondPassword = etSecondPwd.getText().toString();
        if (TextUtils.isEmpty(firstPassword) || TextUtils.isEmpty(secondPassword)) {
            Utils.Toast("密码不能为空");
            return;
        }
        if (!firstPassword.equals(secondPassword)) {
            Utils.Toast("两次密码输入不一致");
            return;
        }
        String passwordPassMsg = Common.isPasswordPass(firstPassword);
        if (!TextUtils.isEmpty(passwordPassMsg)) {
            Utils.Toast(passwordPassMsg);
            return;
        }
        String uPwd = etFirstPwd.getText().toString();
        if (null != userRegisterBean) {//完成注册请求
            if(userRegisterBean.getPwdSetType() == 1) {//是注册
                if(userRegisterBean.getIsFromWeixin()) {//是微信注册,调用微信注册接口
                    if(userRegisterBean.getUserType()) {
                        httpService.weixinRegiter(userRegisterBean.getUnionid(),userRegisterBean.getOpenid(),"1",
                                userRegisterBean.getPhoneNum(),userRegisterBean.getCaptcha(),etFirstPwd.getText().toString(),"1");
                    }else {
                        httpService.weixinRegiter(userRegisterBean.getUnionid(),userRegisterBean.getOpenid(),"2",
                                userRegisterBean.getPhoneNum(),userRegisterBean.getCaptcha(),etFirstPwd.getText().toString(),"1");
                    }
                }else {//是普通注册
                    httpService.userRegister(userRegisterBean.getReferrerNum(), userRegisterBean.getPhoneNum(),
                            Utils.md5encodeNew(uPwd), userRegisterBean.getPhoneNum(), userRegisterBean.getCaptcha());
                }
            }else {//是重置密码
                if(userRegisterBean.getUserType()) {
                    httpService.resetPwdPerson(userRegisterBean.getPhoneNum(),Utils.md5encodeNew(uPwd),userRegisterBean.getCaptcha());
                }else {
                    httpService.resetPwdCompany(userRegisterBean.getEmail(),Utils.md5encodeNew(uPwd),userRegisterBean.getCaptcha());
                }
            }
        }
    }

    public void getUser() {
        clearDB();
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

        if (dao != null && AppState.instance().logined()) {
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

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (null != json) {
            if(reqId == ServiceCmd.CmdId.CMD_userRegister.ordinal()) {
                String msg = json.optString("msg");
                switch (msg) {
                    case "0":
                        Utils.Toast("验证码错误");
                        break;
                    case "1":
                        Utils.Toast("注册成功");
                        EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_REGISTER_FINISH));
                        //跳转手势密码
                        FinanceApplication application = (FinanceApplication) getApplication();
                        application.isLogin = true;
                        //登录成功保存是否是个人账户
                        application.isPersonType = userRegisterBean.getUserType();
                        httpService.getUserInfo();
                        getUser();
                        finish();
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    case "5":
                        break;
                }
            }
            if(reqId == ServiceCmd.CmdId.CMD_WEIXIN_REGISTER.ordinal()) {
                String msg = json.optString("msg");
                switch (msg) {
                    case "0":
                        Utils.Toast("信息有误");
                        break;
                    case "1":
                        Utils.Toast("注册成功");
                        EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_WEIXIN_REGISTER_SUCCESS));
                        finish();
                        //跳转手势密码
                        FinanceApplication application = (FinanceApplication) getApplication();
                        application.isLogin = true;
                        //登录成功保存是否是个人账户
                        application.isPersonType = userRegisterBean.getUserType();
                        httpService.getUserInfo();
                        getUser();
                        finish();
                        break;
                    case "2":
                        Utils.Toast("注册出现错误");
                        break;
                    case "4":
                        Utils.Toast("手机号己被注册");
                        break;
                    case "5":
                        Utils.Toast("验证码错误");
                        break;
                    case "10":
                        Utils.Toast("非个人用户不允许注册");
                        break;
                }
            }
            if(reqId == ServiceCmd.CmdId.CMD_RESET_PWD_PERSON.ordinal() || reqId == ServiceCmd.CmdId.CMD_RESET_PWD_COMPANY.ordinal()) {//个人重置
                String msg = json.optString("msg");
                switch (msg) {
                    case "0":
                        Utils.Toast("验证码错误");
                        break;
                    case "1":
                        Utils.Toast("重置密码成功");
                        EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_RESET_PWD_SUCCESS));
                        finish();
                        break;
                    case "2":
                        Utils.Toast("服务器异常");
                        break;
                    case "4":
                        Utils.Toast("短信验证码超时");
                        break;
                    case "5":
                        Utils.Toast("用户不存在");
                        break;
                }
            }
            if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal() && (!isFinishing())) {
                httpService.onGetUserInfo(json, user);

                String message = json.optString("message");
                if (!TextUtils.isEmpty(message) && message.contains("没有登陆")) {
                } else {
                    String isNewUser = json.optString("isNewUser", "0");
                    boolean isNewUserBoolean = "1".equals(isNewUser) ? true : false;
                    SharedPreferencesHelper.getInstance(this).putBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER, isNewUserBoolean);
                }
                int needUpdatePwd = json.optInt("needUpdatePwd", 0);//1就是需要修改密码
                if (needUpdatePwd == 1) {
                    ((FinanceApplication) getApplication()).isNeedUpdatePwd = true;
                }

                if (user != null) {
                    if (dao != null && AppState.instance().logined()) {
                        dao.insertOrReplace(user);
                    }
                    SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                    String username = user.getUserName();
                    String password = etFirstPwd.getText().toString();
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, username);
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME, username);
                    if (user != null) {
                        preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_ID, "" + user.getId());
                    }
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD, password);

                    String uid = AppState.instance().getSessionCode();
                    String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
                    if (!TextUtils.isEmpty(uid) && !uid.equals(savedUid)) {
                        preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID, uid);
                    }

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    //清理login present标志
                    HttpService.clearPresentLoginFlag();
                    ((FinanceApplication) getApplication()).login = true;
                    startActivity(new Intent(this, LockSetupActivity.class));
                    finish();
                }
            }
        }
    }
}
