package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import de.greenrobot.event.EventBus;

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
    @Bind(R.id.etEmail)
    EditTextWithDel etEmail;
    private UserRegisterBean userRegisterBean;
    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_bind_phone);
        ButterKnife.bind(this);
        titleBar.reset().setTitle("绑定手机号码").setHeadBackVisible(View.VISIBLE);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        userRegisterBean = (UserRegisterBean) getIntent().getSerializableExtra("userRegisterBean");
        if (userRegisterBean.getUserType()) {//个人用户
            etUsername.setVisibility(View.VISIBLE);
            etEmail.setVisibility(View.GONE);
        } else {//企业用户
            etUsername.setVisibility(View.GONE);
            etEmail.setVisibility(View.VISIBLE);
        }
        mHttpService = new HttpService(this, this);
        mHttpService.getImageCode();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_IMAGE_CODE.ordinal()) {//注册图形验证码
            String imageUrl = json.optString("imageUrl");
            Glide
                    .with(this)
                    .load(imageUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用磁盘缓存
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(ivCaptcha);
        } else if (reqId == ServiceCmd.CmdId.CMD_VERIFY_IMAGE_CODE.ordinal()) {//校验图形验证码
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
                    if (userRegisterBean.getUserType()) {
                        userRegisterBean.setPhoneNum(etUsername.getText().toString());
                    }
                    CaptchaActivity.goThis(this, userRegisterBean);
                    break;
                case "7":
                    Utils.Toast("手机号不存在");
                    break;
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_RESPONSIBLE_PHONE.ordinal()) {//获取经办人手机号
            String msg = json.optString("msg");
            switch (msg) {
                case "0":
                    Utils.Toast("服务器异常");
                    break;
                case "1"://成功
                    String managerPhone = json.optString("managerPhone");
                    userRegisterBean.setPhoneNum(managerPhone);
                    userRegisterBean.setEmail(etEmail.getText().toString());
                    mHttpService.getCheckCaptchaImage(etImageCaptcha.getText().toString(), managerPhone, "",
                            etEmail.getText().toString(), "1");
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

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_WEIXIN_LOGIN_SUCCESS)) {//微信登录成功
            finish();
        }
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_WEIXIN_REGISTER_SUCCESS)) {//微信注册成功
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 开启本页
     */
    public static void startWeiXinBindPhoneActivity(Context context, UserRegisterBean userRegisterBean) {
        Intent intent = new Intent(context, WeiXinBindPhoneActivity.class);
        intent.putExtra("userRegisterBean", userRegisterBean);
        context.startActivity(intent);
    }

    @OnClick({R.id.ivCaptcha, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivCaptcha:
                mHttpService.getImageCode();
                break;
            case R.id.btnNext:
                if (userRegisterBean.getUserType()) {//个人
                    if (TextUtils.isEmpty(etUsername.getText().toString())) {
                        Utils.Toast("用户名不能为空");
                        return;
                    }
                    if (TextUtils.isEmpty(etImageCaptcha.getText().toString())) {
                        Utils.Toast("验证码不能为空");
                        return;
                    }
//                    mHttpService.getCheckCaptchaImage(etImageCaptcha.getText().toString(), etUsername.getText().toString(), "",
//                             etUsername.getText().toString(),"1");
                    mHttpService.getVerifyImageCode(etImageCaptcha.getText().toString(), etUsername.getText().toString(), "1","1");
                } else {//企业
                    if (TextUtils.isEmpty(etEmail.getText().toString())) {
                        Utils.Toast("邮箱不能为空");
                        return;
                    }
                    if (TextUtils.isEmpty(etImageCaptcha.getText().toString())) {
                        Utils.Toast("验证码不能为空");
                        return;
                    }
                    mHttpService.getResponsiblePhone(etEmail.getText().toString());
                }
                break;
        }
    }
}
