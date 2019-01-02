package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.LoanRecord;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.gson.PersonalInfo;
import cn.vpfinance.vpjr.model.RegularProductInfo;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.product.BorrowerCreditActivity;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.module.product.record.ProductInvestListActivity;
import cn.vpfinance.vpjr.module.product.record.TransferAvailableTimeActivity;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import de.greenrobot.event.EventBus;

/**
 * 个人标详情
 */
public class RegularProductBorrowerFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.ic_iphone7_index)
    ImageView mIcIphone7Index;
    @Bind(R.id.img_activity)
    ImageView mImgActivity;
    private TextView          tvProductName;
    private NumberProgressBar tvNumberbar;
    private TextView          tvProductRate;
    private TextView          tvProductLeft;
    private TextView          tvProductTotalMoney;
    private TextView          tvProductTerm;
    private TextView          tvRepayType;
    private TextView          tvBorrowerName;
    private TextView          tvLoanUse;
    private TextView          tvCredit;
    private TextView          tvInvestCount;
    private HttpService       mHttpService;
    private FinanceProduct    product;
    private double mRemainMoney = 0;

    private MyCountDownTimer mCountDownTimer;

    private TextView         detal_reward;
    private Button           btnInvest;
    private ImageView        ivAllowTransfer;
//    private ImageView        ivCleanProduct;
    private LinearLayout     order_ll;
    private MyCountDownTimer order_countDown;
    private LinearLayout     ll_time;
    private TextView         investTimeTv;
    private TextView         beginTime;
    private TextView         orderVoucherNum;
    private TextView         btnOrder;

    private long pid;
    private int  type;

    private static final String ARGS_PRODUCT_TYPE = "type";
    private static final String ARGS_PRODUCT_ID   = "id";

    @Bind(R.id.ll_remain_invest_time)
    LinearLayout ll_remain_invest_time;
    @Bind(R.id.ll_invest_finish_time)
    LinearLayout ll_invest_finish_time;
    @Bind(R.id.ll_interest_way)
    LinearLayout ll_interest_way;
    @Bind(R.id.ll_start_interest)
    LinearLayout ll_start_interest;
    @Bind(R.id.clickToAvailableTime)
    LinearLayout clickToAvailableTime;
    @Bind(R.id.invest_finish_time)
    TextView     invest_finish_time;
    @Bind(R.id.start_interest)
    TextView     start_interest;
    @Bind(R.id.divider_start_interest)
    View         divider_start_interest;
    @Bind(R.id.divider_invest_finish_time)
    View         divider_invest_finish_time;
