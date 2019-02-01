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
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.InvestTopBean;

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
    private HttpService httpService;

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
        httpService = new HttpService(this,this);
        httpService.getInvestTop(1,"1");
    }

    private void initTab() {
        InvestTopNewAdapter investTopNewViewPager = new InvestTopNewAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(tabs.size());
        viewPager.setAdapter(investTopNewViewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (reqId == ServiceCmd.CmdId.CMD_Invest_Top.ordinal()) {
            InvestTopBean investTopBean = httpService.onGetInvestTop(json);
            initHeaderView(investTopBean);
        }
    }

    private void initHeaderView(InvestTopBean investTopBean) {
        if(investTopBean == null) return;
        if(investTopBean.currentRank != null) {
            tvTotalList.setText(investTopBean.currentRank);
        }
        if(investTopBean.monthCurrentRank != null) {
            tvMonthList.setText(investTopBean.monthCurrentRank);
        }
        if(investTopBean.weekCurrentRank != null) {
            tvWeekList.setText(investTopBean.weekCurrentRank);
        }
        if(investTopBean.number != null) {
            tvBeatDesc.setText("击败了"+investTopBean.number+"的出借达人");
        }
    }

    public class InvestTopNewAdapter extends FragmentPagerAdapter {

        public InvestTopNewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return InvestTopListFragment.newInstance(position);
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
