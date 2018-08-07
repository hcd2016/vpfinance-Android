package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.HttpService;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

public class AutoInvestProtocolActivity extends BaseActivity{


    private int accountType = 0;
    private String cashBalance = "";
    private String isAutoTender = "";
    private String isBindBank = "";
    private int status = 0;
    private HttpService mHttpService;

    public static void goThis(Context context,int accountType,String cashBalance,String isAutoTender,String isBindBank){
        Intent intent = new Intent(context,AutoInvestProtocolActivity.class);
        intent.putExtra(Constant.AccountType, accountType);
        intent.putExtra(AutoInvestSettingActivity.ACCOUNT_BALANCE, cashBalance);
        intent.putExtra(AutoInvestSettingActivity.IS_OPEN_AUTO_INVEST, isAutoTender);
        intent.putExtra(AutoInvestSettingActivity.IS_OPEN_BANK_ACCOUNT, isBindBank);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_protocol);

        mHttpService = new HttpService(this, this);

        ((ActionBarWhiteLayout) findViewById(R.id.titleBar)).reset().setTitle("自动投标服务协议").setHeadBackVisible(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null){
            accountType = intent.getIntExtra(Constant.AccountType,0);
            cashBalance = intent.getStringExtra(AutoInvestSettingActivity.ACCOUNT_BALANCE);
            isAutoTender = intent.getStringExtra(AutoInvestSettingActivity.IS_OPEN_AUTO_INVEST);
            isBindBank = intent.getStringExtra(AutoInvestSettingActivity.IS_OPEN_BANK_ACCOUNT);
        }

        findViewById(R.id.btnAgree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mHttpService.getAgreeAutoTenderProtocol();

                Intent autoInvestIntent = new Intent(AutoInvestProtocolActivity.this, AutoInvestSettingActivity.class);
                autoInvestIntent.putExtra(Constant.AccountType, accountType);
                autoInvestIntent.putExtra(AutoInvestSettingActivity.ACCOUNT_BALANCE, cashBalance);
                autoInvestIntent.putExtra(AutoInvestSettingActivity.IS_OPEN_AUTO_INVEST, isAutoTender);
                autoInvestIntent.putExtra(AutoInvestSettingActivity.IS_OPEN_BANK_ACCOUNT, isBindBank);
                gotoActivity(autoInvestIntent);
                finish();
            }
        });

        findViewById(R.id.btnNoAgree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
