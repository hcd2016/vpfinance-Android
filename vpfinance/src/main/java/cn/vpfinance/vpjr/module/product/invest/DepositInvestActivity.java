package cn.vpfinance.vpjr.module.product.invest;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.dialog.RechargeCloseDialog;
import cn.vpfinance.vpjr.module.voucher.NewSelectVoucherActivity;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.AddRateBean;
import cn.vpfinance.vpjr.gson.LoanProtocolBean;
import cn.vpfinance.vpjr.model.AddRateInfo;
import cn.vpfinance.vpjr.model.BuyResult;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.DepositInvestInfo;
import cn.vpfinance.vpjr.model.FundOverInfo;
import cn.vpfinance.vpjr.model.Voucher;
import cn.vpfinance.vpjr.model.VoucherArray;
import cn.vpfinance.vpjr.model.VoucherEvent;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.product.success.ProductInvestSuccessActivity;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import de.greenrobot.event.EventBus;

/**
 * Created by zzlz13 on 2017/4/18.
 */

public class DepositInvestActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.productTitle)
    TextView productTitle;
    @Bind(R.id.productRate)
    TextView productRate;
    @Bind(R.id.productMonth)
    TextView productMonth;
    @Bind(R.id.productIssueLoan)
    TextView productIssueLoan;
    @Bind(R.id.productRefundWay)
    TextView productRefundWay;
    //    @Bind(R.id.info)
