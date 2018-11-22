package cn.vpfinance.vpjr.module.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.IsGetWelfareBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.common.RegisterActivity;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialogFragment;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.module.user.OpenBankHintActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.StatusBarCompat1;
import cn.vpfinance.vpjr.view.HideTitleScrollView;

/**
 * 新手福利
 */
public class NewWelfareActivity extends BaseActivity {

    private ActionBarLayout titleBar;
    private HideTitleScrollView hideTitleScrollView;
    private Button btnToRegister;
    private Button btnToBindBank;
    private Button btnToInvest;
    private RelativeLayout titleBar2;
    private boolean logined;
    private HttpService mHttpService;
    private TextView tvRegister;
    private TextView tvBindBank;
    private TextView tvInvest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_welfare);

        mHttpService = new HttpService(this, this);

        logined = AppState.instance().logined();

        titleBar = ((ActionBarLayout) findViewById(R.id.titleBar));
        titleBar.setHeadBackVisible(View.VISIBLE).setTitle("新手大礼包");
        titleBar2 = (RelativeLayout) findViewById(R.id.titleBar2);
        tvRegister = ((TextView) findViewById(R.id.tvRegister));
        tvBindBank = ((TextView) findViewById(R.id.tvBindBank));
        tvInvest = ((TextView) findViewById(R.id.tvInvest));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) titleBar2.getLayoutParams();
            int top = StatusBarCompat1.getStatusBarHeight(this);
            layoutParams.setMargins(0, top, 0, 0);
            titleBar2.setLayoutParams(layoutParams);
        }

        ViewHelper.setAlpha(titleBar, 0);

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

        btnToRegister = ((Button) findViewById(R.id.btnToRegister));
        btnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(RegisterActivity.class);
            }
        });

        btnToBindBank = ((Button) findViewById(R.id.btnToBindBank));
        btnToBindBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = DBUtils.getUser(NewWelfareActivity.this);
                if (logined && user != null) {
                    if (!TextUtils.isEmpty(user.getRealName())) {
                        RealnameAuthActivity.goThis(NewWelfareActivity.this);
                        Utils.Toast("请先去实名认证");
                    } else {
                        BindBankHintActivity.goThis(NewWelfareActivity.this, user.getUserId().toString());
                    }
                } else {
//                    new AlertDialog.Builder(NewWelfareActivity.this)
//                            .setMessage("请先去登录")
//                            .setPositiveButton("登录", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    gotoActivity(LoginActivity.class);
//                                }
//                            })
//                            .setNegativeButton("取消",null)
//                            .show();
                    new CommonTipsDialogFragment.Buidler()
                            .setContent("请先去登录")
                            .setBtnRight("登录")
                            .setOnRightClickListener(new CommonTipsDialogFragment.OnRightClickListner() {
                                @Override
                                public void rightClick() {
                                    gotoActivity(LoginActivity.class);
                                }
                            })
                            .setBtnLeft("取消")
                            .createAndShow(NewWelfareActivity.this);
                }
//
            }
        });
        btnToInvest = ((Button) findViewById(R.id.btnToInvest));
        btnToInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewWelfareActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.SWITCH_TAB_NUM, 1);
                gotoActivity(intent);
            }
        });
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (reqId == ServiceCmd.CmdId.CMD_IS_GET_WELFARE.ordinal()) {
            IsGetWelfareBean isGetWelfareBean = new Gson().fromJson(json.toString(), IsGetWelfareBean.class);
            tvRegister.setVisibility(isGetWelfareBean.register == 1 ? View.VISIBLE : View.INVISIBLE);
            tvBindBank.setVisibility(isGetWelfareBean.addCard == 1 ? View.VISIBLE : View.INVISIBLE);
            tvInvest.setVisibility(isGetWelfareBean.firstInvest == 1 ? View.VISIBLE : View.INVISIBLE);

            btnToRegister.setEnabled(isGetWelfareBean.register == 0);
            btnToBindBank.setEnabled(isGetWelfareBean.addCard == 0);
            btnToInvest.setEnabled(isGetWelfareBean.firstInvest == 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppState.instance().logined()) {

            User user = DBUtils.getUser(this);
            if (user != null) {
                mHttpService.getIsWelfare(user.getUserId().toString());
            }
        } else {
            tvRegister.setVisibility(View.INVISIBLE);
            tvBindBank.setVisibility(View.INVISIBLE);
            tvInvest.setVisibility(View.INVISIBLE);
            btnToRegister.setEnabled(true);
            btnToBindBank.setEnabled(true);
            btnToInvest.setEnabled(true);
        }
    }
}
