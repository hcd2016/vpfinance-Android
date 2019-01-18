package cn.vpfinance.vpjr.module.list;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.Constant;
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


    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    //    @Bind(R.id.coordinatorLayout)
//    CoordinatorLayout coordinatorLayout;
//    @Bind(R.id.appBarLayout)
//    AppBarLayout appBarLayout;
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.v_red_bg)
    View v_red_bg;
    @Bind(R.id.ll_title_contianer)
    LinearLayout llTitleContianer;
    private ViewPager mViewPager;
    //    private PagerSlidingTabStripNew mTabs;
    private HttpService mHttpService;
    //    private boolean isShowDeposit = false;
    private MyAdapter mTabsAdapter;
    private List<Fragment> fragmentsList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_category, null);
        ButterKnife.bind(this, view);

        ((ActionBarLayout) view.findViewById(R.id.titleBar)).reset().setTitle("产品列表").setFakeStatusBar(false).setImageButtonLeft(R.mipmap.icon_info, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWeb("/AppContent/productdescription", "名词解释");
            }
        }).setImageButtonRight(R.mipmap.icon_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewSearchActivity.class);
                gotoActivity(intent);
            }
        });
        initFragment();

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setPageMargin(Utils.dip2px(getActivity(), 4));
        mViewPager.setOffscreenPageLimit(5);

        mHttpService = new HttpService(mContext, this);
//        mHttpService.getIsShowDeposit();
        mHttpService.getLoanSignType();

        return view;
    }


    private boolean isShow = true;
    private boolean isAnimationRunning = false;
    private long preTime = System.currentTimeMillis();

    //标题动画效果
    private void initFragment() {
//        int height = titleBar.getHeight();
        int height = Utils.dip2px(getActivity(),48);
        final Animation mShowAction =
//                new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                -height, Animation.RELATIVE_TO_SELF, 0.0f);
        new TranslateAnimation(0,0,-height,0);


        final Animation mHiddenAction =
                new TranslateAnimation(0,0,0,-height);
//                new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                -height);
        final Animation mShowAction1 =
//                new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                -height, Animation.RELATIVE_TO_SELF, 0.0f);
                new TranslateAnimation(0,0,-height,0);


        final Animation mHiddenAction1 =
//                new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                -height);
                new TranslateAnimation(0,0,0,-height);

        mShowAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimationRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                isAnimationRunning = true;
            }
        });
        mShowAction.setDuration(150);
        mHiddenAction.setDuration(150);
        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimationRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
//                                isAnimationRunning = true;
            }
        });
        mHiddenAction1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimationRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                isAnimationRunning = true;
            }
        });
        mShowAction1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimationRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
