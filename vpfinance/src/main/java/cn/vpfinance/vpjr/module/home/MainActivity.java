package cn.vpfinance.vpjr.module.home;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.jewelcredit.model.AppUpdateInfo;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.download.DownloadObserver;
import cn.vpfinance.vpjr.download.SpUtils;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.NewAppUpdateInfo;
import cn.vpfinance.vpjr.gson.PersonalInfo;
import cn.vpfinance.vpjr.gson.QueryAutoStatusBean;
import cn.vpfinance.vpjr.gson.QueryPopUpBean;
import cn.vpfinance.vpjr.model.RefreshTab;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.NewUserDialogActivity;
import cn.vpfinance.vpjr.module.dialog.UpdateDialogFragment;
import cn.vpfinance.vpjr.module.gusturelock.LockActivity;
import cn.vpfinance.vpjr.module.home.fragment.HomeFragment;
import cn.vpfinance.vpjr.module.list.ProductCategoryFragment;
import cn.vpfinance.vpjr.module.more.MoreFragment2;
import cn.vpfinance.vpjr.module.product.success.ProductInvestSuccessActivity;
import cn.vpfinance.vpjr.module.setting.AutoInvestOverInfoActivity;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.module.user.fragment.BankAccountFragment;
import cn.vpfinance.vpjr.service.DemoIntentService;
import cn.vpfinance.vpjr.service.DemoPushService;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.util.UpdateAppUtil;
import cn.vpfinance.vpjr.view.popwindow.MainTab2Menu;
import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity {

    private static final int CHANGE_TITLE = 10;
    public static final String SWITCH_TAB_NUM = "switch_tab_num";
    public static final String IS_GET_TUI = "IS_GET_TUI";

    private RadioGroup radioGroup;
    public int mLastRadioId = -1;
    private MainActivity self;

    private long exitTime = 0;
    private long exitDelay = 2000;

    //    private HomeFragment mHomeFragment;
//    private ProductCategoryFragment productCategoryFragment;
//    private MineFragment mineFragment;
//    private NewMineFragment newMineFragment;
    public ViewPager mViewPager;

    private MoreFragment2 moreFragment;
    private HttpService mHttpService = null;

    private MainTab2Menu mainTab2Menu;
    private PopupWindow titlePopupWindow;
    private static final String SCHEDULE_PRODUCT = "定期投资";
    private static final String TRANSFER_PRODUCT = "转让专区";
    private AppUpdateInfo info;
    private DownloadObserver mDownloadObserver;
    private ContentResolver mContentResolver;
    //    public int mineFragmentColor;
    private Pair<ContentResolver, UpdateAppUtil.DownloadObserver> downloadObserverPair;
    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //{"mac":"9c:2e:a1:d1:75:4b","device_id":"9c:2e:a1:d1:75:4b"}
        self = this;

        super.onCreate(savedInstanceState);
        //个推初始化
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);

        setContentView(R.layout.activity_main);

        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();

        mHttpService = new HttpService(this, this);
        String version = Utils.getVersion(this);

        //第一次进来检查版本号和是否锁屏
        if (((FinanceApplication) getApplication()).isCheckUpdate) {
            SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
            String saved_logPwd = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD);
            String saved_name = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME);
            if (!TextUtils.isEmpty(saved_logPwd) && !TextUtils.isEmpty(saved_name)) {
                Intent intent = new Intent(this, LockActivity.class);
                intent.putExtra(LockActivity.NAME_AUTO_LOGIN, true);
                startActivity(intent);
            }
            mHttpService.getVersionCheck(version);
            ((FinanceApplication) getApplication()).isCheckUpdate = false;
        }
        //mHttpService.getVpUrl();
        initView();
        addListener();
    }


    private String uid;
    private String cid;

    @Override
    protected void onResume() {
        super.onResume();
        mHttpService.querySessionStatus();
        mHttpService.getServiceTime();

        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        uid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
        cid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_GETUI_CLIENT_ID);
        String lastSent = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_GETUI_LAST_SENT);
        String uc = uid + cid;
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(cid) && !uc.equals(lastSent)) {
            mHttpService.getuiLoginSendMess(uid, cid);
        }

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra(IS_GET_TUI, false)) {
                //个推点击了过来的就友盟统计
                ArrayMap<String, String> map = new ArrayMap<String, String>();
                map.put("GeTuiType", "0"); // 就是default
                MobclickAgent.onEvent(MainActivity.this, "GeTuiClick", map);
            }
        }

        String stringExtra = getIntent().getStringExtra(ProductInvestSuccessActivity.INVESTSUCCESSRESULT);
        //		Utils.log("stringExtra:" + stringExtra);
        if (!TextUtils.isEmpty(stringExtra) && ProductInvestSuccessActivity.INVESTSUCCESSRESULT.equals(stringExtra)) {
            if (AppState.instance().logined()) {
                switchToTab(2, false);
            }
        }

        //弹窗提醒用户
        user = DBUtils.getUser(this);
        if (((FinanceApplication) getApplication()).login && user != null) {
            mHttpService.getQueryPopUp(user.getUserId().toString());
            mHttpService.getQueryAutoPlankStatus(user.getUserId().toString());
            ((FinanceApplication) getApplication()).login = false;
        }
    }



    @Override
    protected void onRestart() {
        super.onRestart();
    }

    protected void initView() {
//        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
//        titleBar.setVisibility(View.VISIBLE);

        mViewPager = (ViewPager) findViewById(R.id.homePager);
        MyAdapter mTabsAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mTabsAdapter);
        radioGroup = (RadioGroup) findViewById(R.id.maintab_radiogroup);

        Intent intent = getIntent();
        int switchTabNum = intent.getIntExtra(SWITCH_TAB_NUM, 0);
        switchToTab(switchTabNum);
    }

    private void addListener() {
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mLastRadioId != checkedId) {
                    switch (checkedId) {
                        case R.id.maintab_home_radiobtn:
                            switchToTab(0);
                            break;
                        case R.id.maintab_plan_radiobtn:
                            switchToTab(1);
                            break;
                        case R.id.maintab_mine_radiobtn:
                            if (!AppState.instance().logined()) {
                                self.startActivityForResult(new Intent(self, LoginActivity.class), 1);
                                return;
                            }
                            switchToTab(2);
                            break;
                        case R.id.maintab_more_radiobtn:
                            /*更多*/
                            switchToTab(3);
                            return;
                    }
                    mLastRadioId = checkedId;
                }
            }
        });
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
//                    return mHomeFragment;
                    return HomeFragment.newInstance();
                case 1:
