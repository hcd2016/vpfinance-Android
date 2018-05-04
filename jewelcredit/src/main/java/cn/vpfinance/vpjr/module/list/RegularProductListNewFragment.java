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
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.transfer.NewTransferProductActivity;
import cn.vpfinance.vpjr.module.product.shenyang.PresellProductActivity;
import cn.vpfinance.vpjr.adapter.LoanSignListNewAdapter;
import cn.vpfinance.vpjr.gson.LoanSignListNewBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.model.RefreshTab;
import cn.vpfinance.vpjr.util.Common;
import de.greenrobot.event.EventBus;

/**
 */
public class RegularProductListNewFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.listView)
    ListView mListView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.textview)
    TextView mTextview;

    public static final int REGULAR_PRODUCT_LIST = 1; //定期理财 不可更改
    public static final int TRANSFER_PRODUCT_LIST = 2; //债权转让 不可更改
    public static final int BANK_PRODUCT_LIST = 4; // "存管专区" 不可更改
    public static final int POOL_PRODUCT_LIST = 5; // "智存投资"
    public static final String LIST_TYPE = "list_type";
    public int typeList;
    private final static int PAGE_SIZE = 10;
    private int page = 0;
    private boolean isRefreshing = false;
    private boolean isLastPage = false;
    private HttpService mHttpService;
    private LoanSignListNewAdapter mAdapter;
    private List<LoanSignListNewBean.LoansignsBean> totalData = new ArrayList<>();
    private boolean isFirstRequest = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        isFirstRequest = true;
    }

    public static RegularProductListNewFragment getInstance(int listType){
        RegularProductListNewFragment fragment = new RegularProductListNewFragment();
        Bundle args = new Bundle();
        args.putInt(LIST_TYPE,listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null){
            typeList = bundle.getInt(LIST_TYPE);
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_regular_list_new, container, false);
        ButterKnife.bind(this, view);
        mHttpService = new HttpService(mContext,this);

        mTextview.setOnClickListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1 && !isRefreshing && !isLastPage) {
                    page++;
                    mHttpService.getLoanSignListNew(typeList,page*PAGE_SIZE,PAGE_SIZE,"");
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter == null)   return;

                LoanSignListNewBean.LoansignsBean item = mAdapter.getItem(position);
                if (item != null && item.loansign != null) {
                    if (isAdded()) {
                        if (typeList == REGULAR_PRODUCT_LIST){
                            if (item.loansign.productType == 3) {
                                PresellProductActivity.goPresellProductActivity(getActivity(), "" + item.loansign.id);
                            } else {
                                NewRegularProductActivity.goNewRegularProductActivity(mContext,item.loansign.id,0,item.loansignbasic.loanTitle,false,0);
                            }
                        }else if (typeList == TRANSFER_PRODUCT_LIST){
                            NewTransferProductActivity.goNewTransferProductActivity(mContext, item.loansign.id);
                        }else if (typeList == BANK_PRODUCT_LIST){
                            NewRegularProductActivity.goNewRegularProductActivity(mContext,item.loansign.id,0,item.loansignbasic.loanTitle,false,1);
                        }
                    }
                }
            }
        });

        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                clearData();
                mHttpService.getLoanSignListNew(typeList,page*PAGE_SIZE,PAGE_SIZE,"");
            }
        });
        if (isFirstRequest){
            clearData();
            mHttpService.getLoanSignListNew(typeList,page*PAGE_SIZE,PAGE_SIZE,"");
            isFirstRequest = false;
        }
        mAdapter = new LoanSignListNewAdapter(mContext);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_List_New.ordinal()){
            isRefreshing = false;
            mRefresh.setRefreshing(false);
            LoanSignListNewBean listNew = mHttpService.onGetLoanSignListNew(json);
            if (listNew == null){
                mTextview.setText("暂无数据");
                mTextview.setVisibility(View.VISIBLE);
            }else{
                mTextview.setVisibility(View.GONE);
                if (listNew.loansigns != null){
                    isLastPage = listNew.total <= (page+1) * PAGE_SIZE;
                    totalData.addAll(listNew.loansigns);
                }
                mAdapter.setData(totalData,typeList);
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_List_New.ordinal()){
            if(mRefresh != null){
                mRefresh.setRefreshing(false);
            }
            isRefreshing = false;
        }
    }

    public void onEventMainThread(RefreshTab event) {
        if (event != null && isAdded() && event.tabType == RefreshTab.TAB_LIST){
            isFirstRequest = false;
            clearData();
//            Logger.e("event.listType:"+event.listType);
            mHttpService.getLoanSignListNew(typeList,page*PAGE_SIZE,PAGE_SIZE,"");
        }
    }

    public void onEventMainThread(RefreshCountDown event){
        if (event != null && event.isRefresh == true && isAdded()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (typeList == REGULAR_PRODUCT_LIST){
//                        EventBus.getDefault().post(new ChangeListType(REGULAR_PRODUCT_LIST));
                        clearData();
                        mHttpService.getLoanSignListNew(typeList,page*PAGE_SIZE,PAGE_SIZE,"");
                    }
//                    typeList = REGULAR_PRODUCT_LIST;

                }
            }, Constant.delay);
        }
    }

    private void clearData(){
        mRefresh.setRefreshing(true);
        page = 0 ;
        totalData.clear();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview:
                clearData();
                mHttpService.getLoanSignListNew(typeList,page*PAGE_SIZE,PAGE_SIZE,"");
                break;
        }
    }
}
