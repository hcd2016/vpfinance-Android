package cn.vpfinance.vpjr.module.user.transfer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.DifColorTextStringBuilder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;


/**
 */
public class TransferProductExecuteNowActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.tvRefundTitle)
    TextView tvRefundTitle;
    //    @Bind(R.id.etTransferMoney)
//    EditText etTransferMoney;
    @Bind(R.id.tvRefundMoney)
    TextView tvRefundMoney;
    //    @Bind(R.id.tvRefundDesc)
    //    TextView tvRefundDesc;
    //    @Bind(R.id.tvPercent)
    //    TextView tvPercent;
    //    @Bind(R.id.llTransferInfo)
    //    LinearLayout llTransferInfo;

    //    @Bind(R.id.predict_income)
//    TextView mPredictIncome;
    @Bind(R.id.transfer_cost)
    TextView mTransferCost;
    //    @Bind(R.id.cost_scale)
//    TextView mCostScale;
    @Bind(R.id.real_income_money)
    TextView mRealIncomeMoney;

    private static final String RECORD_ID = "recordId";
    private static final String BORROW_ID = "borrowId";
    private static final String TITLE = "title";
    private static final String TENDER_MONEY = "tenderMoney";
    private static final String HAVE_RETURN_MONEY = "haveReturnMoney";
    private static final String STAY_RETURN_MONEY = "stayReturnMoney";
    private static final String TRANSFERMINRATE = "transferMinRate";
    @Bind(R.id.tv_desc)
    TextView tvDesc;
    private String tenderMoneyStr;
    /**
     * 已回款
     */
    private String haveReturnMoneyStr;
    /**
     * 待回款
     */
    private String stayReturnMoneyStr;
    private Context mContext;
    private HttpService mHttpService;
    private String recordId;
    private String borrowId;
    private TextInputDialogFragment tidf;
    private double mTransferMinRate;
    private double promitRate;
    private int accountType = Constant.AccountLianLain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_product_now_new);
        ButterKnife.bind(this);
        //        llTransferInfo.setVisibility(View.INVISIBLE);
        mHttpService = new HttpService(this, this);
        mContext = this;
        titleBar.setTitle("确认转让")
                .setHeadBackVisible(View.VISIBLE);
//                .setImageButtonRight(R.drawable.ic_help_voucher, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        gotoWeb("/AppContent/problemdetails?type=26", "债权说明");
//                    }
//                });

        Intent intent = getIntent();
        if (intent != null) {
            recordId = intent.getStringExtra(RECORD_ID);
            borrowId = intent.getStringExtra(BORROW_ID);
            tvRefundTitle.setText(intent.getStringExtra(TITLE));
            tenderMoneyStr = intent.getStringExtra(TENDER_MONEY);
            tvRefundMoney.setText(tenderMoneyStr + "元");
            haveReturnMoneyStr = intent.getStringExtra(HAVE_RETURN_MONEY);
            stayReturnMoneyStr = intent.getStringExtra(STAY_RETURN_MONEY);
            mTransferMinRate = intent.getDoubleExtra(TRANSFERMINRATE, 0);
            accountType = intent.getIntExtra(Constant.AccountType, Constant.AccountLianLain);

//            mPredictIncome.setText(stayReturnMoneyStr + "元");
            String content = "您将全额转让该投资标的剩余本金，转让成功后您将不能继续获得标的剩余待回款利息" + stayReturnMoneyStr + "元。";
            DifColorTextStringBuilder difColorTextStringBuilder = new DifColorTextStringBuilder();
            difColorTextStringBuilder.setContent(content)
                    .setHighlightContent(stayReturnMoneyStr, R.color.red_text2)
                    .setTextView(tvDesc)
                    .create();
//            tvRefundDesc.setText("已赚取收益" + haveReturnMoneyStr + "元,预计待回款利息" + stayReturnMoneyStr + "元");
            mHttpService.getTransferCost(borrowId, tenderMoneyStr, tenderMoneyStr);
        }

