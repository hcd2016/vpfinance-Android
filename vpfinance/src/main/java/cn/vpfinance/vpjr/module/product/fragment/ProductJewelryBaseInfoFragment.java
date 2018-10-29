package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.module.product.record.ProductInvestListActivity;
import cn.vpfinance.vpjr.module.product.record.TransferAvailableTimeActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.model.RegularProductInfo;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import de.greenrobot.event.EventBus;

public class ProductJewelryBaseInfoFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.topView)
    LinearLayout mTopView;
    @Bind(R.id.switchOpen)
    TextView     switchOpen;
    @Bind(R.id.returnTop)
    ImageButton  returnTop;

    @Bind(R.id.ivAllowTransfer)
    ImageView        ivAllowTransfer;
    @Bind(R.id.isAllowTrip)
    ImageView        isAllowTrip;
    @Bind(R.id.detal_reward)
    TextView         detal_reward;
    @Bind(R.id.invest)
    Button           invest;
    @Bind(R.id.ivProductState)
    ImageView        ivProductState;
    @Bind(R.id.countDown)
    MyCountDownTimer mCountDownTimer;
    @Bind(R.id.safeway)
    TextView         safeway;
    @Bind(R.id.borrowing)
    TextView         borrowing;

    @Bind(R.id.btnOrder)
    TextView         btnOrder;
    @Bind(R.id.order_ll)
    LinearLayout     order_ll;
    @Bind(R.id.order_countDown)
    MyCountDownTimer order_countDown;
    @Bind(R.id.investTimeTv)
    TextView         investTimeTv;
    @Bind(R.id.beginTime)
    TextView         beginTime;
    @Bind(R.id.orderVoucherNum)
    TextView         orderVoucherNum;
    @Bind(R.id.ll_time)
    LinearLayout     ll_time;
    @Bind(R.id.clickInvestRecord)
    LinearLayout     clickInvestRecord;

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
    @Bind(R.id.tvInvestCount)
    TextView     tvInvestCount;
    @Bind(R.id.ic_iphone7_index)
    ImageView    mIcIphone7Index;
    @Bind(R.id.img_activity)
    ImageView    mImgActivity;

    private long pid;
    private int  type;

    private static final String ARGS_PRODUCT_TYPE = "type";
    private static final String ARGS_PRODUCT_ID   = "id";
    private FinanceProduct product;
    private User           mUser;
    private boolean        mIsShow;
    private boolean isOrder = false;


    public static ProductJewelryBaseInfoFragment newInstance(long pid, int type) {
        ProductJewelryBaseInfoFragment frag = new ProductJewelryBaseInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PRODUCT_TYPE, type);
        args.putLong(ARGS_PRODUCT_ID, pid);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        if (args != null) {
            pid = args.getLong(ARGS_PRODUCT_ID);
            type = args.getInt(ARGS_PRODUCT_TYPE);
        }

        mUser = DBUtils.getUser(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mCountDownTimer!= null){
            mCountDownTimer.cancel();
        }
        if (order_countDown != null){
            order_countDown.cancel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View mContentView = inflater.inflate(R.layout.fragment_product_jewelry_base_info, null);
        ButterKnife.bind(this, mContentView);

        switchOpen.setOnClickListener(this);
        returnTop.setOnClickListener(this);

        invest.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        clickInvestRecord.setOnClickListener(this);
        clickToAvailableTime.setOnClickListener(this);

        return mContentView;
    }

    private void finishInvest() {
        ll_remain_invest_time.setVisibility(View.GONE);
        ll_interest_way.setVisibility(View.GONE);
        ll_invest_finish_time.setVisibility(View.VISIBLE);
        divider_start_interest.setVisibility(View.VISIBLE);
        divider_invest_finish_time.setVisibility(View.VISIBLE);
        clickToAvailableTime.setVisibility(View.VISIBLE);
        ll_start_interest.setVisibility(View.VISIBLE);
        invest_finish_time.setText(TextUtils.isEmpty(product.getLastTenderTime()) ? "" : product.getLastTenderTime());
        start_interest.setText(TextUtils.isEmpty(product.getCreditTime2()) ? "" : product.getCreditTime2());
    }

    public void onEventMainThread(RegularProductInfo event) {
        if (!isAdded()) return;
        if (event != null) {
            product = event.product;

            //国庆投资送iphone7活动
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
            String allowTransfer = product.getAllowTransfer();
            if (!TextUtils.isEmpty(allowTransfer) && "true".equals(allowTransfer)) {
                ivAllowTransfer.setVisibility(View.VISIBLE);
            }

            Integer allowTrip = product.getIsAllowTrip();
            if (!TextUtils.isEmpty(isAllowTrip + "") && allowTrip == 1) {
                isAllowTrip.setVisibility(View.VISIBLE);
            } else {
                isAllowTrip.setVisibility(View.GONE);
            }

            String reward = product.getReward();

            float val = 0;
            if (!TextUtils.isEmpty(reward)) {
                try {
                    val = Float.parseFloat(reward);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            if (val > 0) {
                val *= 100;
                detal_reward.setText(" +" + String.format("%.1f", val) + "%");
                detal_reward.setVisibility(View.VISIBLE);
            }


            View view = getView();
            TextView name = ((TextView) view.findViewById(R.id.product_name));
            name.setText(product.getLoanTitle());
            double rate = product.getRate();
            double mRemainMoney = product.getIssueLoan() - product.getTotal_tend_money();
            if (null != mUser && AppState.instance().logined()) {
                double mCanBuyMoney = mUser.getCashBalance();
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String remainMoney = decimalFormat.format(mRemainMoney);
            //            if (remainMoney != null && remainMoney.endsWith(".00")) {
            //                remainMoney = remainMoney.substring(0, remainMoney.length() - 3);
            //            }
            String totalMoney = decimalFormat.format(product.getIssueLoan());
            //            if (totalMoney != null && totalMoney.endsWith(".00")) {
            //                totalMoney = totalMoney.substring(0, totalMoney.length() - 3);
            //            }
            rate *= 100;
            if (val > 0) {
                rate -= val;
            }
            String showRate = String.format("%.1f", rate);

            ((TextView) view.findViewById(R.id.product_rate)).setText(showRate + "%");
            ((TextView) view.findViewById(R.id.product_total_money)).setText(FormatUtils.formatDown2(product.getIssueLoan() / 10000) + "万");
            if (TextUtils.isEmpty(remainMoney)) {
                remainMoney = "0";
            }
            if (remainMoney.startsWith(".")) {
                remainMoney = "0" + remainMoney;
            }
            if (mRemainMoney < 100) {
            }
            //            ((TextView) view.findViewById(R.id.product_usable_buy)).setText("￥" + remainMoney);
            if (event.product.getLoanType() == 2) {
                ((TextView) view.findViewById(R.id.invest_time)).setText(event.product.getMonth() + "天");
            } else {
                ((TextView) view.findViewById(R.id.invest_time)).setText(event.product.getMonth() + "个月");
            }
            double p = (double) (100 * product.getTotal_tend_money() / product.getIssueLoan());
            String proStr = FormatUtils.formatDownByProgress(p);
            float pro = 0;
            try {
                pro = Float.parseFloat(proStr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int loanstate = (int) product.getLoanstate();
            if (loanstate == 1) {
                ((TextView) view.findViewById(R.id.product_usable_buy)).setText(totalMoney + "元");
            } else {
                ((TextView) view.findViewById(R.id.product_usable_buy)).setText(remainMoney + "元");
            }

            String state = "我要出借";
            mIsShow = true;
            switch (loanstate)//1未发布 2进行中 3回款中 4已完成
            {
                case 1:
                    state = getString(R.string.productState1);//待售
                    invest.setVisibility(View.GONE);
                    order_ll.setVisibility(View.VISIBLE);
                    isOrder = true;
                    break;
                case 2:
                    if (100 > pro) {
                        state = getString(R.string.productState2);
                        mIsShow = true;
                        invest.setEnabled(true);
                    } else {
                        state = "进行中";
                        mIsShow = true;
                        if (product.getTotal_tend_money() >= product.getIssueLoan()) {
                            state = "满标审核";
                            mIsShow = false;
                        }
                        invest.setEnabled(false);
                    }
                    break;
                case 3:
                    state = getString(R.string.productState3);
                    mIsShow = false;
                    pro = 100;
                    invest.setEnabled(false);
                    finishInvest();
                    break;
                case 4:
                    state = getString(R.string.productState4);
                    mIsShow = false;
                    pro = 100;
                    invest.setEnabled(false);
                    finishInvest();
                    break;
            }
            if (loanstate == 1 && (!TextUtils.isEmpty(product.getPublishTime()))) {
                try {

                    /*String publishTime = product.getPublishTime();
                    long totleTime = Long.parseLong(publishTime);
                    mCountDownTimer.setCountDownTime(totleTime);
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
                            btnOrder.setText("预约投资");
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
            } else {
                long time = product.getBidEndTime() - System.currentTimeMillis();
                time = time / 1000;
                if (time > 0 && time < 30 * 24 * 60 * 60) {
                    investTimeTv.setText("剩余投标时间");
                    beginTime.setVisibility(View.GONE);
                    mCountDownTimer.setVisibility(View.VISIBLE);
                    mCountDownTimer.setCountDownTime(mContext,product.getBidEndTime());
                }
            }
            invest.setText(state);

            if (loanstate == 1) {
                ((NumberProgressBar) view.findViewById(R.id.product_progress)).setProgress(0);
            } else {
                ((NumberProgressBar) view.findViewById(R.id.product_progress)).setProgress(pro);
            }

            if (pro >= 100) {
                mCountDownTimer.cancel();
                if (invest != null) {
                    invest.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    //                    invest.setTextColor(Color.parseColor("#999999"));
                    invest.setEnabled(false);
                } else {
                    invest.setEnabled(true);
                }
            }

            safeway.setText(product.getSafeway());
            borrowing.setText(product.getBehoof());

            TextView payType = ((TextView) view.findViewById(R.id.payType));
            TextView riskGrade = ((TextView) view.findViewById(R.id.riskGrade));
            riskGrade.setText(product.getRiskGrade());

            TextView tvAtrri = ((TextView) view.findViewById(R.id.tvAtrri));

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

            if (payType != null) {
                payType.setText(pType);
            }

            Common.productSubType(mContext,ivProductState,(int) product.getSubType());
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
            case R.id.clickInvestRecord:
                if (product != null)
                    ProductInvestListActivity.goProductInvestListActivity(getActivity(), product.getPid(), product.getType(),-1,false);
                break;
            case R.id.returnTop:
                break;
            case R.id.invest:
                if (product != null) {
                    Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
                    intent.putExtra(ProductInvestActivity.TYPE_PRODUCT, ProductInvestActivity.TYPE_Jewelry_PRODUCT);
                    intent.putExtra(ProductInvestActivity.IPHONE, product.getGivePhone());
                    intent.putExtra("pid", "" + product.getPid());
                    FinanceApplication myApp = (FinanceApplication) getActivity().getApplication();
                    myApp.currentPid = "" + product.getPid();
                    startActivity(intent);
                }
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
                    intent.putExtra(ProductInvestActivity.TYPE_PRODUCT, ProductInvestActivity.TYPE_Jewelry_PRODUCT);
                    intent.putExtra("pid", "" + product.getPid());
                    FinanceApplication myApp = (FinanceApplication) getActivity().getApplication();
                    myApp.currentPid = "" + product.getPid();
                    startActivity(intent);
                }
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
