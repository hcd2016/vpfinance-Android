package cn.vpfinance.vpjr.module.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.NewBaseInfoBean;
import cn.vpfinance.vpjr.gson.ProductTabBean;
import cn.vpfinance.vpjr.gson.TabPermissionBean;
import cn.vpfinance.vpjr.module.common.fragment.WebViewFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewBaseInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewCarInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewDepositFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewWritingAndPicFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewWritingFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductPrivateFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductPrivateNoLoginFragment;
import cn.vpfinance.vpjr.module.voucher.fragment.NewPersonInfoFragment;
import cn.vpfinance.vpjr.module.voucher.fragment.NewPictureFragment;
import cn.vpfinance.vpjr.module.voucher.fragment.NewType6Fragment;

/**
 * Created by Administrator on 2016/10/24.
 * 标的详情
 */
public class NewRegularProductActivity extends BaseActivity {

    public static final String EXTRA_PRODUCT_ID  = "id";
    public static final String NATIVE_PRODUCT_ID = "native_product_id";//如果查看原标传转让标id，不是传0
    public static final String PRODUCT_TITLE     = "product_title";//标名
    public static final String IS_GE_TUI     = "is_ge_tui";//是否个推
    public static final String IS_DEPOSIT     = "is_deposit";//是否定存宝
    public static final String RecordPoolId     = "recordPoolId";//如果是从定存宝的出借详情进去标的详情,传递(RecordPoolId),其他的不传递
    public int answerStatus;//1不弹 2弹提示框, 去做风险测评

