package cn.vpfinance.vpjr.module.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.jewelcredit.util.Utils;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.util.DBUtils;

public class OpenBankHintActivity extends BaseActivity {

    public static void goThis(Context context) {
        Intent intent = new Intent(context, OpenBankHintActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_bank_hint);
        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = DBUtils.getUser(OpenBankHintActivity.this);
                if (user != null) {
                    if(!TextUtils.isEmpty(user.getRealName())){
                        gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
                        finish();
                    }else{
                        Utils.Toast("开通银行存管前, 请先去实名认证");
                        RealnameAuthActivity.goThis(OpenBankHintActivity.this);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        ((FinanceApplication)getApplication()).isFirstRegieter = false;
        super.onDestroy();
    }
}
