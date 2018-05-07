package cn.vpfinance.vpjr.module.list;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.adapter.LoanSignDepositListAdapter;
import cn.vpfinance.vpjr.gson.LoanSignDepositBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.model.RefreshTab;
import cn.vpfinance.vpjr.util.Common;
import de.greenrobot.event.EventBus;

/**
 * 定存宝产品列表fragment
 * Created by zzlz13 on 2017/4/13.
 */

public class DepositProductListNewFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.listView)
    ListView mListView;

    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.textview)
    TextView mTextview;

    private LoanSignDepositListAdapter mAdapter;
    private HttpService mHttpService;
    private int page = 0;
    private static final int PAGE_SIZE = 10;
    private List<LoanSignDepositBean.LoansignpoolBean> datas = new ArrayList<>();
    private boolean isFirstRequest = true;

    public static DepositProductListNewFragment getInstance(){
        DepositProductListNewFragment fragment = new DepositProductListNewFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        isFirstRequest = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(RefreshTab event) {
        if (event != null && isAdded() && event.tabType == RefreshTab.TAB_LIST){
            isFirstRequest = false;
            refresh();
        }
    }

    public void onEventMainThread(RefreshCountDown event) {
        if (event != null && isAdded() && event.isRefresh){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            }, Constant.delay);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_regular_list_new, container, false);
        ButterKnife.bind(this, view);
        mHttpService = new HttpService(mContext,this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    page++;
                    mHttpService.getLoanSignPool(page*PAGE_SIZE,PAGE_SIZE,"");
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mListView.setOnItemClickListener(this);
        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        if (isFirstRequest){
            refresh();
            isFirstRequest = false;
        }
        mAdapter = new LoanSignDepositListAdapter(mContext);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Pool.ordinal()){
            mRefresh.setRefreshing(false);
            //isLastPage
            LoanSignDepositBean loanSignDepositBean = mHttpService.onGetLoanSignPool(json);
            if (loanSignDepositBean == null){
                mTextview.setVisibility(View.VISIBLE);
                return;
            }
            if (loanSignDepositBean != null && loanSignDepositBean.loansignpool != null && loanSignDepositBean.loansignpool.size() != 0){//有数据
                datas.addAll(loanSignDepositBean.loansignpool);
                mAdapter.setData(datas);
                mTextview.setVisibility(View.GONE);
            }else if (datas.size() == 0){
                mTextview.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (!isAdded()) return;
        mRefresh.setRefreshing(false);
//        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Pool.ordinal()){
//            mTextview.setVisibility(View.VISIBLE);
//        }
    }

    @OnClick({R.id.textview})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview:
                refresh();
                break;
        }
    }

    /**
     * 刷新列表
     */
    private void refresh(){
        mRefresh.setRefreshing(true);
        page = 0;
        datas.clear();
        mHttpService.getLoanSignPool(0,PAGE_SIZE,"");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LoanSignDepositBean.LoansignpoolBean item = mAdapter.getItem(position);
        if (item != null && item.id != 0){
            int pid = item.id;
            String loanTitle = item.loanTitle;
            NewRegularProductActivity.goNewRegularProductActivity(mContext,pid,0,loanTitle,true);
        }
    }
}
