package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.DBUtils;

/**
 * 存管账户设置
 */
public class DepositAccountSetActivity extends BaseActivity {
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.changeHXPayPassword)
    LinearLayout changeHXPayPassword;
    @Bind(R.id.changeHXBindPhone)
    LinearLayout changeHXBindPhone;
    private UserInfoBean userInfoBean;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_account_setting);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("存管账户设置").setHeadBackVisible(View.VISIBLE);
        userInfoBean = (UserInfoBean) getIntent().getSerializableExtra("userInfoBean");
        user = DBUtils.getUser(this);
    }

    @OnClick({R.id.changeHXPayPassword, R.id.changeHXBindPhone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.changeHXPayPassword://修改交易密码
                if (userInfoBean != null || user != null) {
                    Long userId = user.getUserId();
                    if (!"1".equals(userInfoBean.isOpen)) {
                        boolean isRealName = !TextUtils.isEmpty(userInfoBean.realName);
                        AlertDialogUtils.openBankAccount(this, isRealName, userId.toString());
                        return;
                    }

                    String link = HttpService.mBaseUrl + "/hx/account/manage?userId=" + userId.toString();
                    gotoWeb(link, "修改交易密码");
                }
                break;
            case R.id.changeHXBindPhone://修改绑定手机号码
                if (userInfoBean != null || user != null) {
                    Long userId2 = user.getUserId();
                    if (!"1".equals(userInfoBean.isOpen)) {
                        boolean isRealName = !TextUtils.isEmpty(userInfoBean.realName);
                        AlertDialogUtils.openBankAccount(this, isRealName, userId2.toString());
                        return;
                    }

                    String link2 = HttpService.mBaseUrl + "/hx/account/manage?userId=" + userId2;
                    gotoWeb(link2, "修改绑定手机号");
                }
                break;
        }
    }

    /**
     * 开启本页
     *
     * @param userInfoBean 传递用户信息
     */
    public static void startDepositAccountSetActivity(Context context,UserInfoBean userInfoBean) {
        Intent intent = new Intent(context, DepositAccountSetActivity.class);
        intent.putExtra("userInfoBean", userInfoBean);
        context.startActivity(intent);
    }
}
