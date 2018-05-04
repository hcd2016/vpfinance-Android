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
import cn.vpfinance.vpjr.adapter.ProductListAdapter;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.LoanSignListNewBean;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.shenyang.PresellProductActivity;
import cn.vpfinance.vpjr.module.product.transfer.NewTransferProductActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshRecyclerView;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshUtil;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshView;


/**
 * Created by zzlz13 on 2017/9/11.
 */

public class ProductListFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.textview)
    TextView mTextview;
    @Bind(R.id.pull_refresh_recycler_view)
    PullRefreshRecyclerView mRecyclerView;


    public static final String LIST_TYPE = "list_type";
    private int typeList;
    private List<LoanSignListNewBean.LoansignsBean> totalData = new ArrayList<>();
    private HttpService mHttpService;
    private final static int PAGE_SIZE = 10;
    private int pageNum = 0;
    private ProductListAdapter myAdapter;
    private boolean isShouldClear = false;

    public static ProductListFragment getInstance(int listType) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putInt(LIST_TYPE, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            typeList = bundle.getInt(LIST_TYPE);
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_product_list, container, false);
        ButterKnife.bind(this, view);
        mHttpService = new HttpService(mContext, this);

        mTextview.setOnClickListener(this);

        mRecyclerView.setDamp(3);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myAdapter = new ProductListAdapter(getContext());
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
        myAdapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LoanSignListNewBean.LoansignsBean item) {
                if (item != null && item.loansign != null) {
                    if (isAdded()) {
                        if (typeList == RegularProductListNewFragment.REGULAR_PRODUCT_LIST){
                            if (item.loansign.productType == 3) {
                                PresellProductActivity.goPresellProductActivity(getActivity(), "" + item.loansign.id);
                            } else {
                                NewRegularProductActivity.goNewRegularProductActivity(mContext,item.loansign.id,0,item.loansignbasic.loanTitle,false,0);
                            }
                        }else if (typeList == RegularProductListNewFragment.TRANSFER_PRODUCT_LIST){
                            NewTransferProductActivity.goNewTransferProductActivity(mContext, item.loansign.id);
                        }else if (typeList == RegularProductListNewFragment.BANK_PRODUCT_LIST){
                            NewRegularProductActivity.goNewRegularProductActivity(mContext,item.loansign.id,0,item.loansignbasic.loanTitle,false,1);
                        }
                    }
                }
            }
        });

        loadDate();

        return view;
    }

    @Override
    protected void loadDate() {
        mHttpService.getLoanSignListNew(typeList, pageNum * PAGE_SIZE, PAGE_SIZE, "");
    }

    private void clearData() {
        pageNum = 0;
        isShouldClear = true;
        totalData.clear();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_List_New.ordinal()) {
            mRecyclerView.refreshFinish();
            if (isShouldClear){
                clearData();
                isShouldClear = false;
            }
            LoanSignListNewBean listNew = mHttpService.onGetLoanSignListNew(json);
            if (listNew == null) {
                mTextview.setText("暂无数据");
                mTextview.setVisibility(View.VISIBLE);
            } else {
                mTextview.setVisibility(View.GONE);
                if (listNew.loansigns != null) {
                    boolean isMore = listNew.total > (pageNum + 1) * PAGE_SIZE;
                    mRecyclerView.isMore(isMore);
                    totalData.addAll(listNew.loansigns);
                    mRecyclerView.refreshFinish();
                }
                myAdapter.setData(totalData, typeList);
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (!isAdded()) return;
        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_List_New.ordinal()){
            mRecyclerView.refreshFinish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview:
                clearData();
                mHttpService.getLoanSignListNew(typeList, pageNum * PAGE_SIZE, PAGE_SIZE, "");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        mRecyclerView.refreshFinish();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
