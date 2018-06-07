package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarLayout;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

public class AutoInvestOverInfoActivity extends BaseActivity{

    public static void goThis(Context context) {
        Intent intent = new Intent(context, AutoInvestOverInfoActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_over_info);
        ((ActionBarLayout) findViewById(R.id.mActionBar)).reset().setTitle("重新授权").setHeadBackVisible(View.VISIBLE);

        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoInvestOverSmsActivity.goThis(AutoInvestOverInfoActivity.this);
                finish();
            }
        });
    }
}
