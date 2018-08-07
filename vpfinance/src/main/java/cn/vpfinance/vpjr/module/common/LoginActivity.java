package cn.vpfinance.vpjr.module.common;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

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
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.gusturelock.LockSetupActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import de.greenrobot.dao.query.QueryBuilder;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Bind(R.id.etUsername)
    EditTextWithDel etUsername;
    @Bind(R.id.etPassword)
    EditTextWithDel etPassword;
    @Bind(R.id.btnLogin)
    Button btnLogin;
    @Bind(R.id.tvRegister)
    TextView tvRegister;

    private boolean isPersonType = true;

    private HttpService mHttpService;
    private UserDao dao = null;
    private User user;
    private String username;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mHttpService = new HttpService(this, this);

        titleBar
                .setTitle("登录")
                .setImageButtonLeft(R.drawable.toolbar_x, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setActionRight("企业用户", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeUserType();
                    }
                });


        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        String name = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME);
        etUsername.setText(name);
        etUsername.requestFocus();
        if (!TextUtils.isEmpty(name)) {
            etUsername.setSelection(name.length());
        }
    }

    private void changeUserType(){
        isPersonType = !isPersonType;
        if (isPersonType){//个人用户
            titleBar.setTitle("登录");
            tvRegister.setText("立即注册");
            etUsername.setHint("请输入用户名/手机号");
            titleBar.setActionRight("企业用户");
        }else{//企业用户
            titleBar.setTitle("企业登录");
            tvRegister.setText("企业注册");
            etUsername.setHint("请输入企业名称/邮箱");
            titleBar.setActionRight("个人用户");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //清理login present标志
            HttpService.clearPresentLoginFlag();
        }

        return super.onKeyDown(keyCode, event);
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

        if (dao != null && AppState.instance().logined()) {
            QueryBuilder<User> qb = dao.queryBuilder();
            List<User> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                user = userList.get(0);
            }
        }
    }

    @OnClick({R.id.tvForget, R.id.tvRegister, R.id.ibWeiXinLogin, R.id.btnLogin})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRegister:
                RegisterActivity.goThis(this,isPersonType);
                break;
            case R.id.tvForget:
//                gotoActivity(ResetLoginPswActivity.class);
                ForgetLoginPasswordActivity.goThis(this,isPersonType);
                break;
            case R.id.btnLogin:
                clearDB();
                doLogin();
                break;
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


    private void doLogin() {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if ("".equals(username) || "".equals(password)) {
            Utils.Toast(this, "用户名或密码不能为空！请重新输入.");
            return;
        }

        if (password.length() <= 0 && password.length() > 0) {
            Utils.Toast(this, "登录密码不能为纯空格!");
            return;
        }

        if (password.length() < 6) {
            Utils.Toast(this, "登录密码不能少于6位!");
            return;
        }

        Md5Algorithm md5 = Md5Algorithm.getInstance();
        password = md5.md5Digest((password + HttpService.LOG_KEY).getBytes());
        mHttpService.userLogin(username, password);
    }


    @Override
    public void onHttpSuccess(int req, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (req == ServiceCmd.CmdId.CMD_member_center.ordinal() && (!isFinishing())) {
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
                ((FinanceApplication) getApplication()).isNeedUpdatePwd = true;
            }

            if (user != null) {
                if (dao != null && AppState.instance().logined()) {
                    dao.insertOrReplace(user);
                }
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
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
        if (req == ServiceCmd.CmdId.CMD_userLogin.ordinal() && (!isFinishing())) {
            String msg = mHttpService.onUserLogin(this, json);
            if (!msg.equals("")) {
                Utils.Toast(this, msg);
                findViewById(R.id.btnLogin).setEnabled(true);
                return;
            } else {
                FinanceApplication application = (FinanceApplication) getApplication();
                application.isLogin = true;
                //登录成功保存是否是个人账户
                application.isPersonType = isPersonType;
                mHttpService.getUserInfo();
                getUser();
            }
        }
    }
}
