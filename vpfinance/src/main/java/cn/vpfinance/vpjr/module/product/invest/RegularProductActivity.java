package cn.vpfinance.vpjr.module.product.invest;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.tdk.utils.HttpDownloader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.LoanRecord;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.gson.PersonalInfo;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.RegularProductInfo;
import cn.vpfinance.vpjr.module.product.fragment.NewBaseInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.PersonalProductRiskControlFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductInvestListFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductJewelryBaseInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductJewelryDescriptionFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductPrivateFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductPrivateNoLoginFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductBaoliBasicFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductBaoliFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductBorrowerCreditFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductBorrowerFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductBorrowerFragment2;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductBorrowerInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductBorrowerQualificationMaterialFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductCarDescriptionFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductCarInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductDetailFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductJewelryDetailsFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductJewelryRiskFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductProjectIntroductionFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductQualificationMaterialFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductRiskControlFragment;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;


public class RegularProductActivity extends BaseActivity implements HttpDownloader.HttpDownloaderListener{

	private static final String TAG        = "RegularProductActivity";
	public static final  String IS_GET_TUI = "IS_GET_TUI";

	private HttpService mHttpService = null;
	private ViewPager                 mViewPager;
	private FragmentStatePagerAdapter mTabsAdapter;
	private PagerSlidingTabStrip      tabs;

	public static final String EXTRA_PRODUCT_TYPE       = "type";
	public static final String EXTRA_PRODUCT_ID         = "id";
	public static final String EXTRA_PRODUCT_DATABSE_ID = "databse_id";
	public static final String EXTRA_PRODUCT_PRODUCT    = "product";
	public static final String NATIVE_PRODUCT_ID        = "native_product_id";//如果查看原标传转让标id，不是传0
	private FinanceProduct product;
	private long           productLong;

	//	private Toolbar toolbar;

	private DaoMaster.DevOpenHelper dbHelper;
	private SQLiteDatabase          db;
	private DaoMaster               daoMaster;
	private DaoSession              daoSession;
//	private FinanceProductDao       dao;
	private UserDao                 userDao;

	private long prodType = -1;
	private ActionBarLayout titleBar;
	private int             mShowRecord;
	private Context         mContext;
	private long mLid  = -1;
	private int  mType = -1;
	private long mPid  = -1;
	private long mNativePid;
	//	private boolean needJumpLogin = false;

	public static void goRegularProductActivity(Context context, String id, int type, long product, long dbId, String nativeProductId) {
		if (context != null) {
			Intent intent = new Intent(context, RegularProductActivity.class);
			intent.putExtra(EXTRA_PRODUCT_ID, id);
			intent.putExtra(EXTRA_PRODUCT_TYPE, type);
			intent.putExtra(EXTRA_PRODUCT_PRODUCT, product);
			intent.putExtra(EXTRA_PRODUCT_DATABSE_ID, dbId);
			intent.putExtra(NATIVE_PRODUCT_ID, nativeProductId);
			context.startActivity(intent);
		}
	}

	private void umengEvent(int position) {
		//Log.e(TAG,"position:" + position);
		ArrayMap<String, String> map = new ArrayMap<String, String>();
		map.put("type", "" + prodType);
		map.put("page", "" + (position + 1));
		MobclickAgent.onEvent(RegularProductActivity.this, "ProductPage", map);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_regular_detail);

