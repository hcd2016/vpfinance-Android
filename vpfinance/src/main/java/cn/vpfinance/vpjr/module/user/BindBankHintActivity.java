package cn.vpfinance.vpjr.module.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.DifColorTextStringBuilder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.MyClickableSpan;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.EAccountBean;
import cn.vpfinance.vpjr.util.GsonUtil;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * 绑卡激活之前提示界面
 */
public class BindBankHintActivity extends BaseActivity {

    @Bind(R.id.mActionBarLayout)
    ActionBarLayout mActionBarLayout;
    @Bind(R.id.btnKnow)
    Button btnKnow;
    @Bind(R.id.ll_person_hint_container)
    LinearLayout llPersonHintContainer;
    @Bind(R.id.tv_account_name)
    TextView tvAccountName;
    @Bind(R.id.btn_account_name_copy)
    TextView btnAccountNameCopy;
    @Bind(R.id.tv_account_num)
    TextView tvAccountNum;
    @Bind(R.id.btn_account_num_copy)
    TextView btnAccountNumCopy;
    @Bind(R.id.tv_account_bank_info)
    TextView tvAccountBankInfo;
    @Bind(R.id.ll_company_hint_container)
    LinearLayout llCompanyHintContainer;
    @Bind(R.id.tv_desc)
    TextView tvDesc;
    @Bind(R.id.btnKnow_company)
    Button btnKnowCompany;
    private String userId = "";
    private HttpService httpService;
    private EAccountBean eAccountBean;

    public static void goThis(Context context, String userId) {
        Intent intent = new Intent(context, BindBankHintActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_hint);
        ButterKnife.bind(this);
        httpService = new HttpService(this, this);
        ((ActionBarLayout) findViewById(R.id.mActionBarLayout))
                .reset()
                .setHeadBackVisible(View.VISIBLE)
                .setTitle("温馨提示");
        userId = getIntent().getStringExtra("userId");
        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(this);
        if (sp.getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE)) {//个人账户
            llPersonHintContainer.setVisibility(View.VISIBLE);
            llCompanyHintContainer.setVisibility(View.GONE);
        } else {
            llPersonHintContainer.setVisibility(View.GONE);
            llCompanyHintContainer.setVisibility(View.VISIBLE);
            httpService.getEAccountInfo();
        }
        findViewById(R.id.btnKnow).setOnClickListener(new View.OnClickListener() {//个人
            @Override
            public void onClick(View view) {
                gotoWeb("hx/bankcard/bind?userId=" + userId, "");
                finish();
            }
        });

        findViewById(R.id.btnKnow_company).setOnClickListener(new View.OnClickListener() {//企业,和个人跳转一样
            @Override
            public void onClick(View view) {
                gotoWeb("hx/bankcard/bind?userId=" + userId, "");
                finish();
            }
        });
        DifColorTextStringBuilder difColorTextStringBuilder = new DifColorTextStringBuilder();
        difColorTextStringBuilder.setContent(tvDesc.getText().toString())
                .setHighlightContent("图文指引>", R.color.btn_blue)
                .setHighlightContent("图文指引>", new MyClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        gotoWeb("https://www.vpfinance.cn/h5/help/hxGuideOpen?company=1", "");
                        finish();
                    }
                })
                .setTextView(tvDesc)
                .create();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_E_Account.ordinal()) {
            eAccountBean = GsonUtil.modelParser(json.toString(), EAccountBean.class);
            tvAccountName.setText(eAccountBean.getVpAccountName());
            tvAccountNum.setText(eAccountBean.getVpAccount());
            tvAccountBankInfo.setText("开户行:  " + eAccountBean.getVpOpenName());
        }

    }

    @OnClick({R.id.btn_account_name_copy, R.id.btn_account_num_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_account_name_copy://
                if (null != eAccountBean) {
                    Utils.copy(eAccountBean.getVpAccountName(), this);
                    Utils.Toast("复制成功");
                }
                break;
            case R.id.btn_account_num_copy://
                if (null != eAccountBean) {
                    Utils.copy(eAccountBean.getVpAccount(), this);
                    Utils.Toast("复制成功");
                }
                break;
        }
    }
}
