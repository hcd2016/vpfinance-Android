package cn.vpfinance.vpjr.module.product.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.DifColorTextStringBuilder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.MyClickableSpan;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.DepositTab1Bean;
import cn.vpfinance.vpjr.gson.NewBaseInfoBean;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.model.DepositInvestInfo;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.InvestLianLianInformDialog;
import cn.vpfinance.vpjr.module.dialog.InvestmentRiskTipsDialog;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.invest.DepositInvestActivity;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.module.product.record.NewRepayListActivity;
import cn.vpfinance.vpjr.module.product.record.ProductInvestListActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.MyCountDownTimer;

/**
 * Created by Administrator on 2016/10/24.
 * 标的基本信息fragment
 */
public class NewBaseInfoFragment extends BaseFragment implements View.OnClickListener {


    private static final int START_REFRESH = 100;
    private static final int CALCULATE_COMPLETE = 99;
    @Bind(R.id.product_name)
    TextView mProductName;
    @Bind(R.id.ivProductState)
    ImageView mIvProductState;
    @Bind(R.id.ivAllowTransfer)
    ImageView mIvAllowTransfer;
    //    @Bind(R.id.isAllowTrip)
//    ImageView mIsAllowTrip;
//    @Bind(R.id.ivCleanProduct)
//    ImageView mIvCleanProduct;
//    @Bind(R.id.tvAtrri)
//    TextView mTvAtrri;
    @Bind(R.id.product_name_state)
    LinearLayout mProductNameState;
    @Bind(R.id.product_rate)
    TextView mProductRate;
    @Bind(R.id.invest_time)
    TextView mInvestTime;
    @Bind(R.id.product_usable_buy)
    TextView mProductUsableBuy;
    @Bind(R.id.product_total_money)
    TextView mProductTotalMoney;
    @Bind(R.id.product_info)
    LinearLayout mProductInfo;
    @Bind(R.id.product_progress)
    NumberProgressBar mProductProgress;
    @Bind(R.id.detal_reward)
    TextView mDetalReward;
    @Bind(R.id.ic_iphone7_index)
    ImageView mIcIphone7Index;
    @Bind(R.id.ll_container)
    LinearLayout mLlContainer;
    @Bind(R.id.tvRefundDes)
    TextView mTvRefundDes;
    @Bind(R.id.tvInvestCount)
    TextView mTvInvestCount;
    @Bind(R.id.clickInvestRecord)
    LinearLayout mClickInvestRecord;
    @Bind(R.id.clickToAvailableTime)
    LinearLayout mClickToAvailableTime;
    @Bind(R.id.invest)
    Button mInvest;
    @Bind(R.id.order_countDown)
    MyCountDownTimer mOrderCountDown;
    @Bind(R.id.ll_time)
    LinearLayout mLlTime;
    @Bind(R.id.orderVoucherNum)
    TextView mOrderVoucherNum;
    @Bind(R.id.btnOrder)
    TextView mBtnOrder;
    @Bind(R.id.order_ll)
    LinearLayout mOrderLl;
    @Bind(R.id.img_activity)
    ImageView mImgActivity;
    @Bind(R.id.product_total_money_text)
    TextView product_total_money_text;
    @Bind(R.id.product_usable_buy_text)
    TextView product_usable_buy_text;
    @Bind(R.id.iv_fdjx)
    ImageView ivFdjx;
    @Bind(R.id.iv_warning)
    ImageView ivWarning;
    @Bind(R.id.tv_warning_desc)
    TextView tvWarningDesc;
    @Bind(R.id.rl_warning_desc_container)
    RelativeLayout rlWarningDescContainer;
//    @Bind(R.id.tv_risk_tips)
//    TextView tvRiskTips;
//    @Bind(R.id.bankAccountStatus)
//    TextView bankAccountStatus;

    private HttpService mHttpService;
    public static final String EXTRA_PRODUCT_ID = "id";
    public static final String NATIVE_PRODUCT_ID = "native_product_id";//如果查看原标传转让标id，不是传0
    public static final String REQUEST_URL = "request_url";
    public static final String IS_DEPOSIT = "is_deposit";
    private boolean isOrder = false;//是否为预售标
    private String mRequestUrl;
    private NewBaseInfoBean mNewBaseInfoBean;
    private boolean isDeposit;
    private DepositTab1Bean depositTab1Bean;
    //    private double          mIssueloan;
    //    private double mHadTenderMoney;
    private DepositInvestInfo depositInvestInfo;

