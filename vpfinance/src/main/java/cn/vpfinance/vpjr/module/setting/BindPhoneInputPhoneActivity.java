package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.view.CodeVerifyView;

public class BindPhoneInputPhoneActivity extends BaseActivity{

    @Bind(R.id.mActionBar)
    ActionBarWhiteLayout mActionBar;
    @Bind(R.id.etPhone)
    EditText etPhone;


    public static void goThis(Context context){
        Intent intent = new Intent(context, BindPhoneInputPhoneActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_input_phone);

        ButterKnife.bind(this);

        mActionBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("改绑手机号");
    }

    @OnClick({R.id.vClickGetCode})
    public void click(View view){
        switch (view.getId()){
            case R.id.vClickGetCode:
                String phone = etPhone.getText().toString();
                if (phone == null || phone.length() != 11){
                    Utils.Toast("手机号格式不正确");
                    return;
                }
                BindPhoneByAbleCodeActivity.goThis(this,BindPhoneByAbleCodeActivity.VERIFY_NEW_PHONE,phone);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