    @Bind(R.id.titleBar)
    ActionBarLayout      mTitleBar;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.pager)
    ViewPager            mPager;
    private HttpService                                         mHttpService;
    private long                                                mPid;
    private int                                                 mNativeId;
    private ArrayList<BaseFragment>                             mFragments;
    private ArrayList<String>                                   mTabName;
    private String mTitle;
    private TabPermissionBean mPermissionBean;
    private boolean isDeposit;
    private String recordPoolId = "";

    public static void goNewRegularProductActivity(Context context, long id, int nativeProductId, String title,boolean isDeposit) {
        if (context != null) {
            Intent intent = new Intent(context, NewRegularProductActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, id);
            intent.putExtra(NATIVE_PRODUCT_ID, nativeProductId);
            intent.putExtra(PRODUCT_TITLE, title);
            intent.putExtra(IS_DEPOSIT,isDeposit);
            context.startActivity(intent);
        }
    }

    public static void goNewRegularProductActivity(Context context, long id, int nativeProductId, String title,boolean isDeposit,String recordPoolId) {
        if (context != null) {
            Intent intent = new Intent(context, NewRegularProductActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, id);
            intent.putExtra(NATIVE_PRODUCT_ID, nativeProductId);
            intent.putExtra(PRODUCT_TITLE, title);
            intent.putExtra(IS_DEPOSIT,isDeposit);
            intent.putExtra(RecordPoolId,recordPoolId);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_detail);
        ButterKnife.bind(this);

        mHttpService = new HttpService(this, this);
        Intent intent = getIntent();
        if (intent != null) {
            mPid = intent.getLongExtra(EXTRA_PRODUCT_ID, -1);
            mNativeId = intent.getIntExtra(NATIVE_PRODUCT_ID, 0);
            String titleStr = intent.getStringExtra(PRODUCT_TITLE);
            mTitle = TextUtils.isEmpty(titleStr) ? "产品详情" : titleStr;
            boolean isGeTui = intent.getBooleanExtra(IS_GE_TUI, false);
            isDeposit = intent.getBooleanExtra(IS_DEPOSIT,false);
            recordPoolId = intent.getStringExtra(RecordPoolId);
            if (isGeTui){
                //个推点击了过来的就友盟统计
                ArrayMap<String, String> map = new ArrayMap<String, String>();
                map.put("GeTuiType", "2");
                MobclickAgent.onEvent(this, "GeTuiClick", map);
            }
        }
        mTitleBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("微品金融");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDeposit){
            mHttpService.onGetDepositPermission(""+mPid);
        }else{
            mHttpService.onGetPermission(""+mPid,mNativeId+"");
        }
        mPager.setCurrentItem(0);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Tab_Permission.ordinal()){
            //解析
            Gson gson = new Gson();
            mPermissionBean = gson.fromJson(json.toString(), TabPermissionBean.class);
            if (isDeposit){
                mHttpService.getDepositProductInfo(mPid,recordPoolId);
            }else{
                mHttpService.getFixProductNew("" + mPid, ""+mNativeId);
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo_New.ordinal()) {
            Gson gson = new Gson();
            ProductTabBean productTabBean = gson.fromJson(json.toString(), ProductTabBean.class);

            initData(productTabBean);
            initView();
        }

        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Pool_Info.ordinal()) {
            Gson gson = new Gson();
            ProductTabBean productTabBean = gson.fromJson(json.toString(), ProductTabBean.class);
            initData(productTabBean);
            initView();
        }

        if (reqId == ServiceCmd.CmdId.CMD_Regular_Tab.ordinal()) {
            try {
                Gson gson = new Gson();
                NewBaseInfoBean newBaseInfoBean = gson.fromJson(json.toString(), NewBaseInfoBean.class);
                answerStatus = newBaseInfoBean.answerStatus;
                mTitleBar.setTitle(newBaseInfoBean.loanTitle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (isFinishing())  return;
        if (reqId == ServiceCmd.CmdId.CMD_Tab_Permission.ordinal()){//接口请求失败默认可以查看
            mPermissionBean = new TabPermissionBean();
            mPermissionBean.showRecord = 1;
            mHttpService.getFixProductNew("" + mPid, ""+mNativeId);
        }
    }

    protected void initView(){
        try{
//            mTabs.setIndicatorColor(0xFFFF3035);
            mPager.setPageMargin(Utils.dip2px(this, 4));
            mPager.setOffscreenPageLimit(5);
            MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
            mPager.setAdapter(myAdapter);
            mTabs.setupWithViewPager(mPager);
//            mTabs.setViewPager(mPager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initData(ProductTabBean productTabBean) {

        if (mFragments == null) {
            mFragments = new ArrayList<>();
            mTabName = new ArrayList<>();
        } else {
            mFragments.clear();
            mTabName.clear();
        }
        List<ProductTabBean.TabListBean> tabList = productTabBean.tabList;
        if (tabList != null){
            for (int i = 0; i < tabList.size(); i++) {
                ProductTabBean.TabListBean bean = tabList.get(i);
                if (bean != null){
                    String tabName = bean.tabName;
                    String dataType = bean.dataType;
                    String showType = bean.showType;
                    String url = bean.url;

                    if (i== 0){
                        mTabName.add(tabName);
                        mFragments.add(createFragment(dataType,showType,url));
                        mHttpService.getRegularTab(url);
                    }else{//判断权限
                        int showRecord;
                        if (mPermissionBean == null){
                            showRecord = 1;
                        }else{
                            showRecord = mPermissionBean.showRecord;
                        }
                        mTabName.add(bean.tabName);

                        BaseFragment fragment = null;
                        if (mPermissionBean == null){
                            //默认可以查看
                            fragment = createFragment(dataType,showType,url);
                        }else if (showRecord == 1){
                            //可以查看
                            fragment = createFragment(dataType,showType,url);
                        }else if (showRecord == 2){
                            //需要登录 这儿登录状态换成本地检测，直接new ProductPrivateNoLoginFragment()会造成登录后还是需要登录界面
//                            fragment = createFragment(dataType,showType,url);
                            fragment = new ProductPrivateNoLoginFragment();
                        }else if (showRecord == 3){
                            //提示信息
                            String info = TextUtils.isEmpty(mPermissionBean.info) ? "" : mPermissionBean.info;
                            ProductPrivateFragment productPrivateFragment = new ProductPrivateFragment();
                            Bundle args = new Bundle();
                            args.putString(ProductPrivateFragment.HINT_TEXT,info);
                            productPrivateFragment.setArguments(args);
                            fragment = productPrivateFragment;
                        }
                        mFragments.add(fragment);
                    }
                }
            }
        }
        if(tabList.size() > 3) {
            mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else {
            mTabs.setTabMode(TabLayout.MODE_FIXED);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    public BaseFragment createFragment(String dataType,String showType ,String url) {
//        showType = "2";
//        url = "http://192.168.1.192//h5/loan/jewelInfo?platform=android";
        BaseFragment fragment = null;
        if ("2".equals(showType)){
            fragment = WebViewFragment.newInstance(url);
        }else{
            switch (dataType) {
                case "0":
                    fragment = NewBaseInfoFragment.newInstance(url,isDeposit);
                    break;
                case "1"://完成
                    fragment = NewWritingFragment.newInstance(url);
                    break;
                case "2"://完成
                    fragment = NewPictureFragment.newInstance(url);
                    break;
                case "3"://完成
                    fragment = NewWritingAndPicFragment.newInstance(url);
                    break;
                case "4"://完成
                    fragment = NewCarInfoFragment.newInstance(url);
                    break;
                case "5"://完成
                    fragment = NewPersonInfoFragment.newInstance(url);
                    break;
                case "7"://定存宝债权列表
                    fragment = NewDepositFragment.newInstance(url);
                    break;
                case "6"://担保贷,借款人信息
                    fragment = NewType6Fragment.newInstance(url);
                    break;
            }
        }

        return fragment;
    }

    class MyAdapter extends FragmentStatePagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabName == null ? "" : mTabName.get(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            super.setPrimaryItem(container, position, object);
        }
    }
}
