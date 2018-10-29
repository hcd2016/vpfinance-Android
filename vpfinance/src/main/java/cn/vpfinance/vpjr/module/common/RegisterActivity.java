package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import de.greenrobot.event.EventBus;

public class RegisterActivity extends BaseActivity {

    private HttpService mHttpService = null;

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Bind(R.id.etPhone)
    EditTextWithDel etPhone;
    @Bind(R.id.etEmail)
    EditTextWithDel etEmail;
    @Bind(R.id.etCompanyPhone)
    EditTextWithDel etCompanyPhone;
    @Bind(R.id.etImageCaptcha)
    EditText etImageCaptcha;
    @Bind(R.id.etRecommendPhone)
    EditTextWithDel etRecommendPhone;
    @Bind(R.id.ivCaptcha)
    ImageView ivCaptcha;
    @Bind(R.id.btnRegister)
    Button btnRegister;
    @Bind(R.id.containerProtocol)
    LinearLayout containerProtocol;
    @Bind(R.id.cbProtocol)
    CheckBox cbProtocol;
    @Bind(R.id.tvProtocol1)
    TextView tvProtocol1;
    @Bind(R.id.tvProtocol2)
    TextView tvProtocol2;
    @Bind(R.id.tvProtocol3)
    TextView tvProtocol3;

    private boolean isPersonType = true;

    public static void goThis(Context context, boolean isPersonType) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra("isPersonType", isPersonType);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        if (null != intent) {
            isPersonType = intent.getBooleanExtra("isPersonType", true);
        }
        mHttpService = new HttpService(this, this);
        mHttpService.getImageCode();
        initView();
    }

    @Override
    protected void initView() {
        titleBar.setHeadBackVisible(View.VISIBLE);
        titleBar.setTitle(isPersonType ? "注册" : "企业注册");
        etPhone.setVisibility(isPersonType ? View.VISIBLE : View.GONE);
        etRecommendPhone.setVisibility(isPersonType ? View.VISIBLE : View.GONE);
        etEmail.setVisibility(!isPersonType ? View.VISIBLE : View.GONE);
        etCompanyPhone.setVisibility(!isPersonType ? View.VISIBLE : View.GONE);


//        setWidth(R.drawable.login_username,etPhone);
//        setWidth(R.drawable.login_icon_email,etEmail);
//        setWidth(R.drawable.register_recommend,etCompanyPhone);
//        setWidth(R.drawable.register_code,etImageCaptcha);
//        setWidth(R.drawable.register_recommend,etRecommendPhone);


//        etPhone.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (Utils.isMobile(s.toString())) {
//                    mHttpService.isExistPhone(s.toString());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        cbProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbProtocol.isChecked()) {
                    btnRegister.setEnabled(true);
                } else {
                    btnRegister.setEnabled(false);
                }
            }
        });
    }