		umengEvent(0);
		mContext = this;
		mHttpService = new HttpService(this, this);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setIndicatorColor(0xFFFF3035);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		mViewPager.setPageMargin(pageMargin);
		mViewPager.setOffscreenPageLimit(5);

		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				umengEvent(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
		titleBar.setHeadBackVisible(View.VISIBLE).setTitle("微品金融");

		dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
		db = dbHelper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
//		dao = daoSession.getFinanceProductDao();
		userDao = daoSession.getUserDao();

		Intent intent = getIntent();
		if (intent != null) {
			if (intent.getBooleanExtra(IS_GET_TUI, false)) {
				//个推点击了过来的就友盟统计
				ArrayMap<String, String> map = new ArrayMap<String, String>();
				map.put("GeTuiType", "2");
				MobclickAgent.onEvent(RegularProductActivity.this, "GeTuiClick", map);
			}

			productLong = intent.getLongExtra(EXTRA_PRODUCT_PRODUCT, 0);
			//			Log.e(TAG,"productLong:" + productLong);
			pid = intent.getStringExtra(EXTRA_PRODUCT_ID);
			String nativePid = intent.getStringExtra(NATIVE_PRODUCT_ID);
			try {
				mNativePid = Long.parseLong(nativePid);
				mLid = Long.parseLong(pid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mType = intent.getIntExtra(EXTRA_PRODUCT_TYPE, -1);
		}
	}

	private String pid;

	@Override
	protected void onResume() {
		mHttpService.getUserInfo();
		super.onResume();
		mViewPager.setCurrentItem(0);
		product = new FinanceProduct();

		mHttpService.getFixProduct("" + pid, "" + mNativePid);
		//个人借贷
		if (productLong == 3) {
			mHttpService.getPersonalProduct("" + pid, "3",mNativePid);
		}
		//沈阳项目
		if (productLong == 4){

		}
	}
	class JewelryAdapter extends FragmentStatePagerAdapter{
		private long pid;
		private int type;
		private FragmentManager mFragmentManager;
		private String[] titles = new String[]{"基本信息","交易描述","质押详情","风险控制"};

		public JewelryAdapter(FragmentManager fm,long pid,int type) {
			super(fm);
			this.pid = pid;
			this.type = type;
			mFragmentManager = fm;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return ProductJewelryBaseInfoFragment.newInstance(pid, type);
				case 1:
					return ProductJewelryDescriptionFragment.newInstance(pid, type);
				case 2:
					return isShowTab(RegularProductJewelryDetailsFragment.newInstance(pid, type), mShowRecord);
				case 3:
					return isShowTab(RegularProductJewelryRiskFragment.newInstance(pid),mShowRecord);
//					return isShowTab(ProductInvestListFragment.newInstance(pid, type),mShowRecord);
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String result = "微品金融";
			try{
				result = titles[position];
			}catch (Exception e){
				e.printStackTrace();
			}
			return result;
		}

		@Override
		public int getCount() {
			return 4;
		}
	}
	class CarAdapter extends FragmentStatePagerAdapter{

		private long pid;
		private int type;

		private SparseIntArray names;

		private SparseArray<String> mFragmentTags;

		private final FragmentManager mFragmentManager;

		public CarAdapter(FragmentManager fm,long pid,int type)
		{
			super(fm);

			this.pid = pid;
			this.type = type;
			mFragmentManager = fm;
			mFragmentTags = new SparseArray<String>();

			names = new SparseIntArray();
			names.put(0, R.string.product_car_tab1);
			names.put(1, R.string.product_car_tab2);
			names.put(2, R.string.product_car_tab3);
			names.put(3, R.string.product_car_tab4);
		}

		@Override
		public Fragment getItem(int position) {
//			Logger.e("mShowRecord:"+mShowRecord);
			switch (position) {
				case 0:
					return RegularProductBorrowerFragment2.newInstance(pid, type);
				case 1:
					return isShowTab(RegularProductCarDescriptionFragment.newInstance(), mShowRecord);
//					return isShowTab(ProductInvestListFragment.newInstance(pid, type),mShowRecord);
				case 2:
					return isShowTab(RegularProductCarInfoFragment.newInstance(pid, type), mShowRecord);
				case 3:
					return isShowTab(RegularProductRiskControlFragment.newInstance(pid),mShowRecord);
			}
			return null;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (names != null)
			{
				return getString(names.get(position, R.string.app_name));
			}
			return getString(R.string.app_name);
		}

		public Fragment getFragment(int position) {
			String tag = mFragmentTags.get(position);
			if (tag == null)
				return null;
			return mFragmentManager.findFragmentByTag(tag);
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = (Fragment)super.instantiateItem(container, position);
			fragment.setMenuVisibility(true);
			fragment.setUserVisibleHint(true);
			return fragment;
		}
	}

	class MyAdapter extends FragmentStatePagerAdapter{
		
//		private long pid;
//		private int type;

		private SparseIntArray names;

		private SparseArray<String> mFragmentTags;

		private final FragmentManager mFragmentManager;

		public MyAdapter(FragmentManager fm,long pid,int type)
		{
			super(fm);

			mPid = pid;
			mType = type;
			mFragmentManager = fm;
			mFragmentTags = new SparseArray<String>();

			names = new SparseIntArray();
			//names.put(0, R.string.product_basic_info);
			//names.put(1, R.string.product_borrower_info);
			names.put(0, R.string.product_basic_info);
			names.put(1, R.string.product_project_introduction);
			names.put(2, R.string.product_risk_control);
			names.put(3, R.string.product_qualification_material);
			names.put(4, R.string.product_inverst_record);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
/*			case 0:
				return new LoanDetailBasicFragment();
			case 1:
				return ProductBorrowerFragment.newInstance(pid, type);*/
			case 0:
				return new RegularProductDetailFragment();
			case 1:
				return isShowTab(RegularProductProjectIntroductionFragment.newInstance(mPid),mShowRecord);
//				return RegularProductProjectIntroductionFragment.newInstance(pid);
			case 2:
				return isShowTab(RegularProductRiskControlFragment.newInstance(mPid),mShowRecord);
//				return RegularProductRiskControlFragment.newInstance(pid);
			case 3:
				return isShowTab(RegularProductQualificationMaterialFragment.newInstance(mPid),mShowRecord);
//				return RegularProductQualificationMaterialFragment.newInstance(pid);
			case 4:
				return isShowTab(ProductInvestListFragment.newInstance(mPid, mType,-1,false,System.currentTimeMillis()),mShowRecord);
//				return ProductInvestListFragment.newInstance(pid, type);

			default:
				break;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (names != null)
			{
				return getString(names.get(position, R.string.app_name));
			}
			return getString(R.string.app_name);
		}

		public Fragment getFragment(int position) {
	        String tag = mFragmentTags.get(position);
	        if (tag == null)
	            return null;
	        return mFragmentManager.findFragmentByTag(tag);
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

	class PersonalAdapter extends FragmentStatePagerAdapter {

		private long pid;
		private int type;

		private SparseIntArray names;

		private SparseArray<String> mFragmentTags;

		private final FragmentManager mFragmentManager;

		public PersonalAdapter(FragmentManager fm,long pid,int type)
		{
			super(fm);

			this.pid = pid;
			this.type = type;
			mFragmentManager = fm;
			mFragmentTags = new SparseArray<String>();

			names = new SparseIntArray();
			names.put(0, R.string.product_detail);
			names.put(1, R.string.product_person_borrower_info);
			names.put(2, R.string.product_person_borrower_credit);
			names.put(3, R.string.product_risk_control);
			names.put(4, R.string.product_qualification_material);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return RegularProductBorrowerFragment.newInstance(pid, type);
				case 1:
					return isShowTab(RegularProductBorrowerInfoFragment.newInstance(pid),mShowRecord);
//					return RegularProductBorrowerInfoFragment.newInstance(pid);
				case 2:
					return isShowTab(RegularProductBorrowerCreditFragment.newInstance(pid),mShowRecord);
//					return RegularProductBorrowerCreditFragment.newInstance(pid);
				case 3:
					return isShowTab(PersonalProductRiskControlFragment.newInstance(pid),mShowRecord);
//					return PersonalProductRiskControlFragment.newInstance(pid);
				case 4:
					return isShowTab(RegularProductBorrowerQualificationMaterialFragment.newInstance(pid),mShowRecord);
//					return RegularProductBorrowerQualificationMaterialFragment.newInstance(pid);
//				case 4:
//					return ProductInvestListFragment.newInstance(pid, type);

				default:
					break;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (names != null)
			{
				return getString(names.get(position, R.string.app_name));
			}
			return getString(R.string.app_name);
		}

		public Fragment getFragment(int position) {
			String tag = mFragmentTags.get(position);
			if (tag == null)
				return null;
			return mFragmentManager.findFragmentByTag(tag);
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

	class BaoliAdapter extends FragmentStatePagerAdapter {
		private long pid;
		private int type;

		private SparseIntArray names;
		private SparseArray<String> mFragmentTags;
		private final FragmentManager mFragmentManager;

		public BaoliAdapter(FragmentManager fm,long pid,int type,String type2)
		{
			super(fm);
			this.pid = pid;
			this.type = type;
			mFragmentManager = fm;
			mFragmentTags = new SparseArray<String>();

			names = new SparseIntArray();
			names.put(0, R.string.product_detail);
			if ("7".equals(type2)){//7的时候是融珠宝
				names.put(1, R.string.product_project_rongzhu_tab2);
				names.put(2, R.string.product_project_rongzhu_tab4);
			}else{
				names.put(1, R.string.product_project_introduction);
				names.put(2, R.string.product_project_baoli);
			}
			names.put(3, R.string.product_risk_control);

//			names.put(4, R.string.product_inverst_record);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return new RegularProductDetailFragment();
				case 1:
					return isShowTab(RegularProductBaoliBasicFragment.newInstance(pid),mShowRecord);
				case 2:
					return isShowTab(RegularProductBaoliFragment.newInstance(pid),mShowRecord);
				case 3:
					return isShowTab(RegularProductRiskControlFragment.newInstance(pid),mShowRecord);
//				case 4:
//					return isShowTab(ProductInvestListFragment.newInstance(pid, type),mShowRecord);

				default:
					break;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (names != null)
			{
				return getString(names.get(position, R.string.app_name));
			}
			return getString(R.string.app_name);
		}

		public Fragment getFragment(int position) {
			String tag = mFragmentTags.get(position);
			if (tag == null)
				return null;
			return mFragmentManager.findFragmentByTag(tag);
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

	private BaseFragment isShowTab(BaseFragment fragment, int status){
		if (status == 2){//为2的话除了投资了记录的tab以外其余tab需要登录
			if (AppState.instance().logined()){
//				needJumpLogin = false;
				return fragment;
			}else{
//				needJumpLogin = true;
				return new ProductPrivateNoLoginFragment();
			}

		}else if(status == 3){//为3的话提示,不能看
//			needJumpLogin = false;
			return new ProductPrivateFragment();
		}
		//为1的话，所有tab均可看
		return fragment;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item != null) {
			switch (item.getItemId()) {
				case android.R.id.home:
					finish();
				default:
					break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		if (!isHttpHandle(json)) return;
		if (reqId == ServiceCmd.CmdId.CMD_commonLoanDesc.ordinal()) {
			PersonalInfo info = mHttpService.onGetPersonal(json);
			if (info != null) {
				EventBus.getDefault().post(info);
			}
		}
		else if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo.ordinal()) {
			if (json != null) {
				mShowRecord = json.optInt("showRecord");

				ArrayList<LoanRecord> rList = mHttpService.onGetProductDetail(this, json, product);
				FragmentManager supportFragmentManager = getSupportFragmentManager();
				if(product!=null && supportFragmentManager!=null)
				{
					if (titleBar != null) {
						titleBar.setTitle(product.getLoanTitle());
					}

					prodType = product.getProduct();//这个是product，之前命名很奇怪
					String productType = product.getProductType();

//					if ("6".equals(productType) && prodType==0){//珠宝贷productType=6,product=0
//						mTabsAdapter = new JewelryAdapter(supportFragmentManager,product.getPid(),product.getType());
//						mViewPager.setAdapter(mTabsAdapter);
//						mTabsAdapter.notifyDataSetChanged();
//						tabs.setViewPager(mViewPager);
//						JewelryBean jewelryBean = mHttpService.onGetProductJewelryInfo(json);
//						if (jewelryBean != null && jewelryBean.getContentList() != null && jewelryBean.getContentList().size() != 0){
//							EventBus.getDefault().post(new JewelryContentListEvent(jewelryBean.getContentList()));
//						}
//
//					}else if ("2".equals(productType) && prodType==0){//车贷productType=2,product=0
//
//						mTabsAdapter = new CarAdapter(supportFragmentManager,product.getPid(),product.getType());
//						mViewPager.setAdapter(mTabsAdapter);
//						mTabsAdapter.notifyDataSetChanged();
//						tabs.setViewPager(mViewPager);
//					}else if(prodType==2){//保理标productType=7,product=2（和接口反了）
//						mTabsAdapter = new BaoliAdapter(supportFragmentManager,product.getPid(),product.getType(),productType);
//						mViewPager.setAdapter(mTabsAdapter);
//						mTabsAdapter.notifyDataSetChanged();
//						tabs.setViewPager(mViewPager);
//					}else if (productLong == 3) {
//						mTabsAdapter = new PersonalAdapter(supportFragmentManager, mLid, mType);
//						mViewPager.setAdapter(mTabsAdapter);
//						tabs.setViewPager(mViewPager);
//					}else{
						mTabsAdapter = new MyAdapter(supportFragmentManager,mLid,mType);
						mViewPager.setAdapter(mTabsAdapter);
						tabs.setViewPager(mViewPager);
//					}
				}
				/*if (dao != null) {
					dao.insertOrReplace(product);
				}*/
				
				EventBus.getDefault().post(new RegularProductInfo(product));
				if(rList!=null)
				{
					EventBus.getDefault().post(rList);
				}
			}
		}
		if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal())
		{
			User user = null;
			if (userDao != null) {
				QueryBuilder<User> qb = userDao.queryBuilder();
				List<User> userList = qb.list();
				if (userList != null && userList.size() > 0) {
					user = userList.get(0);
				}
			}
			if (user != null) {
				mHttpService.onGetUserInfo(json,user);
				if (userDao != null && AppState.instance().logined()) {
					userDao.insertOrReplace(user);
				}
			}
		}
	}
}
