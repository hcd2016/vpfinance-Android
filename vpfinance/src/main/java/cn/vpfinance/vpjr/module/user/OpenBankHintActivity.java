package cn.vpfinance.vpjr.module.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.util.DBUtils;

public class OpenBankHintActivity extends BaseActivity {

    private RelativeLayout rootContainer;

    public static void goThis(Context context) {
        Intent intent = new Intent(context, OpenBankHintActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_bank_hint);

        rootContainer = ((RelativeLayout) findViewById(R.id.rootContainer));

//        new HttpService(this,this).getHxIsUpdate();

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenBankHintActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.SWITCH_TAB_NUM, 2);
                startActivity(intent);
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
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        rootContainer.setBackgroundResource(R.drawable.open_bank_hint_hx_update);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
