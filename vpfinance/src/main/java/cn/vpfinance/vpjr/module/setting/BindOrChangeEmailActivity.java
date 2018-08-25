package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.dialog.CommonDialogFragment;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialog;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * 绑定/更换邮箱
 */
public class BindOrChangeEmailActivity extends BaseActivity {
    @Bind(R.id.ll_no_email_container)
    LinearLayout llNoEmailContainer;
    @Bind(R.id.tv_email)
    TextView tvEmail;
    @Bind(R.id.tv_btn_remove_binding)
    TextView tvBtnRemoveBinding;
    @Bind(R.id.ll_have_email_container)
    LinearLayout llHaveEmailContainer;
    @Bind(R.id.btn_bind)
    Button btnBind;
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    private HttpService httpService;
    private String email;
    private String emailPass;
    private String customerType;
    private CommonTipsDialog commonTipsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_or_change_email);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("邮箱绑定").setHeadBackVisible(View.VISIBLE);
        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(this);
        if (sp.getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE)) {
            tvBtnRemoveBinding.setVisibility(View.VISIBLE);
        } else {
            tvBtnRemoveBinding.setVisibility(View.GONE);
        }
        httpService = new HttpService(this, this);
    }

    @OnClick({R.id.tv_btn_remove_binding, R.id.btn_bind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_btn_remove_binding://解除绑定
                commonTipsDialog = new CommonTipsDialog(this);
                commonTipsDialog.setTitle("邮箱解绑");
                commonTipsDialog.setTips("是否需要解除邮箱绑定?解除后将无法通过邮箱接收每月账单");
                commonTipsDialog.setRightBtnTextColor(R.color.red_text);
                commonTipsDialog.setOnRightClickListener(new CommonTipsDialog.OnRightClickListener() {
                    @Override
                    public void onRightClick() {//确定
                        httpService.unBindEmail(DBUtils.getUser(BindOrChangeEmailActivity.this).getUserId() + "");
                    }
                });
                commonTipsDialog.show();
                break;
            case R.id.btn_bind://绑定/更换邮箱
                BindEmailActivity.startBindEmailActivity(this, customerType, emailPass, tvEmail.getText().toString());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpService.getUserInfo();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            customerType = json.optString("customerType");
            if (customerType.equals("1")) {
                email = json.optString("email");
            } else {
                email = json.optString("phone");
            }
            emailPass = json.optString("emailPass");
            if (customerType.equals("1")) {
                if ("1".equals(emailPass)) {
                    llHaveEmailContainer.setVisibility(View.VISIBLE);
                    llNoEmailContainer.setVisibility(View.GONE);
                    tvEmail.setText(email);
                    btnBind.setText("更换邮箱");
                } else {
                    llHaveEmailContainer.setVisibility(View.GONE);
                    llNoEmailContainer.setVisibility(View.VISIBLE);
                    btnBind.setText("绑定邮箱");
                }
            } else {
                llHaveEmailContainer.setVisibility(View.VISIBLE);
                llNoEmailContainer.setVisibility(View.GONE);
                tvEmail.setText(email);
                btnBind.setText("更换邮箱");
            }

        }
        if (reqId == ServiceCmd.CmdId.CMD_UNBIND_EMAIL.ordinal()) {//邮箱解除绑定
            if (!isHttpHandle(json)) return;
            String msg = json.optString("msg");
            switch (msg) {
                case "0":
                    Utils.Toast("解除失败");
                    break;
                case "1":
                    Utils.Toast("邮箱解除成功");
                    llHaveEmailContainer.setVisibility(View.GONE);
                    llNoEmailContainer.setVisibility(View.VISIBLE);
                    btnBind.setText("绑定邮箱");
                    break;
                case "2":
                    Utils.Toast("用户不存在");
                    break;
            }
            commonTipsDialog.dismiss();
        }

        super.onHttpSuccess(reqId, json);
    }

    /**
     * 开启本页
     */
    public static void startBindOrChangeEmailActivity(Context context) {
        Intent intent = new Intent(context, BindOrChangeEmailActivity.class);
        context.startActivity(intent);
    }
}
