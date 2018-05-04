package cn.vpfinance.vpjr.module.product.transfer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.module.product.record.ProductInvestListActivity;
import cn.vpfinance.vpjr.module.product.invest.RegularProductActivity;
import cn.vpfinance.vpjr.module.product.record.TransferAvailableTimeActivity;
import cn.vpfinance.vpjr.greendao.LoanRecord;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.util.Common;

/**
 *债权转让- 转让专区
 */
public class TransferProductActivity extends BaseActivity implements View.OnClickListener {

    public static final String PID        = "pid";
    public static final String NATIVE_PID = "nativePid";
    private TextView              tvProductName;
    //    private SmallCircularProgressView circle;
    private NumberProgressBar     presellNumberbar;
    private TextView              countdown_day;
    private TextView              countdown_hour;
    private TextView              countdown_minute;
    private TextView              countdown_second;
    private TextView              tvProductMonth;
    private TextView              tvProductAvailableMoney;
    private TextView              tvProductTotalMoney;
    private TextView              tvProductRate;
    private TextView              tvProductNativeMoney;
    private Button                btnInvest;
    private HttpService           mHttpService;
    private Context               mContext;
    private FinanceProduct        product;
    //    private TextView                  tvProgress;
    private ArrayList<LoanRecord> rList;
    private MyCounter             counter;
    private View                  countDowcn;
    //    private TextView                  mTv_name;
    private TextView              tvEarningsYield;
    private TextView              tvRefund;
    private TextView              tvTransferWay;
    private String                mPid;


    public static void goTransferProductActivity(Context context, String pid) {
        if (context != null) {
            Intent intent = new Intent(context, TransferProductActivity.class);
            intent.putExtra(PID, pid);
            //            intent.putExtra(NATIVE_PID,nativePid);
            context.startActivity(intent);
        }
    }

