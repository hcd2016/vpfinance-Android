package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.UserRegisterBean;
import cn.vpfinance.vpjr.view.EditTextWithDel;

public class WeiXinBindPhoneActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarWhiteLayout titleBar;
    @Bind(R.id.etUsername)
    EditTextWithDel etUsername;
    @Bind(R.id.etImageCaptcha)
    EditText etImageCaptcha;
    @Bind(R.id.ivCaptcha)
    ImageView ivCaptcha;
    @Bind(R.id.btnNext)
    Button btnNext;
    private UserRegisterBean userRegisterBean;
    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_bind_phone);
        ButterKnife.bind(this);
        titleBar.reset().setTitle("绑定手机号码").setHeadBackVisible(View.VISIBLE);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        userRegisterBean = (UserRegisterBean) getIntent().getSerializableExtra("userRegisterBean");
        mHttpService = new HttpService(this, this);
        mHttpService.getCaptchaImage();
    }

    @OnClick({R.id.btnNext})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
//                CaptchaActivity.goThis(this,CaptchaActivity.WEI_XIN_BIND_PHONE,"17512039401");
                break;
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
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
        } else if (reqId == ServiceCmd.CmdId.CMD_REGISTER_CHECK_CAPTCHA_IMAGE.ordinal()) {
            userRegisterBean.setPhoneNum(etUsername.getText().toString());
            CaptchaActivity.goThis(this, userRegisterBean);
        }
    }

    //检测图形验证码
    private void doCheckCaptchaImage() {
        String registerPhone = etUsername.getText().toString().trim();
//        String recommendPhone = etRecommendPhone.getText().toString().trim();
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
//        mHttpService.getCheckCaptchaImage(imageCaptcha,registerPhone,recommendPhone);

//        Md5Algorithm md5 = Md5Algorithm.getInstance();
//        rsaPassword = md5.md5Digest((rsaPassword + HttpService.LOG_KEY).getBytes());
//        mHttpService.userRegister(inv, rsaPhoneNum, rsaPassword, rsaPhoneNum, rsaPasscode);//Md5Algorithm
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 开启本页
     */
    public static void startWeiXinBindPhoneActivity(Context context, UserRegisterBean userRegisterBean) {
        Intent intent = new Intent(context, WeiXinBindPhoneActivity.class);
        intent.putExtra("userRegisterBean", userRegisterBean);
        context.startActivity(intent);
    }
}
