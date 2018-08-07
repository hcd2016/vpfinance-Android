package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.yintong.pay.utils.Md5Algorithm;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.module.gusturelock.LockSetupActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * 安全中心
 */
public class SafetyCentreActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.modifypasword)
    LinearLayout modifypasword;
    @Bind(R.id.lockSwitch)
    SwitchCompat lockSwitch;
    private boolean close = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_centre);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("安全中心").setHeadBackVisible(View.VISIBLE);
        initLockSwitch(this);
    }

    //手势密码
    private void initLockSwitch(final Context context) {
        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        String lockStr = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING);
        lockSwitch.setChecked(!TextUtils.isEmpty(lockStr));
        lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!close) {
                        if (!AppState.instance().logined()) {
                            context.startActivity(new Intent(SafetyCentreActivity.this, LoginActivity.class));
                            finish();
                            return;
                        }
                        context.startActivity(new Intent(SafetyCentreActivity.this, LockSetupActivity.class));

                        ArrayMap<String, String> map = new ArrayMap<String, String>();
                        map.put("switch", "on");
                        MobclickAgent.onEvent(SafetyCentreActivity.this, "GestureLock", map);
                    }
                    close = false;

                } else {
                    final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(SafetyCentreActivity.this);
                    String patternString = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING, null);
                    if (patternString == null) {
                        return;
                    }

                    TextInputDialogFragment tidf = TextInputDialogFragment.newInstance("登录验证", "请输入登录密码", false);
                    tidf.setOnTextConfrimListener(new TextInputDialogFragment.onTextConfrimListener() {
                        @Override
                        public boolean onTextConfrim(String value) {
                            if (value != null) {
                                Md5Algorithm md5 = Md5Algorithm.getInstance();
                                value = md5.md5Digest((value + HttpService.LOG_KEY).getBytes());
                                String pwd = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD, null);
                                if (value.equals(pwd)) {
                                    preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_STRING);
                                    lockSwitch.setChecked(false);
                                    close = false;

                                    ArrayMap<String, String> map = new ArrayMap<String, String>();
                                    map.put("switch", "off");
                                    MobclickAgent.onEvent(SafetyCentreActivity.this, "GestureLock", map);

                                    return true;
                                } else {
                                    Utils.Toast(SafetyCentreActivity.this, "密码错误!");
                                    close = true;
                                    lockSwitch.setChecked(true);
                                }
                            }
                            return false;
                        }
                    });
                    tidf.setOnTextCancleListener(new TextInputDialogFragment.onTextConfrimListener() {
                        @Override
                        public boolean onTextConfrim(String value) {
                            close = true;
                            lockSwitch.setChecked(true);
                            return true;
                        }
                    });
                    tidf.show(getSupportFragmentManager(), "inputPwd");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        String lockStr = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING);
        lockSwitch.setChecked(!TextUtils.isEmpty(lockStr));
    }

    @OnClick({R.id.modifypasword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.modifypasword://修改登录密码
                gotoActivity(PasswordChangeActivity.class);
                break;
        }
    }

    /**
     * 开启本页
     *
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SafetyCentreActivity.class);
        context.startActivity(intent);
    }
}