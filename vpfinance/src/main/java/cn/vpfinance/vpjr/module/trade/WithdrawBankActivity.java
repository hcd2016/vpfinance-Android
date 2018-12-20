package cn.vpfinance.vpjr.module.trade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.model.FundOverInfo;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * 银行存管 - 提现
 * Created by zzlz13 on 2017/7/31.
 */

public class WithdrawBankActivity extends BaseActivity {

    @Bind(R.id.title_bar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.et_money)
    EditText mMoney;
    @Bind(R.id.tv_account_money)
    TextView tvAccountMoney;
    @Bind(R.id.tv_freeze_money)
    TextView tvFreezeMoney;
//    private HttpService mHttpService;
    public static final String CASHBALANCE = "cashBalance";
    public static final String FROZENAMTN = "frozenAmtN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_bank);
        ButterKnife.bind(this);
        mTitleBar.reset().setTitle("提现").setHeadBackVisible(View.VISIBLE);
//        mHttpService = new HttpService(this, this);

        Intent intent = getIntent();
        String cashBalance = intent.getStringExtra(CASHBALANCE);
        tvAccountMoney.setText(TextUtils.isEmpty(cashBalance) ? "" : FormatUtils.formatDown(cashBalance));
        String frozenAmtN = intent.getStringExtra(FROZENAMTN);
        tvFreezeMoney.setText(TextUtils.isEmpty(frozenAmtN) ? "" : FormatUtils.formatDown(frozenAmtN));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        User user = DBUtils.getUser(this);
//        if (user != null) {
//            mHttpService.getFundOverInfo("" + user.getUserId(), 1);
//        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
//        if (reqId == ServiceCmd.CmdId.CMD_FundOverView.ordinal()) {
//            FundOverInfo fundOverInfo = mHttpService.onGetFundOverInfo(json);
//            /*账户余额*/
//            String cashBalance = fundOverInfo.getCashBalance();
//            tvAccountMoney.setText(TextUtils.isEmpty(cashBalance) ? "" : FormatUtils.formatDown(cashBalance));
//            /*冻结金额*/
//            String realMoney = fundOverInfo.getFrozenAmtN();
//            tvFreezeMoney.setText(TextUtils.isEmpty(realMoney) ? "" : FormatUtils.formatDown(realMoney));
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.click_withdraw,R.id.iv_hint,R.id.tvWithDrawInfo,R.id.tvRealnameInfo})
    public void onViewClicked(View view) {
        switch(view.getId()){
            case R.id.click_withdraw:
                User user = DBUtils.getUser(this);
                final Long userId = user.getUserId();
                boolean isBindBank = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_IS_BIND_BANK);
                if (!isBindBank){
                    new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.message_no_bind_bank))
                            .setPositiveButton("去绑定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BindBankHintActivity.goThis(WithdrawBankActivity.this,userId.toString());
                                }
                            })
                            .setNegativeButton("取消",null)
                            .show();
                    return;
                }

                String money = mMoney.getText().toString();
                if (user != null) {
                    if (TextUtils.isEmpty(money)){
                        Utils.Toast("请填写提现金额");
                        return;
                    }

                    gotoWeb("/hx/account/withDraw?userId=" + userId + "&amount=" + money, "");
                } else {
                    gotoActivity(LoginActivity.class);
                }
                break;
            case R.id.iv_hint:
                Utils.Toast(WithdrawBankActivity.this,"提现或出借中银行待审金额");
                break;
            case R.id.tvWithDrawInfo:
                gotoWeb("/h5/help/hxGuideWith","提现图文指引");
                break;
            case R.id.tvRealnameInfo:
                gotoWeb("/h5/help/hxGuideUse","实名认证图文指引");
                break;
        }
    }
}
