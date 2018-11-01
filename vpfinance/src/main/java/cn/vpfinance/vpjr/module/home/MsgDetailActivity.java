package cn.vpfinance.vpjr.module.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

public class MsgDetailActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_desc)
    TextView tvDesc;
    @Bind(R.id.btn_check)
    Button btnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("消息详情").setHeadBackVisible(View.VISIBLE);
    }

    @OnClick({R.id.btn_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_check:
                break;
        }
    }
}