//                    return productCategoryFragment;
                    return ProductCategoryFragment.newInstance();
                case 2:
//                    return new MineFragment();
                    return new BankAccountFragment();
                case 3:
                    return MoreFragment2.newInstance();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            //super.setPrimaryItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            return fragment;
        }

    }

    public void switchToTab(int tab) {
        switchToTab(tab, false);
    }

    public void switchToTab(int tab, boolean smooth) {
        switch (tab) {
            case 0:
                EventBus.getDefault().post(new RefreshTab(RefreshTab.TAB_HOME));
                mViewPager.setCurrentItem(0, smooth);
                ((RadioButton) findViewById(R.id.maintab_home_radiobtn)).setChecked(true);
                mLastRadioId = R.id.maintab_home_radiobtn;
                break;

            case 1:
                EventBus.getDefault().post(new RefreshTab(RefreshTab.TAB_LIST));

                mViewPager.setCurrentItem(1, smooth);
                ((RadioButton) findViewById(R.id.maintab_plan_radiobtn)).setChecked(true);
                mLastRadioId = R.id.maintab_plan_radiobtn;
                break;

            case 2:
                EventBus.getDefault().post(new RefreshTab(RefreshTab.TAB_MINE));
                mViewPager.setCurrentItem(2, smooth);
                ((RadioButton) findViewById(R.id.maintab_mine_radiobtn)).setChecked(true);

                //两年没换密码就提示更换一下密码
                FinanceApplication application = (FinanceApplication) getApplication();
                if (application.isNeedUpdatePwd) {
                    new AlertDialog.Builder(this)
                            .setMessage("您已经很久没更换过密码了，请更换登录密码保障您的账户安全")
                            .setCancelable(false)
                            .setPositiveButton("去更换密码", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(MainActivity.this, PasswordChangeActivity.class));
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                    application.isNeedUpdatePwd = false;
                }
                mLastRadioId = R.id.maintab_mine_radiobtn;
                break;
            case 3:
                mViewPager.setCurrentItem(3, smooth);
                ((RadioButton) findViewById(R.id.maintab_more_radiobtn)).setChecked(true);
//                titleBar.reset().setTitle("更多");
                mLastRadioId = R.id.maintab_more_radiobtn;
                break;
            case 4:
                mViewPager.setCurrentItem(4, smooth);
                ((RadioButton) findViewById(R.id.maintab_mine_radiobtn)).setChecked(true);
                mLastRadioId = R.id.maintab_mine_radiobtn;
                break;

            default:
                break;
        }
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_VERSION_CHECK.ordinal()) {
            NewAppUpdateInfo appUpdateInfo = HttpService.onGetVersion(json);
            if (appUpdateInfo != null) {
                showUpdate(appUpdateInfo);
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_SERVICE_TIME.ordinal()) {
            long serverTime = json.optLong("serverTime");
            long differTime = serverTime - System.currentTimeMillis();
            ((FinanceApplication) getApplication()).differTime = differTime;
//            Logger.e("Main: serverTime = [" + serverTime + "], differTime = [" + differTime + "]");
        } else if (reqId == ServiceCmd.CmdId.CMD_QUERY_POP_UP.ordinal()) {
            QueryPopUpBean bean = new Gson().fromJson(json.toString(), QueryPopUpBean.class);

            if (bean != null && bean.returnType != 0 && user != null) {
                String userId = user.getUserId().toString();
                Set<String> values = SharedPreferencesHelper.getInstance(this).getSetValue("NewUserPop_" + userId, new HashSet<String>());
                if (!values.contains(bean.bigType)) {
                    NewUserDialogActivity.goThis(this, json.toString());
                }
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_GetVpUrl.ordinal()) {
            String url = json.optString("url");
            if (!TextUtils.isEmpty(url)) {
                //gotoWeb(url,"测试");
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_querySessionStatus.ordinal()) {

        } else if (reqId == ServiceCmd.CmdId.CMD_GETUI_loginSendMess.ordinal()) {
            SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(cid)) {
                //preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID,uid + cid);
            }

        } else if (reqId == ServiceCmd.CmdId.CMD_commonLoanDesc.ordinal()) {
            PersonalInfo info = mHttpService.onGetPersonal(json);
        } else if (reqId == ServiceCmd.CmdId.CMD_QUERY_AUTO_PLANK_STATUS.ordinal()) {
            QueryAutoStatusBean autoStatusBean = new Gson().fromJson(json.toString(), QueryAutoStatusBean.class);
            if (autoStatusBean != null && !TextUtils.isEmpty(autoStatusBean.autoPlankStatus)
                    && ("2".equals(autoStatusBean.autoPlankStatus) || "3".equals(autoStatusBean.autoPlankStatus))) {
                showAutoStatus(autoStatusBean.autoPlankStatus);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (AppState.instance().logined()) {
                switchToTab(2, false);
            } else {
                ((RadioButton) findViewById(mLastRadioId)).setChecked(true);
            }
        }
    }

    public MainActivity instance() {
        return self;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > exitDelay) {
                Toast.makeText(this, "再按一次退出微品金融.", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                FinanceApplication.appExit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//		setIntent(intent);
//		boolean isfromweb = intent.getBooleanExtra("isfromweb", false);
//		if(isfromweb){
//			switchToTab(1, false);
//		}

        int switchTabNum = intent.getIntExtra(SWITCH_TAB_NUM, 0);
        switchToTab(switchTabNum);
    }

    /**
     * 版本检查更新
     *
     * @param appUpdateInfo
     */
    private void showUpdate(NewAppUpdateInfo appUpdateInfo) {
        if (appUpdateInfo == null) return;
        int status = appUpdateInfo.appStatus;
        if (status == 0) return;

        if (status == 1) {//最新版本

        } else if (status == 2) {//不是最新版本
            SpUtils spUtils = SpUtils.getInstance(this);
            //保存版本名称,取消三次后，这个版本将不再提示
            if (TextUtils.isEmpty(appUpdateInfo.appVersion)) return;

            String spVersionName = spUtils.getString(SpUtils.UPDATE_VERSION, "");
            if (TextUtils.isEmpty(spVersionName) || (!appUpdateInfo.appVersion.equals(spVersionName))) {
                spUtils.putInt(SpUtils.UPDATE_ALERT_NUM, 0);
                spUtils.putString(SpUtils.UPDATE_VERSION, appUpdateInfo.appVersion);
            }

            int num = spUtils.getInt(SpUtils.UPDATE_ALERT_NUM, 0);
            if (num < 3) {
                showUpdateDialog(appUpdateInfo);
            }

        } else if (status == 3) {//版本状态无效，须要升级版本
            showUpdateDialog(appUpdateInfo);
        }
    }

    private void showUpdateDialog(final NewAppUpdateInfo info) {
        if (info == null) return;

        final UpdateDialogFragment dialogFragment = UpdateDialogFragment.newInstance(info);
        dialogFragment.setOnAllLinstener(new UpdateDialogFragment.onAllListener() {
            @Override
            public void clickOk() {
                if (!TextUtils.isEmpty(info.downloadUrl)) {
//                    download(info.downloadUrl);
                    UpdateAppUtil instance = UpdateAppUtil.getInstance(MainActivity.this);
                    long downloadId = instance.download(info.downloadUrl, getResources().getString(R.string.app_name), "下载完后点击安装");
                    downloadObserverPair = instance.registerDownloadObserver(mHandler, downloadId);
                    Utils.Toast(MainActivity.this, "正在下载");
                }
            }

            @Override
            public void clickCancel() {
                //取消三次后，这个版本将不再提示
                SpUtils spUtils = SpUtils.getInstance(MainActivity.this);
                int num = spUtils.getInt(SpUtils.UPDATE_ALERT_NUM, 0);
                spUtils.putInt(SpUtils.UPDATE_ALERT_NUM, ++num);
            }
        });
        dialogFragment.show(getFragmentManager(), "UpdateDialogFragment");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            if (msg.what == UpdateAppUtil.WHAT_DOWNLOADED) {
                long downloadId = (long) msg.obj;
                UpdateAppUtil.getInstance(MainActivity.this).install(downloadId);
            }
        }
    };

    /**
     * 1或null 可用、2 超额 、3 过期
     */
    private void showAutoStatus(String status) {
        String title = "";
        String message = "";
        if ("2".equals(status)) {
            title = "自动投标超额提醒";
            message = "您的自动投标总额已达到上限,请重新授权";
        } else if ("3".equals(status)) {
            title = "自动投标授权过期提醒";
            message = "您的自动投标已超过约定时间,请重新授权";
        }
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message) || user == null) return;
        String value = SharedPreferencesHelper.getInstance(MainActivity.this).getStringValue(SharedPreferencesHelper.KEY_SHOW_AUTO_STATUS_TIME);
        if (TextUtils.isEmpty(value)){
            showAutoStatusDialog(title,message, status);
        }else{
            String[] split = value.split("_");
            if (split != null && split.length == 2){
                if (split[0].equalsIgnoreCase(user.getUserId().toString())){
                    try {
                        long time = new SimpleDateFormat("yyyy-MM-dd").parse(split[1]).getTime();
                        if (time < System.currentTimeMillis()){
                            showAutoStatusDialog(title,message, status);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    showAutoStatusDialog(title,message, status);
                }
            }
        }

    }

    private void showAutoStatusDialog(String title,String message,final String status){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("重新授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if ("2".equals(status)) {
                            AutoInvestOverInfoActivity.goThis(MainActivity.this);
                        } else if ("3".equals(status)) {
                            gotoWeb("hx/loansign/authAutoBid?userId=" + user.getUserId(), "自动授权");
                        }
                    }
                })
                .setNegativeButton("暂不", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                        String data = year + "-" + month + "-" + day;
                        SharedPreferencesHelper.getInstance(MainActivity.this).putStringValue(SharedPreferencesHelper.KEY_SHOW_AUTO_STATUS_TIME, user.getUserId() + "_" + data);
                    }
                })
                .setNeutralButton("不再提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferencesHelper.getInstance(MainActivity.this).putStringValue(SharedPreferencesHelper.KEY_SHOW_AUTO_STATUS_TIME, user.getUserId() + "_" + "2024-02-02");
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
