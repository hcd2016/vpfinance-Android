package cn.vpfinance.vpjr.module.user.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 */
public class MedalDetailActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout mTitlaBar;

    public static final String MEDAL_ID = "medal_id";
    public static final String MEDAL_STATUS = "medal_status";
    public static final String MEDAL_DESCRIPTION = "medal_description";
    public static final String MEDAL_NAME = "medal_name";
    public static final String MEDAL_CONDITION = "medal_condition";
    public static final String MEDAL_LOGO = "medal_logo";
    @Bind(R.id.logo)
    ImageView mLogo;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.condition)
    TextView mCondition;
    @Bind(R.id.status)
    TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal_detail);
//        StatusBarCompat1.translucentStatusBar(this);
        ButterKnife.bind(this);
        mTitlaBar.setTitle("勋章详情").setHeadBackVisible(View.VISIBLE).setTransparent();

        Intent intent = getIntent();
        if (intent != null) {
            String medalId = intent.getStringExtra(MEDAL_ID);
            boolean medalStatus = intent.getBooleanExtra(MEDAL_STATUS, false);
            String medalDescription = intent.getStringExtra(MEDAL_DESCRIPTION);
            String medalName = intent.getStringExtra(MEDAL_NAME);
            String medalCondition = intent.getStringExtra(MEDAL_CONDITION);
            String medalLogo = intent.getStringExtra(MEDAL_LOGO);

            mName.setText(medalName);
            mCondition.setText(medalCondition);
            ImageLoader.getInstance().displayImage(medalLogo+"3.png",mLogo);
            mStatus.setText(medalStatus ? "已获得" : "未达到条件");
        }
    }

}
