package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.EditTextWithDel;

public class LoginPasswordActivity extends BaseActivity {

    public static final int PASSWORD_SETTING = 1; //设置登录密码
    public static final int PASSWORD_RESET = 2; //重置登录密码

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Bind(R.id.et_first_pwd)
    EditTextWithDel etFirstPwd;
    @Bind(R.id.tv_psd_level)
    TextView tvPsdLevel;
    @Bind(R.id.et_second_pwd)
    EditTextWithDel etSecondPwd;
    @Bind(R.id.btn_regiter)
    Button btnRegiter;
    private HttpService httpService;
    private UserRegisterBean userRegisterBean;

    public static void goThis(Context context, UserRegisterBean userRegisterBean) {
        Intent intent = new Intent(context, LoginPasswordActivity.class);
        intent.putExtra("userRegisterBean", userRegisterBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        userRegisterBean = (UserRegisterBean) getIntent().getSerializableExtra("userRegisterBean");
        etFirstPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = Common.checkPasswordStrength(editable.toString());//检测密码强度
                if (!TextUtils.isEmpty(s)) {
                    String replace = s.replace("密码强度:", "");
                    tvPsdLevel.setText(replace);
                }
            }
        });
        httpService = new HttpService(this, this);
    }

    @OnClick(R.id.btn_regiter)
    public void onViewClicked() {//完成注册
        String firstPassword = etFirstPwd.getText().toString();
        String secondPassword = etSecondPwd.getText().toString();
        if (TextUtils.isEmpty(firstPassword) || TextUtils.isEmpty(secondPassword)) {
            Utils.Toast("密码不能为空");
            return;
        }
        if (firstPassword.equals(secondPassword)) {
            Utils.Toast("两次密码输入不一致");
            return;
        }
        String passwordPassMsg = Common.isPasswordPass(firstPassword);
        if (!TextUtils.isEmpty(passwordPassMsg)) {
            Utils.Toast(passwordPassMsg);
            return;
        }
        String uPwd = etFirstPwd.getText().toString();
        if (null != userRegisterBean) {//完成注册请求
            httpService.userRegister(userRegisterBean.getReferrerNum(), userRegisterBean.getPhoneNum(),
                    Utils.md5encode(uPwd), userRegisterBean.getPhoneNum(), userRegisterBean.getCaptcha());
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (null != json) {
            String msg = json.optString("msg");
            switch (msg) {
                case "0":
                    Utils.Toast("验证码错误");
                    break;
                case "1":
                    Utils.Toast("注册成功");
                    break;
                case "2":

                    break;
                case "3":
                    break;
                case "4":
                    break;
                case "5":
                    break;
            }
        }
    }
}
