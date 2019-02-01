package cn.vpfinance.vpjr.module.product.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.ui.widget.BaseRefreshHeaderView;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.LoanRecord;

/**
 * Created by zhangqingtian on 16/2/2.
 * 出借记录
 */
public class ProductInvestListActivity extends BaseActivity {

    public static final String KEY_PID = "pid";
    public static final String KEY_TYPE = "type";
    public static final String KEY_IS_DEPOSIT = "isDeposit";
    public static final String KEY_TOTAL_COUNT = "frequency";
    public static final String SERVICE_TIME = "serverTime";
    @Bind(R.id.line_gray)
    View lineGray;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout refresh;
    @Bind(R.id.textview)
    TextView textview;
    @Bind(R.id.tv_loan_people_num)
    TextView tvLoanPeopleNum;
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    private int totalCount = 0;
    private long serverTime;
    private int page = 0;
    private int limit = 10;//默认分页
    private List<LoanRecord> list;
    private HttpService mHttpService;
    private long pid;
    private InvestRecordAdapter investRecordAdapter;

    public static void goProductInvestListActivity(Context context, long pid, int type, int frequency, boolean is) {
        if (context != null) {
            Intent intent = new Intent(context, ProductInvestListActivity.class);
            intent.putExtra(KEY_PID, pid);
            intent.putExtra(KEY_TYPE, type);
            intent.putExtra(KEY_IS_DEPOSIT, is);
            intent.putExtra(KEY_TOTAL_COUNT, frequency);
            context.startActivity(intent);
        }
    }

    public static void goProductInvestListActivity(Context context, long pid, int type, int frequency, boolean is, long serviceTime) {
        if (context != null) {
            Intent intent = new Intent(context, ProductInvestListActivity.class);
            intent.putExtra(KEY_PID, pid);
            intent.putExtra(KEY_TYPE, type);
            intent.putExtra(KEY_IS_DEPOSIT, is);
            intent.putExtra(KEY_TOTAL_COUNT, frequency);
            intent.putExtra(SERVICE_TIME, serviceTime);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_record);
        ButterKnife.bind(this);

        initView();
        Intent intent = getIntent();
        if (intent != null) {
            pid = intent.getLongExtra(KEY_PID, 0);
            totalCount = intent.getIntExtra(KEY_TOTAL_COUNT, 0);
            serverTime = intent.getLongExtra(SERVICE_TIME, System.currentTimeMillis());
        }
        tvLoanPeopleNum.setText("已有" + totalCount + "人出借");
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("出借记录").setHeadBackVisible(View.VISIBLE);
        Intent intent = getIntent();
        pid = 0;
        if (intent != null) {
            pid = intent.getLongExtra(KEY_PID, 0);
            totalCount = intent.getIntExtra(KEY_TOTAL_COUNT, 0);
            serverTime = intent.getLongExtra(SERVICE_TIME, System.currentTimeMillis());
        }

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        investRecordAdapter = new InvestRecordAdapter(list);
        View view = View.inflate(this,R.layout.no_data_default,null);
        investRecordAdapter.setEmptyView(view);
        recyclerView.setAdapter(investRecordAdapter);
        mHttpService = new HttpService(this, this);

        investRecordAdapter.setEnableLoadMore(true);
        investRecordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                requstData();
            }
        },recyclerView);
//        refresh.setEnableLoadMore(true);
//        refresh.setRefreshHeader(new BaseRefreshHeaderView(this));
//        refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                list.clear();
//                page = 0;
//                requstData();
//            }
//
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                requstData();
//            }
//        });
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                page = 0;
                requstData();
                refresh.setRefreshing(true);
            }
        });
        refresh.setColorSchemeColors(Utils.getColor(R.color.red_text2));
        requstData();
    }

    private void requstData() {
        mHttpService.getProductInvestRecord("" + pid, page, limit, serverTime);// PAGE_SIZE;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {
        super.onHttpSuccess(reqId, json);
        if (json == null) return;
        if (reqId == ServiceCmd.CmdId.CMD_getLoanBidList.ordinal() || reqId == ServiceCmd.CmdId.CMD_getLoanBidListForDeposit.ordinal()) {
            page++;
            ArrayList<LoanRecord> loanList = mHttpService.onGetProductInvestRecord(json);
            if (loanList != null && loanList.size() != 0) {
                list.addAll(loanList);
                investRecordAdapter.notifyDataSetChanged();
                if(loanList.size() < limit) {//不够一页了
                    investRecordAdapter.loadMoreEnd();
//                    refresh.finishLoadMoreWithNoMoreData();
                }else {
                    investRecordAdapter.loadMoreComplete();
//                    refresh.finishLoadMore(true);
                }
            } else {//没有更多数据可以加载
                investRecordAdapter.loadMoreEnd();
//                refresh.finishLoadMoreWithNoMoreData();
            }
//            refresh.finishRefresh();
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        refresh.setRefreshing(false);
        investRecordAdapter.loadMoreEnd();
//        refresh.finishRefresh();
//        refresh.finishLoadMoreWithNoMoreData();
    }

    private class InvestRecordAdapter extends BaseQuickAdapter<LoanRecord, BaseViewHolder> {


        public InvestRecordAdapter(@Nullable List<LoanRecord> data) {
            super(R.layout.item_invest_record, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, LoanRecord item) {
            helper.setText(R.id.tv_user_name, item.getUsername());
            helper.setText(R.id.tv_toloan_amount, item.getTendMoney() + "元");
            helper.setText(R.id.tv_date, item.getPaytime());
            helper.setText(R.id.tv_earnings, item.getPreProfit() + "元");

            TextView tv_deduction = helper.getView(R.id.tv_deduction);//抵扣券
            tv_deduction.setVisibility(View.GONE);
            switch (item.getType()) {
                case "1"://没用券
                    tv_deduction.setVisibility(View.GONE);
                    break;
                case "2"://代金券
                    tv_deduction.setVisibility(View.VISIBLE);
                    tv_deduction.setText("已抵扣" + item.getVoucherMoney() + "元");
                    break;
            }
        }
    }

}
