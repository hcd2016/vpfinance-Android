package cn.vpfinance.vpjr.module.voucher.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.model.Voucher;
import cn.vpfinance.vpjr.model.VoucherArray;

/**
 */
public class VoucherTabFragment extends BaseFragment implements AbsListView.OnScrollListener {

    @Bind(R.id.voucher_info)
    TextView mVoucherInfo;
    @Bind(R.id.voucherLV)
    ListView mListView;
    @Bind(R.id.textview)
    TextView mTextView;
    @Bind(R.id.mRefresh)
    SwipeRefreshLayout mRefresh;

    private Context mContext;
    public static final String PAGE_NUM = "pageNum";
    public static final String TYPE = "type";
    private MyAdapter adapter;
    private HttpService mHttpService;
    private String mType = "1";
    private int mPageNum = 1;
    private int totalPage;
    private ArrayList<Voucher> allVoucherList = new ArrayList<Voucher>();
    private boolean isLastPage = false;

    public static VoucherTabFragment newInstance(int pageNum, String type) {
        VoucherTabFragment fragment = new VoucherTabFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        args.putInt(PAGE_NUM, pageNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.fragment_voucher_tab, null);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle == null) return view;
        mType = bundle.getString(TYPE);
        mPageNum = bundle.getInt(PAGE_NUM);

        adapter = new MyAdapter();
        mListView.setAdapter(adapter);

        mHttpService = new HttpService(getActivity(), this);
        mHttpService.getVoucherlistTab(mPageNum + "", mType);

        mRefresh = ((SwipeRefreshLayout) view.findViewById(R.id.mRefresh));
        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allVoucherList.clear();
                mPageNum = 1;
                mRefresh.setRefreshing(true);
                mHttpService.getVoucherlistTab(1 + "", mType);
            }
        });
        mListView.setOnScrollListener(this);

        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_VOUCHERLIST_TAB.ordinal()) {
            if (json != null|| !isAdded()) {

                String totalMoney = json.optString("totalMoney");
                if ("1".equals(mType)) {
                    mVoucherInfo.setText("当前可使用代金券" + totalMoney + "元");
                } else if ("2".equals(mType)) {
                    mVoucherInfo.setText("已使用代金券" + totalMoney + "元");
                } else if ("3".equals(mType)) {
                    mVoucherInfo.setText("累计已过期代金券" + totalMoney + "元");
                }


                mRefresh.setRefreshing(false);
                VoucherArray voucherArray = mHttpService.onVoucherArray(json);
                try {
                    totalPage = Integer.parseInt(json.optString("totalPage"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ArrayList<Voucher> voucherList = voucherArray.getVoucherList();
                if (voucherList != null && voucherList.size() != 0) {
                    allVoucherList.addAll(voucherList);
                }
                if (allVoucherList == null || allVoucherList.size() == 0) {
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText("你还没有代金券哦~");
                } else {
                    mTextView.setVisibility(View.GONE);
                    adapter.setData(allVoucherList);
                }
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        if (mRefresh != null){
            mRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (view.getLastVisiblePosition() == view.getCount() - 1) {
            mPageNum++;
            isLastPage = mPageNum > totalPage ? true : false;
            if (isLastPage) return;
            mHttpService.getVoucherlistTab(mPageNum + "", mType);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.useInfo)
    public void onClick() {
        gotoWeb("/AppContent/voucher", "优惠券");
    }

    class MyAdapter extends BaseAdapter {
        private ArrayList<Voucher> datas;

        public void setData(ArrayList<Voucher> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas == null ? null : datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            VoucherViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_voucher_ticket, null);
                holder = new VoucherViewHolder();
                holder.voucher_bg = (RelativeLayout) convertView.findViewById(R.id.voucher_bg);
                holder.voucher_time = (TextView) convertView.findViewById(R.id.voucher_time);
                holder.voucher_info = (TextView) convertView.findViewById(R.id.voucher_info);
                holder.voucher_money = (TextView) convertView.findViewById(R.id.voucher_money);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                holder.voucherState = (ImageView) convertView.findViewById(R.id.voucherState);
                holder.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);
                convertView.setTag(holder);
            } else {
                holder = (VoucherViewHolder) convertView.getTag();
            }
            if (datas != null && datas.size() != 0) {
                final Voucher voucher = datas.get(position);
                if ("未使用".equals(voucher.getName()) || "可使用".equals(voucher.getName())) {
//                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_usable));
                    holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_header_usable));
                    holder.checkBox.setVisibility(View.GONE);
                } else if ("已过期".equals(voucher.getName())) {
//                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_nousable));
                    holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                    holder.checkBox.setVisibility(View.GONE);
                } else if ("已使用".equals(voucher.getName())) {
//                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_nousable));
                    holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                    holder.checkBox.setVisibility(View.GONE);
                }
                if (voucher.getVoucherStatus() == 2){//冻结置灰
                    holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                    holder.ivStatus.setVisibility(View.VISIBLE);
                }else{
                    if ("1".equals(mType)){
                        holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_header_usable));
                    }
                    holder.ivStatus.setVisibility(View.GONE);
                }
                holder.voucher_money.setText("" + voucher.getAmount());
                holder.voucher_info.setText(TextUtils.isEmpty(voucher.getUseRuleExplain()) ? "" : voucher.getUseRuleExplain());
                holder.voucher_time.setText("有效期至"+Utils.getDate(voucher.getExpireDate()));
            }

            return convertView;
        }
    }

    static class VoucherViewHolder {
        TextView voucher_money;
        TextView voucher_time;
        TextView voucher_info;
        RelativeLayout voucher_bg;
        ImageView voucherState;
        CheckBox checkBox;
        ImageView ivStatus;
    }
}
