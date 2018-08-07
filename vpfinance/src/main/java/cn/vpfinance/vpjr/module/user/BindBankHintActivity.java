package cn.vpfinance.vpjr.module.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarLayout;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 绑卡激活之前提示界面
 */
public class BindBankHintActivity extends BaseActivity {

    private String userId = "";
    public static void goThis(Context context,String userId){
        Intent intent = new Intent(context, BindBankHintActivity.class);
        intent.putExtra("userId",userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_hint);

        userId = getIntent().getStringExtra("userId");

        ((ActionBarLayout) findViewById(R.id.mActionBarLayout))
                .reset()
                .setHeadBackVisible(View.VISIBLE)
                .setTitle("温馨提示");
        findViewById(R.id.btnKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoWeb("hx/bankcard/bind?userId=" + userId, "");
                finish();
            }
        });
    }
}
