package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.jewelcredit.ui.widget.ActionBarWhiteLayout;

import butterknife.Bind;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

public class LoginPasswordActivity extends BaseActivity{

    public static final int PASSWORD_SETTING = 1; //设置登录密码
    public static final int PASSWORD_RESET = 2; //重置登录密码

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;

    public static void goThis(Context context, int type) {
        Intent intent = new Intent(context, LoginPasswordActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
    }
}