//    TextView info;
//    @Bind(R.id.select_type)
//    LinearLayout select_type;
    @Bind(R.id.productAvailMoney)
    TextView productAvailMoney;
    @Bind(R.id.cashBalance)
    TextView cashBalance;
    @Bind(R.id.click_recharge)
    TextView click_recharge;
    @Bind(R.id.etRechargeMoney)
    EditText etRechargeMoney;
    @Bind(R.id.click_all_invest)
    TextView click_all_invest;
    @Bind(R.id.tvPredictMoney)
    TextView tvPredictMoney;
    @Bind(R.id.tvUseVoucher)
    TextView tvUseVoucher;
    @Bind(R.id.selectVoucher)
    LinearLayout selectVoucher;
    @Bind(R.id.tvCalcedMoney)
    TextView tvCalcedMoney;
    @Bind(R.id.click_invest)
    Button click_invest;
    @Bind(R.id.mCheckBox)
    Button mCheckBox;
    @Bind(R.id.tvProtocal)
    TextView tvProtocal;
    @Bind(R.id.tvProtocal2)
    TextView tvProtocal2;
    @Bind(R.id.tv_buy)
    TextView tv_buy;

    public static final String INVEST_INFO = "investInfo";
    public static final String IS_ORDER = "order";//是否是预约购买

    private int pid;
    private int poolId;
    private HttpService mHttpService;
    private DepositInvestInfo investInfo;
    private HashSet<DepositInvestInfo.InvestScopeBean> selectTypes = new HashSet<>();
    private User user;
    private List<LoanProtocolBean.DataEntity> mData;
    private int mAddrateCount;
    private int usableVoucherCount = 0;
    private String mCanBuyMoney;
    private double raminMoney;
    private int timeType;
    private double rate;
    private int month;
    private TextInputDialogFragment tidf;
    private Dialog investLoadingDialog;
    private boolean order;
    String byStagestype = "";
    private String investMoney;
    private double canPresellMoney = 0;
    private long user_id;
    private double cashBalanceDouble;
    private boolean isSettingPwd = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_invest);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null){
            pid = intent.getIntExtra("pid",0);
            poolId = intent.getIntExtra("poolId",0);
            order = intent.getBooleanExtra(IS_ORDER,false);
            investInfo = (DepositInvestInfo)intent.getSerializableExtra(INVEST_INFO);
        }
        mHttpService = new HttpService(this,this);


        titleBar.reset().setHeadBackVisible(View.VISIBLE);
        if (order){
            titleBar.setTitle("立即预约");
            click_invest.setText("立即预约");
            tv_buy.setText("可预约金额");
        }else{
            setTitle("我要出借");
            click_invest.setText("我要出借");
            tv_buy.setText("可购金额");
        }

        click_invest.setOnClickListener(this);
        selectVoucher.setOnClickListener(this);

        initView(investInfo);

        user = DBUtils.getUser(this);
        if (user == null) {
            Toast.makeText(this, "请登录.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        user_id = user.getUserId();
        mHttpService.getFundOverInfo("" + user.getUserId(), Constant.AccountLianLain);

//        if (isOrder) {
//            titleBar.reset().setTitle("立即预约").setHeadBackVisible(View.VISIBLE);
//            btnInvest.setText("立即预约");
//            tv_userOrder.setVisibility(View.VISIBLE);
//            Utils.setTwoTextColor("您将使用1张预约券进行预约", "1", Color.RED, tv_userOrder);
//        }

        mHttpService.getVoucherlist("" + 1, ""+pid);//获取代金券
        mHttpService.getAddRateInvest(1 + "", ""+pid, "");//出借可用加息券
        mHttpService.getProtocol(""+pid,""+poolId);//协议
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHttpService.getUserInfo();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        this.selectTypes = (HashSet<DepositInvestInfo.InvestScopeBean>)savedInstanceState.getSerializable("selectTypes");
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putSerializable("selectTypes",selectTypes);
        super.onSaveInstanceState(outState);
    }

    private void initView(final DepositInvestInfo investInfo) {
        if (investInfo == null) return;
        timeType = investInfo.timeType;
        productTitle.setText(investInfo.title);
        rate = investInfo.rate / 100;
        productRate.setText(FormatUtils.formatAbout(investInfo.rate) +"%");
        productMonth.setText(investInfo.month);
        this.byStagestype = investInfo.byStagesType;
        //获取month
        try{
            String temp;
            if (timeType == 1){
                temp = investInfo.month.split("个月")[0];
            }else{
                temp = investInfo.month.split("天")[0];
            }
            month = Integer.parseInt(temp);
        }catch (Exception e){
            e.printStackTrace();
        }
        raminMoney = investInfo.raminMoney;
        productIssueLoan.setText(FormatUtils.formatAbout(investInfo.surplusMoney) +"元");
        productRefundWay.setText(investInfo.returnWay);
//        String investScopeInfo = investInfo.investScopeInfo;
//        String replace = investScopeInfo.replace("++", "\n");
//        info.setText(replace);

//        if (order){
//            canPresellMoney = ((float)(investInfo.sumMoney * investInfo.bookPercent - (investInfo.sumMoney - investInfo.raminMoney)));
//            productAvailMoney.setText(FormatUtils.formatAbout(canPresellMoney)+"元");
//        }else{
//            canPresellMoney = (float) raminMoney;
//            productAvailMoney.setText(FormatUtils.formatSaveAfterTwo(canPresellMoney)+"元");
//        }
        //直接使用canBuyMoney
        canPresellMoney = investInfo.raminMoney;
        productAvailMoney.setText(FormatUtils.formatAbout(canPresellMoney)+"元");

        /*if (investInfo.investScope != null){
            select_type.removeAllViews();
            final List<DepositInvestInfo.InvestScopeBean> investScope = investInfo.investScope;
            for (final DepositInvestInfo.InvestScopeBean investScopeBean : investInfo.investScope) {
                View view = LayoutInflater.from(this).inflate(R.layout.view_deposit_invest_type, null);
                ((TextView) view.findViewById(R.id.name)).setText(investScopeBean.key);
                LinearLayout click_content = (LinearLayout) view.findViewById(R.id.click_content);
                final CheckBox cb_select = (CheckBox) view.findViewById(R.id.cb_select);
                click_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = cb_select.isChecked();
                        cb_select.setChecked(!checked);
                        if (checked){
                            //移除
                            selectTypes.remove(investScopeBean);
                        }else{
                            //添加
                            selectTypes.add(investScopeBean);
                        }
                        double tempMoney = 0;
                        for(Iterator it=selectTypes.iterator();it.hasNext();){
                            DepositInvestInfo.InvestScopeBean next = (DepositInvestInfo.InvestScopeBean) it.next();
                            tempMoney += next.money;
                        }
                        if (selectTypes.size() == 0 || investScope.size() == selectTypes.size()){
                            //全选和全不选,填写剩余金额
                            double value = investInfo.raminMoney;
                            productAvailMoney.setText(FormatUtils.formatSaveAfterTwo(FormatUtils.formatAbout(value))+"元");
                            canPresellMoney = value;
                        }else{
                            productAvailMoney.setText(FormatUtils.formatSaveAfterTwo(FormatUtils.formatAbout(tempMoney))+"元");
                            canPresellMoney = tempMoney;
                        }
                    }
                });
                select_type.addView(view);
            }
        }*/

        click_recharge.setOnClickListener(this);
        click_all_invest.setOnClickListener(this);
        setEditTextListener(etRechargeMoney);

        if(canPresellMoney == 0){
            click_invest.setText("预约已满额");
            click_invest.setEnabled(false);
        }
    }

    private void setEditTextListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

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
                if ("1".equals(mType)) {
                    double calcedMoney = 0;
                    if (tvCalcedMoney != null) {
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
                            tvCalcedMoney.setTextColor(getResources().getColor(R.color.main_color));
                            tvCalcedMoney.setText("已抵扣代金券" + String.format("%.2f", calcedMoney) + "元");
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
                    mHttpService.getPredictMoney("" + poolId, etContent,true);
//                    String predictMoney = calcPredictMoney(etContent);
//                    tvPredictMoney.setText(predictMoney + "元");
                } else {
                    tvPredictMoney.setText("0.00元");
                }
            }
        });
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_PredictMoney.ordinal()) {
            if (json != null) {
                try {
                    double income = json.optDouble("income");
                    tvPredictMoney.setText(FormatUtils.formatAbout(income) + "元");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()){
            mHttpService.onGetUserInfo(json, user);

            DaoMaster.DevOpenHelper dbHelper;
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;

            UserDao dao = null;
            if (dao == null) {
                dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
                db = dbHelper.getWritableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                dao = daoSession.getUserDao();
            }
            if (user != null) {
                isSettingPwd = user.getHasTradePassword();
                if (dao != null && AppState.instance().logined()) {
                    dao.insertOrReplace(user);
                }
            }
        }else if (reqId == ServiceCmd.CmdId.CMD_Addrate_Ticket_invest.ordinal()) {
            AddRateBean addRateBean = mHttpService.onGetAddRateInvest(json);
            if (addRateBean != null) {
                mAddrateCount = addRateBean.getTotalCount();
                tvUseVoucher.setText(mAddrateCount + usableVoucherCount + "张可用");
            }
        }else if (reqId == ServiceCmd.CmdId.CMD_FundOverView.ordinal()) {
            FundOverInfo fundOverInfo = mHttpService.onGetFundOverInfo(json);
            /*可用余额*/
            mCanBuyMoney = fundOverInfo.getCashBalance();
            try {
                cashBalanceDouble = Double.parseDouble(mCanBuyMoney);
                cashBalance.setText(FormatUtils.formatAbout(mCanBuyMoney) + "元");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (reqId == ServiceCmd.CmdId.CMD_VOUCHERLIST_V2.ordinal()) {
            if (json != null) {
                VoucherArray voucherArray = mHttpService.onVoucherArray(json);
                ArrayList<Voucher> voucherList = voucherArray.getVoucherList();
                if (voucherList != null && voucherList.size() != 0) {
                    usableVoucherCount = voucherArray.useable;
                } else {
                    usableVoucherCount = 0;
                }
                tvUseVoucher.setText(mAddrateCount + usableVoucherCount + "张可用");
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_LOAN_PROTOCOL.ordinal()) {
            LoanProtocolBean loanProtocolBean = mHttpService.onGetgetProtocol(json);
            if (loanProtocolBean != null) {
                mData = loanProtocolBean.getData();
                if (mData != null && mData.size() != 0) {
                    final LoanProtocolBean.DataEntity dataEntity = mData.get(0);
                    if (dataEntity != null) {
                        tvProtocal.setText(dataEntity.getTitle());
                        tvProtocal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoWeb(dataEntity.getUrl(),dataEntity.getTitle());
                            }
                        });
                    }
                }
                if (mData != null && mData.size() == 2) {
                    final LoanProtocolBean.DataEntity dataEntity1 = mData.get(1);
                    if (dataEntity1 != null) {
                        tvProtocal2.setVisibility(View.VISIBLE);
                        tvProtocal2.setText(dataEntity1.getTitle());
                        tvProtocal2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoWeb(dataEntity1.getUrl(),dataEntity1.getTitle());
                            }
                        });
                    }
                }
            }
        }else if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Pool_Invest.ordinal()){
            Logger.e(json.toString());
            if (investLoadingDialog != null && investLoadingDialog.isShowing())    investLoadingDialog.dismiss();

            BuyResult res = mHttpService.onPlankFinish(json,true);
            if (res != null) {
                String redPacketState = json.optString(ProductInvestSuccessActivity.REDPACKETSTATE);
                int status = res.getStatus();
                String desc = res.getStatusDesc();
                if ("投标成功".equals(desc)) {
                    Intent intent = new Intent(this, ProductInvestSuccessActivity.class);
                    //                        intent.putExtra(ProductInvestSuccessActivity.DRAWTIME, drawTime);
                    intent.putExtra(ProductInvestSuccessActivity.DRAWTIME, res.getCashTime());
                    //                        intent.putExtra(ProductInvestSuccessActivity.STATE, isOneYear);
                    intent.putExtra(ProductInvestSuccessActivity.STATE, res.getDoubleFestival());
                    intent.putExtra(ProductInvestSuccessActivity.IMAGEURL, res.getImageUrl());
                    intent.putExtra(ProductInvestSuccessActivity.ACTIVITYURL, res.getActivityUrl());
                    intent.putExtra(ProductInvestSuccessActivity.INVESTMONEY, investMoney);
                    intent.putExtra(ProductInvestSuccessActivity.PID, "" + pid);
                    intent.putExtra(ProductInvestSuccessActivity.REDPACKETSTRURL, "" + res.getRedpacketstrurl());
                    intent.putExtra(ProductInvestSuccessActivity.REDPACKETCOUNT, "" + res.getRedPacketCount());
                    intent.putExtra(ProductInvestSuccessActivity.ALLOW_TRANSFER, "false");
                    intent.putExtra(ProductInvestSuccessActivity.ISALLOWTRIP, "false" );
                    intent.putExtra(ProductInvestSuccessActivity.PRODUCT_TYPE, "");
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
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (isFinishing())  return;
        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Pool_Invest.ordinal()){
            if (investLoadingDialog != null && investLoadingDialog.isShowing())    investLoadingDialog.dismiss();

            Utils.Toast(this,"网络不给力 ，请稍后重试");
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.SWITCH_TAB_NUM,1);
            gotoActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.click_invest:
                invest();
                break;
            case R.id.click_recharge:
                //充值入口
                boolean isAllowRecharge = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_ALLOW_RECHARGE);
                if (isAllowRecharge){
                    AlertDialogUtils.confirmGoRecharg(this);
                }else{
                    new RechargeCloseDialog().show(getFragmentManager(),"RechargeCloseDialog");
                }
                break;
            case R.id.click_all_invest:
                try {
                    if (cashBalanceDouble < canPresellMoney) {
                        etRechargeMoney.setText(FormatUtils.formatAbout(cashBalanceDouble));
                    } else {
                        etRechargeMoney.setText(FormatUtils.formatAbout(canPresellMoney));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.selectVoucher:
                if (mAddrateCount + usableVoucherCount > 0) {
                    double moneyValue = 0;
                    String money = null;
                    money = etRechargeMoney.getText().toString();

                    if (!TextUtils.isEmpty(money)) {
                        try {
                            moneyValue = Double.parseDouble(money);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    if (moneyValue < 0.00) {
                        Toast.makeText(this, "请输入出借金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(this, NewSelectVoucherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ProductInvestActivity.VOUCHEREVENT, mVoucherEvent);
                    bundle.putParcelable(ProductInvestActivity.ADDRATEEVENT, mAddRateInfo);
                    bundle.putString(ProductInvestActivity.PRODUCT_ID, pid + "");
                    bundle.putDouble(ProductInvestActivity.NAME_MONEY, moneyValue);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * 出借
     */
    private void invest() {
        investMoney = etRechargeMoney.getText().toString();
        if (pid == 0){
            Utils.Toast(this,"数据有误,请刷新界面");
            return;
        }
        if (TextUtils.isEmpty(investMoney)){
            Utils.Toast(this,"请输入购买金额");
            return;
        }

//        if (selectTypes.size() == 0){
//            Utils.Toast(this,"请选择您认可的项目类型");
//            return;
//        }

//        byStagestype = "";
        if (TextUtils.isEmpty(byStagestype)){
            Utils.Toast(this,"请选择您认可的项目类型");
        }
        try{
            double investMoneyDouble = Double.parseDouble(investMoney);
            if (investMoneyDouble < 100){//输入金额不低于剩余额度请重新输入
                if (canPresellMoney > 100){
                    Toast.makeText(this, "100元起投请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if (investMoneyDouble < canPresellMoney) {
                        Toast.makeText(this, "输入金额不低于剩余额度请重新输入", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            try{
                if (investMoneyDouble > cashBalanceDouble) {
                    AlertDialogUtils.showRechargCloseDialog(this, "温馨提示", "账户余额不足，请先充值", "取消", "充值");
                    return;
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
            }

            //暂时写死,业务扩大再改
//            if (selectTypes.size() == 1){
//                Iterator it=selectTypes.iterator();
//                DepositInvestInfo.InvestScopeBean next = (DepositInvestInfo.InvestScopeBean) it.next();
//                if (next.money < investMoneyFloat){
//                    Toast.makeText(this, next.key+"当前剩余认购余额为"+next.money+",请调整您的出借金额或分期类型",Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }

//            for(Iterator it=selectTypes.iterator();it.hasNext();){
//                DepositInvestInfo.InvestScopeBean next = (DepositInvestInfo.InvestScopeBean) it.next();
//                byStagestype = byStagestype + next.value+",";
//            }
//            Logger.e("byStagestype: "+byStagestype);

            if (investMoneyDouble > canPresellMoney){
                Toast.makeText(this, "输入的购买金额大于该产品的可购金额", Toast.LENGTH_SHORT).show();
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        user = DBUtils.getUser(this);

        if (user == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        user_id = user.getUserId();
        Logger.e("user.getHasTradePassword(): "+user.getHasTradePassword());
        Logger.e("user.getUserId(): "+user.getUserId());

        if (user != null) {
            if (!isSettingPwd) {
                Intent intent = new Intent(DepositInvestActivity.this, PasswordChangeActivity.class);
                intent.putExtra(PasswordChangeActivity.EXTRA_KEY_INDEX, 1);
                AlertDialogUtils.showAlertDialog(DepositInvestActivity.this, "您未设置交易密码", "取消", "去设置", intent);
                return;
            }
        }

        tidf = TextInputDialogFragment.newInstance("交易密码", "请输入交易密码", true);
        tidf.setOnTextConfrimListener(new TextInputDialogFragment.onTextConfrimListener() {

            @Override
            public boolean onTextConfrim(String value) {
                if (value != null) {
                    //弹出dialog防止再次点击请求
                    investLoadingDialog = AlertDialogUtils.showInvestLoadingDialog(DepositInvestActivity.this);

                    Md5Algorithm md5 = Md5Algorithm.getInstance();
                    value = md5.md5Digest(value.getBytes());
                    //默认1预约购买
                    String orderStr = order ? "1" : "0";
                    mHttpService.getDepositProductInvest(""+poolId,""+investMoney,""+ user_id,value,vouchers,couponId,orderStr,byStagestype);
                    tidf.dismiss();
                }
                return false;
            }
        });
        tidf.show(getSupportFragmentManager(), "inputPwd");
    }

    private VoucherEvent mVoucherEvent;
    private AddRateInfo mAddRateInfo;
    private String mType; // 1代金券, 2加息券
    private String couponId;
    private double voucherValue = 0;
    private double voucherrate = 0;
    private int[] vouchersArray = null;//已选优惠券id
    private String vouchers;
    private double mValue;
    private int mTemp = -1;
    private int mAddRatePeriod;

    public void onEventMainThread(VoucherEvent event) {
        if (isFinishing())  return;
        if (event == null) return;
        mAddRateInfo = null;
        couponId = "";
        mVoucherEvent = event;
        mType = event.getType();
        if ("1".equals(mType)) {//代金券页面过来
            voucherValue = event.getVoucherValue();
            double calcedMoney = event.getCalcedMoney();
            voucherrate = event.getVoucherrate();
            tvCalcedMoney.setTextColor(getResources().getColor(R.color.main_color));
            tvCalcedMoney.setText("已抵扣" + FormatUtils.formatAbout(calcedMoney) + "元");
            vouchersArray = event.getVouchersArray();
            if (vouchersArray != null) {
                StringBuilder sb = new StringBuilder();
                for (int v : vouchersArray) {
                    sb.append(v).append(',');
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
        tvCalcedMoney.setTextColor(getResources().getColor(R.color.main_color));
        tvCalcedMoney.setText("预计加息" + FormatUtils.formatAbout(event.getAddRateMoey()) + "元");
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
}
