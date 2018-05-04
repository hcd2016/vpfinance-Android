package cn.vpfinance.vpjr.module.trade;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
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
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.EAccountBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.more.MyQRcodeActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * 银行存管 - 充值
 * Created by zzlz13 on 2017/7/31.
 */

public class RechargBankActivity extends BaseActivity {

    @Bind(R.id.title_bar)
    ActionBarWhiteLayout mTitleBar;
    @Bind(R.id.et_money)
    EditText mMoney;
    @Bind(R.id.tvBankAccountNum)
    TextView tvBankAccountNum;

    private HttpService mHttpService;
    private String acno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharg_bank);
        ButterKnife.bind(this);
        mTitleBar.reset().setTitle("充值").setHeadBackVisible(View.VISIBLE);

        mHttpService = new HttpService(this, this);
        mHttpService.getEAccountInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (!isHttpHandle(json)) return;
        EAccountBean eAccountBean = new Gson().fromJson(json.toString(), EAccountBean.class);
        if (eAccountBean != null && eAccountBean.getStatus() == 1){
            acno = eAccountBean.getAcno();
            tvBankAccountNum.setText(acno);
        }else{
            Utils.Toast(this,"查询失败!稍后再试");
        }
    }

    @OnClick({R.id.click_recharge,R.id.tvRechargeInfo1,R.id.tvRechargeInfo2,R.id.btnCopy})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.click_recharge:
                User user = DBUtils.getUser(this);
                final Long userId = user.getUserId();
                boolean isBindBank = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_IS_BIND_BANK);
                if (!isBindBank){
                    new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.message_no_bind_bank))
                            .setPositiveButton("去绑定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BindBankHintActivity.goThis(RechargBankActivity.this,userId.toString());
                                }
                            })
                            .setNegativeButton("取消",null)
                            .show();
                    return;
                }
                String money = mMoney.getText().toString();
                if (user != null) {
                    if (TextUtils.isEmpty(money)){
                        Utils.Toast("请填写充值金额");
                        return;
                    }
                    gotoWeb("/hx/account/recharge?userId=" + userId + "&amount=" + money, "");
                } else {
                    gotoActivity(LoginActivity.class);
                }
                break;
            case R.id.tvRechargeInfo1:
                gotoWeb("/h5/help/hxGuideRechargeFirst","充值");
                break;
            case R.id.tvRechargeInfo2:
                gotoWeb("/h5/help/hxGuideRechargeSecond","充值");
                break;
            case R.id.btnCopy:
                if (!TextUtils.isEmpty(acno)){
                    ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    ClipData mClip = ClipData.newPlainText("text", acno);
                    myClipboard.setPrimaryClip(mClip);
                    Utils.Toast("已经复制到剪切版");
                }
                break;
        }

    }
}
