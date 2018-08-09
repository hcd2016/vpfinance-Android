package cn.vpfinance.vpjr.module.common;

import android.os.Bundle;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

public class WeiXinBindPhoneActivity extends BaseActivity{

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_bind_phone);
        ButterKnife.bind(this);

        titleBar.reset().setTitle("绑定手机号码").setHeadBackVisible(View.VISIBLE);
    }

    @OnClick({R.id.btnNext})
    public void click(View v){
        switch (v.getId()){
            case R.id.btnNext:
                CaptchaActivity.goThis(this,CaptchaActivity.WEI_XIN_BIND_PHONE,"17512039401");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
