package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.view.CodeVerifyView;

public class BindPhoneSuccessActivity extends BaseActivity{

    @Bind(R.id.mActionBar)
    ActionBarWhiteLayout mActionBar;


    public static void goThis(Context context){
        Intent intent = new Intent(context, BindPhoneSuccessActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_success);

        ButterKnife.bind(this);

        mActionBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("验证手机号");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