//        etTransferMoney.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String text = s.toString();
//                if (!TextUtils.isEmpty(text) && (!".".equals(text))) {
//                    if (Double.parseDouble(text) != 0) {
//                        mHttpService.getTransferCost(borrowId, tenderMoneyStr, s.toString());
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
    }


    public static void gotoTransferProductExecuteNowActivity(Context context, String recordId, String borrowId, String title, String tenderMoney, String haveReturnMoney, String stayReturnMoney, double transferMinRate, int accountType) {
        Intent intent = new Intent(context, TransferProductExecuteNowActivity.class);
        intent.putExtra(RECORD_ID, recordId);
        intent.putExtra(BORROW_ID, borrowId);
        intent.putExtra(TITLE, title);
        intent.putExtra(TENDER_MONEY, tenderMoney);
        intent.putExtra(HAVE_RETURN_MONEY, haveReturnMoney);
        intent.putExtra(STAY_RETURN_MONEY, stayReturnMoney);
        intent.putExtra(TRANSFERMINRATE, transferMinRate);
        intent.putExtra(Constant.AccountType, accountType);
        context.startActivity(intent);
    }

    private void commit() {
        String moneyStr = tenderMoneyStr;
        try {
//            final double money = Double.parseDouble(moneyStr);
//            double stayReturnMoney = Double.parseDouble(stayReturnMoneyStr);
//            double tenderMoney = Double.parseDouble(tenderMoneyStr);
//            if (money > stayReturnMoney + tenderMoney) {
//                Utils.Toast(mContext, "转让价格不超过待回款本息");
//                return;
//            }
//
//            double min = tenderMoney * (1 - 0.3);
////            double max = tenderMoney * (1 + 0.3);
//            if (money < min) {
//                Utils.Toast(mContext, "折价转让的折价率不超过30%");
//                return;
//            }
//
//            if (mTransferMinRate > promitRate) {
//                new AlertDialog.Builder(TransferProductExecuteNowActivity.this)
//                        .setTitle("提示")
//                        .setMessage("转让净收益率不能小于平台标最低利率")
//                        .setNegativeButton("我知道了", null).show();
//                return;
//            }

            if (TextUtils.isEmpty(recordId) || TextUtils.isEmpty(borrowId)) return;

            User user = DBUtils.getUser(mContext);
            if (user == null) {
                Toast.makeText(mContext, "您还没有登录，请登录", Toast.LENGTH_SHORT).show();
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                return;
            }

//            if (accountType == Constant.AccountLianLain) {
//                tidf = TextInputDialogFragment.newInstance("交易密码", "请输入交易密码", true);
//                tidf.setOnTextConfrimListener(new TextInputDialogFragment.onTextConfrimListener() {
//                    @Override
//                    public boolean onTextConfrim(String value) {
//                        if (value != null) {
//                            Md5Algorithm md5 = Md5Algorithm.getInstance();
//                            value = md5.md5Digest(value.getBytes());
//                            mHttpService.getTransferProductNow(recordId, borrowId, "" + money, value);
//                        }
//                        return false;
//                    }
//                });
//                tidf.show(getSupportFragmentManager(), "inputPwd");
//            } else
            mHttpService.assignmentNowTransferCommit(recordId, tenderMoneyStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Transfer_cost.ordinal()) {
            double fee = json.optDouble("fee");//期数
            //到期净收益率
            promitRate = json.optDouble("promitRate");
            String arrivalMoney = json.optString("arrivalMoney");//实际到账金额
            mTransferCost.setText(fee + "元");
            double v = promitRate * 100;
            String value = FormatUtils.formatDown(v);
//            mCostScale.setText("约定年利率:" + value + "%");
            mRealIncomeMoney.setText(arrivalMoney + "元");
        } else if (reqId == ServiceCmd.CmdId.CMD_Transfer_Assign_Now.ordinal()) {
            int msg = -1;
            try {
                msg = json.optInt("Msg");
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (msg) {
                case 0:
                    Utils.Toast(mContext, "成功");
                    finish();
                    break;
                case 1:
                    Utils.Toast(mContext, "已经申请过转让");
                    break;
                case 3:
                    Utils.Toast(mContext, "交易密码错误");
                    break;
                case 6:
                    Utils.Toast(mContext, "剩余日期在还款的5天内");
                    break;
                case 7:
                    Utils.Toast(mContext, "产生利息后，剩余利息小作0");
                    break;
                case 8:
                    Utils.Toast(mContext, "转让净收益率不能小于平台标最低利率");
                    break;
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_Bank_Transfer_Verify.ordinal()) {
            //转让规则匹配
            String resultCode = json.optString("code");
            String mess = json.optString("mess");
            switch (resultCode) {
                case "0":
//                    String moneyStr = etTransferMoney.getText().toString();
                    String moneyStr = tvRefundMoney.getText().toString();
                    String url = "hx/creditassignment/apply?investId=" + recordId + "&transferMoney=" + moneyStr;
                    gotoWeb(url, "转让债权");
                    break;
                case "1":
                    Utils.Toast(FinanceApplication.getContext(), mess);
                    break;
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_ASSIGNMENT_OF_DEBT_COMMIT.ordinal()) {
            String msg = json.optString("msg");
            switch (msg) {
                case "1"://转让成功
                    finish();
                    Utils.Toast("转让成功!");
                    break;
                case "2":
                    if (accountType == Constant.AccountBank) {
                        //转让规则匹配
                        String url = "hx/creditassignment/apply?investId=" + recordId + "&transferMoney=" + tenderMoneyStr;
                        gotoWeb(url, "转让债权");
                    }
                    break;
                case "3":
                    Utils.Toast("转让失败!");
                    break;
            }
        }
    }

    @OnClick({R.id.btnTransfer})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.ivHelp:
//                String des = getResources().getString(R.string.transfer_product_execute_now_info);
//                des = String.format(des, FormatUtils.formatDown(mTransferMinRate * 100) + "%", "30%");
//                new AlertDialog.Builder(mContext)
//                        .setMessage(des)
//                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .create()
//                        .show();
//                break;
            case R.id.btnTransfer:
                commit();
                break;
//            case R.id.clickTransferNativeMoney:
//                if (tvRefundMoney != null && (!TextUtils.isEmpty(tvRefundMoney.getText()))) {
////                    etTransferMoney.setText(tvRefundMoney.getText());
//                }
//                break;
        }
    }
}