    private long serverTime = System.currentTimeMillis();

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_REFRESH:
                    mHttpService.getRegularTab(mRequestUrl);
                    break;
            }
        }
    };
    private int loanstate;
    private double realTenderMoney;
    private boolean isNewUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_baseinfo_fragment, container, false);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            mRequestUrl = args.getString(REQUEST_URL);
            isDeposit = args.getBoolean(IS_DEPOSIT);
        }

        //        mFinanceProduct = new FinanceProduct();
        mHttpService = new HttpService(getActivity(), this);
        //        mHttpService.getFixProductNew("" + mLoanId, "" + mNativeId);
        initListener();
        return view;
    }

    private void initListener() {
        mInvest.setOnClickListener(this);
        mBtnOrder.setOnClickListener(this);
        mClickInvestRecord.setOnClickListener(this);
        mClickToAvailableTime.setOnClickListener(this);


    }

    @Override
    public void onResume() {
        //        mHttpService.getFixProductNew("" + mLoanId, "" + mNativeId);
        if (!TextUtils.isEmpty(mRequestUrl)) {
            mHttpService.getRegularTab(mRequestUrl);
        }
        super.onResume();
    }


    public static NewBaseInfoFragment newInstance(String url, boolean isDeposit) {
        NewBaseInfoFragment fragment = new NewBaseInfoFragment();
        Bundle args = new Bundle();
        args.putString(REQUEST_URL, url);
        args.putBoolean(IS_DEPOSIT, isDeposit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mOrderCountDown != null) {
            mOrderCountDown.cancel();
        }
        mHandle.removeCallbacksAndMessages(null);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Bank_Real_Tender_Money.ordinal()) {
            realTenderMoney = mHttpService.getOnBankRealTenderMoney(json);
            if (isOrder) {
                mProductUsableBuy.setText(FormatUtils.formatAbout(mNewBaseInfoBean.issueloan - realTenderMoney) + "元");//可购余额
            } else {
                mProductUsableBuy.setText(FormatUtils.formatAbout(mNewBaseInfoBean.issueloan - realTenderMoney) + "元");//可购余额
            }

        } else if (reqId == ServiceCmd.CmdId.CMD_Regular_Tab.ordinal()) {
            mHttpService.getServiceTime();
            try {
                Gson gson = new Gson();
                if (isDeposit) {
                    depositTab1Bean = gson.fromJson(json.toString(), DepositTab1Bean.class);
                    product_usable_buy_text.setText("剩余金额");
                    product_total_money_text.setText("计划金额");
                    initData(depositTab1Bean);
                    initDepositBean(depositTab1Bean);
                } else {
                    mNewBaseInfoBean = gson.fromJson(json.toString(), NewBaseInfoBean.class);
                    product_usable_buy_text.setText("可购余额");
                    product_total_money_text.setText("项目总额");
                    initData(mNewBaseInfoBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_SERVICE_TIME.ordinal()) {
            serverTime = json.optLong("serverTime");
        }
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            if (!AppState.instance().logined() || json.optString("success").equals("false")) {
                gotoActivity(LoginActivity.class);
                return;
            }
            UserInfoBean mUserInfoBean = mHttpService.onGetUserInfo(json);
            if (null == mUserInfoBean) return;
            isNewUser = SharedPreferencesHelper.getInstance(mContext).getBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER);
            if (mNewBaseInfoBean == null) {
                return;
            }
            if (mNewBaseInfoBean.product != 4 && isNewUser) {
                InvestLianLianInformDialog dialog = new InvestLianLianInformDialog();
                dialog.show(getActivity().getFragmentManager(), "InvestLianLianInformDialog");
                return;
            }


            if (mNewBaseInfoBean.product == 4 &&
                    !mUserInfoBean.isOpen.equals("1")) {
                new AlertDialog.Builder(getContext())
                        .setTitle("开通存管账户")
                        .setMessage("根据监管要求，请先开通银行存管账户")
                        .setPositiveButton("立即开通", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User user = DBUtils.getUser(mContext);
                                if (user != null) {
                                    if (TextUtils.isEmpty(user.getRealName())) {
                                        RealnameAuthActivity.goThis(getContext());
                                        Utils.Toast("请先去实名认证");
                                    } else {
                                        gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
                                    }
                                }
                            }
                        })
                        .setNegativeButton("暂不", null)
                        .show();
                return;
            }

            final User user = DBUtils.getUser(mContext);

            if (((NewRegularProductActivity) getActivity()).answerStatus == 0) {
                Utils.Toast("请先进行风险评测");
                gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
                return;
            }
            if (((NewRegularProductActivity) getActivity()).answerStatus == 2) {
                new AlertDialog.Builder(mContext)
                        .setMessage("您很久未进行过出借人风险测评，根据监管要求，请先完成风险测评再进行出借")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (user != null) {
                                    gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
                                }
                            }
                        })
                        .setNegativeButton("下次再说", null)
                        .show();
                return;
            }

            //风险提示弹窗
            if (mNewBaseInfoBean.riskLevel > mUserInfoBean.riskLevel) {//产品风险等级高过个人风险等级
                InvestmentRiskTipsDialog investmentRiskTipsDialog = new InvestmentRiskTipsDialog(getActivity());
                switch (mUserInfoBean.riskLevel) {
                    case 1:
                        investmentRiskTipsDialog.setTvDesc1Content("您的风险评估级别为“保守型”,");
                        investmentRiskTipsDialog.setTvDesc2Content("建议出借标的风险级别不超过“保守型”");
                        break;
                    case 2:
                        investmentRiskTipsDialog.setTvDesc1Content("您的风险评估级别为“稳健型”,");
                        investmentRiskTipsDialog.setTvDesc2Content("建议出借标的风险级别不超过“稳健型”");
                        break;
                    case 3:
                        investmentRiskTipsDialog.setTvDesc1Content("您的风险评估级别为“积极型”,");
                        investmentRiskTipsDialog.setTvDesc2Content("建议出借标的风险级别不超过“积极型”");
                        break;
                }
                investmentRiskTipsDialog.setOnCheckClickListner(new InvestmentRiskTipsDialog.OnCheckClickListner() {
                    @Override
                    public void onCheckClick() {
                        gotoWeb("/h5/help/riskCheckReason?userId=" + user.getUserId(), "查看原因");
                    }
                });
                investmentRiskTipsDialog.setOnConfimClickListner(new InvestmentRiskTipsDialog.OnConfimClickListner() {
                    @Override
                    public void onConfimClick() {
                        invest();
                    }
                });
                investmentRiskTipsDialog.show();
            } else {
                invest();
            }


        }
    }

    //投资跳转
    public void invest() {
        if (isDeposit) {
            if (depositTab1Bean != null) {
                Intent intent = new Intent(getActivity(), DepositInvestActivity.class);
                intent.putExtra("pid", depositTab1Bean.loanId);
                intent.putExtra(DepositInvestActivity.IS_ORDER, false);
                intent.putExtra("poolId", depositTab1Bean.poolId);
                intent.putExtra(DepositInvestActivity.INVEST_INFO, depositInvestInfo);
                startActivity(intent);
            }
        } else {
            if (mNewBaseInfoBean != null) {
                Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
                if ("1".equals(mNewBaseInfoBean.imageUrlJump)) {//跳转本地活动投资页面
                    intent.putExtra(ProductInvestActivity.IPHONE, mNewBaseInfoBean.givePhone);
                }
                intent.putExtra("pid", "" + mNewBaseInfoBean.loanId);
                int accountType = Constant.AccountLianLain;
                if (mNewBaseInfoBean.product == 4) {
                    accountType = Constant.AccountBank;
                }
                intent.putExtra(Constant.AccountType, accountType);
                intent.putExtra(DepositInvestActivity.IS_ORDER, false);
                FinanceApplication myApp = (FinanceApplication) getActivity().getApplication();
                myApp.currentPid = "" + mNewBaseInfoBean.loanId;
                intent.putExtra("isGraceDays", mNewBaseInfoBean.graceDays);//是否是浮动计息产品
                startActivity(intent);
            }
        }
    }


    private void initDepositBean(DepositTab1Bean bean) {
        if (bean == null) return;
        depositInvestInfo = new DepositInvestInfo();
        depositInvestInfo.publishTime = bean.publishTime;
        depositInvestInfo.bookCouponNumber = bean.bookCouponNumber;
        depositInvestInfo.bookPercent = bean.bookPercent;
        depositInvestInfo.timeType = bean.timeType;
        depositInvestInfo.investScopeInfo = bean.scope;
        depositInvestInfo.month = bean.term;
        depositInvestInfo.raminMoney = depositTab1Bean.canBuyMoney;
        depositInvestInfo.sumMoney = bean.sumMoney;
        depositInvestInfo.tenderMoney = bean.tenderMoney;
        depositInvestInfo.surplusMoney = bean.surplusMoney;
        depositInvestInfo.byStagesType = bean.byStagesType;

        List<DepositTab1Bean.DataBean> data = depositTab1Bean.data;
        for (DepositTab1Bean.DataBean dataBean : data) {
            if ("还款方式".equals(dataBean.key)) {
                depositInvestInfo.returnWay = dataBean.value;
                break;
            }
        }
//        depositInvestInfo.planMoney = FormatUtils.formatDown2(depositTab1Bean.issueloan / 10000) + "万";
        depositInvestInfo.rate = depositTab1Bean.rate;
        depositInvestInfo.investScope = new ArrayList<>();
        for (DepositTab1Bean.LoanTypeBean loanTypeBean : depositTab1Bean.loanType) {
            DepositInvestInfo.InvestScopeBean scopeBean = new DepositInvestInfo.InvestScopeBean();
            scopeBean.key = loanTypeBean.key;
            scopeBean.money = loanTypeBean.money;
            scopeBean.type = loanTypeBean.type;
            scopeBean.value = loanTypeBean.value;
            depositInvestInfo.investScope.add(scopeBean);
        }
        depositInvestInfo.title = depositTab1Bean.loanTitle;
    }

    private void initData(DepositTab1Bean depositTab1Bean) {
        if (depositTab1Bean != null) {

            if (depositTab1Bean.frequency == null) {
                depositTab1Bean.frequency = -1;
            }
            initViewAndData2(depositTab1Bean.data);
            mIcIphone7Index.setVisibility(View.GONE);
            mImgActivity.setVisibility(View.GONE);

            mProductName.setText(depositTab1Bean.loanTitle);//标名
            mProductRate.setText(FormatUtils.formatAbout((float) (depositTab1Bean.rate)) + "%");//约定年利率

            mProductTotalMoney.setText(FormatUtils.formatDown2(depositTab1Bean.issueloan / 10000) + "万");//项目总额
            mInvestTime.setText(depositTab1Bean.term);//项目期限

            mProductUsableBuy.setText(FormatUtils.formatAbout(depositTab1Bean.surplusMoney) + "元");

            mTvInvestCount.setText("已有" + depositTab1Bean.buyCount + "人出借");
            mIvProductState.setVisibility(View.GONE);
//            float pro = (float) ((issueloan - buyMoney) / issueloan);
            float pro = (float) (depositTab1Bean.process);
//            mProductProgress.setProgress(pro);

            loanstate = depositTab1Bean.loanState;
            String state = "我要出借";
            switch (loanstate) {//1未发布 2进行中 3回款中 4已完成
                case 1:
                    state = getString(R.string.productState1);//待售
                    mInvest.setVisibility(View.GONE);
                    mOrderLl.setVisibility(View.VISIBLE);
                    isOrder = true;
                    mProductProgress.setProgress(0);
                    break;
                case 2:
                    mOrderLl.setVisibility(View.GONE);
                    mInvest.setVisibility(View.VISIBLE);
                    long currentTimeMillis = System.currentTimeMillis();
                    if (pro >= 100) {
                        state = "进行中";
                        if (depositTab1Bean.canBuyMoney <= 0.005) {
                            state = "满标审核";
                        }
                        mInvest.setEnabled(false);
                    } else if (depositTab1Bean.endTime <= currentTimeMillis) {
                        state = getString(R.string.productState5);
                        mInvest.setEnabled(false);
                        mClickToAvailableTime.setVisibility(View.VISIBLE);
                    } else {
                        state = getString(R.string.productState2);
                        mInvest.setEnabled(true);
                    }
                    mProductProgress.setProgress(pro);
                    break;
                case 3:
                    mOrderLl.setVisibility(View.GONE);
                    mInvest.setVisibility(View.VISIBLE);
                    state = getString(R.string.productState3);
                    pro = 100;
                    mProductProgress.setProgress(pro);
                    mInvest.setEnabled(false);
                    mClickToAvailableTime.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    mOrderLl.setVisibility(View.GONE);
                    mInvest.setVisibility(View.VISIBLE);
                    state = getString(R.string.productState4);
                    pro = 100;
                    mProductProgress.setProgress(pro);
                    mInvest.setEnabled(false);
                    mClickToAvailableTime.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    mOrderLl.setVisibility(View.GONE);
                    mInvest.setVisibility(View.VISIBLE);
                    state = getString(R.string.productState5);
                    mInvest.setEnabled(false);
                    mClickToAvailableTime.setVisibility(View.VISIBLE);
                    break;
            }
            mInvest.setText(state);

            if (loanstate > 2) {
                //
            }
            if (1 == loanstate) {
                long totleTime = depositTab1Bean.publishTime;
                mOrderCountDown.setCountDownTime(mContext, totleTime);
                mOrderCountDown.setOnFinishListener(new MyCountDownTimer.onFinish() {
                    @Override
                    public void finish() {
//                        isDeposit = false;
                        mHandle.sendEmptyMessageDelayed(START_REFRESH, Constant.delay);
                    }
                });
                if (depositTab1Bean.bookCouponNumber > 0) {
                    //有可使用状态的预约券
                    mOrderVoucherNum.setVisibility(View.VISIBLE);
                    mOrderVoucherNum.setText("您有" + depositTab1Bean.bookCouponNumber + "张预约券！点击进行预约。");
                    mBtnOrder.setEnabled(true);
                    mBtnOrder.setText("预约出借");
                } else {
                    mBtnOrder.setEnabled(false);
                }
                double canOrderMoney = depositTab1Bean.issueloan * depositTab1Bean.bookPercent;
//                double alreadyOrder = depositTab1Bean.issueloan - depositTab1Bean.canBuyMoney;
                if (canOrderMoney <= depositTab1Bean.tenderMoney) {
                    String str = "预售中\n预约已满额";
                    String targetStr = "预约已满额";
                    Utils.setTwoTextSize(str, targetStr, 10, mBtnOrder);
                    mBtnOrder.setEnabled(false);
                }
            }
        }
    }

    private void initData(NewBaseInfoBean mNewBaseInfoBean) {
        if (mNewBaseInfoBean != null) {
            if (mNewBaseInfoBean.frequency == null) {
                mNewBaseInfoBean.frequency = -1;
            }
            initViewAndData(mNewBaseInfoBean.data);

            //国庆投资送iphone7活动
            if ("1".equals(mNewBaseInfoBean.givePhone)) {
                mIcIphone7Index.setVisibility(View.VISIBLE);
                mImgActivity.setVisibility(View.VISIBLE);
                mImgActivity.setOnClickListener(this);
                ImageLoader.getInstance().displayImage(mNewBaseInfoBean.imageUrl, mImgActivity);
                ImageLoader.getInstance().displayImage(mNewBaseInfoBean.imageTagUrl, mIcIphone7Index);
            } else {
                mIcIphone7Index.setVisibility(View.GONE);
                mImgActivity.setVisibility(View.GONE);
            }


            mProductName.setText(mNewBaseInfoBean.loanTitle);//标名
            float v = (float) ((mNewBaseInfoBean.rate - mNewBaseInfoBean.reward) * 100);
            mProductRate.setText(FormatUtils.formatAbout(v) + "%");//约定年利率
            if (!TextUtils.isEmpty(mNewBaseInfoBean.issueloan + "")) {
                //                mIssueloan = Double.parseDouble(mNewBaseInfoBean.issueloan+"");
                //                mHadTenderMoney = Double.parseDouble(mNewBaseInfoBean.hadTenderMoney+"");
            }
            mProductTotalMoney.setText(FormatUtils.formatDown2(mNewBaseInfoBean.issueloan / 10000) + "万");//项目总额
            mInvestTime.setText(mNewBaseInfoBean.month);//项目期限

//            if (mNewBaseInfoBean.product == 4){
//                bankAccountStatus.setVisibility(View.VISIBLE);
//            }else{
//                bankAccountStatus.setVisibility(View.GONE);
//            }
            if ("1".equals(mNewBaseInfoBean.status)) {
                //预约标
                mProductUsableBuy.setText(FormatUtils.formatAbout(mNewBaseInfoBean.issueloan) + "元");//可购余额
            } else {
                if (mNewBaseInfoBean.product != 4) {
                    mProductUsableBuy.setText(FormatUtils.formatAbout(mNewBaseInfoBean.issueloan - mNewBaseInfoBean.hadTenderMoney) + "元");//可购余额
                } else {
                    mHttpService.getBankRealTenderMoney("" + mNewBaseInfoBean.loanId);
                }
            }

            mTvInvestCount.setText("已有" + mNewBaseInfoBean.buyCount + "人出借");

            if (mNewBaseInfoBean.reward != 0) {
                mDetalReward.setVisibility(View.VISIBLE);
                mDetalReward.setText("+" + mNewBaseInfoBean.reward * 100 + "%");//加息
            }

//            if ("1".equals(mNewBaseInfoBean.isAllowTrip)) {//是否是旅游标
//                mIsAllowTrip.setVisibility(View.VISIBLE);
//            } else {
//                mIsAllowTrip.setVisibility(View.GONE);
//            }

            if ("true".equals(mNewBaseInfoBean.allowTransfer)) {//是否允许转让
                mIvAllowTransfer.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(mNewBaseInfoBean.remark)) {
                mTvRefundDes.setVisibility(View.VISIBLE);
                mTvRefundDes.setText(mNewBaseInfoBean.remark);
            }

//            double p = 0;
//            try {
//                p = (100 * mNewBaseInfoBean.hadTenderMoney / mNewBaseInfoBean.issueloan);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            float pro = 0;
//            try {
//                pro = Float.parseFloat(FormatUtils.formatDownByProgress(p));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            float pro = mNewBaseInfoBean.process;
            String loanstate = mNewBaseInfoBean.status;

            String state = "我要出借";
            if (!TextUtils.isEmpty(loanstate)) {
                switch (loanstate) {//1未发布 2进行中 3回款中 4已完成
                    case "1":
                        state = getString(R.string.productState1);//待售
                        mInvest.setVisibility(View.GONE);
                        mOrderLl.setVisibility(View.VISIBLE);
                        isOrder = true;
                        //                    invest.setEnabled(false);
                        break;
                    case "2":
                        if (100 > pro) {
                            state = getString(R.string.productState2);
                            mInvest.setEnabled(true);
                        } else {
                            state = "进行中";
                            if (mNewBaseInfoBean.hadTenderMoney >= mNewBaseInfoBean.issueloan) {
                                state = "满标审核";
                            }
                            mInvest.setEnabled(false);
                        }
                        break;
                    case "3":
                        state = getString(R.string.productState3);
                        pro = 100;
                        mInvest.setEnabled(false);
                        mClickToAvailableTime.setVisibility(View.VISIBLE);
                        //                    finishInvest();
                        break;
                    case "4":
                        state = getString(R.string.productState4);
                        pro = 100;
                        mInvest.setEnabled(false);
                        mClickToAvailableTime.setVisibility(View.VISIBLE);
                        //                    finishInvest();
                        break;
                }
            }

            mInvest.setText(state);
            if ("1".equals(loanstate)) {//预售中
                mProductProgress.setProgress(0);
            } else {
                mProductProgress.setProgress(pro);
            }

            if (pro >= 100) {
                if (mInvest != null) {
                    mInvest.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    //                    invest.setTextColor(Color.parseColor("#999999"));
                    mInvest.setEnabled(false);
                } else {
                    mInvest.setEnabled(true);
                }
            }

            Common.productSubType(mContext, mIvProductState, mNewBaseInfoBean.subType);

            if ("1".equals(loanstate) && (!TextUtils.isEmpty(mNewBaseInfoBean.publishTime))) {
                try {
                    String publishTime = mNewBaseInfoBean.publishTime;
                    long totleTime = Long.parseLong(publishTime);
                    if (!AppState.instance().logined()) {
                        mBtnOrder.setEnabled(true);
                        mOrderCountDown.setCountDownTime(mContext, totleTime);
                    } else {
                        if (!TextUtils.isEmpty(mNewBaseInfoBean.bookCouponNumber) && 0 < Integer.parseInt(mNewBaseInfoBean.bookCouponNumber)) {
                            //有可使用状态的预约券
                            mOrderVoucherNum.setVisibility(View.VISIBLE);
                            mOrderVoucherNum.setText("您有" + mNewBaseInfoBean.bookCouponNumber + "张预约券！点击进行预约。");
                            mBtnOrder.setEnabled(true);
                            mBtnOrder.setText("预约出借");
                            mOrderCountDown.setCountDownTime(mContext, totleTime);
                        } else {
                            double orderMoney = mNewBaseInfoBean.issueloan * mNewBaseInfoBean.bookPercent - mNewBaseInfoBean.hadTenderMoney;
                            if (orderMoney <= 0) {
                                String str = "预售中\n预约已满额";
                                String targetStr = "预约已满额";
                                Utils.setTwoTextSize(str, targetStr, 10, mBtnOrder);
                                //                            btnOrder.setText("预售中\n预约已满额");
                            }
                            mBtnOrder.setEnabled(false);
                            mOrderCountDown.setCountDownTime(mContext, totleTime);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (mNewBaseInfoBean.graceDays > 0) {//是浮动计息
            rlWarningDescContainer.setVisibility(View.VISIBLE);
            ivFdjx.setVisibility(View.VISIBLE);
            setWarningContent(mNewBaseInfoBean);
        } else {
            ivFdjx.setVisibility(View.GONE);
            rlWarningDescContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 设置高亮提示内容
     *
     * @param mNewBaseInfoBean
     */
    private void setWarningContent(final NewBaseInfoBean mNewBaseInfoBean) {
        //        final String content = "该产品52.12%采用浮动计息36.85%方式，最大还款日40.50%期为1个月+7天；1个月内还款年利率为7.2%，超过1个月的7天浮动计息期每天以7.5%的年利率计息。了解详情>>";
        String content = mNewBaseInfoBean.flowInvestReminder + "  了解详情>>";
        List<String> floatPercent = Utils.getFloatPercent(content);
        DifColorTextStringBuilder difColorTextStringBuilder = new DifColorTextStringBuilder();
        difColorTextStringBuilder.setContent(content);
        for (int i = 0; i < floatPercent.size(); i++) {
            difColorTextStringBuilder.setHighlightContent(floatPercent.get(i), R.color.red_text);
        }
        difColorTextStringBuilder.setHighlightContent("了解详情>>", R.color.red_text)
                .setHighlightContent("了解详情>>", new MyClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        gotoWeb("/h5/help/floatProductTips?loanId=" + mNewBaseInfoBean.loanId, "");
                    }
                })
                .setTextView(tvWarningDesc)
                .create();
    }

    private void initViewAndData2(List<DepositTab1Bean.DataBean> data) {
        if (data == null)
            return;
        mLlContainer.removeAllViews();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            View view = null;
            try {
                DepositTab1Bean.DataBean dataEntity = data.get(i);
                if (dataEntity == null)
                    break;
                if ("2".equals(dataEntity.type)) {
                    view = View.inflate(mContext, R.layout.new_base_info_time_item, null);
                    ((TextView) view.findViewById(R.id.tvKey)).setText(dataEntity.key);
                    ((MyCountDownTimer) view.findViewById(R.id.tvValue)).setCountDownTime(mContext, Long.parseLong(dataEntity.value));
                } else {
                    view = View.inflate(mContext, R.layout.new_base_info_basic_item, null);
                    ((TextView) view.findViewById(R.id.tvKey)).setText(dataEntity.key);
                    ((TextView) view.findViewById(R.id.tvValue)).setText(dataEntity.value);
                }

                mLlContainer.addView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initViewAndData(List<NewBaseInfoBean.DataEntity> data) {
        if (data == null)
            return;
        mLlContainer.removeAllViews();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            View view = null;
            try {
                NewBaseInfoBean.DataEntity dataEntity = data.get(i);
                if (dataEntity == null)
                    break;
                if ("2".equals(dataEntity.type)) {
                    view = View.inflate(mContext, R.layout.new_base_info_time_item, null);
                    ((TextView) view.findViewById(R.id.tvKey)).setText(dataEntity.key);
                    ((MyCountDownTimer) view.findViewById(R.id.tvValue)).setCountDownTime(mContext, Long.parseLong(dataEntity.value));
                } else {
                    view = View.inflate(mContext, R.layout.new_base_info_basic_item, null);
                    ((TextView) view.findViewById(R.id.tvKey)).setText(dataEntity.key);
                    ((TextView) view.findViewById(R.id.tvValue)).setText(dataEntity.value);
                }

                mLlContainer.addView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*private void finishInvest() {
        mLlRemainInvestTime.setVisibility(View.GONE);
        mLlInterestWay.setVisibility(View.GONE);
        mLlInvestFinishTime.setVisibility(View.VISIBLE);
        mDividerStartInterest.setVisibility(View.VISIBLE);
        mDividerInvestFinishTime.setVisibility(View.VISIBLE);
        mClickToAvailableTime.setVisibility(View.VISIBLE);
        mLlStartInterest.setVisibility(View.VISIBLE);
        mInvestFinishTime.setText(TextUtils.isEmpty(mFinanceProduct.getLastTenderTime()) ? "" : mFinanceProduct.getLastTenderTime());
        mStartInterest.setText(TextUtils.isEmpty(mFinanceProduct.getCreditTime2()) ? "" : mFinanceProduct.getCreditTime2());
    }*/

//    @OnClick({R.id.tv_risk_tips})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invest://正常购买
                mHttpService.getUserInfo();

//                isNewUser = SharedPreferencesHelper.getInstance(mContext).getBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER);
//                if (mNewBaseInfoBean == null) {
//                    return;
//                }
//                if (mNewBaseInfoBean.product != 4 && isNewUser) {
//                    InvestLianLianInformDialog dialog = new InvestLianLianInformDialog();
//                    dialog.show(getActivity().getFragmentManager(), "InvestLianLianInformDialog");
//                    return;
//                }
//
//                if (mNewBaseInfoBean.product == 4 &&
//                        !SharedPreferencesHelper.getInstance(getActivity()).getBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT)) {
//                    Utils.Toast(getContext(), "开通存管账户");
//                    new AlertDialog.Builder(getContext())
//                            .setTitle("开通存管账户")
//                            .setMessage("根据监管要求，请先开通银行存管账户")
//                            .setPositiveButton("立即开通", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    User user = DBUtils.getUser(mContext);
//                                    if (user != null) {
//                                        if (TextUtils.isEmpty(user.getRealName())) {
//                                            RealnameAuthActivity.goThis(getContext());
//                                            Utils.Toast("请先去实名认证");
//                                        } else {
//                                            gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
//                                        }
//                                    }
//                                }
//                            })
//                            .setNegativeButton("暂不", null)
//                            .show();
//                    return;
//                }
//
//                if (((NewRegularProductActivity) getActivity()).answerStatus == 2) {
//                    new AlertDialog.Builder(mContext)
//                            .setMessage("您很久未进行过出借人风险测评，根据监管要求，请先完成风险测评再进行投资")
//                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    User user = DBUtils.getUser(mContext);
//                                    if (user != null) {
//                                        gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
//                                    }
//                                }
//                            })
//                            .setNegativeButton("下次再说", null)
//                            .show();
//                    return;
//                }
//
//                if (isDeposit) {
//                    if (depositTab1Bean != null) {
//                        Intent intent = new Intent(getActivity(), DepositInvestActivity.class);
//                        intent.putExtra("pid", depositTab1Bean.loanId);
//                        intent.putExtra(DepositInvestActivity.IS_ORDER, false);
//                        intent.putExtra("poolId", depositTab1Bean.poolId);
//                        intent.putExtra(DepositInvestActivity.INVEST_INFO, depositInvestInfo);
//                        startActivity(intent);
//                    }
//                } else {
//                    if (mNewBaseInfoBean != null) {
//                        Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
//                        if ("1".equals(mNewBaseInfoBean.imageUrlJump)) {//跳转本地活动投资页面
//                            intent.putExtra(ProductInvestActivity.IPHONE, mNewBaseInfoBean.givePhone);
//                        }
//                        intent.putExtra("pid", "" + mNewBaseInfoBean.loanId);
//                        int accountType = Constant.AccountLianLain;
//                        if (mNewBaseInfoBean.product == 4) {
//                            accountType = Constant.AccountBank;
//                        }
//                        intent.putExtra(Constant.AccountType, accountType);
//                        intent.putExtra(DepositInvestActivity.IS_ORDER, false);
//                        FinanceApplication myApp = (FinanceApplication) getActivity().getApplication();
//                        myApp.currentPid = "" + mNewBaseInfoBean.loanId;
//                        intent.putExtra("isGraceDays",mNewBaseInfoBean.graceDays);//是否是浮动计息产品
//                        startActivity(intent);
//                    }
//                }

                break;
            case R.id.btnOrder://预约购买
                if (!AppState.instance().logined()) {
                    gotoActivity(LoginActivity.class);
                    break;
                }

                isNewUser = SharedPreferencesHelper.getInstance(mContext).getBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER);
                if (mNewBaseInfoBean == null) return;
                int accountType = Constant.AccountLianLain;
                if (mNewBaseInfoBean.product == 4) {
                    accountType = Constant.AccountBank;
                }
                if (accountType == Constant.AccountLianLain && isNewUser) {
                    InvestLianLianInformDialog dialog = new InvestLianLianInformDialog();
                    dialog.show(getActivity().getFragmentManager(), "InvestLianLianInformDialog");
                    return;
                }

                if (accountType == Constant.AccountBank &&
                        !SharedPreferencesHelper.getInstance(getActivity()).getBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("开通存管账户")
                            .setMessage("根据监管要求，请先开通银行存管账户")
                            .setPositiveButton("立即开通", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    User user = DBUtils.getUser(mContext);
                                    if (user != null) {
                                        if (TextUtils.isEmpty(user.getRealName())) {
                                            RealnameAuthActivity.goThis(getContext());
                                            Utils.Toast("请先去实名认证");
                                        } else {
                                            gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("暂不", null)
                            .show();
                    return;
                }

                if (((NewRegularProductActivity) getActivity()).answerStatus == 2) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("您很久未进行过出借人风险测评，根据监管要求，请先完成风险测评再进行出借")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    User user = DBUtils.getUser(mContext);
                                    if (user != null) {
                                        gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
                                    }
                                }
                            })
                            .setNegativeButton("下次再说", null)
                            .show();
                    return;
                }

                if (isDeposit) {//定存宝,暂时废弃.
                    if (depositTab1Bean != null) {
                        Intent intent = new Intent(getActivity(), DepositInvestActivity.class);
                        intent.putExtra("pid", depositTab1Bean.loanId);
                        intent.putExtra(DepositInvestActivity.IS_ORDER, true);
                        intent.putExtra("poolId", depositTab1Bean.poolId);
                        intent.putExtra(DepositInvestActivity.INVEST_INFO, depositInvestInfo);
                        startActivity(intent);
                    }
                } else {
                    if (mNewBaseInfoBean != null) {
                        Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
                        if ("1".equals(mNewBaseInfoBean.imageUrlJump)) {//是 1 跳转本地活动投资页面
                            intent.putExtra(ProductInvestActivity.IPHONE, mNewBaseInfoBean.givePhone);
                        }
                        intent.putExtra(ProductInvestActivity.IS_ORDER, true);
                        intent.putExtra(Constant.AccountType, accountType);
                        //                    intent.putExtra(ProductInvestActivity.TYPE_PRODUCT, ProductInvestActivity.TYPE_Jewelry_PRODUCT);
                        intent.putExtra("pid", "" + mNewBaseInfoBean.loanId);
                        FinanceApplication myApp = (FinanceApplication) getActivity().getApplication();
                        myApp.currentPid = "" + mNewBaseInfoBean.loanId;
                        startActivity(intent);
                    }
                }
                break;
            case R.id.clickInvestRecord://投标记录
                if (isDeposit) {
                    if (depositTab1Bean != null) {
                        gotoProductInvestList(depositTab1Bean.poolId + "", depositTab1Bean.frequency, true, serverTime);
                    }
                } else {
                    if (mNewBaseInfoBean != null) {
                        gotoProductInvestList(mNewBaseInfoBean.loanId + "", mNewBaseInfoBean.frequency, false, serverTime);
                    }
                }
                break;
            case R.id.clickToAvailableTime://回款计划
                if (null != mNewBaseInfoBean) {
                    if (isDeposit) {
                        if (depositTab1Bean != null) {
                            List<DepositTab1Bean.RepaysBean> repays = depositTab1Bean.repays;

                            NewRepayListActivity.goNewRepayListByDepositActivity(mContext, true, repays);
                        }
                    } else {
                        if (mNewBaseInfoBean != null) {
                            NewRepayListActivity.goNewRepayListActivity(mContext, false, mNewBaseInfoBean.repays);
                        }
                    }
                }
                break;
        }
    }

    private void gotoProductInvestList(String id, int frequency, boolean is_deposit, long serviceTime) {
        try {
            long pid = Long.parseLong(id);
            if (serverTime == 0) {
                ProductInvestListActivity.goProductInvestListActivity(mContext, pid, 0, frequency, is_deposit);
            } else {
                ProductInvestListActivity.goProductInvestListActivity(mContext, pid, 0, frequency, is_deposit, serviceTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
