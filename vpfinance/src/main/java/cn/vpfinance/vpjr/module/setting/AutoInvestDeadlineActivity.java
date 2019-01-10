package cn.vpfinance.vpjr.module.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.Utils;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 自动投标设置 - 借款期限
 * Created by zzlz13 on 2017/7/31.
 */

public class AutoInvestDeadlineActivity extends BaseActivity {

    @Bind(R.id.title_bar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.switchOpen)
    ToggleButton switchOpen;
    @Bind(R.id.tv_min_deadline)
    TextView tvMinDeadline;
    @Bind(R.id.click_min_deadline)
    LinearLayout clickMinDeadline;
    @Bind(R.id.tv_max_deadline)
    TextView tvMaxDeadline;
    @Bind(R.id.click_max_deadline)
    LinearLayout clickMaxDeadline;

    private String[] deadlineScope = new String[]{"1个月", "2个月", "3个月", "4个月", "5个月", "6个月", "7个月", "8个月", "9个月", "10个月", "11个月", "12个月", "13个月", "14个月", "15个月", "16个月", "17个月", "18个月", "19个月", "20个月", "21个月", "22个月", "23个月", "24个月"};
//    private HashMap<String, Integer> data;
    private Integer start;
    private Integer end;
    private int black;
    private int gray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_deadline);
        ButterKnife.bind(this);

        mTitleBar.reset().setTitle("借款期限").setHeadBackVisible(View.VISIBLE);

        black = getResources().getColor(R.color.text_1c1c1c);
        gray = getResources().getColor(R.color.text_cccccc);

        Intent intent = getIntent();
        if (intent != null) {
//            data = ((HashMap<String, Integer>) intent.getSerializableExtra(AutoInvestSettingActivity.ARGS_BORROW_TIME));
            start = intent.getIntExtra(AutoInvestSettingActivity.ARGS_BORROW_TIME_BEGIN_VALUE, 1);
            end = intent.getIntExtra(AutoInvestSettingActivity.ARGS_BORROW_TIME_END_VALUE, 12);
            if (start == 1 && end == 24){//不限
                switchOpen.setChecked(true);
                tvMinDeadline.setTextColor(gray);
                tvMaxDeadline.setTextColor(gray);
                switchOpen(true);
            }else{
                switchOpen.setChecked(false);
                tvMinDeadline.setTextColor(black);
                tvMaxDeadline.setTextColor(black);
                clickMinDeadline.setEnabled(true);
                clickMaxDeadline.setEnabled(true);

                tvMinDeadline.setText(start + "个月");
                tvMaxDeadline.setText(end + "个月");
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
            tvMinDeadline.setTextColor(gray);
            tvMaxDeadline.setTextColor(gray);
            clickMinDeadline.setEnabled(false);
            clickMaxDeadline.setEnabled(false);
            start = 1;
            end = 24;
            tvMinDeadline.setText(deadlineScope[0]);
            tvMaxDeadline.setText(deadlineScope[deadlineScope.length-1]);
        } else {
            start = 1;
            end = 24;
            tvMinDeadline.setTextColor(black);
            tvMaxDeadline.setTextColor(black);
            clickMinDeadline.setEnabled(true);
            clickMaxDeadline.setEnabled(true);
            tvMinDeadline.setText(deadlineScope[start - 1]);
            tvMaxDeadline.setText(deadlineScope[end - 1]);
        }

    }

    @OnClick({R.id.click_min_deadline, R.id.click_max_deadline,R.id.btnSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.click_min_deadline:
                if (start == 0)   return;
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(deadlineScope, start - 1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tvMinDeadline.setText(deadlineScope[which]);
                                        start = which + 1;
                                        dialog.cancel();  //用户选择后，关闭对话框
                                    }
                                })
                        .create()
                        .show();
                break;
            case R.id.click_max_deadline:
                if (end == 0)   return;
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(deadlineScope, end - 1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tvMaxDeadline.setText(deadlineScope[which]);
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
                intent.putExtra(AutoInvestSettingActivity.ARGS_BORROW_TIME_BEGIN_VALUE,start);
                intent.putExtra(AutoInvestSettingActivity.ARGS_BORROW_TIME_END_VALUE,end);
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
