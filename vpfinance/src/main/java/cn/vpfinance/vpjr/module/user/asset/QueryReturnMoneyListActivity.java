package cn.vpfinance.vpjr.module.user.asset;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.model.TradeFlowRecordInfo;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * 回款查询
 */
public class QueryReturnMoneyListActivity extends BaseActivity {

	private UserDao userDao;
	private User user;
	private HttpService mHttpService;
	private ArrayList<TradeFlowRecordInfo.DataListItem> allList = new ArrayList<TradeFlowRecordInfo.DataListItem>();
	private SwipeRefreshLayout mRefresh = null;
	private boolean isRefreshing = false;
	private int page =1;
	private final static int PAGE_SIZE = 10;
	private static String returnValue =null;
	private String totalPage;
	private TextView textview;
	private ExpandableListView expandableListView;
	private MyExpandableListViewAdapter myExpandableListViewAdapter;
	private boolean loading = false;
	private int accountType = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_return_money);

		Intent intent = getIntent();
		if (intent != null){
			accountType = intent.getIntExtra(Constant.AccountType,0);
		}

		mHttpService = new HttpService(this,this);
		EventBus.getDefault().register(this);
		getUser();
        initView();
    }
	protected void initView() {
		ActionBarLayout titlebar = (ActionBarLayout) findViewById(R.id.titleBar);
		titlebar.setTitle("回款查询").setHeadBackVisible(View.VISIBLE)
				.setImageButtonRight(R.drawable.img_calendar_switcher, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(FinanceApplication.getAppContext());
						sp.putStringValue(SharedPreferencesHelper.STATE_RETURN_CALENDER_OR_LIST,"1");
						Intent intent = new Intent(QueryReturnMoneyListActivity.this,ReturnMoneyCalendarActivity.class);
						intent.putExtra(Constant.AccountType,accountType);
						gotoActivity(intent);
						Utils.Toast(FinanceApplication.getAppContext(), "回款查询已切换至日历视图");
						finish();
					}
				});
