package cn.vpfinance.vpjr.module.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.tencent.bugly.crashreport.common.info.AppInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.home.fragment.MsgPaymentFragment;
import cn.vpfinance.vpjr.module.home.fragment.MsgPlatformFragment;
import cn.vpfinance.vpjr.module.user.asset.FundRecordsActivity;
import cn.vpfinance.vpjr.util.EventMsgReadModel;
import cn.vpfinance.vpjr.util.EventStringModel;
import de.greenrobot.event.EventBus;

public class MsgActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    private List<String> tabList;
    private HttpService httpService;

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
        httpService = new HttpService(this, this);
        if (!AppState.instance().logined()) {//未登录状态
            titleBar.setImageButtonRightVisible(View.GONE);
        } else {
            titleBar.setActionRight("全部已读", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    httpService.getAllRead();
                    EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_MSG_ALL_READ_CLICK));
                }
            });
        }
        EventBus.getDefault().register(this);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(tabList.size());
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

    //消息请求成功返回列表是否有已读未读消息
    public void onEventMainThread(EventMsgReadModel event) {
        if(event.type.equals("1") ) {
            if(event.isRead.equals("0")) {
                tabLayout.getTabAt(0).getCustomView().findViewById(R.id.iv_point).setVisibility(View.GONE);
            }else {
                tabLayout.getTabAt(0).getCustomView().findViewById(R.id.iv_point).setVisibility(View.VISIBLE);
            }
        }
        if(event.type.equals("2")) {
            if(event.isRead.equals("0")) {
                tabLayout.getTabAt(1).getCustomView().findViewById(R.id.iv_point).setVisibility(View.GONE);
            }else {
                tabLayout.getTabAt(1).getCustomView().findViewById(R.id.iv_point).setVisibility(View.VISIBLE);
            }
        }
    }

    //设置选中Tab颜色
    private void setTabColor(TabLayout.Tab tab) {
        for (int i = 0; i < tabList.size(); i++) {
            TextView tv_tab = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tv_tab);
            if (tab.getPosition() == i) {
                tv_tab.setTextColor(Utils.getColor(R.color.red_text2));
            } else {
                tv_tab.setTextColor(Utils.getColor(R.color.text_666666));
            }
        }
    }

    private void initTabs() {
        for (int i = 0; i < tabList.size(); i++) {
            View view = View.inflate(this, R.layout.message_tab, null);
            TextView tv_tab = view.findViewById(R.id.tv_tab);
            ImageView iv_point = view.findViewById(R.id.iv_point);
            tv_tab.setText(tabList.get(i));
            if (!AppState.instance().logined()) {//未登录状态
                iv_point.setVisibility(View.GONE);
            } else {
                iv_point.setVisibility(View.VISIBLE);
            }
            tabLayout.getTabAt(i).setCustomView(view);
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String type = "1";
            if (position == 0) {//平台公告
                type = "1";
            } else {//还款公告
                type="2";
            }
            return MsgPaymentFragment.newInstance(type);
        }

        @Override
        public int getCount() {
            return tabList.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
