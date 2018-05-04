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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.AddrateTicketTabInfo;

/**
 */
public class AddrateTicketTabFragment extends BaseFragment implements AbsListView.OnScrollListener {

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
    private List<AddrateTicketTabInfo.MapListBean> allList = new ArrayList<>();
    private boolean isLastPage = false;

    public static AddrateTicketTabFragment newInstance(int pageNum, String type) {
        AddrateTicketTabFragment fragment = new AddrateTicketTabFragment();
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
        mHttpService.getAddrateTicketlistTab(mPageNum + "", mType);

        mRefresh = ((SwipeRefreshLayout) view.findViewById(R.id.mRefresh));
        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allList.clear();
                mPageNum = 1;
                mRefresh.setRefreshing(true);
                mHttpService.getAddrateTicketlistTab(1 + "", mType);
            }
        });
        mListView.setOnScrollListener(this);

        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Addrate_Ticket_Tab.ordinal()) {
            if (mRefresh != null)   mRefresh.setRefreshing(false);

            AddrateTicketTabInfo addrateTicketTabInfo = mHttpService.onGetAddrateTicketlistTab(json);

            if (addrateTicketTabInfo == null)   return;

            try{
                int i = Integer.parseInt(addrateTicketTabInfo.getTotalPage());
                totalPage = i;
            }catch (Exception e){
                e.printStackTrace();
            }

            List<AddrateTicketTabInfo.MapListBean> list = addrateTicketTabInfo.getMapList();
            if (list != null)
                allList.addAll(list);

            if (allList == null || allList.size() == 0) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("你还没有加息券哦~");
            } else {
                mTextView.setVisibility(View.GONE);
                adapter.setData(allList);
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
            mHttpService.getAddrateTicketlistTab(mPageNum + "", mType);
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
        private List<AddrateTicketTabInfo.MapListBean> datas;

        public void setData(List<AddrateTicketTabInfo.MapListBean> list) {
            if (list == null)   return;

            this.datas = list;
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
                convertView = View.inflate(getActivity(), R.layout.item_addrate_ticket, null);
                holder = new VoucherViewHolder();
                holder.voucher_bg = (RelativeLayout) convertView.findViewById(R.id.voucher_bg);
                holder.voucher_get = (TextView) convertView.findViewById(R.id.voucher_get);
                holder.voucher_time = (TextView) convertView.findViewById(R.id.voucher_time);
                holder.addrate_info1 = (TextView) convertView.findViewById(R.id.addrate_info1);
                holder.addrate_info2 = (TextView) convertView.findViewById(R.id.addrate_info2);
                holder.addrate_info3 = (TextView) convertView.findViewById(R.id.addrate_info3);
                holder.point1 = (ImageView) convertView.findViewById(R.id.point1);
                holder.point2 = (ImageView) convertView.findViewById(R.id.point2);
                holder.point3 = (ImageView) convertView.findViewById(R.id.point3);
                holder.voucher_money = (TextView) convertView.findViewById(R.id.voucher_money);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                holder.voucherState = (ImageView) convertView.findViewById(R.id.voucherState);
                holder.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);
                convertView.setTag(holder);
            } else {
                holder = (VoucherViewHolder) convertView.getTag();
            }
            if (datas != null && datas.size() != 0) {
                if ("1".equals(mType)) {
//                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_addrate_usable));
                    holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_addrate_header_usable));
                    holder.checkBox.setVisibility(View.GONE);
                } else if ("2".equals(mType)) {
//                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_addrate_nousable));
                    holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_addrate_header_nousable));
                    holder.checkBox.setVisibility(View.GONE);
                } else if ("3".equals(mType)) {
//                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_addrate_nousable));
                    holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_addrate_header_nousable));
                    holder.checkBox.setVisibility(View.GONE);
                }

                AddrateTicketTabInfo.MapListBean bean = datas.get(position);

                if (bean.getVoucherStatus() == 2){
                    holder.ivStatus.setVisibility(View.VISIBLE);
                    holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_addrate_header_nousable));
                }else{
                    holder.ivStatus.setVisibility(View.GONE);
                    if ("1".equals(mType)){
                        holder.voucherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_addrate_header_usable));
                    }
                }

                try{
                    double v = new BigDecimal(bean.getValue()).multiply(new BigDecimal(100)).doubleValue();
                    holder.voucher_money.setText(""+v);
                }catch (Exception e){
                    e.printStackTrace();
                }
                String getWay = bean.getGetWay();
                if (!TextUtils.isEmpty(getWay)){
                    holder.voucher_get.setText(getWay);
                }
                List<String> useRemarks = bean.getUseRemarks();
                if (useRemarks != null){
                    try{
                        if (!TextUtils.isEmpty(useRemarks.get(0))){
                            holder.addrate_info1.setText(useRemarks.get(0));
                            holder.point1.setVisibility(View.VISIBLE);
                        }
                        if (!TextUtils.isEmpty(useRemarks.get(1))){
                            holder.addrate_info2.setText(useRemarks.get(1));
                            holder.point2.setVisibility(View.VISIBLE);
                        }
                        if (!TextUtils.isEmpty(useRemarks.get(2))){
                            holder.addrate_info3.setText(useRemarks.get(2));
                            holder.point3.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                holder.voucher_time.setText("有效期至"+Utils.getDate(bean.getExpiredTm()));
            }

            return convertView;
        }
    }

    static class VoucherViewHolder {
        TextView voucher_money;
        TextView voucher_get;
        TextView voucher_time;
        TextView addrate_info1;
        TextView addrate_info2;
        TextView addrate_info3;
//        TextView addrate_info4;
        ImageView point1;
        ImageView point2;
        ImageView point3;
//        ImageView point4;
        RelativeLayout voucher_bg;
        ImageView voucherState;
        ImageView ivStatus;
        CheckBox checkBox;
    }
}
