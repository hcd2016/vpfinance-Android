package com.jewelcredit.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jewelcredit.model.AppUpdateInfo;
import com.jewelcredit.model.TradeFlowRecordInfo;
import com.tdk.utils.HttpDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.adapter.RegularAdapter;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.BonusProfit;
import cn.vpfinance.vpjr.greendao.BorrowerInfo;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.InvestRecord;
import cn.vpfinance.vpjr.greendao.LoanRecord;
import cn.vpfinance.vpjr.greendao.MyInvestRecord;
import cn.vpfinance.vpjr.greendao.QueryPage;
import cn.vpfinance.vpjr.greendao.QueryPageDao;
import cn.vpfinance.vpjr.greendao.TrialBonus;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.AddRateBean;
import cn.vpfinance.vpjr.gson.AddrateTicketTabInfo;
import cn.vpfinance.vpjr.gson.AllMedalsBean;
import cn.vpfinance.vpjr.gson.AppmemberIndexBean;
import cn.vpfinance.vpjr.gson.AutoInvestSettingBean;
import cn.vpfinance.vpjr.gson.BankBean;
import cn.vpfinance.vpjr.gson.BannerBean;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.gson.HomeVpProductInfo;
import cn.vpfinance.vpjr.gson.InvestSummaryTab1Bean;
import cn.vpfinance.vpjr.gson.InvestSummaryTab2Bean;
import cn.vpfinance.vpjr.gson.InvestSummaryTab3Bean;
import cn.vpfinance.vpjr.gson.InvestTopBean;
import cn.vpfinance.vpjr.gson.IphoneDesBean;
import cn.vpfinance.vpjr.gson.JewelryBean;
import cn.vpfinance.vpjr.gson.LoanProtocolBean;
import cn.vpfinance.vpjr.gson.LoanSignDepositBean;
import cn.vpfinance.vpjr.gson.LoanSignListNewBean;
import cn.vpfinance.vpjr.gson.NewAppUpdateInfo;
import cn.vpfinance.vpjr.gson.NewRecordsBean;
import cn.vpfinance.vpjr.gson.OneYearBean;
import cn.vpfinance.vpjr.gson.PersonalCardBean;
import cn.vpfinance.vpjr.gson.PersonalInfo;
import cn.vpfinance.vpjr.gson.PrivateSettingBean;
import cn.vpfinance.vpjr.gson.ProductCarInfo;
import cn.vpfinance.vpjr.gson.RecordDepositDetailBean;
import cn.vpfinance.vpjr.gson.RecordDetailBean;
import cn.vpfinance.vpjr.gson.ReturnEventBean;
import cn.vpfinance.vpjr.gson.ReturnMonthBean;
import cn.vpfinance.vpjr.gson.SelectVoucherBean;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.model.AwardRecordInvestInfo;
import cn.vpfinance.vpjr.model.AwardRecordRegistInfo;
import cn.vpfinance.vpjr.model.Baoli;
import cn.vpfinance.vpjr.model.BaoliItem;
import cn.vpfinance.vpjr.model.BuyResult;
import cn.vpfinance.vpjr.model.CarInfo;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.FundFlowInfo;
import cn.vpfinance.vpjr.model.FundOverInfo;
import cn.vpfinance.vpjr.model.FundRecordInfo;
import cn.vpfinance.vpjr.model.HomeInfo;
import cn.vpfinance.vpjr.model.InviteShowInfo;
import cn.vpfinance.vpjr.model.PayInfo;
import cn.vpfinance.vpjr.model.PresellProductInfo;
import cn.vpfinance.vpjr.model.ProjectIntroduction;
import cn.vpfinance.vpjr.model.PromoteLinks;
import cn.vpfinance.vpjr.model.QualificationMaterial;
import cn.vpfinance.vpjr.model.QueryPageInfo;
import cn.vpfinance.vpjr.model.TransferListItemInfo;
import cn.vpfinance.vpjr.model.TransferProductDetailInfo;
import cn.vpfinance.vpjr.model.TransferRefundInfo;
import cn.vpfinance.vpjr.model.Voucher;
import cn.vpfinance.vpjr.model.VoucherArray;
import cn.vpfinance.vpjr.module.user.personal.TrialCoinActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import de.greenrobot.dao.query.QueryBuilder;


public class HttpService {
    //	public static String mBaseUrl = BaseUrl.mBaseUrl;
    public static String mBaseUrl = "https://www.vpfinance.cn";
    public HttpLoader httpClient;

    public static final String LOG_KEY = "1234567890";

    private String mPasscodeErrmsg = "";
    private Context mContext = null;

    private static final int DELAY_SECONDS = 60;
    private static boolean mPresentLoginFlag = false;
    private static long mLastPresentLoginTime = 0;
    // 两次present login的时间间隔为5秒
    private static long mPresentLoginPeriod = 5 * 1000;    // 10秒


    public String getPasscodeErrmsg() {
        return mPasscodeErrmsg;
    }


    public static void clearPresentLoginFlag() {
        mPresentLoginFlag = false;
    }

    public static int getDelaySeconds() {
        return DELAY_SECONDS;
    }

    public HttpService(Context context, HttpDownloader.HttpDownloaderListener listener) {
        httpClient = new HttpLoader(context, listener);
        mContext = context;
        mBaseUrl = context.getResources().getString(R.string.host);
    }

    public static String getServiceUrl(String method) {
        return mBaseUrl + method;
    }


    public boolean isSessionTimeout(JSONObject json) {
        try {
            String msg = json.optString("message");
            if (msg.contains("未登录")) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }


    /*
	 * 3.2.	发送手机验证码
	 *	params:
	 *		userName	输入的用户名称
	 *		phoneNum	输入的手机号码
	 * 		smsStatus	1.注册验证码.		2.找回密码		3.修改密码
	 *
	 * return boolean
	 *
	 * 	output json:
	 * 		msgCode:
成功：000000
314001 表示参数缺失
314002 表示发送失败
391009 表示校验码超时
391007 表示校验码有误
391008 表示输入错误次数过多

	 */
    public boolean getVerifyCode(String userKey,
                                 String userNo,
                                 String userName,
                                 String phoneNum,
                                 String smsStatus, String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getVerifyCode;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

//		params.put("CI_KEY", userKey);
//		params.put("CI_NO", userNo);
//		params.put("CI_NM", userName);
        params.put("phone", phoneNum);
//		params.put("SMS_STATUS", smsStatus);
        params.put("type", type);

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }


    public String onGetVerifyCode(JSONObject json) {

        try {
            int msgCode = json.optInt("msg", -1);
            String msg = "发送失败";
            switch (msgCode) {
                case 0:
                    msg = "发送失败";
                    break;
                case 1:
                    msg = "发送成功";
                    break;
                case 2:
                    msg = "操作太频繁，请稍后再试";
                    break;
                case 3:
                    msg = "手机号码格式不正确";
                    break;
                case 4:
                    msg = "手机号己经存在";
                    break;
                case 5:
                    msg = "操作太频繁，请输入图形验证码";
                    break;
                case 6:
                    msg = "手机号不存在";
                    break;
                default:
                    break;
            }
            mPasscodeErrmsg = msg;
            return msg;
        } catch (Exception ex) {
        }

        return "请求验证码出错";
    }


    /*
	 * 3.4.	会员注册
	 *	params:
	 *		userName		输入的用户名称
	 *		userPsw			输入的用户密码
	 *		phoneNum		输入的手机号码
	 *		verifyCode		验证码
	 *
	 * return boolean
	 *
	 * 	output json:
	 * 		msgCode:
000000表示注册成功
	 */
    public boolean userRegister(String recommend, String userName,
                                String userPsw,
                                String phoneNum,
                                String verifyCode) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_userRegister;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("userName", userName);
        params.put("pwd", userPsw);
        params.put("phone", phoneNum);
        params.put("captcha", verifyCode);
        params.put("recommend", recommend);
        params.put("regChannel", "1");


        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }


    public String onUserRegister(JSONObject json) {

        try {
            int msgCode = json.optInt("msg", -1);
            String msg = "注册失败";
            switch (msgCode) {
                case 0:
                    msg = "验证码错误";
                    break;
                case 1:
                    msg = "注册成功";
                    break;
                case 2:
                    msg = "注册出现错误";
                    break;
                case 3:
                    msg = "用户名己被注册";
                    break;
                case 4:
                    msg = "手机号己被注册";
                    break;
                case 6:
                    msg = "推荐人不存在";
                    break;
                default:
                    break;
            }
            return msg;
        } catch (Exception ex) {
        }

        return "注册失败";
    }


    /*
	 * 3.5.	会员登录
	 *	params:
	 *		userName		输入的用户名称
	 *		userPsw			输入的用户密码
	 *
	 * return boolean
	 *
	 * 	output json:
	 * 		msgCode:
成功：000000
	 * 		SESSION_ID:
	 * 			session id,用于鉴权
	 */
    public boolean userLogin(String userName,
                             String userPsw) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_userLogin;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("userName", userName);
        params.put("pwd", userPsw);
        params.put("TYPE", "android");

        httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }


    public String onUserLogin(Context context, JSONObject json) {
        String msg = "登录失败";
        try {
            int msgCode = json.optInt("msg", -1);
            long uid = json.optInt("uid", -1);
            long errorCount = json.optInt("errorCount");
            long lockTime = json.optLong("lockTime");
            switch (msgCode) {
                case 0:
                    msg = "网络不给力,请稍候重试";
                    break;
                case 1:
                    msg = "";//登录成功
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

                    dbHelper = new DaoMaster.DevOpenHelper(context, Config.DB_NAME, null);
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
                    break;
                case 2:
                    if (errorCount > 0) {
                        msg = "用户名密码错误,你还有" + errorCount + "次机会";
                    } else {//该账户已被锁定，请xx分钟后再试
                        msg = "该账户已被锁定, 请" + (lockTime / 1000 / 60) + "分钟后再试";
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
        }

        return msg;
    }

    public boolean resetLoginPassword2(String uid,
                                       String oldPwd,
                                       String newPwd) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_resetLoginPassword;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("old_pwd", oldPwd);
        params.put("pwd", newPwd);
        params.put("uid", uid);

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onResetLoginPassword2(JSONObject json) {
        int msgCode = json.optInt("msg");
        String errorMsg = "";
        if (msgCode == 0) {
            errorMsg = "原密码输入错误";
        } else if (msgCode == 1) {
            errorMsg = "修改成功";
        } else if (msgCode == 2) {
            errorMsg = "新密码不能和旧密码一样";
        }
        mPasscodeErrmsg = errorMsg;
        return errorMsg;
    }


    public boolean forgetLoginPassword(String phone, String newPwd, String smscode) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_forgetLoginPassword;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("phone", phone);
        params.put("pwd", newPwd);
        params.put("smscode", smscode);

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onForgetLoginPassword(JSONObject jsonObject) {
        int errCode = jsonObject.optInt("msg");
        String errMsg;
        switch (errCode) {
            case 0:
                errMsg = "验证码错误";
                break;

            case 1:
                errMsg = "成功";
                break;

            case 2:
                errMsg = "网络异常";
                break;

            case 4:
                errMsg = "验证码超时";
                break;
            case 5:
                errMsg = "手机号码不存在";
                break;
            default:
                errMsg = "操作失败";
                break;
        }
        return errMsg;
    }

    public boolean resetTradePassword2(String type,
                                       String pwd,
                                       @Nullable String new_pwd,
                                       String smscode,
                                       String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_resetTradePassword;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("type", type);
        params.put("pwd", pwd);
        if (!TextUtils.isEmpty(new_pwd)) {
            params.put("new_pwd", new_pwd);
        }
        params.put("smscode", smscode);
        params.put("uid", uid);

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onResetTradePassword2(JSONObject json) {
        int msgCode = json.optInt("msg");
        String errorMsg;
        if (msgCode == 0) {
            errorMsg = "用户不存在";
        } else if (msgCode == 1) {
            errorMsg = "操作成功";
        } else if (msgCode == 2) {
            errorMsg = "验证码错误";
        } else if (msgCode == 3) {
            errorMsg = "原交易密码不正确";
        } else if (msgCode == 4) {
            errorMsg = "短信己超时";
        } else if (msgCode == 5) {
            errorMsg = "内部错误";
        } else {
            errorMsg = "未知错误";
        }
        mPasscodeErrmsg = errorMsg;
        return errorMsg;
    }

    public boolean bindingEmail(String code, String email) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_BIND_EMAIL;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("code", code);
        params.put("email", email);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 192.168.1.253:8080/Appmember_index/bindingEmail
     * 参数：code,email
     * 返回参数：
     * status: 1验证码超时 2验证码错误 3邮件发送等待用户激活 4发送失败，系统错误
     * success: true(status为3的时候) false
     */
    public int onBindingEmail(JSONObject json) {
        //{"status":"2","success":"false"}
        int status = json.optInt("status");
        return status;

    }

    public boolean realnameAuth(String name, String cardId, String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_realnameAuth;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("name", name);
        params.put("cardId", cardId);
        params.put("uid", uid);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public String onRealnameAuth(JSONObject json) {
        if (isSessionTimeout(json)) {
            return "用户未登录或登录态已超时!";
        }

        int msgCode = json.optInt("msgCode");
        String errorMsg;
        switch (msgCode) {
            case 1:
                errorMsg = "认证成功";
                break;

            case 2:
                errorMsg = "身份证号无效";
                break;

            case 3:
                errorMsg = "身份证号验证失败";
                break;

            case 4:
                errorMsg = "名字必须是汉字";
                break;

            case 5:
                errorMsg = "名字为空";
                break;
            case 6:
                errorMsg = "身份证号己存在";
                break;

            case 0:
            default:
                errorMsg = "认证失败";
                break;
        }

        return errorMsg;
    }

    public String onAddBankCard(JSONObject json, BankCard card) {
        boolean success = json.optBoolean("success");
        String errorMsg;
        if (success) {
            errorMsg = "操作成功";

            if (card == null) {
                card = new BankCard();
            }

            JSONObject userbank = json.optJSONObject("card");
            if (userbank != null) {
                String accountName = userbank.optString("accountName");
                String bankAccount = userbank.optString("bankAccount");
                String bankname = userbank.optString("bankname");
                String branch = userbank.optString("branch");
                String province = userbank.optString("province");
                String city = userbank.optString("city");

                card.setAccountName(accountName);
                card.setAccountName(accountName);
                card.setBankAccount(bankAccount);
                card.setBankname(bankname);
                card.setBranch(branch);
                card.setProvince(province);
                card.setCity(city);

                JSONObject banktype = userbank.optJSONObject("banktype");
                if (banktype != null) {
                    long id = banktype.optInt("id");
                    String name = banktype.optString("name");
                    card.setId(id);
                    card.setName(name);
                }
            }
        } else {
            errorMsg = "添加失败";
        }
        return errorMsg;
    }

    public boolean withdraw(String uid, String money, String smsCode, String transCode) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_WITHDRAW;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);
        params.put("money", money);
        params.put("smscode", smsCode);
        params.put("transCode", transCode);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 获取充值信息
     *
     * @param uid
     * @param money
     * @return
     */
    public boolean getPayInfo(String uid, String money) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_GET_PAY_INFO_NEW;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("user_id", uid);
        params.put("money_order", money);
        params.put("name_goods", "Android客户端充值");

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public PayInfo onGetPayInfo(JSONObject json) {
/*
{
	"message": {
		"acct_name": "张清田",
		"bankaccount": "6225880117166257",
		"busi_partner": "101001",
		"dt_order": "20150729110147",
		"flag_modify": "1",
		"id_no": "370126198707082433",
		"info_order": "用户购买Android客户端充值",
		"money_order": "100.0",
		"name_goods": "Android客户端充值",
		"no_agree": "",
		"no_order": "20150729110147_115",
		"notify_url": "http://501d9fb.nat123.net/AppNotify/notify",
		"oid_partner": "201507071000401511",
		"req_url": "https://yintong.com.cn/payment/bankgateway.htm",
		"risk_item": "{\"frms_ware_category\":\"1999\",\"request_imei\":\"1122111221\",\"user_info_bind_phone\":\"18565772900\",\"user_info_dt_register\":\"2015-07-03 09:42:37\"}",
		"sign": "7f4ec24a919432ecb5d7250ffbfedb07",
		"sign_type": "MD5",
		"timestamp": "20150729110147",
		"url_order": "",
		"url_return": "http://501d9fb.nat123.net/llreceive/return",
		"user_id": "115",
		"userreq_ip": "59_40_3_118",
		"valid_order": "10080",
		"version": "1.0"
	},
	"messageCode": "0000"
}
 */

//		String card_no = json.optString("bankaccount");
//		String no_agree = json.optString("no_agree");

        JSONObject message = json.optJSONObject("message");
        if (message != null) {
            String acct_name = message.optString("acct_name");
            String back_url = message.optString("back_url");
            String busi_partner = message.optString("busi_partner");
            String dt_order = message.optString("dt_order");
            String flag_modify = message.optString("flag_modify");
            String id_no = message.optString("id_no");
            String id_type = message.optString("id_type");
            String info_order = message.optString("info_order");
            String money_order = message.optString("money_order");
            String name_goods = message.optString("name_goods");
            String no_order = message.optString("no_order");
            String notify_url = message.optString("notify_url");
            String oid_partner = message.optString("oid_partner");
            String req_url = message.optString("req_url");
            String risk_item = message.optString("risk_item");
            String sign = message.optString("sign");
            String sign_type = message.optString("sign_type");
            String timestamp = message.optString("timestamp");
            String url_order = message.optString("url_order");
            String url_return = message.optString("url_return");
            String user_id = message.optString("user_id");
            String userreq_ip = message.optString("userreq_ip");
            String valid_order = message.optString("valid_order");
            String version = message.optString("version");

            String card_no = message.optString("bankaccount");
            String no_agree = message.optString("no_agree");

            PayInfo info = new PayInfo();
            info.setAcct_name(acct_name);
            info.setBack_url(back_url);
            info.setBusi_partner(busi_partner);
            info.setDt_order(dt_order);
            info.setFlag_modify(flag_modify);
            info.setId_no(id_no);
            info.setId_type(id_type);
            info.setInfo_order(info_order);
            info.setMoney_order(money_order);
            info.setName_goods(name_goods);
            info.setNo_order(no_order);
            info.setNotify_url(notify_url);
            info.setOid_partner(oid_partner);
            info.setReq_url(req_url);
            info.setRisk_item(risk_item);
            info.setSign(sign);
            info.setSign_type(sign_type);
            info.setTimestamp(timestamp);
            info.setUrl_order(url_order);
            info.setUrl_return(url_return);
            info.setUser_id(user_id);
            info.setUserreq_ip(userreq_ip);
            info.setValid_order(valid_order);
            info.setVersion(version);

            info.setCard_no(card_no);
            info.setNo_agree(no_agree);
            info.setFlag_modify(flag_modify);
            info.setSign_type(sign_type);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("acct_name", acct_name);
            map.put("back_url", back_url);
            map.put("busi_partner", busi_partner);
            map.put("dt_order", dt_order);
            map.put("flag_modify", flag_modify);
            map.put("id_no", id_no);
            map.put("id_type", id_type);
            map.put("info_order", info_order);
            map.put("money_order", money_order);
            map.put("name_goods", name_goods);
            map.put("no_order", no_order);
            map.put("notify_url", notify_url);
            map.put("oid_partner", oid_partner);
            map.put("req_url", req_url);
            map.put("risk_item", risk_item);
            map.put("sign", sign);
            map.put("sign_type", sign_type);
            map.put("timestamp", timestamp);
            map.put("url_order", url_order);
            map.put("url_return", url_return);
            map.put("user_id", user_id);
            map.put("userreq_ip", userreq_ip);
            map.put("valid_order", valid_order);
            map.put("version", version);

            info.setMap(map);

            return info;
        }
        return null;
    }

    /**
     * 获取银行卡信息接口
     *
     * @param uid
     * @return
     */
    public boolean getBankCard(String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getBankCard;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 获取用户绑定的银行卡，没有绑定银行卡返回null
     *
     * @param context
     * @param json
     * @return
     */
    public BankCard onGetBankCard(Context context, JSONObject json) {
/*
{
	"success": false,
	"msg": "还未绑定银行卡"
}

{
	"userbank": {
		"accountName": "张清田",
		"bankAccount": "6225880117166257",
		"bankname": "招商银行",
		"banktype": {
			"id": 1,
			"name": "招商银行"
		},
		"branch": "东三环支行",
		"id": 53,
		"province": "1"
	},
	"success": true,
	"msg": "操作成功"
}

userbank
accountName	String     	户名
bankAccount	String     	银行卡号
bankname	String	银行名
Branch	String	分行
bankType	Object	Id name
province	Integer	省
city	Integer	市	直辖市无此参数
success	Boolean	执行结果	True/False

*/
        BankCard card = null;
        String msg = json.optString("msg");
        boolean success = json.optBoolean("success");
        if (success) {
            JSONObject userbank = json.optJSONObject("userbank");
            if (userbank != null) {
                card = new BankCard();
                String accountName = userbank.optString("accountName");
                String bankAccount = userbank.optString("bankAccount");
                String bankname = userbank.optString("bankname");
                String branch = userbank.optString("branch");
                String province = userbank.optString("province");
                String city = userbank.optString("city");

                if (accountName == null) {
                    accountName = "";
                }

                if (bankAccount == null) {
                    bankAccount = "";
                }

                if (bankname == null) {
                    bankname = "";
                }

                if (branch == null) {
                    branch = "";
                }

                if (province == null) {
                    province = "";
                }

                if (city == null) {
                    city = "";
                }


                card.setAccountName(accountName);
                card.setAccountName(accountName);
                card.setBankAccount(bankAccount);
                card.setBankname(bankname);
                card.setBranch(branch);
                card.setProvince(province);
                card.setCity(city);
                card.setId(-1L);
                card.setName("");

                JSONObject banktype = userbank.optJSONObject("banktype");
                if (banktype != null) {
                    long id = banktype.optInt("id");
                    String name = banktype.optString("name");
                    card.setId(id);
                    card.setName(name);
                }

                DaoMaster.DevOpenHelper dbHelper;
                SQLiteDatabase db;
                DaoMaster daoMaster;
                DaoSession daoSession;
                BankCardDao dao;

                dbHelper = new DaoMaster.DevOpenHelper(context, Config.DB_NAME, null);
                db = dbHelper.getWritableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                dao = daoSession.getBankCardDao();

                if (dao != null) {
                    QueryBuilder<BankCard> qb = dao.queryBuilder();
                    qb.buildDelete().executeDeleteWithoutDetachingEntities();
                    dao.insertInTx(card);
                }

            }
        }
        return card;
    }

    public boolean addBankCard(String bankType, String bankAccount,
                               String accountName, String branch, String province, String city,
                               String bankname) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_addCard;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("banktype", bankType);//
        params.put("bankAccount", bankAccount);// 账号
        params.put("accountName", accountName);// 户主名
        params.put("branch", branch);// 分行名
        params.put("province", province);//
        params.put("city", city);//
        params.put("bankname", bankname);//
//		params.put("smscode", smscode);//

		/*
1	招商银行
2	中国银行
3	交通银行
4	平安银行
5	兴业银行
6	光大银行
7	华夏银行
8	中信银行
9	中国农业银行
10	中国工商银行
11	中国民生银行
12	深圳发展银行
13	广东发展银行
14	中国建设银行
15	上海浦东发展银行
16	中国储蓄邮政银行
17	其他银行
		 */

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public boolean delBankCard() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_delBankCard;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onDelBankCard(JSONObject jsonObject) {
        boolean success = jsonObject.optBoolean("success");
        String errMsg;
        if (success) {
            errMsg = "操作成功";
        } else {
            errMsg = "操作失败";
        }
        return errMsg;
    }

    public boolean getUserInfo() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_member_center;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("TYPE", "android");
        //0连连1存管
        params.put("accountType", "0");
        //httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public boolean getUserInfoBank() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_member_center;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("TYPE", "android");
        //0连连1存管
        params.put("accountType", "1");
        //httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public void onGetUserInfo(JSONObject json, User user) {
		/*
{
	"phone": "18565772900",
	"dataList": [
		["1434448810", "普通标", "2015-07-16", "101.0", "100.0+1.0", "1/1", "125"],
		["20140704", "普通标", "2015-08-04", "1010.0", "1000.0+10.0", "1/1", "145"],
		["20150707", "普通标", "2015-08-07", "15200.0", "200.0+15000.0", "1/1", "154"],
		["1436322034", "普通标", "2015-08-08", "100.67", "100.0+0.67", "1/1", "159"],
		["1436324545", "普通标", "2015-08-08", "101.67", "100.0+1.67", "1/1", "160"]
	],
	"set_trans_pwd": true,
	"preIncome": "15012.33",
	"invest": 1400.0000,
	"frozenAmtN": 4250.5,
	"netAsset": "28704.66",
	"dSum": "8.33",
	"identityCard": "431381197903117478",
	"userName": "vfishv",
	"realName": "朱德厚5",
	"dBid": 3700.0,
	"paying": "1111.0",
	"cashBalance": 5444.5,
	"registerTime": "2015-01-23 00:33:43"
}
identityCard身份证;
cashBalance		可用余额
dBid+dSum+invest+preIncome		待收余额
netAsset		账户总额
frozenAmtN		冻结资产
paying		偿还中借款


		*/
        String userName = json.optString("userName");
        String realName = json.optString("realName");
        String identityCard = json.optString("identityCard");//身份证
        double cashBalance = json.optDouble("cashBalance");
        double netAsset = json.optDouble("netAsset");//账户总额
        double frozenAmtN = json.optDouble("frozenAmtN");//冻结资产
        double paying = json.optDouble("paying");//偿还中借款

        String email = json.optString("email");
        String emailPass = json.optString("emailPass");

        //dBid+dSum+invest+preIncome 待收余额
        double dBid = json.optDouble("dBid");
        double dSum = json.optDouble("dSum");
        double invest = json.optDouble("invest");
        double preIncome = json.optDouble("preIncome");

        String cellPhone = json.optString("phone");

        if (user != null) {
            user.setEmail(email);
            user.setEmailPass(emailPass);

            user.setRealName(realName);
            user.setCellPhone(cellPhone);
            user.setUserName(userName);
            user.setIdentityCard(identityCard);
            user.setCashBalance(cashBalance);
            user.setNetAsset(netAsset);
            user.setFrozenAmtN(frozenAmtN);
            user.setPaying(paying);

            user.setDBid(dBid);
            user.setDSum(dSum);
            user.setInvest(invest);
            user.setPreIncome(preIncome);
            user.setHasTradePassword(json.optBoolean("set_trans_pwd"));
        }
    }

    /**
     * 个人中心投资记录
     *
     * @param loanType  1全部 2进行中、3回款中、4已完成
     * @param pageNum
     * @param beginTime
     * @param endTime
     * @return
     */
    public boolean getInvestRecord(String loanType, String pageNum, String beginTime, String endTime) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_INVEST_RECORD;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        if (loanType != null) {
            params.put("loanType", loanType);// 类型   1全部 2进行中、3回款中、4已完成
        }
        if (pageNum != null) {
            params.put("pageNum", pageNum);//非必填	默认为第1页
        }
        if (beginTime != null) {
            params.put("beginTime", beginTime);//起始时间 2015-07-16
        }
        if (endTime != null) {
            params.put("endTime", endTime);// 2015-07-16
        }

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 个人借贷
     *
     * @param loanId
     * @param product
     * @return
     */
    public boolean getPersonalProduct(String loanId, String product, long mNativeId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_commonLoanDesc;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);// 类型   1全部 2进行中、3回款中、4已完成
        params.put("product", product);//起始时间 2015-07-16
        if (mNativeId > 0) {
            params.put("deptLoanId", "" + mNativeId);
        }

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public PersonalInfo onGetPersonal(JSONObject json) {
        PersonalInfo personalInfo = null;
        try {
            Gson gson = new Gson();
            personalInfo = gson.fromJson(json.toString(), new TypeToken<PersonalInfo>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return personalInfo;
    }

    public String onGetRecord(JSONObject json, List<MyInvestRecord> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.clear();

        boolean success = json.optBoolean("success");
        String errMsg;
        if (success) {
            errMsg = "成功";
            JSONArray loans = json.optJSONArray("loans");
            if (loans != null) {
                for (int i = 0; i < loans.length(); i++) {
                    JSONObject item = loans.optJSONObject(i);
                    MyInvestRecord record = new MyInvestRecord();
                    String borrowername = item.optString("borrowername");
                    String deadline = item.optString("deadline");
                    double interestRate = item.optDouble("interestRate");
                    double issueLoan = item.optDouble("issueLoan");
                    String loanTitle = item.optString("loanTitle");
                    double money = item.optDouble("money");
                    String refundWay = item.optString("refundWay");
                    double schedule = item.optDouble("schedule");
                    double tenderMoney = item.optDouble("tenderMoney");
                    String tenderTime = item.optString("tenderTime");

                    record.setBorrowername(borrowername);
                    record.setDeadline(deadline);
                    record.setInterestRate(interestRate * 100);
                    record.setIssueLoan(issueLoan);
                    record.setLoanTitle(loanTitle);
                    record.setMoney(money);
                    record.setRefundWay(refundWay);
                    record.setSchedule(schedule);
                    record.setTenderMoney(tenderMoney);
                    record.setTenderTime(tenderTime);
                    list.add(record);
                }
            }
        } else {
            errMsg = "失败";
        }

        return errMsg;
    }

    public ArrayList<InvestRecord> onGetInvestRecord(JSONObject json) {
        ArrayList<InvestRecord> list = null;
        JSONArray loans = json.optJSONArray("loans");
        if (loans != null) {
            list = new ArrayList<InvestRecord>();
            for (int i = 0; i < loans.length(); i++) {
                JSONObject item = loans.optJSONObject(i);
                InvestRecord record = new InvestRecord();
                long id = item.optLong("id");
                int isSucceed = item.optInt("isSucceed");
                int product = item.optInt("product");
                double tenderMoney = item.optDouble("tenderMoney");
                String tenderTime = item.optString("tenderTime");
                long voucherId = item.optLong("voucherId");
                double voucherMoney = item.optDouble("voucherMoney");

                record.setIid(id);
                record.setIsSucceed(isSucceed);
                record.setProduct(product);
                record.setTenderMoney(tenderMoney);
                record.setTenderTime(tenderTime);
                record.setVoucherId(voucherId);
                record.setVoucherMoney(voucherMoney);
                list.add(record);
            }
        }

        return list;
    }

    /**
     * 回款查询
     *
     * @param minMoeny
     * @param maxMoney
     * @param minTime
     * @param maxTime
     * @param pageNum
     * @return
     */
    public boolean getQueryPage(String minMoeny, String maxMoney, String minTime, String maxTime, String pageNum) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_query_page;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        if (minMoeny != null) {
            params.put("minMoeny", minMoeny);// 筛选金额
        }
        if (maxMoney != null) {
            params.put("maxMoney", maxMoney);
        }
        if (minTime != null) {
            params.put("minTime", minTime);// 起始时间
        }
        if (maxTime != null) {
            params.put("maxTime", maxTime);
        }
        if (pageNum != null) {
            params.put("pageNum", pageNum);
        }

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public QueryPageInfo onGetQueryPage(Context context, JSONObject json) {
        if (json == null) {
            return null;
        }
        double a_money = json.optDouble("a_money");
        double oneMonth = json.optDouble("oneMonth");
        double threeMonth = json.optDouble("threeMonth");
        double sixMonth = json.optDouble("sixMonth");

        QueryPageInfo qpi = new QueryPageInfo();
        qpi.setA_money(a_money);
        qpi.setOneMonth(oneMonth);
        qpi.setThreeMonth(threeMonth);
        qpi.setSixMonth(sixMonth);

        ArrayList<QueryPage> list = new ArrayList<QueryPage>();

        JSONArray datalist = json.optJSONArray("datalist");
        if (datalist != null) {
            for (int i = 0; i < datalist.length(); i++) {
                JSONArray item = datalist.optJSONArray(i);
                if (item != null && item.length() > 5) {
                    QueryPage qp = new QueryPage();
                    qp.setQid(item.optString(0));
                    qp.setType(item.optString(1));
                    qp.setTime(item.optString(2));
                    String m = item.optString(3);
                    double mn = 0;
                    try {
                        mn = Double.parseDouble(m);
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    qp.setMoney(mn);
                    qp.setMoneyDetail(item.optString(4));
                    qp.setProgress(item.optString(5));
                    qp.setEid(item.optString(6));

                    list.add(qp);

                    //qp.setEid(eid);
                    //qp.setType(type);
//					for (int j = 0; j < item.length(); j++)
//					{
//						item.optString(j);
//					}
                }
//				String data = datalist.optString(i);
//				Log.e("TEST", "" + data);
            }

            DaoMaster.DevOpenHelper dbHelper;
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            QueryPageDao dao;

            dbHelper = new DaoMaster.DevOpenHelper(context, Config.DB_NAME, null);
            db = dbHelper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            dao = daoSession.getQueryPageDao();

            if (dao != null) {
                QueryBuilder<QueryPage> qb = dao.queryBuilder();
                qb.buildDelete().executeDeleteWithoutDetachingEntities();
                dao.insertInTx(list);
            }
        }

        qpi.setList(list);

        return qpi;
    }

    /**
     * 产品投资记录分页
     *
     * @param loanId
     * @param page
     * @param pageSize
     * @return
     */
    public boolean getProductInvestRecord(String loanId, int page, int pageSize, long serverTime) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getLoanBidList;
//		if (page == 0) {
//			cmdId = ServiceCmd.CmdId.CMD_getLoanBidListFirst;
//		}

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);// 标的号
        params.put("start", "" + (page * pageSize));//从start条记录开始（默认0）
        params.put("limit", "" + pageSize);//取limit条记录（默认5）
        params.put("serverTime", "" + serverTime);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 产品投资记录分页-->定存宝
     *
     * @param poolId
     * @param page
     * @param pageSize
     * @return
     */
    public boolean getProductInvestRecordForDeposit(String poolId, int page, int pageSize, long serverTime) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getLoanBidListForDeposit;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("poolId", poolId);// 标的号
        params.put("start", "" + (page * pageSize));//从start条记录开始（默认0）
        params.put("limit", "" + pageSize);//取limit条记录（默认5）
        params.put("serverTime", "" + serverTime);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public ArrayList<LoanRecord> onGetProductInvestRecord(JSONArray json) {

        JSONObject loanrecords = json.optJSONObject(0);
        JSONArray loan = loanrecords.optJSONArray("loanrecords");
        ArrayList<LoanRecord> loanList = new ArrayList<LoanRecord>();
        if (json != null) {
            for (int i = 0; i < loan.length(); i++) {
                JSONObject record = loan.optJSONObject(i);
                Long id = record.optLong("id");
                String username = record.optString("username");
                double rate = record.optDouble("rate");
                double tendMoney = record.optDouble("tendMoney");
                String payStatus = record.optString("payStatus");
                String paytime = record.optString("paytime");
                //isBook = 1  预约购买  isBook = 2 非预约购买
                int isBook = record.optInt("isBook");
                String voucherMoney = record.optString("voucherMoney");
                String type = record.optString("type");

                LoanRecord loanRecord = new LoanRecord();
                loanRecord.setId(id);
                loanRecord.setUsername(username);
                loanRecord.setRate(rate);
                loanRecord.setTendMoney(tendMoney);
                loanRecord.setPayStatus(payStatus);
                loanRecord.setPaytime(paytime);
                loanRecord.setIsBook(isBook);
                loanRecord.setVoucherMoney(voucherMoney);
                loanRecord.setType(type);

                loanList.add(loanRecord);
            }
        }
        return loanList;
    }


    /**
     * 购买定期产品 / 债权
     *
     * @param loanId
     * @return
     */
    public boolean plank(String loanId, String money, String user_id, String trans_password, boolean user_voucher, String voucherIds, String couponId, String isBookInvest) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_PLANK;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

/*
loansign_id	Long	true	标的号
money	Double	true	购买金额
user_id	Long	true	用户ID号
trans_password	String	true	Md5加密后传输
user_voucher	Int	fasle	是否使用代金券（复选框即可，使用逻辑服务器后台优选）0. 否1. 是(如果有)
isBookInvest    1   //为1时是预约购买
*/

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loansign_id", loanId);// 标的号
        params.put("money", money);// 购买金额
        params.put("user_id", user_id);// 用户ID号
        params.put("trans_password", trans_password);
        if (voucherIds == null) {
            voucherIds = "";
        }
        user_voucher = !TextUtils.isEmpty(voucherIds);
        params.put("voucherId", user_voucher ? "1" : "0");//
        params.put("vouchers", voucherIds);//
        params.put("couponId", couponId);
        params.put("isBookInvest", isBookInvest);


        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 重载投资方法  投资送iphone时使用
     *
     * @param loanId
     * @param money
     * @param user_id
     * @param trans_password
     * @param user_voucher
     * @param voucherIds
     * @param couponId
     * @param isBookInvest
     * @param getPhone
     * @param iphoneColor
     * @return
     */
    public boolean plank(String loanId, String money, String user_id, String trans_password, boolean user_voucher, String voucherIds, String couponId, String isBookInvest, String getPhone, String iphoneColor) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_PLANK;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

/*
loansign_id	Long	true	标的号
money	Double	true	购买金额
user_id	Long	true	用户ID号
trans_password	String	true	Md5加密后传输
user_voucher	Int	fasle	是否使用代金券（复选框即可，使用逻辑服务器后台优选）0. 否1. 是(如果有)
isBookInvest    1   //为1时是预约购买
getIphone  投资送iphone活动
*/

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loansign_id", loanId);// 标的号
        params.put("money", money);// 购买金额
        params.put("user_id", user_id);// 用户ID号
        params.put("trans_password", trans_password);
        if (voucherIds == null) {
            voucherIds = "";
        }
        user_voucher = !TextUtils.isEmpty(voucherIds);
        params.put("voucherId", user_voucher ? "1" : "0");//
        params.put("vouchers", voucherIds);//
        params.put("couponId", couponId);
        params.put("isBookInvest", isBookInvest);
        params.put("getPhone", getPhone);
        params.put("iphoneColor", iphoneColor);


        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public BuyResult onPlankFinish(JSONObject json, boolean isDeposit) {
        BuyResult result = null;
/*
scale	double	该标当前募集进度
maxCopies	int	最大可购买份数
buyCount	int	成功购买人数
status	int	状态
bidEndTime	Long	招标结束时间	长整形数字，需转换为时间
money	double	帐户余额
maxMoney	double	剩余可投金额
reMoney	double	标的剩余待募集金额
loanunit	int	最小出借单位	（投资金额须为些参数的1或N倍）
 */
        if (json != null) {
            result = new BuyResult();


            int status = json.optInt("status");
            double scale = json.optDouble("scale");
            int maxCopies = json.optInt("maxCopies");
            int buyCount = json.optInt("buyCount");
            long bidEndTime = json.optLong("bidEndTime");
            double money = json.optDouble("money");
            double maxMoney = json.optDouble("maxMoney");
            double reMoney = json.optDouble("reMoney");
            int loanunit = json.optInt("loanunit");
            String redpacketstrurl = json.optString("redpacketstrurl");
            String sumPacketNum = json.optString("sumPacketNum");
            String doubleFestival = json.optString("doubleFestival");//中秋国庆 双节活动
            String cashTime = json.optString("cashtime");//中秋国庆 双节活动
            String imageUrl = json.optString("imageUrl");//活动图片
            String activityUrl = json.optString("activityUrl");//活动H5地址

            String statusDesc = "";
            if (isDeposit) {
                switch (status) {
                    case 0:
                        statusDesc = "你的帐户余额小于投资金额,请充值";
                        break;
                    case 1:
                        statusDesc = "投标成功";
                        break;
                    case 2:
                        statusDesc = "该标的可投金额小于您投资金额";
                        break;
                    case 3:
                        statusDesc = "用户不存在";
                        break;
                    case 4:
                        statusDesc = "交易密码不正确";
                        break;
                    case 5:
                        statusDesc = "该标的目前不在募集期内";
                        break;
                    case 6:
                        statusDesc = "系统错误";
                        break;
                    case 9:
                        statusDesc = "不能预约购买标,预约金额已经超限制或者预售时间已经截止";
                        break;
                }
            } else {
                switch (status) {
                    case 0:
                        statusDesc = "你的帐户余额小于投资金额";
                        break;
                    case 1:
                    case 10:
                        statusDesc = "投标成功";
                        break;
                    case 2:
                        statusDesc = "金额己超出最大购买份数";
                        break;
                    case 3:
                        statusDesc = "不存在些用户";
                        break;
                    case 4:
                        statusDesc = "交易密码错误";
                        break;
                    case 5:
                        statusDesc = "内部错误";
                        break;
                    case 6:
                        statusDesc = "不能购买自己的标";
                        break;
                    case 7:
                        statusDesc = "用户未登录";
                        break;
                    case 9:
                        statusDesc = "不能预约购买的标，预约金额已经超限制或者预售时间已经截止";
                        break;
                    default:
                        break;
                }
            }

            result.setStatus(status);
            result.setScale(scale);
            result.setMaxCopies(maxCopies);
            result.setBuyCount(buyCount);
            result.setBidEndTime(bidEndTime);
            result.setMoney(money);
            result.setMaxMoney(maxMoney);
            result.setReMoney(reMoney);
            result.setLoanunit(loanunit);
            result.setStatusDesc(statusDesc);
            result.setRedpacketstrurl(redpacketstrurl);
            result.setRedPacketCount(sumPacketNum);
            result.setDoubleFestival(doubleFestival);
            result.setCashTime(cashTime);
            result.setImageUrl(imageUrl);
            result.setActivityUrl(activityUrl);
        }

        return result;
    }

    /**
     * 定期产品详情
     * 查看原标是传参不一样
     *
     * @param loanId
     * @return
     */
    public boolean getFixProduct(String loanId, String nativeId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_loanSignInfo;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);//原标id
        if (!"0".equals(nativeId)) {
            params.put("deptLoanId", nativeId);//转让标id
        }
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 用户投标是否使用过代金卷
     *
     * @param loanId
     * @param userid
     * @return
     */
    public boolean loanVoucherIsUse(String loanId, String userid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_loanVoucherIsUse;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loansignid", loanId);//标的号
        params.put("userid", userid);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 获取招标的项目介绍及详情风控情况
     *
     * @param loanId
     * @return
     */
    public boolean getLoanSignDesc(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_LOAN_SING_DESC;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);//标的号

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public ArrayList<ProjectIntroduction> onGetLoanSignDesc(JSONObject jsonObject) {
        ArrayList<ProjectIntroduction> list = new ArrayList<ProjectIntroduction>();
        JSONArray jsonArray = jsonObject.optJSONArray("descs");
        if (null != jsonArray) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject itemObject = jsonArray.optJSONObject(i);
                ProjectIntroduction item = new ProjectIntroduction();
                item.setBorrowbaseId(itemObject.optInt("borrowbaseId"));
                item.setContent(itemObject.optString("content"));
                item.setId(itemObject.optLong("id"));
                item.setLoansignId(itemObject.optLong("loansignId"));
                item.setName(itemObject.optString("name"));
                item.setSort(itemObject.optInt("sort"));
                item.setCType(itemObject.optInt("ctype"));
                list.add(item);
            }
        }
        return list;
    }

    public static final int LOAN_SIGN_DESC_TYPE_1 = 1;// 产品详情
    public static final int LOAN_SIGN_DESC_TYPE_2 = 2;// 项目介绍
    public static final int LOAN_SIGN_DESC_TYPE_3 = 3;// 风险控制

    public ArrayList<ProjectIntroduction> onGetLoanSignDescProjectIntroduction(JSONObject jsonObject) {
        ArrayList<ProjectIntroduction> list = new ArrayList<ProjectIntroduction>();
        JSONArray jsonArray = jsonObject.optJSONArray("descs");
        if (null != jsonArray) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject itemObject = jsonArray.optJSONObject(i);
                int ctype = itemObject.optInt("ctype");
                if (LOAN_SIGN_DESC_TYPE_2 == ctype) {
                    ProjectIntroduction item = new ProjectIntroduction();
                    item.setBorrowbaseId(itemObject.optInt("borrowbaseId"));
                    item.setContent(itemObject.optString("content"));
                    item.setId(itemObject.optLong("id"));
                    item.setLoansignId(itemObject.optLong("loansignId"));
                    item.setName(itemObject.optString("name"));
                    item.setSort(itemObject.optInt("sort"));
                    item.setCType(ctype);
                    list.add(item);
                }
            }
        }
        return list;
    }

    public ArrayList<ProjectIntroduction> onGetLoanSignDescRiskControl(JSONObject jsonObject) {
        ArrayList<ProjectIntroduction> list = new ArrayList<ProjectIntroduction>();
        JSONArray jsonArray = jsonObject.optJSONArray("descs");
        if (null != jsonArray) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject itemObject = jsonArray.optJSONObject(i);
                int ctype = itemObject.optInt("ctype");
                if (LOAN_SIGN_DESC_TYPE_3 == ctype) {
                    ProjectIntroduction item = new ProjectIntroduction();
                    item.setBorrowbaseId(itemObject.optInt("borrowbaseId"));
                    item.setContent(itemObject.optString("content"));
                    item.setId(itemObject.optLong("id"));
                    item.setLoansignId(itemObject.optLong("loansignId"));
                    item.setName(itemObject.optString("name"));
                    item.setSort(itemObject.optInt("sort"));
                    item.setCType(ctype);
                    list.add(item);
                }
            }
        }
        return list;
    }

    /**
     * 获取招标方资质材料
     *
     * @param loanId
     * @return
     */
    public boolean getLoanSignAttr(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_LOAN_SING_ATTR;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);//标的号

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public ArrayList<QualificationMaterial> onGetLoanSignAttr(JSONObject jsonObject) {
        ArrayList<QualificationMaterial> list = new ArrayList<QualificationMaterial>();
        JSONArray jsonArray = jsonObject.optJSONArray("attrs");
        if (null != jsonArray) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject itemObject = jsonArray.optJSONObject(i);
                QualificationMaterial item = new QualificationMaterial();
                item.setAddTime(itemObject.optString("addTime"));
                item.setFileName(itemObject.optString("fileName"));
                item.setFilePath(itemObject.optString("filePath"));
                item.setFileRemark(itemObject.optString("fileRemark"));
                item.setFileType(itemObject.optInt("fileType"));
                item.setId(itemObject.optLong("id"));
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 保理资料接口
     *
     * @param loanId
     * @return
     */
    public boolean getBaoliLoansignDesc(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_baoliLoansignDesc;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);//标的号

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public Baoli onGetBaoli(JSONObject json) {
/**
 {
 "baoliDescs": [{
 "borrowbaseId": 185,
 "content": "",
 "ctype": 5,
 "id": 310,
 "loansignId": 9,
 "name": "保理业务流程",
 "sort": 2,
 "files": [{
 "borrowersAdditionalId": 310,
 "fileName": "保理业务流程",
 "filePath": "http://www.vpfinance.cn/upload/borrowersadditionalfiles/u30.png",
 "fileRemark": "",
 "fileType": "",
 "id": 42
 }]
 }]
 }
 */
        List<BaoliItem> list = null;
        JSONArray baoliDescs = json.optJSONArray("baoliDescs");
        if (baoliDescs != null) {
            list = new ArrayList<>();
            for (int i = 0; i < baoliDescs.length(); i++) {
                JSONObject js = baoliDescs.optJSONObject(i);
                int id = js.optInt("id");
                int sort = js.optInt("sort");
                int loansignId = js.optInt("loansignId");
                int borrowbaseId = js.optInt("borrowbaseId");

                String content = js.optString("content");
                String name = js.optString("name");
                int ctype = js.optInt("ctype");

                JSONArray files = js.optJSONArray("files");
                List<BaoliItem.File> fList = null;
                if (files != null) {
                    fList = new ArrayList<>();
                    for (int j = 0; j < files.length(); j++) {
                        JSONObject file = files.optJSONObject(j);
                        int fid = file.optInt("id");
                        int borrowersAdditionalId = file.optInt("borrowersAdditionalId");

                        String fileName = file.optString("fileName");
                        String filePath = file.optString("filePath");
                        String fileRemark = file.optString("fileRemark");

                        BaoliItem.File bf = new BaoliItem.File();
                        bf.setId(fid);
                        bf.setBorrowersAdditionalId(borrowersAdditionalId);
                        bf.setFileName(fileName);
                        bf.setFilePath(filePath);
                        bf.setFileRemark(fileRemark);
                        fList.add(bf);
                    }
                }

                BaoliItem item = new BaoliItem();
                item.setId(id);
                item.setSort(sort);
                item.setLoansignId(loansignId);
                item.setBorrowbaseId(borrowbaseId);
                item.setContent(content);
                item.setName(name);
                item.setCtype(ctype);
                item.setFiles(fList);

                list.add(item);
            }
        }

        JSONArray carInfo = json.optJSONArray("carInfo");
        ArrayList<CarInfo> cars = new ArrayList<CarInfo>();
        if (carInfo != null) {
            for (int i = 0; i < carInfo.length(); i++) {
                CarInfo car = new CarInfo();
                JSONObject js = carInfo.optJSONObject(i);
                JSONObject vehicleBrand = js.optJSONObject("vehicleBrand");
                if (vehicleBrand != null) {
                    String name_zh = vehicleBrand.optString("name_zh");
                    String name_en = vehicleBrand.optString("name_en");
                    car.order = i + 1;
                    car.name_zh = name_zh;
                    car.name_en = name_en;
                }
                car.carProducer = js.optString("carProducer");
                car.approvalNo = js.optString("approvalNo");
                car.vehicle_model = js.optString("vehicle_model");
                car.productDate = js.optString("productDate");
                car.value = js.optDouble("value");
                cars.add(car);
            }
        }
        Baoli baoli = new Baoli();
        baoli.files = list;
        baoli.cars = cars;
        return baoli;
    }

    /**
     * 智存
     *
     * @param poolNum
     * @return
     */
    public boolean getAutomaticProductDetail(String poolNum) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_AUTOMATIC;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("poolNum", poolNum);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public FinanceProduct onGetAutomaticProductDetail(JSONObject json) {
        FinanceProduct financeProduct = new FinanceProduct();
	    /*
	     * scale   Double  该标当前募集进度
            status  Integer 状态
            Pool
            amount  Integer 总募集金额
            id  Long    ID
            month   Integer 锁定期
            openAmount  Double  剩余可购金额
            poolNum Integer 资金池
            soldOut Integer 0 没卖完，1 已卖完
            status  Integer 0 今日 ，1 非今日
            yearInterest Double  约定年利率
            money   Double  最多还能购买多少定存宝
            maxMoney    Double  帐户余额
	     */
        financeProduct.setScale(json.optDouble("scale"));
        financeProduct.setStatus(json.optInt("status"));
        JSONObject pool = json.optJSONObject("pool");
        financeProduct.setAmount(pool.optInt("amount"));
        financeProduct.setId(pool.optLong("id"));
        financeProduct.setMonth(pool.optInt("month"));
        financeProduct.setOpenAmount(pool.optDouble("openAmount"));
        financeProduct.setPoolNum(pool.optInt("poolNum"));
        financeProduct.setSoldOut(pool.optInt("soldOut"));
        financeProduct.setStatus(pool.optInt("status"));
        financeProduct.setYearInterest(pool.optDouble("yearInterest"));
        financeProduct.setMoney(json.optDouble("money"));
        financeProduct.setMaxMoney(json.optDouble("maxMoney"));

        return financeProduct;
    }

    /**
     * 智存
     *
     * @param poolNum      债权池
     * @param money        购买金额
     * @param userId       用户ID号
     * @param password     Md5加密后传输
     * @param isUseVoucher 是否使用代金券
     * @param poolId       债权池ID
     * @return
     */
    public boolean buyTransfer(long poolNum, double money, String userId, String password, boolean isUseVoucher, long poolId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_BUYTRANSFER;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("poolNum", "" + poolNum);
        params.put("money", "" + money);
        params.put("userId", userId);
        params.put("trans_password", password);
        params.put("user_voucher", (isUseVoucher == true ? "1" : "0"));// 0. 否 1. 是(如果有)
        params.put("poolId", "" + poolId);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public ArrayList<LoanRecord> onGetProductDetail(Context context, JSONObject json, FinanceProduct p) {
        if (p == null) {
            p = new FinanceProduct();
        }

        String imageUrl = json.optString("imageUrl");
        p.setImageUrl(imageUrl);
        String givePhone = json.optString("givePhone");
        p.setGivePhone(givePhone);
        String buyCount = json.optString("buyCount");
        p.setBuyCount(TextUtils.isEmpty(buyCount) ? "" : buyCount);
        long bidEndTime = json.optLong("bidEndTime");
        String lastTenderTime = json.optString("lastTenderTime");//投标结束时间
        p.setLastTenderTime(TextUtils.isEmpty(lastTenderTime) ? "" : lastTenderTime);

        JSONArray repays = json.optJSONArray("repays");//还款计划
        if (repays != null && repays.length() != 0) {
            String repaysStr = "";
            for (int i = 0; i < repays.length(); i++) {
                JSONObject item = repays.optJSONObject(i);
                String periods = item.optString("periods");
                String repayStatus = item.optString("repayStatus");
                String repaytime = item.optString("repaytime");
                repaysStr += periods + "," + repayStatus + "," + repaytime + "|";
            }
            p.setRepays(TextUtils.isEmpty(repaysStr) ? "" : repaysStr);
        }
        //债权和普通标都有（满标后）
        double totalPeriod = json.optDouble("totalPeriod");
        p.setTotalPeriod(totalPeriod == 0 ? 0 : (int) totalPeriod);

        String bookCouponNumber = json.optString("bookCouponNumber", "");
        if (p != null) {
            p.setBidEndTime(bidEndTime);
            p.setBookCouponNumber(bookCouponNumber);
        }
        JSONObject loansign = json.optJSONObject("loansign");
        String allowTransfer = TextUtils.isEmpty(json.optString("allowTransfer")) ? "" : json.optString("allowTransfer");
        int isAllowTrip = TextUtils.isEmpty(loansign.optInt("isAllowTrip") + "") ? 0 : loansign.optInt("isAllowTrip");
        double bookPercent = loansign.optDouble("bookPercent", 0.5);//默认0.5
        double promitRate = json.optDouble("promitRate");

        p.setAllowTransfer(allowTransfer);
        p.setIsAllowTrip(isAllowTrip);
        p.setPromitRate(promitRate);
        p.setBookPercent(bookPercent);

        String safeway = TextUtils.isEmpty(json.optString("safeway")) ? "" : json.optString("safeway");
        p.setSafeway(safeway);
        String borrowing = TextUtils.isEmpty(json.optString("borrowing")) ? "" : json.optString("borrowing");
        p.setBorrowing(borrowing);

        double total_tend_money = json.optDouble("total_tend_money");

		/*
LoanSign
id	long	标id
rate	double	借款利率
issueloan	double	借款金额
loantype	int	标类型
loanstate	int	借款标状态
refundway	int	还款方式
loanunit	double	最小出借单位
month	int	借款周期
publishtime	String	发布时间
subtype	int	标种子类型	1富担标，2担保，3抵押，4信用，5实地
counterparts	int	普通用户可购买份数
vipcounterparts	int	vip可购买份数
product	int	产品类别	0、散标（含债权转让标），1、定存标
status	int	定存标处理标记	散标和债权列表 请忽略
		 */
        if (loansign != null) {

            long id = loansign.optLong("id");
            double rate = loansign.optDouble("rate");
            double issueLoan = loansign.optDouble("issueLoan");//借款金额
            int loanType = loansign.optInt("loanType");//标类型
            //债权转让字段添加
            if (loanType == 6) {
                String debtUser = json.optString("debtUser");
                p.setDebtUser(TextUtils.isEmpty(debtUser) ? "" : debtUser);
                JSONArray noRepayList = json.optJSONArray("noRepayList");
                String noRepayListStr = "";
                for (int i = 0; i < noRepayList.length(); i++) {
                    JSONObject item = noRepayList.optJSONObject(i);
                    String periods = item.optString("periods");
                    String repayStatus = item.optString("repayStatus");
                    String repaytime = item.optString("repaytime");
                    noRepayListStr += periods + "," + repayStatus + "," + repaytime + "|";
                }
                p.setNoRepayList(TextUtils.isEmpty(noRepayListStr) ? "" : noRepayListStr);
                int originLoanId = json.optInt("originLoanId");
                p.setOriginLoanId(originLoanId == 0 ? -1 : originLoanId);
                String repaytime = json.optString("repaytime");
                p.setRepaytime(TextUtils.isEmpty(repaytime) ? "" : repaytime);
                int periods = json.optInt("periods");
                p.setPeriods(periods == 0 ? -1 : periods);
                String repayStatus = json.optString("repayStatus");
                p.setRepayStatus(TextUtils.isEmpty(repayStatus) ? "" : repayStatus);
                double originIssueLoan = json.optDouble("originIssueLoan");
                p.setOriginIssueLoan(originIssueLoan == 0.0 ? 0.0 : originIssueLoan);
            }

            int loanstate = loansign.optInt("loanstate");//借款标状态
            int refundWay = loansign.optInt("refundWay");//还款方式
            double loanUnit = loansign.optInt("loanUnit");//最小出借单位
            int month = loansign.optInt("month");//借款周期

            int subType = loansign.optInt("subType");//标种子类型	1富担标，2担保，3抵押，4信用，5实地
            int counterparts = loansign.optInt("counterparts");//普通用户可购买份数
            int vipCounterparts = loansign.optInt("vipCounterparts");//VIP可购买份数
            int product = loansign.optInt("product");//产品类别	0、散标（含债权转让标），1、定存标   2保理
            int status = loansign.optInt("status");//定存标处理标记	散标和债权列表 请忽略

            String productType = loansign.optString("productType");
            p.setProductType(TextUtils.isEmpty(productType) ? "" : productType);

            p.setPid(id);
            p.setRate(rate);
            p.setIssueLoan(issueLoan);
            p.setLoanType(loanType);
            if (loanstate == 1) {
                String publishTime = TextUtils.isEmpty(json.optString("publishTime")) ? "" : json.optString("publishTime");//预售发布时间，json外面那个
                p.setPublishTime(publishTime);
            }
            p.setLoanstate(loanstate);
            p.setRefundWay(refundWay);
            p.setLoanUnit(loanUnit);
            p.setMonth(month);
            p.setSubType(subType);
//			p.setCounterparts(counterparts);
//			p.setVipCounterparts(vipCounterparts);
            p.setProduct(product);
            p.setStatus(status);
            p.setTotal_tend_money(total_tend_money);


            JSONObject loansignTypeId = loansign.optJSONObject("loansignTypeId");
        }


        JSONObject loansignbasic = json.optJSONObject("loansignbasic");
		/*
LoanSignbasic
assure       	String	担保方名称
behoof       	String	借款方借款用途
bidTime      	String	招标期限
creditTime   	String	放款时间
guaranteesAmt	double	借款保证金
loanCategory 	int	借款标类型
loanExplain  	String	借款说明
loanNumber   	String	借款标号
loanOrigin   	String	借款方还款来源
loanSignTime 	String	创建时间
loanTitle    	String	标题
mgtMoney     	double	借款管理费
mgtMoneyScale	double	借款管理费比例
overview     	String	借款方商业概述
riskAssess   	int	风险评估	1低、2中、3高
riskCtrlWay  	String	风险控制措施	1.信用 2.担保
speech       	String	借款人感言
views        	int	浏览数
		 */
        if (loansignbasic != null) {
            String assure = loansignbasic.optString("assure");//担保方名称
            String behoof = loansignbasic.optString("behoof");//借款方借款用途
            String borrower = loansignbasic.optString("borrower");//

            String riskGrade = "";
            switch (loansignbasic.optInt("riskAssess")) {
                case 1:
                    riskGrade = "低";
                    break;
                case 2:
                    riskGrade = "中";
                    break;
                case 3:
                    riskGrade = "高";
                    break;
            }

            String reward = loansignbasic.optString("reward");//
            String bidTime = loansignbasic.optString("bidTime");//招标期限
            String creditTime = loansignbasic.optString("bidTime");//放款时间
            double guaranteesAmt = loansignbasic.optDouble("guaranteesAmt");//借款保证金
            int loanCategory = loansignbasic.optInt("loanCategory");//标类型
            String loanExplain = loansignbasic.optString("loanExplain");//借款说明
            String loanNumber = loansignbasic.optString("loanNumber");//借款标号
            String loanOrigin = loansignbasic.optString("loanOrigin");//借款方还款来源
            String loanSignTime = loansignbasic.optString("loanSignTime");//创建时间
            String loanTitle = loansignbasic.optString("loanTitle");//标题
            double mgtMoney = loansignbasic.optDouble("mgtMoney");//借款管理费
            double mgtMoneyScale = loansignbasic.optDouble("mgtMoneyScale");//借款管理费比例
            String overview = loansignbasic.optString("overview");//借款方商业概述
            int riskAssess = loansignbasic.optInt("riskAssess");//风险评估	1低、2中、3高
            String riskCtrlWay = loansignbasic.optString("riskCtrlWay");//风险控制措施	1.信用 2.担保
            String speech = loansignbasic.optString("speech");//借款人感言
            int views = loansignbasic.optInt("views");//浏览数
            String creditTime2 = loansignbasic.optString("creditTime");//起息日

            p.setReward(reward);
            p.setAssure(assure);
            p.setBehoof(behoof);
            p.setBorrower(borrower);
            p.setRiskGrade(riskGrade);
            p.setBidTime(bidTime);
            p.setCreditTime(creditTime);
            p.setGuaranteesAmt(guaranteesAmt);
            p.setLoanCategory(loanCategory);
            p.setLoanExplain(loanExplain);
            p.setLoanNumber(loanNumber);
            p.setLoanOrigin(loanOrigin);
            p.setLoanSignTime(loanSignTime);
            p.setLoanTitle(loanTitle);
            p.setMgtMoney(mgtMoney);
            p.setMgtMoneyScale(mgtMoneyScale);
            p.setOverview(overview);
            p.setRiskAssess(riskAssess);
            p.setRiskCtrlWay(riskCtrlWay);
//			p.setSpeech(speech);
//			p.setViews(views);
            p.setCreditTime2(creditTime2);

        }

        ArrayList<LoanRecord> loanList = new ArrayList<LoanRecord>();
        JSONArray loanrecords = json.optJSONArray("loanrecords");
        if (loanrecords != null) {
			/*
username	String	投资人姓名
rate	double	标的年化利率
tendMoney	double	投资金额
payStatus	String	支付状态
paytime	String	支付时间
			 */
            for (int i = 0; i < loanrecords.length(); i++) {
                JSONObject record = loanrecords.optJSONObject(i);
                String username = record.optString("username");
                double rate = record.optDouble("rate");
                double tendMoney = record.optDouble("tendMoney");
                String payStatus = record.optString("payStatus");
                String paytime = record.optString("paytime");

                LoanRecord loanRecord = new LoanRecord();
                loanRecord.setPid(p.getPid());
                loanRecord.setUsername(username);
                loanRecord.setRate(rate);
                loanRecord.setTendMoney(tendMoney);
                loanRecord.setPayStatus(payStatus);
                loanRecord.setPaytime(paytime);

                loanList.add(loanRecord);
            }

        }

        JSONObject borrowerinfo = json.optJSONObject("borrowerinfo");
        if (borrowerinfo != null) {
/*
age	Int	年龄
credit	Double	授信额度
id	int	借款人ID
income	int	月收入
isCard	String	身份证号
marryStatus	int	婚姻状态
money	Double	申请的借款金额
phone	String	电话
qualifications	String	最高学历
realName	String	真实姓名
remark	String	个人描述
sex	int	性别	[0:女,1:男]
 */
            BorrowerInfo borrower = new BorrowerInfo();
            long id = borrowerinfo.optInt("id");
            //sex
            //phone
            //marryStatus
            //qualifications
            String remark = borrowerinfo.optString("remark");
            //isCard
            //income
            int age = borrowerinfo.optInt("age");
            //money
            String realName = borrowerinfo.optString("realName");
            double credit = borrowerinfo.optDouble("credit");

            borrower.setPid(p.getPid());

            borrower.setBid(id);
            borrower.setAge(age);
            borrower.setCredit(credit);
            borrower.setRealName(realName);
            borrower.setRemark(remark);

            p.setBorrowerId(id);
            p.setAge(age);
            p.setCredit(credit);
            p.setRealName(realName);
            p.setRemark(remark);

        }

        return loanList;
    }

    /**
     * 获取债权转让标列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    public boolean getBondProductList(int page, int pageSize) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_BOND_PRODUCT_LIST;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("type", "6");// 1.定期  2.债权转让  默认为1
        params.put("start", "" + (page * pageSize));//从start条记录开始（默认0）
        params.put("limit", "" + pageSize);//取limit条记录（默认5）

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 智存列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    public boolean getAutomaticProductList(int page, int pageSize) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getAutomaticProductList;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public boolean getRegularProductList(int page, int pageSize) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getBidList;
//		if(page==0)
//			cmdId = ServiceCmd.CmdId.CMD_getBidListFirst;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("type", "1");// 1.定期  2.债权转让  默认为1
        params.put("start", "" + (page * pageSize));//从start条记录开始（默认0）
        params.put("limit", "" + pageSize);//取limit条记录（默认5）

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public ArrayList<FinanceProduct> onGetAutomaticProductList(JSONArray json) {
        ArrayList<FinanceProduct> pList = null;

        if (json == null) {
            Log.e("Test", "json is null");
            return null;
        }

        pList = new ArrayList<FinanceProduct>();

        for (int i = 0; json != null && i < json.length(); i++) {
            JSONObject js = json.optJSONObject(i);
            FinanceProduct fp = new FinanceProduct();
            long id = js.optLong("id");
            double amount = js.optDouble("amount");//总募集额
            double openAmount = js.optDouble("openAmount");//可购余额
            double yearInterest = js.optDouble("yearInterest");//展现给用户的年化收益率
            int month = js.optInt("month");//锁定期
            int poolNum = js.optInt("poolNum");
            int soldOut = js.optInt("soldOut");
            int status = js.optInt("status");

            fp.setPid(id);
            fp.setAmount(amount);
            fp.setOpenAmount(openAmount);
            fp.setYearInterest(yearInterest);
            fp.setMonth(month);

            fp.setPoolNum(poolNum);
            fp.setSoldOut(soldOut);
            fp.setStatus(status);
            pList.add(fp);
        }
        return pList;
    }

    public ArrayList<FinanceProduct> onGetBondProductList(Context context, JSONObject json) {

        ArrayList<FinanceProduct> pList = null;
        int total = json.optInt("total");
        JSONArray loansigns = json.optJSONArray("loansigns");
        if (loansigns != null && loansigns.length() > 0) {
            pList = new ArrayList<FinanceProduct>();
            for (int i = 0; i < loansigns.length(); i++) {
                JSONObject item = loansigns.optJSONObject(i);

                FinanceProduct p = new FinanceProduct();
                p.setType(2);
                p.setTotalPage(total);


                JSONObject loansignflow = item.optJSONObject("loansignflow");
                if (loansignflow != null) {
					/*
1.当distype为1时，实际债权每份的金额应该为 （1-appreciation) * amount
2.当distype为2时，实际债权每份的金额应为  （1+appreciation) * amount
3.当distype为3时，可不关注此字段
					 */
                    double appreciation = loansignflow.optDouble("appreciation");
                    //auditResult
                    //auditStatus
                    double discountMoney = loansignflow.optDouble("discountMoney");
                    int distype = loansignflow.optInt("distype");
                    //flowstate
                    //id
                    //loanCount
                    //loanId
                    //loansignId
                    //pcrettype
                    int share = loansignflow.optInt("share");
                    //shiftTime
                    double tenderMoney = loansignflow.optDouble("tenderMoney");
                    long userAuth = loansignflow.optLong("userAuth");
                    long userDebt = loansignflow.optLong("userDebt");

                    p.setAppreciation(appreciation);
                    p.setDiscountMoney(discountMoney);
                    p.setDistype(distype);
                    p.setShare(share);
                    p.setTenderMoney(tenderMoney);
                    p.setUserAuth(userAuth);
                    p.setUserDebt(userDebt);
                }

                double total_tend_money = item.optDouble("total_tend_money");
                p.setTotal_tend_money(total_tend_money);

                JSONObject loansign = item.optJSONObject("loansign");
                if (loansign != null) {
                    long id = loansign.optLong("id");//id
                    double rate = loansign.optDouble("rate");//
                    double issueLoan = loansign.optInt("issueLoan");//借款金额
                    int loanType = loansign.optInt("loanType");//标类型
                    int loanstate = loansign.optInt("loanstate");//借款标状态
                    int refundWay = loansign.optInt("refundWay");//还款方式
                    double loanUnit = loansign.optInt("loanUnit");//最小出借单位
                    int month = loansign.optInt("month");//借款周期
                    String publishTime = loansign.optString("publishTime");//发布时间
                    int subType = loansign.optInt("subType");//标种子类型	1富担标，2担保，3抵押，4信用，5实地
                    int counterparts = loansign.optInt("counterparts");//普通用户可购买份数
                    int vipCounterparts = loansign.optInt("vipCounterparts");//VIP可购买份数
                    int product = loansign.optInt("product");//产品类别	0、散标（含债权转让标），1、定存标
                    int status = loansign.optInt("status");//定存标处理标记	散标和债权列表 请忽略

                    //realDay
                    //useDay

                    JSONObject loansignTypeId = loansign.optJSONObject("loansignTypeId");//

                    p.setPid(id);
                    p.setRate(rate);
                    p.setIssueLoan(issueLoan);
                    p.setLoanType(loanType);
                    p.setLoanstate(loanstate);
                    p.setRefundWay(refundWay);
                    p.setLoanUnit(loanUnit);
                    p.setMonth(month);
                    p.setPublishTime(publishTime);
                    p.setSubType(subType);
//					p.setCounterparts(counterparts);
//					p.setVipCounterparts(vipCounterparts);
                    p.setProduct(product);
                    p.setStatus(status);
                }

                JSONObject loansignbasic = item.optJSONObject("loansignbasic");
                if (loansignbasic != null) {
                    String assure = loansignbasic.optString("assure");//担保方名称
                    String behoof = loansignbasic.optString("behoof");//借款方借款用途
                    String bidTime = loansignbasic.optString("bidTime");//招标期限
                    String creditTime = loansignbasic.optString("creditTime");//放款时间
                    double guaranteesAmt = loansignbasic.optDouble("guaranteesAmt");//借款保证金
                    int loanCategory = loansignbasic.optInt("loanCategory");//标类型
                    String loanExplain = loansignbasic.optString("loanExplain");//借款说明
                    String loanNumber = loansignbasic.optString("loanNumber");//借款标号
                    String loanOrigin = loansignbasic.optString("loanOrigin");//借款方还款来源
                    String loanSignTime = loansignbasic.optString("loanSignTime");//创建时间
                    String loanTitle = loansignbasic.optString("loanTitle");//标题
                    double mgtMoney = loansignbasic.optDouble("mgtMoney");//借款管理费
                    double mgtMoneyScale = loansignbasic.optDouble("mgtMoneyScale");//借款管理费比例
                    String overview = loansignbasic.optString("overview");//借款方商业概述
                    int riskAssess = loansignbasic.optInt("riskAssess");//风险评估	1低、2中、3高
                    String riskCtrlWay = loansignbasic.optString("riskCtrlWay");//风险控制措施	1.信用 2.担保
                    String speech = loansignbasic.optString("speech");//借款人感言
                    int views = loansignbasic.optInt("views");//浏览数

                    p.setAssure(assure);
                    p.setBehoof(behoof);
                    p.setBidTime(bidTime);
                    p.setCreditTime(creditTime);
                    p.setGuaranteesAmt(guaranteesAmt);
                    p.setLoanCategory(loanCategory);
                    p.setLoanExplain(loanExplain);
                    p.setLoanNumber(loanNumber);
                    p.setLoanOrigin(loanOrigin);
                    p.setLoanSignTime(loanSignTime);
                    p.setLoanTitle(loanTitle);
                    p.setMgtMoney(mgtMoney);
                    p.setMgtMoneyScale(mgtMoneyScale);
                    p.setOverview(overview);
                    p.setRiskAssess(riskAssess);
                    p.setRiskCtrlWay(riskCtrlWay);
//					p.setSpeech(speech);
//					p.setViews(views);

                }

                pList.add(p);
            }

			/*DaoMaster.DevOpenHelper dbHelper;
			SQLiteDatabase db;
			DaoMaster daoMaster;
			DaoSession daoSession;
			FinanceProductDao dao;

			dbHelper = new DaoMaster.DevOpenHelper(context, Config.DB_NAME, null);
			db = dbHelper.getWritableDatabase();
			daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			dao = daoSession.getFinanceProductDao();

			if (dao != null) {
				QueryBuilder<FinanceProduct> qb = dao.queryBuilder();
				qb.where(FinanceProductDao.Properties.Type.eq(2));
				qb.buildDelete().executeDeleteWithoutDetachingEntities();
				dao.insertInTx(pList);
			}*/
        }
        return pList;
    }

    /**
     * 定期
     *
     * @param json
     * @return
     */
    public ArrayList<FinanceProduct> onGetRegularProductList(Context context, JSONObject json) {
        ArrayList<FinanceProduct> pList = null;
        if (isSessionTimeout(json)) {
            return null;
        }

        int total = json.optInt("total");
        JSONArray loansigns = json.optJSONArray("loansigns");
        if (loansigns != null && loansigns.length() > 0) {
            pList = new ArrayList<FinanceProduct>();
            for (int i = 0; i < loansigns.length(); i++) {
                JSONObject item = loansigns.optJSONObject(i);

                double total_tend_money = item.optDouble("total_tend_money");
                double promitRate = item.optDouble("promitRate");

                FinanceProduct p = new FinanceProduct();
                p.setType(1);
                p.setTotalPage(total);

                p.setTotal_tend_money(total_tend_money);
                p.setPromitRate(promitRate);

                String givePhone = item.optString("givePhone");
                p.setGivePhone(givePhone);

                JSONObject loansign = item.optJSONObject("loansign");

				/*
LoanSign
id	long	标id
rate	double	借款利率
issueloan	double	借款金额
loantype	int	标类型
loanstate	int	借款标状态
refundway	int	还款方式
loanunit	double	最小出借单位
month	int	借款周期
publishtime	String	发布时间
subtype	int	标种子类型	1富担标，2担保，3抵押，4信用，5实地
counterparts	int	普通用户可购买份数
vipcounterparts	int	vip可购买份数
product	int	产品类别	0、散标（含债权转让标），1、定存标
status	int	定存标处理标记	散标和债权列表 请忽略
isAllowTrip 旅游标示
				 */
                p.setPid(0);
                p.setRate(0);
                p.setIssueLoan(0);
                p.setLoanType(0);
                p.setLoanstate(0);
                p.setRefundWay(0);
                p.setLoanUnit(0);
                p.setMonth(0);
                p.setPublishTime("");
                p.setSubType(0);
//				p.setCounterparts(0);
//				p.setVipCounterparts(0);
                p.setProduct(0);
                p.setStatus(0);
                p.setIsAllowTrip(0);
                if (loansign != null) {
                    p.setLoanType(loansign.optLong("loanType"));//2就是天标

                    long id = loansign.optLong("id");
                    double rate = loansign.optDouble("rate");
                    double issueLoan = loansign.optDouble("issueLoan");//借款金额
                    int loanType = loansign.optInt("loanType");//标类型
                    int loanstate = loansign.optInt("loanstate");//借款标状态
                    int refundWay = loansign.optInt("refundWay");//还款方式
                    double loanUnit = loansign.optInt("loanUnit");//最小出借单位
                    int month = loansign.optInt("month");//借款周期
                    String publishTime = loansign.optString("publishTime");//发布时间
                    int subType = loansign.optInt("subType");//标种子类型	1富担标，2担保，3抵押，4信用，5实地
                    int counterparts = loansign.optInt("counterparts");//普通用户可购买份数
                    int vipCounterparts = loansign.optInt("vipCounterparts");//VIP可购买份数
                    int product = loansign.optInt("product");//产品类别	0、散标（含债权转让标），1、定存标
                    String productType = loansign.optString("productType");//3沈阳预售标  5净值标
                    int isAllowTrip = loansign.optInt("isAllowTrip");//旅游标识

                    if (!TextUtils.isEmpty(productType) && "3".equals(productType)) {
//						String allowTransfer = item.optString("allowTransfer");
                        String imageUrl = item.optString("imageUrl");
                        String bidEndTime = item.optString("bidEndTime");
                        String minRate = item.optString("minRate");
                        String maxRate = item.optString("maxRate");
                        p.setBidEndTime(0L);
//						p.setAllowTransfer(TextUtils.isEmpty(allowTransfer) ? "" : allowTransfer);
                        p.setMinRate(TextUtils.isEmpty(minRate) ? "" : minRate);
                        p.setMaxRate(TextUtils.isEmpty(maxRate) ? "" : maxRate);
                        p.setImageUrl(TextUtils.isEmpty(imageUrl) ? "" : imageUrl);
                        if (!TextUtils.isEmpty(bidEndTime)) {
                            try {
                                long endTime = Long.parseLong(bidEndTime);
                                p.setBidEndTime(endTime);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    String allowTransfer = item.optString("allowTransfer");
                    p.setAllowTransfer(TextUtils.isEmpty(allowTransfer) ? "" : allowTransfer);

                    int status = loansign.optInt("status");//定存标处理标记	散标和债权列表 请忽略

                    p.setPid(id);
                    p.setRate(rate);
                    p.setIssueLoan(issueLoan);
                    p.setLoanType(loanType);
                    if (loanstate == 1) {
                        String endTime = item.optString("publishTime");
                        p.setPublishTime(TextUtils.isEmpty(endTime) ? "" : endTime);
                    }

                    p.setLoanstate(loanstate);
                    p.setRefundWay(refundWay);
                    p.setLoanUnit(loanUnit);
                    p.setMonth(month);
//					p.setPublishTime(publishTime);
                    p.setSubType(subType);
//					p.setCounterparts(counterparts);
//					p.setVipCounterparts(vipCounterparts);
                    p.setProduct(product);
                    p.setProductType(productType);
                    p.setStatus(status);
                    p.setIsAllowTrip(isAllowTrip);

                    JSONObject loansignTypeId = loansign.optJSONObject("loansignTypeId");
                }

                JSONObject loansignbasic = item.optJSONObject("loansignbasic");
				/*
LoanSignbasic
assure       	String	担保方名称
behoof       	String	借款方借款用途
bidTime      	String	招标期限
creditTime   	String	放款时间
guaranteesAmt	double	借款保证金
loanCategory 	int	借款标类型
loanExplain  	String	借款说明
loanNumber   	String	借款标号
loanOrigin   	String	借款方还款来源
loanSignTime 	String	创建时间
loanTitle    	String	标题
mgtMoney     	double	借款管理费
mgtMoneyScale	double	借款管理费比例
overview     	String	借款方商业概述
riskAssess   	int	风险评估	1低、2中、3高
riskCtrlWay  	String	风险控制措施	1.信用 2.担保
speech       	String	借款人感言
views        	int	浏览数
				 */
                p.setAssure("");
                p.setBehoof("");
                p.setBidTime("");
                p.setCreditTime("");
                p.setGuaranteesAmt(1);
                p.setLoanCategory(0);
                p.setLoanExplain("");
                p.setLoanNumber("");
                p.setLoanOrigin("");
                p.setLoanSignTime("");
                p.setLoanTitle("");
                p.setMgtMoney(0);
                p.setMgtMoneyScale(0);
                p.setOverview("");
                p.setRiskAssess(0);
                p.setRiskCtrlWay("");
//				p.setSpeech("");
//				p.setViews(0);
                if (loansignbasic != null) {
                    String reward = loansignbasic.optString("reward");//
                    String assure = loansignbasic.optString("assure");//担保方名称
                    String behoof = loansignbasic.optString("behoof");//借款方借款用途
                    String bidTime = loansignbasic.optString("bidTime");//招标期限
                    String creditTime = loansignbasic.optString("creditTime");//放款时间
                    double guaranteesAmt = loansignbasic.optDouble("guaranteesAmt");//借款保证金
                    int loanCategory = loansignbasic.optInt("loanCategory");//标类型
                    String loanExplain = loansignbasic.optString("loanExplain");//借款说明
                    String loanNumber = loansignbasic.optString("loanNumber");//借款标号
                    String loanOrigin = loansignbasic.optString("loanOrigin");//借款方还款来源
                    String loanSignTime = loansignbasic.optString("loanSignTime");//创建时间
                    String loanTitle = loansignbasic.optString("loanTitle");//标题
                    double mgtMoney = loansignbasic.optDouble("mgtMoney");//借款管理费
                    double mgtMoneyScale = loansignbasic.optDouble("mgtMoneyScale");//借款管理费比例
                    String overview = loansignbasic.optString("overview");//借款方商业概述
                    int riskAssess = loansignbasic.optInt("riskAssess");//风险评估	1低、2中、3高
                    String riskCtrlWay = loansignbasic.optString("riskCtrlWay");//风险控制措施	1.信用 2.担保
                    String speech = loansignbasic.optString("speech");//借款人感言
                    int views = loansignbasic.optInt("views");//浏览数

                    p.setReward(reward);
                    p.setAssure(assure);
                    p.setBehoof(behoof);
                    p.setBidTime(bidTime);
                    p.setCreditTime(creditTime);
                    p.setGuaranteesAmt(guaranteesAmt);
                    p.setLoanCategory(loanCategory);
                    p.setLoanExplain(loanExplain);
                    p.setLoanNumber(loanNumber);
                    p.setLoanOrigin(loanOrigin);
                    p.setLoanSignTime(loanSignTime);
                    p.setLoanTitle(loanTitle);
                    p.setMgtMoney(mgtMoney);
                    p.setMgtMoneyScale(mgtMoneyScale);
                    p.setOverview(overview);
                    p.setRiskAssess(riskAssess);
                    p.setRiskCtrlWay(riskCtrlWay);
//					p.setSpeech(speech);
//					p.setViews(views);

                }

                pList.add(p);

            }

			/*DaoMaster.DevOpenHelper dbHelper;
			SQLiteDatabase db;
			DaoMaster daoMaster;
			DaoSession daoSession;
			FinanceProductDao dao;

			dbHelper = new DaoMaster.DevOpenHelper(context, Config.DB_NAME, null);
			db = dbHelper.getWritableDatabase();
			daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			dao = daoSession.getFinanceProductDao();

			if (dao != null) {
				QueryBuilder<FinanceProduct> qb = dao.queryBuilder();
				qb.where(FinanceProductDao.Properties.Type.eq(1));
				qb.buildDelete().executeDeleteWithoutDetachingEntities();
				dao.insertInTx(pList);
			}*/

        }

        return pList;
    }

    public boolean getBanner() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_BANNER;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("TYPE", "android");

        //httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public boolean querySessionStatus() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_querySessionStatus;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public boolean onQuerySessionStatus(JSONObject json) {

		/*
		 {  0 已失效
		 "ulstatus": "1"未失效
		 }
		 */
        return json != null && json.optInt("ulstatus") == 1;
    }

    public boolean getHomeInfo() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_HOME_INFO;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public HomeInfo onGetHomeInfo(JSONObject json) {
		/*
		 {
		 "annualRate": "9.0",
		 "borrowEndTime": "2015-09-30 21:21:30",
		 "borrowId": "161",
		 "borrowLoanMoney": "27500.0",
		 "borrowLoanPercent": "1.47",
		 "borrowStatus": "3",
		 "borrowTitle": "V孵化-南通-003",
		 "issueLoan": "1870000.0",
		 "month": "3",
		 "totalInterest": "1799190.0",
		 "totalMoney": "43474606.5"
		 }
		 */
        if (json == null) {
            return null;
        }

        HomeInfo info = new HomeInfo();
        String borrowId = json.optString("borrowId");
        String annualRate = json.optString("annualRate");//标的年化利率，如9.3
        //String reward = String.format("%.1f", json.optDouble("reward", 0));
        String reward = json.optString("reward");
        String borrowTitle = json.optString("borrowTitle");//标的标题
        int borrowStatus = json.optInt("borrowStatus");//借款标状态 1未发布、3进行中、5回款中、6已完成
        int month = json.optInt("month");//标的项目期限
        String issueLoan = json.optString("issueLoan");//标的项目总金额
        float borrowLoanPercent = (float) json.optDouble("borrowLoanPercent");
        long borrowEndTime = json.optLong("borrowEndTime");
        int product = json.optInt("product");

        info.setReward(reward);
        info.setBorrowId(borrowId);
        info.setAnnualRate(annualRate);
        info.setBorrowTitle(borrowTitle);
        info.setBorrowStatus(borrowStatus);
        info.setMonth(month);
        info.setIssueLoan(issueLoan);
        info.setProduct(product);
        info.setBorrowLoanPercent(borrowLoanPercent);
        info.setTotalInterest(String.format("%.2f", json.optDouble("totalInterest", 0)));
        info.setTotalMoney(String.format("%.2f", json.optDouble("totalMoney", 0)));
        info.setBorrowEndTime(borrowEndTime);

        return info;
    }

    public boolean checkAppVersion() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_APPUPDATE;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

//		params.put("TYPE", "android");
        params.put("type", "1");

        //httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public boolean getShareInfo() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_GET_SHARE_URL;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("TYPE", "android");

        //httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public boolean guidePage() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_GUIDE_PAGE;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public boolean logout() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_LOGOUT;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        params.put("TYPE", "android");

        //httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public String onLogout(JSONObject json) {
        String msg = "注销失败";
        try {
            int msgCode = json.optInt("msg", -1);
            switch (msgCode) {
                case 0:
                    msg = "注销失败";
                    break;
                case 1:
                    msg = "注销成功";//注销成功
                    AppState.instance().setSessionCode("");
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
        }

        return msg;
    }

    public String onGetVersion2(JSONObject jsonObject, AppUpdateInfo info) {
        if (info == null) {
            info = new AppUpdateInfo();
        }
        info.appVersion = jsonObject.optString("appVersion");
        info.downloadUrl = jsonObject.optString("downloadUrl");
        info.updateLog = jsonObject.optString("updateLog");
        return "获取成功";
    }

    public static NewAppUpdateInfo onGetVersion(JSONObject jsonObject) {

        NewAppUpdateInfo info = new NewAppUpdateInfo();
        info.updateLog = jsonObject.optString("updateLog");
        info.appVersion = jsonObject.optString("appVersion");
        info.downloadUrl = jsonObject.optString("downloadUrl");
        info.appStatus = jsonObject.optInt("appStatus");
        return info;
    }


	/*
	public boolean getHomeBanner()
	{
		ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getHomeBanner;
		String method = ServiceCmd.getMethodName(cmdId);
		String url = getServiceUrl(method);

		Map<String, String> params = new ArrayMap<String, String>();
		httpClient.setTips("提交中");
		return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
	}
	*/


    public ArrayList<BannerBean> onGetBanner(Context context, JSONObject json) {
		/*
id	int
imgurl	String     	图片地址
url	String     	链接URL
Picturename	String	说明文字
Type	Int	Banner类型	1. 链接  2.产品
		 */
        ArrayList<BannerBean> bList = null;

        JSONArray banner = json.optJSONArray("banner");
        if (banner != null) {
            bList = new ArrayList<BannerBean>();
            for (int i = 0; i < banner.length(); i++) {
                JSONObject ban = banner.optJSONObject(i);
                int id = ban.optInt("id");
                int type = ban.optInt("type");
                String url = ban.optString("url");
                String imgurl = ban.optString("imgurl");
                String picturename = ban.optString("picturename");

                BannerBean item = new BannerBean();
                item.id = id;
                item.type = (type);
                item.url = (url);
                item.imgurl = (imgurl);
                item.picturename = (picturename);
                bList.add(item);
            }

//			if (context != null) {
//				DaoMaster.DevOpenHelper dbHelper;
//				SQLiteDatabase db;
//				DaoMaster daoMaster;
//				DaoSession daoSession;
//				BannerDao dao;
//
//				dbHelper = new DaoMaster.DevOpenHelper(context, Config.DB_NAME, null);
//				db = dbHelper.getWritableDatabase();
//				daoMaster = new DaoMaster(db);
//				daoSession = daoMaster.newSession();
//				dao = daoSession.getBannerDao();
//
//				if (dao != null) {
//					QueryBuilder<Banner> qb = dao.queryBuilder();
//					qb.buildDelete().executeDeleteWithoutDetachingEntities();
//					dao.insertInTx(bList);
//				}
//			}

        }

        return bList;
    }

    @PostMethod(method = "/AppLiteMomey/activeLiteAccount")
    public boolean activeLiteAccount() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_activeLiteAccount;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onActiveLiteAccount(JSONObject jsonObject) {
        int success = jsonObject.optInt("success");
        String msg;
        if (success == 1) {
            msg = "激活成功";
        } else {
            msg = "激活失败";
        }
        return msg;
    }

    @PostMethod(method = "/AppLiteMomey/drawEarnings")
    public boolean drawEarnings() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_drawEarnings;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");
//		params.put("transCode",pwd);
//		params.put("code",vCode);

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onDrawEarnings(JSONObject jsonObject) {
        int success = jsonObject.optInt("success", 0);
        String msg;
        if (success == 1) {
            msg = "成功";
        } else if (success == 2) {
            msg = "交易密码错误";
        } else if (success == 3) {
            msg = "验证码过期";
        } else if (success == 4) {
            msg = "验证码错误";
        } else {
            msg = "体验金领取失败，请稍候再试！";
        }
        return msg;
    }

    @PostMethod(method = "/AppLiteMomey/drawEarnings")
    public boolean liteMoneyInfo(int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_liteMoneyInfo;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");
        params.put("accountType", accountType + "");

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onLiteMoneyInfo2(JSONObject jsonObject, TrialCoinActivity.BonusRespondModel respondModel) {

        respondModel.msg = jsonObject.optInt("msg");

        JSONObject bonusJson = jsonObject.optJSONObject("setting");
        String errMsg = "成功";

        if (bonusJson != null) {
            respondModel.bonusObj = new TrialBonus();
            respondModel.bonusObj.trialPeriod = bonusJson.optInt("elite_bonus_period");//体验周期
            respondModel.bonusObj.trialEnable = bonusJson.optInt("is_elite_open");//是否开启体验金机制
            respondModel.bonusObj.trialFee = bonusJson.optInt("elite_fee");//是否开启体验金机制
            respondModel.bonusObj.trialAmount = bonusJson.optDouble("elite_money");//体验金额
            respondModel.bonusObj.expiryPeriod = bonusJson.optInt("elite_expiry_periods");//体验金失效周期
        }

        JSONObject profitJson = jsonObject.optJSONObject("lite_money");//体验金
        if (profitJson != null) {
            respondModel.bonusProfit = new BonusProfit();
            respondModel.bonusProfit.id = profitJson.optInt("id");
            respondModel.bonusProfit.principal = profitJson.optDouble("principal");//本金
            respondModel.bonusProfit.totalEarning = profitJson.optDouble("total_earning");//收益
            respondModel.bonusProfit.provideTime = profitJson.optLong("provide_time");//发放时间
            respondModel.bonusProfit.activeTime = profitJson.optLong("active_time");//激活时间
            respondModel.bonusProfit.earningExpiryTime = profitJson.optLong("earning_failure_time");//收益失效时间
            respondModel.bonusProfit.principalExpiryTime = profitJson.optLong("principal_failure_time");//本金失效时间
            respondModel.bonusProfit.expiryFlag = profitJson.optInt("effect");//失效标记 1.未失效   0.己失效
            respondModel.bonusProfit.activeFlag = profitJson.optInt("active"); //激活标记 0否  1是
            respondModel.bonusProfit.status = profitJson.optInt("status", 1);//体验金状态  0未激活 1已过期 2产生收益中 3收益可领取 4收益已领取 5收益已失效
        }

        return errMsg;
    }


    public boolean liteJoinList() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_liteJoinList;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 获取代金券
     *
     * @param page
     * @return
     */
    public boolean getVoucherlist(String page, String loanid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_VOUCHERLIST_V2;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("pageNum", page);

        if (!TextUtils.isEmpty(loanid) && !"-1".equals(loanid) && !"0".equals(loanid)) {
            params.put("loanid", loanid);
        }

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public SelectVoucherBean onGetVoucherlist(String json) {
        SelectVoucherBean info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, SelectVoucherBean.class);

        }
        return info;
    }

    /**
     * 获取代金券TAB
     *
     * @param
     * @return
     */
    public boolean getVoucherlistTab(String pageNum, String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_VOUCHERLIST_TAB;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("pageNum", pageNum);
        params.put("type", type);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 获取加息券TAB
     *
     * @param
     * @return
     */
    public boolean getAddrateTicketlistTab(String pageNum, String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Addrate_Ticket_Tab;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("pageNum", pageNum);
        params.put("type", type);
        //couponType,1-加息券 2-预约券
        params.put("couponType", "1");
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 获取预约券TAB
     *
     * @param
     * @return
     */
    public boolean getPresellTicketlistTab(String pageNum, String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Addrate_Ticket_Tab;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("pageNum", pageNum);
        params.put("type", type);
        //couponType,1-加息券 2-预约券
        params.put("couponType", "2");
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public AddrateTicketTabInfo onGetAddrateTicketlistTab(JSONObject json) {
        Gson gson = new Gson();
        try {
            AddrateTicketTabInfo info = gson.fromJson("" + json, AddrateTicketTabInfo.class);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean voucherExchange(String exchangecode) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_voucherExchange;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("exchangecode", exchangecode);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public boolean updateUserBasicInfo(String username, String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_updateUserBasicInfo;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("userName", username);
        params.put("uid", uid);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public String onChangeName(JSONObject jsonObject) {
        String msg = "1";// 1 成功
        if (jsonObject != null) {
            msg = "" + jsonObject.optInt("msg");
        }
        return msg;
    }

    public int onVoucherExchange(JSONObject json) {
/*
{
	"msg": "0"
}
*/
        int ret = -1;
        if (json != null) {
            boolean ok = json.optInt("msg", 0) == 1;
            if (ok) {
                ret = json.optInt("money");
            }
        }
        return ret;
    }

    public boolean getuiLoginSendMess(String uid, String clientId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_GETUI_loginSendMess;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);
        params.put("clientId", clientId);
        params.put("systemType", "1");

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 魔方利率组合
     *
     * @return
     */
    public boolean rateComb() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_rateComb;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
//		params.put("LoanSignId", LoanSignId);
//		params.put("Money", Money);


        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * @param json
     * @return
     */
    public RegularAdapter.TopProductData onMofang_remoteCtrl(JSONObject json) {
/*
{"hasTopProduct":"1","topProductUrl":"http://www.vpfinance.cn/mofang/default"}
*/
        int hasTopProduct = json.optInt("hasTopProduct");
        String topProductUrl = json.optString("topProductUrl");
        if (hasTopProduct == 1) {
            RegularAdapter.TopProductData data = new RegularAdapter.TopProductData();
            data.topProductUrl = topProductUrl;
            data.hasTopProduct = true;
            return data;
        }
        return null;
    }

    public VoucherArray onVoucherArray(JSONObject jsonObject) {
        VoucherArray voucherArray = new VoucherArray();
//		int usable = 0;
        voucherArray.setAmount(jsonObject.optDouble("amount"));
        voucherArray.setAlAmount(jsonObject.optDouble("alAmount"));
        voucherArray.setAviAMount(jsonObject.optDouble("aviAmount"));
        voucherArray.setExpAmount(jsonObject.optDouble("expAmount"));
        voucherArray.setType(jsonObject.optInt("type"));
        int canUseVoucher = jsonObject.optInt("canUseVoucher");
        voucherArray.useable = canUseVoucher;

        voucherArray.setVoucherrate(jsonObject.optDouble("voucherrate"));
        JSONArray jsonArray = jsonObject.optJSONArray("voucherlist");
        ArrayList<Voucher> voucherList = new ArrayList<Voucher>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                Voucher voucher = new Voucher();
                JSONObject voucherjsonObject = jsonArray.optJSONObject(i);
                voucher.setId(voucherjsonObject.optInt("id"));
                JSONObject optJSONObject = voucherjsonObject.optJSONObject("voucher");
                if (optJSONObject != null) {
                    voucher.setName(optJSONObject.optString("name"));
                    voucher.setAmount(optJSONObject.optDouble("amount"));
                    voucher.setRate(optJSONObject.optDouble("rate"));
                } else {
                    voucher.setAmount(voucherjsonObject.optDouble("voucher_money"));
                }

                voucher.setExpireDate(voucherjsonObject.optLong("expireDate"));
                voucher.setCreatedAt(voucherjsonObject.optLong("createdAt"));

                String useRuleExplain = voucherjsonObject.optString("useRuleExplain");
                voucher.setUseRuleExplain(TextUtils.isEmpty(useRuleExplain) ? "" : useRuleExplain);

                int voucherStatus = voucherjsonObject.optInt("voucherStatus", 1);
                voucher.setVoucherStatus(voucherStatus);

                int usedTimes = voucherjsonObject.optInt("usedTimes");
                String type = "已使用"; //1已使用  0未使用  2已过期

                if (usedTimes == 1) {
                    type = "已使用";
                } else {
                    long expireDate = voucherjsonObject.optLong("expireDate");
                    type = expireDate > System.currentTimeMillis() ? "未使用" : "已过期";
                }
                if ("未使用".equals(type)) {

                }
                voucher.setName(type);

                voucherList.add(voucher);
            }
        }

        voucherArray.setVoucherList(voucherList);
        return voucherArray;
    }

    /**
     * 获取邀请链接
     *
     * @return
     */
    public boolean getPromoteLinks() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getPromoteLinks;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public PromoteLinks onPromoteLinks(JSONObject jsonObject) {
        PromoteLinks promoteLinks = new PromoteLinks();
        promoteLinks.setUrl(jsonObject.optString("url"));
        promoteLinks.setCode(jsonObject.optString("code"));
        promoteLinks.setMsg(jsonObject.optString("msg"));
        return promoteLinks;
    }

    @PostMethod(method = "/Appregister/isExistPhone")
    public boolean isExistPhone(String phone) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_isExistPhone;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("phone", phone);

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public int onCheckPhoneExist(JSONObject jsonObject) {
        return jsonObject.optInt("msg");
    }

    /**
     * 获取资金总览
     *
     * @return
     */
    public boolean getFundOverInfo(String uid, int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_FundOverView;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
//		Log.i("aaa","url--------:"+url+":uid:"+uid);
        params.put("uid", uid);
        //0连连 1存管
        params.put("accountType", accountType + "");
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public FundOverInfo onGetFundOverInfo(JSONObject json) {
        //{"cashBalance":"0.00","frozenAmtN":"0.00","inCount":"0.00","netAsset":"0.00"}
        if (json == null) {
            return null;
        }
        FundOverInfo info = new FundOverInfo();
        String cashBalance = json.optString("cashBalance");
        String frozenAmtN = json.optString("frozenAmtN");
        String inCount = json.optString("inCount");
        String netAsset = json.optString("netAsset");

        info.realMoney = json.optString("realMoney");

        info.setCashBalance(cashBalance);
        info.setFrozenAmtN(frozenAmtN);
        info.setInCount(inCount);
        info.setNetAsset(netAsset);

        return info;
    }

    /**
     * 资金流水记录显示
     *
     * @param type       流水类型(0:充值和体现 1:充值 2:提现)
     * @param uid        用户id
     * @param beginTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    开始条数（如果不传默认从1开始）
     * @param numPerPage 每页显示条数(如果不传默认每页显示10条)
     * @return
     */
    public boolean getFundFlow(String type, String uid, String beginTime, String endTime, String pageNum, String numPerPage, int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_FundFlow;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);
        params.put("type", type);
        if (beginTime != null) {
            params.put("beginTime", beginTime);
        }
        if (endTime != null) {
            params.put("endTime", endTime);
        }
        if (pageNum != null) {
            params.put("pageNum", pageNum);
        }
        if (numPerPage != null) {
            params.put("numPerPage", numPerPage);
        }
        params.put("accountType", accountType + "");

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public FundFlowInfo onGetFundFlowInfo(JSONObject json) {
        if (json == null) {
            return null;
        }
        FundFlowInfo fundFlowInfo = new FundFlowInfo();
        String type = json.optString("type");

        String success = json.optString("success");

        fundFlowInfo.totalPage = json.optString("totalPage");
        fundFlowInfo.type = type;
        fundFlowInfo.success = success;
        fundFlowInfo.list = new ArrayList<FundFlowInfo.FlowList>();

//		List<FundFlowInfo.FlowList> list = json.optJSONArray("flowList");
        try {
            JSONArray jsonArray = json.getJSONArray("flowList");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject opt = (JSONObject) jsonArray.opt(i);
                String explan = opt.optString("explan");
                String flowMoney = opt.optString("flowMoney");
                String time = opt.optString("time");
                String money = opt.optString("money");

//				FundFlowInfo.FlowList list = new FundFlowInfo().new FlowList();
                FundFlowInfo.FlowList list = new FundFlowInfo().new FlowList();
                list.setExplan(explan);
                list.setFlowMoney(flowMoney);
                list.setTime(time);
                list.setMoney(money);
                //只有提现记录才有
                if ("2".equals(type)) {
                    String status = opt.optString("status");
                    list.setStatus(status);
                    //只有未审核才有applyId
                    if ("0".equals(status)) {
                        String applyId = opt.optString("applyId");
                        list.setApplyId(applyId);
                    }
                }
                fundFlowInfo.list.add(list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fundFlowInfo;
    }

    /**
     * 获取投资记录数据
     *
     * @param uid        用户uid
     * @param beginTime  记录开始时间
     * @param endTime    结束时间
     * @param pageNum    开始页数（默认1开始）
     * @param numPerPage 每页显示条数
     * @return
     */
    public boolean getFundRecord(String uid, String beginTime, String endTime, String pageNum, String numPerPage) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_FundRecord;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);
        if (beginTime != null) {
            params.put("beginTime", beginTime);
        }
        if (endTime != null) {
            params.put("endTime", endTime);
        }
        if (pageNum != null) {
            params.put("pageNum", pageNum);
        }
        if (numPerPage != null) {
            params.put("numPerPage", numPerPage);
        }

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 获取新投资记录数据
     *
     * @param uid        用户uid
     * @param pageNum    开始页数（默认1开始）
     * @param numPerPage 每页显示条数
     * @param type       1 持有中   2已完成
     * @return
     */
    public boolean getNewFundRecord(String uid, String pageNum, String numPerPage, String type, int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_FundRecord;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);
        params.put("type", type);
        params.put("accountType", accountType + "");
        if (pageNum != null) {
            params.put("pageNum", pageNum);
        }
        if (numPerPage != null) {
            params.put("numPerPage", numPerPage);
        }

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public NewRecordsBean onGetNewFundRecord(JSONObject jsonObject) {
        Gson gson = new Gson();
        NewRecordsBean bean = null;
        try {
            bean = gson.fromJson(jsonObject.toString(), NewRecordsBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public FundRecordInfo onGetFundRecordInfo(JSONObject json) {
        if (json == null) {
            return null;
        }
        FundRecordInfo fundRecordInfo = new FundRecordInfo();
        String success = json.optString("success");

        fundRecordInfo.totalPage = json.optString("totalPage");

        fundRecordInfo.success = success;
        fundRecordInfo.recordList = new ArrayList<FundRecordInfo.RecordListInfoItem>();

        try {
            JSONArray jsonArray = json.getJSONArray("recordList");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject opt = (JSONObject) jsonArray.opt(i);
                String tenderMoney = opt.optString("tenderMoney");
                String tenderTime = opt.optString("tenderTime");
                String title = opt.optString("title");
                String voucherMoney = opt.optString("voucherMoney");
                String haveRedPacket = opt.optString("haveRedPacket");
                String shareUrl = opt.optString("shareUrl");
                //recordId=47392
                int recordId = opt.optInt("recordId");
                String couponsCount = opt.optString("couponsCount");

                FundRecordInfo.RecordListInfoItem list = new FundRecordInfo().new RecordListInfoItem();
                list.title = title;
                list.tenderMoney = tenderMoney;
                list.tenderTime = tenderTime;
                list.voucherMoney = voucherMoney;
                list.haveRedPacket = haveRedPacket;
                list.shareUrl = shareUrl;
                list.couponsCount = couponsCount;
                list.recordId = recordId;

                fundRecordInfo.recordList.add(list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fundRecordInfo;
    }

    /**
     * 回款数据查询
     *
     * @param uid
     * @param pageNum     开始页数
     * @param numPerPage  每页显示
     * @param returnValue 分页的时候，第一次不需要传值，第一次以后需要将第一次接受的这个值再返回回去
     * @return
     */
    public boolean getTradeFlowRecord(String uid, String pageNum, String numPerPage, String returnValue, int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_TradeFlowRecord;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        ArrayMap<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);
        params.put("accountType", accountType + "");
        if (pageNum != null) {
            params.put("pageNum", pageNum);
        }
//		if (numPerPage !=null){
//			params.put("numPerPage",numPerPage);
//		}
        if (returnValue != null) {
            params.put("returnValue", returnValue);
        }
        httpClient.setTips("加载中");

        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public TradeFlowRecordInfo onGetTradeFlowRecord(JSONObject json) {
        if (json == null) {
            return null;
        }
        TradeFlowRecordInfo tradeFlowRecordInfo = new TradeFlowRecordInfo();
        tradeFlowRecordInfo.success = TextUtils.isEmpty(json.optString("success")) ? "false" : json.optString("success");
        tradeFlowRecordInfo.returnValue = TextUtils.isEmpty(json.optString("returnValue")) ? "" : json.optString("returnValue");
        tradeFlowRecordInfo.returnedCount = TextUtils.isEmpty(json.optString("returnedCount")) ? "" : json.optString("returnedCount");
        tradeFlowRecordInfo.returnedSumMoney = TextUtils.isEmpty(json.optString("returnedSumMoney")) ? "" : json.optString("returnedSumMoney");
        tradeFlowRecordInfo.totalPage = TextUtils.isEmpty(json.optString("totalPage")) ? "" : json.optString("totalPage");
        tradeFlowRecordInfo.dataList = new ArrayList<TradeFlowRecordInfo.DataListItem>();

        try {
            JSONArray dataList = json.getJSONArray("dataList");
            for (int i = 0; i < dataList.length(); i++) {

                TradeFlowRecordInfo.DataListItem dataListItem = new TradeFlowRecordInfo().new DataListItem();
                JSONObject opt = (JSONObject) dataList.opt(i);
                dataListItem.loanTitle = TextUtils.isEmpty(opt.optString("loanTitle")) ? "" : opt.optString("loanTitle");
                dataListItem.preRepayMoneySum = TextUtils.isEmpty(opt.optString("preRepayMoneySum")) ? "" : opt.optString("preRepayMoneySum");
                dataListItem.repayMoneySum = TextUtils.isEmpty(opt.optString("repayMoneySum")) ? "" : opt.optString("repayMoneySum");
                dataListItem.tenderMoneySum = TextUtils.isEmpty(opt.optString("tenderMoneySum")) ? "" : opt.optString("tenderMoneySum");
                dataListItem.returnMoneyTime = TextUtils.isEmpty(opt.optString("returnMoneyTime")) ? "" : opt.optString("returnMoneyTime");
                dataListItem.dataList2 = new ArrayList<TradeFlowRecordInfo.DataListItem.DataListItem2>();

                JSONArray dataList2 = opt.getJSONArray("dataList2");
                for (int j = 0; j < dataList2.length(); j++) {
                    TradeFlowRecordInfo.DataListItem.DataListItem2 dataListItem2 = new TradeFlowRecordInfo().new DataListItem().new DataListItem2();
                    JSONObject opt2 = (JSONObject) dataList2.opt(j);
                    dataListItem2.status = opt2.optString("status");
                    dataListItem2.repayTime = opt2.optString("repayTime");
                    dataListItem2.preRepayMoney = opt2.optString("money");
                    dataListItem2.periods = opt2.optString("periods");
                    dataListItem2.attribute1 = opt2.optString("attribute1");
                    dataListItem2.attribute2 = opt2.optString("attribute2");

                    dataListItem.dataList2.add(dataListItem2);
                }
                String id = opt.optString("id", UUID.randomUUID().toString());
                if (!TextUtils.isEmpty(id)) {
                    dataListItem.id = id;
                }
                tradeFlowRecordInfo.dataList.add(dataListItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return tradeFlowRecordInfo;
    }

    /**
     * 邀请好友界面显示的信息
     *
     * @param uid
     * @return
     */
    public boolean getInviteShowInfo(String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_InviteGiftShowInfo;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        ArrayMap<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);

        httpClient.setTips("记载中");

        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public InviteShowInfo onGetInviteShowInfo(JSONObject json) {
        if (json == null) {
            return null;
        }
        InviteShowInfo inviteShowInfo = new InviteShowInfo();
        inviteShowInfo.bonusesCount = json.optString("bonusesCount");
        inviteShowInfo.success = json.optString("success");
        inviteShowInfo.invitePerCount = json.optString("invitePerCount");
        inviteShowInfo.registerRewardCount = json.optString("registerRewardCount");
        return inviteShowInfo;
    }

    /**
     * 投资奖励记录
     *
     * @param uid
     * @param pageNum
     * @param numPerPage
     * @return
     */
    public boolean getInvestAwardRecord(String uid, String pageNum, String numPerPage) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_InvestAwardRecord;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        ArrayMap<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);
        if (pageNum != null) {
            params.put("pageNum", pageNum);
        }
        if (numPerPage != null) {
            params.put("numPerPage", numPerPage);
        }
        httpClient.setTips("记载中");

        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public AwardRecordInvestInfo onGetInvestAwardRecord(JSONObject json) {
        if (json == null) {
            return null;
        }
        AwardRecordInvestInfo info = new AwardRecordInvestInfo();
        info.success = json.optString("success");
        info.totalPage = json.optString("totalPage");

        ArrayList<AwardRecordInvestInfo.AwardRecordInvestInfoItem> dataList = new ArrayList<>();
        try {
            JSONArray jsonArray = json.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                AwardRecordInvestInfo.AwardRecordInvestInfoItem infoItem = new AwardRecordInvestInfo().new AwardRecordInvestInfoItem();
                JSONObject opt = (JSONObject) jsonArray.get(i);
                infoItem.periods = opt.optString("periods");
                infoItem.addTime = opt.optString("addTime");
                infoItem.bonuses = opt.optString("bonuses");
                dataList.add(infoItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        info.dataList = dataList;
        return info;
    }

    /**
     * 注册奖励记录
     *
     * @param uid
     * @param pageNum
     * @param numPerPage
     * @return
     */
    public boolean getRegistAwardRecord(String uid, String pageNum, String numPerPage) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_RegistAwardRecord;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        ArrayMap<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);
        if (pageNum != null) {
            params.put("pageNum", pageNum);
        }
        if (numPerPage != null) {
            params.put("numPerPage", numPerPage);
        }
        httpClient.setTips("记载中");

        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public AwardRecordRegistInfo onGetRegistAwardRecord(JSONObject json) {
        if (json == null) {
            return null;
        }
        AwardRecordRegistInfo info = new AwardRecordRegistInfo();
        info.success = json.optString("success");
        info.totalPage = json.optString("totalPage");

        ArrayList<AwardRecordRegistInfo.AwardRecordRegistInfoItem> dataList = new ArrayList<>();
        try {
            JSONArray jsonArray = json.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                AwardRecordRegistInfo.AwardRecordRegistInfoItem infoItem = new AwardRecordRegistInfo().new AwardRecordRegistInfoItem();
                JSONObject opt = (JSONObject) jsonArray.get(i);
                infoItem.phone = TextUtils.isEmpty(opt.optString("phone")) ? "" : opt.optString("phone");
                infoItem.inviteTime = TextUtils.isEmpty(opt.optString("inviteTime")) ? "" : opt.optString("inviteTime");
                infoItem.reward = TextUtils.isEmpty(opt.optString("reward")) ? "" : opt.optString("reward");
                infoItem.status = TextUtils.isEmpty(opt.optString("status")) ? "" : opt.optString("status");
                infoItem.period = TextUtils.isEmpty(opt.optString("period")) ? "" : opt.optString("period");
                dataList.add(infoItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        info.dataList = dataList;
        return info;
    }

    /**
     * 找回交易密码
     *
     * @param phone
     * @param newPwd
     * @param smscode
     * @return
     */
    public boolean resetPayPassword(String phone, String newPwd, String smscode) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_ResetPayPassword;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

//		phone = phone.substring(1);
        params.put("phone", phone);
        params.put("new_pwd", newPwd);
        params.put("smscode", smscode);
        Log.i("aaa", "phone:" + phone + ",new_pwd:" + newPwd + ",smscode:" + smscode);

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onResetPayPassword(JSONObject jsonObject) {
        int errCode = jsonObject.optInt("msg");
        String errMsg = "";
        switch (errCode) {
            case 0:
                errMsg = "用户不存在";
                break;

            case 1:
                errMsg = "操作成功";
                break;

            case 2:
                errMsg = "验证码错误";
                break;

            case 4:
                errMsg = "短信已超时";
                break;

            case 5:
                errMsg = "内部错误";
                break;
        }
        return errMsg;
    }

    /**
     * 获取提现手续费
     *
     * @return
     */
    public boolean getWithdrawCharge(String withdrawCharge) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_withdrawCharge;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("money", withdrawCharge);
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }


    public FundOverInfo onGetWithdrawCharge(JSONObject json) {
        if (json == null) {
            return null;
        }
        FundOverInfo info = new FundOverInfo();
        String cashBalance = json.optString("cashBalance");
        String frozenAmtN = json.optString("frozenAmtN");
        String inCount = json.optString("inCount");
        String netAsset = json.optString("netAsset");

        info.realMoney = json.optString("realMoney");

        info.setCashBalance(cashBalance);
        info.setFrozenAmtN(frozenAmtN);
        info.setInCount(inCount);
        info.setNetAsset(netAsset);

        return info;
    }

    /**
     * 取消提现
     *
     * @param applyId
     */
    public boolean getCancelWithdraw(String applyId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_CancelWithdraw;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("applyId", applyId);
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 预计收益
     *
     * @param loanId      定存宝的是poolId
     * @param tenderMoney
     */
    public boolean getPredictMoney(String loanId, String tenderMoney, boolean isDeposit) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_PredictMoney;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);
        params.put("tenderMoney", tenderMoney);
        if (isDeposit) {
            //定存宝传值2
            params.put("type", "2");
        }
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 获取服务器地址
     */
    public boolean getVpUrl() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_GetVpUrl;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 分享红包信息
     */
    public boolean getRedPacketInfo() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_RedPactetInfo;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 获取首页沈阳众筹标
     *
     * @return
     */
    public boolean getHomePresellProduct() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_HomePresellProduct;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");
        //httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public PresellProductInfo onGetHomePresellProduct(JSONObject json) {
        if (json == null) {
            return null;
        }
        PresellProductInfo info = new PresellProductInfo();

        info.borrowId = TextUtils.isEmpty(json.optString("borrowId")) ? "" : json.optString("borrowId");
        info.borrowTitle = TextUtils.isEmpty(json.optString("borrowTitle")) ? "" : json.optString("borrowTitle");
        info.minRate = TextUtils.isEmpty(json.optString("minRate")) ? "" : json.optString("minRate");
        info.maxRate = TextUtils.isEmpty(json.optString("maxRate")) ? "" : json.optString("maxRate");
        info.imageUrl = TextUtils.isEmpty(json.optString("imageUrl")) ? "" : json.optString("imageUrl");

        info.month = TextUtils.isEmpty(json.optString("month")) ? "" : json.optString("month");
        info.borrowStatus = TextUtils.isEmpty(json.optString("borrowStatus")) ? "" : json.optString("borrowStatus");
        info.borrowEndTime = TextUtils.isEmpty(json.optString("borrowEndTime")) ? "" : json.optString("borrowEndTime");
        info.issueLoan = TextUtils.isEmpty(json.optString("issueLoan")) ? "" : json.optString("issueLoan");
        info.totalMoney = TextUtils.isEmpty(json.optString("totalMoney")) ? "" : json.optString("totalMoney");
        info.success = TextUtils.isEmpty(json.optString("success")) ? "" : json.optString("success");

        info.borrowLoanPercent = TextUtils.isEmpty(json.optString("borrowLoanPercent")) ? "" : json.optString("borrowLoanPercent");
        info.allowTransfer = TextUtils.isEmpty(json.optString("allowTransfer")) ? "" : json.optString("allowTransfer");
        return info;
    }

    /**
     * 获取沈阳众筹标基本信息
     *
     * @return
     */
    public boolean getPresellProductInfo(String pid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_PresellProductInfo;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");
        params.put("loanId", pid);
        //httpClient.setTips("登录中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public PresellProductInfo onGetPresellProductInfo(JSONObject json) {
        if (json == null) {
            return null;
        }
        PresellProductInfo info = new PresellProductInfo();

        info.borrowEndTime = TextUtils.isEmpty(json.optString("bidEndTime")) ? "" : json.optString("bidEndTime");
        info.minRate = TextUtils.isEmpty(json.optString("minRate")) ? "" : json.optString("minRate");
        info.maxRate = TextUtils.isEmpty(json.optString("maxRate")) ? "" : json.optString("maxRate");
        info.imageUrl = TextUtils.isEmpty(json.optString("imageUrl")) ? "" : json.optString("imageUrl");
        info.total_tend_money = TextUtils.isEmpty(json.optString("total_tend_money")) ? "" : json.optString("total_tend_money");

        try {
            JSONObject loansignOpt = json.optJSONObject("loansign");
            info.borrowId = TextUtils.isEmpty(loansignOpt.optString("id")) ? "" : loansignOpt.optString("id");//id
            info.issueLoan = TextUtils.isEmpty(loansignOpt.optString("issueLoan")) ? "" : loansignOpt.optString("issueLoan");//总金额
            info.borrowStatus = TextUtils.isEmpty(loansignOpt.optString("loanstate")) ? "" : loansignOpt.optString("loanstate");//（详情-->1未发布  2进行中 3回款中  4已完成)
            info.loanUnit = TextUtils.isEmpty(loansignOpt.optString("loanUnit")) ? "" : loansignOpt.optString("loanUnit");//最小单位
            info.month = TextUtils.isEmpty(loansignOpt.optString("month")) ? "" : loansignOpt.optString("month");//
            info.refundWay = TextUtils.isEmpty(loansignOpt.optString("refundWay")) ? "" : loansignOpt.optString("refundWay");//1按月等额本息 2按月付息到期还本  3到期一次性还本息

            JSONObject loansignbasicOpt = json.optJSONObject("loansignbasic");
            info.borrowTitle = TextUtils.isEmpty(loansignbasicOpt.optString("loanTitle")) ? "" : loansignbasicOpt.optString("loanTitle");//
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获取沈阳众筹标H5
     *
     * @return
     */
    public boolean getPresellProductH5() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_PresellProductH5;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 我的账户消息通知
     *
     * @return
     */
    public boolean getMessageNotice() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Message_Notice;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("TYPE", "android");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 小微头条
     *
     * @return
     */
    public boolean getXiaoWeiNoticeList() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_XiaoWei_NoticeList;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public ArrayList<Pair<String, String>> onGetXiaoWeiNoticeList(JSONObject json) {

        ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

        String success = json.optString("success");
        if (!TextUtils.isEmpty(success) && "true".equals(success)) {
            JSONArray jsonArray = json.optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String title = TextUtils.isEmpty(item.optString("title")) ? "" : item.optString("title");
                    String linkUrl = TextUtils.isEmpty(item.optString("linkUrl")) ? "" : item.optString("linkUrl");
                    list.add(new Pair<String, String>(title, linkUrl));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }


    /**
     * 我的债权列表
     *
     * @param loanType 1可转让 2转让中3已转让
     * @param pageNum  当前页数
     */
    public boolean getTransferProductList(String loanType, String pageNum, int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Transfer_Assign_List;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanType", loanType);
        params.put("pageNum", pageNum);
        params.put("accountType", accountType + "");

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public HashMap<String, String> onGetTransferProductList(JSONObject json, ArrayList<TransferListItemInfo> list) {
        HashMap<String, String> map = new HashMap<>();
        int totalPage = json.optInt("totalPage");
        map.put("totalPage", "" + totalPage);
        JSONArray loanrecords = json.optJSONArray("loanrecords");
        for (int i = 0; i < loanrecords.length(); i++) {
            JSONObject item = loanrecords.optJSONObject(i);
            TransferListItemInfo itemInfo = new TransferListItemInfo();
            String tenderMoney = item.optString("tenderMoney");
            String tenderTime = item.optString("tenderTime");
            String title = item.optString("title");
            String recordId = item.optString("recordId");
            String voucherMoney = item.optString("voucherMoney");
            itemInfo.setTitle(TextUtils.isEmpty(title) ? "" : title);
            itemInfo.setMoney(TextUtils.isEmpty(tenderMoney) ? "" : tenderMoney);
            itemInfo.setDesc(TextUtils.isEmpty(voucherMoney) ? "" : voucherMoney);
            itemInfo.setTime(TextUtils.isEmpty(tenderTime) ? "" : tenderTime);
            itemInfo.setRecordId(TextUtils.isEmpty(recordId) ? "" : recordId);
            list.add(itemInfo);
        }
        return map;
    }

    /**
     * 债权详情
     *
     * @param recordId
     */
    public boolean getTransferProductDetail(String recordId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Transfer_Assign_Detail;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("recordId", recordId);

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public TransferProductDetailInfo onGetTransferProductDetail(JSONObject json) {
        TransferProductDetailInfo info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, TransferProductDetailInfo.class);
        }
        return info;
    }

    /**
     * 回款计划
     *
     * @param borrowId
     * @return
     */
    public boolean getTransferProductRefund(String borrowId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Transfer_Assign_Refund;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("borrowId", borrowId);

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public TransferRefundInfo onGetTransferProductRefund(JSONObject json) {
        TransferRefundInfo info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, TransferRefundInfo.class);
        }
        return info;
    }

    /**
     * 投资记录详情-还款计划
     *
     * @param type
     * @param borrowId
     * @return
     */
    public boolean getInvestRecordRefund(int type, String borrowId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Invest_Record_Refund;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("recordId", borrowId);
        if (type == 1) {
            params.put("type", type + "");
        }

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public boolean getTransferProductNow(String loanRecordId, String borrowId, String tenderMoney, String pwd) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Transfer_Assign_Now;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanRecordId", loanRecordId);
        params.put("borrowId", borrowId);
        params.put("tenderMoney", tenderMoney);
        params.put("transPassword", pwd);

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 轮播首页数据
     *
     * @return
     */
    public boolean getHomeProductInfo() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Home_Product_Info;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public HomeVpProductInfo onGetHomeProductInfo(JSONObject json) {
        HomeVpProductInfo info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, HomeVpProductInfo.class);
        }
        return info;
    }

    /**
     * 银行卡类型列表
     *
     * @return
     */
    public boolean getbBankTypeList() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Bank_Type_List;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public BankBean onGetBankTypeList(JSONObject json) {
        BankBean info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, BankBean.class);
        }
        return info;
    }

    /**
     * 设置交易密码，不用验证码
     *
     * @param new_pwd
     * @param uid
     * @return
     */
    public boolean resetTradePassword2New(
            @Nullable String new_pwd,
            String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_resetTradePasswordNew;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        if (!TextUtils.isEmpty(new_pwd)) {
            params.put("pwd", new_pwd);
        }
        params.put("uid", uid);

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 随机返回用户名
     *
     * @return
     */
    public boolean getRandomUserName() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_RandomUserName;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public String onGetRandomUserName(JSONObject json) {
        String username = json.optString("username");
        return username;
    }

    /**
     * 获取代金券抵扣比率
     *
     * @return
     */
    public boolean getVoucherRate(String loansignid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_voucherRate;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        if (!TextUtils.isEmpty(loansignid)) {
            params.put("loansignid", loansignid);
        }
        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 周年庆活动
     *
     * @param loanId
     * @return
     */
    public boolean getOneYear(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_OneYear;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public OneYearBean onGetOneYear(JSONObject json) {

        OneYearBean info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, OneYearBean.class);
        }
        return info;
    }

    public boolean getOneYearVoucher(String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_OneYear_GetVoucher;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("uid", uid);

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 投资统计资产总额
     *
     * @return
     */
    public boolean getAssetStatistics(int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_AssetStatistics;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("accountType", "" + accountType);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public InvestSummaryTab2Bean onGetAssetStatistics(JSONObject json) {
        InvestSummaryTab2Bean info = null;
        if (json != null) {
            info = new InvestSummaryTab2Bean();
            info.setCashMoney(json.optDouble("cashMoney"));
            info.setFrozenMoney(json.optDouble("frozenMoney"));
            info.setReturnMoney(json.optDouble("returnMoney"));
            info.setTotalMoney(json.optDouble("totalMoney"));

            try {
                JSONArray data = json.optJSONArray("data");
                ArrayList<Map<String, Double>> rsList = new ArrayList<Map<String, Double>>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.optJSONObject(i);
                    Map<String, Double> map = new HashMap<String, Double>();
                    for (Iterator<?> iter = jsonObject.keys(); iter.hasNext(); ) {
                        String key = (String) iter.next();
                        double value = (double) jsonObject.optDouble(key);
//						Logger.e("key:"+key+",value:"+value);
                        map.put(key, value);
                    }
                    rsList.add(map);
                }
                info.setData(rsList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return info;
    }

    /**
     * 投资统计收益统计
     *
     * @return
     */
    public boolean getTenderPromit(int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_TenderPromit;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("accountType", "" + accountType);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public InvestSummaryTab3Bean onGetTenderPromit(JSONObject json) {
        InvestSummaryTab3Bean info = null;
        if (json != null) {
            info = new InvestSummaryTab3Bean();
            info.setCapitalMoney(json.optDouble("capitalMoney"));
            info.setVoucherSumMoney(json.optDouble("voucherSumMoney"));
            info.setAllTotalMoney(json.optDouble("allTotalMoney"));
            info.setInviteMoney(json.optDouble("inviteMoney"));
            info.setOtherMoney(json.optDouble("otherMoney"));

            try {
                JSONArray data = json.optJSONArray("data");
                ArrayList<Map<String, Double>> rsList = new ArrayList<Map<String, Double>>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.optJSONObject(i);
                    Map<String, Double> map = new HashMap<String, Double>();
                    for (Iterator<?> iter = jsonObject.keys(); iter.hasNext(); ) {
                        String key = (String) iter.next();
                        double value = (double) jsonObject.optDouble(key);
//						Logger.e("key:"+key+",value:"+value);
                        map.put(key, value);
                    }
                    rsList.add(map);
                }
                info.setData(rsList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return info;
    }

    /**
     * @param type 类型 1.财富 2.收益
     * @return
     */
    public boolean getInvestTop(int accountType, String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Invest_Top;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("type", type);
        params.put("accountType", accountType + "");
        httpClient.setTips("加载中");

        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public InvestTopBean onGetInvestTop(JSONObject json) {
        InvestTopBean info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, InvestTopBean.class);
        }
        return info;
    }

    /**
     * 个人名片
     *
     * @return
     */
    public boolean getPersonalCard(String userId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Personal_Card;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("userId", userId);

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public PersonalCardBean onGetPersonalCard(JSONObject json) {
        PersonalCardBean info = null;
        if (json != null) {
            try {
                Gson gson = new Gson();
                info = gson.fromJson("" + json, PersonalCardBean.class);
                info.isShowFriends = json.has("myFriends");
                info.isShowInvestInfo = json.has("month1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return info;
    }

    /**
     * 我的账户  重载解析方法
     *
     * @param json
     */
    public UserInfoBean onGetUserInfo(JSONObject json) {
        UserInfoBean info = null;
        try {
            if (json != null) {
                Gson gson = new Gson();
                info = gson.fromJson("" + json, UserInfoBean.class);
                if (info == null) return null;
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(mContext);
                boolean isOpen = "1".equals(info.isOpen) ? true : false;
                preferencesHelper.putBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT, isOpen);
//				info.isNewUser = 1;
                boolean isNewUser = "1".equals(info.isNewUser) ? true : false;
                preferencesHelper.putBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER, isNewUser);

                boolean isBindHxBank = "1".equals(info.isBindHxBank) ? true : false;
                preferencesHelper.putBooleanValue(SharedPreferencesHelper.KEY_IS_BIND_BANK, isBindHxBank);

                boolean isAllowRecharge = "1".equals(info.isAllowRecharge) ? true : false;//1.允许充值2不允许充值
                if (info.accountType == Constant.AccountLianLain) {
                    preferencesHelper.putBooleanValue(SharedPreferencesHelper.KEY_ALLOW_RECHARGE, isAllowRecharge);
                }

                SQLiteDatabase db = new DaoMaster.DevOpenHelper(mContext, Config.DB_NAME, null).getWritableDatabase();
                UserDao userDao = new DaoMaster(db).newSession().getUserDao();
                List<User> users = userDao.loadAll();
                if (users != null && users.size() != 0) {
                    User user = users.get(0);
                    user.setUserName(info.userName);
                    user.setRealName(info.realName);
                    user.setCellPhone(info.phone);
                    user.setIdentityCard(info.identityCard);
                    try {
                        user.setHasTradePassword(Boolean.parseBoolean(info.set_trans_pwd));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //只保存连连账户的. 因为连连充值用到了
                    if (info.accountType == Constant.AccountLianLain) {
                        try {
                            user.setPreIncome(Double.parseDouble(info.preIncome));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setInvest(Double.parseDouble(info.invest));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setDSum(Double.parseDouble(info.dSum));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setDBid(Double.parseDouble(info.dBid));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setPaying(Double.parseDouble(info.paying));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setFrozenAmtN(Double.parseDouble(info.frozenAmtN));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setNetAsset(Double.parseDouble(info.netAsset));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setCashBalance(Double.parseDouble(info.cashBalance));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    userDao.update(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    /**
     * 产品投资分布
     *
     * @return
     */
    public boolean getProductInvestDistribution(int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_ProductInvestDistribution;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("accountType", "" + accountType);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public InvestSummaryTab1Bean onGetProductInvestDistribution(JSONObject json) {
        InvestSummaryTab1Bean info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, InvestSummaryTab1Bean.class);
        }
        return info;
    }

    /**
     * 查看所有勋章
     *
     * @return
     */
    public boolean getAllMedals(int accountType, String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_LookAllMedals;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("userId", uid);
        params.put("accountType", accountType + "");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public AllMedalsBean onGetAllMedals(JSONObject json) {
        AllMedalsBean info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, AllMedalsBean.class);
        }
        return info;
    }

    /**
     * 修改个性签名
     *
     * @param content
     * @return
     */
    public boolean getMyDescribe(String content) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_My_Describe;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("content", content);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 查看隐式设置
     *
     * @return
     */
    public boolean getPrivateSetting() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_LookPrivateSetting;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public PrivateSettingBean onGetPrivateSetting(JSONObject json) {
        PrivateSettingBean info = null;
        if (json != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + json, PrivateSettingBean.class);
        }
        return info;
    }

    /**
     * 更新隐式设置
     *
     * @return
     */
    public boolean getUpdatePrivateSetting(String key, String value) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_ChangePrivateSetting;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("settingKey", key);
        params.put("settingValue", value);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 车贷项目
     *
     * @return
     */
    public boolean getProductCarInfo(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_loanSignInfo;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);//标的号

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public ProductCarInfo onGetProductCarInfo(JSONObject jsonObject) {
        ProductCarInfo info = null;
        try {
            if (jsonObject != null) {
                Gson gson = new Gson();
                info = gson.fromJson("" + jsonObject, ProductCarInfo.class);
            }
        } catch (Exception e) {
            return info;
        }
        return info;
    }

    /**
     * 珠宝贷bean
     *
     * @param jsonObject
     * @return
     */
    public JewelryBean onGetProductJewelryInfo(JSONObject jsonObject) {
        JewelryBean info = null;
        try {
            if (jsonObject != null) {
                Gson gson = new Gson();
                info = gson.fromJson("" + jsonObject, JewelryBean.class);
            }
        } catch (Exception e) {
            return info;
        }
        return info;
    }

    /**
     * 语音验证码
     *
     * @param phoneNum
     * @return
     */
    public boolean getVoiceCaptcha(String phoneNum, String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Voice_Captcha;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("phone", phoneNum);
        params.put("type", type);

        httpClient.setTips("提交中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public String onGetVoiceCaptcha(JSONObject json) {
        try {
            int msgCode = json.optInt("msg", -1);
            String msg = "语音电话已经呼出，请注意接听021/028开头的电话";
            switch (msgCode) {
                case 0:
                    msg = "发送失败";
                    break;
                case 1:
                    msg = "语音电话已经呼出，请注意接听021/028开头的电话";
                    break;
                case 2:
                    msg = "操作太频繁，请稍后再试";
                    break;
                case 3:
                    msg = "手机号码格式不正确";
                    break;
                case 5:
                    msg = "操作太频繁，请输入图形验证码";
                    break;
                case 6:
                    msg = "手机号不存在";
                    break;
                default:
                    break;
            }
            return msg;
        } catch (Exception ex) {
        }
        return "请求验证码出错";
    }

    /**
     * 投资可用加息券
     *
     * @param pageNum
     * @param loanId
     * @param money
     * @return
     */
    public boolean getAddRateInvest(String pageNum, String loanId, String money) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Addrate_Ticket_invest;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("pageNum", pageNum);//当前页
        params.put("loanId", loanId);//标的号
        params.put("money", money);//投资金额

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public AddRateBean onGetAddRateInvest(JSONObject jsonObject) {
        AddRateBean info = null;
        if (jsonObject != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + jsonObject, AddRateBean.class);
        }
        return info;
    }

    /**
     * 计算加息抵扣金额
     *
     * @param money
     * @param rate
     * @param addRatePeriod
     * @return
     */
    public boolean getAddRateIncome(String money, String rate, String addRatePeriod) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Addrate_Income;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("money", money);//投资金额
        params.put("rate", rate);//加息利率
        params.put("addRatePeriod", addRatePeriod);//加息天数

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 奥运推广页
     *
     * @return
     */
    public boolean getHomeEvent() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Home_Event;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 获取webview分享内容
     *
     * @return
     */
    public boolean getWebViewShareContent(String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Webview_Share_Content;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("type", type);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 通知服务器请求成功
     *
     * @return
     */
    public boolean getWebViewShareSuccess(String phone, String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Webview_Share_Success;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("type", type);
        params.put("phone", phone);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public boolean getTransferCost(String loanId, String money, String debtMoney) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Transfer_cost;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);
        params.put("money", money);
        params.put("debtMoney", debtMoney);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 投资送iphone页面描述
     *
     * @param type
     * @return
     */
    public boolean getIphoneDes(String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Invest_Give_Iphone;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("type", type);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public IphoneDesBean onGetIphoneDes(JSONObject jsonObject) {
        IphoneDesBean info = null;
        if (jsonObject != null) {
            Gson gson = new Gson();
            info = gson.fromJson("" + jsonObject, IphoneDesBean.class);
        }
        return info;
    }

    /**
     * 获取定期新的接口
     *
     * @param typeList  1.定期  2.债权转让  默认为1
     * @param startPage 从start条记录开始（默认0）
     * @param pageSize  取limit条记录（默认5）
     * @param queryText 模糊查询
     * @return
     */
    public boolean getLoanSignListNew(int typeList, int startPage, int pageSize, String queryText) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Loan_Sign_List_New;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("type", "" + typeList);// 1.定期  2.债权转让 4存管专区 默认为1

        param.put("start", "" + startPage);//从start条记录开始（默认0）
        param.put("limit", "" + pageSize);//取limit条记录（默认5）
        param.put("title", queryText);//模糊查询
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    public LoanSignListNewBean onGetLoanSignListNew(JSONObject jsonObject) {
        Gson gson = new Gson();
        LoanSignListNewBean bean = null;
        try {
            bean = gson.fromJson(jsonObject.toString(), LoanSignListNewBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 新的定期产品详情
     * 查看原标是传参不一样
     *
     * @param loanId
     * @return
     */
    public boolean getFixProductNew(String loanId, String nativeId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_loanSignInfo_New;

        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);//原标id
        if (!"0".equals(nativeId)) {
            params.put("deptLoanId", nativeId);//转让标id
        }
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }


    /**
     * 通用请求TAB接口
     *
     * @param url url
     * @return
     */
    public boolean getRegularTab(String url) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Regular_Tab;
        Map<String, String> param = new ArrayMap<String, String>();
        return httpClient.doPost(mBaseUrl + url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 查看tab权限控制
     *
     * @param loanId     标的号
     * @param deptLoanId 通过查看原标进入的标详情界面，需要传此参数，转让标id
     */
    public boolean onGetPermission(String loanId, String deptLoanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Tab_Permission;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);//原标id
        if (!"0".equals(deptLoanId)) {
            params.put("deptLoanId", deptLoanId);//转让标id
        }
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public boolean onGetDepositPermission(String poolId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Tab_Permission;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("poolId", poolId);//原标id
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 债权转让
     *
     * @param loanId 标的号
     */
    public boolean getTransferProductInfo(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Transfer_Product_Info;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);
        params.put("dataType", "0");
        params.put("loanType", "0");

        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 版本检查更新
     *
     * @return
     */
    public boolean getVersionCheck(String version) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_VERSION_CHECK;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("type", "1");
        params.put("version", version);
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 合同范本接口(定存宝)
     *
     * @param loanId
     * @return
     */
    public boolean getProtocol(String loanId, String poolId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_LOAN_PROTOCOL;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);
        params.put("poolId", poolId);
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    /**
     * 合同范本接口(普通标)
     *
     * @param loanId
     * @return
     */
    public boolean getProtocol(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_LOAN_PROTOCOL;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("loanId", loanId);
        httpClient.setTips("加载中");
        return httpClient.doPost(url, params, cmdId.ordinal(), false, true);
    }

    public LoanProtocolBean onGetgetProtocol(JSONObject jsonObject) {
        Gson gson = new Gson();
        LoanProtocolBean bean = null;
        try {
            bean = gson.fromJson(jsonObject + "", LoanProtocolBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 获取图形验证码
     *
     * @return
     */
    public boolean getImageCode() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_IMAGE_CODE;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 校验图形验证码
     *
     * @param imgCaptcha 验证码
     * @param phone      手机号
     * @param type       1.短信2.语音
     * @return
     */
    public boolean getVerifyImageCode(String imgCaptcha, String phone, String type) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_VERIFY_IMAGE_CODE;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("imgCaptcha", imgCaptcha);
        params.put("phone", phone);
        params.put("type", type);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    /**
     * 获取投资记录详情
     *
     * @param uid
     * @return
     */
    public boolean getRecordDetailInfo(String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Record_Detail_info;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("recordId", uid);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public RecordDetailBean onGetgetRecordDetailInfo(JSONObject jsonObject) {
        Gson gson = new Gson();
        RecordDetailBean bean = null;
        try {
            bean = gson.fromJson(jsonObject + "", RecordDetailBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 获取投资记录详情
     *
     * @param recordPoolId
     * @return
     */
    public boolean getRecordDepositDetailInfo(String recordPoolId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Record_Deposit_Detail_info;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("recordPoolId", recordPoolId);
        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public RecordDepositDetailBean onGetgetRecordDepositDetailInfo(JSONObject jsonObject) {
        Gson gson = new Gson();
        RecordDepositDetailBean bean = null;
        try {
            bean = gson.fromJson(jsonObject + "", RecordDepositDetailBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 查询指定月份有回款事件的天数
     *
     * @param year  年份，不传默认为当前年份
     * @param month 月份，不传默认为当前月份
     * @return
     */
    public boolean getReturnCalendarTime(String year, String month, int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Return_Calendar_Time;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("year", year);
        params.put("month", month);
        params.put("accountType", accountType + "");

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public ReturnMonthBean onGetReturnCalendarTime(JSONObject jsonObject) {
        Gson gson = new Gson();
        ReturnMonthBean bean = null;
        try {
            bean = gson.fromJson(jsonObject + "", ReturnMonthBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 查询指定天的回款事件
     *
     * @param date 指定日期，例：20160901
     * @return
     */
    public boolean getEventByDay(String date, int accountType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Get_Event_ByDay;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();
        params.put("date", date);
        params.put("accountType", accountType + "");

        return httpClient.doPost(url, params, cmdId.ordinal(), false, false);
    }

    public ReturnEventBean onGetEventByDay(JSONObject jsonObject) {
        Gson gson = new Gson();
        ReturnEventBean bean = null;
        try {
            bean = gson.fromJson(jsonObject + "", ReturnEventBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 首页接口3.0.0
     *
     * @return
     */
    public boolean getAppmemberIndex(boolean showProgress) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_APPMEMBER_INDEX;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> params = new ArrayMap<String, String>();

        return httpClient.doPost(url, params, cmdId.ordinal(), false, showProgress);
    }

    public AppmemberIndexBean onGetAppmemberIndex(JSONObject jsonObject) {
        Gson gson = new Gson();
        AppmemberIndexBean bean = null;
        try {
            bean = gson.fromJson(jsonObject + "", AppmemberIndexBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 获取定存宝列表
     *
     * @param startPage 从start条记录开始（默认0）
     * @param pageSize  取limit条记录（默认5）
     * @param queryText 模糊查询
     * @return
     */
    public boolean getLoanSignPool(int startPage, int pageSize, String queryText) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Loan_Sign_Pool;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("start", "" + startPage);//从start条记录开始（默认0）
        param.put("limit", "" + pageSize);//取limit条记录（默认5）
        param.put("title", queryText);//模糊查询
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    public LoanSignDepositBean onGetLoanSignPool(JSONObject jsonObject) {
        Gson gson = new Gson();
        LoanSignDepositBean bean = null;
        try {
            bean = gson.fromJson(jsonObject + "", LoanSignDepositBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 定存宝产品详情
     *
     * @param mPid
     * @return
     */
    public boolean getDepositProductInfo(long mPid, String recordPoolId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Loan_Sign_Pool_Info;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("poolId", "" + mPid);
        if (!TextUtils.isEmpty(recordPoolId)) {
            param.put("recordPoolId", "" + recordPoolId);
        }
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 定存宝购买
     *
     * @param poolId         标的号
     * @param money          购买金额
     * @param user_id        用户ID号
     * @param trans_password Md5加密后传输
     * @param voucherIds
     * @param couponId
     * @param isBookInvest   为1 时是预约购买
     * @param byStagesType   所选的类型,用逗号隔开
     * @return
     */
    public boolean getDepositProductInvest(String poolId, String money, String user_id, String trans_password, String voucherIds, String couponId, String isBookInvest, String byStagesType) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Loan_Sign_Pool_Invest;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);

        Map<String, String> param = new ArrayMap<String, String>();
        param.put("poolId", poolId);
        param.put("money", money);
        param.put("user_id", user_id);
        param.put("trans_password", trans_password);
        if (voucherIds == null) {
            voucherIds = "";
        }

        param.put("vouchers", voucherIds);//
        param.put("couponId", couponId);
        param.put("isBookInvest", isBookInvest);
        param.put("byStagesType", byStagesType);

        if (!TextUtils.isEmpty(voucherIds)) {
            param.put("voucherId", "1");//代金券传1
        } else if (!TextUtils.isEmpty(couponId)) {
            param.put("voucherId", "0");//加息券传0
        }

        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 定存宝债权详情
     *
     * @return
     */
    public boolean getDepositItemInfo(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Loan_Sign_Info;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("loanId", loanId);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    public boolean getServiceTime() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_SERVICE_TIME;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 定存宝是否显示
     *
     * @return
     */
    public boolean getIsShowDeposit() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Deposit_Is_Show;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 我的存管账户
     *
     * @return
     */
    public boolean getEAccountInfo() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_E_Account;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 银行存管 - 转让规则匹配
     *
     * @return
     */
    public boolean getBankTransfeVerify(String investId, String transferMoney) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Bank_Transfer_Verify;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("investId", investId);
        param.put("transferMoney", transferMoney);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 银行存管 - 查询华兴标当前锁定金额
     *
     * @return
     */
    public boolean getBankRealTenderMoney(String loanId) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Bank_Real_Tender_Money;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("loanId", loanId);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 银行存管 - 查询华兴标当前锁定金额
     *
     * @param jsonObject
     * @return 用标的总额 - 这个值 = 可购余额
     */
    public double getOnBankRealTenderMoney(JSONObject jsonObject) {
        return jsonObject.optDouble("realTenderMoney");
    }

    /**
     * 保存自动投标设置
     *
     * @param isAutoPlank        是否开启自动投标0否 1 是
     * @param userRemainingMoney 帐户保留金额
     * @param userMaxLoanMoney   最大投资金额
     * @param loanPeriodBegin    最小借款期限
     * @param loanPeriodEnd      最大借款期限
     * @param rateBegin          最低利率，小数
     * @param rateEnd            最高利率
     * @param loanType           标的类型,多选或不限时为多条,中间用 “,”分隔.如 “1,8,10”
     * @param refundWay          还款方式，多选或不限时为多条,中间用 “,”分隔
     * @param securityLevel      风险级别
     * @param coupons            优惠券，多选或不限时为多条,中间用 “,”分隔
     * @return
     */
    public boolean getAutoInvestSetting(boolean isAutoPlank, String userRemainingMoney, String userMaxLoanMoney, String loanPeriodBegin, String loanPeriodEnd, String rateBegin, String rateEnd, String loanType, String refundWay, String securityLevel, String coupons) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Auto_Plan_Setting;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("isAutoPlank", isAutoPlank ? "1" : "0");
        param.put("userRemainingMoney", userRemainingMoney);
        param.put("userMaxLoanMoney", userMaxLoanMoney);
        param.put("loanPeriodBegin", loanPeriodBegin);
        param.put("loanPeriodEnd", loanPeriodEnd);
        param.put("rateBegin", rateBegin);
        param.put("rateEnd", rateEnd);
        param.put("loanType", loanType);
        param.put("refundWay", refundWay);
        param.put("securityLevel", securityLevel);
        param.put("coupons", coupons);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 获取自动投标设置详情
     *
     * @return
     */
    public boolean getAutoInvestSettingGet() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Auto_Plan_Setting_Get;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    public AutoInvestSettingBean getOnAutoInvestSettingGet(JSONObject jsonObject) {
        try {
            return new Gson().fromJson(jsonObject.toString(), AutoInvestSettingBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取列表tab
     *
     * @return
     */
    public boolean getLoanSignType() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Loan_Sign_Type;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 红包分享列表
     *
     * @return
     */
    public boolean getShareRedPacketList() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_SHARE_RED_PACKET_LIST;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    public boolean setShareRedPacketList() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_SHARE_RED_PACKET_LIST;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("isClick", "1");
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    public boolean getVerifyOldPhone(String phone, String smscode) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_VERIFY_LOD_PHONE;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("phone", phone);
        param.put("smscode", smscode);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    public boolean getVerifyNewPhone(String phone, String smscode) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_VERIFY_NEW_PHONE;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("phone", phone);
        param.put("smscode", smscode);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    public boolean getAgreeAutoTenderProtocol() {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_AGREE_AUTO_TENDER_PROTOCOL;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("autoTenderProtocol","1");
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    public boolean getIsWelfare(String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_IS_GET_WELFARE;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("uid", uid);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 获取所有优惠券列表
     * @param couponType 1代金券 2预约卷  其他 全部
     * @param type  1.可用代金券; 2.己使用代金券; 3.己过期代金券
     * @param pageNum
     * @return
     */
    public boolean getCouponList(int couponType, int type, int pageNum) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_COUPON_LIST;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("type", "" + type);
        param.put("couponType", "" + couponType);
        param.put("pageNum", "" + pageNum);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * APP查询弹窗内容
     * @param uid
     * @return
     */
    public boolean getQueryPopUp(String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_QUERY_POP_UP;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("uid", uid);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }
    /**
     * 启动APP查询用户投标状态
     * @param uid
     * @return
     */
    public boolean getQueryAutoPlankStatus(String uid) {
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_QUERY_AUTO_PLANK_STATUS;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("uid", uid);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 撤销自动投标授权
     * @param userId
     * @param dynamicPassword 短信验证码
     * @return
     */
    public boolean getUnAuthAutoBid(String userId, String dynamicPassword){
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_unAuthAutoBid;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("userId", userId);
        param.put("dynamicPassword", dynamicPassword);
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

    /**
     * 发送华兴短信验证码
     * @param userId
     * @param type 1：自动投标撤销  2：自动还款撤销 0：默认
     * @return
     */
    public boolean getHxSendSms(String userId, int type){
        ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_HX_SEND_SMS;
        String method = ServiceCmd.getMethodName(cmdId);
        String url = getServiceUrl(method);
        Map<String, String> param = new ArrayMap<String, String>();
        param.put("userId", userId);
        param.put("type", String.valueOf(type));
        return httpClient.doPost(url, param, cmdId.ordinal(), false, false);
    }

}
