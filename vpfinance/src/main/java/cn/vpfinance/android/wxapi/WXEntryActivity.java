/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package cn.vpfinance.android.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpLoader;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.model.RegularProductList;
import cn.vpfinance.vpjr.model.WXAccessTokenAModel;
import cn.vpfinance.vpjr.module.common.WeiXinBindPhoneActivity;
import cn.vpfinance.vpjr.network.OkHttpUtil;
import cn.vpfinance.vpjr.retrofit.RetrofitUtil;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.GsonUtil;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends WechatHandlerActivity implements IWXAPIEventHandler, HttpDownloader.HttpDownloaderListener {

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private HttpService httpService;
    private UserRegisterBean userRegisterBean;
    private Handler m_handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IWXAPI mWxApi = ((FinanceApplication) getApplication()).mWxApi;
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
                        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                                "appid=" + Constant.WEIXIN_APP_ID + "&secret=" + Constant.WEIXIN_APP_SECRET + "&" + "code=" + code + "&" + "grant_type=authorization_code";
                        Request request = new Request.Builder().url(url).build();
                        RetrofitUtil.genericClient().newCall(request).enqueue(new Callback() {


                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.getMessage();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String body = response.body().string();
                                WXAccessTokenAModel wxAccessTokenAModel = GsonUtil.modelParser(body, WXAccessTokenAModel.class);
                                if (null != wxAccessTokenAModel) {
                                    userRegisterBean = new UserRegisterBean();
                                    userRegisterBean.setOpenid(wxAccessTokenAModel.getOpenid());
                                    userRegisterBean.setScope(wxAccessTokenAModel.getScope());
                                    userRegisterBean.setUnionid(wxAccessTokenAModel.getUnionid());
                                    userRegisterBean.setReferrerNum(wxAccessTokenAModel.getRefresh_token());
                                    userRegisterBean.setAccess_token(wxAccessTokenAModel.getAccess_token());
                                    userRegisterBean.setFromWeixin(true);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (((FinanceApplication) getApplication()).isPersonType) {
                                                httpService.getIsBindWeiXin(userRegisterBean.getUnionid(), "1");
                                            } else {
                                                httpService.getIsBindWeiXin(userRegisterBean.getUnionid(), "2");
                                            }
                                        }
                                    });


//                                    ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_IS_BIND_WEIXIN;
//                                    String method = ServiceCmd.getMethodName(cmdId);
//                                    String url = HttpService.getServiceUrl(method);


//
//                                    FormBody.Builder params=new FormBody.Builder();
//                                    params.add("unionid", wxAccessTokenAModel.getUnionid());
//                                    if (((FinanceApplication) getApplication()).isPersonType) {
//                                        params.add("type", "1");
//                                    } else {
//                                        params.add("type", "2");
//                                    }
//                                    Request request = new Request.Builder()
//                                            .addHeader("APP-VERSION", Utils.getVersion(FinanceApplication.getAppContext()))
//                                            .url(url)
//                                            .post(params.build())
//                                            .build();
//
//                                    OkHttpClient httpClient = new OkHttpClient();
//                                    httpClient.newCall(request).enqueue(new Callback() {
//                                        @Override
//                                        public void onFailure(Call call, IOException e) {
//
//                                        }
//
//                                        @Override
//                                        public void onResponse(Call call, Response response) throws IOException {
//                                            String s = response.body().toString();
//                                            try {
//                                                JSONObject object = new JSONObject(s);
//                                                String msg = object.optString("msg");
//                                                switch (msg) {
//                                                    case "5"://未绑定平台
//                                                        userRegisterBean.setUserType(((FinanceApplication) getApplication()).isPersonType);
//                                                        WeiXinBindPhoneActivity.startWeiXinBindPhoneActivity(WXEntryActivity.this,userRegisterBean);
//                                                        finish();
//                                                        break;
//                                                    case "1"://登录成功
//                                                        String uid = object.optString("uid");
//                                                        if(TextUtils.isEmpty(uid)) {
//                                                            DBUtils.getUser(WXEntryActivity.this).setUserId(Long.parseLong(uid));
//                                                            AppState.instance().setSessionCode("" + uid);
//                                                            Utils.Toast("登录成功!");
//                                                        }
//                                                        finish();
//                                                        break;
//                                                    case "2"://已注销
//                                                        Utils.Toast("账户已注销");
//                                                        break;
//                                                    case "0"://账户已锁定
//                                                        Utils.Toast("账户已锁定");
//                                                        break;
//                                                }
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
                                }


//								{
//									"access_token":"ACCESS_TOKEN",
//										"expires_in":7200,
//										"refresh_token":"REFRESH_TOKEN",
//										"openid":"OPENID",
//										"scope":"SCOPE",
//										"unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"
//								}
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
                    userRegisterBean.setUserType(((FinanceApplication) getApplication()).isPersonType);
                    WeiXinBindPhoneActivity.startWeiXinBindPhoneActivity(WXEntryActivity.this, userRegisterBean);
                    finish();
                    break;
                case "1"://登录成功
                    String uid = json.optString("uid");
                    if (TextUtils.isEmpty(uid)) {
                        AppState.instance().setSessionCode("" + uid);
                        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                        String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
                        if (!TextUtils.isEmpty(uid) && !uid.equals(savedUid)) {
                            preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID, uid);
                        }
                        Utils.Toast("登录成功!");
                    }
                    EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_WEIXIN_LOGIN_SUCCESS));
                    finish();
                    break;
                case "2"://已注销
                    Utils.Toast("账户已注销");
                    break;
                case "0"://账户已锁定
                    Utils.Toast("账户已锁定");
                    break;
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
        errmsg.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
