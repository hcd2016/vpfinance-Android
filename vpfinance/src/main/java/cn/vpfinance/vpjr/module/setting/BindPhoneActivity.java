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
import cn.vpfinance.vpjr.util.EventStringModel;
import de.greenrobot.event.EventBus;

public class BindPhoneActivity extends BaseActivity{

    @Bind(R.id.mActionBar)
    ActionBarWhiteLayout mActionBar;

    public static void goThis(Context context,String phone){
        Intent intent = new Intent(context, BindPhoneActivity.class);
        intent.putExtra("phone",phone);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mActionBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("验证手机号");
    }
    @OnClick({R.id.vClickAbleCode,R.id.vClickUnableCode})
    public void click(View view){
        switch (view.getId()){
            case R.id.vClickAbleCode:
                BindPhoneByAbleCodeActivity.goThis(this,BindPhoneByAbleCodeActivity.VERIFY_OLD_PHONE, getIntent().getStringExtra("phone"));
                break;
            case R.id.vClickUnableCode:
                BindPhoneByUnableCodeActivity.goThis(this);
                break;
        }
    }

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_CHANGE_PHONE_SUCCESS)) {//修改密码成功
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
