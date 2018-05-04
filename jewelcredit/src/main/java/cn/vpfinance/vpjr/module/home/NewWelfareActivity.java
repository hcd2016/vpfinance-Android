package cn.vpfinance.vpjr.module.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.nineoldandroids.view.ViewHelper;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.common.RegisterActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.StatusBarCompat1;
import cn.vpfinance.vpjr.view.HideTitleScrollView;

/**
 * 新手福利
 */
public class NewWelfareActivity extends BaseActivity {

    private ActionBarLayout titleBar;
    private HideTitleScrollView hideTitleScrollView;
    private Button clickToRegister;
    private Button clickToProve;
    private Button clickToInvest;
    private ImageView ivRegister;
    private ImageView ivRealName;
    private RelativeLayout titleBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_welfare);
        ivRegister = ((ImageView) findViewById(R.id.ivRegister));
        ivRealName = ((ImageView) findViewById(R.id.ivRealName));

        titleBar = ((ActionBarLayout) findViewById(R.id.titleBar));
        titleBar.setHeadBackVisible(View.VISIBLE).setTitle("新手大礼包");
        titleBar2 = (RelativeLayout)findViewById(R.id.titleBar2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) titleBar2.getLayoutParams();
            int top = StatusBarCompat1.getStatusBarHeight(this);
            layoutParams.setMargins(0,top,0,0);
            titleBar2.setLayoutParams(layoutParams);
        }

        ViewHelper.setAlpha(titleBar, 0);

        hideTitleScrollView = ((HideTitleScrollView) findViewById(R.id.scrollView));
        hideTitleScrollView.setScrollViewListener(new HideTitleScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(HideTitleScrollView scrollView, int x, int y, int oldx, int oldy) {
                titleBar.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                int measuredHeight = titleBar.getMeasuredHeight();
                if (hideTitleScrollView.getScrollY() < measuredHeight * 3 ){
                    ViewHelper.setAlpha(titleBar, (float) hideTitleScrollView.getScrollY() / (float) (measuredHeight * 3));
                }else{
                    ViewHelper.setAlpha(titleBar, 1);
                }
            }
        });

        clickToRegister = ((Button) findViewById(R.id.clickToRegister));
        clickToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(RegisterActivity.class);
            }
        });

        clickToProve = ((Button) findViewById(R.id.clickToProve));
        clickToProve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppState.instance().logined()) {
                    gotoActivity(RealnameAuthActivity.class);
                } else {
                    AlertDialogUtils.showAlertDialog(NewWelfareActivity.this, "您未登录", "取消", "前去登录", LoginActivity.class);
                }
            }
        });
        clickToInvest = ((Button) findViewById(R.id.clickToInvest));
        clickToInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewWelfareActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.SWITCH_TAB_NUM,1);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppState.instance().logined()){
            ivRegister.setBackgroundResource(R.drawable.bg_new_welfare_registed);
            clickToRegister.setVisibility(View.GONE);

            User user = DBUtils.getUser(this);
            if (user != null) {
                if (!TextUtils.isEmpty(user.getRealName())){
                    ivRealName.setBackgroundResource(R.drawable.bg_new_welfare_rename);
                    clickToProve.setVisibility(View.GONE);
                }
            }else{
                ivRealName.setBackgroundResource(R.drawable.bg_new_welfare3);
                clickToProve.setVisibility(View.VISIBLE);
            }

        }else{
            ivRegister.setBackgroundResource(R.drawable.bg_new_welfare2);
            clickToRegister.setVisibility(View.VISIBLE);
        }

    }
}
