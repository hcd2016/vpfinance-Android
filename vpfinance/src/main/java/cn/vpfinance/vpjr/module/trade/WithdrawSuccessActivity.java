package cn.vpfinance.vpjr.module.trade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 */
public class WithdrawSuccessActivity extends BaseActivity {

    private TextView bankName;
    private TextView lastBandCode;
    private TextView money;
    private TextView realMoney;
    private ActionBarLayout titleBar;
    public final static String BANKNAME = "bankName";
    public final static String BANKCARDNUM = "bankCardNum";
    public final static String WITHDRAWMONEY = "withdrawMoney";
    public final static String FACT_MONEY = "factMoney";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_success);

        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setTitle("提现成功").setHeadBackVisible(TextView.VISIBLE);

        bankName = (TextView) findViewById(R.id.bankName);
        lastBandCode = (TextView) findViewById(R.id.lastBandCode);
        money = (TextView) findViewById(R.id.money);
        realMoney = (TextView) findViewById(R.id.realMoney);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent !=null){
            bankName.setText(intent.getStringExtra(BANKNAME) == null ? "" : intent.getStringExtra(BANKNAME));

            String bankCardNum = intent.getStringExtra(BANKCARDNUM);
            lastBandCode.setText(bankCardNum == null ? "" : bankCardNum.substring(bankCardNum.length() - 4));

            money.setText(intent.getStringExtra(WITHDRAWMONEY) == null ? "" : (intent.getStringExtra(WITHDRAWMONEY)+"元"));
            realMoney.setText(intent.getStringExtra(FACT_MONEY) == null ? "" : (intent.getStringExtra(FACT_MONEY)+"元"));
        }

    }
}
