package cn.vpfinance.vpjr.module.product.success;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.common.SharePop;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.ScreenUtil;

/**
 * 参加活动出借成功的界面,比如iphone7活动
 */
public class ProductInvestSuccessByActiveActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.product_title)
    TextView        mProductTitle;
    @Bind(R.id.product_invest_money)
    TextView        mProductInvestMoney;
    @Bind(R.id.product_income)
    TextView        mProductIncome;
    @Bind(R.id.product_refund_way)
    TextView        mProductRefundWay;
    @Bind(R.id.look_account)
    TextView        mLookAccount;
    @Bind(R.id.next_invest)
    TextView        mNextInvest;
    @Bind(R.id.view_ll)
    LinearLayout mViewLl;

    public final static String      PID                 = "pid";
    public final static String      COLOR               = "color";
    public final static String      INVESTMONEY         = "investMoney";
    public final static String      STATE               = "state";
    public final static String      REDPACKETSTRURL     = "redpacketstrurl";
    public final static String      REDPACKETCOUNT      = "redPacketCount";
    public final static String      REDPACKETSTATE      = "redPacketState";
    public final static String      ALLOW_TRANSFER      = "allowTransfer";
    public final static String      ISALLOWTRIP         = "isAllowTRip";
    public final static String      DRAWTIME            = "drawTime";
    public final static String      PRODUCT_TYPE        = "productType";
    public final static String      IMAGEURL            = "imageUrl";
    public final static String      ACTIVITYURL         = "activityUrl";
    public final static String      SUCCESS_CONTENT     = "success_content";
    public final static String      INVESTSUCCESSRESULT = "investsuccessresult";
    private             ImageLoader imageLoader         = ImageLoader.getInstance();
    private HttpService mHttpService;
    private FinanceProduct product = new FinanceProduct();
    private String      redpacketstrurl;
    private String      redPacketCount;
    private String      redPacketState;
    private AlertDialog mDialog;
    private String      imageIconPath;
    private Button      mBtnRedPacket;
    private String      color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_invest_success_by_active);
        ButterKnife.bind(this);

        mTitleBar.setTitle("出借成功").setHeadBackVisible(View.VISIBLE);
        mHttpService = new HttpService(this, this);
        mHttpService.getRedPacketInfo();
        Intent intent = getIntent();
        if (intent != null) {
            redpacketstrurl = intent.getStringExtra(REDPACKETSTRURL);
            redPacketCount = intent.getStringExtra(REDPACKETCOUNT);
            redPacketState = intent.getStringExtra(REDPACKETSTATE);

            color = intent.getStringExtra(COLOR);

            String pid = intent.getStringExtra(ProductInvestSuccessByActiveActivity.PID);
            mHttpService.getFixProduct(pid, "" + 0);
            ArrayList<String> successContents = intent.getStringArrayListExtra(ProductInvestSuccessByActiveActivity.SUCCESS_CONTENT);
            Logger.e("successContents:" + successContents);
            if (successContents != null && successContents.size() != 0) {
                int size = successContents.size();
                for (int i = 0; i < size; i++) {
                    if (i == 0) {
                        String tempStr = successContents.get(i);
                        String content_1 = tempStr.replace("x", color);
                        ((TextView) ButterKnife.findById(this, R.id.content_1)).setText(content_1);
                    }else if (i == 1) {
                        ((TextView) ButterKnife.findById(this, R.id.content_2)).setText(successContents.get(i));
                    }else if (i == 2) {
                        ((TextView) ButterKnife.findById(this, R.id.content_3)).setText(successContents.get(i));
                    }
                }
            }


        }
        showDialog(redPacketState);
    }

    private void showDialog(String state) {
        if (!"1".equals(state)) return;

        View dialogView;
        dialogView = View.inflate(this, R.layout.dialog_product_invest_success, null);
        mDialog = new AlertDialog.Builder(this).create();
        mDialog.show();
        final Window dialogWindow = mDialog.getWindow();
        dialogWindow.setContentView(dialogView);


        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialogWindow.getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.6);   //高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8);    //宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);


        TextView tvRedPacketCount = (TextView) dialogView.findViewById(R.id.tvRedPacketCount);
        TextView tvInfo = (TextView) dialogView.findViewById(R.id.tvInfo);
        mBtnRedPacket = (Button) dialogView.findViewById(R.id.btnRedPacket);
        ImageView ivBg = (ImageView) dialogView.findViewById(R.id.ivBg);
        mBtnRedPacket.setOnClickListener(this);

        tvRedPacketCount.setText("恭喜您获得该产品的红包");
        mBtnRedPacket.setText("分享给好友");
        mBtnRedPacket.setEnabled(true);
        ivBg.setBackgroundResource(R.drawable.bg_dialog_invest_success);

        dialogView.findViewById(R.id.ibClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo.ordinal()) {
            if (json != null) {
                mHttpService.onGetProductDetail(this, json, product);
                String loanTitle = product.getLoanTitle();
                mProductTitle.setText(TextUtils.isEmpty(loanTitle) ? "" : loanTitle);
                mProductRefundWay.setText(product.getRefundWay() == 1 ? getString(R.string.refundState1) :
                        (product.getRefundWay() == 2 ? getString(R.string.refundState2) : getString(R.string.refundState3)));
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_RedPactetInfo.ordinal()) {
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
                                    file = diskCache.get(HttpService.mBaseUrl+"/resources/activity/redpacket/red.png");
                                }
                                if (file != null && file.exists()) {
                                    imageIconPath = file.getAbsolutePath();
                                }
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

    private String picurl;
    private String topic;
    private String description;

    @OnClick({R.id.look_account, R.id.next_invest})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.look_account:
                Intent intent = new Intent(ProductInvestSuccessByActiveActivity.this, MainActivity.class);
                intent.putExtra(INVESTSUCCESSRESULT, INVESTSUCCESSRESULT);
                intent.putExtra(MainActivity.SWITCH_TAB_NUM, 2);
                startActivity(intent);
                finish();
                break;
            case R.id.next_invest:
                finish();
                break;
            case R.id.btnRedPacket:
                String title = TextUtils.isEmpty(topic) ? "微品金融拼手气红包" : topic;
                String content = TextUtils.isEmpty(description) ? "好友发来微品金融红包，个数有限先到先得，快去碰碰手气吧。" : description;
                if (!TextUtils.isEmpty(redpacketstrurl)) {
//                    showShare(title, content, imageIconPath, redpacketstrurl);
                    SharePop sharePop = new SharePop(this, redpacketstrurl, imageIconPath,
                            content, title);
                    sharePop.showAtLocation(mViewLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ScreenUtil.getBottomStatusHeight(this));
                }
                mDialog.dismiss();
                break;
        }
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
}

