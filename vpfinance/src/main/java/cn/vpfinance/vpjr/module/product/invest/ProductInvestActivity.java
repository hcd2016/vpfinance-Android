package cn.vpfinance.vpjr.module.product.invest;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.AddRateBean;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.gson.LoanProtocolBean;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.model.AddRateInfo;
import cn.vpfinance.vpjr.model.Voucher;
import cn.vpfinance.vpjr.model.VoucherArray;
import cn.vpfinance.vpjr.model.VoucherEvent;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.InvestmentRiskTipsDialog;
import cn.vpfinance.vpjr.module.dialog.RechargeCloseDialog;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.module.trade.RechargBankActivity;
import cn.vpfinance.vpjr.module.voucher.NewSelectVoucherActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/18.
 */
public class ProductInvestActivity extends BaseActivity implements View.OnClickListener {

    public static final String PRODUCT_ID = "product_id";//pid
    public static final String NAME_MONEY = "money";//投资金额
    public static final String VOUCHEREVENT = "voucherevent";//投资金额
    public static final String ADDRATEEVENT = "addrateevent";//投资金额
    @Bind(R.id.iv_fdjx)
    ImageView ivFdjx;
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.item_loan_title)
    TextView itemLoanTitle;
    @Bind(R.id.ivAllowTransfer)
    ImageView ivAllowTransfer;
    @Bind(R.id.ivProductState)
    ImageView ivProductState;
    @Bind(R.id.item_loan_rate)
    TextView itemLoanRate;
    @Bind(R.id.item_loan_rate_percent)
    TextView itemLoanRatePercent;
    @Bind(R.id.tv_addrate)
    TextView tvAddrate;
    @Bind(R.id.tv_deadline)
    TextView tvDeadline;
    @Bind(R.id.item_loan_term)
    TextView itemLoanTerm;
    @Bind(R.id.tv_money_desc)
    TextView tvMoneyDesc;
    @Bind(R.id.item_loan_totle)
    TextView itemLoanTotle;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.tv_progress_num)
    TextView tvProgressNum;
    @Bind(R.id.ll_progress_container)
    LinearLayout llProgressContainer;
    @Bind(R.id.countDown)
    MyCountDownTimer countDown;
    @Bind(R.id.rewardIv)
    ImageView rewardIv;
    @Bind(R.id.iv_home_state)
    ImageView ivHomeState;
    @Bind(R.id.rootView)
    RelativeLayout rootView;
    @Bind(R.id.tv_account_balance)
    TextView tvAccountBalance;
    @Bind(R.id.btn_recharge)
    TextView btnRecharge;
    @Bind(R.id.et_loan_amount)
    EditText etLoanAmount;
    @Bind(R.id.btn_allin)
    TextView btnAllin;
    @Bind(R.id.tv_earnings)
    TextView tvEarnings;
    @Bind(R.id.tv_discount_counts)
    TextView tvDiscountCounts;
    @Bind(R.id.line_gray)
    View lineGray;
    @Bind(R.id.mCheckBox)
    CheckBox mCheckBox;
    @Bind(R.id.tvProtocal)
    TextView tvProtocal;
    @Bind(R.id.tvProtocal2)
    TextView tvProtocal2;
    @Bind(R.id.tv_bottom_desc)
    TextView tvBottomDesc;
    //    private ActionBarLayout titleBar;
    private HttpService mHttpService;
    private FinanceProduct product = new FinanceProduct();
    //    private TextView productTitle;
//    private TextView productRate;
//    private TextView productMonth;
//    private TextView productIssueLoan;
//    private TextView productAvailMoney;
//    private TextView productRefundWay;
//    private TextView cashBalance;
    //    private User user;
    private TextInputDialogFragment tidf;
    //    private FundOverInfo fundOverInfo;
    private String mCanBuyMoney;
    private double availMoney;
    //    private EditText etRechargeMoney;
    private double mBuyMoney;
    //    private CheckBox mCheckBox;
    private String pid;
    //    private TextView tvUseVoucher;
//    private TextView tvProtocal;
//    private TextView tvProtocal2;
//    private TextView transfer_money;
//    private TextView tvCalcedMoney;
    public static final String TYPE_PRODUCT = "type_product";
    public static final String PRODUCT_TOTAL_MONEY = "product_total_money";
    public static final String PRODUCT_NATIVE_MONEY = "product_native_money";
    public static final String PRODUCT_TRANSFER_RATE = "product_transfer_rate";
    public static final String AVAIL_MONEY = "avail_money";//可投资金额
    public static final String IS_ORDER = "order";//是否是预约购买
    public static final String IPHONE = "iphone";//是否是投资送iphone7
    public static final int TYPE_TRANSFER = 1; //投资债权转让
    public static final int TYPE_CAR_PRODUCT = 2; //车贷
    public static final int TYPE_Jewelry_PRODUCT = 3; //珠宝贷

    boolean isUse = false;


    private double voucherValue = 0;
    private double voucherrate = 0;
    private int usableVoucherCount = 0;
    private String vouchers;

    private int[] vouchersArray = null;//已选优惠券id
    //    private TextView tvPredictMoney;
    private Long loanId;
    //    private ImageView ivAllowTransfer;
    private String allowTransferStr = "false";
    private Button btnInvest;
//    private LinearLayout selectVoucher;
    //    private LinearLayout ll_acitvity;
//    private LinearLayout ll_normal;
//    private RelativeLayout rl_iphone_submit;
    //    private EditText etRechargeMoney_activity;
