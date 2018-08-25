package cn.vpfinance.vpjr.module.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.AutoInvestSettingBean;
import cn.vpfinance.vpjr.gson.QueryAutoStatusBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.Logger;

/**
 * 自动投标设置
 * Created by zzlz13 on 2017/7/31.
 */

public class AutoInvestSettingActivity extends BaseActivity {

    @Bind(R.id.title_bar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.allow_pub)
    SwitchCompat allowPub;
    @Bind(R.id.etReserveMoney)
    EditText etReserveMoney;
    @Bind(R.id.etMaxInvestMoney)
    EditText etMaxInvestMoney;
    @Bind(R.id.tvInvestType)
    TextView tvInvestType;
    @Bind(R.id.tvRefundType)
    TextView tvRefundType;
    @Bind(R.id.tvBorrowTime)
    TextView tvBorrowTime;
    @Bind(R.id.tvRate)
    TextView tvRate;
    @Bind(R.id.tvRiskLevel)
    TextView tvRiskLevel;
    @Bind(R.id.tvVoucher)
    TextView tvVoucher;
    @Bind(R.id.tvAuthorization)
    TextView tvAuthorization;
    @Bind(R.id.btnSubmit)
    Button btnSubmit;
    @Bind(R.id.containerSetting)
    LinearLayout containerSetting;

    private int accountType;
    private int autoInvestStatus;
    private HttpService mHttpService;
    public static final String ACCOUNT_BALANCE = "account_balance";
    public static final String IS_OPEN_AUTO_INVEST = "is_open_auto_invest";
    public static final String IS_OPEN_BANK_ACCOUNT = "is_open_bank_account";
    private Long userId;

    public static final String ARGS_INVEST_TYPE = "args_invest_type";
    public static final String ARGS_INVEST_TYPE_VALUE = "args_invest_type_value";
    public static final int REQUEST_CODE_INVEST_TYPE = 1;

    public static final String ARGS_REFUND_TYPE = "args_refund_type";
    public static final String ARGS_REFUND_TYPE_VALUE = "args_refund_type_value";
    public static final int REQUEST_CODE_REFUND_TYPE = 2;

    public static final String ARGS_BORROW_TIME_BEGIN_VALUE = "args_borrow_time_begin_value";
    public static final String ARGS_BORROW_TIME_END_VALUE = "args_borrow_time_end_value";
    public static final int REQUEST_CODE_BORROW_TIME = 3;

    public static final String ARGS_RATE_BEGIN_VALUE = "args_rate_begin_value";
    public static final String ARGS_RATE_END_VALUE = "args_rate_end_value";
    public static final int REQUEST_CODE_RATE = 4;

    public static final String ARGS_RISK_LEVEL = "args_risk_level";
    public static final String ARGS_RISK_LEVEL_VALUE = "args_risk_level_value";
    public static final int REQUEST_CODE_RISK_LEVEL = 5;

    public static final String ARGS_VOUCHER = "args_voucher";
    public static final String ARGS_VOUCHER_VALUE = "args_voucher_value";
    public static final int REQUEST_CODE_VOUCHER = 6;
    private AutoInvestSettingBean bean;

