package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.util.DBUtils;

public class BindPhoneActivity extends BaseActivity{

    @Bind(R.id.mActionBar)
    ActionBarWhiteLayout mActionBar;

    public static void goThis(Context context){
        Intent intent = new Intent(context, BindPhoneActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ButterKnife.bind(this);

        mActionBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("验证手机号");
    }
    @OnClick({R.id.vClickAbleCode,R.id.vClickUnableCode})
    public void click(View view){
        switch (view.getId()){
            case R.id.vClickAbleCode:
                BindPhoneByAbleCodeActivity.goThis(this,BindPhoneByAbleCodeActivity.VERIFY_OLD_PHONE, DBUtils.getPhone(BindPhoneActivity.this));
                break;
            case R.id.vClickUnableCode:
                BindPhoneByUnableCodeActivity.goThis(this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
