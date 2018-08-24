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
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.view.CodeVerifyView;
import de.greenrobot.event.EventBus;

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
        EventBus.getDefault().register(this);
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
//                finish();
                break;
        }
    }

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_CHANGE_PHONE_SUCCESS)) {//修改手机号成功
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
