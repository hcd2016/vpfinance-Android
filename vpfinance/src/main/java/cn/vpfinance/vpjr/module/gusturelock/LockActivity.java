package cn.vpfinance.vpjr.module.gusturelock;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.tdk.utils.HttpDownloader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.util.StatusBarCompat1;

//import android.util.Log;

public class LockActivity extends Activity implements
        LockPatternView.OnPatternListener {
    private static final String TAG = "LockActivity";

    public static final String NAME_AUTO_LOGIN = "auto_login";

    private HttpService mHttpService;

    private boolean autoLogin = false;

    
    private String saved_uid;
    private String saved_name;
    private String saved_logPwd;

    private int error_times = 0;
    public static final int TIME_LOCK = 5 * 60 * 1000;//5 * 60 * 1000;
//     public static final int TIME_LOCK = 6 * 1000;//5 * 60 * 1000;

    private List<LockPatternView.Cell> lockPattern;
    private LockPatternView lockPatternView;
    private TextView tip;
    private boolean isPersonType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent!=null)
        {
            autoLogin = intent.getBooleanExtra(NAME_AUTO_LOGIN,false);
        }

        final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        String patternString = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING, null);
        if (patternString == null) {
            finish();
            return;
        }

        saved_name = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME);
        saved_uid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_ID);
        saved_logPwd = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD);
        isPersonType = preferencesHelper.getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, true);
        if (TextUtils.isEmpty(saved_uid) || TextUtils.isEmpty(saved_logPwd))
        {
            autoLogin = false;
        }

        lockPattern = LockPatternView.stringToPattern(patternString);

        setContentView(R.layout.activity_lock);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompat1.translucentStatusBar(this);
            TextView topText = (TextView) findViewById(R.id.top_text);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) topText.getLayoutParams();
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int statusBarHeight = StatusBarCompat1.getStatusBarHeight(this);
            layoutParams.setMargins(0,statusBarHeight,0,statusBarHeight);
            topText.setLayoutParams(layoutParams);
        }

        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        lockPatternView.setTactileFeedbackEnabled(false);

        tip = (TextView) findViewById(R.id.tip);
        TextView username = (TextView) findViewById(R.id.username);
        username.setText(saved_name);

        View forget = findViewById(R.id.forget);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(LockActivity.this);
                preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_STRING);
                startActivity(new Intent(LockActivity.this, LoginActivity.class));
                finish();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(LockActivity.this);
                preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_STRING);
                startActivity(new Intent(LockActivity.this, LoginActivity.class));
                finish();
            }
        });


        if (mHttpService == null)
        {
            mHttpService = new HttpService(this, new HttpDownloader.HttpDownloaderListener()
            {

                @Override
                public void onHttpSuccess(int reqId, JSONObject json) {
                    if (json == null || isFinishing() || Common.isForceLogout(LockActivity.this,json)) return;
                    if (reqId == ServiceCmd.CmdId.CMD_userLogin.ordinal() || reqId == ServiceCmd.CmdId.CMD_enterpriseUserLogin.ordinal()) {
                        String msg = mHttpService.onUserLogin(LockActivity.this,json);
                        if (msg.equals("3")) {
                            if (isPersonType) {
                                Utils.Toast(LockActivity.this, "企业用户请切换企业登录模式");
                            } else {
                                Utils.Toast(LockActivity.this, "个人用户请切换个人登录模式");
                            }
                            return;
                        }else if(!msg.equals("")) {
                            Utils.Toast(LockActivity.this, msg);
                        }
                        else
                        {
                            String uid = AppState.instance().getSessionCode();
                            String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
                            if(!TextUtils.isEmpty(uid) && !uid.equals(savedUid))
                            {
                                preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID,uid);
                            }
                            finish();
                        }
                    }
                }

                @Override
                public void onHttpSuccess(int reqId, JSONArray json) {

                }

                @Override
                public void onHttpCache(int reqId) {

                }

                @Override
                public void onHttpError(int reqId, String errmsg) {
                    if (reqId == ServiceCmd.CmdId.CMD_userLogin.ordinal()) {
                        Toast.makeText(LockActivity.this, "自动登录错误，请重新登录", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LockActivity.this, LoginActivity.class));
                        logout();
                        finish();
                    }
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // disable back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPatternStart() {
//        Log.d(TAG, "onPatternStart");
    }

    @Override
    public void onPatternCleared() {
//        Log.d(TAG, "onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
//        Log.d(TAG, "onPatternCellAdded");
//        Log.e(TAG, LockPatternView.patternToString(pattern));
        // Toast.makeText(this, LockPatternView.patternToString(pattern),
        // Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
//        Log.d(TAG, "onPatternDetected");

        if (pattern.equals(lockPattern)) {
            tip.setText("");
            error_times = 0;
            if (!Utils.isNetworkAvailable(this)) {
                Toast.makeText(this, "没有可用网络请重试.", Toast.LENGTH_LONG).show();
                return;
            }
            if(autoLogin)
            {
                if(isPersonType) {
                    mHttpService.userLogin(saved_name, saved_logPwd);
                }else {
                    mHttpService.enterpriseUserLogin(saved_name, saved_logPwd);
                }
            }
            else
            {
                finish();
            }
        } else {
            lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            tip.setTextColor(Color.parseColor("#26EEFA"));
            error_times++;
            if (error_times > 4) {
                Toast.makeText(this, "输入错误次数超过5次,请重新登录", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                logout();
                finish();
            }
            tip.setText("密码错误，还可以尝试"+(5-error_times)+"次");
            //Toast.makeText(this, , Toast.LENGTH_LONG).show();
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        Message message = new Message();
                        message.what = CLEAR;
                        mHandle.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }
    private static final int CLEAR = 0;
    private static class MyHandler extends Handler {
        private WeakReference<Activity> reference;

        public MyHandler(Activity activity) {
            reference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LockActivity activity = (LockActivity) reference.get();
            if (msg.what == CLEAR) {
                if (activity != null) {
                    activity.lockPatternView.clearPattern();
                }
            }
            super.handleMessage(msg);
        }
    }

    private Handler mHandle = new MyHandler(this);

    public void logout() {
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        BankCardDao dao;
        UserDao userDao;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getBankCardDao();
        userDao = daoSession.getUserDao();
        dao.deleteAll();
        userDao.deleteAll();

        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_STRING);
        preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_USER_ID);

        mHttpService.logout();
        AppState.instance().logout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
