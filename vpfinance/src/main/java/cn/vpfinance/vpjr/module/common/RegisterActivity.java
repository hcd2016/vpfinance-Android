package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import cn.vpfinance.vpjr.view.EditTextWithDel;

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

        Intent intent = getIntent();
        if (null != intent) {
            isPersonType = intent.getBooleanExtra("isPersonType", true);
        }
        mHttpService = new HttpService(this, this);

        mHttpService.getCaptchaImage();
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

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Utils.isMobile(s.toString())) {
                    mHttpService.isExistPhone(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.ivCaptcha, R.id.btnRegister, R.id.cbProtocol, R.id.tvProtocol1, R.id.tvProtocol2, R.id.tvProtocol3})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.ivCaptcha:
                mHttpService.getCaptchaImage();
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
        String recommendPhone = etRecommendPhone.getText().toString().trim();
        String imageCaptcha = etImageCaptcha.getText().toString();

        if ("".equals(registerPhone)) {
            Utils.Toast(this, "手机号不能为空!");
            return;
        }
        if (!Utils.isMobile(registerPhone)) {
            Utils.Toast(this, "手机号格式不正确!");
            return;
        }
        if ("".equals(imageCaptcha)) {
            Utils.Toast(this, "图形验证码不能为空!");
            return;
        }
        mHttpService.getCheckCaptchaImage(imageCaptcha,registerPhone,recommendPhone);

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

        if (reqId == ServiceCmd.CmdId.CMD_REGISTER_CAPTCHA_IMAGE.ordinal()) {
            String imageUrl = json.optString("imageUrl");
            Glide
                    .with(this)
                    .load(imageUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用磁盘缓存
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(ivCaptcha);
        } else if (reqId == ServiceCmd.CmdId.CMD_REGISTER_CHECK_CAPTCHA_IMAGE.ordinal()){
            if (isPersonType) {
                CaptchaActivity.goThis(this, CaptchaActivity.REGISTER_PERSON, null);
            } else {
                CaptchaActivity.goThis(this, CaptchaActivity.REGISTER_COMPANY, null);
            }
        }

        /*if (reqId == ServiceCmd.CmdId.CMD_userRegister.ordinal()) {
            btnRegister.setEnabled(true);

            String msg = mHttpService.onUserRegister(json);
            if (msg != "") {
                Utils.Toast(this, msg);
                if ("注册成功".equals(msg)) {
                    Utils.Toast(this, "注册成功!");

                    mHttpService.userLogin(rsaPhoneNum, rsaPassword);
                    FinanceApplication application = (FinanceApplication) getApplication();
                    application.isFirstRegieter = true;
                }
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_userLogin.ordinal()) {
            String msg = mHttpService.onUserLogin(this, json);
            if (!msg.equals("")) {
                Utils.Toast(this, msg);
                return;
            } else {
                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, rsaPhoneNum + "");
                preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME, rsaPhoneNum + "");
                preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD, rsaPassword);

                uid = AppState.instance().getSessionCode();
                String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
                if (!TextUtils.isEmpty(uid) && !uid.equals(savedUid)) {
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID, uid);
                }
            }


            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            //清理login present标志
            HttpService.clearPresentLoginFlag();
            startActivity(new Intent(this, LockSetupActivity.class));

            finish();
        }

        if (reqId == ServiceCmd.CmdId.CMD_isExistPhone.ordinal()) {
            int errNo = mHttpService.onCheckPhoneExist(json);
            if (errNo == 1) {
                mPhoneExist = true;
                Utils.Toast(this, "手机号已存在");
            } else {
                mPhoneExist = false;
            }
        }*/
    }

    @Override
    public void onHttpError(int reqId, String msg) {
        if (reqId == ServiceCmd.CmdId.CMD_userRegister.ordinal()) {
            btnRegister.setEnabled(true);
        }
    }

}
