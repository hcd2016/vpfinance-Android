package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.view.EditTextWithDel;

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
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
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
    }
}
