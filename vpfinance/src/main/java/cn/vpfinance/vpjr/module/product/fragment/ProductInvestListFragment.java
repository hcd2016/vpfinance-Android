package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.LoanRecord;
import de.greenrobot.event.EventBus;

/**
 * 产品出借记录
 *
 * @author cheungquentin
 */
public class ProductInvestListFragment extends BaseFragment {

    //    private ListView mListView;
    private TextView mTextView;

    private HttpService mHttpService = null;

    private InvestAdapter mListAdapter;

    private long pid;
    private int type;
    private boolean is_deposit;

    private static final String ARGS_PRODUCT_TYPE = "type";
    private static final String ARGS_PRODUCT_ID = "id";
    private static final String ARGS_IS_DEPOSIT = "is_deposit";
    private static final String ARGS_TOTAL_COUNT = "frequency";
    private static final String ARGS_SERVICE_TIME = "service_time";

    private ArrayList<LoanRecord> allList = new ArrayList<LoanRecord>();

    private SwipeRefreshLayout mRefresh = null;

    private int page = 0;
    private final static int PAGE_SIZE = 10;
    private int totalCount;
    private long serverTime;
    private boolean requesting = false;//

    public static ProductInvestListFragment newInstance(long pid, int type, int frequency, boolean isDeposit, long serviceTime) {
        ProductInvestListFragment frag = new ProductInvestListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PRODUCT_TYPE, type);
        args.putLong(ARGS_PRODUCT_ID, pid);
        args.putBoolean(ARGS_IS_DEPOSIT, isDeposit);
        args.putInt(ARGS_TOTAL_COUNT, frequency);
        args.putLong(ARGS_SERVICE_TIME, serviceTime);
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
            totalCount = args.getInt(ARGS_TOTAL_COUNT);
            is_deposit = args.getBoolean(ARGS_IS_DEPOSIT);
            serverTime = args.getLong(ARGS_SERVICE_TIME);
        }

        mListAdapter = new InvestAdapter(getActivity());
        page = 0;
        request();
    }

    private void request() {
//        if (!requesting) {
//            if (is_deposit) {
//                mHttpService.getProductInvestRecordForDeposit("" + pid, page, PAGE_SIZE, serverTime);// PAGE_SIZE
//            } else {
                mHttpService.getProductInvestRecord("" + pid, page, PAGE_SIZE, serverTime);// PAGE_SIZE
//            }
//        }
        requesting = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_invest_record, null);
//        mListView = (ListView) view.findViewById(R.id.listView);
        mTextView = (TextView) view.findViewById(R.id.textview);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 0;
                request();
            }
        });
