package cn.vpfinance.vpjr.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 3.6.2版本
 * 新版自动投标优惠券界面
 */
public class AutoInvestCouponActivityNew extends BaseActivity {
    @Bind(R.id.iv_use)
    ImageView ivUse;
    @Bind(R.id.ll_user_container)
    LinearLayout llUserContainer;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.iv_unuse)
    ImageView ivUnuse;
    @Bind(R.id.ll_unuse_container)
    LinearLayout llUnuseContainer;
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    public int resultType = 0;
    @Bind(R.id.btnSubmit)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_coupon_new);
        ButterKnife.bind(this);
        titleBar.setTitle("优惠券").setHeadBackVisible(View.VISIBLE);
        String isUse = getIntent().getStringExtra("isUse");
        if (TextUtils.isEmpty(isUse)) {
            ivUse.setVisibility(View.GONE);
            ivUnuse.setVisibility(View.VISIBLE);
        } else {
            ivUse.setVisibility(View.VISIBLE);
            ivUnuse.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.ll_user_container, R.id.ll_unuse_container, R.id.btnSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_user_container:
                ivUse.setVisibility(View.VISIBLE);
                ivUnuse.setVisibility(View.GONE);
                resultType = 1;
                break;
            case R.id.ll_unuse_container:
                ivUse.setVisibility(View.GONE);
                ivUnuse.setVisibility(View.VISIBLE);
                resultType = 2;
                break;
            case R.id.btnSubmit:
                Intent intent = new Intent();
                intent.putExtra("isUse", resultType);
                setResult(AutoInvestSettingActivity.REQUEST_CODE_VOUCHER,intent);
                finish();
                break;
        }
    }
}
