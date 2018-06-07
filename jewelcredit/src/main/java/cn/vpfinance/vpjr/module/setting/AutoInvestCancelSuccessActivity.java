package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarLayout;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.util.DBUtils;

public class AutoInvestCancelSuccessActivity extends BaseActivity{

    public static void goThis(Context context) {
        Intent intent = new Intent(context, AutoInvestCancelSuccessActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_cancel_success);
        ((ActionBarLayout) findViewById(R.id.mActionBar)).reset().setTitle("完成").setHeadBackVisible(View.VISIBLE);

        findViewById(R.id.btnFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = DBUtils.getUser(AutoInvestCancelSuccessActivity.this);
                if (user != null){
                    gotoWeb("hx/loansign/authAutoBid?userId=" + user.getUserId().toString(), "自动授权");
                    finish();
                }
            }
        });
    }
}
