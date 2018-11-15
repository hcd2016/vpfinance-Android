package cn.vpfinance.vpjr.module.user.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.model.EventBusCoupon;
import cn.vpfinance.vpjr.module.dialog.VoucherDialogFragment;
import cn.vpfinance.vpjr.module.user.fragment.CouponFragment;
import de.greenrobot.event.EventBus;

/**
 * 我的优惠券
 */
public class CouponActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.mActionBar)
    ActionBarLayout mActionBar;
    @Bind(R.id.mTab)
    SlidingTabLayout mTab;
    @Bind(R.id.mVp)
    ViewPager mVp;
    @Bind(R.id.vRootView)
    LinearLayout vRootView;

    private HttpService mHttpService;

    private int type = CouponFragment.TYPE_ALL;
    private PopupWindow menuWindow;
    private PopupWindow statusWindow;

    public static void goThis(Context context) {
        Intent intent = new Intent(context, CouponActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        ButterKnife.bind(this);

        mHttpService = new HttpService(this, this);

        mActionBar.reset().setTitle("我的优惠券")
                .setHeadBackVisible(View.VISIBLE)
                .setUpDown(ActionBarLayout.TYPE_DOWN)
                .setImageButtonRight(R.drawable.toolbar_menu, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popMenu();
                    }
                })
                .setTitleClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popTitle();
                    }
                });
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(CouponFragment.getInstance(type, CouponFragment.STATUS_UNUSED));
        fragments.add(CouponFragment.getInstance(type, CouponFragment.STATUS_USED));
        fragments.add(CouponFragment.getInstance(type, CouponFragment.STATUS_INVALID));

        mVp.setOffscreenPageLimit(3);
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager(), fragments);
        mVp.setAdapter(adapter);

        String[] titles = new String[]{"未使用", "已使用", "已过期"};
        mTab.setViewPager(mVp, titles);
    }

    public void setPageTitle(String[] titles) {
        mTab.setViewPager(mVp, titles);
    }

    private void popMenu() {
        View rightView = LayoutInflater.from(this).inflate(R.layout.layout_coupon_menu, null, false);
        menuWindow = new PopupWindow(rightView, Utils.dip2px(this, 154), Utils.dip2px(this, 148), true);
        menuWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        menuWindow.setOutsideTouchable(true);
        menuWindow.setTouchable(true);

        menuWindow.showAtLocation(mActionBar, Gravity.TOP | Gravity.RIGHT, 0, mActionBar.getMeasuredHeight());

        rightView.findViewById(R.id.tvAddCoupon).setOnClickListener(this);
        rightView.findViewById(R.id.tvGetCoupon).setOnClickListener(this);
        rightView.findViewById(R.id.tvRuleCoupon).setOnClickListener(this);
    }

    private void popTitle() {
        mActionBar.setUpDown(ActionBarLayout.TYPE_UP);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_coupon_title, null, false);
        final LinearLayout shadowContainer = (LinearLayout) view.findViewById(R.id.shadowContainer);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.mRadioGroup);

        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        shadowContainer.setMinimumHeight(heightPixels - mActionBar.getMeasuredHeight() -mTab.getMeasuredHeight()- radioGroup.getMeasuredHeight());
//        shadowContainer.setAlpha(0.3F);

        statusWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, heightPixels - mActionBar.getMeasuredHeight()-mTab.getMeasuredHeight(), true);
        statusWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        statusWindow.setOutsideTouchable(true);
        statusWindow.setTouchable(true);
        statusWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mActionBar.setUpDown(ActionBarLayout.TYPE_DOWN);
