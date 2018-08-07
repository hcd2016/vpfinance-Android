package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;

public class AutoInvestOverSmsActivity extends BaseActivity {

    private HttpService mHttpService;
    private Handler mSmsHandler = new Handler();
    private Button btnSend;
    private int mDelaySeconds = 60;
    private EditText etCode;
    private User user;

    public static void goThis(Context context) {
        Intent intent = new Intent(context, AutoInvestOverSmsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_over_sms);
        ((ActionBarLayout) findViewById(R.id.mActionBar)).reset().setTitle("重新授权").setHeadBackVisible(View.VISIBLE);

        btnSend = (Button) findViewById(R.id.btnSend);
        etCode = (EditText) findViewById(R.id.etCode);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null){
                    mHttpService.getHxSendSms(user.getUserId().toString(),1);
                }
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAction();
            }
        });

        user = DBUtils.getUser(this);
        if (user == null)   return;
        if (!TextUtils.isEmpty(user.getCellPhone())) {
            ((TextView) findViewById(R.id.tvSms)).setText("短信验证码已发送至您的手机" + FormatUtils.hidePhone(user.getCellPhone()));
        }
        mHttpService = new HttpService(this, this);

        btnSend.performClick();
    }

    private void cancelAction() {
        String code = etCode.getText().toString();
        if (TextUtils.isEmpty(code)){
            Utils.Toast("请填写验证码");
            return;
        }
        if (user != null){
            mHttpService.getUnAuthAutoBid(user.getUserId().toString(),code);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (reqId == ServiceCmd.CmdId.CMD_HX_SEND_SMS.ordinal()){
            if ("success".equals(json.optString("result"))){
                mDelaySeconds = 60;
                mSmsHandler.postDelayed(mSmsCallback, 1000);
            }else{
                Utils.Toast(json.optString("message"));
            }
        }else if (reqId == ServiceCmd.CmdId.CMD_unAuthAutoBid.ordinal()){
            String result = json.optString("result");
            if ("success".equals(result)){
                if (user != null){
                    gotoWeb("hx/loansign/authAutoBid?userId=" + user.getUserId(), "自动授权");
                    finish();
                }
            }else{
                String message = json.optString("message");
                Utils.Toast(message);
            }
        }
    }
    private Runnable mSmsCallback = new Runnable()
    {
        @Override
        public void run() {

            mDelaySeconds --;
            if(mDelaySeconds <= 0)
            {
                btnSend.setText("重新发送");
                btnSend.setEnabled(true);
                mSmsHandler.removeCallbacks(mSmsCallback);
                return;
            }
            btnSend.setText("重新发送(" + mDelaySeconds + "s)");
            btnSend.setEnabled(false);
            mSmsHandler.postDelayed(this, 1000);
        }
    };
}
