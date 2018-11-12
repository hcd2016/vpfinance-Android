package cn.vpfinance.vpjr.module.product.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.module.product.record.ProductInvestListActivity;
import cn.vpfinance.vpjr.module.product.record.TransferAvailableTimeActivity;
import cn.vpfinance.vpjr.module.voucher.VoucherActivity2;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.LoanRecord;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.gson.ProductCarInfo;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.ProductCarDescriptionInfo;
import cn.vpfinance.vpjr.model.RegularProductInfo;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

public class RegularProductBorrowerFragment2 extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.ic_iphone7_index)
    ImageView mIcIphone7Index;
    @Bind(R.id.img_activity)
    ImageView mImgActivity;
    private              boolean mIsShow = true;
    private static final String  TAG     = "RegProdFragment";

    private HttpService mHttpService = null;
    private View        mContentView = null;
    private Button                  invest;
    private EditText                etMoney;
    private FinanceProduct          product;
    private TextInputDialogFragment tidf;
    private User mUser = null;
    ;
    private CheckBox mUseVoucherCheckBox = null;

    private TextView detal_reward;

    //    private View countDown;

    private double mBuyMoney    = 0;
    private double mCanBuyMoney = 0;
    private double mRemainMoney = 0;

    private static final int RET_CODE_VOUCHER = 100;

    private ImageView    ivAllowTransfer;
    private ImageView    ivProductState;
    private ImageView    mIsAllowTrip;
    private LinearLayout order_ll;
    private LinearLayout ll_time;
    private TextView     borrower;


    private long pid;
    private int  type;

    private static final String ARGS_PRODUCT_TYPE = "type";
    private static final String ARGS_PRODUCT_ID   = "id";
    private MyCountDownTimer mCountDownTimer;
    private MyCountDownTimer order_countDown;
    private TextView         investTimeTv;
    private TextView         beginTime;
    private TextView         orderVoucherNum;
    private TextView         btnOrder;

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
    private boolean isOrder = false;

    public static RegularProductBorrowerFragment2 newInstance(long pid, int type) {
        RegularProductBorrowerFragment2 frag = new RegularProductBorrowerFragment2();
        Bundle args = new Bundle();
        args.putInt(ARGS_PRODUCT_TYPE, type);
        args.putLong(ARGS_PRODUCT_ID, pid);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onStop() {
        mCountDownTimer.cancel();
        super.onStop();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mHttpService = new HttpService(getActivity(), this);
        if (pid != 0) {
            mHttpService.getProductCarInfo("" + pid);
        }

        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        UserDao dao;

        dbHelper = new DaoMaster.DevOpenHelper(getActivity(), Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getUserDao();

        mContentView = inflater.inflate(R.layout.fragment_borrower_product2, null);
        ButterKnife.bind(this, mContentView);
        mCountDownTimer = ((MyCountDownTimer) mContentView.findViewById(R.id.countDown));
        clickToAvailableTime.setOnClickListener(this);
        mCountDownTimer.setOnFinishListener(new MyCountDownTimer.onFinish() {
            @Override
            public void finish() {
                if (mHttpService != null && product != null)
                    mHttpService.getFixProduct("" + product.getPid(), "" + 0);
            }
        });
        ivProductState = ((ImageView) mContentView.findViewById(R.id.ivProductState));
        //        etMoney = (EditText) mContentView.findViewById(R.id.etMoney);
        invest = (Button) mContentView.findViewById(R.id.invest);
        ivAllowTransfer = ((ImageView) mContentView.findViewById(R.id.ivAllowTransfer));
        mIsAllowTrip = (ImageView) mContentView.findViewById(R.id.isAllowTrip);
        order_ll = (LinearLayout) mContentView.findViewById(R.id.order_ll);
        ll_time = (LinearLayout) mContentView.findViewById(R.id.ll_time);
        order_countDown = ((MyCountDownTimer) mContentView.findViewById(R.id.order_countDown));
        investTimeTv = ((TextView) mContentView.findViewById(R.id.investTimeTv));
        beginTime = ((TextView) mContentView.findViewById(R.id.beginTime));
        orderVoucherNum = ((TextView) mContentView.findViewById(R.id.orderVoucherNum));
        btnOrder = (TextView) mContentView.findViewById(R.id.btnOrder);


        borrower = ((TextView) mContentView.findViewById(R.id.borrower));


        detal_reward = (TextView) mContentView.findViewById(R.id.detal_reward);
        mContentView.findViewById(R.id.clickInvestRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product != null)
                    ProductInvestListActivity.goProductInvestListActivity(getActivity(), product.getPid(), product.getType(),-1,false);
            }
        });

        if (dao != null) {
            QueryBuilder<User> qb = dao.queryBuilder();
            List<User> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                mUser = userList.get(0);
            }
        }

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppState.instance().logined()) {
                    gotoActivity(LoginActivity.class);

                } else if (AppState.instance().logined() && product != null) {
                    Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
                    intent.putExtra(ProductInvestActivity.IPHONE, product.getGivePhone());
                    intent.putExtra(ProductInvestActivity.TYPE_PRODUCT, ProductInvestActivity.TYPE_CAR_PRODUCT);
                    intent.putExtra("pid", "" + product.getPid());
                    intent.putExtra(ProductInvestActivity.IS_ORDER, true);
                    App myApp = (App) getActivity().getApplication();
                    myApp.currentPid = "" + product.getPid();
                    startActivity(intent);
                }
            }
        });

        invest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product != null) {
                    Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
                    intent.putExtra(ProductInvestActivity.IPHONE, product.getGivePhone());
                    intent.putExtra(ProductInvestActivity.TYPE_PRODUCT, ProductInvestActivity.TYPE_CAR_PRODUCT);
                    intent.putExtra("pid", "" + product.getPid());
                    App myApp = (App) getActivity().getApplication();
                    myApp.currentPid = "" + product.getPid();
                    startActivity(intent);
                }

            }
        });
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo.ordinal() && isAdded()) {
            if (json != null) {
                ProductCarInfo productCarInfo = mHttpService.onGetProductCarInfo(json);
                if (productCarInfo != null) {
                    if (!TextUtils.isEmpty(productCarInfo.getBorrowing())) {
                        EventBus.getDefault().post(new ProductCarDescriptionInfo(productCarInfo.getBorrowing()));
                    }
                    if (!TextUtils.isEmpty(productCarInfo.getSafeway())) {
                        borrower.setText(productCarInfo.getSafeway());
                    }
                }
                ArrayList<LoanRecord> rList = mHttpService.onGetProductDetail(getActivity(), json, product);
                if (product == null)
                    return;
                //                int loanstate = (int) product.getLoanstate();
                //不做判断，因为fragment只会在预售标到了的时候才会有这个唯一的请求，刷新标
                // tvStartBuy.setVisibility(View.GONE);
                long time = product.getBidEndTime() - System.currentTimeMillis();
                time = time / 1000;
                if (time > 0 && time < 30 * 24 * 60 * 60) {
                    //                    Logger.e("product.getBidEndTime():"+product.getBidEndTime());
                    //                    setCountDownTime(product.getBidEndTime());
                    mCountDownTimer.setCountDownTime(mContext,product.getBidEndTime());
                }
                // tvStartBuy.setVisibility(View.GONE);
                //                invest.setText("我要出借");
                //                invest.setEnabled(true);
            }
        }

        /*if (reqId == ServiceCmd.CmdId.CMD_PLANK.ordinal() && isAdded()) {
            if (json != null && isAdded()) {

                ArrayMap<String, String> map = new ArrayMap<String, String>();
                map.put("type", "Regular");
                map.put("money", "" + RechargeActivity.getStatisticsMoney(mBuyMoney));


                BuyResult res = mHttpService.onPlankFinish(json,false);
                if (res != null) {
                    int status = res.getStatus();
                    String desc = res.getStatusDesc();
                    map.put("status", "" + status);
                    if ("投标成功".equals(desc)) {
                        if (tidf != null) {
                            tidf.dismiss();

                        }
                        showSuccessDialog();
                    }
                    Utils.Toast(getActivity(), desc);
                }
                MobclickAgent.onEvent(getActivity(), "Buy", map);
            }
        }*/
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RET_CODE_VOUCHER:
                if (data != null) {
                    String[] ret = data.getStringArrayExtra(VoucherActivity2.NAME_SELECT_RESULT);
                    if (ret != null) {
                        for (String v : ret) {
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onEventMainThread(RegularProductInfo event) {
        if (event != null && isAdded()) {
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

            Integer isAllowTrip = product.getIsAllowTrip();
            if (!TextUtils.isEmpty(isAllowTrip + "") && isAllowTrip == 1) {
                mIsAllowTrip.setVisibility(View.VISIBLE);
            } else {
                mIsAllowTrip.setVisibility(View.GONE);
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
                detal_reward.setText("+" + String.format("%.1f", val) + "%");
                detal_reward.setVisibility(View.VISIBLE);
            }


            View view = getView();
            TextView name = ((TextView) view.findViewById(R.id.product_name));
            name.setText(product.getLoanTitle());
            double rate = product.getRate();
            mRemainMoney = product.getIssueLoan() - product.getTotal_tend_money();
            if (null != mUser && AppState.instance().logined()) {
                mCanBuyMoney = mUser.getCashBalance();
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String remainMoney = decimalFormat.format(mRemainMoney);
            //            if(remainMoney!=null && remainMoney.endsWith(".00"))
            //            {
            //                remainMoney = remainMoney.substring(0,remainMoney.length()-3);
            //                //                if(remainMoney.endsWith("0000"))
            //                //                {
            //                //                    remainMoney = totalMoney.substring(0,remainMoney.length()-4);
            //                //                    remainMoney = totalMoney + "万";
            //                //                }
            //            }
            String totalMoney = decimalFormat.format(product.getIssueLoan());
            //            if(totalMoney!=null && totalMoney.endsWith(".00"))
            //            {
            //                totalMoney = totalMoney.substring(0,totalMoney.length()-3);
            //                //                if(totalMoney.endsWith("0000"))
            //                //                {
            //                //                    totalMoney = totalMoney.substring(0,totalMoney.length()-4);
            //                //                    totalMoney = totalMoney + "万";
            //                //                }
            //            }
            rate *= 100;
            if (val > 0) {
                rate -= val;
            }
            String showRate = String.format("%.1f", rate);
            //            if (showRate.endsWith(".0")) {
            //                showRate = showRate.substring(0, showRate.length() - 2);
            //            }
            ((TextView) view.findViewById(R.id.product_rate)).setText(showRate + "%");
            ((TextView) view.findViewById(R.id.product_total_money)).setText(FormatUtils.formatDown2(product.getIssueLoan() / 10000) + "万");
            if (TextUtils.isEmpty(remainMoney)) {
                remainMoney = "0";
            }
            if (remainMoney.startsWith(".")) {
                remainMoney = "0" + remainMoney;
            }
            if (mRemainMoney < 100) {
                //                etMoney.setHint("");
                //                etMoney.setText(String.format("%.2f", mRemainMoney));
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
                    //                    invest.setEnabled(false);
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
            invest.setText(state);

            if (loanstate == 1 && (!TextUtils.isEmpty(product.getPublishTime()))) {
                try {


                    //                    countDown.setVisibility(View.VISIBLE);
                    // tvStartBuy.setVisibility(View.VISIBLE);
                    String publishTime = product.getPublishTime();
                    long totleTime = Long.parseLong(publishTime);
                    //                    Logger.e("totleTime:"+totleTime);
                    //                    setCountDownTime(totleTime);
                    //                    mCountDownTimer.setCountDownTime(totleTime);
                    beginTime.setVisibility(View.VISIBLE);
                    beginTime.setText(Utils.getDate_M(totleTime));
                    investTimeTv.setText("开始投标时间");
                    //                    Log.d("aa", "onEventMainThread: " +product.getBookCouponNumber());
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
                //tvStartBuy.setVisibility(View.GONE);
                long time = product.getBidEndTime() - System.currentTimeMillis();
                time = time / 1000;
                if (time > 0 && time < 30 * 24 * 60 * 60) {
                    //                    Logger.e("product.getBidEndTime():"+product.getBidEndTime());
                    //                    setCountDownTime(product.getBidEndTime());
                    investTimeTv.setText("剩余投标时间");
                    beginTime.setVisibility(View.GONE);
                    mCountDownTimer.setVisibility(View.VISIBLE);
                    mCountDownTimer.setCountDownTime(mContext,product.getBidEndTime());
                }
            }
            //            if (99.99 < pro && pro < 100) {
            //                pro = 99.99f;
            //            }
            if (loanstate == 1) {
                ((NumberProgressBar) view.findViewById(R.id.product_progress)).setProgress(0);
            } else {
                ((NumberProgressBar) view.findViewById(R.id.product_progress)).setProgress(pro);
            }

            if (pro >= 100) {
                //                if(counter!=null)
                //                {
                //                    counter.cancel();
                //                }
                mCountDownTimer.cancel();
                //                countDown.setVisibility(View.VISIBLE);
                // tvStartBuy.setVisibility(View.GONE);
                if (invest != null) {
                    invest.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    //                    invest.setTextColor(Color.parseColor("#999999"));
                    invest.setEnabled(false);
                } else {
                    invest.setEnabled(true);
                }
            }

            TextView payType = ((TextView) view.findViewById(R.id.payType));
            //            TextView borrower = ((TextView) view.findViewById(R.id.borrower));
            TextView behoof = ((TextView) view.findViewById(R.id.behoof));
            TextView riskGrade = ((TextView) view.findViewById(R.id.riskGrade));
            //            borrower.setText(product.getBorrower());
            behoof.setText(product.getBehoof());
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

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("出借成功");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
