package cn.vpfinance.vpjr.module.product.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.yintong.pay.utils.Md5Algorithm;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.RechargeCloseDialog;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.module.trade.RechargeActivity;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.model.BuyResult;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.CircularProgressView;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

public class RegularProductDetailFragment2 extends BaseFragment implements View.OnClickListener {

    private HttpService mHttpService = null;
    private View        mContentView = null;
    private Button                  invest;
    private EditText                etMoney;
    private FinanceProduct          product;
    private TextInputDialogFragment tidf;
    private User mUser = null;
    ;
    private CheckBox mUseVoucherCheckBox = null;

    private TextView countdown_day;
    private TextView countdown_hour;
    private TextView countdown_minute;
    private TextView countdown_second;

    private View countDown;

    private double mBuyMoney    = 0;
    private double mCanBuyMoney = 0;
    private double mRemainMoney = 0;

    private MyCounter   counter;
    private PopupWindow popupWindow;
    private View        popupWindowView;
    private TextView    popInvest;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //            case R.id.clickDismissPop:
            //
            //                break;
            //            case R.id.popInvest:
            //
            //                break;
        }
    }

    /* 定义一个倒计时的内部类 */
    class MyCounter extends CountDownTimer {
        private WeakReference<View>           viewRef;
        private WeakReference<FinanceProduct> prodRef;

        public MyCounter(long millisInFuture, long countDownInterval, View countDown, FinanceProduct product) {
            super(millisInFuture, countDownInterval);
            viewRef = new WeakReference<View>(countDown);
            prodRef = new WeakReference<FinanceProduct>(product);
        }

        @Override
        public void onFinish() {
            if (viewRef != null) {
                View countDown = viewRef.get();
                if (countDown != null) {
                    countDown.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (viewRef != null && prodRef != null) {
                View countDown = viewRef.get();
                FinanceProduct prod = prodRef.get();
                if (countDown != null && prod != null) {
                    TextView countdown_day = (TextView) countDown.findViewById(R.id.countdown_day);
                    TextView countdown_hour = (TextView) countDown.findViewById(R.id.countdown_hour);
                    TextView countdown_minute = (TextView) countDown.findViewById(R.id.countdown_minute);
                    TextView countdown_second = (TextView)countDown.findViewById(R.id.countdown_second);


                    long time = millisUntilFinished;
//            time = 20*24*60*60 * 1000 + 8*60*60 * 1000 + 18*60 * 1000   + 16 * 1000;
                    time = time/1000;
                    if(time>0 && time < 30*24*60*60)
                    {
                        //System.currentTimeMillis() + 20*24*60*60 * 1000 + 8*60*60 * 1000   + 18*60 * 1000   + 16 * 1000
                        Interval interval = new Interval(System.currentTimeMillis(), prod.getBidEndTime());//
                        Period p = interval.toPeriod();
                        time = Math.max(time,0);
                        //Period p = Period.seconds((int)time);
                        countDown.setVisibility(View.VISIBLE);

                        countdown_day.setText("" + (p.getWeeks() * 7 + p.getDays()));
                        countdown_hour.setText("" + p.getHours());
                        countdown_minute.setText("" + p.getMinutes());
                        countdown_second.setText("" + p.getSeconds());
                        //countDown.setText(p.getDays()+"天 " + p.getHours()+ "时 "+p.getMinutes()+"分 "+p.getSeconds()+" 秒");
                    }
                    else
                    {
                        countDown.setVisibility(View.GONE);
                    }


                }
            }
        }
    }

    @Override
    public void onStop() {
        if(counter!=null)
        {
            counter.cancel();
        }
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mHttpService = new HttpService(getActivity(), this);

        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        UserDao dao;

        dbHelper = new DaoMaster.DevOpenHelper(getActivity(), Config.DB_NAME , null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getUserDao();

        mContentView = inflater.inflate(R.layout.pager_regular_product_detail2, null);
//        etMoney = (EditText) mContentView.findViewById(R.id.etMoney);
        invest = (Button)mContentView.findViewById(R.id.invest);
//        mUseVoucherCheckBox = (CheckBox)mContentView.findViewById(R.id.use_voucher_checkbox);

        countDown = mContentView.findViewById(R.id.countDown);
        countdown_day = (TextView)mContentView.findViewById(R.id.countdown_day);
        countdown_hour = (TextView)mContentView.findViewById(R.id.countdown_hour);
        countdown_minute = (TextView)mContentView.findViewById(R.id.countdown_minute);
        countdown_second = (TextView)mContentView.findViewById(R.id.countdown_second);

//        TextView tvProtocal = (TextView)mContentView.findViewById(R.id.tvProtocal);
//        tvProtocal.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_PROTOCAL;
//                String method = ServiceCmd.getMethodName(cmdId);
//                String url = HttpService.getServiceUrl(method);
//                gotoWeb(url,"合同范本");
//            }
//        });

//        etMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    hideSoftKeyboard(v);
//                }
//            }
//        });

        if (dao != null) {
            QueryBuilder<User> qb = dao.queryBuilder();
            List<User> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                mUser = userList.get(0);
            }
        }

        invest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!AppState.instance().logined()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                showPopWindow();
            }
        });
        return mContentView;
    }

    private void showPopWindow() {
        popupWindowView = View.inflate(getActivity(), R.layout.popwindow_click_invest, null);
        popupWindowView.findViewById(R.id.clickDismissPop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow !=null && (popupWindow.isShowing())){
                    popupWindow.dismiss();
                }
            }
        });
        etMoney = (EditText) popupWindowView.findViewById(R.id.etMoney);
        mUseVoucherCheckBox = (CheckBox)popupWindowView.findViewById(R.id.use_voucher_checkbox);
        popInvest = (TextView) popupWindowView.findViewById(R.id.popInvest);
        popInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow !=null && (popupWindow.isShowing())){
                    if (!AppState.instance().logined()) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        return;
                    }
                    final String money = etMoney.getText().toString();
                    if (TextUtils.isEmpty(money)) {
                        Toast.makeText(getActivity(), "请输入购买金额", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        mBuyMoney = Double.parseDouble(money);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (mBuyMoney <= 0) {
                        Toast.makeText(getActivity(), "请输入正确的购买金额", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double loanUnit = product.getLoanUnit();
//                mRemainMoney =99.0;
                /*可购买余额小于loanUnit*/
                    if(mRemainMoney < 100){
                    /*默认全部买完*/
                        if(mBuyMoney > mRemainMoney){
                            String tmp = String.format("%.2f", mRemainMoney);
                            Toast.makeText(getActivity(),"可购份额不足请重新输入",Toast.LENGTH_SHORT).show();//
//                        mRemainMoney = (""+mRemainMoney).contains(".0") ? (int)mRemainMoney : mRemainMoney;
                            etMoney.setText(tmp);
//                        mBuyMoney = mRemainMoney;
                            return;
                        }
                        if(mBuyMoney < mRemainMoney)
                        {
                            String tmp = String.format("%.2f", mRemainMoney);
                            Toast.makeText(getActivity(),"输入金额不低于剩余额度请重新输入",Toast.LENGTH_SHORT).show();//+tmp+"元"
                            etMoney.setText(tmp);
                            return;
                        }
                    }else{
                    /*大于loanUnit，购买至少loanUnit*/
                        if(mBuyMoney < 100){
                            Toast.makeText(getActivity(), "100元起投请重新输入", Toast.LENGTH_SHORT).show();
                            return;
                        }
//                    if(mBuyMoney < loanUnit){
//                        Toast.makeText(getActivity(), "输入金额最小为"+loanUnit, Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    }
                    if (mBuyMoney > mRemainMoney) {
                        Toast.makeText(getActivity(), "输入的购买金额大于该产品的可购金额", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (mBuyMoney > mCanBuyMoney) {
                        AlertDialogUtils.showRechargCloseDialog(getActivity(), "温馨提示", "账户余额不足，请先充值", "取消", "充值");
                        return;
                    }

                    if (mUser != null) {
                        String mRealname = mUser.getRealName();
                        String idCard = mUser.getIdentityCard();

                        if (!mUser.getHasTradePassword()) {
                            Toast.makeText(getActivity(),"请先设置交易密码",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), PasswordChangeActivity.class);
                            intent.putExtra(PasswordChangeActivity.EXTRA_KEY_INDEX, 1);
                            startActivity(intent);
                        }
                    }

                    tidf = TextInputDialogFragment.newInstance("交易密码", "请输入交易密码",true);
                    tidf.setOnTextConfrimListener(new TextInputDialogFragment.onTextConfrimListener()
                    {
                        @Override
                        public boolean onTextConfrim(String value)
                        {
                            if(value!=null)
                            {
                                if(product!=null)
                                {
                                    String uid = AppState.instance().getSessionCode();
                                    Md5Algorithm md5 = Md5Algorithm.getInstance();
                                    value = md5.md5Digest(value.getBytes());
                                    mHttpService.plank("" + product.getPid(), "" + mBuyMoney, uid, value, mUseVoucherCheckBox.isChecked(),"","","");
                                }
                            }
                            return false;
                        }
                    });
                    tidf.show(getChildFragmentManager(), "inputPwd");
                }
            }
        });


        popupWindow = new PopupWindow(popupWindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(getActivity().findViewById(R.id.parent), Gravity.BOTTOM, 0, 0);
        popupWindow.setFocusable(true);
        backgroundAlpha(0.5f);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        popupWindow.update();
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onResume()
    {
        super.onResume();
//		doFetchLoanInfo();
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json)
    {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_PLANK.ordinal()) {
            if (json != null && isAdded()) {

                ArrayMap<String,String> map = new ArrayMap<String,String>();
                map.put("type","Regular");
                map.put("money", "" + RechargeActivity.getStatisticsMoney(mBuyMoney));


                BuyResult res = mHttpService.onPlankFinish(json,false);
                if (res != null) {
                    String desc = res.getStatusDesc();
                    int status = res.getStatus();
                    map.put("status","" + status);
                    if ("投标成功".equals(desc)) {
                        if (tidf != null) {
                            tidf.dismiss();

                        }
                        showSuccessDialog();
                    }

                    Utils.Toast(getActivity(), desc);
                }
                MobclickAgent.onEvent(getActivity(), "Buy", map);

//                 mHttpService.onGetProductDetail(getActivity(),json, product);
            }
        }
    }

    public void onEventMainThread(FinanceProduct event) {
        if (event != null && isAdded()) {
            product = event;
            long time = product.getBidEndTime() - System.currentTimeMillis();

//            time = 20*24*60*60 * 1000 + 8*60*60 * 1000 + 18*60 * 1000   + 16 * 1000;
            time = time/1000;
            if(time>0 && time < 30*24*60*60)
            {
                //System.currentTimeMillis() + 20*24*60*60 * 1000 + 8*60*60 * 1000   + 18*60 * 1000   + 16 * 1000
                Interval interval = new Interval(System.currentTimeMillis(), product.getBidEndTime());//
                Period p = interval.toPeriod();
                time = Math.max(time,0);
                //Period p = Period.seconds((int)time);
                countDown.setVisibility(View.VISIBLE);

                countdown_day.setText("" + (p.getWeeks() * 7 + p.getDays()));
                countdown_hour.setText("" + p.getHours());
                countdown_minute.setText("" + p.getMinutes());
                countdown_second.setText("" + p.getSeconds());
                //countDown.setText(p.getDays()+"天 " + p.getHours()+ "时 "+p.getMinutes()+"分 "+p.getSeconds()+" 秒");

                counter = new MyCounter(product.getBidEndTime() - System.currentTimeMillis(), 1000, countDown,product);
                counter.start();
            }
            else
            {
                countDown.setVisibility(View.GONE);
            }



            View view = getView();
            TextView name = ((TextView) view.findViewById(R.id.product_name));
            name.setText(product.getLoanTitle());
            double rate = product.getRate();
            mRemainMoney = product.getIssueLoan() - product.getTotal_tend_money();
            if(null != mUser && AppState.instance().logined()) {
                mCanBuyMoney = mUser.getCashBalance();
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String remainMoney = decimalFormat.format(mRemainMoney);
            if(remainMoney!=null && remainMoney.endsWith(".00"))
            {
                remainMoney = remainMoney.substring(0,remainMoney.length()-3);
//                if(remainMoney.endsWith("0000"))
//                {
//                    remainMoney = totalMoney.substring(0,remainMoney.length()-4);
//                    remainMoney = totalMoney + "万";
//                }
            }
            String totalMoney = decimalFormat.format(product.getIssueLoan());
            if(totalMoney!=null && totalMoney.endsWith(".00"))
            {
                totalMoney = totalMoney.substring(0,totalMoney.length()-3);
//                if(totalMoney.endsWith("0000"))
//                {
//                    totalMoney = totalMoney.substring(0,totalMoney.length()-4);
//                    totalMoney = totalMoney + "万";
//                }
            }
            rate *= 100;
            String showRate = String.format("%.1f", rate);
            if (showRate.endsWith(".0")) {
                showRate = showRate.substring(0, showRate.length() - 2);
            }
            ((TextView) view.findViewById(R.id.product_rate)).setText(showRate + "%");
            ((TextView) view.findViewById(R.id.product_total_money)).setText("" + totalMoney);
            if(TextUtils.isEmpty(remainMoney))
            {
                remainMoney = "0";
            }

            if(mRemainMoney < 100){
//                etMoney.setHint("");
//                etMoney.setText(String.format("%.2f", mRemainMoney));
            }
            ((TextView) view.findViewById(R.id.product_left)).setText("" + remainMoney);
            ((TextView) view.findViewById(R.id.product_term)).setText(event.getMonth() + "个月");
            int pro = (int)(100 * product.getTotal_tend_money()/product.getIssueLoan());


            String state = getString(R.string.productState2);
            switch ((int)product.getLoanstate())//1未发布 2进行中 3回款中 4已完成
            {
                case 1:
                    state = getString(R.string.productState1);//待售
                    invest.setEnabled(false);
                    break;
                case 2:
                    if (100 > pro) {
                        state = getString(R.string.productState2);
                        invest.setEnabled(true);
                    }else{
                        state = "进行中";
                        if (product.getTotal_tend_money() >= product.getIssueLoan()) {
                            state = "满标审核";
                        }
                        invest.setEnabled(false);
                    }
                    break;
                case 3:
                    state = getString(R.string.productState3);
                    pro = 100;
                    invest.setEnabled(false);
                    break;
                case 4:
                    state = getString(R.string.productState4);
                    pro = 100;
                    invest.setEnabled(false);
                    break;
            }
            invest.setText(state);

            ((CircularProgressView) view.findViewById(R.id.circle)).setProgress(pro);
            TextView number = (TextView) view.findViewById(R.id.numberbar);
            number.setText(pro+"%");

            if (pro >= 100) {
                if(counter!=null)
                {
                    counter.cancel();
                }
                countDown.setVisibility(View.GONE);
                if(invest!=null)
                {
                    invest.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    invest.setTextColor(Color.parseColor("#999999"));
                    invest.setEnabled(false);
                }
                else
                {
                    invest.setEnabled(true);
                }
            }

            TextView payType = ((TextView) view.findViewById(R.id.payType));
            TextView borrower = ((TextView) view.findViewById(R.id.borrower));
            TextView behoof = ((TextView) view.findViewById(R.id.behoof));
            TextView riskGrade = ((TextView) view.findViewById(R.id.riskGrade));
            borrower.setText(product.getBorrower());
            behoof.setText(product.getBehoof());
            riskGrade.setText(product.getRiskGrade());

//            TextView tvAtrri = ((TextView) view.findViewById(R.id.tvAtrri));

            String pType = "";//1按月等额本息 2按月付息到期还本 3到期一次性还本息
            switch ((int)product.getRefundWay()) {
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

//            String att = "";
            Drawable right = null;
            //1质押，2保证，3抵押，4信用，5实地
//            switch ((int)product.getSubType()) {
//                case 1:
//                    att = "质押";
//                    right = getResources().getDrawable(R.drawable.stype_zhi);
//                    break;
//                case 2:
//                    att = "保证";
//                    right = getResources().getDrawable(R.drawable.stype_bao);
//                    break;
//                case 3:
//                    att = "抵押";
//                    right = getResources().getDrawable(R.drawable.stype_ya);
//                    break;
//                case 4:
//                    att = "信用";
//                    right = getResources().getDrawable(R.drawable.stype_xin);
//                    break;
//                case 5:
//                    att = "实地";
//                    right = getResources().getDrawable(R.drawable.stype_ya);
//                    break;
//                default:
//                    break;
//            }

//            tvAtrri.setText(att);
            name.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void showSuccessDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("投资成功");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