//    private TextView tvPredictMoney_activity;
//    private TextView allRecharge_activity;
    private Intent intent;
    //    private ImageView mIsAllowTrip;
    private Integer productIsAllowTrip;
    private long loanType;
    private int mAddRatePeriod;
    private double mValue;
    private String mType;
    private String couponId;
    private double mTotalMoney;
    private double mNativeMoney;
    private int mType_product;
    private int mTotalCount;
    private VoucherEvent mVoucherEvent;
    private int mTemp = -1;
    private AddRateInfo mAddRateInfo;
    private boolean isOrder = false;
    //    private TextView tv_buy;
//    private TextView tv_userOrder;
    private String mIPhone;
    private List<LoanProtocolBean.DataEntity> mData;
    private double mRate;
    private double mTransfer_rate;

    private Dialog investLoadingDialog;
    private int accountType = Constant.AccountLianLain;
    private boolean isOpen;
    private boolean isInvestTag = false;
    private UserInfoBean userInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_product_invest_new);
        ButterKnife.bind(this);
        User user = DBUtils.getUser(this);
        if (user == null) {
            Toast.makeText(this, "请登录.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        isOpen = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT, false);

        intent = getIntent();
        if (intent != null) {
            mType_product = intent.getIntExtra(TYPE_PRODUCT, 0);
            isOrder = intent.getBooleanExtra(IS_ORDER, false);
            mIPhone = intent.getStringExtra(IPHONE);
            accountType = intent.getIntExtra(Constant.AccountType, Constant.AccountLianLain);
        }

//        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.reset().setTitle("我要出借").setHeadBackVisible(View.VISIBLE);
        mHttpService = new HttpService(this, this);

//        tvUseVoucher = (TextView) findViewById(R.id.tvUseVoucher);
//        tvCalcedMoney = (TextView) findViewById(R.id.tvCalcedMoney);
//        tvPredictMoney = ((TextView) findViewById(R.id.tvPredictMoney));
//        ivAllowTransfer = ((ImageView) findViewById(R.id.ivAllowTransfer));
//        mIsAllowTrip = ((ImageView) findViewById(R.id.isAllowTrip));
//        selectVoucher = ((LinearLayout) findViewById(R.id.selectVoucher));
//        ll_acitvity = ((LinearLayout) findViewById(R.id.ll_acitvity));
//        ll_normal = ((LinearLayout) findViewById(R.id.ll_normal));
//        rl_iphone_submit = ((RelativeLayout) findViewById(R.id.rl_iphone_submit));
//        etRechargeMoney_activity = ((EditText) findViewById(R.id.etRechargeMoney_activity));
//        tvPredictMoney_activity = ((TextView) findViewById(R.id.tvPredictMoney_activity));
//        allRecharge_activity = ((TextView) findViewById(R.id.allRecharge_activity));
//        allRecharge_activity.setOnClickListener(this);
//        rl_iphone_submit.setOnClickListener(this);
//        selectVoucher.setOnClickListener(this);

//        tvProtocal = (TextView) findViewById(R.id.tvProtocal);
//        tvProtocal2 = (TextView) findViewById(R.id.tvProtocal2);
        tvProtocal.setOnClickListener(this);
        tvProtocal2.setOnClickListener(this);
//        transfer_money = (TextView) findViewById(R.id.transfer_money);
//        tv_userOrder = (TextView) findViewById(R.id.tv_userOrder);
//        tv_buy = (TextView) findViewById(R.id.tv_buy);
        btnInvest = ((Button) findViewById(R.id.invest));

//        mHttpService.getFundOverInfo("" + user.getUserId(),accountType);

//        if ("1".equals(mIPhone) && !isOrder) {
//            //投资送iphone活动
////            ll_acitvity.setVisibility(View.VISIBLE);
//            ll_normal.setVisibility(View.GONE);
//        } else {
//        ll_normal.setVisibility(View.VISIBLE);
//            ll_acitvity.setVisibility(View.GONE);
//        }
        if (isOrder) {//是预售
            titleBar.reset().setTitle("立即预约").setHeadBackVisible(View.VISIBLE);
            btnInvest.setText("立即预约");
            countDown.setVisibility(View.VISIBLE);
            llProgressContainer.setVisibility(View.GONE);
            tvMoneyDesc.setText("预售余额");

//            tv_userOrder.setVisibility(View.VISIBLE);
//            Utils.setTwoTextColor("您将使用1张预约券进行预约", "1", Color.RED, tv_userOrder);
        }else {
            countDown.setVisibility(View.GONE);
            llProgressContainer.setVisibility(View.VISIBLE);
            tvMoneyDesc.setText("可购余额");
        }
        if (TextUtils.isEmpty(intent.getStringExtra("pid"))) {
            App myApp = (App) getApplication();
            //            Log.i("aaa","myapp:"+myApp.currentPid);
            mHttpService.getFixProduct(myApp.currentPid, "" + 0);
            mHttpService.loanVoucherIsUse(myApp.currentPid, "" + user.getUserId());
            mHttpService.getVoucherlist("" + 1, myApp.currentPid);
        } else {
            pid = intent.getStringExtra("pid");
            mHttpService.getFixProduct(pid, "" + 0);
            mHttpService.loanVoucherIsUse(pid, "" + user.getUserId());
            mHttpService.getVoucherlist("" + 1, pid);//获取代金券
            mHttpService.getAddRateInvest(1 + "", pid, "");//投资可用加息券
            mHttpService.getProtocol(pid + "");//协议
        }


        if (intent != null && mType_product == TYPE_TRANSFER) {
            mTotalMoney = intent.getDoubleExtra(PRODUCT_TOTAL_MONEY, 0);
            mNativeMoney = intent.getDoubleExtra(PRODUCT_NATIVE_MONEY, 0);
            mTransfer_rate = intent.getDoubleExtra(PRODUCT_TRANSFER_RATE, 0);
//            ((LinearLayout) findViewById(R.id.transfer_info)).setVisibility(View.GONE);
//            ((LinearLayout) findViewById(R.id.transfer_des)).setVisibility(View.VISIBLE);
//            transfer_money.setVisibility(View.VISIBLE);
//            ((TextView) findViewById(R.id.tvMonthInfo)).setText("剩余期限");
//            selectVoucher.setVisibility(View.GONE);
//            findViewById(R.id.devider).setVisibility(View.GONE);
        }

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        user = DBUtils.getUser(this);
//        if (user != null) {
//            mHttpService.getUserInfoBank();
////            mHttpService.getFundOverInfo("" + user.getUserId(),accountType);
//        }
//        if (accountType == Constant.AccountBank) {
//            mHttpService.getUserInfoBank();
//        } else {
        mHttpService.getUserInfo();
//        }
    }

    protected void initView() {
//        productTitle = (TextView) findViewById(R.id.productTitle);
//        productRate = (TextView) findViewById(R.id.productRate);
//        productMonth = (TextView) findViewById(R.id.productMonth);
//        productIssueLoan = (TextView) findViewById(R.id.productIssueLoan);
//        productAvailMoney = (TextView) findViewById(R.id.productAvailMoney);
//        productRefundWay = (TextView) findViewById(R.id.productRefundWay);
//        cashBalance = (TextView) findViewById(R.id.cashBalance);
//        etRechargeMoney = (EditText) findViewById(R.id.etRechargeMoney);
//        mCheckBox = (CheckBox) findViewById(R.id.mCheckBox);
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBox.isChecked()) {
                    btnInvest.setEnabled(true);
                } else {
                    btnInvest.setEnabled(false);
                }
            }
        });

