package cn.vpfinance.vpjr.module.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 风云榜4.0
 */
public class InvestTopNewActivity extends BaseActivity {
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.tv_total_list)
    TextView tvTotalList;
    @Bind(R.id.tv_week_list)
    TextView tvWeekList;
    @Bind(R.id.tv_month_list)
    TextView tvMonthList;
    @Bind(R.id.tv_beat_desc)
    TextView tvBeatDesc;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    private List<String> tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_top);
        ButterKnife.bind(this);
        initView();
        initTab();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("风云榜").setHeadBackVisible(View.VISIBLE);
        tabs = new ArrayList<>();
        tabs.add("总榜");
        tabs.add("周榜");
        tabs.add("月榜");
    }

    private void initTab() {
        InvestTopNewAdapter investTopNewViewPager = new InvestTopNewAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(tabs.size());
        viewPager.setAdapter(investTopNewViewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class InvestTopNewAdapter extends FragmentPagerAdapter {

        public InvestTopNewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            switch (position) {
//                case 0://总榜
//                    break;
//                case 1://周榜
//                    break;
//                case 2://月榜
//                    break;
//            }
            return new InvestTopListFragment();
        }

        @Override
        public int getCount() {
            return tabs.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position);
        }
    }
}
