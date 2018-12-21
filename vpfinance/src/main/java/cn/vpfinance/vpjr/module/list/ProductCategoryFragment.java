package cn.vpfinance.vpjr.module.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.LoanSignTypeBean;
import cn.vpfinance.vpjr.model.RefreshTab;
import cn.vpfinance.vpjr.module.home.NewSearchActivity;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.Logger;
import de.greenrobot.event.EventBus;

/**
 * 产品列表
 */
public class ProductCategoryFragment extends BaseFragment {


    private ViewPager mViewPager;
    private PagerSlidingTabStrip mTabs;
    private HttpService mHttpService;
    //    private boolean isShowDeposit = false;
    private MyAdapter mTabsAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_category, null);

        ((ActionBarLayout) view.findViewById(R.id.titleBar)).reset().setTitle("产品列表").setImageButtonLeft(R.drawable.ic_mine_top_info, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWeb("/AppContent/productdescription", "名词解释");
            }
        }).setImageButtonRight(R.drawable.ic_menu_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewSearchActivity.class);
                gotoActivity(intent);
            }
        });

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabs = ((PagerSlidingTabStrip) view.findViewById(R.id.tabs));
        mTabs.setIndicatorColor(0xFFFF3035);
        mViewPager.setPageMargin(Utils.dip2px(getActivity(), 4));
        mViewPager.setOffscreenPageLimit(5);

        mHttpService = new HttpService(mContext, this);
//        mHttpService.getIsShowDeposit();
        mHttpService.getLoanSignType();
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        /*if (reqId == ServiceCmd.CmdId.CMD_Deposit_Is_Show.ordinal()) {
            Integer result = json.optInt("isShowDcb");
            if (result == 1) {
                //不显示
                isShowDeposit = false;
            } else if (result == 2) {
                //显示
                isShowDeposit = true;
            }
            updateView(isShowDeposit);
        }*/
        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Type.ordinal()) {
            Logger.e("Json:" + json.toString());
//            String json2 = "{\"types\":[{\"name\":\"出借产品\",\"value\":\"1\"},{\"name\":\"转让专区\",\"value\":\"2\"}]}";
//            String json3 = "";
            LoanSignTypeBean typeBean = new Gson().fromJson(json.toString(), LoanSignTypeBean.class);
            updateView(typeBean);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ProductCategory");
    }

    private void updateView(LoanSignTypeBean typeBean) {
        if (typeBean == null || typeBean.types == null) return;

        mTabsAdapter = new MyAdapter(getChildFragmentManager(), typeBean.types);
        mViewPager.setOffscreenPageLimit(mTabsAdapter.getCount());
        mViewPager.setAdapter(mTabsAdapter);
        mTabs.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

        int currentListTabType = ((FinanceApplication) getActivity().getApplication()).currentListTabType;
        for (int i = 0; i < typeBean.types.size(); i++) {
            if (currentListTabType == typeBean.types.get(i).value) {
                mViewPager.setCurrentItem(i);
            }
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                ((FinanceApplication) getActivity().getApplication()).currentListTabType = mTabsAdapter.getTabValue(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    //产品列表是否加载成功
    boolean productListLoadSuccess = false;

    public void onEventMainThread(RefreshTab event) {
        if (event != null && isAdded() && event.tabType == RefreshTab.TAB_LIST) {//切换我要出借TAB时
            if (mHttpService != null) {
                if (!productListLoadSuccess) {
                    mHttpService.getLoanSignType();
                }
            }
        }
    }

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_PRODUCT_LIST_LOAD_SUCCECC)) {//产品列表加载成功
            productListLoadSuccess = true;
        }
    }

    class MyAdapter extends FragmentStatePagerAdapter {

        private List<LoanSignTypeBean.TypesBean> types = null;

        @Override
        public CharSequence getPageTitle(int position) {
            if (!TextUtils.isEmpty(types.get(position).name))
                return types.get(position).name;
            return "";
        }

        public MyAdapter(FragmentManager fm, List<LoanSignTypeBean.TypesBean> types) {
            super(fm);
            this.types = types;
        }

        public Integer getTabValue(int position) {
            return types.get(position) == null ? 0 : types.get(position).value;
        }

        @Override
        public Fragment getItem(int position) {
            Integer type = types.get(position).value;
            switch (type) {
                case Constant.TYPE_REGULAR://定期
                    return ProductListFragment.getInstance(Constant.TYPE_REGULAR);
                case Constant.TYPE_BANK://存管专区
                    return ProductListFragment.getInstance(Constant.TYPE_BANK);
                case Constant.TYPE_TRANSFER://债权转让
                    return ProductListFragment.getInstance(Constant.TYPE_TRANSFER);
                case Constant.TYPE_POOL://智存出借
                    return ProductDepositListFragment.getInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return types == null ? 0 : types.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            //super.setPrimaryItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            return fragment;
        }

    }

    public static ProductCategoryFragment newInstance() {
        return new ProductCategoryFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ProductCategory");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
