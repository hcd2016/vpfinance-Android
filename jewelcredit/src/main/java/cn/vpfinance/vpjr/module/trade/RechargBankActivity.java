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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.vpfinance.vpjr.module.dialog.HxUpdateDialog;
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
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.tv3)
    TextView tv3;
    @Bind(R.id.ivWhat)
    ImageView ivWhat;
    @Bind(R.id.containerAccountName)
    LinearLayout containerAccountName;

    private HttpService mHttpService;
    private String acno;
    private String isUpdate = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharg_bank);
        ButterKnife.bind(this);
        mTitleBar.reset().setTitle("充值").setHeadBackVisible(View.VISIBLE);

        mHttpService = new HttpService(this, this);
        mHttpService.getHxIsUpdate();
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
        if (reqId == ServiceCmd.CmdId.CMD_E_Account.ordinal()){
            EAccountBean eAccountBean = new Gson().fromJson(json.toString(), EAccountBean.class);
            if ("1".equals(isUpdate)){//0是未更新 1是更新
                tvBankAccountNum.setText(eAccountBean.getVpSuperviseAccount());
            }else{
                if (eAccountBean != null && eAccountBean.getStatus() == 1){
                    acno = eAccountBean.getAcno();
                    tvBankAccountNum.setText(acno);
                }else{
                    Utils.Toast(this,"查询失败!稍后再试");
                }
            }

        }else if (reqId == ServiceCmd.CmdId.CMD_HX_IS_UPDATE.ordinal()){
            //0是未更新 1是更新
            isUpdate = json.optString("AppIsUpdate");
//            isUpdate = "1";
            if ("1".equals(isUpdate)){//0是未更新 1是更新
                tv1.setText("存管账户绑定银行卡转账到汇总账户");
                tv2.setText("账号：");
                tvBankAccountNum.setText("");
                tv3.setText("开户行：城市商业银行 - 广东华兴银行股份有限公司后海支行");
                ivWhat.setVisibility(View.VISIBLE);
                tvBankAccountNum.setText("805880100033126");
                containerAccountName.setVisibility(View.VISIBLE);
            }else{
                mHttpService.getEAccountInfo();
                ivWhat.setVisibility(View.GONE);
                tv1.setText("我的银行卡转账到存管账户");
                tv2.setText("存管账户：");
                tv3.setText("开户行查找方式：城市商业银行-广东华兴银行汕头分行");
                containerAccountName.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.click_recharge,R.id.tvRechargeInfo1,R.id.tvRechargeInfo2,R.id.btnCopy,R.id.ivWhat,R.id.btnCopy2})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.ivWhat:
                new HxUpdateDialog().show(getSupportFragmentManager(),"HxUpdateDialog");
                break;
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
                if ("1".equals(isUpdate)) {//0是未更新 1是更新
                    ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    ClipData mClip = ClipData.newPlainText("text", "805880100033126");
                    myClipboard.setPrimaryClip(mClip);
                    Utils.Toast("已经复制到剪切版");
                }else{
                    if (!TextUtils.isEmpty(acno)){
                        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        ClipData mClip = ClipData.newPlainText("text", acno);
                        myClipboard.setPrimaryClip(mClip);
                        Utils.Toast("已经复制到剪切版");
                    }
                }

                break;
            case R.id.btnCopy2:
                ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData mClip = ClipData.newPlainText("text", "深圳五维微品金融信息服务有限公司");
                myClipboard.setPrimaryClip(mClip);
                Utils.Toast("已经复制到剪切版");
                break;
        }

    }
}
