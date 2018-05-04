package cn.vpfinance.vpjr.module.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.dialog.MailDialogFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.util.Common;

/**
 * Created by Administrator on 2015/12/14.
 */
public class BindMailActivity2 extends BaseActivity implements View.OnClickListener {


    private String emailPass;
    private String email;
    private String cellPhone;
    private TextView tvEmail;
    private TextView tvEmailPass;
    private Button btnSubmit;
    private HttpService mHttpService;
    private User user;
    private ActionBarLayout titleBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_mail2);
        mHttpService = new HttpService(this, this);

        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setTitle("绑定邮箱").setHeadBackVisible(View.VISIBLE);
        tvEmail = (TextView) findViewById(R.id.email);
        tvEmailPass = (TextView) findViewById(R.id.emailPass);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHttpService.getUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSubmit:
                bindMail();
                break;
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            mHttpService.onGetUserInfo(json, user);
//            if (user != null) {
//                emailPass = user.emailPass;
//                email = user.email;
//                cellPhone = user.getCellPhone();
//            }
            cellPhone = json.optString("phone");
            email = json.optString("email");
            emailPass = json.optString("emailPass");

            if ("1".equals(emailPass)){
                tvEmail.setText(email);
                tvEmailPass.setText("已绑定");
                btnSubmit.setText("更换邮箱");
            }else{
                tvEmail.setText("您还未绑定邮箱哦！");
                tvEmailPass.setText("");
                btnSubmit.setText("绑定邮箱");
//                bindMail();
            }
        }

    }

    private void bindMail() {
        MailDialogFragment mailDialogFragment = MailDialogFragment.newInstance(emailPass,cellPhone);
        mailDialogFragment.setOnTextConfrimListener(new MailDialogFragment.onTextConfrimListener() {
            @Override
            public boolean onTextConfrim(String value) {
                if (!TextUtils.isEmpty(value)){
                    tvEmail.setText(value);
                    tvEmailPass.setText("已绑定");
                    btnSubmit.setText("更换邮箱");
                }
                return false;
            }
        });
        mailDialogFragment.show(getSupportFragmentManager(),"MailDialogFragment");
    }
}
