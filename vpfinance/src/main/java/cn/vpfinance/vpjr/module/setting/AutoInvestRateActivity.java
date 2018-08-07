package cn.vpfinance.vpjr.module.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 自动投标设置 - 预期年化收益
 * Created by zzlz13 on 2017/7/31.
 */

public class AutoInvestRateActivity extends BaseActivity {

    @Bind(R.id.title_bar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.switchOpen)
    Switch switchOpen;
    @Bind(R.id.tv_min_rate)
    TextView tvMinRate;
    @Bind(R.id.click_min_rate)
    LinearLayout clickMinRate;
    @Bind(R.id.tv_max_rate)
    TextView tvMaxRate;
    @Bind(R.id.click_max_rate)
    LinearLayout clickMaxRate;
    private String[] rateScope = new String[]{"1%", "2%", "3%", "4%", "5%", "6%", "7%", "8%", "9%", "10%", "11%", "12%"};
    private int start;
    private int end;
    private int black;
    private int gray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_rate);
        ButterKnife.bind(this);

        mTitleBar.reset().setTitle("预期年化收益").setHeadBackVisible(View.VISIBLE);

        black = getResources().getColor(R.color.text_1c1c1c);
        gray = getResources().getColor(R.color.text_cccccc);

        Intent intent = getIntent();
        if (intent != null) {
//            data = ((HashMap<String, Integer>) intent.getSerializableExtra(AutoInvestSettingActivity.ARGS_RATE));
            start = intent.getIntExtra(AutoInvestSettingActivity.ARGS_RATE_BEGIN_VALUE,1);
            end = intent.getIntExtra(AutoInvestSettingActivity.ARGS_RATE_END_VALUE,12);
            if (start == 1 && end == 12){//不限
                switchOpen.setChecked(true);
                tvMinRate.setTextColor(gray);
                tvMaxRate.setTextColor(gray);
                switchOpen(true);
            }else{
                switchOpen.setChecked(false);
                tvMinRate.setTextColor(black);
                tvMaxRate.setTextColor(black);
                clickMinRate.setEnabled(true);
                clickMaxRate.setEnabled(true);
                tvMinRate.setText(start + "%");
                tvMaxRate.setText(end + "%");
            }
        }

        switchOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchOpen(switchOpen.isChecked());
            }
        });
    }

    private void switchOpen(boolean isChecked) {

        if (isChecked) {
            tvMinRate.setTextColor(gray);
            tvMaxRate.setTextColor(gray);

            clickMinRate.setEnabled(false);
            clickMaxRate.setEnabled(false);
            //默认预期年化收益1-12%
            tvMinRate.setText(1 + "%");
            tvMaxRate.setText(12 + "%");
            start = 1;
            end = 12;
        } else {
            start = 1;
            end = 12;
            tvMinRate.setTextColor(black);
            tvMaxRate.setTextColor(black);
            clickMinRate.setEnabled(true);
            clickMaxRate.setEnabled(true);
            tvMinRate.setText(start + "%");
            tvMaxRate.setText(end + "%");
        }
    }


    @OnClick({R.id.click_min_rate, R.id.click_max_rate,R.id.btnSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.click_min_rate:
                if (start == 0) return;
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(rateScope, start - 1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tvMinRate.setText(rateScope[which]);
                                        start = which + 1;
                                        dialog.cancel();  //用户选择后，关闭对话框
                                    }
                                })
                        .create()
                        .show();
                break;
            case R.id.click_max_rate:
                if (end == 0) return;
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(rateScope, end - 1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tvMaxRate.setText(rateScope[which]);
                                        end = which + 1;
                                        dialog.cancel();  //用户选择后，关闭对话框
                                    }
                                })
                        .create()
                        .show();
                break;
            case R.id.btnSubmit:
                int temp;
                if (start > end){
                    temp = end;
                    end = start;
                    start = temp;
                }
                Intent intent = getIntent();
                intent.putExtra(AutoInvestSettingActivity.ARGS_RATE_BEGIN_VALUE,start);
                intent.putExtra(AutoInvestSettingActivity.ARGS_RATE_END_VALUE,end);
                setResult(200,intent);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