//    @Bind(R.id.ivProductState)
//    ImageView    ivProductState;
    private boolean isOrder = false;


    public static RegularProductBorrowerFragment newInstance(long pid, int type) {
        RegularProductBorrowerFragment frag = new RegularProductBorrowerFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PRODUCT_TYPE, type);
        args.putLong(ARGS_PRODUCT_ID, pid);
        frag.setArguments(args);
        return frag;
    }

    private void initFind(View view) {
//        ivCleanProduct = ((ImageView) view.findViewById(R.id.ivCleanProduct));

        ivAllowTransfer = ((ImageView) view.findViewById(R.id.ivAllowTransfer));
        tvProductName = (TextView) view.findViewById(R.id.product_name);
        tvNumberbar = (NumberProgressBar) view.findViewById(R.id.product_progress);
        tvProductRate = (TextView) view.findViewById(R.id.product_rate);
        tvProductLeft = (TextView) view.findViewById(R.id.product_usable_buy);
        tvProductTotalMoney = (TextView) view.findViewById(R.id.product_total_money);
        tvProductTerm = (TextView) view.findViewById(R.id.invest_time);

        tvRepayType = (TextView) view.findViewById(R.id.tvRepayType);
        tvBorrowerName = (TextView) view.findViewById(R.id.tvBorrowerName);
        tvLoanUse = (TextView) view.findViewById(R.id.tvLoanUse);
        tvCredit = (TextView) view.findViewById(R.id.tvCredit);
        tvInvestCount = (TextView) view.findViewById(R.id.tvInvestCount);
        view.findViewById(R.id.clickInvestRecord).setOnClickListener(this);
        view.findViewById(R.id.clickLookCredit).setOnClickListener(this);
        btnInvest = ((Button) view.findViewById(R.id.btnInvest));
        btnInvest.setOnClickListener(this);

        detal_reward = (TextView) view.findViewById(R.id.detal_reward);

        mCountDownTimer = (MyCountDownTimer) view.findViewById(R.id.countDown);
        order_ll = (LinearLayout) view.findViewById(R.id.order_ll);
        order_countDown = ((MyCountDownTimer) view.findViewById(R.id.order_countDown));
        ll_time = (LinearLayout) view.findViewById(R.id.ll_time);
        investTimeTv = ((TextView) view.findViewById(R.id.investTimeTv));
        beginTime = ((TextView) view.findViewById(R.id.beginTime));
        orderVoucherNum = ((TextView) view.findViewById(R.id.orderVoucherNum));
        btnOrder = (TextView) view.findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            pid = args.getLong(ARGS_PRODUCT_ID);
            type = args.getInt(ARGS_PRODUCT_TYPE);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mHttpService = new HttpService(getActivity(), this);
        View view = View.inflate(getActivity(), R.layout.fragment_borrower_product, null);
        ButterKnife.bind(this, view);
        initFind(view);
        mHttpService.getFixProduct("" + pid, "" + 0);
        return view;
    }

    public void onEventMainThread(PersonalInfo event) {
        if (event != null && isAdded()) {
            String allowTransfer = product.getAllowTransfer();
            if (!TextUtils.isEmpty(allowTransfer) && "true".equals(allowTransfer)) {
                ivAllowTransfer.setVisibility(View.VISIBLE);
            }
            String productType = product.getProductType();
//            if (!TextUtils.isEmpty(productType)) {
//                ivCleanProduct.setVisibility("5".equals(productType) ? View.VISIBLE : View.GONE);
//            }

            if (event.borrowerInfo != null) {
                if (event.borrowerInfo.borrowedUse != null) {
                    tvLoanUse.setText(event.borrowerInfo.borrowedUse.moneyUse);
                }
            }

            if (event.creditAssess != null) {
                if (event.creditAssess.creditLV != null) {
                    tvCredit.setText(event.creditAssess.creditLV.level + "级");
                }
            }

            if (event.borrowerInfo != null) {
                if (event.borrowerInfo.basicInfo != null) {
                    //                    tvBorrowerName.setText(FormatUtils.hideName(event.borrowerInfo.basicInfo.userName));

                }
            }
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo.ordinal() && isAdded()) {
            if (json != null) {
                ArrayList<LoanRecord> rList = mHttpService.onGetProductDetail(getActivity(), json, product);
                if (product == null)
                    return;
                if (!TextUtils.isEmpty(product.getBorrower())) {
                    tvBorrowerName.setText(product.getBorrower());
                }
                //不做判断，因为fragment只会在预售标到了的时候才会有这个唯一的请求，刷新标
                long time = product.getBidEndTime() - System.currentTimeMillis();
                time = time / 1000;
                if (time > 0 && time < 30 * 24 * 60 * 60) {
                    mCountDownTimer.setCountDownTime(mContext,product.getBidEndTime());
                }
                //                btnInvest.setText("我要出借");
                btnInvest.setEnabled(true);
            }
        }
    }

    private void finishInvest() {
        ll_remain_invest_time.setVisibility(View.GONE);
        ll_interest_way.setVisibility(View.GONE);
        ll_invest_finish_time.setVisibility(View.VISIBLE);
        divider_start_interest.setVisibility(View.VISIBLE);
        divider_invest_finish_time.setVisibility(View.VISIBLE);
        clickToAvailableTime.setVisibility(View.VISIBLE);
        clickToAvailableTime.setOnClickListener(this);
        ll_start_interest.setVisibility(View.VISIBLE);
        invest_finish_time.setText(TextUtils.isEmpty(product.getLastTenderTime()) ? "" : product.getLastTenderTime());
        start_interest.setText(TextUtils.isEmpty(product.getCreditTime2()) ? "" : product.getCreditTime2());
    }

    public void onEventMainThread(RegularProductInfo event) {
        if (event != null && isAdded()) {
            product = event.product;

            //国庆出借送iphone7活动
            if ("1".equals(product.getGivePhone())) {
                mIcIphone7Index.setVisibility(View.VISIBLE);
                mImgActivity.setVisibility(View.VISIBLE);
                mImgActivity.setOnClickListener(this);
                ImageLoader.getInstance().displayImage(HttpService.mBaseUrl+product.getImageUrl(), mImgActivity);
            } else {
                mIcIphone7Index.setVisibility(View.GONE);
                mImgActivity.setVisibility(View.GONE);
            }

            String buyCount = product.getBuyCount();
            tvInvestCount.setText("已有" + buyCount + "人出借");

            long refundWay = product.getRefundWay();
            String refunWayStr = "";
            refunWayStr = refundWay == 1 ? getString(R.string.refundState1) :
                    (refundWay == 2 ? getString(R.string.refundState2) : getString(R.string.refundState3));
            tvRepayType.setText(refunWayStr);

            String reward = product.getReward();

            float val = 0;
            if (!TextUtils.isEmpty(reward)) {
                try {
                    //reward = reward.replaceAll("%","");
                    val = Float.parseFloat(reward);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            if (val > 0) {
                val *= 100;
                detal_reward.setText("+" + String.format("%.1f", val) + "%");
                detal_reward.setVisibility(View.VISIBLE);
            }


            View view = getView();
            tvProductName.setText(product.getLoanTitle());
            double rate = product.getRate();
            mRemainMoney = product.getIssueLoan() - product.getTotal_tend_money();

            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String remainMoney = decimalFormat.format(mRemainMoney);
            //            if(remainMoney!=null && remainMoney.endsWith(".00"))
            //            {
            //                remainMoney = remainMoney.substring(0,remainMoney.length()-3);
            //            }
            String totalMoney = decimalFormat.format(product.getIssueLoan());
            //            if(totalMoney!=null && totalMoney.endsWith(".00"))
            //            {
            //                totalMoney = totalMoney.substring(0,totalMoney.length()-3);
            //            }
            rate *= 100;
            if (val > 0) {
                rate -= val;
            }
            String showRate = String.format("%.1f", rate);
            tvProductRate.setText(showRate + "%");
            tvProductTotalMoney.setText(FormatUtils.formatDown2(product.getIssueLoan() / 10000) + "万");
            if (TextUtils.isEmpty(remainMoney)) {
                remainMoney = "0";
            }
            if (remainMoney.startsWith(".")) {
                remainMoney = "0" + remainMoney;
            }

            //            tvProductLeft.setText("￥" + remainMoney);
            if (event.product.getLoanType() == 2) {
                tvProductTerm.setText(event.product.getMonth() + "天");
            } else {
                tvProductTerm.setText(event.product.getMonth() + "个月");
            }
            //            float pro = (float) (100 * product.getTotal_tend_money()/product.getIssueLoan());

            double p = (100 * product.getTotal_tend_money() / product.getIssueLoan());
            float pro = 0;
            try {
                pro = Float.parseFloat(FormatUtils.formatDownByProgress(p));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int loanstate = (int) product.getLoanstate();
            if (loanstate == 1) {
                tvProductLeft.setText(totalMoney + "元");
            } else {
                tvProductLeft.setText(remainMoney + "元");
            }
            if (loanstate == 1 && (!TextUtils.isEmpty(product.getPublishTime()))) {
                try {
                    /*countDown.setVisibility(View.VISIBLE);
                    tvStartBuy.setVisibility(View.VISIBLE);
                    String publishTime = product.getPublishTime();
                    long totleTime = Long.parseLong(publishTime);
                    setCountDownTime(totleTime);
                    order_countDown.setCountDownTime(totleTime);*/
                    String publishTime = product.getPublishTime();
                    long totleTime = Long.parseLong(publishTime);
                    beginTime.setVisibility(View.VISIBLE);
                    beginTime.setText(Utils.getDate_M(totleTime));
                    investTimeTv.setText("开始投标时间");
                    if (!AppState.instance().logined()) {
                        btnOrder.setEnabled(true);
                        ll_time.setVisibility(View.VISIBLE);
                        order_countDown.setCountDownTime(mContext,totleTime);
                    } else {
                        if (!TextUtils.isEmpty(product.getBookCouponNumber()) && 0 < Integer.parseInt(product.getBookCouponNumber())) {
                            //有可使用状态的预约券
                            ll_time.setVisibility(View.GONE);
                            orderVoucherNum.setVisibility(View.VISIBLE);
                            orderVoucherNum.setText("您有" + product.getBookCouponNumber() + "张预约券！点击进行预约。");
                            btnOrder.setEnabled(true);
                            btnOrder.setText("预约出借");
                        } else {
                            double orderMoney = product.getIssueLoan() * product.getBookPercent() - product.getTotal_tend_money();
                            if (orderMoney <= 0) {
                                String str = "预售中\n预约已满额";
                                String targetStr = "预约已满额";
                                Utils.setTwoTextSize(str, targetStr, 10, btnOrder);
                                //                            btnOrder.setText("预售中\n预约已满额");
                            }
                            btnOrder.setEnabled(false);
                            ll_time.setVisibility(View.VISIBLE);
                            order_countDown.setCountDownTime(mContext,totleTime);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else/* if (loanstate == 2 && (product.getBidEndTime() != 0))*/ {
                /*tvStartBuy.setVisibility(View.GONE);
                setCountDownTime(product.getBidEndTime());*/
                investTimeTv.setText("剩余投标时间");
                beginTime.setVisibility(View.GONE);
                mCountDownTimer.setVisibility(View.VISIBLE);
                mCountDownTimer.setCountDownTime(mContext,product.getBidEndTime());
            }

            String state = "我要出借";
            switch (loanstate)//1未发布 2进行中 3回款中 4已完成
            {
                case 1:
                    state = getString(R.string.productState1);//待售
                    btnInvest.setVisibility(View.GONE);
                    order_ll.setVisibility(View.VISIBLE);
                    isOrder = true;
                    //                    btnInvest.setEnabled(false);
                    break;
                case 2:
                    if (100 > pro) {
                        state = getString(R.string.productState2);
                        btnInvest.setEnabled(true);
                    } else {
                        state = "进行中";
                        if (product.getTotal_tend_money() >= product.getIssueLoan()) {
                            state = "满标审核";
                        }
                        btnInvest.setEnabled(false);
                    }
                    break;
                case 3:
                    state = getString(R.string.productState3);
                    pro = 100;
                    btnInvest.setEnabled(false);
                    finishInvest();
                    break;
                case 4:
                    state = getString(R.string.productState4);
                    pro = 100;
                    btnInvest.setEnabled(false);
                    finishInvest();
                    break;
            }
            btnInvest.setText(state);

            if (loanstate == 1) {
                tvNumberbar.setProgress(0);
            } else {
                tvNumberbar.setProgress(pro);
            }

            if (pro >= 100) {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                if (btnInvest != null) {
                    btnInvest.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    //                    btnInvest.setTextColor(Color.parseColor("#999999"));
                    btnInvest.setEnabled(false);
                } else {
                    btnInvest.setEnabled(true);
                }
            }

            String pType = "";//1按月等额本息 2按月付息到期还本 3到期一次性还本息
            switch ((int) product.getRefundWay()) {
                case 1:
                    pType = getString(R.string.refundState1);
                    break;
                case 2:
                    pType = getString(R.string.refundState2);
                    break;
                case 3:
                    pType = getString(R.string.refundState3);
                    break;
                default:
                    break;
            }

//            Common.productSubType(mContext,ivProductState,(int) product.getSubType());
        }
    }

    private void productInvest() {
        if (product != null) {
            Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
            intent.putExtra(ProductInvestActivity.IPHONE, product.getGivePhone());
            intent.putExtra("pid", "" + product.getPid());
            App myApp = (App) getActivity().getApplication();
            myApp.currentPid = "" + product.getPid();
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clickToAvailableTime:
                if (product != null && (!TextUtils.isEmpty(product.getRepays()))) {
                    TransferAvailableTimeActivity.goTransferAvailableTime(mContext, product.getRepays(), "" + product.getTotalPeriod());
                }else{
                    TransferAvailableTimeActivity.goTransferAvailableTime(mContext, "", "");
                }
                break;
            case R.id.clickLookCredit:
                gotoActivity(BorrowerCreditActivity.class);
                break;
            //            case R.id.clickProductDetail:
            //                break;
            case R.id.clickInvestRecord:
                ProductInvestListActivity.goProductInvestListActivity(getActivity(), pid, type,-1,false);
                break;
            case R.id.btnInvest:
                productInvest();
                break;
            case R.id.btnOrder://预约购买
                if (!AppState.instance().logined()) {
                    gotoActivity(LoginActivity.class);
                    break;
                }
                if (AppState.instance().logined() && product != null) {
                    Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
                    intent.putExtra(ProductInvestActivity.IPHONE, product.getGivePhone());
                    intent.putExtra(ProductInvestActivity.IS_ORDER, true);
                    intent.putExtra("pid", "" + product.getPid());
                    App myApp = (App) getActivity().getApplication();
                    myApp.currentPid = "" + product.getPid();
                    startActivity(intent);
                }
                break;

        }
    }

    @Override
    public void onStop() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
        if (order_countDown != null){
            order_countDown.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
