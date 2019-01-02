package cn.vpfinance.vpjr.module.user.personal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialogFragment;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.module.dialog.WithdrawDialogFragment;
import cn.vpfinance.vpjr.greendao.BonusProfit;
import cn.vpfinance.vpjr.greendao.TrialBonus;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

import static android.view.View.GONE;

/**
 */
public class TrialCoinActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvYearRate;
    private HttpService mHttpService;
    private BonusRespondModel mBonusModel;
    private TextView tvNormalProfit;
    private TextView tvChequeMoney;  //赠送体验金
    private TextView tvChequeProfit; //预计收益
    private int status = 1;
    private TextView tvChequeAction;
    private User user;
    private ImageView ivTrialCoin;
    private TextView tvDeadInfo;
    private TextView tvTotalOrIncomeTitle;
    private TextView tvInvestDeadline;
    private BonusRespondModel bonusModel;

    private int accountType = Constant.AccountBank;

    private void initFind() {
//        chartView = ((ChartView) findViewById(R.id.chartView));
        findViewById(R.id.clickAbout).setOnClickListener(this);
        tvYearRate = (TextView) findViewById(R.id.mrate);
        tvNormalProfit = (TextView) findViewById(R.id.normalProfit);
        tvChequeMoney = (TextView) findViewById(R.id.chequeMoney);
        tvChequeProfit = (TextView) findViewById(R.id.chequeProfit);
        tvChequeAction = (TextView) findViewById(R.id.chequeAction);
        ivTrialCoin = ((ImageView) findViewById(R.id.ivTrialCoin));
        tvDeadInfo = ((TextView)findViewById(R.id.tvDeadInfo));
        tvTotalOrIncomeTitle = ((TextView)findViewById(R.id.tvTotalOrIncomeTitle));
        tvInvestDeadline = ((TextView)findViewById(R.id.tvInvestDeadline));
    }

    public static void goThis(Context context, int accountType){
        Intent intent = new Intent(context,TrialCoinActivity.class);
        intent.putExtra(Constant.AccountType, accountType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            accountType = intent.getIntExtra(Constant.AccountType,Constant.AccountBank);
        }
        setContentView(R.layout.activity_trialcoin2);
        mHttpService = new HttpService(this, this);
        initFind();
        initView();
        mBonusModel = new BonusRespondModel();
        mHttpService.liteMoneyInfo(accountType);
    }

    protected void initView() {
        ActionBarLayout titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setTitle("体验金").setHeadBackVisible(View.VISIBLE);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        // 体验金信息
        if (reqId == ServiceCmd.CmdId.CMD_liteMoneyInfo.ordinal()) {
//            BonusRespondModel model = new BonusRespondModel();
            String msg = mHttpService.onLiteMoneyInfo2(json, mBonusModel);
            if (msg.contains("成功")) {
                bonusModel = mBonusModel;
                onModel(bonusModel);
            }else{
                Utils.Toast(this, "加载失败");
            }
        }
        // 激活体验金
        if (reqId == ServiceCmd.CmdId.CMD_activeLiteAccount.ordinal()) {
            String msg = mHttpService.onActiveLiteAccount(json);
            if (msg.contains("成功")) {
                mHttpService.liteMoneyInfo(accountType);
            } else {
                Utils.Toast(this, "体验金领取失败");
            }
        }

        // 领取收益
        if (reqId == ServiceCmd.CmdId.CMD_drawEarnings.ordinal()) {
            String msg = mHttpService.onDrawEarnings(json);
            if (msg.contains("成功")) {
                mHttpService.liteMoneyInfo(accountType);
            } else {
                Utils.Toast(this, msg);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.clickAbout:
                gotoWeb("/AppContent/litemoney", "体验金");
                break;
        }
    }
    private void onModel(BonusRespondModel model) {

        tvYearRate.setText(String.format("%.1f", model.bonusObj.trialFee * 12)+"%");
//        tvPeriod.setText(String.format("期限%d天", model.bonusObj.trialPeriod));
        double atomProfit = 10000 * model.bonusObj.trialFee * 0.01 * 12 * model.bonusObj.trialPeriod / 360;
        tvNormalProfit.setText(String.format("%.2f", atomProfit)+"元");//每万元收益

        tvChequeMoney.setText(String.format("%.2f", model.bonusObj.trialAmount));//新手赠送体验金
//        double realProfit = atomProfit * model.bonusObj.trialAmount / 10000;
//        tvChequeProfit.setText(String.format("￥%.2f", Math.floor(realProfit)));//预计收益

        if (model.msg == 0 && AppState.instance().logined()) {
            // 用户没有体验金
            tvChequeMoney.setText(String.format("%.2f", 0f));
            tvChequeProfit.setText(String.format("￥%.2f", 0f));
            tvChequeProfit.setVisibility(View.GONE);
            ivTrialCoin.setBackgroundResource(R.drawable.bg_trial_orin_none);
        }else{
            ivTrialCoin.setBackgroundResource(R.drawable.bg_trial_coin_change);
        }

        if (model.bonusProfit != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            tvDeadInfo.setText("有效期 " + format.format(new Date(model.bonusProfit.principalExpiryTime*1000)));
            tvChequeMoney.setText(String.format("%.2f", model.bonusProfit.principal));
            tvChequeProfit.setText(String.format("￥%.2f", Utils.doubleFloor(model.bonusProfit.totalEarning)));
            status = model.bonusProfit.status;
        }

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        if (model != null){
            if (model.bonusProfit != null){
//                Date date = new Date(model.bonusProfit.earningExpiryTime);
                DateTime dateTime = new DateTime(model.bonusProfit.earningExpiryTime*1000);
                String dateTimeStr = dateTime.toString("yyyy-MM-dd");
                tvInvestDeadline.setText("过期时间:" + dateTimeStr);
            }
        }
        String statusStr = "收益己失效";
        boolean enable = true;
        switch (status)
        {
            case 0:
                statusStr = "立即体验";//未激活
                tvDeadInfo.setVisibility(View.VISIBLE);
                if(model != null){
                    if (model.bonusProfit!= null){
                        tvDeadInfo.setText("有效期 " + simpleDateFormat1.format(new Date(model.bonusProfit.principalExpiryTime*1000)));
                        tvChequeMoney.setText(String.format("%.2f", model.bonusProfit.principal));
                    }
                }
                tvTotalOrIncomeTitle.setText("总金额");
                tvInvestDeadline.setVisibility(View.GONE);
                break;
            case 1:
                statusStr = "己过期";
                tvDeadInfo.setVisibility(View.GONE);
                tvTotalOrIncomeTitle.setText("总金额");
                if(model != null){
                    if (model.bonusProfit!= null){
                        tvChequeMoney.setText(String.format("%.2f", model.bonusProfit.principal));
                    }
                }
                tvInvestDeadline.setVisibility(View.GONE);
                enable = false;
                break;
            case 2:
                statusStr = "产生收益中";
                enable = false;
                tvTotalOrIncomeTitle.setText("累计收益");
                if(model != null){
                    if (model.bonusProfit!= null){
                        tvChequeMoney.setText(String.format("￥%.2f", Utils.doubleFloor(model.bonusProfit.totalEarning)));
                        tvDeadInfo.setText("出借金额" + String.format("%.2f", model.bonusProfit.principal) + "元");
                    }
                }
                tvDeadInfo.setVisibility(View.VISIBLE);
                tvInvestDeadline.setVisibility(View.VISIBLE);
                break;
            case 3:
                statusStr = "收益可领取";
                tvTotalOrIncomeTitle.setText("累计收益");
                tvDeadInfo.setVisibility(View.VISIBLE);
                if(model != null){
                    if (model.bonusProfit!= null){
                        tvChequeMoney.setText(String.format("￥%.2f", Utils.doubleFloor(model.bonusProfit.totalEarning)));
                        tvDeadInfo.setText("出借金额" + String.format("%.2f", model.bonusProfit.principal) + "元");
                    }
                }

                tvInvestDeadline.setVisibility(View.VISIBLE);
                break;
            case 4:
                statusStr = "收益已领取";
                tvTotalOrIncomeTitle.setText("累计收益");
                if(model != null){
                    if (model.bonusProfit!= null){
                        tvChequeMoney.setText(String.format("￥%.2f", Utils.doubleFloor(model.bonusProfit.totalEarning)));
                    }
                }

                tvDeadInfo.setVisibility(View.GONE);
                tvInvestDeadline.setVisibility(View.GONE);
                enable = false;
                break;
            case 5:
                statusStr = "收益已失效";
                tvTotalOrIncomeTitle.setText("累计收益");
                if(model != null){
                    if (model.bonusProfit!= null){
                        tvChequeMoney.setText(String.format("￥%.2f", Utils.doubleFloor(model.bonusProfit.totalEarning)));
                    }
                }

                tvDeadInfo.setVisibility(View.GONE);
                tvInvestDeadline.setVisibility(View.VISIBLE);
                enable = false;
                break;
            default:
                break;
        }

        tvChequeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppState.instance().logined()) {
                    startActivity(new Intent(TrialCoinActivity.this, LoginActivity.class));
                } else {
                    if(status==0)
                    {
                        mHttpService.activeLiteAccount();
                    }
                    else if(status==3)
                    {
                        checkTradePwd();
                    }
                }
            }
        });

        tvChequeAction.setVisibility(View.VISIBLE);
        tvChequeAction.setText(statusStr);
        tvChequeAction.setEnabled(enable);
    }

    private void checkTradePwd() {
        boolean bindBank = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_IS_BIND_BANK,false);
