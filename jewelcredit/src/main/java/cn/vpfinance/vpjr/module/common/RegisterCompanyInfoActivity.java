package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

public class RegisterCompanyInfoActivity extends BaseActivity{

    public static void goThis(Context context) {
        Intent intent = new Intent(context, RegisterCompanyInfoActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company_info);
    }
}
