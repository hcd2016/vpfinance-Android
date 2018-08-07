package cn.vpfinance.vpjr.module.user.asset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.jewelcredit.ui.widget.ActionBarLayout;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.product.fragment.NewFundRecordsListFragment;
import de.greenrobot.event.EventBus;

/**
 * 个人中心 我的投资
 *
 */
public class FundRecordsActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

	private ViewPager mViewPager = null;
	private PagerSlidingTabStrip tabs = null;
	private String[] mNames = new String[]{"持有中", "已完成"};
	private int accountType = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_fund_records);
		Intent intent = getIntent();
		if (intent != null){
			accountType = intent.getIntExtra(Constant.AccountType,0);
		}
		initView();
	}

	protected void initView() {
		ActionBarLayout titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
		titleBar.setTitle("我的投资").setHeadBackVisible(View.VISIBLE);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setIndicatorColor(0xFFFF3035);
//		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
//				.getDisplayMetrics());
//		mViewPager.setPageMargin(pageMargin);
//		mViewPager.setOffscreenPageLimit(2);

		MyAdapter mTabsAdapter = new MyAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mTabsAdapter);

		tabs.setViewPager(mViewPager);
		mViewPager.addOnPageChangeListener(this);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if (position == 0) {
			EventBus.getDefault().post(new RecordsRefreshBean("1"));
		}else if (position == 1) {
			EventBus.getDefault().post(new RecordsRefreshBean("2"));
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	public class RecordsRefreshBean{
		public String msg;

		public RecordsRefreshBean(String msg) {
			this.msg = msg;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}

	class MyAdapter extends FragmentPagerAdapter{

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return NewFundRecordsListFragment.newInstance("1",accountType);
				case 1:
					return NewFundRecordsListFragment.newInstance("2",accountType);
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mNames[position];
		}
	}
}