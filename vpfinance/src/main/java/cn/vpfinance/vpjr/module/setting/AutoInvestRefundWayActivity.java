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
import com.jewelcredit.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.adapter.AutoSettingAdapter;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.AutoInvestSettingBean;

/**
 * 自动投标设置 - 还款方式
 * Created by zzlz13 on 2017/7/31.
 */

public class AutoInvestRefundWayActivity extends BaseActivity {


    @Bind(R.id.title_bar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.switchOpen)
    ToggleButton switchOpen;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private AutoSettingAdapter myAdapter;
    private String refundWayValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_refund_way);
        ButterKnife.bind(this);
        mTitleBar.reset().setTitle("还款方式").setHeadBackVisible(View.VISIBLE);

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
            refundWayValue = intent.getStringExtra(AutoInvestSettingActivity.ARGS_REFUND_TYPE_VALUE);
            ArrayList<AutoInvestSettingBean.OptionsBean.RefundWayBean> refundWayList = (ArrayList<AutoInvestSettingBean.OptionsBean.RefundWayBean>) intent.getSerializableExtra(AutoInvestSettingActivity.ARGS_REFUND_TYPE);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            myAdapter = new AutoSettingAdapter<AutoInvestSettingBean.OptionsBean.RefundWayBean>(AutoInvestRefundWayActivity.this,refundWayList, refundWayValue);
            mRecyclerView.setAdapter(myAdapter);
            if ("0".equals(refundWayValue)) {//不限
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

    private void finishAction(){
        String result = "";
        if (myAdapter != null || mRecyclerView != null) {
            if (myAdapter.unlimited) {
                result = "0";
            } else {
                Iterator<String> iterator = myAdapter.codes.iterator();
                while (iterator.hasNext()) {
                    result = result + "," + iterator.next();
                }
            }
        }
        if (TextUtils.isEmpty(result)) {
            Utils.Toast(Constant.chooseAtLeastOne);
            return;
        }
        setResult(200, getIntent().putExtra(AutoInvestSettingActivity.ARGS_REFUND_TYPE_VALUE, result));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
