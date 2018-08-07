package cn.vpfinance.vpjr.module.user.asset;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.product.record.FundRecordsDetailActivity;
import cn.vpfinance.vpjr.module.common.SharePop;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.FundRecordInfo;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.ScreenUtil;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * 个人中心 投资记录
 *
 */
public class FundRecordsActivity2 extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

	private ListView           listView;
	private TextView           textview;
	private MyAdapter          mAdapter;
	private SwipeRefreshLayout mRefresh;
	private              boolean isRefreshing = false;
	private              int     page         = 1;
	private final static int     PAGE_SIZE    = 10;
	private HttpService mHttpService;
	private UserDao     userDao;
	private User        user;
	private ArrayList<FundRecordInfo.RecordListInfoItem> allList = new ArrayList<FundRecordInfo.RecordListInfoItem>();
	private EditText     beginTime;
	private EditText     endTime;
	private List<String> currentDate;
	private Button       find;
	private String       totalPage;
	private LinearLayout mViewLl;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_fund_records2);
		mHttpService = new HttpService(this, this);
		EventBus.getDefault().register(this);
		getUser();
		initView();
	}

	protected void initView() {
		ActionBarLayout titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
		titleBar.setTitle("我的投资").setHeadBackVisible(View.VISIBLE);
		listView = (ListView) findViewById(R.id.listView);
		textview = (TextView) findViewById(R.id.textview);

		mViewLl = (LinearLayout) findViewById(R.id.view_ll);
		beginTime = (EditText) findViewById(R.id.beginTime);
		endTime = (EditText) findViewById(R.id.endTime);
		find = (Button) findViewById(R.id.find);
		beginTime.setFocusable(false);
		endTime.setFocusable(false);

		beginTime.setOnClickListener(this);
		endTime.setOnClickListener(this);
		beginTime.setOnFocusChangeListener(this);
		endTime.setOnFocusChangeListener(this);

		find.setOnClickListener(this);


		mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
		mRefresh.setColorSchemeColors(getResources().getColor(R.color.main_color));
		mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isRefreshing = true;
				page = 1;
				allList.clear();
				mRefresh.setRefreshing(isRefreshing);
				//				mHttpService.getFundRecord(user.getUserId() + "", null, null, "" + page, "" + PAGE_SIZE);
				startFindRecord(beginTime.getText() + "", endTime.getText() + "");
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				FundRecordInfo.RecordListInfoItem item = mAdapter.getItem(i);
				int recordId = item.recordId;

				Intent where = new Intent(FundRecordsActivity2.this,FundRecordsDetailActivity.class);
				where.putExtra(FundRecordsDetailActivity.UID,recordId);
				startActivity(where);
			}
		});
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == view.getCount() - 1) {

					//模拟最后一页
					if (Integer.parseInt(totalPage) >= page+1){
						page++;
						startFindRecord(beginTime.getText()+"", endTime.getText()+"");
					} else {
//						Toast.makeText(FundRecordsActivity2.this,"数据加载完了！",Toast.LENGTH_SHORT).show();
					}
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
			}
		});

		mAdapter = new MyAdapter(this);
		listView.setAdapter(mAdapter);

	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		if (!isHttpHandle(json)) return;
		if (reqId == ServiceCmd.CmdId.CMD_FundRecord.ordinal()) {
			FundRecordInfo fundRecordInfo = mHttpService.onGetFundRecordInfo(json);
			if ("true".equals(fundRecordInfo.success)){
				textview.setVisibility(View.GONE);
				totalPage = fundRecordInfo.totalPage;

				isRefreshing = false;
				if (mRefresh.isRefreshing()){
					mRefresh.setRefreshing(isRefreshing);
				}
				EventBus.getDefault().post(fundRecordInfo);
			}
		}
	}
	public void onEventMainThread(FundRecordInfo event) {
		if (isFinishing())	return;
		List<FundRecordInfo.RecordListInfoItem> recordList = event.recordList;

		if (allList.size() == 0 && (recordList==null || recordList.size()==0)){
			textview.setText("暂无数据");
			textview.setVisibility(View.VISIBLE);
		}else{
			textview.setVisibility(View.GONE);
			allList.addAll(recordList);
		}

		mAdapter.setData(allList);

		if(mRefresh.isRefreshing()) {
			mRefresh.setRefreshing(false);
		}
		isRefreshing = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (user ==null){
			getUser();
		}
		page = 1;
		allList.clear();
		currentDate = Utils.getCurrentDate();
		beginTime.setText(currentDate.get(0));
		endTime.setText(currentDate.get(1));
		startFindRecord(beginTime.getText()+"", endTime.getText()+"");
	}

	private void startFindRecord(String s, String s1) {
		if (Utils.compareDate(s, s1)){
			Toast.makeText(FundRecordsActivity2.this,"起始时间大于结束时间",Toast.LENGTH_SHORT).show();
			if (mRefresh.isRefreshing()){
				mRefresh.setRefreshing(false);
			}
			isRefreshing = false;
			return;
		}

		s = "".equals(s) ? null : s ;
		s1 = "".equals(s1) ? null : s1 ;
		mHttpService.getFundRecord(user.getUserId() + "", s, s1, "" + page, "" + PAGE_SIZE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch(v.getId()){
			case R.id.beginTime:
				Utils.showSelectDialog(beginTime, FundRecordsActivity2.this);
				break;
			case R.id.endTime:
				Utils.showSelectDialog(endTime, FundRecordsActivity2.this);
				break;
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.beginTime:
				Utils.showSelectDialog(beginTime, FundRecordsActivity2.this);
				break;
			case R.id.endTime:
				Utils.showSelectDialog(endTime, FundRecordsActivity2.this);
				break;
			case R.id.find:
				allList.clear();
				page = 1 ;
				startFindRecord(beginTime.getText()+"",endTime.getText()+"");
				break;
		}
	}


	private class MyAdapter extends BaseAdapter{
		private Context mContext;
		private List<FundRecordInfo.RecordListInfoItem> list;

		public MyAdapter(Context mContext) {
			this.mContext = mContext;
		}
		public void setData(List<FundRecordInfo.RecordListInfoItem> list){
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (list !=null){
				return list.size();
			}
			return 0;
		}

		@Override
		public FundRecordInfo.RecordListInfoItem getItem(int position) {
			if (list !=null){
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=null;
			ViewHolder holder =null;
			if (convertView == null){
				view = View.inflate(mContext,R.layout.item_fund_record,null);
				holder =new ViewHolder();
				holder.title = (TextView)view.findViewById(R.id.title);
				holder.tenderMoney = (TextView)view.findViewById(R.id.tenderMoney);
				holder.tenderTime = (TextView)view.findViewById(R.id.tenderTime);
				holder.voucherMoney = (TextView)view.findViewById(R.id.voucherMoney);
				holder.btnShareRedPacket = (Button)view.findViewById(R.id.btnShareRedPacket);
				holder.vDivider = view.findViewById(R.id.vDivider);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder)view.getTag();
			}
			try{
				if (list !=null){
					FundRecordInfo.RecordListInfoItem recordListInfoItem = list.get(position);
					holder.title.setText(recordListInfoItem.title);
					String tenderMoney = recordListInfoItem.tenderMoney;
					holder.tenderMoney.setText("¥"+tenderMoney);
					holder.tenderTime.setText(recordListInfoItem.tenderTime);
					if ("1".equals(recordListInfoItem.couponsCount)) {
						holder.voucherMoney.setVisibility(View.VISIBLE);
						holder.voucherMoney.setText("已加息" + recordListInfoItem.voucherMoney + "元");
						Drawable drawable = getResources().getDrawable(R.drawable.interest);
						drawable.setBounds(0, 0, Utils.sp2px(mContext, 12), Utils.sp2px(mContext, 12));
						holder.voucherMoney.setCompoundDrawables(drawable, null, null, null);
					} else {
						holder.voucherMoney.setVisibility("0.00".equals(recordListInfoItem.voucherMoney)?View.GONE:View.VISIBLE);
						holder.voucherMoney.setText("已抵扣" + recordListInfoItem.voucherMoney + "元");
						Drawable drawable= getResources().getDrawable(R.drawable.voucher);
						drawable.setBounds(0, 0, Utils.sp2px(mContext, 12), Utils.sp2px(mContext, 12));
						holder.voucherMoney.setCompoundDrawables(drawable, null, null, null);
					}
					final String shareUrl = recordListInfoItem.shareUrl;

					if ("false".equals(recordListInfoItem.haveRedPacket)){
						holder.btnShareRedPacket.setVisibility(View.GONE);
						holder.vDivider.setVisibility(View.GONE);
					}if("true".equals(recordListInfoItem.haveRedPacket) && (!TextUtils.isEmpty(recordListInfoItem.shareUrl))){
						holder.btnShareRedPacket.setVisibility(View.VISIBLE);
						holder.vDivider.setVisibility(View.VISIBLE);
						final View finalView = view;
						holder.btnShareRedPacket.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								final File imgFile = new File(getCacheDir(), "redpack.png");
								if (imgFile != null && imgFile.exists()) {
									//imgFile.delete();
								}
								else
								{
									try {
										Utils.copyAssetsFileToPath(FundRecordsActivity2.this, "redpack.png", imgFile.getAbsolutePath());
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
//								showShare("好友发来微品金融红包，个数有限先到先得，快去碰碰手气吧。", imgFile == null ? "" : imgFile.getAbsolutePath(), shareUrl);
								SharePop sharePop = new SharePop(mContext, shareUrl, imgFile == null ? "" : imgFile.getAbsolutePath(),
										"好友发来微品金融红包，个数有限先到先得，快去碰碰手气吧。", "微品金融拼手气红包");
								sharePop.showAtLocation(mViewLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ScreenUtil.getBottomStatusHeight(mContext));
							}
						});
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			return view;
		}
	}
	static class ViewHolder{
		TextView title;
		TextView tenderMoney;
		TextView tenderTime;
		TextView voucherMoney;
		Button btnShareRedPacket;
		View vDivider;
	}

	private void showShare(final String text, String imageUrl, final String link) {
		if (TextUtils.isEmpty(link))	return;

//		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		//oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("微品金融拼手气红包");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(link);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(text);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if(!TextUtils.isEmpty(imageUrl))
		{
			oks.setImagePath(imageUrl);
		}
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(link);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		//oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite("微品金融拼手气红包");
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(link);

		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {

			@Override
			public void onShare(Platform platform,
								cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
				if ("SinaWeibo".equals(platform.getName())) {
					paramsToShare.setText(text + " " +link);

				}
			}
		});

		// 启动分享GUI
		oks.show(this);
	}

	private void getUser() {
		if (mHttpService ==null){
			mHttpService =new HttpService(this,this);
		}
		DaoMaster.DevOpenHelper dbHelper;
		SQLiteDatabase db;
		DaoMaster daoMaster;
		DaoSession daoSession;

		dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME , null);
		db = dbHelper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		userDao = daoSession.getUserDao();

		if(AppState.instance().logined())
		{
			if (userDao != null) {
				QueryBuilder<User> qb = userDao.queryBuilder();
				List<User> userList = qb.list();
				if (userList != null && userList.size() > 0) {
					user = userList.get(0);
				}
			}
		}
		else
		{
			Toast.makeText(this, "请登录.", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}

//	class MyAdapter extends FragmentPagerAdapter {
//		private SparseArray<String> names;
//		private SparseArray<String> mFragmentTags;
//		private final FragmentManager mFragmentManager;
//		public MyAdapter(FragmentManager fm) {
//			super(fm);
//
//			mFragmentManager = fm;
//			mFragmentTags = new SparseArray<String>();
//
//			names = new SparseArray<String>();
//			names.put(0, "1月");
//			names.put(1, "2月");
//			names.put(2, "3月");
//			names.put(3, "4月");
//			names.put(4, "5月");
//			names.put(5, "6月");
//			names.put(6, "7月");
//			names.put(7, "8月");
//			names.put(8, "9月");
//			names.put(9, "10月");
//			names.put(10, "11月");
//			names.put(11, "12月");
//		}
//
//		@Override
//		public Fragment getItem(int position) {
//			switch (position) {
//				case 0:
//					return FundRecordsListFragment.newInstance("1");
//				case 1:
//					return FundRecordsListFragment.newInstance("2");
//				case 2:
//					return FundRecordsListFragment.newInstance("3");
//				case 3:
//					return FundRecordsListFragment.newInstance("4");
//				default:
//					break;
//			}
//			return null;
//		}
//
//		@Override
//		public int getCount() {
//			return 4;
//		}
//
//		@Override
//		public CharSequence getPageTitle(int position) {
//			if (names != null) {
//				return names.get(position, "微品金融");
//			}
//			return getString(R.string.app_name);
//		}
//
//		public Fragment getFragment(int position) {
//			String tag = mFragmentTags.get(position);
//			if (tag == null)
//				return null;
//			return mFragmentManager.findFragmentByTag(tag);
//		}
//
//		@Override
//		public void setPrimaryItem(ViewGroup container, int position, Object object) {
//			//super.setPrimaryItem(container, position, object);
//		}
//
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
//			Fragment fragment = (Fragment)super.instantiateItem(container, position);
//			fragment.setMenuVisibility(true);
//			fragment.setUserVisibleHint(true);
//			return fragment;
//		}
//	}
}