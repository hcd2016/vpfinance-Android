package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.Utils;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import de.greenrobot.event.EventBus;

/**
 * 企业注册信息
 */
public class RegisterCompanyInfoActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Bind(R.id.et_company_name)
    EditTextWithDel etCompanyName;
    @Bind(R.id.et_credit_num)
    EditTextWithDel etCreditNum;
    @Bind(R.id.et_legal_name)
    EditTextWithDel etLegalName;
    @Bind(R.id.et_company_address)
    EditTextWithDel etCompanyAddress;
    @Bind(R.id.btnNext)
    Button btnNext;

    public static void goThis(Context context, UserRegisterBean userRegisterBean) {
        Intent intent = new Intent(context, RegisterCompanyInfoActivity.class);
        intent.putExtra("userRegisterBean", userRegisterBean);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("完善企业信息").setHeadBackVisible(View.VISIBLE);
    }

    @OnClick(R.id.btnNext)
    public void onViewClicked() {
        if (TextUtils.isEmpty(etCompanyAddress.getText().toString())) {
            Utils.Toast("公司地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(etCompanyName.getText().toString())) {
            Utils.Toast("公司名称不能为空");
            return;
        }
        if (TextUtils.isEmpty(etCreditNum.getText().toString())) {
            Utils.Toast("社会信用代码不能为空");
            return;
        }
        if (TextUtils.isEmpty(etLegalName.getText().toString())) {
            Utils.Toast("法人代表名字不能为空");
            return;
        }
        if (etCompanyName.getText().toString().contains(" ")) {
            Utils.Toast("企业名称不允许输入空格");
            return;
        }
        if (etLegalName.getText().toString().contains(" ")) {
            Utils.Toast("法人姓名不允许输入空格");
            return;
        }
        if (etCreditNum.getText().toString().length() != 18) {
            Utils.Toast("请输入18位社会信用");
            return;
        }
        if(etCompanyName.getText().toString().length() < 5) {
            Utils.Toast("企业名称不能少于5位");
            return;
        }
        UserRegisterBean userRegisterBean = (UserRegisterBean) getIntent().getSerializableExtra("userRegisterBean");
        userRegisterBean.setCompanyName(etCompanyName.getText().toString());
        userRegisterBean.setCompanyAddress(etCompanyAddress.getText().toString());
        userRegisterBean.setCompanyCreditCode(etCreditNum.getText().toString());
        userRegisterBean.setCompanyLegal(etLegalName.getText().toString());
        LoginPasswordActivity.goThis(this, userRegisterBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_REGISTER_FINISH)) {//注册成功
            finish();
        }
    }
}
