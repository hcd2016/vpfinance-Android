package cn.vpfinance.vpjr.module.product;

import android.os.Bundle;
import android.view.View;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 */
public class BorrowerCreditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_credit);
        findViewById(R.id.ibCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
