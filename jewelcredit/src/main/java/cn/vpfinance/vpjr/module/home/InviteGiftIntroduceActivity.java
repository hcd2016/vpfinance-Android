package cn.vpfinance.vpjr.module.home;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.nineoldandroids.view.ViewHelper;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.user.personal.InviteGiftActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.StatusBarCompat1;
import cn.vpfinance.vpjr.view.HideTitleScrollView;

/**
 * 邀请双重礼
 */
public class InviteGiftIntroduceActivity extends BaseActivity {

    private ActionBarLayout titleBar;
    private HideTitleScrollView hideTitleScrollView;
    private RelativeLayout titleBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_gift_introduce);

        findViewById(R.id.clickToInviteGiftH5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWeb("AppContent/content?topicId=75","邀请双重礼");
            }
        });
        titleBar = ((ActionBarLayout) findViewById(R.id.titleBar));
        titleBar.setHeadBackVisible(View.VISIBLE).setTitle("邀请双重礼");

        titleBar2 = (RelativeLayout)findViewById(R.id.titleBar2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) titleBar2.getLayoutParams();
            int top = StatusBarCompat1.getStatusBarHeight(this);
            layoutParams.setMargins(0,top,0,0);
            titleBar2.setLayoutParams(layoutParams);
        }

        ViewHelper.setAlpha(titleBar, 0);

        findViewById(R.id.clickInviteGift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppState.instance().logined()) {
                    InviteGiftActivity.goThis(InviteGiftIntroduceActivity.this);
                } else {
                    AlertDialogUtils.showAlertDialog(InviteGiftIntroduceActivity.this, "您未登录", "取消", "前去登录", LoginActivity.class);
                }
            }
        });

        hideTitleScrollView = ((HideTitleScrollView) findViewById(R.id.scrollView));
        hideTitleScrollView.setScrollViewListener(new HideTitleScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(HideTitleScrollView scrollView, int x, int y, int oldx, int oldy) {
                titleBar.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                int measuredHeight = titleBar.getMeasuredHeight();
                if (hideTitleScrollView.getScrollY() < measuredHeight * 3) {
                    ViewHelper.setAlpha(titleBar, (float) hideTitleScrollView.getScrollY() / (float) (measuredHeight * 3));
                } else {
                    ViewHelper.setAlpha(titleBar, 1);
                }

            }
        });
    }
}
