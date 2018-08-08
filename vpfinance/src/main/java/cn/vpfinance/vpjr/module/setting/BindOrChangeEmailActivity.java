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

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

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
        httpService = new HttpService(this, this);
    }

    @OnClick({R.id.tv_btn_remove_binding, R.id.btn_bind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_btn_remove_binding://解除绑定
                break;
            case R.id.btn_bind://绑定/更换邮箱
                if("1".equals(emailPass)) {

                }else {//绑定邮箱
                    BindEmailActivity.startBindEmailActivity(this);
                }
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
            email = json.optString("email");
            emailPass = json.optString("emailPass");
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
        }
        super.onHttpSuccess(reqId, json);
    }

    /**
     * 开启本页
     *
     */
    public static void startBindOrChangeEmailActivity(Context context) {
        Intent intent = new Intent(context, BindOrChangeEmailActivity.class);
        context.startActivity(intent);
    }
}
