package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.LoanRecord;
import cn.vpfinance.vpjr.view.InsideRelativeLayout;
import de.greenrobot.event.EventBus;

/**
 */
public class PresellProductInvestListFragment extends BaseFragment {

    private ListView mListView;
    private TextView mTextView;

    private HttpService mHttpService = null;

    private InvestAdapter mListAdapter;

    private long pid;
    private int type;

    private static final String ARGS_PRODUCT_TYPE = "type";
    private static final String ARGS_PRODUCT_ID = "id";

    private ArrayList<LoanRecord> allList = new ArrayList<LoanRecord>();

//    private SwipeRefreshLayout mRefresh = null;

    private int page;
    private boolean isRefreshing = false;
    private boolean isLastPage = false;
    private final static int PAGE_SIZE = 20;
    private InsideRelativeLayout rootView;
    private long time;

    public static PresellProductInvestListFragment newInstance(long pid, int type) {
        PresellProductInvestListFragment frag = new PresellProductInvestListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PRODUCT_TYPE, type);
        args.putLong(ARGS_PRODUCT_ID, pid);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHttpService = new HttpService(getActivity(), this);

        Bundle args = getArguments();
        if (args != null) {
            pid = args.getLong(ARGS_PRODUCT_ID);
            type = args.getInt(ARGS_PRODUCT_TYPE);
        }
        time = System.currentTimeMillis();
        mListAdapter = new InvestAdapter(getActivity());
        mHttpService.getProductInvestRecord("" + pid, 0, PAGE_SIZE,time);// PAGE_SIZE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presell_regular_list, null);
        rootView = ((InsideRelativeLayout) view.findViewById(R.id.rootView));

        mListView = (ListView) view.findViewById(R.id.listView);
        mTextView = (TextView) view.findViewById(R.id.textview);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHttpService.getProductInvestRecord("" + pid, 0, PAGE_SIZE,time);// PAGE_SIZE
            }
        });
        mListView.setEmptyView(mTextView);
        mListView.setAdapter(mListAdapter);


        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1 && !isRefreshing && !isLastPage) {
                    page++;
                    isRefreshing = true;
//                    mRefresh.setRefreshing(true);
                    mHttpService.getProductInvestRecord("" + pid, page, PAGE_SIZE,time);// PAGE_SIZE
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

//        mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
//        mRefresh.setColorSchemeResources(R.color.main_color);

//        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                allList.clear();
//                isLastPage = false;
//                page = 0;
//                isRefreshing = true;
//                mRefresh.setRefreshing(true);
//                mHttpService.getProductInvestRecord("" + pid, page, PAGE_SIZE);// PAGE_SIZE
//            }
//        });

        return view;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId,json);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {
        if (reqId == ServiceCmd.CmdId.CMD_getLoanBidList.ordinal()) {
            ArrayList<LoanRecord> loanList = mHttpService.onGetProductInvestRecord(json);
            if(loanList!=null && isAdded() && mListAdapter!=null)
            {
                allList.addAll(loanList);
                mListAdapter.setList(allList);
            }
            if((loanList==null || loanList.size()<PAGE_SIZE )&& page>0)
            {
                isLastPage = true;
            }
//            if(mRefresh.isRefreshing()) {
//                mRefresh.setRefreshing(false);
//            }
            isRefreshing = false;
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
//        if(mRefresh!=null && mRefresh.isRefreshing()) {
//            mRefresh.setRefreshing(false);
//        }
        isRefreshing = false;
        super.onHttpError(reqId, errmsg);
    }

    class InvestAdapter extends BaseAdapter {
        private static final String TAG = "InvestAdapter";

        private List<LoanRecord> list;

        private Context mContext;
        private LayoutInflater mInflater;

        public InvestAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list == null ? null : list.get(position);
        }

        public void setList(List<LoanRecord> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_product_invest, null);
                holder.tvUser = (TextView) convertView.findViewById(R.id.tvUser);
                holder.tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final LoanRecord product = list.get(position);

            if (product != null) {
//				holder.tvUser.setText(getDealUserName(product.getUsername()));
                holder.tvUser.setText((product.getUsername()));
                holder.tvMoney.setText(product.getTendMoney() + "元");
                String payTime = product.getPaytime();
                int pos = payTime.indexOf(" ");
                holder.tvDate.setText(payTime.substring(0, pos));
                holder.tvTime.setText(payTime.substring(pos, payTime.length()));
            }

            return convertView;
        }

        private class ViewHolder {
            private TextView tvUser;
            private TextView tvMoney;
            private TextView tvDate;
            private TextView tvTime;
        }
    }
}
