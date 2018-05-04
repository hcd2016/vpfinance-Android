package cn.vpfinance.vpjr.module.user.personal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.dialog.VoucherDialogFragment;
import cn.vpfinance.vpjr.module.voucher.fragment.VoucherTabFragment;
import cn.vpfinance.vpjr.util.Common;

/**
 * Created by Administrator on 2015/10/29.
 */
public class VoucherV3Activity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout mToolBar;
    @Bind(R.id.radio_group)
    RadioGroup mRadioGroup;
    @Bind(R.id.ibInvite)
    ImageButton mIbInvite;

    private HttpService mHttpService;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_v3);
        ButterKnife.bind(this);
        mContext = this;
        mHttpService = new HttpService(this,this);
        mToolBar.setTitle("代金券").setHeadBackVisible(View.VISIBLE)
                .setImageButtonRight(R.drawable.toolbar_add, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VoucherDialogFragment voucherDialogFragment = new VoucherDialogFragment();
                        voucherDialogFragment.setOnTextConfrimListener(new VoucherDialogFragment.onTextConfrimListener() {
                            @Override
                            public boolean onTextConfrim(String value) {
                                if (value != null) {
                                    mHttpService.voucherExchange(value);
                                }
                                return true;
                            }
                        });
                        voucherDialogFragment.show(getSupportFragmentManager(), "VoucherDialog");
                    }
                });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectTypeLoadView(checkedId);
            }
        });

        mIbInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteGiftActivity.goThis(mContext);
            }
        });

        mRadioGroup.check(R.id.canUse);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_voucherExchange.ordinal()) {
            if (json == null) {
                return;
            }
            String msg = json.optString("msg");
            String money = json.optString("money");
            if ("1".equals(msg)) {
                Toast.makeText(mContext, "恭喜您兑换了" + money + "元代金券!", Toast.LENGTH_LONG).show();
            } else if ("0".equals(msg)) {
                Toast.makeText(mContext, "输入兑换码有误！", Toast.LENGTH_SHORT).show();
            } else if ("2".equals(msg)) {
                Toast.makeText(mContext, "兑换码已使用！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectTypeLoadView(int checkedId){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        VoucherTabFragment type1 = (VoucherTabFragment) fm.findFragmentByTag("type1");
        VoucherTabFragment type2 = (VoucherTabFragment) fm.findFragmentByTag("type2");
        VoucherTabFragment type3 = (VoucherTabFragment) fm.findFragmentByTag("type3");

        if (type1 != null)  ft.hide(type1);
        if (type2 != null)  ft.hide(type2);
        if (type3 != null)  ft.hide(type3);

        switch (checkedId){
            case R.id.canUse:
                if (type1 == null)
                    ft.add(R.id.contentView,VoucherTabFragment.newInstance(1,"1"),"type1");
                else
                    ft.show(type1);
                break;
            case R.id.noCanUse:
                if (type2 == null)
                    ft.add(R.id.contentView,VoucherTabFragment.newInstance(1,"2"),"type2");
                else
                    ft.show(type2);
                break;
            case R.id.overdue:
                if (type3 == null)
                    ft.add(R.id.contentView,VoucherTabFragment.newInstance(1,"3"),"type3");
                else
                    ft.show(type3);
                break;
        }
        ft.commit();
    }
}
