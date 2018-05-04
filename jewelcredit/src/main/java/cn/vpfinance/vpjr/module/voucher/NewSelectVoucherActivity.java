package cn.vpfinance.vpjr.module.voucher;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.voucher.fragment.NewSelectAddRateFragment;
import cn.vpfinance.vpjr.module.voucher.fragment.NewSelectVoucherFragment;
import cn.vpfinance.vpjr.model.AddRateInfo;
import cn.vpfinance.vpjr.model.CalcMoneyBean;
import cn.vpfinance.vpjr.model.VoucherEvent;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.util.FormatUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by 张清田 on 2015/10/29.
 */
public class NewSelectVoucherActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.voucher_btn)
    RadioButton     mVoucherBtn;
    @Bind(R.id.addRate_btn)
    RadioButton     mAddRateBtn;
    @Bind(R.id.radiogroup)
    RadioGroup      mRadiogroup;
    @Bind(R.id.container)
    FrameLayout     mContainer;
    @Bind(R.id.calcMoney)
    TextView        mCalcMoney;
    @Bind(R.id.btnOK)
    Button          mBtnOK;
    @Bind(R.id.selectConfirm)
    LinearLayout    mSelectConfirm;


    private String       mPid;
    private double       money;
    private VoucherEvent mVoucherEvent;
    private String       mType;
    private AddRateInfo mAddRateInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_select_voucher);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        if (intent != null) {
            //            mPid = intent.getStringExtra(ProductInvestActivity.PRODUCT_ID);
            //            money = intent.getDoubleExtra(ProductInvestActivity.NAME_MONEY, 0);
            Bundle bundle = intent.getExtras();
            mPid = bundle.getString(ProductInvestActivity.PRODUCT_ID);
            money = bundle.getDouble(ProductInvestActivity.NAME_MONEY, 0);
            mVoucherEvent = bundle.getParcelable(ProductInvestActivity.VOUCHEREVENT);
            mAddRateInfo = bundle.getParcelable(ProductInvestActivity.ADDRATEEVENT);
        }

        init();
    }

    private void init() {
        mTitleBar.setTitle("选择优惠券")
                .setHeadBackVisible(View.VISIBLE)
                .setImageButtonRight(R.drawable.ic_mine_top_info, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoWeb("/AppContent/voucher", "代金券");
                    }
                });

        mRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectFragment(checkedId);
            }
        });

        if (mVoucherEvent == null && mAddRateInfo != null) {
            mAddRateBtn.setChecked(true);
        } else {
            mVoucherBtn.setChecked(true);
        }

        mBtnOK.setOnClickListener(this);
    }

    private void selectFragment(int checkedId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        NewSelectVoucherFragment newSelectVoucherFragment = (NewSelectVoucherFragment) fm.findFragmentByTag("voucher_btn");
        NewSelectAddRateFragment newSelectAddRateFragment = (NewSelectAddRateFragment) fm.findFragmentByTag("addRate_btn");
        if (newSelectVoucherFragment != null)
            ft.hide(newSelectVoucherFragment);
        if (newSelectAddRateFragment != null)
            ft.hide(newSelectAddRateFragment);
        switch (checkedId) {
            case R.id.voucher_btn:
                if (newSelectVoucherFragment == null)
                    ft.add(R.id.container, NewSelectVoucherFragment.newInstance(mPid, money, mVoucherEvent), "voucher_btn");
                else
                    ft.show(newSelectVoucherFragment);
                break;
            case R.id.addRate_btn:
                if (newSelectAddRateFragment == null)
                    ft.add(R.id.container, NewSelectAddRateFragment.newInstance(mPid, money, mAddRateInfo), "addRate_btn");
                else
                    ft.show(newSelectAddRateFragment);
                break;
        }
        ft.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        postEvent();
    }

    public void onEventMainThread(CalcMoneyBean event) {
        if(event == null || isFinishing()) return;
        mType = event.getType();
        if ("1".equals(mType)) {
            mCalcMoney.setText("预计抵扣￥" + FormatUtils.formatDown(event.getCalcedMoney()));
            Drawable drawable= getResources().getDrawable(R.drawable.voucher);
            drawable.setBounds(0, 0, Utils.sp2px(this, 14), Utils.sp2px(this, 14));
            mCalcMoney.setCompoundDrawablePadding(Utils.dip2px(this, 10));
            mCalcMoney.setCompoundDrawables(drawable, null, null, null);
        }else if ("2".equals(mType)) {
            mCalcMoney.setText("预计加息" + FormatUtils.formatDown(event.getAddRateMoey()) + "元");
            Drawable drawable= getResources().getDrawable(R.drawable.interest);
            drawable.setBounds(0, 0, Utils.dip2px(this, 14), Utils.sp2px(this, 14));
            mCalcMoney.setCompoundDrawablePadding(Utils.dip2px(this, 10));
            mCalcMoney.setCompoundDrawables(drawable, null, null, null);
        }

    }

    @Override
    public void onClick(View v) {
        postEvent();

    }

    private void postEvent() {
        FragmentManager fm = getSupportFragmentManager();
        NewSelectVoucherFragment newSelectVoucherFragment = (NewSelectVoucherFragment) fm.findFragmentByTag("voucher_btn");
        NewSelectAddRateFragment newSelectAddRateFragment = (NewSelectAddRateFragment) fm.findFragmentByTag("addRate_btn");


        if (newSelectVoucherFragment != null && newSelectVoucherFragment.isPost()) {
            cleanData("2");
            newSelectVoucherFragment.postEvent();
        } else if (newSelectAddRateFragment != null && newSelectAddRateFragment.isPost()) {
            cleanData("1");
            newSelectAddRateFragment.postEvent();
        } else {
            if (newSelectVoucherFragment != null) {
                newSelectVoucherFragment.postEvent();
            }else if (newSelectAddRateFragment != null) {
                newSelectAddRateFragment.postEvent();
            }
        }
    }

    public void cleanData(String type) {
        FragmentManager fm = getSupportFragmentManager();
        NewSelectVoucherFragment newSelectVoucherFragment = (NewSelectVoucherFragment) fm.findFragmentByTag("voucher_btn");
        NewSelectAddRateFragment newSelectAddRateFragment = (NewSelectAddRateFragment) fm.findFragmentByTag("addRate_btn");

        if ("1".equals(type) && newSelectVoucherFragment!= null) {
            newSelectVoucherFragment.cleanData();
        }else if ("2".equals(type) && newSelectAddRateFragment != null) {
            newSelectAddRateFragment.cleanData();
        }
    }

    public boolean alertDialog(String type) {
        FragmentManager fm = getSupportFragmentManager();
        NewSelectVoucherFragment newSelectVoucherFragment = (NewSelectVoucherFragment) fm.findFragmentByTag("voucher_btn");
        NewSelectAddRateFragment newSelectAddRateFragment = (NewSelectAddRateFragment) fm.findFragmentByTag("addRate_btn");

        if ("1".equals(type) && newSelectVoucherFragment!= null) {
            return newSelectVoucherFragment.isPost();
        }else if ("2".equals(type) && newSelectAddRateFragment != null) {
            return newSelectAddRateFragment.isPost();
        }

        return false;
    }
}