    private void initFind() {
        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("产品详情").setHeadBackVisible(View.VISIBLE);
        tvProductName = ((TextView) findViewById(R.id.tvProductName));
        //        tvProgress = ((TextView) findViewById(R.id.tvProgress));
        //        circle = ((SmallCircularProgressView) findViewById(R.id.circle));
        presellNumberbar = ((NumberProgressBar) findViewById(R.id.presellNumberbar));
        countDowcn = findViewById(R.id.countDown);
        countdown_day = ((TextView) findViewById(R.id.countdown_day));
        countdown_hour = (TextView) findViewById(R.id.countdown_hour);
        countdown_minute = (TextView) findViewById(R.id.countdown_minute);
        countdown_second = (TextView) findViewById(R.id.countdown_second);
        tvProductMonth = (TextView) findViewById(R.id.tvProductMonth);
        tvProductAvailableMoney = (TextView) findViewById(R.id.tvProductAvailableMoney);
        tvProductTotalMoney = (TextView) findViewById(R.id.tvProductTotalMoney);
        tvProductRate = (TextView) findViewById(R.id.tvProductRate);
        tvProductNativeMoney = (TextView) findViewById(R.id.tvProductNativeMoney);
//        mTv_name = (TextView) findViewById(R.id.tvProductNativeName);
        tvEarningsYield = (TextView) findViewById(R.id.tvEarningsYield);
        tvRefund = (TextView) findViewById(R.id.tvRefund);
        tvTransferWay = (TextView) findViewById(R.id.tvTransferWay);

        findViewById(R.id.clickToNativeProduct).setOnClickListener(this);
        findViewById(R.id.clickToRecord).setOnClickListener(this);
        findViewById(R.id.clickToAvailableTime).setOnClickListener(this);
        btnInvest = ((Button) findViewById(R.id.btnInvest));
        btnInvest.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_product2);
        mContext = this;
        mHttpService = new HttpService(this,this);
        initFind();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mPid)) {
            mHttpService.getFixProduct(mPid,""+0);
        }
    }

    protected void initView() {
        Intent intent = getIntent();
        if (intent != null){
            mPid = intent.getStringExtra(PID);
            if (!TextUtils.isEmpty(mPid))
                mHttpService.getFixProduct(mPid,""+0);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo.ordinal()) {
            if (json != null) {
//                    String debtUserName = json.optString("debtUserName");
//                    mTv_name.setText(debtUserName);
                String disType = json.optString("disType");
                tvTransferWay.setText(disType);

                product = new FinanceProduct();
                rList = mHttpService.onGetProductDetail(this, json, product);
                if(product !=null){

                    tvProductMonth.setText(product.getMonth() + "个月");
                    double availableMoney = product.getIssueLoan() - product.getTotal_tend_money();
                    tvProductAvailableMoney.setText(String.format("%.2f",availableMoney));
                    tvProductTotalMoney.setText(String.format("%.2f",product.getIssueLoan()));
                    tvProductRate.setText(String.format("%.1f", (product.getRate() * 100)) +"%");
                    tvProductNativeMoney.setText(String.format("%.2f", product.getOriginIssueLoan()));
                    tvEarningsYield.setText(String.format("%.2f", product.getPromitRate() * 100) + "%");

                    String pType = "";//1按月等额本息 2按月付息到期还本 3到期一次性还本息
                    switch ((int)product.getRefundWay()) {
                        case 1:
                            pType = mContext.getString(R.string.refundState1);
                            break;
                        case 2:
                            pType = mContext.getString(R.string.refundState2);
                            break;
                        case 3:
                            pType = mContext.getString(R.string.refundState3);
                            break;
                        default:
                            break;
                    }

                    tvRefund.setText(pType);

                    //1质押，2保证，3抵押，4信用，5实地
                    ImageView imageView = (ImageView) findViewById(R.id.ivProductState);
                    Common.productSubType(this,imageView,(int)product.getSubType());

                    long time = product.getBidEndTime() - System.currentTimeMillis();
//                    Logger.e("getBidEndTime:"+product.getBidEndTime()+",time:"+time);
                    if(counter!=null)
                    {
                        counter.cancel();
                    }
                    time = time/1000;
                    if(time>0)
                    {
                        Interval interval = new Interval(System.currentTimeMillis(),product.getBidEndTime());//
                        Period p = interval.toPeriod();
                        time = Math.max(time,0);
                        countDowcn.setVisibility(View.VISIBLE);
                        countdown_day.setText("" + (p.getWeeks() * 7 + p.getDays()));
                        countdown_hour.setText("" + p.getHours());
                        countdown_minute.setText("" + p.getMinutes());
                        countdown_second.setText("" + p.getSeconds());

                        counter = new MyCounter(product.getBidEndTime() - System.currentTimeMillis(), 1000, countDowcn,product.getBidEndTime());
                        counter.start();
                    }

                    tvProductName.setText(product.getLoanTitle());
                    float pro = (float)(100 * product.getTotal_tend_money()/product.getIssueLoan());//已买  / 总

                    int status = (int) product.getLoanstate();
                    String state = "立即投资";
                    switch (status)//1未发布 2进行中 3回款中 4已完成
                    {
                        case 1:
                            state = getString(R.string.productState1);//待售
                            btnInvest.setEnabled(false);
                            break;
                        case 2:
                            if (100 > pro) {
                                state = getString(R.string.productState2);
                                btnInvest.setEnabled(true);
                            }else{
                                state = "进行中";
                                if (product.getTotal_tend_money() >= product.getIssueLoan()) {
                                    state = "满标审核";
                                    if(counter!=null)
                                    {
                                        counter.cancel();
                                        countdown_day.setText(""+0);
                                        countdown_hour.setText(""+0);
                                        countdown_minute.setText("" + 0);
                                        countdown_second.setText("" + 0);
                                    }
                                }
                                btnInvest.setEnabled(false);
                            }
                            break;
                        case 3:
                            state = getString(R.string.productState3);
                            pro = 100;
                            btnInvest.setEnabled(false);
                            if(counter!=null)
                            {
                                counter.cancel();
                                countdown_day.setText(""+0);
                                countdown_hour.setText(""+0);
                                countdown_minute.setText("" + 0);
                                countdown_second.setText("" + 0);
                            }
                            break;
                        case 4:
                            state = getString(R.string.productState4);
                            pro = 100;
                            btnInvest.setEnabled(false);
                            if(counter!=null)
                            {
                                counter.cancel();
                                countdown_day.setText(""+0);
                                countdown_hour.setText(""+0);
                                countdown_minute.setText("" + 0);
                                countdown_second.setText("" + 0);
                            }
                            break;
                    }
                    btnInvest.setText(state);

                    presellNumberbar.setProgress(pro);
//                    tvProgress.setText(String.format("%.2f", pro) + "%");
                    if (pro >= 100){
                        btnInvest.setEnabled(false);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clickToNativeProduct:
                if (product != null && product.getOriginLoanId() != null){
                    RegularProductActivity.goRegularProductActivity(
                            mContext, "" + product.getOriginLoanId(),
                            1, -1, -1, mPid);
                }
                break;
            case R.id.clickToRecord:
                if (product != null && rList != null){
                    ProductInvestListActivity.goProductInvestListActivity(mContext, product.getPid(), product.getType()-1,-1,false);
                }
                break;
            case R.id.clickToAvailableTime:
                if (product != null && (!TextUtils.isEmpty(product.getNoRepayList()))){
                    TransferAvailableTimeActivity.goTransferAvailableTime(mContext,product.getNoRepayList(),""+product.getTotalPeriod());
                }
                break;
            case R.id.btnInvest:
                if (product != null){
                    Intent intent = new Intent(mContext, ProductInvestActivity.class);
                    intent.putExtra("pid", "" + product.getPid());
                    intent.putExtra(ProductInvestActivity.PRODUCT_TOTAL_MONEY, product.getIssueLoan());//转让总额
                    intent.putExtra(ProductInvestActivity.PRODUCT_NATIVE_MONEY, product.getOriginIssueLoan());//原始本金
                    intent.putExtra(ProductInvestActivity.TYPE_PRODUCT,ProductInvestActivity.TYPE_TRANSFER);
//                    FinanceApplication myApp = (FinanceApplication)getApplication();
//                    myApp.currentPid = ""+product.getPid();
                    startActivity(intent);
                }
                break;

        }
    }

    /* 定义一个倒计时的内部类 */
    class MyCounter extends CountDownTimer
    {
        private WeakReference<View> viewRef;
        private long endTime;
        public MyCounter(long millisInFuture, long countDownInterval,View countDown,long endTime)
        {
            super(millisInFuture, countDownInterval);
            viewRef = new WeakReference<View>(countDown);
            this.endTime = endTime;
        }

        @Override
        public void onFinish()
        {
            if (viewRef != null)
            {
                View countDown = viewRef.get();
                if (countDown != null)
                {
                    countDown.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            if (viewRef != null)
            {
                View countDown = viewRef.get();
                if (countDown != null)
                {
                    TextView countdown_day = (TextView)countDown.findViewById(R.id.countdown_day);
                    TextView countdown_hour = (TextView)countDown.findViewById(R.id.countdown_hour);
                    TextView countdown_minute = (TextView)countDown.findViewById(R.id.countdown_minute);
                    TextView countdown_second = (TextView)countDown.findViewById(R.id.countdown_second);


                    long time = millisUntilFinished;
//            time = 20*24*60*60 * 1000 + 8*60*60 * 1000 + 18*60 * 1000   + 16 * 1000;
                    time = time/1000;
                    if(time>0 && time < 30*24*60*60)
                    {
                        //System.currentTimeMillis() + 20*24*60*60 * 1000 + 8*60*60 * 1000   + 18*60 * 1000   + 16 * 1000
                        Interval interval = new Interval(System.currentTimeMillis(), endTime);//
                        Period p = interval.toPeriod();
                        time = Math.max(time,0);
                        //Period p = Period.seconds((int)time);
                        countDown.setVisibility(View.VISIBLE);

                        countdown_day.setText("" + (p.getWeeks() * 7 + p.getDays()));
                        countdown_hour.setText("" + p.getHours());
                        countdown_minute.setText("" + p.getMinutes());
                        countdown_second.setText("" + p.getSeconds());

                    }
                    else
                    {
                        countDown.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }
}
