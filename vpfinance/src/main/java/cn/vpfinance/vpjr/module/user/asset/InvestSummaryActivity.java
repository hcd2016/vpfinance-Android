package cn.vpfinance.vpjr.module.user.asset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.InvestSummaryTab1Bean;
import cn.vpfinance.vpjr.module.dialog.InvestSummaryDialogFragment;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.user.fragment.InvestSummaryType1Fragment;
import cn.vpfinance.vpjr.module.user.fragment.InvestSummaryType2Fragment;
import cn.vpfinance.vpjr.module.user.fragment.InvestSummaryType3Fragment;
import cn.vpfinance.vpjr.util.Common;

/**
 * 投资统计
 */
public class InvestSummaryActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout mToolBar;
    @Bind(R.id.selectType)
    RadioGroup mSelectType;
    @Bind(R.id.yesInvest)
    LinearLayout mYesInvest;
    @Bind(R.id.noInvest)
    RelativeLayout mNoInvest;
    private HttpService mHttpService;
    private int accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_summary);
        ButterKnife.bind(this);

        mToolBar.setTitle("出借统计").setHeadBackVisible(View.VISIBLE)
                .setImageButtonRight(R.drawable.ic_mine_top_info, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        new AlertDialog.Builder(InvestSummaryActivity.this)
//                                .setMessage(getString(R.string.str_invest_summary_info))
//                                .setPositiveButton("OK", null)
//                                .create()
//                                .show();
                        InvestSummaryDialogFragment.newInstance().show(getSupportFragmentManager(), "InvestSummaryDialogFragment");
                    }
                });

        Intent intent = getIntent();
        if (intent != null) {
            accountType = intent.getIntExtra(Constant.AccountType, 0);
        }

        mHttpService = new HttpService(this, this);
        mHttpService.getProductInvestDistribution(accountType);

        mSelectType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectTypeLoadView(checkedId);
            }
        });
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_ProductInvestDistribution.ordinal()) {
            InvestSummaryTab1Bean info = mHttpService.onGetProductInvestDistribution(json);
            if (info == null) return;
            float sum = info.getCount1() + info.getCount2() + info.getCount3() + info.getCount4() + info.getCount5();
            mYesInvest.setVisibility(sum == 0 ? View.GONE : View.VISIBLE);
            mNoInvest.setVisibility(sum > 0 ? View.GONE : View.VISIBLE);
            if (sum > 0) {
                mSelectType.check(R.id.type1);
            }
        }
    }

    private void selectTypeLoadView(int checkedId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        InvestSummaryType1Fragment type1 = (InvestSummaryType1Fragment) fm.findFragmentByTag("type1");
        InvestSummaryType2Fragment type2 = (InvestSummaryType2Fragment) fm.findFragmentByTag("type2");
        InvestSummaryType3Fragment type3 = (InvestSummaryType3Fragment) fm.findFragmentByTag("type3");

        if (type1 != null) ft.hide(type1);
        if (type2 != null) ft.hide(type2);
        if (type3 != null) ft.hide(type3);

        switch (checkedId) {
            case R.id.type1:
                if (type1 == null)
                    ft.add(R.id.contentView, InvestSummaryType1Fragment.newInstance(accountType), "type1");
                else
                    ft.show(type1);
                break;
            case R.id.type2:
                if (type2 == null)
                    ft.add(R.id.contentView, InvestSummaryType2Fragment.newInstance(accountType), "type2");
                else
                    ft.show(type2);
                break;
            case R.id.type3:
                if (type3 == null)
                    ft.add(R.id.contentView, InvestSummaryType3Fragment.newInstance(accountType), "type3");
                else
                    ft.show(type3);
                break;
        }
        ft.commit();
    }

    @OnClick(R.id.invest)
    public void onClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SWITCH_TAB_NUM, 1);
        startActivity(intent);
        finish();
    }
}
