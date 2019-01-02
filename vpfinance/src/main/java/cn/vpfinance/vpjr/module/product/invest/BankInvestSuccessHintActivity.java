package cn.vpfinance.vpjr.module.product.invest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.jewelcredit.ui.widget.ActionBarLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * Created by zzlz13 on 2017/12/11.
 */

public class BankInvestSuccessHintActivity extends BaseActivity{

    @Bind(R.id.mActionBar)
    ActionBarLayout mActionBar;
    @Bind(R.id.vCheckBox)
    CheckBox vCheckBox;
    @Bind(R.id.vButton)
    Button vButton;

    private static final String BANK_INVEST_SUCCESS_COUNT = "BankInvestSuccessCount";
    public static final String BANK_INVEST_SUCCESS_IS_SHOW = "BankInvestSuccessIsShow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_invest_success_hint);
        ButterKnife.bind(this);

        mActionBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("存管出借提示");

        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        int bankInvestSuccessCount = preferencesHelper.getIntValue(BANK_INVEST_SUCCESS_COUNT, 0);

        vCheckBox.setVisibility(bankInvestSuccessCount <3 ? View.GONE : View.VISIBLE);

        preferencesHelper.putIntValue(BANK_INVEST_SUCCESS_COUNT,bankInvestSuccessCount+1);

        vButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vCheckBox.isChecked()){
                    SharedPreferencesHelper.getInstance(BankInvestSuccessHintActivity.this).putBooleanValue(BANK_INVEST_SUCCESS_IS_SHOW,false);
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
