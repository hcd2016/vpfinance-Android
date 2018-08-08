package cn.vpfinance.vpjr.module.setting;

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
    private HttpService mHttpService;

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
        mHttpService = new HttpService(this, this);
        mHttpService.getCaptchaImage();
    }

    //todo 接口待改
    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_REGISTER_CAPTCHA_IMAGE.ordinal()) {//注册验证码
            String imageUrl = json.optString("imageUrl");
            Glide
                    .with(this)
                    .load(imageUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用磁盘缓存
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(ivVerificationCode);
        } else if (reqId == ServiceCmd.CmdId.CMD_REGISTER_CHECK_CAPTCHA_IMAGE.ordinal()) {//检测验证码
        }
    }

    @OnClick({R.id.iv_clean, R.id.btn_next,R.id.iv_verification_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_clean://清除邮箱输入
                etEmail.setText("");
                break;
            case R.id.btn_next://下一步
                if(TextUtils.isEmpty(etEmail.getText().toString())) {
                    Utils.Toast("邮箱不能为空!");
                    return;
                }else if(TextUtils.isEmpty(etVerificationCode.getText().toString())) {
                    Utils.Toast("验证码不能为空!");
                    return;
                }
                //校验验证码.
                break;
        }
    }

    /**
     * 开启本页
     *
     */
    public static void startBindEmailActivity(Context context) {
        Intent intent = new Intent(context, BindEmailActivity.class);
        context.startActivity(intent);
    }
}
