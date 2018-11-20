package cn.vpfinance.vpjr.module.user.personal;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.highlight.Highlight;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.DifColorTextStringBuilder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.dialog.InviteGifeDialog;
import cn.vpfinance.vpjr.module.more.MyQRcodeActivity;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.InviteShowInfo;
import cn.vpfinance.vpjr.model.PromoteLinks;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2015/11/3.
 * 邀请有礼
 */
public class InviteGiftActivity extends BaseActivity implements View.OnClickListener {

    private ActionBarLayout titleBar;

    private HttpService mHttpService = null;
    private PromoteLinks mPromoteLinks = null;
    private TextInputDialogFragment tidf;
    private User user;
    private UserDao userDao;
    private TextView invitePerCount;
    private TextView registerRewardCount;
    private TextView bonusesCount;
    private TextView tvInviteGift;

    private MyQRcodeActivity.ShareInfo info;

    public static void goThis(Context context) {
        Intent intent = new Intent(context, InviteGiftActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_gift);


        mHttpService = new HttpService(this, this);
        getUser();
        mHttpService.getPromoteLinks();

        mHttpService.getShareInfo();


        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setTitle("邀请有礼").setHeadBackVisible(View.VISIBLE);

        invitePerCount = (TextView) findViewById(R.id.invitePerCount);
        registerRewardCount = (TextView) findViewById(R.id.registerRewardCount);
        bonusesCount = (TextView) findViewById(R.id.bonusesCount);
        tvInviteGift = (TextView) findViewById(R.id.tvInviteGift);
        if (user != null) {
            String cellPhone = TextUtils.isEmpty(user.getCellPhone()) ? "" : user.getCellPhone();
            String content = "3.好友注册输入您的专享邀请码：" + cellPhone;
            DifColorTextStringBuilder difColorTextStringBuilder = new DifColorTextStringBuilder();
            difColorTextStringBuilder.setContent(content)
                    .setHighlightContent(cellPhone, R.color.text_333333)
                    .setTextView(tvInviteGift)
                    .create();
//            tvInviteGift.setText("3.好友注册输入您的专享邀请码："+cellPhone);
        }

        findViewById(R.id.awardRecordForInvest).setOnClickListener(this);
        findViewById(R.id.awardRecordForRegist).setOnClickListener(this);


        findViewById(R.id.clickRule).setOnClickListener(this);

        findViewById(R.id.inviteFriends).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InviteGifeDialog inviteGifeDialog = new InviteGifeDialog(InviteGiftActivity.this);
                inviteGifeDialog.setData(info.shareUrl,info.imageUrl,mPromoteLinks.getMsg());
                inviteGifeDialog.show();
//                String msg = "";
//                if (info != null) {
//                    if (mPromoteLinks != null) {
//                        msg = TextUtils.isEmpty(mPromoteLinks.getMsg()) ? "" : mPromoteLinks.getMsg();
//                    }
//                    MyQRcodeActivity.goQRCodeActivity(InviteGiftActivity.this, info.shareUrl, info.imageUrl, msg);
//                } else {
//                    MyQRcodeActivity.goQRCodeActivity(InviteGiftActivity.this, null, null, msg);
//                }
//                if (null == mPromoteLinks) {
//                    mHttpService.getPromoteLinks();
//                    Toast.makeText(InviteGiftActivity.this, "正在获取邀请链接，请稍后再试", Toast.LENGTH_SHORT).show();
//                } else {
//                    showShare(mPromoteLinks.getMsg() + mPromoteLinks.getUrl(), null, mPromoteLinks.getUrl());
//                }
            }
        });
        if (user != null) {
            mHttpService.getInviteShowInfo(user.getUserId() + "");
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

    private void showShare(String text, String imageUrl, String link) {
//        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(HttpService.mBaseUrl);
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
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.vpfinance.cn/");

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_getPromoteLinks.ordinal()) {
            mPromoteLinks = mHttpService.onPromoteLinks(json);
        }
        if (reqId == ServiceCmd.CmdId.CMD_voucherExchange.ordinal()) {

        }
        if (reqId == ServiceCmd.CmdId.CMD_InviteGiftShowInfo.ordinal()) {
            InviteShowInfo inviteShowInfo = mHttpService.onGetInviteShowInfo(json);
            if ("true".equals(inviteShowInfo.success)) {
                bonusesCount.setText(FormatUtils.formatAbout(inviteShowInfo.bonusesCount)+"元");
                invitePerCount.setText(inviteShowInfo.invitePerCount + "人");
                registerRewardCount.setText(FormatUtils.formatAbout(inviteShowInfo.registerRewardCount)+"元");
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_GET_SHARE_URL.ordinal()) {
            MyQRcodeActivity.ShareInfo info = MyQRcodeActivity.parseShareInfo(json);
            if (info != null) {
                this.info = info;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clickRule:
                gotoActivity(RuleActivity.class);
                break;
            case R.id.awardRecordForInvest:
                gotoActivity(AwardRecordForInvestActivity.class);
                break;
            case R.id.awardRecordForRegist:
                gotoActivity(AwardRecordForRegistActivity.class);
                break;
        }
    }
}
