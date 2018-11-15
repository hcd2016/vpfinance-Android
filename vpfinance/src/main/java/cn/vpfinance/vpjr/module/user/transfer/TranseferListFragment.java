package cn.vpfinance.vpjr.module.user.transfer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.NewBaseFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.NewRecordsBean;
import cn.vpfinance.vpjr.util.DBUtils;
import de.greenrobot.dao.DbUtils;

/**
 * V4.0我要转让 可转让,转让中,已完成 列表 统一fragment
 */
public class TranseferListFragment extends NewBaseFragment {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout refresh;
    private HttpService httpService;
    private int transeferType = 1; //转让类型,0为可转让,1位转让中,2位已完成, //todo 状态待改
    private Long mUserId;
    private List<NewRecordsBean.RecordListEntity> list;
    private TranseferListAdapter transeferListAdapter;
    private int page = 1;
    private int page_size = 10;


    public static TranseferListFragment newInstance(int transeferType){
        TranseferListFragment fragment = new TranseferListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("transeferType",transeferType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_transefer_list, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }


    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null){
            transeferType = bundle.getInt("transeferType");
        }
        User user = DBUtils.getUser(getActivity());
        if (user != null) {
            mUserId = user.getUserId();
        }
        list = new ArrayList();
        httpService = new HttpService(getActivity(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        transeferListAdapter = new TranseferListAdapter(list);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                list.clear();
                refresh.setRefreshing(true);
                httpService.getNewFundRecord(mUserId + "", page + "", page_size + "", transeferType + "", 1);
            }
        });
        refresh.setColorSchemeColors(Utils.getColor(R.color.red_text2));
        transeferListAdapter.setEnableLoadMore(true);
        transeferListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                httpService.getNewFundRecord(mUserId + "", page + "", page_size + "", transeferType + "", 1);
            }
        }, recyclerView);
        recyclerView.setAdapter(transeferListAdapter);
        httpService.getNewFundRecord(mUserId + "", page + "", page_size + "", transeferType + "", 1);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        refresh.setRefreshing(false);
        if (reqId == ServiceCmd.CmdId.CMD_FundRecord.ordinal()) {
            NewRecordsBean newRecordsBean = httpService.onGetNewFundRecord(json);
            if (newRecordsBean == null) return;
            List<NewRecordsBean.RecordListEntity> recordList = newRecordsBean.getRecordList();
            if (recordList != null && recordList.size() != 0) {
                list.addAll(recordList);
                transeferListAdapter.notifyDataSetChanged();
                if (recordList.size() < page_size) {
                    transeferListAdapter.loadMoreEnd();
                } else {
                    transeferListAdapter.loadMoreComplete();
                }
            } else {
                transeferListAdapter.loadMoreEnd();
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        refresh.setRefreshing(false);
        transeferListAdapter.loadMoreEnd();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class TranseferListAdapter extends BaseQuickAdapter<NewRecordsBean.RecordListEntity, BaseViewHolder> {
        public TranseferListAdapter(@Nullable List<NewRecordsBean.RecordListEntity> data) {
            super(R.layout.item_transefer_list, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, NewRecordsBean.RecordListEntity item) {
            TextView tvMonth = helper.getView(R.id.tvMonth);
            //月份显示判断,上一个item的月份与本item一样时,隐藏月份,否则,显示月份
            if(helper.getLayoutPosition() != 0 && getData().get(helper.getLayoutPosition()).getTenderMonth().equals(item.getTenderMonth())) {
                tvMonth.setVisibility(View.GONE);
            }else {
                tvMonth.setVisibility(View.VISIBLE);
                tvMonth.setText(item.getTenderMonth()+"月出借");
            }

            helper.setText(R.id.tvTitle,item.getTitle());
            helper.setText(R.id.tvMoney,item.getTenderMoney()+"元");
            if(transeferType == 0) {
                helper.setText(R.id.tv_earnings_desc,"可转让本金");
            }else {
                helper.setText(R.id.tv_earnings_desc,"转让本金");
            }

//            viewHolder.tvMonth = (TextView) convertView.findViewById(R.id.tvMonth);
//            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
//            viewHolder.tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
//            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
//            viewHolder.tv_earnings_desc = (TextView) convertView.findViewById(R.id.tv_earnings_desc);
////                viewHolder.tvSellState = (TextView) convertView.findViewById(R.id.tvSellState);
////                viewHolder.tvUseVoucher = (TextView) convertView.findViewById(R.id.tvUseVoucher);
////                viewHolder.interest = (ImageView) convertView.findViewById(R.id.interest);
//            viewHolder.ll_share = (LinearLayout) convertView.findViewById(R.id.ll_share);
//            viewHolder.ll_month = (LinearLayout) convertView.findViewById(R.id.ll_month);
        }
    }
}
