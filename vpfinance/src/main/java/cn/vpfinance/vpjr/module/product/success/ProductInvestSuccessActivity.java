package cn.vpfinance.vpjr.module.product.success;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.product.DialogWebViewActivity;
import cn.vpfinance.vpjr.module.common.SharePop;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.gson.OneYearBean;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.ScreenUtil;
import de.greenrobot.dao.query.QueryBuilder;

/**
 */
public class ProductInvestSuccessActivity extends BaseActivity implements View.OnClickListener {

    private HttpService mHttpService;
    private TextView productTitle;
    private LinearLayout mViewLl;
    private TextView productRate;
    private TextView productMonth;
    private TextView productIssueLoan;
    private TextView productRefundWay;
    private FinanceProduct product = new FinanceProduct();
    private TextView tvMoney;
    private TextView tvLookAccount;
    private TextView tvNextInvest;
    private String pid;
    public final static String PID = "pid";
    public final static String INVESTMONEY = "investMoney";
    public final static String STATE = "state";
    public final static String REDPACKETSTRURL = "redpacketstrurl";
    public final static String REDPACKETCOUNT = "redPacketCount";
    public final static String REDPACKETSTATE = "redPacketState";
    public final static String ALLOW_TRANSFER = "allowTransfer";
    public final static String ISALLOWTRIP = "isAllowTRip";
    public final static String DRAWTIME = "drawTime";
    public final static String PRODUCT_TYPE = "productType";
    public final static String IMAGEURL = "imageUrl";
    public final static String ACTIVITYURL = "activityUrl";
    public final static String IS_DEPOSIT = "is_deposit";


    private String imageIconPath;
    public final static String RED_PACK = "redpack.png";
    private ImageLoader imageLoader = ImageLoader.getInstance();

    private String redpacketstrurl;
    private String redPacketCount = "0";
    private String redPacketState;
    public final static String INVESTSUCCESSRESULT = "investsuccessresult";

