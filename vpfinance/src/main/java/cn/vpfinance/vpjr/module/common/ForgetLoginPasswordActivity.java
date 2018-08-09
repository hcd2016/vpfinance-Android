package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.HttpService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.view.EditTextWithDel;

public class ForgetLoginPasswordActivity extends BaseActivity{

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Bind(R.id.etUsername)
    EditTextWithDel etUsername;

    private boolean isPersonType;
    private HttpService mHttpService;

    public static void goThis(Context context, boolean isPersonType) {
        Intent intent = new Intent(context, ForgetLoginPasswordActivity.class);
        intent.putExtra("isPersonType", isPersonType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_login_password);
        ButterKnife.bind(this);

        mHttpService = new HttpService(this, this);
        mHttpService.getCaptchaImage();

        titleBar.reset().setTitle("忘记密码").setHeadBackVisible(View.VISIBLE);

        Intent intent = getIntent();
        if (null != intent){
            isPersonType = intent.getBooleanExtra("isPersonType", true);
        }

        if (isPersonType){
            etUsername.setHint("请输入用户名/手机号");
        }else {
            etUsername.setHint("请输入企业名称/邮箱");
        }
    }

    @OnClick({R.id.ivCaptcha, R.id.btnNext})
    public void click(View v){
        switch (v.getId()){
            case R.id.ivCaptcha:
                mHttpService.getCaptchaImage();
                break;
            case R.id.btnNext:
                int type = isPersonType ? CaptchaActivity.FORGET_LOGIN_PASSWORD_PERSON : CaptchaActivity.FORGET_LOGIN_PASSWORD_COMPANY;
//                CaptchaActivity.goThis(this,type,phone);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