    /*
    自动投标默认设置项
    1.账户保留金额 0元
    2.最大投资金额  10万
    3.投标种类  不限
    4.还款方式 不限
    5.借款期限 1-12个月
    6.预期年化收益1-12%
    7.风险等级  不限
    8.优惠券 不显示
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_setting);
        mHttpService = new HttpService(this, this);
        ButterKnife.bind(this);

        User user = DBUtils.getUser(this);
        if (user != null && user.getUserId() != 0) {
            userId = user.getUserId();
        } else {
            Utils.Toast(FinanceApplication.getContext(), "登录失败,请重新登录");
            gotoActivity(LoginActivity.class);
            finish();
            return;
        }

        mTitleBar.reset().setTitle("自动投标").setHeadBackVisible(View.VISIBLE).setActionRight("说明", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWeb("/h5/help/autoDesc", "自动投标说明");
            }
        });

        allowPub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!"已授权".equals(tvAuthorization.getText().toString())) {
//                    Utils.Toast("请先进行授权");
//                } else {
//                    containerSetting.setVisibility(isChecked ? View.VISIBLE : View.GONE);
//                }
                if (null != bean && bean.isHXAutoPlank == 1) {//已授权
                    tvAuthorization.setText("已授权");
                    if (isChecked) {
                        containerSetting.setVisibility(View.VISIBLE);
                    } else {
                        containerSetting.setVisibility(View.GONE);
                    }
                } else {//未授权
                    Utils.Toast("请先进行授权");
                    tvAuthorization.setText("去授权");
                    allowPub.setChecked(false);
                    containerSetting.setVisibility(View.GONE);
                }


                btnSubmit.setEnabled(true);
//                //处理保存按钮置灰
//                if (null != bean) {
//                    int isAutoPlank = 0;
//                    if (isChecked) {
//                        isAutoPlank = 1;
//                    } else {
//                        isAutoPlank = 0;
//                    }
//                    if (bean.isAutoPlank != isAutoPlank) {
//
//                    } else {
//                        btnSubmit.setEnabled(false);
//                    }
//                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {

            accountType = intent.getIntExtra(Constant.AccountType, Constant.AccountLianLain);
//
            String balance = intent.getStringExtra(ACCOUNT_BALANCE);
            ((TextView) ButterKnife.findById(this, R.id.tvBalance)).setText("￥" + balance);
            boolean isOpen = "1".equals(intent.getStringExtra(IS_OPEN_AUTO_INVEST));
            allowPub.setChecked(isOpen);

            boolean isOpenBankAccount = "1".equals(intent.getStringExtra(IS_OPEN_BANK_ACCOUNT));
//            ButterKnife.findById(this, R.id.click_authorization).setVisibility(isOpenBankAccount ? View.VISIBLE : View.GONE);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setEnabled(false);
                requestAutoSetting(allowPub.isChecked());
            }
        });

        mHttpService.getAutoInvestSettingGet();
        etReserveMoney.addTextChangedListener(new TextWatcher() {//账户保留金额监听
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && bean.userRemainingMoney != Double.parseDouble(s.toString())) {//账户保留金额发生了变化
                    btnSubmit.setEnabled(true);
                }
//                else {
//                    btnSubmit.setEnabled(false);
//                }
            }
        });

        etMaxInvestMoney.addTextChangedListener(new TextWatcher() {//最大投资金额监听
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && bean.userMaxLoanMoney != Double.parseDouble(s.toString())) {//最大投资金额发生了变化
                    btnSubmit.setEnabled(true);
                }
//                else {
//                    btnSubmit.setEnabled(false);
//                }
            }
        });
    }

    private void requestAutoSetting(boolean isOpen) {
        if (bean == null) return;
        String reserveMoney = etReserveMoney.getText().toString();
        String maxInvestMoney = etMaxInvestMoney.getText().toString();
        double v = Double.parseDouble(maxInvestMoney);
        if (isOpen) {
            if (TextUtils.isEmpty(reserveMoney)) {
                btnSubmit.setEnabled(true);
                Utils.Toast("账户保留金额不能为空");
                return;
            }
            if (TextUtils.isEmpty(maxInvestMoney)) {
                btnSubmit.setEnabled(true);
                Utils.Toast("最大投资金额不能为空");
                return;
            }
            if (v < 100) {
                btnSubmit.setEnabled(true);
                Utils.Toast("最大投资金额不能小于一百");
                return;
            }
        }
        int loanPeriodBegin = bean.loanPeriodBegin;//最小借款期限
        int loanPeriodEnd = bean.loanPeriodEnd;//最大借款期限

        double rateBegin = bean.rateBegin;// 最低利率，小数
        double rateEnd = bean.rateEnd;// 最高利率

        //1、车贷宝，2、消费宝，8、供应链，10、企业贷，11、珠宝贷，12、融租宝,13个人贷,14智存投资
        String investTypeStr = FormatUtils.checkDot(bean.loanType);
        String refundTypeStr = FormatUtils.checkDot(bean.refundWay);
        String riskLevelStr = bean.securityLevel;
        String voucherStr = FormatUtils.checkDot(bean.coupons);

        mHttpService.getAutoInvestSetting(isOpen, reserveMoney, maxInvestMoney, "" + loanPeriodBegin, "" + loanPeriodEnd, "" + rateBegin, "" + rateEnd, investTypeStr, refundTypeStr, riskLevelStr, voucherStr);
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (isFinishing()) return;
        if (ServiceCmd.CmdId.CMD_Auto_Plan_Setting.ordinal() == reqId) {
            btnSubmit.setEnabled(true);
            Utils.Toast("网络异常，请稍后重试");
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (ServiceCmd.CmdId.CMD_Auto_Plan_Setting.ordinal() == reqId) {
            String msg = json.optString("msg");
            if ("1".equals(msg)) {
                Utils.Toast("保存成功");
                finish();
            } else {
//                String errorCode = json.optString("errorCode");
                btnSubmit.setEnabled(true);
                Utils.Toast("网络异常，请稍后重试");
            }
        } else if (ServiceCmd.CmdId.CMD_Auto_Plan_Setting_Get.ordinal() == reqId) {
            bean = mHttpService.getOnAutoInvestSettingGet(json);

            mHttpService.getQueryAutoPlankStatus(userId.toString());
            setData();


        } else if (reqId == ServiceCmd.CmdId.CMD_QUERY_AUTO_PLANK_STATUS.ordinal()) {
            QueryAutoStatusBean autoStatusBean = new Gson().fromJson(json.toString(), QueryAutoStatusBean.class);
            if (autoStatusBean != null && !TextUtils.isEmpty(autoStatusBean.autoPlankStatus)
                    && ("2".equals(autoStatusBean.autoPlankStatus) || "3".equals(autoStatusBean.autoPlankStatus))) {
                autoInvestStatus = Integer.parseInt(autoStatusBean.autoPlankStatus);
                if (autoInvestStatus == 2) {//2超额 3过期
                    tvAuthorization.setText("已超额");
                } else if (autoInvestStatus == 3) {
                    tvAuthorization.setText("已过期");
                }
            }
        }
    }

    public void setData() {
        if (bean.isHXAutoPlank == 1) {//已授权
            tvAuthorization.setText("已授权");
//                allowPub.setChecked(true);
        } else {//未授权
            tvAuthorization.setText("去授权");
            allowPub.setChecked(false);
        }
        if (allowPub.isChecked()) {
            containerSetting.setVisibility(View.VISIBLE);
        } else {
            containerSetting.setVisibility(View.GONE);
        }

        etReserveMoney.setText("" + bean.userRemainingMoney);
        etMaxInvestMoney.setText("" + bean.userMaxLoanMoney);
        tvBorrowTime.setText((bean.loanPeriodBegin == 1 && bean.loanPeriodEnd == 24) ? getText(R.string.unlimited) : (bean.loanPeriodBegin + "-" + bean.loanPeriodEnd + "个月"));
        tvRate.setText((bean.rateBegin == 1 && bean.rateEnd == 12) ? getText(R.string.unlimited) : (bean.rateBegin + "-" + bean.rateEnd + "%"));
//        tvVoucher.setText(TextUtils.isEmpty(bean.coupons) ? R.string.unselected : R.string.selected);
//        tvVoucher.setText(TextUtils.isEmpty(bean.coupons) ? "不使用" : "使用");
        tvVoucher.setText(bean.coupons.equals("1") ? "使用" : "不使用");
        tvInvestType.setText("0".equals(bean.loanType) ? R.string.unlimited : R.string.selected);
        tvRefundType.setText("0".equals(bean.refundWay) ? R.string.unlimited : R.string.selected);
        tvRiskLevel.setText("0".equals(bean.securityLevel) ? R.string.unlimited : R.string.selected);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mHttpService.getAutoInvestSettingGet();
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().post(new RefreshMineData());
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || bean == null) return;
        if (requestCode == REQUEST_CODE_INVEST_TYPE) {
            if (!bean.loanType.equals(data.getStringExtra(ARGS_INVEST_TYPE_VALUE))) {//状态已改变
                btnSubmit.setEnabled(true);
                bean.loanType = data.getStringExtra(ARGS_INVEST_TYPE_VALUE);
            }
            Logger.e("bean.loanType:" + bean.loanType);
            if ("0".equals(bean.loanType)) {
                tvInvestType.setText(R.string.unlimited);
            } else {
                tvInvestType.setText(R.string.selected);
            }
        } else if (requestCode == REQUEST_CODE_REFUND_TYPE) {
            if (!bean.refundWay.equals(data.getStringExtra(ARGS_REFUND_TYPE_VALUE))) {//状态已改变
                btnSubmit.setEnabled(true);
                bean.refundWay = data.getStringExtra(ARGS_REFUND_TYPE_VALUE);
            }
            Logger.e("bean.refundWay:" + bean.refundWay);
            if ("0".equals(bean.refundWay)) {
                tvRefundType.setText(R.string.unlimited);
            } else {
                tvRefundType.setText(R.string.selected);
            }
        } else if (requestCode == REQUEST_CODE_RISK_LEVEL) {
            if (!bean.securityLevel.equals(data.getStringExtra(ARGS_RISK_LEVEL_VALUE))) {//状态已改变
                btnSubmit.setEnabled(true);
                bean.securityLevel = data.getStringExtra(ARGS_RISK_LEVEL_VALUE);
            }
            Logger.e("bean.securityLevel:" + bean.securityLevel);
            if ("0".equals(bean.securityLevel)) {
                tvRiskLevel.setText(R.string.unlimited);
            } else {
                tvRiskLevel.setText(R.string.selected);
            }
        } else if (requestCode == REQUEST_CODE_BORROW_TIME) {
            if (bean.loanPeriodBegin != data.getIntExtra(ARGS_BORROW_TIME_BEGIN_VALUE, 0) || bean.loanPeriodEnd != data.getIntExtra(ARGS_BORROW_TIME_END_VALUE, 0)) {//状态已改变
                btnSubmit.setEnabled(true);
                bean.loanPeriodBegin = data.getIntExtra(ARGS_BORROW_TIME_BEGIN_VALUE, 0);
                bean.loanPeriodEnd = data.getIntExtra(ARGS_BORROW_TIME_END_VALUE, 0);
            }
            Logger.e("bean.loanPeriodBegin:" + bean.loanPeriodBegin + "---" + "bean.loanPeriodEnd:" + bean.loanPeriodEnd);
            if (bean.loanPeriodBegin == 1 && bean.loanPeriodEnd == 24) {
                tvBorrowTime.setText(R.string.unlimited);
            } else {
                tvBorrowTime.setText(bean.loanPeriodBegin + "-" + bean.loanPeriodEnd + "个月");
            }
        } else if (requestCode == REQUEST_CODE_RATE) {
            if (bean.rateBegin != data.getIntExtra(ARGS_RATE_BEGIN_VALUE, 0) || bean.rateEnd != data.getIntExtra(ARGS_RATE_END_VALUE, 0)) {//状态已改变
                btnSubmit.setEnabled(true);
                bean.rateBegin = data.getIntExtra(ARGS_RATE_BEGIN_VALUE, 0);
                bean.rateEnd = data.getIntExtra(ARGS_RATE_END_VALUE, 0);
            }
            Logger.e("bean.rateBegin:" + bean.rateBegin + "---" + "bean.rateEnd:" + bean.rateEnd);
            if (bean.rateBegin == 1 && bean.rateEnd == 12) {
                tvRate.setText(R.string.unlimited);
            } else {
                tvRate.setText(bean.rateBegin + "-" + bean.rateEnd + "%");
            }
        } else if (requestCode == REQUEST_CODE_VOUCHER) {
            btnSubmit.setEnabled(true);
            int isUse = data.getIntExtra("isUse", 2);
            if (isUse == 1) {
                tvVoucher.setText("使用");
            } else {
                tvVoucher.setText("不使用");
            }
            bean.coupons = isUse+"";
//            if(TextUtils.isEmpty(data.getStringExtra("isUse")) ){//选择了不使用
//                tvVoucher.setText("不使用");
//            }else {//使用
//                tvVoucher.setText("使用");
//            }
//
//            if (!bean.coupons.equals(data.getStringExtra("isUse"))) {//状态已改变
//
//                bean.coupons = data.getStringExtra(ARGS_VOUCHER_VALUE);
//            }
//            Logger.e("bean.coupons:" + bean.coupons);
//            if (TextUtils.isEmpty(bean.coupons)) {
//                tvVoucher.setText("不使用");
//            } else {
//                tvVoucher.setText("使用");
//            }
        }

        setData();

    }

    @OnClick({R.id.go_setting_invest_type, R.id.go_setting_refund_way, R.id.go_setting_deadline, R.id.go_setting_rate, R.id.go_setting_risk_level, R.id.go_setting_coupon, R.id.click_authorization})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go_setting_invest_type:
                if (bean != null) {
                    Intent loanTypeIntent = new Intent(this, AutoInvestTypeActivity.class);
                    loanTypeIntent.putExtra(ARGS_INVEST_TYPE, bean.options.loanType);
                    loanTypeIntent.putExtra(ARGS_INVEST_TYPE_VALUE, bean.loanType);
                    startActivityForResult(loanTypeIntent, REQUEST_CODE_INVEST_TYPE);
                }
                break;
            case R.id.go_setting_refund_way:
                if (bean != null) {
                    Intent refundIntent = new Intent(this, AutoInvestRefundWayActivity.class);
                    refundIntent.putExtra(ARGS_REFUND_TYPE, bean.options.refundWay);
                    refundIntent.putExtra(ARGS_REFUND_TYPE_VALUE, bean.refundWay);
                    startActivityForResult(refundIntent, REQUEST_CODE_REFUND_TYPE);
                }
                break;
            case R.id.go_setting_deadline:
                if (bean != null) {
                    Intent borrowTimeIntent = new Intent(this, AutoInvestDeadlineActivity.class);
                    borrowTimeIntent.putExtra(ARGS_BORROW_TIME_BEGIN_VALUE, bean.loanPeriodBegin);
                    borrowTimeIntent.putExtra(ARGS_BORROW_TIME_END_VALUE, bean.loanPeriodEnd);
                    startActivityForResult(borrowTimeIntent, REQUEST_CODE_BORROW_TIME);
                }
                break;
            case R.id.go_setting_rate:
                if (bean != null) {
                    Intent rateIntent = new Intent(this, AutoInvestRateActivity.class);
                    rateIntent.putExtra(ARGS_RATE_BEGIN_VALUE, bean.rateBegin);
                    rateIntent.putExtra(ARGS_RATE_END_VALUE, bean.rateEnd);
                    startActivityForResult(rateIntent, REQUEST_CODE_RATE);
                }
                break;
            case R.id.go_setting_risk_level:
                if (bean != null) {
                    Intent riskLevelIntent = new Intent(this, AutoInvestRiskLevelActivity.class);
                    riskLevelIntent.putExtra(ARGS_RISK_LEVEL, bean.options.securityLevel);
                    riskLevelIntent.putExtra(ARGS_RISK_LEVEL_VALUE, bean.securityLevel);
                    startActivityForResult(riskLevelIntent, REQUEST_CODE_RISK_LEVEL);
                }
                break;
            case R.id.go_setting_coupon:
                if (bean != null) {
//                    Intent voucherIntent = new Intent(this, AutoInvestCouponActivity.class);
//                    voucherIntent.putExtra(ARGS_VOUCHER, bean.options.coupons);
//                    voucherIntent.putExtra(ARGS_VOUCHER_VALUE, bean.coupons);
//                    startActivityForResult(voucherIntent, REQUEST_CODE_VOUCHER);
                    Intent voucherIntent = new Intent(this, AutoInvestCouponActivityNew.class);
                    voucherIntent.putExtra("isUse", bean.coupons);
                    startActivityForResult(voucherIntent, REQUEST_CODE_VOUCHER);
                }
                break;
            case R.id.click_authorization:
                if (bean == null) return;
                if (autoInvestStatus == 2) {//2超额
                    AutoInvestOverInfoActivity.goThis(AutoInvestSettingActivity.this);
                } else if (autoInvestStatus == 3) {//3过期
                    gotoWeb("hx/loansign/authAutoBid?userId=" + userId, "自动授权");
                } else if (bean.isHXAutoPlank == 0) {//去授权
                    gotoWeb("hx/loansign/authAutoBid?userId=" + userId, "自动授权");
                } else if (bean.isHXAutoPlank == 1) {//已授权
                    new AlertDialog.Builder(this)
                            .setMessage("需要撤销自动投标授权?")
                            .setPositiveButton("取消", null)
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AutoInvestCancelInfoActivity.goThis(AutoInvestSettingActivity.this);
                                }
                            })
                            .show();
                }
                break;
        }
    }
}
