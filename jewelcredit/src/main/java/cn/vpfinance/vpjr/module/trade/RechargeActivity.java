package cn.vpfinance.vpjr.module.trade;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yintong.pay.utils.BaseHelper;
import com.yintong.pay.utils.Constants;
import com.yintong.pay.utils.MobileSecurePayer;
import com.yintong.pay.utils.PayOrder;

import org.json.JSONObject;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.setting.BankAddActivity2;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.PayInfo;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 充值
 */
public class RechargeActivity extends BaseActivity implements OnClickListener {

    private HttpService mHttpService;

    private View mContentView = null;

    private EditText mRechargeAmount = null;
    private String cardNo = "";
    private Button submit;

    float mon = 0;

    private TextView tvRemainingMoney;

    private BankCard bankCard;

    private Handler mHandler = createHandler();
    private TextView mBankName;
    private TextView mBankAccount;
    private TextView mBankLimit;
    private ImageView mBankLogo;

    private Handler createHandler() {
        return new Handler() {
            public void handleMessage(Message msg) {
                String strRet = (String) msg.obj;
                switch (msg.what) {
                    case Constants.RQF_PAY: {
                        JSONObject objContent = BaseHelper.string2JSON(strRet);
                        String retCode = objContent.optString("ret_code");
                        String retMsg = objContent.optString("ret_msg");
                        // 先判断状态码，状态码为 成功或处理中 的需要 验签
                        if (Constants.RET_CODE_SUCCESS.equals(retCode)) {
                            // 卡前置模式返回的银行卡绑定协议号，用来下次支付时使用，此处仅作为示例使用。正式接入时去掉
//							if (pay_type_flag == 1) {
//								TextView tv_agree_no = (TextView) findViewById(R.id.tv_agree_no);
//								tv_agree_no.setVisibility(View.VISIBLE);
//								tv_agree_no.setText(objContent.optString(
//										"agreementno", ""));
//
//							}

                            DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            };
                            //okListener = null;

                            String resulPay = objContent.optString("result_pay");
                            if (Constants.RESULT_PAY_SUCCESS
                                    .equalsIgnoreCase(resulPay)) {
//								BaseHelper.showDialog(RechargeActivity.this, "提示",
//										"支付成功，交易状态码：" + retCode,
//										android.R.drawable.ic_dialog_alert, okListener);
                                BaseHelper.showDialog(RechargeActivity.this, "提示",
                                        "支付成功",
                                        android.R.drawable.ic_dialog_alert, okListener);
                                // 支付成功后续处理

                                ArrayMap<String, String> map = new ArrayMap<String, String>();
                                map.put("result", "ok");
                                map.put("type", "lianlianPay");
                                map.put("money", "" + getStatisticsMoney(mon));
                                MobclickAgent.onEvent(RechargeActivity.this, "Recharge", map);


                            } else {
                                BaseHelper.showDialog(RechargeActivity.this, "提示",
                                        retMsg + "，交易状态码:" + retCode,
                                        android.R.drawable.ic_dialog_alert);

                                ArrayMap<String, String> map = new ArrayMap<String, String>();
                                map.put("result", "exception");
                                map.put("type", "lianlianPay");
                                map.put("money", "" + getStatisticsMoney(mon));
                                map.put("msg", "" + retMsg);
                                map.put("code", "" + retCode);
                                MobclickAgent.onEvent(RechargeActivity.this, "Recharge", map);
                            }

                        } else if (Constants.RET_CODE_PROCESS.equals(retCode)) {
                            String resulPay = objContent.optString("result_pay");
                            if (Constants.RESULT_PAY_PROCESSING
                                    .equalsIgnoreCase(resulPay)) {
                                BaseHelper.showDialog(RechargeActivity.this, "提示",
                                        objContent.optString("ret_msg") + "交易状态码："
                                                + retCode,
                                        android.R.drawable.ic_dialog_alert);

                                ArrayMap<String, String> map = new ArrayMap<String, String>();
                                map.put("result", "processing");
                                map.put("type", "lianlianPay");
                                map.put("money", "" + getStatisticsMoney(mon));
                                map.put("msg", "" + retMsg);
                                map.put("code", "" + retCode);
                                MobclickAgent.onEvent(RechargeActivity.this, "Recharge", map);

                            }

                        } else {
                            BaseHelper.showDialog(RechargeActivity.this, "提示", retMsg
                                            + "，交易状态码:" + retCode,
                                    android.R.drawable.ic_dialog_alert);

                            ArrayMap<String, String> map = new ArrayMap<String, String>();
                            map.put("result", "processingError");
                            map.put("type", "lianlianPay");
                            map.put("money", "" + getStatisticsMoney(mon));
                            map.put("msg", "" + retMsg);
                            map.put("code", "" + retCode);
                            MobclickAgent.onEvent(RechargeActivity.this, "Recharge", map);
                        }
                    }
                    break;
                }
                super.handleMessage(msg);
            }
        };

    }

    private EditText money;

    private User user;

    private DaoMaster.DevOpenHelper dbHelper;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private BankCardDao bankDao;
    private UserDao userDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_recharge);
        mBankName = ((TextView) findViewById(R.id.bankName));
        mBankAccount = ((TextView) findViewById(R.id.bankAccount));
        mBankLimit = ((TextView) findViewById(R.id.bankLimit));
        mBankLogo = ((ImageView) findViewById(R.id.bankLogo));
        findViewById(R.id.bankLimitInfo).setOnClickListener(this);

        money = (EditText) findViewById(R.id.money);
        money.setFocusableInTouchMode(true);
        money.requestFocus();

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        bankDao = daoSession.getBankCardDao();
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

        if (user != null) {
            String mRealname = user.getRealName();
            String idCard = user.getIdentityCard();

//			if (TextUtils.isEmpty(mRealname) || TextUtils.isEmpty(idCard)) {
//				AlertDialogUtils.showAlertDialog(RechargeActivity.this,"您未实名认证","取消","去认证",RealnameAuthActivity.class);
//			}

//			else if (!user.getHasTradePassword()) {
//				Toast.makeText(this,"请先设置交易密码",Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent(this, PasswordChangeActivity.class);
//				intent.putExtra(PasswordChangeActivity.EXTRA_KEY_INDEX, 1);
//				startActivity(intent);
//			}
        }

        if (bankDao != null) {
            QueryBuilder<BankCard> qb = bankDao.queryBuilder();
            List<BankCard> list = qb.list();
            if (list != null && list.size() > 0) {
                bankCard = list.get(0);
            }
        }
        initView();
        mHttpService = new HttpService(this, this);

