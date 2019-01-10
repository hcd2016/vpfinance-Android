package cn.vpfinance.vpjr.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.jewelcredit.ui.widget.ActionBarLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.adapter.AutoSettingAdapter;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.AutoInvestSettingBean;

/**
 * 自动投标设置 - 风险等级
 * Created by zzlz13 on 2017/7/31.
 */

public class AutoInvestRiskLevelActivity extends BaseActivity {

    @Bind(R.id.title_bar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.switchOpen)
    ToggleButton switchOpen;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private AutoSettingAdapter myAdapter;
    private String riskLevelValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_rish_level);
        ButterKnife.bind(this);
        mTitleBar.reset().setTitle("风险等级").setHeadBackVisible(View.VISIBLE);

        ButterKnife.findById(this, R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAction();
            }
        });

        loadDate();
    }

    protected void loadDate() {
        Intent intent = getIntent();
        if (intent != null) {
            riskLevelValue = intent.getStringExtra(AutoInvestSettingActivity.ARGS_RISK_LEVEL_VALUE);
            ArrayList<AutoInvestSettingBean.OptionsBean.SecurityLevelBean> riskLevelList = (ArrayList<AutoInvestSettingBean.OptionsBean.SecurityLevelBean>) intent.getSerializableExtra(AutoInvestSettingActivity.ARGS_RISK_LEVEL);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            myAdapter = new AutoSettingAdapter(AutoInvestRiskLevelActivity.this, riskLevelList, riskLevelValue);
            mRecyclerView.setAdapter(myAdapter);
            if ("0".equals(riskLevelValue)) {//不限
                switchOpen.setChecked(true);
            } else {
                switchOpen.setChecked(false);
            }
        }
        switchOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAdapter != null) {
                    if (switchOpen.isChecked()) {
                        myAdapter.updateState(mRecyclerView,"0");
                    } else {
                        myAdapter.updateState(mRecyclerView,"-1");//-1代表默认
                    }
                }
            }
        });
    }

    private void finishAction() {
        String result = "";
        if (myAdapter != null || mRecyclerView != null) {
            if (myAdapter.unlimited) {
                result = "0";
            } else {
                int itemCount = myAdapter.getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    View view = mRecyclerView.getChildAt(i);
                    AutoSettingAdapter.AutoSettingViewHolder viewHolder = (AutoSettingAdapter.AutoSettingViewHolder) mRecyclerView.getChildViewHolder(view);
                    if (viewHolder.checkBox.isChecked()) {
                        if (TextUtils.isEmpty(result)) {
                            result = viewHolder.code;
                        } else {
                            result = result + "," + viewHolder.code;
                        }
                    }
                }
            }
        }
        setResult(200, getIntent().putExtra(AutoInvestSettingActivity.ARGS_RISK_LEVEL_VALUE, result));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