    private ActionBarLayout titleBar;
    private String picurl;
    private String topic;
    private String description;
    private String allowTransfer;
    private String productType;
    private ImageView ivAllowTransfer;
    private ImageView mIsAllowTrip;
    private String isAllowTrip;
    private boolean isOneYear = false;
    private boolean isActivity = false;
    private AlertDialog mDialog;
    private OneYearBean mOneYearBean;
    private String mAllowGetVoucher;
    private String mAllowRedPacket;
    private String mCashTime;
    private UserDao userDao;
    private User user;
    private ImageView mWallet_btn;
    private String mActivityUrl;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUser();
        final File imgFile = new File(getCacheDir(), RED_PACK);
        try {
            Utils.copyAssetsFileToPath(ProductInvestSuccessActivity.this, RED_PACK, imgFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageIconPath = imgFile.getAbsolutePath();

        imageLoader = ImageLoader.getInstance();
        setContentView(R.layout.activity_product_invest_success);
        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setTitle("出借成功").setHeadBackVisible(View.VISIBLE);

        mHttpService = new HttpService(this, this);
        mHttpService.getRedPacketInfo();

        mViewLl = (LinearLayout) findViewById(R.id.view_ll);
        productTitle = (TextView) findViewById(R.id.productTitle);
        productRate = (TextView) findViewById(R.id.productRate);
        productMonth = (TextView) findViewById(R.id.productMonth);
        productIssueLoan = (TextView) findViewById(R.id.productIssueLoan);
        productRefundWay = (TextView) findViewById(R.id.productRefundWay);
        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvLookAccount = (TextView) findViewById(R.id.tvLookAccount);
        tvNextInvest = (TextView) findViewById(R.id.tvNextInvest);
        ivAllowTransfer = ((ImageView) findViewById(R.id.ivAllowTransfer));
        mIsAllowTrip = ((ImageView) findViewById(R.id.isAllowTrip));

        tvLookAccount.setOnClickListener(this);
        tvNextInvest.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            pid = intent.getStringExtra(PID);
            mCashTime = intent.getStringExtra(DRAWTIME);
            mImageUrl = intent.getStringExtra(IMAGEURL);
            mActivityUrl = intent.getStringExtra(ACTIVITYURL);
//            if (!TextUtils.isEmpty(pid)) {
//                if (is_deposit) {
//                    long mPid = Long.parseLong(pid);
//                    try {
//                        mHttpService.getDepositProductInfo(mPid);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    mHttpService.getFixProduct(pid, "" + 0);
//                }
//            }
            String investMoney = intent.getStringExtra(INVESTMONEY);
            tvMoney.setText("出借金额 ¥" + investMoney);
            redpacketstrurl = intent.getStringExtra(REDPACKETSTRURL);
//            Logger.e("redpacketstrurl:"+redpacketstrurl);
            redPacketCount = intent.getStringExtra(REDPACKETCOUNT);
            redPacketState = intent.getStringExtra(REDPACKETSTATE);
            allowTransfer = intent.getStringExtra(ALLOW_TRANSFER);
            productType = intent.getStringExtra(PRODUCT_TYPE);
            isAllowTrip = intent.getStringExtra(ISALLOWTRIP);
            String state = intent.getStringExtra(STATE);
            if ("1".equals(state)) {
//                isOneYear = true;
                isActivity = true;
//                mHttpService.getOneYear(pid);
            } else {
//                isOneYear = false;
                isActivity = false;
            }
        }

        if ("true".equals(allowTransfer)) {
            ivAllowTransfer.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(isAllowTrip) && isAllowTrip == 1 + "") {
            mIsAllowTrip.setVisibility(View.VISIBLE);
        } else {
            mIsAllowTrip.setVisibility(View.GONE);
        }

        tvNextInvest.setText("继续出借");
        if (!isActivity) {
            if ("1".equals(redPacketState) && (!TextUtils.isEmpty(redpacketstrurl))) {
//                Log.d("aa", "onCreate: 不是活动");
                showDialog(redPacketState);
            } else if ("2".equals(redPacketState) || "3".equals(redPacketState)) {
                tvNextInvest.setText("继续出借");
            }
        } else {
            if ("1".equals(redPacketState) && (!TextUtils.isEmpty(redpacketstrurl)) || !"0".equals(mCashTime)) {
//                Log.d("aa", "onCreate: 双节活动");
                showDialog("");
            }
        }
    }