//        if (bankCard == null) {
//            AlertDialogUtils.showAlertDialog(RechargeActivity.this, "您未绑定银行卡", "取消", "去绑定", BankAddActivity2.class);
//            return;
//        }
    }

    public void onResume() {
        super.onResume();
        /*if (bankCard != null) {
            mBankName.setText(bankCard.getBankname());
            mBankAccount.setText(maskBankCardNo(bankCard.getBankAccount()));
            int res = FileUtil.getBankLogo(bankCard);
            if (res > 0)
            mBankLogo.setBackgroundResource(res);
        }*/

        if (mHttpService != null) {
            mHttpService.getBankCard(AppState.instance().getSessionCode());
        }

        if (AppState.instance().logined()) {
            if (userDao != null) {
                QueryBuilder<User> qb = userDao.queryBuilder();
                List<User> userList = qb.list();
                if (userList != null && userList.size() > 0) {
                    user = userList.get(0);
                    tvRemainingMoney.setText(String.format("%.2f 元", user.getCashBalance()));
                }
            }
        }
    }

    protected void initView() {
        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("充值").setHeadBackVisible(View.VISIBLE);
        submit = (Button) findViewById(R.id.submit);
        submit.setEnabled(false);
        submit.setOnClickListener(this);
        tvRemainingMoney = (TextView) findViewById(R.id.remaining_money);

        money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                submit.setEnabled(!TextUtils.isEmpty(s));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (user != null) {
                    String mRealname = user.getRealName();
                    String idCard = user.getIdentityCard();


//				if (TextUtils.isEmpty(mRealname) || TextUtils.isEmpty(idCard)) {
//					AlertDialogUtils.showAlertDialog(RechargeActivity.this,"您未实名认证","取消","去认证",RealnameAuthActivity.class);
//					return;
//				}

//				else if (!user.getHasTradePassword()) {
//					Toast.makeText(this,"请先设置交易密码",Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent(this, PasswordChangeActivity.class);
//					intent.putExtra(PasswordChangeActivity.EXTRA_KEY_INDEX, 1);
//					startActivity(intent);
//					return;
//				}
                }

                if (bankCard == null) {
                    AlertDialogUtils.showAlertDialog(RechargeActivity.this, "您未绑定银行卡", "取消", "去绑定", BankAddActivity2.class);
//				Toast.makeText(this,"请先绑定银行卡",Toast.LENGTH_SHORT).show();
//				startActivity(new Intent(this, BankAddActivity.class));
                    return;
                }

                String uid = AppState.instance().getSessionCode();
                String m = money.getText().toString();

                if (!TextUtils.isEmpty(m)) {
                    if (m.contains(".")) {
                        m = m.trim();
                        int dotIndex = m.indexOf(".");
                        if (m.length() - dotIndex > 3) {
                            Toast.makeText(RechargeActivity.this, "输入金额要求精确到分", Toast.LENGTH_SHORT).show();
                            money.setText(m.substring(0, dotIndex + 3));
                            return;
                        }
                    }
                    try {
                        mon = Float.parseFloat(m);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (mon >= 100) {
                    mHttpService.getPayInfo(uid, "" + mon);
                } else {
                    Toast.makeText(this, "最少充值额度为100元", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.bankLimitInfo:
                gotoWeb("/AppContent/tosupportbank","银行卡限额说明");
                break;
            default:
                break;
        }
    }

    private String maskBankCardNo(String cardNo) {
        return cardNo.substring(0, 4) + "****" + cardNo.substring(cardNo.length() - 4);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_GET_PAY_INFO_NEW.ordinal()) {
            PayInfo payinfo = mHttpService.onGetPayInfo(json);
            if (payinfo != null) {
                PayOrder order = createOrder(payinfo);
                if (order != null) {
                    String content4Pay = BaseHelper.toJSONString(order);
                    // 关键 content4Pay 用于提交到支付SDK的订单支付串，如遇到签名错误的情况，请将该信息帖给我们的技术支持
                    MobileSecurePayer msp = new MobileSecurePayer();
                    boolean bRet = msp.payAuth(content4Pay, mHandler,
                            Constants.RQF_PAY, RechargeActivity.this, false);

//                    Log.e("Test", "bRet:" + bRet);
                }
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_getBankCard.ordinal()) {
            if(json == null ) return;
            bankCard = mHttpService.onGetBankCard(this, json);
            JSONObject userbank = json.optJSONObject("userbank");
            if (userbank != null) {
                JSONObject banktype = userbank.optJSONObject("banktype");
                if (banktype != null) {

                    double singleLimit = banktype.optDouble("quickpaymentsinglelimit");
                    double dayLimit = banktype.optDouble("quickpaymentdaylimit");
                    String monthLimit = banktype.optString("quickpaymentmonthlimit");
                    String bankcode = banktype.optString("bankcode");
                    String bankimgurl = banktype.optString("bankimgurl");
                    ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + bankimgurl, mBankLogo);
//                    Log.d("aa", "onHttpSuccess: " +singleLimit+ dayLimit+monthLimit + bankcode);
                    mBankLimit.setText("充值限额 | "+ FormatUtils.formatDown2(singleLimit) +"元/笔，"+ FormatUtils.formatDown2(dayLimit) +"元/日");
                }
            }

            if (bankCard != null) {
//                mBankName.setText(bankCard.getBankname());
                mBankAccount.setText(maskBankCardNo(bankCard.getBankAccount()));
                mBankName.setText(bankCard.getBankname());

                //                int res = FileUtil.getBankLogo(bankCard);
//                if (res > 0)
//                    mBankLogo.setBackgroundResource(res);
            }else{
                AlertDialogUtils.showAlertDialog(RechargeActivity.this, "您未绑定银行卡", "取消", "去绑定", BankAddActivity2.class);
            }
        }
    }

    private PayOrder createOrder(PayInfo payinfo) {
        if (payinfo == null) {
            return null;
        }
        PayOrder order = new PayOrder();

        order.setBusi_partner(payinfo.getBusi_partner());//busi_partner
        order.setName_goods(payinfo.getName_goods());//name_goods
        order.setNotify_url(payinfo.getNotify_url());//notify_url
        order.setOid_partner(payinfo.getOid_partner());//oid_partner
        order.setAcct_name(payinfo.getAcct_name());//acct_name
        order.setUser_id(payinfo.getUser_id());//user_id
        order.setDt_order(payinfo.getDt_order());//dt_order
        order.setId_no(payinfo.getId_no());//id_no
        order.setMoney_order(payinfo.getMoney_order());//money_order
        order.setNo_order(payinfo.getNo_order());//no_order
        order.setValid_order(payinfo.getValid_order());//valid_order
        order.setRisk_item(payinfo.getRisk_item());//risk_item
        order.setCard_no(payinfo.getCard_no());//card_no
        order.setNo_agree(payinfo.getNo_agree());//no_agree
        order.setFlag_modify(payinfo.getFlag_modify());//flag_modify
        order.setSign_type(payinfo.getSign_type());//sign_type
        order.setSign(payinfo.getSign());//sign
        return order;
    }


    public static String getStatisticsMoney(double money) {
        String statistics = "<50";
        if (money <= 50) {
            statistics = "<=50";
        } else if (money <= 100) {
            statistics = "<=100";
        } else if (money < 500) {
            statistics = "100-500";
        } else if (money < 1000) {
            statistics = "500-1000";
        } else if (money < 5000) {
            statistics = "1000-5000";
        } else if (money < 10000) {
            statistics = "5000-10000";
        } else if (money <= 100000) {
            statistics = (int) money / 10000 + "0000";
        } else if (money <= 200000) {
            statistics = "100000-200000";
        } else if (money <= 300000) {
            statistics = "200000-300000";
        } else if (money <= 400000) {
            statistics = "300000-400000";
        } else if (money <= 500000) {
            statistics = "400000-500000";
        }
        if (money > 500000) {
            statistics = ">500000";
        }
        return statistics;
    }


}