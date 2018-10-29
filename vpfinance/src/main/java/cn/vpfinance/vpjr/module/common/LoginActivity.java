package cn.vpfinance.vpjr.module.common;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.mob.tools.utils.SharePrefrenceHelper;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
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
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.gusturelock.LockSetupActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

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
    @Bind(R.id.tvForget)
    TextView tvForget;
    @Bind(R.id.ibWeiXinLogin)
    ImageButton ibWeiXinLogin;
    @Bind(R.id.scroll_view_container)
    ScrollView scrollViewContainer;
    @Bind(R.id.ll_acitvity)
    LinearLayout llAcitvity;
    @Bind(R.id.iv_logo)
    ImageView ivLogo;


    private HttpService mHttpService;
    private UserDao dao = null;
    private User user;
    private String username;
    private String password;
    private boolean isPersonType;
    private boolean isFirstClick = true;
    private int screenHeight;
    private int keyHeight;
    private String personPhone;
    private String companyUsername;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        isPersonType = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, true);
        resetSv();
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
        if (isPersonType) {//个人用户
            titleBar.setTitle("登录");
            tvRegister.setText("立即注册");
            etUsername.setHint("请输入手机号/用户名");
            titleBar.setActionRight("企业用户");
        } else {//企业用户
            titleBar.setTitle("企业登录");
            tvRegister.setText("企业注册");
            etUsername.setHint("请输入企业名称/邮箱");
            titleBar.setActionRight("个人用户");
        }

        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        personPhone = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_CELL_PHONE);
        companyUsername = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_COMPANY_USER_NAME);
        String name = "";
        if (isPersonType) {//个人用户记住之前登录手机号,企业用户记住企业名称
            etUsername.setText(personPhone);
            name = personPhone;
        } else {
            etUsername.setText(companyUsername);
            name = companyUsername;
        }
//        etUsername.requestFocus();
        if (!TextUtils.isEmpty(name)) {//设置光标在后面
            etUsername.setSelection(name.length());
        }
        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                scrollViewContainer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollViewContainer.smoothScrollTo(0, ivLogo.getBottom());
                    }
                }, 500);
            }
        });
        if (isFirstClick) {
            etUsername.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    scrollViewContainer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewContainer.smoothScrollTo(0, ivLogo.getBottom());
                            isFirstClick = false;
                        }
                    }, 500);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 根据软键盘的高度设置ScrollView的marginBottom
     */
    private void resetSv() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final View decorView = getWindow().getDecorView();
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect rect = new Rect();
                    //getWindowVisibleDisplayFrame(rect)可以获取到程序显示的区域，包括标题栏
                    // 但不包括状态栏,获取后的区域坐标会保存在rect(Rect类型)中
                    decorView.getWindowVisibleDisplayFrame(rect);
                    int screenHeight = Utils.getScreenHeight(LoginActivity.this);
                    int heightDifference = screenHeight - rect.bottom;//计算软键盘占有的高度  = 屏幕高度 - 视图可见高度
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) scrollViewContainer.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, heightDifference);//设置sv的marginBottom的值为软键盘占有的高度即可
                    scrollViewContainer.requestLayout();
                }
            });
        }
    }

    private void changeUserType() {

        isPersonType = !isPersonType;
        if (isPersonType) {//个人用户
            titleBar.setTitle("登录");
            tvRegister.setText("立即注册");
            titleBar.setActionRight("企业用户");
            if (!TextUtils.isEmpty(personPhone)) {
                etUsername.setText(personPhone);
            } else {
                etUsername.setText("");
                etUsername.setHint("请输入手机号/用户名");
            }
        } else {//企业用户
            titleBar.setTitle("企业登录");
            tvRegister.setText("企业注册");
            titleBar.setActionRight("个人用户");
            if (!TextUtils.isEmpty(companyUsername)) {
                etUsername.setText(companyUsername);
            } else {
                etUsername.setText("");
                etUsername.setHint("请输入企业名称/邮箱");
            }
        }
        etUsername.setSelection(etUsername.getText().length());
        SharedPreferencesHelper.getInstance(this).putBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, isPersonType);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //清理login present标志
            HttpService.clearPresentLoginFlag();
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_WEIXIN_LOGIN_SUCCESS)) {//微信登录成功
            finish();
        }
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_REGISTER_FINISH)) {//注册成功
            finish();
        }
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_WEIXIN_REGISTER_SUCCESS)) {//微信注册成功
            finish();
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
                RegisterActivity.goThis(this, isPersonType);
                break;
            case R.id.tvForget:
//                gotoActivity(ResetLoginPswActivity.class);
                ForgetLoginPasswordActivity.goThis(this, isPersonType);
                break;
            case R.id.btnLogin:
                clearDB();
                doLogin();
                break;
            case R.id.ibWeiXinLogin:
                IWXAPI mWxApi = ((FinanceApplication) getApplication()).mWxApi;
                if (!mWxApi.isWXAppInstalled()) {
                    Utils.Toast("您还未安装微信客户端");
                    return;
                }
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                preferencesHelper.putBooleanValue(SharedPreferencesHelper.KEY_WX_BIND_IS_FROM_SETTING, false);
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "diandi_wx_login";
                mWxApi.sendReq(req);
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

        if (isPersonType) {//个人用户
            mHttpService.userLogin(username, password);
        } else {//企业用户
            mHttpService.enterpriseUserLogin(username, password);
        }
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
                String cellPhone = user.getCellPhone();
                if (isPersonType) {//个人用户
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_CELL_PHONE, cellPhone);
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, username);
                } else {//企业用户
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_COMPANY_USER_NAME, username);//保存登录企业用户名
                }
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
                if (!TextUtils.isEmpty(preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_WEIXIN_UNIONID))) {//是账号密码登录,如之前是微信登录,清除unionid
                    preferencesHelper.removeKey(SharedPreferencesHelper.KEY_WEIXIN_UNIONID);
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
        if (req == ServiceCmd.CmdId.CMD_userLogin.ordinal() || req == ServiceCmd.CmdId.CMD_enterpriseUserLogin.ordinal() && (!isFinishing())) {
            String msg = mHttpService.onUserLogin(this, json);
            if (msg.equals("3")) {
                if (isPersonType) {
                    Utils.Toast(this, "企业用户请切换企业登录模式");
                } else {
                    Utils.Toast(this, "个人用户请切换个人登录模式");
                }
                return;
            } else if (!msg.equals("")) {
                Utils.Toast(this, msg);
                findViewById(R.id.btnLogin).setEnabled(true);
                return;
            } else {
                FinanceApplication application = (FinanceApplication) getApplication();
                application.isLogin = true;
                SharedPreferencesHelper.getInstance(this).putBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, isPersonType);
                mHttpService.getUserInfo();
                getUser();
            }
        }
    }
}