//        mListView.setEmptyView(mTextView);
//        mListView.setAdapter(mListAdapter);
//
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (view.getLastVisiblePosition() == view.getCount() - 1) {
//                    request();
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            }
//        });




        mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!requesting) {
                    allList.clear();
                    page = 0;
                    mRefresh.setRefreshing(true);
                    if (is_deposit) {
                        mHttpService.getProductInvestRecordForDeposit("" + pid, 0, PAGE_SIZE, serverTime);// PAGE_SIZE
                    } else {
                        mHttpService.getProductInvestRecord("" + pid, 0, PAGE_SIZE, serverTime);// PAGE_SIZE
                    }
                }
                requesting = true;
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
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {
        if (json == null || !isAdded()) return;

        if (reqId == ServiceCmd.CmdId.CMD_getLoanBidList.ordinal() || reqId == ServiceCmd.CmdId.CMD_getLoanBidListForDeposit.ordinal()) {
            page++;
            requesting = false;
            ArrayList<LoanRecord> loanList = mHttpService.onGetProductInvestRecord(json);
            if (loanList != null && isAdded() && mListAdapter != null) {
                allList.addAll(loanList);
                mListAdapter.setList(allList);
            }
            if (mRefresh.isRefreshing()) {
                mRefresh.setRefreshing(false);
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (mRefresh != null && mRefresh.isRefreshing()) {
            mRefresh.setRefreshing(false);
        }
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
                holder.tvPresell = (TextView) convertView.findViewById(R.id.tvPresell);
                holder.tvVoucheMoney = (TextView) convertView.findViewById(R.id.tvVoucheMoney);
                holder.tv_order_id = (TextView) convertView.findViewById(R.id.tv_order_id);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            LoanRecord product = null;

            try {
                product = list.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (product != null) {
                holder.tvPresell.setVisibility(product.getIsBook() == 1 ? View.VISIBLE : View.GONE);

                if (totalCount > 0 && (totalCount - position > 0)) {
//                    holder.tv_order_id.setText(""+product.getId());
                    holder.tv_order_id.setVisibility(View.VISIBLE);
                    holder.tv_order_id.setText("" + (totalCount - position));
                } else {
                    holder.tv_order_id.setVisibility(View.GONE);
                }

                holder.tvUser.setText((product.getUsername()));
                holder.tvMoney.setText(product.getTendMoney() + "元");
                String payTime = product.getPaytime();
                int pos = payTime.indexOf(" ");
                holder.tvDate.setText(payTime);
//				holder.tvDate.setText(payTime.substring(0, pos));
//				holder.tvTime.setText(payTime.substring(pos, payTime.length()));
                String type = product.getType();
                switch (type) {
                    case "1"://没用券
                        holder.tvVoucheMoney.setVisibility(View.GONE);
                        break;
                    case "2"://代金券
                        holder.tvVoucheMoney.setVisibility(View.VISIBLE);
                        holder.tvVoucheMoney.setText("已抵扣" + product.getVoucherMoney() + "元");
                        Drawable drawable = getResources().getDrawable(R.drawable.voucher);
                        drawable.setBounds(0, 0, Utils.sp2px(mContext, 12), Utils.sp2px(mContext, 12));
                        holder.tvVoucheMoney.setCompoundDrawables(drawable, null, null, null);
                        break;
                    case "3"://加息券
                        holder.tvVoucheMoney.setVisibility(View.VISIBLE);
                        holder.tvVoucheMoney.setText("已加息" + product.getVoucherMoney() + "元");
                        Drawable drawable2 = getResources().getDrawable(R.drawable.interest);
                        drawable2.setBounds(0, 0, Utils.sp2px(mContext, 12), Utils.sp2px(mContext, 12));
                        holder.tvVoucheMoney.setCompoundDrawables(drawable2, null, null, null);
                        break;
                    default:
                        holder.tvVoucheMoney.setVisibility(View.GONE);
                        break;
                }
                /*if (!TextUtils.isEmpty(product.getVoucherMoney()) && Double.parseDouble(product.getVoucherMoney()) > 0) {
					holder.tvVoucheMoney.setVisibility(View.VISIBLE);
					holder.tvVoucheMoney.setText("已抵扣" + product.getVoucherMoney() + "元");
					Drawable drawable= getResources().getDrawable(R.drawable.voucher);
					drawable.setBounds(0, 0, Utils.sp2px(mContext, 12), Utils.sp2px(mContext, 12));
					holder.tvVoucheMoney.setCompoundDrawables(drawable, null, null, null);
				} else {
					holder.tvVoucheMoney.setVisibility(View.GONE);
				}*/
            }

            return convertView;
        }

        private class ViewHolder {
            private TextView tvUser;
            private TextView tvMoney;
            private TextView tvDate;
            private TextView tvTime;
            private TextView tvPresell;
            private TextView tvVoucheMoney;
            private TextView tv_order_id;
        }
    }

//	private String getDealUserName(String userName){
//		StringBuffer result = new StringBuffer();
//		if(userName != null && false == userName.isEmpty()){
//			int len = userName.length();
//			result.append(userName.substring(0, 1));
//			for(int i=1; i<len; i++){
//				result.append("*");
//			}
//		}
//		return result.toString();
//	}


}
