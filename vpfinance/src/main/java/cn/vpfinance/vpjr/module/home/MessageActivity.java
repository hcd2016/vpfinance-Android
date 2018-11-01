package cn.vpfinance.vpjr.module.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.home.fragment.MsgPaymentFragment;
import cn.vpfinance.vpjr.module.home.fragment.MsgPlatformFragment;

public class MessageActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    private List<String> tabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        tabList = new ArrayList<>();
        tabList.add("平台公告");
        tabList.add("还款公告");
        titleBar.setTitle("消息中心").setHeadBackVisible(View.VISIBLE);
        titleBar.setActionRight("全部已读", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
            }
        });
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        initTabs();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabColor(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(0);//默认选中第一个
    }

    //设置选中Tab颜色
    private void setTabColor(TabLayout.Tab tab) {
        for (int i = 0; i < tabList.size(); i++) {
            TextView tv_tab = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tv_tab);
            if(tab.getPosition() == i) {
                tv_tab.setTextColor(Utils.getColor(R.color.red_text2));
            }else {
                tv_tab.setTextColor(Utils.getColor(R.color.text_666666));
            }
        }
    }

    private void initTabs() {
        for (int i = 0; i < tabList.size(); i++) {
            View view = View.inflate(this, R.layout.message_tab, null);
            TextView tv_tab = view.findViewById(R.id.tv_tab);
            tv_tab.setText(tabList.get(i));
            tabLayout.getTabAt(i).setCustomView(view);
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {//平台公告
                return new MsgPlatformFragment();
            } else  {//还款公告
                return new MsgPaymentFragment();
            }
        }

        @Override
        public int getCount() {
            return tabList.size();
        }
    }
}
