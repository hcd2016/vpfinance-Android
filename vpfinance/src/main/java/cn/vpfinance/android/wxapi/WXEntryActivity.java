/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package cn.vpfinance.android.wxapi;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.tdk.utils.HttpDownloader;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.WXAccessTokenAModel;
import cn.vpfinance.vpjr.module.common.WeiXinBindPhoneActivity;
import cn.vpfinance.vpjr.module.gusturelock.LockSetupActivity;
import cn.vpfinance.vpjr.retrofit.RetrofitUtil;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.GsonUtil;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends WechatHandlerActivity implements IWXAPIEventHandler, HttpDownloader.HttpDownloaderListener {

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private HttpService httpService;
    private UserRegisterBean userRegisterBean;
    private Handler m_handler;
    private UserDao dao = null;
    private User user;
    private WXAccessTokenAModel wxAccessTokenAModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IWXAPI mWxApi = ((App) getApplication()).mWxApi;
        mWxApi.handleIntent(getIntent(), this);
        httpService = new HttpService(this, this);
    }

    /**
     * 处理微信发出的向第三方应用请求app message
     * <p>
     * 在微信客户端中的聊天页面有“添加工具”，可以将本应用的图标添加到其中
     * 此后点击图标，下面的代码会被执行。Demo仅仅只是打开自己而已，但你可
     * 做点其他的事情，包括根本不打开任何页面
     */
    public void onGetMessageFromWXReq(WXMediaMessage msg) {
        Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity(iLaunchMyself);
    }

    /**
     * 处理微信向第三方应用发起的消息
     * <p>
     * 此处用来接收从微信发送过来的消息，比方说本demo在wechatpage里面分享
     * 应用时可以不分享应用文件，而分享一段应用的自定义信息。接受方的微信
     * 客户端会通过这个方法，将这个信息发送回接收方手机上的本demo中，当作
     * 回调。
     * <p>
     * 本Demo只是将信息展示出来，但你可做点其他的事情，而不仅仅只是Toast
     */
    public void onShowMessageFromWXReq(WXMediaMessage msg) {
        if (msg != null && msg.mediaObject != null
                && (msg.mediaObject instanceof WXAppExtendObject)) {
            WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
            Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()) Utils.Toast("分享失败");
                else Utils.Toast("登录失败");
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) resp).code;
                        Logger.e("code = " + code);
//						https://api.weixin.qq.com/sns/oauth2/access_token?appid=%@&secret=%@&code=%@&grant_type=authorization_code"

                        //get
//                        String url =  "https://api.weixin.qq.com/sns/oauth2/access_token?";
                        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                                "appid=" + Constant.WEIXIN_APP_ID + "&secret=" + Constant.WEIXIN_APP_SECRET + "&" + "code=" + code + "&" + "grant_type=authorization_code";