//		titlebar.setActionRight("已回款", new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(TradeFlowRecordsActivity2.this, "已回款", Toast.LENGTH_SHORT);
//			}
//		});

		textview = (TextView) findViewById(R.id.textview);
		/*textview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (loading)
					return;
				isRefreshing = true;
				page = 1;
				returnValue = null;
				allList.clear();
				loading = true;
				mHttpService.getTradeFlowRecord(user.getUserId() + "", page + "", PAGE_SIZE + "", returnValue);
			}
		});*/

		expandableListView = (ExpandableListView) findViewById(R.id.expandablelistview);
		//取消箭头
		expandableListView.setGroupIndicator(new BitmapDrawable());
		//取消子项分割线
		expandableListView.setChildDivider(new BitmapDrawable());

		myExpandableListViewAdapter = new MyExpandableListViewAdapter(this);
		expandableListView.setAdapter(myExpandableListViewAdapter);


		mRefresh = (SwipeRefreshLayout)findViewById(R.id.refresh);
		mRefresh.setColorSchemeResources(R.color.main_color);
		mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (loading)	return;
				expandableListView.collapseGroup(0);
				isRefreshing = true;
				page = 1;
				returnValue = null;
				allList.clear();
				loading = true;
				mHttpService.getTradeFlowRecord(user.getUserId() + "", page + "", PAGE_SIZE + "", returnValue,accountType);
			}
		});
		expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				Logger.e("onScrollStateChanged:"+ (view.getLastVisiblePosition() == view.getCount() - 1) + ":" + (!isRefreshing));
				if (loading)	return;
				if (view.getLastVisiblePosition() == view.getCount() - 1 && !isRefreshing) {
//                    isRefreshing = true;
					page++;
					//判断是否是最后一页
//					Logger.e("是否最后一页：" + (Integer.parseInt(totalPage) >= page) + ":" + Integer.parseInt(totalPage) + ":" + page);
					if (Integer.parseInt(totalPage) >= page) {
						loading = true;
						mHttpService.getTradeFlowRecord(user.getUserId() + "", page + "", PAGE_SIZE + "", returnValue,accountType);
					}else{
//						Toast.makeText(TradeFlowRecordsActivity2.this,"数据加载完了！",Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		if (user.getUserId() == null){
			gotoActivity(LoginActivity.class);
		}else {
			mHttpService.getTradeFlowRecord(user.getUserId() + "", page + "", PAGE_SIZE + "", returnValue,accountType);
		}
	}

	class MyExpandableListViewAdapter extends BaseExpandableListAdapter{
		private Context context;
		private ArrayList<TradeFlowRecordInfo.DataListItem> list;

		MyExpandableListViewAdapter(Context context) {
			this.context = context;
		}

		public void setData(ArrayList<TradeFlowRecordInfo.DataListItem> list) {
			this.list = list;
			notifyDataSetChanged();
		}
		@Override
		public int getGroupCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return list.get(groupPosition).dataList2 == null ? 0 : list.get(groupPosition).dataList2.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return list == null ? null :list.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return list.get(groupPosition).dataList2 == null ? null : list.get(groupPosition).dataList2.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			View view=null;
			ViewHolder holder =null;
			if (convertView == null){
				view = View.inflate(context, R.layout.item_trade_flow_records, null);
				holder =new ViewHolder();
				holder.loanTitle = (TextView)view.findViewById(R.id.loanTitle);
				holder.recentTradeTime = (TextView)view.findViewById(R.id.recentTradeTime);
				holder.checkState = (View)view.findViewById(R.id.checkState);
				holder.ExDivider = (View)view.findViewById(R.id.ExDivider);
				holder.tenderMoneySum = (TextView)view.findViewById(R.id.tenderMoneySum);
				holder.preRepayMoneySum = (TextView)view.findViewById(R.id.preRepayMoneySum);
				holder.repayMoneySum = (TextView)view.findViewById(R.id.repayMoneySum);
				holder.tradeList =(ListView)view.findViewById(R.id.tradeList);
				holder.imageState =(ImageView)view.findViewById(R.id.imageState);
				holder.imgTriangle =(ImageView)view.findViewById(R.id.imgTriangle);
				view.setTag(holder);
			}else{
				view =convertView;
				holder =(ViewHolder)view.getTag();
			}
			try{
				if (list != null && list.size() != 0 && list.size() > groupPosition){
					TradeFlowRecordInfo.DataListItem dataListItem = list.get(groupPosition);
					holder.loanTitle.setText(dataListItem.loanTitle);
					holder.recentTradeTime.setText("近期回款"+dataListItem.returnMoneyTime);
					holder.tenderMoneySum.setText(dataListItem.tenderMoneySum);
					holder.preRepayMoneySum.setText(dataListItem.preRepayMoneySum);
					holder.repayMoneySum.setText(dataListItem.repayMoneySum);

					if (!isExpanded){
						holder.imageState.setImageResource(R.drawable.list_arrow_up);
						holder.checkState.setVisibility(View.GONE);
						holder.imgTriangle.setVisibility(View.GONE);
						holder.ExDivider.setVisibility(View.VISIBLE);
						holder.checkState.setBackgroundColor(getResources().getColor(R.color.gray_6c));
					}else{
						holder.imageState.setImageResource(R.drawable.list_arrow_down);
						holder.checkState.setVisibility(View.GONE);
						holder.imgTriangle.setVisibility(View.VISIBLE);
						holder.ExDivider.setVisibility(View.GONE);
						holder.checkState.setBackgroundColor(Color.RED);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			return view;
		}


		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(context, R.layout.item_trade_flow_detail_record, null);
			try{
				LinearLayout description = (LinearLayout) view.findViewById(R.id.description);
				description.setVisibility(childPosition == 0 ? View.VISIBLE : View.GONE);

				View vDivider = view.findViewById(R.id.vDivider);
				vDivider.setVisibility(isLastChild ? View.VISIBLE : View.GONE );

				TextView periods = (TextView) view.findViewById(R.id.periods);
				TextView preRepayMoney = (TextView) view.findViewById(R.id.preRepayMoney);
				TextView repayTime = (TextView) view.findViewById(R.id.repayTime);
				TextView status = (TextView) view.findViewById(R.id.status);
				LinearLayout data = (LinearLayout) view.findViewById(R.id.data);
				TextView tvAheadReturnInfo = (TextView) view.findViewById(R.id.tvAheadReturnInfo);
				TradeFlowRecordInfo.DataListItem.DataListItem2 inListItem = list.get(groupPosition).dataList2.get(childPosition);
				if (inListItem == null ){
					data.setVisibility(View.GONE);
				}else{
					data.setVisibility(View.VISIBLE);
					periods.setText(inListItem.periods);
					preRepayMoney.setText(inListItem.preRepayMoney);
					repayTime.setText(inListItem.repayTime);
					status.setText(inListItem.status);
					if (!TextUtils.isEmpty(inListItem.attribute1)){
						tvAheadReturnInfo.setVisibility(View.VISIBLE);
						tvAheadReturnInfo.setText(inListItem.attribute1+inListItem.attribute2);
					}else{
						tvAheadReturnInfo.setVisibility(View.GONE);
					}

					return view;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		page = 1;
		returnValue =null;
		if (mHttpService ==null){
			mHttpService =new HttpService(this,this);
		}
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
	}
	public void onEventMainThread(ArrayList<TradeFlowRecordInfo.DataListItem> event) {
		if (isFinishing())	return;
		if (event.size()==0 || event== null){
			Toast.makeText(QueryReturnMoneyListActivity.this,"您没有相关数据",Toast.LENGTH_SHORT).show();
		}else{
			for (TradeFlowRecordInfo.DataListItem item : event) {
				if (!allList.contains(item)){
					allList.add(item);
				}
			}
		}
		myExpandableListViewAdapter.setData(allList);
		expandableListView.expandGroup(0);
		if(mRefresh.isRefreshing()) {
			mRefresh.setRefreshing(false);
		}
		isRefreshing = false;
	}

	static class ViewHolder{
		TextView loanTitle;
		TextView recentTradeTime;
		View checkState;
		View ExDivider;
		TextView tenderMoneySum;
		TextView preRepayMoneySum;
		TextView repayMoneySum;
		ListView tradeList;
		ImageView imageState;
		ImageView imgTriangle;
	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		if (!isHttpHandle(json)) return;
		mRefresh.setRefreshing(false);
		if (reqId == ServiceCmd.CmdId.CMD_TradeFlowRecord.ordinal()){
			loading = false;
			//			Log.i("aaa", "CMD_TradeFlowRecord--->" + json.toString());
			TradeFlowRecordInfo tradeFlowRecordInfo = mHttpService.onGetTradeFlowRecord(json);
			if ("true".equals(tradeFlowRecordInfo.success)){
				totalPage = tradeFlowRecordInfo.totalPage;
				returnValue = tradeFlowRecordInfo.returnValue;

				ArrayList<TradeFlowRecordInfo.DataListItem> dataList = tradeFlowRecordInfo.dataList;
				EventBus.getDefault().post(dataList);
				textview.setVisibility(View.GONE);
			}else{
				if (allList != null && allList.size() == 0){
					textview.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	@Override
	public void onHttpError(int reqId, String errmsg) {
		super.onHttpError(reqId,errmsg);
		if(isFinishing()) return;
		if (reqId == ServiceCmd.CmdId.CMD_TradeFlowRecord.ordinal()){
			mRefresh.setRefreshing(false);
			textview.setVisibility(View.VISIBLE);
			loading = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		page = 1;
		returnValue =null;
		EventBus.getDefault().unregister(this);
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
}
