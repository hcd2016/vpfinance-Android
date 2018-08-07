package cn.vpfinance.vpjr.module.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.adapter.ProductDepositListAdapter;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.LoanSignDepositBean;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshRecyclerView;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshUtil;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshView;

/**
 * Created by zzlz13 on 2017/9/11.
 */

public class ProductDepositListFragment extends BaseFragment {

    @Bind(R.id.pull_refresh_recycler_view)
    PullRefreshRecyclerView mRecyclerView;
    @Bind(R.id.textview)
    TextView mTextview;

    private HttpService mHttpService;
    private int pageNum = 0;
    private static final int PAGE_SIZE = 10;
    private List<LoanSignDepositBean.LoansignpoolBean> datas = new ArrayList<>();
    private boolean isShouldClear = false;
    private ProductDepositListAdapter myAdapter;

    public static ProductDepositListFragment getInstance() {
        ProductDepositListFragment fragment = new ProductDepositListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mHttpService = new HttpService(mContext, this);
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_product_deposit_list, container, false);
        ButterKnife.bind(this, view);
        mTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });

        mRecyclerView.setDamp(3);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myAdapter = new ProductDepositListAdapter(getContext());
        mRecyclerView.setAdapter(myAdapter);

        PullRefreshUtil.setRefresh(mRecyclerView, true, true);
        mRecyclerView.isMore(true);
        mRecyclerView.setVerticalScrollBarEnabled(true);

        mRecyclerView.setOnPullDownRefreshListener(new PullRefreshView.OnPullDownRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 0;
                isShouldClear = true;
                loadDate();
            }
        });
        mRecyclerView.setOnPullUpRefreshListener(new PullRefreshView.OnPullUpRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum++;
                loadDate();
            }
        });
        myAdapter.setOnItemClickListener(new ProductDepositListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LoanSignDepositBean.LoansignpoolBean item) {
                if (item != null && item.id != 0){
                    int pid = item.id;
                    String loanTitle = item.loanTitle;
                    NewRegularProductActivity.goNewRegularProductActivity(mContext,pid,0,loanTitle,true);
                }
            }
        });
        loadDate();
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Pool.ordinal()) {
            mRecyclerView.refreshFinish();
            LoanSignDepositBean loanSignDepositBean = mHttpService.onGetLoanSignPool(json);
            if (loanSignDepositBean != null && loanSignDepositBean.loansignpool != null && loanSignDepositBean.loansignpool.size() != 0) {//有数据
                if (isShouldClear) {
                    clearData();
                    isShouldClear = false;
                }
                datas.addAll(loanSignDepositBean.loansignpool);
                myAdapter.setData(datas);
                mTextview.setVisibility(View.GONE);
            } else if (datas.size() == 0) {
                mTextview.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.isMore(false);
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (!isAdded()) return;
        mRecyclerView.refreshFinish();
    }

    private void clearData() {
        pageNum = 0;
        isShouldClear = false;
        datas.clear();
    }

    @Override
    protected void loadDate() {
        mHttpService.getLoanSignPool(pageNum * PAGE_SIZE, PAGE_SIZE, "");
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        LoanSignDepositBean.LoansignpoolBean item = mAdapter.getItem(position);
//        if (item != null && item.id != 0) {
//            int pid = item.id;
//            String loanTitle = item.loanTitle;
//            NewRegularProductActivity.goNewRegularProductActivity(mContext, pid, 0, loanTitle, true, 0);
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
