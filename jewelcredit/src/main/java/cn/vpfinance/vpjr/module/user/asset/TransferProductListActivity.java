package cn.vpfinance.vpjr.module.user.asset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.jewelcredit.ui.widget.ActionBarLayout;

import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.user.transfer.TransferAllowProductListFragment;
import cn.vpfinance.vpjr.module.user.transfer.TransferedProductListFragment;
import cn.vpfinance.vpjr.module.user.transfer.TransferingProductListFragment;

/**
 * 债权转让列表
 */
public class TransferProductListActivity extends BaseActivity {

    private ViewPager mViewPager;
    private PagerSlidingTabStrip tabs;
    private TransferListAdapter transferListAdapter;
//    private ArrayList<Fragment> fragments;
    private int accountType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_product_list);

        Intent intent = getIntent();
        if (intent != null){
            accountType = intent.getIntExtra(Constant.AccountType,0);
        }
        initView();
    }

    protected void initView() {
        ActionBarLayout titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setTitle("我要转让").setHeadBackVisible(View.VISIBLE);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setIndicatorColor(0xFFFF3035);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);
        mViewPager.setOffscreenPageLimit(3);

//        if(fragments == null)
//            fragments = new ArrayList<>();
//        fragments.clear();
//        fragments.add(TransferAllowProductListFragment.newInstance(accountType));
//        fragments.add(TransferingProductListFragment.newInstance(accountType));
//        fragments.add(TransferedProductListFragment.newInstance(accountType));
        transferListAdapter = new TransferListAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(transferListAdapter);
        tabs.setViewPager(mViewPager);
    }

    class TransferListAdapter extends FragmentStatePagerAdapter {

        private final FragmentManager mFragmentManager;

        public TransferListAdapter(FragmentManager fm)
        {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            //1可转让 2转让中3已转让
            switch (position){
                case 0:
                    return TransferAllowProductListFragment.newInstance(accountType);
                case 1:
                    return TransferingProductListFragment.newInstance(accountType);
                case 2:
                    return TransferedProductListFragment.newInstance(accountType);
            }
            return TransferAllowProductListFragment.newInstance(accountType);
//            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = getResources().getStringArray(R.array.transferListType);
            return TextUtils.isEmpty(titles[position]) ? "" : titles[position];
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            //super.setPrimaryItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment)super.instantiateItem(container, position);
            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            return fragment;
        }
    }


}