    private void showDialog(String state) {
        //Logger.e("showDialog");
        View dialogView = null;
        if (isActivity) {
            dialogView = View.inflate(ProductInvestSuccessActivity.this, R.layout.dialog_product_invest_success_oneyears, null);
        } else {
            dialogView = View.inflate(ProductInvestSuccessActivity.this, R.layout.dialog_product_invest_success, null);
        }
        mDialog = new AlertDialog.Builder(ProductInvestSuccessActivity.this).create();
        mDialog.show();
        final Window dialogWindow = mDialog.getWindow();
        dialogWindow.setContentView(dialogView);

        if (isActivity) {

            WindowManager m = getWindowManager();
            Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialogWindow.getAttributes();  //获取对话框当前的参数值
            //            p.height = (int) (d.getHeight() * 0.6);   //高度设置为屏幕的0.6
            //            p.width = (int) (d.getWidth() * 0.8);    //宽度设置为屏幕的0.8
            p.width = WindowManager.LayoutParams.WRAP_CONTENT;
            p.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(p);
        } else {
            WindowManager m = getWindowManager();
            Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialogWindow.getAttributes();  //获取对话框当前的参数值
            p.height = (int) (d.getHeight() * 0.6);   //高度设置为屏幕的0.6
            p.width = (int) (d.getWidth() * 0.8);    //宽度设置为屏幕的0.8
//            p.width = WindowManager.LayoutParams.WRAP_CONTENT;
//            p.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(p);

        }
//        }


        if (isActivity) {
            View btnRedPacket = dialogView.findViewById(R.id.btnRedPacket);
            View envelope_btn = dialogView.findViewById(R.id.lottery_btn);
            View wallet_gro = dialogView.findViewById(R.id.wallet_gro);
            mWallet_btn = (ImageView) dialogView.findViewById(R.id.wallet_btn);
            ImageView ivBg = (ImageView) dialogView.findViewById(R.id.ivBg);
            if (!TextUtils.isEmpty(mImageUrl)) {
                imageLoader.displayImage(HttpService.mBaseUrl + mImageUrl, ivBg);
            }
            btnRedPacket.setOnClickListener(this);
            envelope_btn.setOnClickListener(this);
            wallet_gro.setOnClickListener(this);


            if ("1".equals(mAllowGetVoucher)) {
                wallet_gro.setVisibility(View.GONE);
            }
            if (!"1".equals(redPacketState)) {
                btnRedPacket.setVisibility(View.GONE);
            }
            if ("0".equals(mCashTime)) {
                envelope_btn.setVisibility(View.GONE);
            }

        } else {
            TextView tvRedPacketCount = (TextView) dialogView.findViewById(R.id.tvRedPacketCount);
            TextView tvInfo = (TextView) dialogView.findViewById(R.id.tvInfo);
            Button btnRedPacket = (Button) dialogView.findViewById(R.id.btnRedPacket);
            ImageView ivBg = (ImageView) dialogView.findViewById(R.id.ivBg);
            btnRedPacket.setOnClickListener(this);
            if ("1".equals(state)) {
                tvRedPacketCount.setText("恭喜您获得该产品的红包");
                //            tvInfo.setText("首次出借本产品即可获得红包");
                //            btnRedPacket.setVisibility(View.VISIBLE);
                btnRedPacket.setText("分享给好友");
                btnRedPacket.setEnabled(true);
                ivBg.setBackgroundResource(R.drawable.bg_dialog_invest_success);
            } else if ("2".equals(state)) {
                tvRedPacketCount.setText("您已获得过本产品的红包");
                //            tvInfo.setText("首次出借本产品即可获得红包");
                //            btnRedPacket.setVisibility(View.GONE);
                btnRedPacket.setText("继续出借");
                btnRedPacket.setEnabled(true);
                ivBg.setBackgroundResource(R.drawable.bg_dialog_invest_success);
            } else if ("3".equals(state)) {
                tvRedPacketCount.setText("您已获得过本产品的红包");
                //            tvInfo.setVisibility(View.GONE);
                //            btnRedPacket.setVisibility(View.GONE);
                btnRedPacket.setText("继续出借");
                btnRedPacket.setEnabled(false);
                ivBg.setBackgroundResource(R.drawable.bg_red_packet_over);
            }
        }

        dialogView.findViewById(R.id.ibClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

    }

    private void showShare(String title, final String text, String imageUrl, final String link) {
        if (TextUtils.isEmpty(link)) return;
        //imageUrl为空时，就算有link也只是单纯的文字，不能跳转（微信）
        //link为空时，只会有图片，连接文案都没有（微信）
        //Logger.e("title:"+title+",text:"+text+",imageUrl:"+imageUrl+",link:"+link);
//        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(link);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        if (!TextUtils.isEmpty(imageUrl)) {
            oks.setImagePath(imageUrl);
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(link);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(title);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(link);

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {

            @Override
            public void onShare(Platform platform,
                                cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if ("SinaWeibo".equals(platform.getName())) {
                    paramsToShare.setText(text + " " + link);

                }
            }
        });

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_OneYear_GetVoucher.ordinal()) {
            String mess = json.optString("mess");
            switch (mess) {
                case "1":
                    Utils.Toast(this, "领取成功");
                    mWallet_btn.setImageResource(R.drawable.wallet_btn_end);
                    break;
                case "2":
                    Utils.Toast(this, "您已经领取过啦");
                    break;
                case "4":
                    Utils.Toast(this, "活动已经结束");
                    break;
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_OneYear.ordinal()) {
            mOneYearBean = mHttpService.onGetOneYear(json);
            mAllowGetVoucher = mOneYearBean.allowGetVoucher;
            mAllowRedPacket = mOneYearBean.allowRedPacket;
            if (!("1".equals(mAllowGetVoucher) && "1".equals(mAllowRedPacket) && "0".equals(mCashTime))) {
                showDialog("");
            } else {
                tvNextInvest.setText("继续出借");
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_RedPactetInfo.ordinal()) {
            if (json != null) {
                picurl = json.optString("picurl");
                topic = json.optString("topic");
                description = json.optString("description");
                if (!TextUtils.isEmpty(picurl)) {
                    File file = null;
                    DiskCache diskCache = imageLoader.getDiskCache();
                    if (diskCache != null) {
                        file = diskCache.get(picurl);
                    }
                    if (file != null && file.exists()) {
                        imageIconPath = file.getAbsolutePath();
                    } else {
                        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .imageScaleType(ImageScaleType.NONE)
                                .bitmapConfig(Bitmap.Config.ARGB_8888)
                                .build();
                        imageLoader.loadImage(picurl, imageOptions, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                File file = null;
                                DiskCache diskCache = imageLoader.getDiskCache();
                                if (diskCache != null) {
                                    file = diskCache.get(HttpService.mBaseUrl + "/resources/activity/redpacket/red.png");
                                }
                                if (file != null && file.exists()) {
                                    imageIconPath = file.getAbsolutePath();
                                }
//                                Logger.e("imageIconPath3:"+imageIconPath);
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                File file = null;
                                DiskCache diskCache = imageLoader.getDiskCache();
                                if (diskCache != null) {
                                    file = diskCache.get(picurl);
                                }
                                if (file != null && file.exists()) {
                                    imageIconPath = file.getAbsolutePath();
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNextInvest:
//                Intent intent = new Intent(ProductInvestSuccessActivity.this, ProductInvestActivity.class);
//                intent.putExtra("pid", pid);
//                startActivity(intent);
//
                finish();
                /*if (isOneYear) {
                    mHttpService.getOneYear(pid);
                    if ("1".equals(mAllowGetVoucher) && "1".equals(mAllowRedPacket) && "0".equals(mCashTime)) {
                        finish();
                    }
                } else {
                    if("1".equals(redPacketState) && (!TextUtils.isEmpty(redpacketstrurl))){
                        showDialog(redPacketState);
                    }else if ("2".equals(redPacketState) || "3".equals(redPacketState)){
                        finish();
                    }
                }*/
                break;
            case R.id.tvLookAccount:
                Intent intent = new Intent(ProductInvestSuccessActivity.this, MainActivity.class);
                intent.putExtra(INVESTSUCCESSRESULT, INVESTSUCCESSRESULT);
                intent.putExtra(MainActivity.SWITCH_TAB_NUM, 2);
                startActivity(intent);
                finish();
                break;
            case R.id.btnRedPacket:
                String title = TextUtils.isEmpty(topic) ? "微品金融拼手气红包" : topic;
                String content = TextUtils.isEmpty(description) ? "好友发来微品金融红包，个数有限先到先得，快去碰碰手气吧。" : description;
                if (!TextUtils.isEmpty(redpacketstrurl)) {
//                    showShare(title, content, imageIconPath, redpacketstrurl);
                    SharePop sharePop = new SharePop(this, redpacketstrurl, imageIconPath,
                            content, title);
//                    sharePop.showAtLocation(mViewLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ScreenUtil.getBottomStatusHeight(this));
                    sharePop.showAtLocation(mViewLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                mDialog.dismiss();
                break;
            case R.id.lottery_btn:
                Intent intent1 = new Intent(this, DialogWebViewActivity.class);
//                intent1.putExtra(DialogWebViewActivity.KEY_URL, BaseUrl.mBaseUrl + "/oneyear/toLucyDrawAwardPageApp?uid=" + user.getUserId());
                intent1.putExtra(DialogWebViewActivity.KEY_URL, mActivityUrl);
                startActivity(intent1);
                break;
            case R.id.wallet_gro:
                mHttpService.getOneYearVoucher(user.getUserId() + "");
                break;
        }
    }

    private void getUser() {
        if (mHttpService == null) {
            mHttpService = new HttpService(this, this);
        }
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();

        if (AppState.instance().logined()) {
            if (userDao != null) {
                QueryBuilder<User> qb = userDao.queryBuilder();
                List<User> userList = qb.list();
                if (userList != null && userList.size() > 0) {
                    user = userList.get(0);
                }
            }
        } else {
            Toast.makeText(this, "请登录.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}