//                                isAnimationRunning = true;
            }
        });
        mShowAction1.setDuration(150);
        mHiddenAction1.setDuration(150);
        fragmentsList = new ArrayList<>();
        fragmentsList.add(ProductListFragment.getInstance(Constant.TYPE_REGULAR));
        fragmentsList.add(ProductListFragment.getInstance(Constant.TYPE_BANK));
        fragmentsList.add(ProductListFragment.getInstance(Constant.TYPE_TRANSFER));
        fragmentsList.add(ProductDepositListFragment.getInstance());

        for (int i = 0; i < fragmentsList.size(); i++) {
            if (i < 3) {
                ((ProductListFragment) (fragmentsList.get(i))).setOnRecyclerViewChangeListner(new ProductListFragment.OnRecyclerViewChangeListner() {
                    @Override
                    public void onScrollChange(RecyclerView recyclerView, boolean isTop, int dx, int dy) {
                        Log.i("onScrollChange", "isTop=============: " + isTop);
                        Log.i("onScrollChange", "dy=============: " + dy);
                        Log.i("onScrollChange", "isAnimationRunning=============: " + isAnimationRunning);
                        Log.i("onScrollChange", "isShow=============: " + isShow);
                        Log.i("preTime", "System.currentTimeMillis() - preTime=============: " + (System.currentTimeMillis() - preTime));
                        if (isTop) {
                            if (dy < 0 && Math.abs(dy) > 2) {//下拉且到顶部
                                if (!isShow) {
                                    llTitleContianer.clearAnimation();
                                    v_red_bg.clearAnimation();
//                                    if (!isAnimationRunning && System.currentTimeMillis() - preTime > 200) {
                                    if (!isAnimationRunning) {
                                        llTitleContianer.startAnimation(mShowAction);
                                        titleBar.setVisibility(View.VISIBLE);
                                        v_red_bg.startAnimation(mShowAction1);
                                        v_red_bg.setVisibility(View.VISIBLE);
                                        isShow = !isShow;
                                        preTime = System.currentTimeMillis();
                                    }
                                }
                            }
                        } else {
                            if (isShow && dy > 0 && Math.abs(dy) > 2) {
                                llTitleContianer.clearAnimation();
                                v_red_bg.clearAnimation();
//                                if (!isAnimationRunning && System.currentTimeMillis() - preTime > 200) {
                                if (!isAnimationRunning) {
                                    llTitleContianer.startAnimation(mHiddenAction);
                                    titleBar.setVisibility(View.GONE);
                                    v_red_bg.startAnimation(mHiddenAction1);
                                    v_red_bg.setVisibility(View.GONE);
                                    isShow = !isShow;
                                    preTime = System.currentTimeMillis();
                                }
                            }
                        }
                    }

                    @Override
                    public void onRefrsh() {
                        if (!isShow) {
                            llTitleContianer.clearAnimation();
                            v_red_bg.clearAnimation();
                            if (!isAnimationRunning) {
                                llTitleContianer.startAnimation(mShowAction);
                                titleBar.setVisibility(View.VISIBLE);
                                v_red_bg.startAnimation(mShowAction1);
                                v_red_bg.setVisibility(View.VISIBLE);
                                isShow = !isShow;
                            }
                        }
                    }
                });
            }
        }
    }


    public void reflex(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dp10 = Utils.dip2px(tabLayout.getContext(), 10);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        int tabViewWidth = tabView.getWidth();
                        if (tabViewWidth == 0) {
                            tabView.measure(0, 0);
                            tabViewWidth = tabView.getMeasuredWidth();
                        }

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        /**
                         * 1.因为用到了反射，所以混淆的时候要注意
                         * 2.如果app:tabMode="fixed"，每个TabView的weight都为1，设置width是没用的，目前的解决办法是可以先拿到TabView的宽度减去TextView宽度除以2，得到TabView的左右margin，设置上去就行了（看看谁有更好的办法，希望提出来如何解决）
                         */
                        int margin = (tabViewWidth - width) / 2;
                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = margin;
                        params.rightMargin = margin;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
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
//        mTabs.setViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);


//        int currentListTabType = ((App) getActivity().getApplication()).currentListTabType;
//        for (int i = 0; i < typeBean.types.size(); i++) {
//            if (currentListTabType == typeBean.types.get(i).value) {
//                mViewPager.setCurrentItem(i);
//            }
//        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                ((App) getActivity().getApplication()).currentListTabType = mTabsAdapter.getTabValue(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //字体加粗
                LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                View tabView = mTabStrip.getChildAt(tab.getPosition());
                Field mTextViewField = null;
                try {
                    mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                    mTextViewField.setAccessible(true);
                    TextView mTextView = (TextView) mTextViewField.get(tabView);
                    mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //字体取消加粗
                LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                View tabView = mTabStrip.getChildAt(tab.getPosition());
                Field mTextViewField = null;
                try {
                    mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                    mTextViewField.setAccessible(true);
                    TextView mTextView = (TextView) mTextViewField.get(tabView);
                    mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.setCurrentItem(0);
        reflex(tabLayout);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
//                    return ProductListFragment.getInstance(Constant.TYPE_REGULAR);
                    return fragmentsList.get(0);
                case Constant.TYPE_BANK://存管专区
                    return fragmentsList.get(1);
//                    return ProductListFragment.getInstance(Constant.TYPE_BANK);
                case Constant.TYPE_TRANSFER://债权转让
                    return fragmentsList.get(2);
//                    return ProductListFragment.getInstance(Constant.TYPE_TRANSFER);
                case Constant.TYPE_POOL://智存出借
                    return fragmentsList.get(3);
//                    return ProductDepositListFragment.getInstance();
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

        public Fragment getCurrentFragment(int position) {
            return fragmentsList.get(position);
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
