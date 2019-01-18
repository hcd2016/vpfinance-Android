package cn.vpfinance.vpjr.module.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.adapter.ProductListAdapter;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.LoanSignListNewBean;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.shenyang.PresellProductActivity;
import cn.vpfinance.vpjr.module.product.transfer.NewTransferProductActivity;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshRecyclerView;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshUtil;
import cn.vpfinance.vpjr.view.pullrefresh.PullRefreshView;
import de.greenrobot.event.EventBus;


/**
 * Created by zzlz13 on 2017/9/11.
 */

//产品列表非智存fragment
public class ProductListFragment extends BaseFragment implements View.OnClickListener {
    OnRecyclerViewChangeListner onRecyclerViewChangeListner;

    public void setOnRecyclerViewChangeListner(OnRecyclerViewChangeListner onRecyclerViewChangeListner) {
        this.onRecyclerViewChangeListner = onRecyclerViewChangeListner;
    }

    @Bind(R.id.textview)
    TextView mTextview;
    @Bind(R.id.pull_refresh_recycler_view)
    PullRefreshRecyclerView mRecyclerView;


    public static final String LIST_TYPE = "list_type";
    @Bind(R.id.refresh)
    SmartRefreshLayout refresh;
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

        PullRefreshUtil.setRefresh(mRecyclerView, false, true);
        mRecyclerView.isMore(true);
        mRecyclerView.setVerticalScrollBarEnabled(true);
//        refresh.setEnableLoadMore(true);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNum = 0;
                isShouldClear = true;
                loadDate();
            }
        });
        refresh.setOnMultiPurposeListener(new OnMultiPurposeListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                if(onRecyclerViewChangeListner != null) {
                    onRecyclerViewChangeListner.onRefrsh();
                }
            }

            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {

            }

            @Override
            public void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int maxDragHeight) {
            }

            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) {

            }

            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            }
        });
        mRecyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean top = mRecyclerView.isTop();
                if (onRecyclerViewChangeListner != null) {
                    onRecyclerViewChangeListner.onScrollChange(recyclerView,top,dx,dy);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
//        refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                pageNum++;
//                loadDate();
//            }
//
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//
//            }
//        });
        refresh.setEnableLoadMore(false);
//        mRecyclerView.setOnPullDownRefreshListener(new PullRefreshView.OnPullDownRefreshListener() {
//            @Override
//            public void onRefresh() {
//                pageNum = 0;
//                isShouldClear = true;
//                loadDate();
//            }
//        });

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
                        if (typeList == Constant.TYPE_REGULAR || typeList == Constant.TYPE_BANK) {
                            if (item.loansign.productType == 3) {
                                PresellProductActivity.goPresellProductActivity(getActivity(), "" + item.loansign.id);
                            } else {
                                NewRegularProductActivity.goNewRegularProductActivity(mContext, item.loansign.id, 0, item.loansignbasic.loanTitle, false);
                            }
                        } else if (typeList == Constant.TYPE_TRANSFER) {
                            NewTransferProductActivity.goNewTransferProductActivity(mContext, item.loansign.id);
                        }
                    }
                }
            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        mHttpService.getLoanSignListNew(typeList, pageNum * PAGE_SIZE, PAGE_SIZE, "");
//            }
//        },2000);
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
            EventBus.getDefault().post(new EventStringModel(EventStringModel.EVENT_PRODUCT_LIST_LOAD_SUCCECC));
//            mRecyclerView.refreshFinish();
            refresh.finishRefresh();
            if (isShouldClear) {
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
                    refresh.finishLoadMore(true);
                    totalData.addAll(listNew.loansigns);
                    mRecyclerView.refreshFinish();
                } else {
                    refresh.finishLoadMoreWithNoMoreData();
                }
                myAdapter.setData(totalData, typeList);
                refresh.finishLoadMoreWithNoMoreData();
            }
            refresh.finishRefresh();
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (!isAdded()) return;
        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_List_New.ordinal()) {
            mRecyclerView.refreshFinish();
            refresh.finishRefresh();
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

    public interface OnRecyclerViewChangeListner {
        void onScrollChange(RecyclerView recyclerView, boolean isTop, int dx, int dy);
        void onRefrsh();
    }
}
