package cn.vpfinance.vpjr.module.user.fragment;

import android.animation.ArgbEvaluator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.test.mock.MockApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.jewelcredit.ui.widget.ActionBarLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.product.invest.DepositInvestActivity;
import cn.vpfinance.vpjr.module.setting.NewPersonalInfoActivity;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.module.setting.PersonalInfoActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * Created by zzlz13 on 2017/7/28.
 */

public class MineFragment extends BaseFragment {

    @Bind(R.id.tabs)
    SlidingTabLayout mTabLayout;
    @Bind(R.id.pager)
    ViewPager mViewPager;
    @Bind(R.id.tabBg)
    RelativeLayout tabBg;

    private int mineFragmentColor;
    private ActionBarLayout titleBar;
    private BaseFragment[] fragments = {new BankAccountFragment(), new OriginalAccountFragment()};

//    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_mine, container, false);

        titleBar = ((ActionBarLayout) view.findViewById(R.id.titleBar));
        titleBar.reset().setTitle("我的账户").setColor(mineFragmentColor).setActionRight("设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
            }
        });
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
//        if (activity instanceof MainActivity) {
//            mainActivity = (MainActivity) activity;
//        }

        MyAdapter mAdapter = new MyAdapter(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setViewPager(mViewPager);
        //初始化颜色
        if ((FinanceApplication.getAppContext()).saveMineTabSelected == 0){
            mViewPager.setCurrentItem(0);
//            mainActivity.mineFragmentColor = ContextCompat.getColor(getActivity(), R.color.account_bank_header);
            mineFragmentColor = ContextCompat.getColor(getActivity(), R.color.account_bank_header);
        }else{
            mViewPager.setCurrentItem(1);
//            mainActivity.mineFragmentColor = ContextCompat.getColor(getActivity(), R.color.account_original_header);
            mineFragmentColor = ContextCompat.getColor(getActivity(), R.color.account_original_header);
        }
//        tabBg.setBackgroundColor(mainActivity.mineFragmentColor);
//        mTabLayout.setTextSelectColor(mainActivity.mineFragmentColor);
        tabBg.setBackgroundColor(mineFragmentColor);
        mTabLayout.setTextSelectColor(mineFragmentColor);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //positionOffset当前页面偏移的百分比(0-1)
//                Logger.e("position: "+position+" , positionOffset: "+positionOffset+" , positionOffsetPixels: "+positionOffsetPixels);
//                if (mainActivity == null || mainActivity.titleBar == null || mainActivity.mLastRadioId != R.id.maintab_mine_radiobtn)
//                    return;

                MyAdapter adapter = (MyAdapter) mViewPager.getAdapter();
                BankAccountFragment bankAccountFragment = (BankAccountFragment) adapter.getItem(0);
                OriginalAccountFragment originalAccountFragment = (OriginalAccountFragment) adapter.getItem(1);

                changeColor(bankAccountFragment,originalAccountFragment,position,positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
//                    mainActivity.mineFragmentColor = ContextCompat.getColor(getActivity(), R.color.account_bank_header);
                    mineFragmentColor = ContextCompat.getColor(getActivity(), R.color.account_bank_header);
                } else if (position == 1) {
//                    mainActivity.mineFragmentColor = ContextCompat.getColor(getActivity(), R.color.account_original_header);
                    mineFragmentColor = ContextCompat.getColor(getActivity(), R.color.account_original_header);
                }
                (FinanceApplication.getAppContext()).saveMineTabSelected = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    private void changeColor(BankAccountFragment bankAccountFragment, OriginalAccountFragment originalAccountFragment,int position, float positionOffset) {
        if (originalAccountFragment == null || bankAccountFragment == null) return;

        int account_bank_header_color = ContextCompat.getColor(getActivity(), R.color.account_bank_header);
        int account_original_header_color = ContextCompat.getColor(getActivity(), R.color.account_original_header);
        ArgbEvaluator evaluator = new ArgbEvaluator();

        if (position % 2 == 0) {
//            mainActivity.titleBar.setColor(account_bank_header_color);
            titleBar.setColor(account_bank_header_color);
            int evaluate = (Integer) evaluator.evaluate(positionOffset,account_bank_header_color , account_original_header_color);
//            mainActivity.titleBar.setColor(evaluate);
            titleBar.setColor(evaluate);
            tabBg.setBackgroundColor(evaluate);

            if (bankAccountFragment.mHeaderNoOpen != null) {
                bankAccountFragment.mHeaderNoOpen.setBackgroundColor(evaluate);
            }
            if (originalAccountFragment.mHeader != null) {
                originalAccountFragment.mHeader.setBackgroundColor(evaluate);
            }
            if (bankAccountFragment.mHeader != null) {
                bankAccountFragment.mHeader.setBackgroundColor(evaluate);
            }
            if (bankAccountFragment.mHeaderNoOpenInside != null) {
                bankAccountFragment.mHeaderNoOpenInside.setBackgroundColor(evaluate);
            }
        } else {
//            mainActivity.titleBar.setColor(account_original_header_color);
            titleBar.setColor(account_original_header_color);
            int evaluate = (Integer) evaluator.evaluate(positionOffset,account_original_header_color , account_bank_header_color);
//            mainActivity.titleBar.setColor(evaluate);
            titleBar.setColor(evaluate);
            tabBg.setBackgroundColor(evaluate);
            if (bankAccountFragment.mHeader != null) {
                bankAccountFragment.mHeader.setBackgroundColor(evaluate);
            }
            if (bankAccountFragment.mHeaderNoOpen != null) {
                bankAccountFragment.mHeaderNoOpen.setBackgroundColor(evaluate);
            }
            if (originalAccountFragment.mHeader != null) {
                originalAccountFragment.mHeader.setBackgroundColor(evaluate);
            }
            if (bankAccountFragment.mHeaderNoOpenInside != null) {
                bankAccountFragment.mHeaderNoOpenInside.setBackgroundColor(evaluate);
            }
        }
        if (positionOffset == 1 || positionOffset == 0) {
            mTabLayout.setTextSelectColor(position == 1 ? account_original_header_color : account_bank_header_color);
        } else {
            mTabLayout.setTextSelectColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        private String[] mTitles = new String[]{"银行存管账户", "连连支付账户"};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return fragments[0];
            } else if (position == 1) {
                return fragments[1];
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            super.setPrimaryItem(container, position, object);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
