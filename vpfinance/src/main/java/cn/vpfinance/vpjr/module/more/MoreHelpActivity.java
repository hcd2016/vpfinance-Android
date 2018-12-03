package cn.vpfinance.vpjr.module.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 帮助中心--更多
 */
public class MoreHelpActivity extends BaseActivity {
    @Bind(R.id.tv_btn_faq)
    TextView tvBtnFaq;
//    @Bind(R.id.tv_btn_feedback)
//    TextView tvBtnFeedback;
    @Bind(R.id.ll_btn_service_num)
    LinearLayout llBtnServiceNum;
    @Bind(R.id.tv_phone_num1)
    TextView tvPhoneNum1;
    @Bind(R.id.tv_wechat1)
    TextView tvWechat1;
    @Bind(R.id.tv_phone_num2)
    TextView tvPhoneNum2;
    @Bind(R.id.tv_wechat2)
    TextView tvWechat2;
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_help);
        ButterKnife.bind(this);
        titleBar.setTitle("帮助中心").setHeadBackVisible(View.VISIBLE);
    }

    @OnClick({R.id.tv_btn_faq,R.id.ll_btn_service_num, R.id.tv_phone_num1, R.id.tv_wechat1, R.id.tv_phone_num2, R.id.tv_wechat2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_btn_faq://常见问题
                gotoWeb("/AppContent/commonproblem", "常见问题");
                break;
//            case R.id.tv_btn_feedback://意见反馈
//                gotoActivity(FeekBackActivity.class);
//                break;
            case R.id.ll_btn_service_num://客服热线
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:0755-86627551")));
                break;
            case R.id.tv_phone_num1:
                Utils.copy(tvPhoneNum1.getText().toString().trim(), this);
                Utils.Toast("复制成功!");
                break;
            case R.id.tv_wechat1:
                Utils.copy(tvWechat1.getText().toString().trim(), this);
                Utils.Toast("复制成功!");
                break;
            case R.id.tv_phone_num2:
                Utils.copy(tvPhoneNum2.getText().toString().trim(), this);
                Utils.Toast("复制成功!");
                break;
            case R.id.tv_wechat2:
                Utils.copy(tvWechat2.getText().toString().trim(), this);
                Utils.Toast("复制成功!");
                break;
        }
    }
}