//                        httpService.wxAccessToken(url,Constant.WEIXIN_APP_ID,Constant.WEIXIN_APP_SECRET,code,"authorization_code");
                        Request request = new Request.Builder().url(url).build();
                        RetrofitUtil.getUnsafeOkHttpClient().newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.getMessage();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {//授权成功
                                String body = response.body().string();
                                wxAccessTokenAModel = GsonUtil.modelParser(body, WXAccessTokenAModel.class);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (null != wxAccessTokenAModel) {
                                            SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(WXEntryActivity.this);
                                            if (preferencesHelper.getBooleanValue(SharedPreferencesHelper.KEY_WX_BIND_IS_FROM_SETTING)) {//设置过来的直接调用绑定微信
                                                boolean isPersonType = SharedPreferencesHelper.getInstance(WXEntryActivity.this).getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, true);
                                                if (isPersonType) {
                                                    httpService.bindWEIXIN(wxAccessTokenAModel.getUnionid(), wxAccessTokenAModel.getOpenid(), "1", DBUtils.getUser(WXEntryActivity.this).getCellPhone(), "", "1");
                                                } else {
                                                    httpService.bindWEIXIN(wxAccessTokenAModel.getUnionid(), wxAccessTokenAModel.getOpenid(), "2", DBUtils.getUser(WXEntryActivity.this).getEmail(), "", "1");
                                                }
                                            } else {
                                                userRegisterBean = new UserRegisterBean();
                                                userRegisterBean.setOpenid(wxAccessTokenAModel.getOpenid());
                                                userRegisterBean.setScope(wxAccessTokenAModel.getScope());
                                                userRegisterBean.setUnionid(wxAccessTokenAModel.getUnionid());
                                                userRegisterBean.setReferrerNum(wxAccessTokenAModel.getRefresh_token());
                                                userRegisterBean.setAccess_token(wxAccessTokenAModel.getAccess_token());
                                                userRegisterBean.setFromWeixin(true);
                                                boolean isPersonType = SharedPreferencesHelper.getInstance(WXEntryActivity.this).getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, true);
                                                if (isPersonType) {
                                                    httpService.getIsBindWeiXin(userRegisterBean.getUnionid(), "1");
                                                } else {
                                                    httpService.getIsBindWeiXin(userRegisterBean.getUnionid(), "2");
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        //就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求
                        break;

                    case RETURN_MSG_TYPE_SHARE:
                        Utils.Toast("微信分享成功");

                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (reqId == ServiceCmd.CmdId.CMD_IS_BIND_WEIXIN.ordinal()) {
            String msg = json.optString("msg");
            switch (msg) {
                case "5"://未绑定平台
                    userRegisterBean.setUserType(SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, true));
                    WeiXinBindPhoneActivity.startWeiXinBindPhoneActivity(WXEntryActivity.this, userRegisterBean);
                    finish();
                    break;
                case "1"://登录成功
                    clearDB();
                    String uid = json.optString("uid");
                    AppState.instance().setSessionCode("" + uid);
                    if (!TextUtils.isEmpty(uid)) {
                        loginSucess(Long.parseLong(uid));
                        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                        String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
                        if (!TextUtils.isEmpty(uid) && !uid.equals(savedUid)) {
                            preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID, uid);
                        }
                        //微信登录保存unionid
                        String unionid = userRegisterBean.getUnionid();
                        if (!TextUtils.isEmpty(unionid)) {
                            preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_WEIXIN_UNIONID, unionid);
                        }
                        Utils.Toast("登录成功!");
                    }
                    App application = (App) getApplication();
                    application.isLogin = true;
                    SharedPreferencesHelper.getInstance(this).putBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, userRegisterBean.getUserType());
                    getUser();
                    httpService.getUserInfo();
                    break;
                case "2"://已注销
                    Utils.Toast("账户已注销");
                    break;
                case "0"://账户已锁定
                    Utils.Toast("账户已锁定");
                    break;
                case "6"://账户锁定
                    String lockTime = json.optString("lockTime");
                    if (lockTime != null) {
                        long l = Long.parseLong(lockTime);
                        Utils.Toast("该账户已被锁定,请" + l / (60 * 1000) + "分钟后重试");
                    }
                    break;
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
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
                ((App) getApplication()).isNeedUpdatePwd = true;
            }

            if (user != null) {
                if (dao != null && AppState.instance().logined()) {
                    dao.insertOrReplace(user);
                }
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                String username = user.getUserName();
                String cellPhone = user.getCellPhone();
                if (userRegisterBean.getUserType()) {//个人用户
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_CELL_PHONE, cellPhone);
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, username);
                } else {//企业用户
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
                EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_WEIXIN_LOGIN_SUCCESS));
                finish();
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_WEIXIN_BIND.ordinal()) {//绑定微信
            String msg = json.optString("msg");
            switch (msg) {
                case "0":
                    Utils.Toast("账号被锁定");
                    break;
                case "1":
                    Utils.Toast("您已成功绑定微信");
                    EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_BIND_WEIXIN_SUCCESS_FROM_SETTING));
                    finish();
//                    Long uid = json.optLong("uid");
//                    DBUtils.getUser(WXEntryActivity.this).setUserId(uid);
//                    EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_WEIXIN_LOGIN_SUCCESS));//微信登录成功
//                    finish();
                    break;
//                case "2"://此用户为企业用户，不允许注册，请跳转到企业注册页面
//                    Utils.Toast("请先注册");
//                    userRegisterBean.setCaptcha(etCaptcha.getText().toString());
//                    RegisterCompanyInfoActivity.goThis(this, userRegisterBean);
//                    break;
//                case "3"://手机号未注册，跳转设置密码,再调用接口
//                    userRegisterBean.setCaptcha(etCaptcha.getText().toString());
//                    LoginPasswordActivity.goThis(this, userRegisterBean);
//                    break;
//                case "4":
//                    Utils.Toast("账号已注销");
//                    break;
//                case "5":
//                    Utils.Toast("验证码不正确");
//                    break;
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

        dbHelper = new DaoMaster.DevOpenHelper(WXEntryActivity.this, Config.DB_NAME, null);
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

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {

    }

    @Override
    public void onHttpCache(int reqId) {

    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        errmsg.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
