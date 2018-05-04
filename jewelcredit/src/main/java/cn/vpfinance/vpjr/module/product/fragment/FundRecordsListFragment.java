package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ExpandListView;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.MyInvestRecord;
import cn.vpfinance.vpjr.util.Common;
import de.greenrobot.event.EventBus;

public class FundRecordsListFragment extends BaseFragment implements AdapterView.OnItemClickListener{

	private static final String ARGS_PRODUCT_LOAN_TYPE = "loanType";
	private ExpandListView mListView;
	private LinearLayout emptyLayout;
	private HttpService mHttpService = null;
	private String loanType;
	private RecordAdapter mRecordAdapter;


	private int page = 1;
	private boolean isRefreshing = false;
	private boolean isLastPage = false;
	private final static int PAGE_SIZE = 10;

	private SwipeRefreshLayout mRefresh = null;

	public static FundRecordsListFragment newInstance(String loanType) {
		FundRecordsListFragment frag = new FundRecordsListFragment();
		Bundle args = new Bundle();
		args.putString(ARGS_PRODUCT_LOAN_TYPE, loanType);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHttpService = new HttpService(getActivity(), this);

		Bundle args = getArguments();
		if (args != null) {
			loanType = args.getString(ARGS_PRODUCT_LOAN_TYPE);
		}
		mRecordAdapter = new RecordAdapter(getActivity());

		String beginTime = "";
		String endTime = "";
		mHttpService.getInvestRecord(loanType, "" + page, beginTime, endTime);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fund_records_list, null);
		mListView = (ExpandListView) view.findViewById(R.id.listRecord);
		emptyLayout = (LinearLayout) view.findViewById(R.id.layout_data_empty);
		mListView.setAdapter(mRecordAdapter);
		mListView.setOnItemClickListener(this);


		mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
		mRefresh.setColorSchemeResources(R.color.main_color);
		mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isRefreshing = true;
				page = 1;
				String beginTime = "";
				String endTime = "";
				mHttpService.getInvestRecord(loanType, "" + page, beginTime, endTime);
			}
		});


		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == view.getCount() - 1 && !isRefreshing && !isLastPage) {
					isRefreshing = true;
					page++;
					mRefresh.setRefreshing(true);
					String beginTime = "";
					String endTime = "";
					mHttpService.getInvestRecord(loanType, "" + page, beginTime, endTime);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});



		return view;
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onHttpSuccess(int reqId, JSONArray json) {
		super.onHttpSuccess(reqId, json);
	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		if (!isHttpHandle(json)) return;
		if (reqId == ServiceCmd.CmdId.CMD_INVEST_RECORD.ordinal()) {
			ArrayList<MyInvestRecord> list = new ArrayList<MyInvestRecord>();
			String msg = mHttpService.onGetRecord(json, list);
			if (msg.contains("成功")) {
				if (mRecordAdapter != null) {
					mRecordAdapter.setList(list);
					updateEmptyData(list == null || list.size() == 0);
				}
				if(mRefresh.isRefreshing()) {
					mRefresh.setRefreshing(false);
				}
				isRefreshing = false;
			} else {
				updateEmptyData(true);
			}
		}
	}

	@Override
	public void onHttpError(int reqId, String errmsg) {
		super.onHttpError(reqId, errmsg);
		if(mRefresh.isRefreshing()) {
			mRefresh.setRefreshing(false);
		}
		isRefreshing = false;
		updateEmptyData(true);
	}

	private void updateEmptyData(boolean empty) {
		if (empty) {
			emptyLayout.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		} else {
			emptyLayout.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		if(View.INVISIBLE == viewHolder.expandlayout.getVisibility()){
			viewHolder.imageState.setBackgroundResource(R.drawable.list_arrow_down);
		}else{
			viewHolder.imageState.setBackgroundResource(R.drawable.list_arrow_up);
		}
		mListView.startExpandAnimation(viewHolder.expandlayout, position);
	}

	class RecordAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<MyInvestRecord> list;

		RecordAdapter(Context context) {
			this.context = context;
		}

		public void setList(ArrayList<MyInvestRecord> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_vip_fund_record, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.payType = (TextView) convertView.findViewById(R.id.payType);
				holder.rate = (TextView) convertView.findViewById(R.id.rate);
				holder.imageState = (ImageView) convertView.findViewById(R.id.imageState);
				holder.expandlayout = (LinearLayout) convertView.findViewById(R.id.expandlayout);
				holder.term = (TextView) convertView.findViewById(R.id.term);
				holder.borrowAmount = (TextView) convertView.findViewById(R.id.borrowAmount);
				holder.investTime = (TextView) convertView.findViewById(R.id.investTime);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			LinearLayout.LayoutParams buyLayoutParams = (LinearLayout.LayoutParams) holder.expandlayout.getLayoutParams();
			buyLayoutParams.height = 180;
			buyLayoutParams.bottomMargin = -180;
			holder.expandlayout.setVisibility(View.INVISIBLE);
			MyInvestRecord record = list.get(position);
			if (record != null) {
				holder.title.setText(record.getLoanTitle());
				holder.payType.setText(record.getRefundWay());
				holder.rate.setText(String.format("%.2f", record.getInterestRate()) + "%");
				holder.term.setText(record.getDeadline());
				holder.borrowAmount.setText(String.format("%.2f", record.getTenderMoney()));
				holder.investTime.setText(record.getTenderTime());
			}
			convertView.setTag(holder);
			return convertView;
		}
	}

	public class ViewHolder {
		TextView title;
		TextView investTime;
		ImageView imageState;
		LinearLayout expandlayout;
		TextView borrowAmount;
		TextView payType;
		TextView rate;
		TextView term;
	}
}