//        boolean openBank = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT,false);
        user = DBUtils.getUser(TrialCoinActivity.this);
        if (bindBank){
            mHttpService.drawEarnings();
        }else{
//            new AlertDialog.Builder(this)
//                    .setMessage("请先绑卡激活E账户再领取体验金收益")
//                    .setNegativeButton("去激活", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            BindBankHintActivity.goThis(TrialCoinActivity.this,user.getUserId().toString());
//                        }
//                    })
//                    .setNegativeButton("取消",null)
//                    .create()
//                    .show();
            new CommonTipsDialogFragment.Buidler()
                    .setTitleVisibility(GONE)
                    .setContent("请先绑卡激活E账户再领取体验金收益")
                    .setBtnRight("去激活")
                    .setOnRightClickListener(new CommonTipsDialogFragment.OnRightClickListner() {
                        @Override
                        public void rightClick() {
                            BindBankHintActivity.goThis(TrialCoinActivity.this,user.getUserId().toString());
                        }
                    })
                    .setBtnLeft("取消")
                    .createAndShow(this);
        }
        /*if (user != null){
            String mRealname = user.getRealName();
            String idCard = user.getIdentityCard();

            //只需要判断交易密码
            if (!user.getHasTradePassword()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TrialCoinActivity.this);
                builder.setMessage("您未设置交易密码");
                builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TrialCoinActivity.this, PasswordChangeActivity.class);
                        intent.putExtra(PasswordChangeActivity.EXTRA_KEY_INDEX, 1);
                        TrialCoinActivity.this.startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
                return;
            }
        }
        WithdrawDialogFragment dialog = WithdrawDialogFragment.newInstance("","确定领取");
        dialog.setOnTextConfrimListener(new WithdrawDialogFragment.onTextConfrimListener() {
            @Override
            public boolean onTextConfrim(String value,String value2) {
                String uid = AppState.instance().getSessionCode();
                //需要验证码
                Md5Algorithm md5 = Md5Algorithm.getInstance();
                String pwd = md5.md5Digest(value.getBytes());
                mHttpService.drawEarnings(value2,pwd);
                return false;
            }
        });
        dialog.show(getSupportFragmentManager(), "WithdrawDialog");
        */
    }

    public class BonusRespondModel {
        public TrialBonus bonusObj;
        public BonusProfit bonusProfit;
        public int msg;     // 用户是否有体验金
    }
}