//    private void setWidth(int id,TextView view) {
//        Drawable drawable = getResources().getDrawable(id);
//        drawable.setBounds(0, 0, 80, 120);//第一0是距左边距离，第二0是距上边距离，30、35分别是长宽
//        view.setCompoundDrawables(drawable,null,null,null);//只放左边
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.ivCaptcha, R.id.btnRegister, R.id.cbProtocol, R.id.tvProtocol1, R.id.tvProtocol2, R.id.tvProtocol3})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.ivCaptcha:
                mHttpService.getImageCode();
                break;
            case R.id.btnRegister:
                doCheckCaptchaImage();
                break;
            case R.id.cbProtocol:
                break;
            case R.id.tvProtocol1:
                gotoWeb("/registration/useragreement", "微品金融用户服务协议");
                break;
            case R.id.tvProtocol2:
                gotoWeb("/registration/riskForbidAgreement", "网络借贷风险和禁止性行为提示书");
                break;
            case R.id.tvProtocol3:
                gotoWeb("/registration/financeLegalAgreement", "资金来源合法承诺书");
                break;
        }
    }

    private void doCheckCaptchaImage() {
        String registerPhone = etPhone.getText().toString().trim();
        String etEmailText = etEmail.getText().toString().trim();
        String recommendPhone = etRecommendPhone.getText().toString().trim();
        String imageCaptcha = etImageCaptcha.getText().toString();
        String etCompanyPhoneText = etCompanyPhone.getText().toString();

        if (isPersonType) {
            if (TextUtils.isEmpty(registerPhone)) {
                Utils.Toast(this, "手机号不能为空!");
                return;
            }
            if (!Utils.isMobile(registerPhone)) {
                Utils.Toast(this, "手机号格式不正确!");
                return;
            }
        } else {
            if (!Utils.checkEmail(etEmailText)) {
                Utils.Toast(this, "邮箱格式不正确!");
                return;
            }
            if (TextUtils.isEmpty(etCompanyPhoneText)) {
                Utils.Toast(this, "手机号不能为空!");
                return;
            }
            if (!Utils.isMobile(etCompanyPhoneText)) {
                Utils.Toast(this, "手机号格式不正确!");
                return;
            }
        }
        if ("".equals(imageCaptcha)) {
            Utils.Toast(this, "图形验证码不能为空!");
            return;
        }
        if (isPersonType) {
            mHttpService.getCheckCaptchaImage(imageCaptcha, registerPhone, recommendPhone, registerPhone, "1");
        } else {
            mHttpService.getCheckCaptchaImage(imageCaptcha, etCompanyPhoneText, recommendPhone, etEmailText, "1");
        }


//        Md5Algorithm md5 = Md5Algorithm.getInstance();
//        rsaPassword = md5.md5Digest((rsaPassword + HttpService.LOG_KEY).getBytes());
//        mHttpService.userRegister(inv, rsaPhoneNum, rsaPassword, rsaPhoneNum, rsaPasscode);//Md5Algorithm
    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {
        super.onHttpSuccess(reqId, json);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_IMAGE_CODE.ordinal()) {
            String imageUrl = json.optString("imageUrl");
            Glide
                    .with(this)
                    .load(imageUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用磁盘缓存
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(ivCaptcha);
        } else if (reqId == ServiceCmd.CmdId.CMD_REGISTER_CHECK_CAPTCHA_IMAGE.ordinal()) {
            String msg = json.optString("msg");
            switch (msg) {
                case "0":
                    Utils.Toast("发送失败");
                    break;
                case "1":
                    Utils.Toast("参数有误");
                    break;
                case "2":
                    Utils.Toast("输入验证码错误");
                    break;
                case "3":
                    Utils.Toast("手机号码格式不正确");
                    break;
                case "4":
                    Utils.Toast("操作太频繁，稍后再试");
                    break;
                case "5":
                    Utils.Toast("手机号己经存在");
                    break;
                case "6"://校验成功
                    UserRegisterBean userRegisterBean = new UserRegisterBean();
                    userRegisterBean.setUserType(isPersonType);
                    if (userRegisterBean.getUserType()) {
                        userRegisterBean.setPhoneNum(etPhone.getText().toString());
                    } else {
                        userRegisterBean.setPhoneNum(etCompanyPhone.getText().toString());
                        userRegisterBean.setEmail(etEmail.getText().toString());
                    }
                    userRegisterBean.setReferrerNum(etRecommendPhone.getText().toString());
                    CaptchaActivity.goThis(this, userRegisterBean);
                    break;
                case "7":
                    Utils.Toast("账号已存在");
                    break;
                case "8":
                    Utils.Toast("推荐人不存在");
                    break;
            }
        }
    }

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_REGISTER_FINISH)) {//注册完成
            finish();
        }
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_WEIXIN_LOGIN_SUCCESS)) {//微信登录成功
            finish();
        }
    }

    @Override
    public void onHttpError(int reqId, String msg) {
        if (reqId == ServiceCmd.CmdId.CMD_userRegister.ordinal()) {
            btnRegister.setEnabled(true);
        }
    }

}
