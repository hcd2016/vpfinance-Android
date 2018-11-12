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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseActivity;
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
    private String managerPhone;

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
        customerType = getIntent().getStringExtra("customerType");
        emailPass = getIntent().getStringExtra("emailPass");
        if("1".equals(emailPass)) {
            titleBar.setTitle("更换邮箱").setHeadBackVisible(View.VISIBLE);
        }else {
            titleBar.setTitle("绑定新邮箱").setHeadBackVisible(View.VISIBLE);
        }
        if (customerType.equals("1")) {//个人
            tvEmailDesc.setText("绑定邮箱作为安全邮箱，可用于接收每月账单");
        } else {
            tvEmailDesc.setText("新邮箱可作为登录账号");
        }
        mHttpService = new HttpService(this, this);
        mHttpService.getImageCode();

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
//                    str = "验证码校验成功";
                    if(customerType.equals("1")) {
                        EmailSMSVerificationActivity.startEmailSMSVerificationActivity(this, DBUtils.getUser(this).getCellPhone(), customerType, etEmail.getText().toString(), emailPass);
                    }else {
                        EmailSMSVerificationActivity.startEmailSMSVerificationActivity(this, managerPhone, customerType, etEmail.getText().toString(), emailPass);
                    }
                    break;
                default:
                    str = "其他错误";
                    break;
            }
            if(!TextUtils.isEmpty(str)) {
                Utils.Toast(App.getAppContext(), str);
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_RESPONSIBLE_PHONE.ordinal()) {//获取经办人手机号
            String msg = json.optString("msg");
            switch (msg) {
                case "0":
                    Utils.Toast("服务器异常");
                    break;
                case "1"://成功
                    managerPhone = json.optString("managerPhone");
                    mHttpService.getVerifyImageCode(etVerificationCode.getText().toString(), managerPhone, "1", "3");
                    break;
                case "2"://
                    Utils.Toast("用户不存在,请先注册");
                    finish();
                    break;
                case "3"://非企业用户
                    Utils.Toast("该用户名非企业用户,请先注册");
                    finish();
                    break;
                case "4":
                    Utils.Toast("验证码超时");
                    break;
                case "5":
                    Utils.Toast("验证码错误");
                    break;
            }
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
                }
                if(!Utils.checkEmail(etEmail.getText().toString())) {
                    Utils.Toast("邮箱格式错误!");
                    return;
                }
                if (TextUtils.isEmpty(etVerificationCode.getText().toString())) {
                    Utils.Toast("验证码不能为空!");
                    return;
                }
                if (customerType.equals("1")) {//个人
                    mHttpService.getVerifyImageCode(etVerificationCode.getText().toString(), DBUtils.getUser(BindEmailActivity.this).getCellPhone(), "1", "");
                } else {
                    String email = getIntent().getStringExtra("email");
                    if(!TextUtils.isEmpty(email)) {
                        mHttpService.getResponsiblePhone(email);
                    }
//                    mHttpService.getVerifyImageCode(etVerificationCode.getText().toString(), DBUtils.getUser(BindEmailActivity.this).getCellPhone(), "1", "3");
                }
                break;
            case R.id.iv_verification_code://获取图形验证码
                mHttpService.getImageCode();
                break;
        }

    }


    /**
     * 开启本页
     */
    public static void startBindEmailActivity(Context context, String customerType, String emailPass,String email) {
        Intent intent = new Intent(context, BindEmailActivity.class);
        intent.putExtra("customerType", customerType);
        intent.putExtra("emailPass", emailPass);
        intent.putExtra("email", email);
        context.startActivity(intent);
    }
}
