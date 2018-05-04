package cn.vpfinance.vpjr.module.gusturelock;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.tdk.utils.HttpDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.gusturelock.LockPatternView.Cell;
import cn.vpfinance.vpjr.module.gusturelock.LockPatternView.DisplayMode;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.util.StatusBarCompat1;
import de.greenrobot.dao.query.QueryBuilder;

public class LockSetupActivity extends Activity implements
        LockPatternView.OnPatternListener, OnClickListener {

    private HttpService mHttpService;
    private String username;

    private static final String TAG = "LockSetupActivity";
    private LockPatternView lockPatternView;
    private TextView leftButton;
    private TextView rightButton;
    private TextView tvTitle;
    private TextView tip;

    private static final int STEP_1 = 1; // 开始
    private static final int STEP_2 = 2; // 第一次设置手势完成
    private static final int STEP_3 = 3; // 按下继续按钮
    private static final int STEP_4 = 4; // 第二次设置手势完成
    // private static final int SETP_5 = 4; // 按确认按钮
    private static final int ERROR = 100;

    private int step;

    private static class MyHandler extends Handler {
        private WeakReference<Activity> reference;

        public MyHandler(Activity activity) {
            reference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LockSetupActivity activity = (LockSetupActivity) reference.get();
            if (activity == null) return;
            if (msg.what == ERROR && activity.step == STEP_4) {
                //                Toast.makeText(LockSetupActivity.this,"请重新设置",Toast.LENGTH_SHORT).show();
                activity.tvTitle.setText("手势密码设置");
                activity.tvTitle.setTextColor(Color.WHITE);
                activity.gotoTryAgain();
            }
            super.handleMessage(msg);
        }
    }

    private Handler mHandle = new MyHandler(this);

    private List<Cell> choosePattern;

    private boolean confirm = false;
    private MiniLockPatternView miniLockPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setup);
        miniLockPattern = ((MiniLockPatternView) findViewById(R.id.minilock));
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        lockPatternView.setTactileFeedbackEnabled(false);
        leftButton = (TextView) findViewById(R.id.left_btn);
        rightButton = (TextView) findViewById(R.id.right_btn);
        tvTitle = (TextView) findViewById(R.id.textView2);
        tip = (TextView) findViewById(R.id.tip);
        findViewById(R.id.tvIgnore).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.headBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        StatusBarCompat1.translucentStatusBar(this);

        step = STEP_1;
        updateView();

        if (mHttpService == null) {
            mHttpService = new HttpService(this, new HttpDownloader.HttpDownloaderListener() {
                @Override
                public void onHttpSuccess(int reqId, JSONObject json) {
                    if (json == null || isFinishing() || Common.isForceLogout(LockSetupActivity.this,json)) return;
                    if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
                        User user = new User();
                        mHttpService.onGetUserInfo(json, user);
                        username = user.getUserName();
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

                }
            });

            mHttpService.getUserInfo();
        }

    }

    private void updateView() {
        switch (step) {
            case STEP_1:
                leftButton.setText(R.string.cancel);
                rightButton.setText("");
                rightButton.setEnabled(false);
                choosePattern = null;
                confirm = false;
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
                break;
            case STEP_2:
//            leftButton.setText(R.string.try_again);
//            rightButton.setText(R.string.goon);
                leftButton.setText("取消");
                tip.setText("请再次绘制手势密码");
//            rightButton.setText(R.string.try_again);
                rightButton.setEnabled(true);
                lockPatternView.disableInput();
                rightButton.performLongClick();
                step = STEP_3;
                updateView();
                break;
            case STEP_3:
                leftButton.setText(R.string.cancel);
                rightButton.setText(R.string.try_again);
                rightButton.setEnabled(true);
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
                break;
            case STEP_4:
                leftButton.setText(R.string.cancel);
                if (confirm) {
//                rightButton.setText(R.string.confirm);
//                rightButton.setEnabled(true);
//                lockPatternView.disableInput();
                    gotoFinish();
                } else {
//                Toast.makeText(LockSetupActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                    rightButton.setText(R.string.try_again);
                    lockPatternView.setDisplayMode(DisplayMode.Wrong);
                    lockPatternView.enableInput();
                    rightButton.setEnabled(true);
                    tip.setText("密码不一致请重试");
//                tvTitle.setTextColor(Color.RED);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                Message message = new Message();
                                message.what = ERROR;
                                mHandle.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.left_btn:
                if (step == STEP_1 || step == STEP_3 || step == STEP_4) {
                    finish();
                } else if (step == STEP_2) {
                    step = STEP_1;
                    updateView();
                }
                break;

            case R.id.right_btn:
                if (step == STEP_2) {
                    step = STEP_3;
                    updateView();
                } else if (step == STEP_3) {
                    gotoTryAgain();
                } else if (step == STEP_4) {
                    gotoTryAgain();
                }

                break;

            default:
                break;
        }

    }

    public void gotoTryAgain() {
        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
//        preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_STRING, LockPatternView.patternToString(choosePattern));
//        preferencesHelper.clearPreference();
        preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_STRING);
        step = STEP_1;
        updateView();
    }

    public void gotoFinish() {
        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_STRING, LockPatternView.patternToString(choosePattern));

        User user = null;
        if (!TextUtils.isEmpty(username)) {
            DaoMaster.DevOpenHelper dbHelper;
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;

            dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
            db = dbHelper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            UserDao userDao = daoSession.getUserDao();
            if (AppState.instance().logined()) {
                if (userDao != null) {
                    QueryBuilder<User> qb = userDao.queryBuilder();
                    List<User> userList = qb.list();
                    if (userList != null && userList.size() > 0) {
                        user = userList.get(0);
                    }
                }
            }
            if (user != null) {
                username = user.getUserName();
            }
        }

        if (user != null) {
            preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_ID, "" + user.getId());
        }

        if (!TextUtils.isEmpty(username)) {
            preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME, username);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SWITCH_TAB_NUM, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPatternStart() {
        Log.d(TAG, "onPatternStart");
    }

    @Override
    public void onPatternCleared() {
//        Log.d(TAG, "onPatternCleared");
        miniLockPattern.clearPoint();
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) {
//        Log.d(TAG, "onPatternCellAdded:" + pattern.toString());
        if (pattern != null && pattern.size() != 0) {
            Cell cell = pattern.get(pattern.size() - 1);
            MiniLockPatternView.PointCell pointCell = miniLockPattern.new PointCell(cell.column, cell.row);
            miniLockPattern.setPoint(pointCell);
        }
    }

    @Override
    public void onPatternDetected(List<Cell> pattern) {
//        Log.d(TAG, "onPatternDetected");
        miniLockPattern.clearPoint();
        if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
            tip.setText(R.string.lockpattern_recording_incorrect_too_short);
//            Toast.makeText(this,
//                    R.string.lockpattern_recording_incorrect_too_short,
//                    Toast.LENGTH_LONG).show();
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            return;
        }

        if (choosePattern == null) {
            choosePattern = new ArrayList<Cell>(pattern);
            //           Log.d(TAG, "choosePattern = "+choosePattern.toString());
//            Log.d(TAG, "choosePattern.size() = "+choosePattern.size());
//            Log.d(TAG, "choosePattern = " + Arrays.toString(choosePattern.toArray()));

            step = STEP_2;
            updateView();
            return;
        }
//[(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]
//[(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]    

//        Log.d(TAG, "choosePattern = " + Arrays.toString(choosePattern.toArray()));
//        Log.d(TAG, "pattern = " + Arrays.toString(pattern.toArray()));

        if (choosePattern.equals(pattern)) {
//            Log.d(TAG, "pattern = "+pattern.toString());
//            Log.d(TAG, "pattern.size() = "+pattern.size());
//            Log.d(TAG, "pattern = " + Arrays.toString(pattern.toArray()));
            confirm = true;
        } else {
            confirm = false;
        }

        step = STEP_4;
        updateView();

    }

}