//        if ("1".equals(mIPhone) && !isOrder) {
//            setEditTextListener(etRechargeMoney_activity);
//        } else {
        setEditTextListener(etLoanAmount);
//        }

        findViewById(R.id.ll_change_checkbox).setOnClickListener(this);
//        findViewById(R.id.recharge).setOnClickListener(this);
//        findViewById(R.id.allRecharge).setOnClickListener(this);
        btnInvest.setOnClickListener(this);

        if (getIntent().getIntExtra("isGraceDays", 0) > 0) {//是浮动计息
            ivFdjx.setVisibility(View.VISIBLE);
        } else {
            ivFdjx.setVisibility(View.GONE);
        }
    }

    private void setEditTextListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mType_product == TYPE_TRANSFER) {//债券转让
                    String my = editable.toString();
                    double m = 0;
                    if (my != null) {
                        try {
                            m = Double.parseDouble(my);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
//                    if (mTotalMoney != 0) {
//                        double v = m * mNativeMoney / mTotalMoney;
//                        transfer_money.setText("购买本金：" + FormatUtils.formatDown(v) + "元");
//                    }
                }
                if ("1".equals(mType)) {
                    double calcedMoney = 0;
                    if (tvDiscountCounts != null) {
                        String my = editable.toString();
                        double m = 0;
                        if (my != null) {
                            try {
                                m = Double.parseDouble(my);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        if (voucherValue > 0 && voucherrate > 0 && m > 0) {
                            calcedMoney = m * voucherrate;
                            calcedMoney = Math.min(calcedMoney, voucherValue);
                            tvDiscountCounts.setTextColor(getResources().getColor(R.color.main_color));
                            tvDiscountCounts.setText("已抵扣代金券" + String.format("%.2f", calcedMoney) + "元");
                        }
                    }
                } else if ("2".equals(mType)) {
                    String etContent = editable.toString();
                    if (etContent.length() >= 3) {
                        mHttpService.getAddRateIncome(etContent, mValue + "", mAddRatePeriod + "");
                    }
                }
                /*预计收益*/
                String etContent = editable.toString();
                if (etContent.length() >= 3) {
                    mHttpService.getPredictMoney("" + loanId, etContent, false);
//                    String predictMoney = calcPredictMoney(etContent);
//                    if ("1".equals(mIPhone) && !isOrder) {
//                        tvPredictMoney_activity.setText(predictMoney + "元");
//                    } else {
//                        tvPredictMoney.setText(predictMoney + "元");
//                    }
                } else {
//                    if ("1".equals(mIPhone) && !isOrder) {
//                        tvPredictMoney_activity.setText("0.00元");
//                    } else {
                    tvEarnings.setText("0.00元");
//                    }
                }
            }
        });
    }

    /*private String calcPredictMoney(String str) {
        double predictMoney = 0;
        *//*  double income = 0d;
            BigDecimal tenderM = new BigDecimal("20000");
            BigDecimal rate = new BigDecimal("0.078");
            double value = tenderM.multiply(rate).divide(new BigDecimal("360"),10,BigDecimal.ROUND_HALF_EVEN ).multiply(new BigDecimal("75")).doubleValue();

            DecimalFormat formater = new DecimalFormat("######0.00");
            formater.setMaximumFractionDigits(2);
            formater.setGroupingSize(0);
            formater.setRoundingMode(RoundingMode.FLOOR);

            System.out.print(formater.format(value));
*//*
        if (product != null) {
            try {
                BigDecimal moneyBD = new BigDecimal(str);
                long month = product.getMonth();
//                double money = Double.parseDouble(str);
                if (product.getLoanType() == 2) {//天标
                    double rate = mType_product == TYPE_TRANSFER ? mTransfer_rate : product.getRate();
                    BigDecimal rateBD = new BigDecimal(rate);

                    double value = moneyBD.multiply(rateBD).divide(new BigDecimal("360"),10,BigDecimal.ROUND_HALF_EVEN ).multiply(new BigDecimal(month)).doubleValue();
//                    predictMoney = money * rate / 360 * month;
                    return format(value);
                } else {//普通标
                    if (product.getLoanType() == 6 && product.getRefundWay() == 3){
                        double rate = mType_product == TYPE_TRANSFER ? mTransfer_rate : product.getRate();
                        BigDecimal rateBD = new BigDecimal(rate);
//                        predictMoney = money * rate / 360 * month;
                        double value = moneyBD.multiply(rateBD).divide(new BigDecimal("360"),10,BigDecimal.ROUND_HALF_EVEN ).multiply(new BigDecimal(month)).doubleValue();
                        return format(value);
                    }else {
                        double rate = mType_product == TYPE_TRANSFER ? mTransfer_rate : product.getRate();
                        BigDecimal rateBD = new BigDecimal(rate);
//                        long month = product.getMonth();
//                        predictMoney = money * rate / 12 * month;
                        double value = moneyBD.multiply(rateBD).divide(new BigDecimal("12"),10,BigDecimal.ROUND_HALF_EVEN ).multiply(new BigDecimal(month)).doubleValue();
                        return format(value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return FormatUtils.formatDown2(predictMoney);
    }*/

    private String format(double value) {
        DecimalFormat formater = new DecimalFormat("######0.00");
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
        return formater.format(value);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Bank_Real_Tender_Money.ordinal()) {
            double v = mHttpService.getOnBankRealTenderMoney(json);
            if (product.getIssueLoan() > 0) {
                if (isOrder) {
                    availMoney = product.getIssueLoan() * product.getBookPercent() - v;
                    if(availMoney >= 10000 ) {
                        itemLoanTotle.setText(FormatUtils.formatDown2(availMoney/10000) + "万");
                    }else {
                        itemLoanTotle.setText(FormatUtils.formatDown2(availMoney) + "元");
                    }
                } else {
                    availMoney = product.getIssueLoan() - v;
                    if(availMoney >= 10000 ) {
                        itemLoanTotle.setText(FormatUtils.formatDown2(availMoney/10000) + "万");
                    }else {
                        itemLoanTotle.setText(FormatUtils.formatDown2(availMoney) + "元");
                    }
                }
            }
            //设置进度
            if(!isOrder) {
                //进度
                double v1 = (v / product.getIssueLoan()) * 100;
                progress.setProgress((int) (v1));
                tvProgressNum.setText(FormatUtils.formatDown3(v1)+"%");
            }

            if (isInvestTag) {
                isInvestTag = false;
                checkInvest();
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            userInfoBean = mHttpService.onGetUserInfo(json);
            if (userInfoBean != null) {
                isOpen = "1".equals(userInfoBean.isOpen) ? true : false;

                /*可用余额*/
                mCanBuyMoney = userInfoBean.cashBalance;
                try {
                    double v = Double.parseDouble(mCanBuyMoney);
                    tvAccountBalance.setText(FormatUtils.formatDown3(v) + "元");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_LOAN_PROTOCOL.ordinal()) {
            LoanProtocolBean loanProtocolBean = mHttpService.onGetgetProtocol(json);
            if (loanProtocolBean != null) {
                mData = loanProtocolBean.getData();
                if (mData != null && mData.size() != 0) {
                    LoanProtocolBean.DataEntity dataEntity = mData.get(0);
                    if (dataEntity != null) {
                        tvProtocal.setText(dataEntity.getTitle());
                    }
                }
                if (mData != null && mData.size() == 2) {
                    LoanProtocolBean.DataEntity dataEntity1 = mData.get(1);
                    if (dataEntity1 != null) {
                        tvProtocal2.setVisibility(View.VISIBLE);
                        tvProtocal2.setText(dataEntity1.getTitle());
                    }
                }
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_Addrate_Ticket_invest.ordinal()) {
            AddRateBean addRateBean = mHttpService.onGetAddRateInvest(json);
            if (addRateBean != null) {
                mTotalCount = addRateBean.getTotalCount();
                tvDiscountCounts.setText(mTotalCount + usableVoucherCount + "张可用");
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_Addrate_Income.ordinal()) {
            String resultMoney = json.optString("resultMoney");
            try {
                double v = Double.parseDouble(resultMoney);
                tvDiscountCounts.setTextColor(getResources().getColor(R.color.main_color));
                tvDiscountCounts.setText("预计加息" + FormatUtils.formatDown(v) + "元");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_PredictMoney.ordinal()) {
            if (json != null) {
                try {
                    float income = (float) json.optDouble("income");
//                    if ("1".equals(mIPhone) && !isOrder) {
//                        tvPredictMoney_activity.setText(FormatUtils.formatAbout(income) + "元");
//                    } else {
                    tvEarnings.setText(FormatUtils.formatAbout(income) + "元");
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo.ordinal()) {
            if (json != null) {
                mHttpService.onGetProductDetail(this, json, product);

                if (!TextUtils.isEmpty(product.getAllowTransfer()) && "true".equals(product.getAllowTransfer())) {
                    ivAllowTransfer.setVisibility(View.VISIBLE);
                    allowTransferStr = "true";
                }
                productIsAllowTrip = product.getIsAllowTrip();
//                if (!TextUtils.isEmpty(productIsAllowTrip + "") && productIsAllowTrip == 1) {
//                    mIsAllowTrip.setVisibility(View.VISIBLE);
//                } else {
//                    mIsAllowTrip.setVisibility(View.GONE);
//                }
                loanId = product.getPid();

                itemLoanTitle.setText(product.getLoanTitle() == null ? "" : product.getLoanTitle());//产品标题

                mRate = product.getRate();
                String rate = String.format("%.1f", mRate * 100);
                itemLoanRate.setText(rate);//年利率

                loanType = product.getLoanType();
                if (loanType == 2) {
                    itemLoanTerm.setText(product.getMonth() + "天");
                } else {
                    itemLoanTerm.setText(product.getMonth() + "个月");
                }


//                if (intent != null && intent.getIntExtra(TYPE_PRODUCT, 0) == TYPE_TRANSFER) {
//                    productIssueLoan.setText(String.format("%.2f", product.getIssueLoan()));
//                } else {
//                    productIssueLoan.setText(product.getIssueLoan() / 10000 + "万");
//                }

                if (isOrder) {
                    tvMoneyDesc.setText("预售余额");
                    countDown.setCountDownTime(this,Long.parseLong(product.getPublishTime()));
                    countDown.setOnFinishListener(new MyCountDownTimer.onFinish() {
                        @Override
                        public void finish() {
                            mHttpService.getFixProduct(pid, "" + 0);
                        }
                    });

                    if (accountType == Constant.AccountLianLain) {
                        availMoney = product.getIssueLoan() * product.getBookPercent() - product.getTotal_tend_money();
                        if(availMoney >= 10000 ) {
                            itemLoanTotle.setText(FormatUtils.formatDown2(availMoney/10000) + "万");
                        }else {
                            itemLoanTotle.setText(FormatUtils.formatDown2(availMoney) + "元");
                        }
                    } else {
                        mHttpService.getBankRealTenderMoney("" + loanId);
                    }
                } else {

                    if (accountType == Constant.AccountLianLain) {
                        availMoney = product.getIssueLoan() - product.getTotal_tend_money();
                        if(availMoney >= 10000 ) {
                            itemLoanTotle.setText(FormatUtils.formatDown2(availMoney/10000) + "万");
                        }else {
                            itemLoanTotle.setText(FormatUtils.formatDown2(availMoney) + "元");
                        }
                    } else {
                        mHttpService.getBankRealTenderMoney("" + loanId);
                    }


                }
//                productRefundWay.setText(product.getRefundWay() == 1 ? getString(R.string.refundState1) :
//                        (product.getRefundWay() == 2 ? getString(R.string.refundState2) : getString(R.string.refundState3)));

                //1质押，2保证，3抵押，4信用，5实地
//                ImageView imageView = (ImageView) findViewById(R.id.ivProductState);
                Common.productSubType(this, ivProductState, (int) product.getSubType());
            }
        }
//        else if (reqId == ServiceCmd.CmdId.CMD_FundOverView.ordinal()) {
//            fundOverInfo = mHttpService.onGetFundOverInfo(json);1
//            /*可用余额*/
//            mCanBuyMoney = fundOverInfo.getCashBalance();
//            try {
//                double v = Double.parseDouble(mCanBuyMoney);
//                cashBalance.setText(FormatUtils.formatDown3(v) + "元");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        else if (reqId == ServiceCmd.CmdId.CMD_VOUCHERLIST_V2.ordinal()) {
            if (isUse) {
                return;
            }
            if (json != null) {
                VoucherArray voucherArray = mHttpService.onVoucherArray(json);
                ArrayList<Voucher> voucherList = voucherArray.getVoucherList();
                if (voucherList != null && voucherList.size() != 0) {
                    usableVoucherCount = voucherArray.useable;
                } else {
                    usableVoucherCount = 0;
                }
                tvDiscountCounts.setText(mTotalCount + usableVoucherCount + "张可用");
            }
        }
        /*else if (reqId == ServiceCmd.CmdId.CMD_PLANK.ordinal()) {
            if (json != null) {

                if (investLoadingDialog != null){
                    investLoadingDialog.dismiss();
                }

                ArrayMap<String, String> map = new ArrayMap<String, String>();
                map.put("type", "Regular");
                map.put("money", "" + RechargeActivity.getStatisticsMoney(mBuyMoney));

                BuyResult res = mHttpService.onPlankFinish(json,false);
                if (res != null) {
                    String redPacketState = json.optString(ProductInvestSuccessActivity.REDPACKETSTATE);
                    //                    String isOneYear = json.optString("isAnniverary");//周年庆活动
                    //                    String drawTime = json.optString("drawTime");
                    //                    String doubleFestival = json.optString("doubleFestival");//中秋国庆 双节活动
                    //                    String cashTime = json.optString("cashtime");//中秋国庆 双节活动
                    int status = res.getStatus();
                    map.put("status", "" + status);
                    String desc = res.getStatusDesc();
                    if ("投标成功".equals(desc)) {
                        if (tidf != null) {
                            tidf.dismiss();
                        }

                        String money = null;
                        if ("1".equals(mIPhone) && !isOrder) {
                            money = etRechargeMoney_activity.getText().toString();
                        } else {
                            money = etRechargeMoney.getText().toString();
                        }
                        //
                        Intent intent = new Intent(this, ProductInvestSuccessActivity.class);
                        //                        intent.putExtra(ProductInvestSuccessActivity.DRAWTIME, drawTime);
                        intent.putExtra(ProductInvestSuccessActivity.DRAWTIME, res.getCashTime());
                        //                        intent.putExtra(ProductInvestSuccessActivity.STATE, isOneYear);
                        intent.putExtra(ProductInvestSuccessActivity.STATE, res.getDoubleFestival());
                        intent.putExtra(ProductInvestSuccessActivity.IMAGEURL, res.getImageUrl());
                        intent.putExtra(ProductInvestSuccessActivity.ACTIVITYURL, res.getActivityUrl());
                        intent.putExtra(ProductInvestSuccessActivity.INVESTMONEY, String.format("%.2f", mBuyMoney));
                        intent.putExtra(ProductInvestSuccessActivity.PID, "" + pid);
                        intent.putExtra(ProductInvestSuccessActivity.REDPACKETSTRURL, "" + res.getRedpacketstrurl());
                        intent.putExtra(ProductInvestSuccessActivity.REDPACKETCOUNT, "" + res.getRedPacketCount());
                        intent.putExtra(ProductInvestSuccessActivity.ALLOW_TRANSFER, "" + allowTransferStr);
                        intent.putExtra(ProductInvestSuccessActivity.ISALLOWTRIP, "" + productIsAllowTrip);
                        intent.putExtra(ProductInvestSuccessActivity.PRODUCT_TYPE, loanType);
                        if (!TextUtils.isEmpty(redPacketState)) {
                            intent.putExtra(ProductInvestSuccessActivity.REDPACKETSTATE, redPacketState);
                        }
                        startActivity(intent);
                        finish();
                    }

                    if (!"投标成功".equals(desc)) {
                        Utils.Toast(this, desc);
                    }
                }
                MobclickAgent.onEvent(this, "Buy", map);
            }
        }*/
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (reqId == ServiceCmd.CmdId.CMD_PLANK.ordinal()) {
            if (investLoadingDialog != null) {
                investLoadingDialog.dismiss();
                Utils.Toast(this, "网络不给力 ，请稍后重试");
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.SWITCH_TAB_NUM, 1);
                gotoActivity(intent);
            }
        }
    }

    private void allRecharge() {
        float canBuyMoney = 0;
        try {
            canBuyMoney = Float.parseFloat(mCanBuyMoney);
//            if ("1".equals(mIPhone) && !isOrder) {
//                if (availMoney < canBuyMoney) {
//                    etRechargeMoney_activity.setText(String.format("%.2f", availMoney));
//                } else {
//                    etRechargeMoney_activity.setText(mCanBuyMoney);
//                }
//            } else {
            if (availMoney < canBuyMoney) {
                etLoanAmount.setText(String.format("%.2f", availMoney));
            } else {
                etLoanAmount.setText(mCanBuyMoney);
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvProtocal:
                if (mData != null && mData.size() != 0) {
                    LoanProtocolBean.DataEntity dataEntity = mData.get(0);
                    if (dataEntity != null) {
                        gotoWeb(dataEntity.getUrl(), dataEntity.getTitle());
                    }
                }
                break;
            case R.id.tvProtocal2:
                if (mData != null && mData.size() == 2) {
                    LoanProtocolBean.DataEntity dataEntity = mData.get(1);
                    if (dataEntity != null) {
                        gotoWeb(dataEntity.getUrl(), dataEntity.getTitle());
                    }
                }
                break;
            case R.id.allRecharge_activity:
                allRecharge();
                break;
            case R.id.recharge:
                if (accountType == Constant.AccountLianLain) {
                    //充值入口
                    boolean isAllowRecharge = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_ALLOW_RECHARGE);
                    if (isAllowRecharge) {
                        AlertDialogUtils.confirmGoRecharg(this);
                    } else {
                        new RechargeCloseDialog().show(getFragmentManager(), "RechargeCloseDialog");
                    }
                } else if (accountType == Constant.AccountBank) {
                    gotoActivity(RechargBankActivity.class);
                }
                break;
            case R.id.allRecharge:
                allRecharge();
                break;
            case R.id.invest:
                if (accountType == Constant.AccountBank) {
                    isInvestTag = true;
                    mHttpService.getBankRealTenderMoney("" + loanId);
                } else {
                    checkInvest();
                }
                break;
            case R.id.ll_change_checkbox:
                mCheckBox.setChecked(!mCheckBox.isChecked());
                if (mCheckBox.isChecked()) {
                    btnInvest.setEnabled(true);
                } else {
                    btnInvest.setEnabled(false);
                }
                break;
            case R.id.selectVoucher:
                if (mTotalCount + usableVoucherCount > 0) {
                    double moneyValue = 0;
//                    String money = etRechargeMoney.getText().toString();
                    String money = null;
//                    if ("1".equals(mIPhone) && !isOrder) {
//                        money = etRechargeMoney_activity.getText().toString();
//                    } else {
                    money = etLoanAmount.getText().toString();
//                    }
                    if (!TextUtils.isEmpty(money)) {
                        try {
                            moneyValue = Double.parseDouble(money);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    if (moneyValue < 0.00) {
                        Toast.makeText(ProductInvestActivity.this, "请输入出借金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(this, NewSelectVoucherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(VOUCHEREVENT, mVoucherEvent);
                    bundle.putParcelable(ADDRATEEVENT, mAddRateInfo);
                    bundle.putString(PRODUCT_ID, product.getPid() + "");
                    bundle.putDouble(NAME_MONEY, moneyValue);
                    intent.putExtras(bundle);
//                     intent.putExtra(PRODUCT_ID, product.getPid() + "");
//                    intent.putExtra(NAME_MONEY, moneyValue);

                    startActivity(intent);
                }
        }
    }

    public void onEventMainThread(VoucherEvent event) {
        if (isFinishing()) return;
        if (event == null) return;
        mAddRateInfo = null;
        couponId = "";
        mVoucherEvent = event;
        mType = event.getType();
        if ("1".equals(mType)) {//代金券页面过来
            voucherValue = event.getVoucherValue();
            double calcedMoney = event.getCalcedMoney();
            voucherrate = event.getVoucherrate();
            tvDiscountCounts.setTextColor(getResources().getColor(R.color.main_color));
            tvDiscountCounts.setText("已抵扣" + FormatUtils.formatDown(calcedMoney) + "元");
            //                    tvCalcedMoney.setText("已抵扣" + String.format("%.2f",calcedMoney) + "元");
            vouchersArray = event.getVouchersArray();
            if (vouchersArray != null) {
                StringBuilder sb = new StringBuilder();
                for (int v : vouchersArray) {
                    sb.append(v).append(',');
                    //Log.e(TAG, "v:" + v);
                }
                if (sb.length() > 0)
                    this.vouchers = sb.substring(0, sb.length() - 1);
                else {
                    this.vouchers = sb.toString();
                }
            } else {
                vouchers = null;
            }
        }
    }

    public void onEventMainThread(AddRateInfo event) {
        if (isFinishing()) return;
        if (event == null) return;
        mVoucherEvent = null;
        vouchers = "";
        mType = event.getType();//加息券过来
        mAddRateInfo = event;
        tvDiscountCounts.setTextColor(getResources().getColor(R.color.main_color));
        tvDiscountCounts.setText("预计加息" + FormatUtils.formatDown(event.getAddRateMoey()) + "元");
        mValue = event.getValue();
        mTemp = event.getTemp();
        mAddRatePeriod = event.getAddRatePeriod();
        int addRateId = event.getAddRateId();
        if (addRateId >= 0) {
            couponId = addRateId + "";
        } else {
            couponId = "";
        }
    }

    /**
     * @param val1
     * @param val2
     * @return 前者大于后者返回1，等于0，小于-1
     */
    public static int doubleMoneyCompare(double val1, double val2) {
        String str1 = String.format("%.2f", val1);
        String str2 = String.format("%.2f", val2);

        BigDecimal bd1 = new BigDecimal(str1);
        BigDecimal bd2 = new BigDecimal(str2);

        return bd1.compareTo(bd2);
    }

    private void checkInvest() {
        if (!AppState.instance().logined()) {
            startActivity(new Intent(ProductInvestActivity.this, LoginActivity.class));
            return;
        }
        String money;
//        if ("1".equals(mIPhone) && !isOrder) {
//            money = etRechargeMoney_activity.getText().toString();
//        } else {
        money = etLoanAmount.getText().toString();
//        }
//        if (TextUtils.isEmpty(money)){
//            money = "0";
//        }
        if (TextUtils.isEmpty(money)) {
            Toast.makeText(ProductInvestActivity.this, "请输入购买金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if (money.contains(".")) {
            money = money.trim();
            int dotIndex = money.indexOf(".");
            if (money.length() - dotIndex > 3) {
                Toast.makeText(ProductInvestActivity.this, "输入金额要求精确到分", Toast.LENGTH_SHORT).show();
//                if ("1".equals(mIPhone) && !isOrder) {
//                    etRechargeMoney_activity.setText(money.substring(0, dotIndex + 3));
//                } else {
                etLoanAmount.setText(money.substring(0, dotIndex + 3));
//                }
                return;
            }
        }
        try {
            mBuyMoney = Double.parseDouble(money);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (mBuyMoney <= 0) {
            Toast.makeText(ProductInvestActivity.this, "请输入正确的购买金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if (availMoney < 100) {
            /*默认全部买完*/
            if (doubleMoneyCompare(mBuyMoney, availMoney) == 1) {
                String tmp = String.format("%.2f", availMoney);
                Toast.makeText(ProductInvestActivity.this, "可购份额不足请重新输入", Toast.LENGTH_SHORT).show();//
//                if ("1".equals(mIPhone) && !isOrder) {
//                    etRechargeMoney_activity.setText(tmp);
//                } else {
                etLoanAmount.setText(tmp);
//                }
                return;
            }
            if (doubleMoneyCompare(mBuyMoney, availMoney) == -1) {
                String tmp = String.format("%.2f", availMoney);
                Toast.makeText(ProductInvestActivity.this, "输入金额不低于剩余额度请重新输入", Toast.LENGTH_SHORT).show();//+tmp+"元"
//                if ("1".equals(mIPhone) && !isOrder) {
//                    etRechargeMoney_activity.setText(tmp);
//                } else {
                etLoanAmount.setText(tmp);
//                }
                return;
            }
        } else {
            if (mBuyMoney < 100) {
                Toast.makeText(ProductInvestActivity.this, "100元起投请重新输入", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (doubleMoneyCompare(mBuyMoney, availMoney) == 1) {
            Toast.makeText(ProductInvestActivity.this, "输入的购买金额大于该产品的可购金额", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double v = Double.parseDouble(mCanBuyMoney);
            if (mBuyMoney > v) {
                if (accountType == Constant.AccountLianLain) {
                    AlertDialogUtils.showRechargCloseDialog(this, "温馨提示", "账户余额不足，请先充值", "取消", "充值");
                } else if (accountType == Constant.AccountBank) {
                    AlertDialogUtils.showAlertDialog(this, "温馨提示", "账户余额不足，请先充值", "取消", "充值", RechargBankActivity.class);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userInfoBean.riskLevelMoney != null) {
            Float riskLevelMoney = Float.parseFloat(userInfoBean.riskLevelMoney);
            if (isOpen) {
                if (riskLevelMoney != 0 && Float.parseFloat(etLoanAmount.getText().toString()) > riskLevelMoney) {//输入金额大于限制金额
                    InvestmentRiskTipsDialog investmentRiskTipsDialog = new InvestmentRiskTipsDialog(this);
                    switch (userInfoBean.riskLevel) {
                        case 1:
                            investmentRiskTipsDialog.setTvDesc1Content("您的风险评估级别为“保守型”,");
                            investmentRiskTipsDialog.setTvDesc2Content("建议单笔金额不超过" + riskLevelMoney + "元");
                            break;
                        case 2:
                            investmentRiskTipsDialog.setTvDesc1Content("您的风险评估级别为“稳健型”,");
                            investmentRiskTipsDialog.setTvDesc2Content("建议单笔金额不超过" + riskLevelMoney + "元");
                            break;
                    }
                    investmentRiskTipsDialog.setCancleText("返回修改");
                    investmentRiskTipsDialog.setOnConfimClickListner(new InvestmentRiskTipsDialog.OnConfimClickListner() {
                        @Override
                        public void onConfimClick() {//继续投资
                            //投资
                            if (accountType == Constant.AccountBank) {
                                investBank();
                            } else if (accountType == Constant.AccountLianLain) {
                                investLianLian();
                            }
                        }
                    });
                    final User user = DBUtils.getUser(this);
                    investmentRiskTipsDialog.setOnCheckClickListner(new InvestmentRiskTipsDialog.OnCheckClickListner() {
                        @Override
                        public void onCheckClick() {//查看原因
                            gotoWeb("/h5/help/riskCheckReason?userId=" + user.getUserId(), "查看原因");
                        }
                    });
                    investmentRiskTipsDialog.show();
                } else {
                    //投资
                    if (accountType == Constant.AccountBank) {
                        investBank();
                    } else if (accountType == Constant.AccountLianLain) {
                        investLianLian();
                    }
                }
            }
        } else {
            Utils.Toast(this, "请先开通存管账户");
        }

    }

    private void investLianLian() {
        User user = DBUtils.getUser(this);
        if (user != null) {
            if (!user.getHasTradePassword()) {
                Intent intent = new Intent(ProductInvestActivity.this, PasswordChangeActivity.class);
                intent.putExtra(PasswordChangeActivity.EXTRA_KEY_INDEX, 1);
                AlertDialogUtils.showAlertDialog(ProductInvestActivity.this, "您未设置交易密码", "取消", "去设置", intent);
                return;
            }
        }
        tidf = TextInputDialogFragment.newInstance("交易密码", "请输入交易密码", true);
        tidf.setOnTextConfrimListener(new TextInputDialogFragment.onTextConfrimListener() {

            @Override
            public boolean onTextConfrim(String value) {
                if (value != null) {
                    if (product != null) {
                        //弹出dialog防止再次点击请求
                        investLoadingDialog = AlertDialogUtils.showInvestLoadingDialog(ProductInvestActivity.this);

                        String uid = AppState.instance().getSessionCode();
                        Md5Algorithm md5 = Md5Algorithm.getInstance();
                        value = md5.md5Digest(value.getBytes());
                        if (!isOrder) {
                            mHttpService.plank("" + product.getPid(), "" + mBuyMoney, uid, value, mCheckBox.isChecked(), vouchers, couponId, "0");
                        } else {
                            mHttpService.plank("" + product.getPid(), "" + mBuyMoney, uid, value, mCheckBox.isChecked(), vouchers, couponId, "1");
                        }
                        tidf.dismiss();
                    }
                }
                return false;
            }
        });
        tidf.show(getSupportFragmentManager(), "inputPwd");
    }

    private void investBank() {
        if (isOpen) {
            User user = DBUtils.getUser(this);
            if (user != null && user.getUserId() != 0) {
                String orderStr = isOrder ? "1" : "0";
                StringBuilder builder = new StringBuilder();
                StringBuilder append = builder.append(HttpService.mBaseUrl)
                        .append("hx/loansign/invest")
                        .append("?userId=" + user.getUserId())
                        .append("&amount=" + mBuyMoney)
                        .append("&loanid=" + loanId)
                        .append("&couponids=" + couponId)
                        .append("&voucherids=" + vouchers)
                        .append("&isBookInvest=" + orderStr);
                String url = append.toString();
                Logger.e("invest url: " + url);
                gotoWeb(url, "我要出借");
            } else {
                gotoActivity(LoginActivity.class);
            }
        } else {
            Utils.Toast(this, "请先开通存管账户");
        }
    }

    @OnClick({R.id.btn_recharge, R.id.btn_allin,R.id.ll_discount_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_recharge://充值
                if (accountType == Constant.AccountLianLain) {
                    //充值入口
                    boolean isAllowRecharge = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_ALLOW_RECHARGE);
                    if (isAllowRecharge) {
                        AlertDialogUtils.confirmGoRecharg(this);
                    } else {
                        new RechargeCloseDialog().show(getFragmentManager(), "RechargeCloseDialog");
                    }
                } else if (accountType == Constant.AccountBank) {
                    gotoActivity(RechargBankActivity.class);
                }
                break;
            case R.id.btn_allin://全投
                allRecharge();
                break;
            case R.id.ll_discount_container://优惠券
                if (mTotalCount + usableVoucherCount > 0) {
                    double moneyValue = 0;
//                    String money = etRechargeMoney.getText().toString();
                    String money = null;
//                    if ("1".equals(mIPhone) && !isOrder) {
//                        money = etRechargeMoney_activity.getText().toString();
//                    } else {
                    money = etLoanAmount.getText().toString();
//                    }
                    if (!TextUtils.isEmpty(money)) {
                        try {
                            moneyValue = Double.parseDouble(money);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    if (moneyValue < 0.00) {
                        Toast.makeText(ProductInvestActivity.this, "请输入出借金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(this, NewSelectVoucherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(VOUCHEREVENT, mVoucherEvent);
                    bundle.putParcelable(ADDRATEEVENT, mAddRateInfo);
                    bundle.putString(PRODUCT_ID, product.getPid() + "");
                    bundle.putDouble(NAME_MONEY, moneyValue);
                    intent.putExtras(bundle);
//                     intent.putExtra(PRODUCT_ID, product.getPid() + "");
//                    intent.putExtra(NAME_MONEY, moneyValue);

                    startActivity(intent);
                }
                break;
        }
    }
}
