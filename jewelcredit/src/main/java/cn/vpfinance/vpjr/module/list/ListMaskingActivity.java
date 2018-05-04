package cn.vpfinance.vpjr.module.list;

import android.os.Bundle;
import android.view.View;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * Created by zzlz13 on 2017/11/21.
 */

public class ListMaskingActivity extends BaseActivity{

    public static final String IS_SHOW_MASKING = "isShowMasking";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_masking);
        SharedPreferencesHelper.getInstance(ListMaskingActivity.this).putBooleanValue(IS_SHOW_MASKING,true);
        findViewById(R.id.btnKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
