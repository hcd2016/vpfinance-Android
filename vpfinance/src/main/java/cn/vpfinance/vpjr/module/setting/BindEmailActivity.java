package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.text.BreakIterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;

/**
 * 绑定邮箱
 */
public class BindEmailActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    @Bind(R.id.et_email)
    EditText etEmail;
    @Bind(R.id.iv_clean)
    ImageView ivClean;
    @Bind(R.id.et_verification_code)
    EditText etVerificationCode;
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.iv_verification_code)
    ImageView ivVerificationCode;
    @Bind(R.id.tv_email_desc)
    TextView tvEmailDesc;
    private HttpService mHttpService;
    private String customerType;
    private String emailPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_bind_email);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("绑定新邮箱").setHeadBackVisible(View.VISIBLE);
        customerType = getIntent().getStringExtra("customerType");
        emailPass = getIntent().getStringExtra("emailPass");
        if (customerType.equals("1")) {//个人
            tvEmailDesc.setText("绑定邮箱作为安全邮箱，可用于接收每月账单");
        } else {
            tvEmailDesc.setText("新邮箱可作为登录账号");
        }
        mHttpService = new HttpService(this, this);
        mHttpService.getCaptchaImage();

    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_IMAGE_CODE.ordinal()) {//注册验证码
            String imageUrl = json.optString("imageUrl");
            Glide.with(this)
                    .load(imageUrl)
                    .asBitmap()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用磁盘缓存
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(ivVerificationCode);
        }
        if (reqId == ServiceCmd.CmdId.CMD_VERIFY_IMAGE_CODE.ordinal()) {//校验验证码
            String msg = json.optString("msg");
            String str = "";
            switch (msg) {
                case "0":
                    str = "发送失败";
                    break;
                case "1":
                    str = "参数有误";
                    break;
                case "2":
                    str = "输入验证码错误";
                    break;
                case "3":
                    str = "手机号码格式不正确";
                    break;
                case "4":
                    str = "操作太频繁，稍后再试";
                    break;
                case "5":
                    str = "手机号己经存在";
                    break;
                case "6"://6. 正确
                    str = "验证码校验成功";
                    EmailSMSVerificationActivity.startEmailSMSVerificationActivity(this,DBUtils.getUser(this).getCellPhone(),customerType,etEmail.getText().toString(),emailPass);
                    break;
                default:
                    str = "其他错误";
                    break;
            }
            Utils.Toast(FinanceApplication.getAppContext(), str);
        }
    }

    @OnClick({R.id.iv_clean, R.id.btn_next, R.id.iv_verification_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_clean://清除邮箱输入
                etEmail.setText("");
                break;
            case R.id.btn_next://下一步
                if (TextUtils.isEmpty(etEmail.getText().toString())) {
                    Utils.Toast("邮箱不能为空!");
                    return;
                } else if (TextUtils.isEmpty(etVerificationCode.getText().toString())) {
                    Utils.Toast("验证码不能为空!");
                    return;
                }
                mHttpService.getVerifyImageCode(etVerificationCode.getText().toString(), DBUtils.getUser(BindEmailActivity.this).getCellPhone(), "1");
                break;
            case R.id.iv_verification_code://获取图形验证码
                mHttpService.getImageCode();
                break;
        }

    }


    /**
     * 开启本页
     */
    public static void startBindEmailActivity(Context context, String customerType,String emailPass) {
        Intent intent = new Intent(context, BindEmailActivity.class);
        intent.putExtra("customerType", customerType);
        intent.putExtra("emailPass",emailPass);
        context.startActivity(intent);
    }
}