//                backgroundAlpha(1f);
            }
        });

        statusWindow.showAtLocation(mTab, Gravity.TOP, 0, mTab.getMeasuredHeight()+mActionBar.getMeasuredHeight());
        final RadioButton mRbAll = view.findViewById(R.id.mRbAll);
        final RadioButton mRbVoucher = view.findViewById(R.id.mRbVoucher);
        final RadioButton mRbPresell = view.findViewById(R.id.mRbPresell);
        //重置状态
        mRbAll.setTextColor(Utils.getColor(R.color.text_333333));
        mRbVoucher.setTextColor(Utils.getColor(R.color.text_333333));
        mRbPresell.setTextColor(Utils.getColor(R.color.text_333333));
        mRbAll.getPaint().setFakeBoldText(false);
        mRbVoucher.getPaint().setFakeBoldText(false);
        mRbPresell.getPaint().setFakeBoldText(false);
        switch (type) {
            case CouponFragment.TYPE_ALL:
                mRbAll.setChecked(true);
                mRbAll.setTextColor(Utils.getColor(R.color.red_text2));
                mRbAll.getPaint().setFakeBoldText(true);
                break;
            case CouponFragment.TYPE_VOUCHER:
                mRbVoucher.setTextColor(Utils.getColor(R.color.red_text2));
                mRbVoucher.getPaint().setFakeBoldText(true);
                mRbVoucher.setChecked(true);
                break;
            case CouponFragment.TYPE_PRESELL:
                mRbPresell.setTextColor(Utils.getColor(R.color.red_text2));
                mRbPresell.getPaint().setFakeBoldText(true);
                mRbPresell.setChecked(true);
                break;
        }
        shadowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusWindow.dismiss();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (statusWindow != null && statusWindow.isShowing()) {
                    statusWindow.dismiss();
                }
                switch (checkedId) {
                    case R.id.mRbAll:
                        mActionBar.setTitle("我的优惠券");
                        type = CouponFragment.TYPE_ALL;
                        break;
                    case R.id.mRbVoucher:
                        mActionBar.setTitle("代金券");
                        type = CouponFragment.TYPE_VOUCHER;
                        break;
                    case R.id.mRbPresell:
                        mActionBar.setTitle("预约券");
                        type = CouponFragment.TYPE_PRESELL;
                        break;
                }
                EventBus.getDefault().post(new EventBusCoupon(type));
            }
        });
//        backgroundAlpha(0.4f);
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_voucherExchange.ordinal()) {
            if (json == null) {
                return;
            }
            String msg = json.optString("msg");
//            String money = json.optString("money");
            if ("3".equals(msg)) {
                EventBus.getDefault().post(new EventBusCoupon(type));
                Toast.makeText(this, "恭喜您兑换成功", Toast.LENGTH_LONG).show();
            } else if ("1".equals(msg)) {
                Toast.makeText(this, "兑换码不正确", Toast.LENGTH_SHORT).show();
            } else if ("2".equals(msg)) {
                Toast.makeText(this, "兑换码已使用", Toast.LENGTH_SHORT).show();
            } else if ("4".equals(msg)){
                Toast.makeText(this, "兑换码已过期", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAddCoupon:
                if (menuWindow != null && menuWindow.isShowing()) {
                    menuWindow.dismiss();
                }
                VoucherDialogFragment voucherDialogFragment = new VoucherDialogFragment();
                voucherDialogFragment.setOnTextConfrimListener(new VoucherDialogFragment.onTextConfrimListener() {
                    @Override
                    public boolean onTextConfrim(String value) {
                        if (value != null) {
                            mHttpService.voucherExchange(value);
                        }
                        return true;
                    }
                });
                voucherDialogFragment.show(getSupportFragmentManager(), "VoucherDialog");
                break;
            case R.id.tvGetCoupon:
                if (menuWindow != null && menuWindow.isShowing()) {
                    menuWindow.dismiss();
                }
                InviteGiftActivity.goThis(this);
                break;
            case R.id.tvRuleCoupon:
                if (menuWindow != null && menuWindow.isShowing()) {
                    menuWindow.dismiss();
                }
                gotoWeb("/AppContent/voucher", "优惠券");
                break;
        }
    }

    class MyAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;

        public MyAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments == null ? null : fragments.get(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }
    }
}
