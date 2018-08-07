package cn.vpfinance.vpjr.module.user.asset;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.EAccountBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * 我的存管账户
 * Created by zzlz13 on 2017/8/2.
 */

public class AccountEActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.bankName)
    TextView bankName;
    @Bind(R.id.bankNum)
    TextView bankNum;
    @Bind(R.id.money)
    TextView money;
    @Bind(R.id.click_bind_card_active)
    TextView click_bind_card_active;

    private HttpService httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_e);
        ButterKnife.bind(this);

        titleBar.reset().setTitle("我的存管账户").setHeadBackVisible(View.VISIBLE);
        httpService = new HttpService(this, this);
        httpService.getEAccountInfo();

        User user = DBUtils.getUser(this);
        if (user != null){
            SharedPreferencesHelper.getInstance(this).putBooleanValue("firstInto_" + user.getUserId(),false);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        EAccountBean eAccountBean = new Gson().fromJson(json.toString(), EAccountBean.class);
        if (eAccountBean != null && eAccountBean.getStatus() == 1){
            name.setText(eAccountBean.getName());
            bankName.setText(eAccountBean.getBankName());
            money.setText(eAccountBean.getCashBalance());
            bankNum.setText(eAccountBean.getAcno());
            click_bind_card_active.setVisibility(eAccountBean.getIsBindHxBank() == 0 ? View.VISIBLE : View.GONE);
            if (eAccountBean.getCustomerType() == 2){//1个人账户2对公账户   对公账户隐藏绑卡激活
                click_bind_card_active.setVisibility(View.GONE);
            }
        }else{
            Utils.Toast(this,"查询失败!稍后再试");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.click_bind_card_active})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.click_bind_card_active:
                User user = DBUtils.getUser(this);
                if (user != null) {
                    Long userId = user.getUserId();
                    //跳转到存管绑卡第三方界面
                    BindBankHintActivity.goThis(this,userId.toString());
                } else {
                    gotoActivity(LoginActivity.class);
                }
                break;
        }
    }
}
