package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.SharePop;
import cn.vpfinance.vpjr.module.user.asset.FundRecordsActivity;
import cn.vpfinance.vpjr.module.product.deposit.FundRecordsDepositDetailActivity;
import cn.vpfinance.vpjr.module.product.record.FundRecordsDetailActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.NewRecordsBean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.ScreenUtil;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/12/5.
 */
public class NewFundRecordsListFragment extends BaseFragment {

    public static final String FUND_RECORDS_TYPE = "fund_records_type";
    public static final String FUND_ACCOUNT_TYPE = "fund_account_type";
    @Bind(R.id.listview)
    ListView           mListview;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.view_ll)
    LinearLayout       mViewLl;
    @Bind(R.id.tvNoData)
    TextView           mTvNoData;
    private String      mType;
    private HttpService mHttpService;
    private MyAdapter   mMyAdapter;
    private Long        mUserId;
    private              int page      = 1;
    private final static int PAGE_SIZE = 10;
    private String mTotalPage;
    private List<NewRecordsBean.RecordListEntity> mAllList    = new ArrayList<>();
    private boolean                               mIsLastPage = false;
    private int accountType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getString(FUND_RECORDS_TYPE);
            accountType = bundle.getInt(FUND_ACCOUNT_TYPE);
        }
        User user = DBUtils.getUser(mContext);
        if (user != null) {
            mUserId = user.getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(mContext, R.layout.new_fund_records_list_fragment, null);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mHttpService = new HttpService(mContext, this);
        mHttpService.getNewFundRecord(mUserId + "", page + "", PAGE_SIZE + "", mType,accountType);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.main_color));

        mMyAdapter = new MyAdapter();
        mListview.setAdapter(mMyAdapter);
        initListener();
        return view;
    }

    private void initListener() {
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mAllList.clear();
                mHttpService.getNewFundRecord(mUserId + "", page + "", PAGE_SIZE + "", mType,accountType);
            }
        });

        mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    page++;

                    mIsLastPage = page > Integer.parseInt(mTotalPage) ? true : false;
                    if (mIsLastPage)
                        return;
                    mHttpService.getNewFundRecord(mUserId + "", page + "", PAGE_SIZE + "", mType,accountType);
                    mRefresh.setRefreshing(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewRecordsBean.RecordListEntity bean = (NewRecordsBean.RecordListEntity) mMyAdapter.getItem(position);
                String recordPoolId = bean.getRecordPoolId();
                if (TextUtils.isEmpty(recordPoolId) || "0".equals(recordPoolId)){
                    int recordId = bean.getRecordId();
                    Intent where = new Intent(mContext, FundRecordsDetailActivity.class);
                    where.putExtra(FundRecordsDetailActivity.UID, recordId);
                    startActivity(where);
                }else{
                    //定存宝
                    Intent where = new Intent(mContext, FundRecordsDepositDetailActivity.class);
                    where.putExtra("poolId",recordPoolId);
                    startActivity(where);
                }

            }
        });

    }

    public static NewFundRecordsListFragment newInstance(String type,int accountType) {
        NewFundRecordsListFragment frag = new NewFundRecordsListFragment();
        Bundle args = new Bundle();
        args.putString(FUND_RECORDS_TYPE, type);
        args.putInt(FUND_ACCOUNT_TYPE, accountType);
        frag.setArguments(args);
        return frag;
    }

    public void onEventMainThread(FundRecordsActivity.RecordsRefreshBean event) {
        if (!isAdded()) return;
        if (event != null) {
            if ("1".equals(event.getMsg())) {//持有中
                page = 1;
                mAllList.clear();
                mHttpService.getNewFundRecord(mUserId + "", page + "", PAGE_SIZE + "", "1",accountType);
            } else {//已完成
                page = 1;
                mAllList.clear();
                mHttpService.getNewFundRecord(mUserId + "", page + "", PAGE_SIZE + "", "2",accountType);
            }
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        mRefresh.setRefreshing(false);
        if (reqId == ServiceCmd.CmdId.CMD_FundRecord.ordinal()) {
            NewRecordsBean newRecordsBean = mHttpService.onGetNewFundRecord(json);
            if (newRecordsBean != null) {
                mTotalPage = newRecordsBean.getTotalPage();
                List<NewRecordsBean.RecordListEntity> recordList = newRecordsBean.getRecordList();
                if (recordList != null && recordList.size() != 0) {
                    mTvNoData.setVisibility(View.GONE);
                    mAllList.addAll(recordList);
                    mMyAdapter.setData(mAllList);
                } else {
                    mTvNoData.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        if(!isAdded()) return;
        mRefresh.setRefreshing(false);
        mTvNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    class MyAdapter extends BaseAdapter {

        private List<NewRecordsBean.RecordListEntity> mData;

        public void setData(List<NewRecordsBean.RecordListEntity> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData == null ? null : mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData == null ? 0 : position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_new_records_list, null);
                viewHolder.tvMonth = (TextView) convertView.findViewById(R.id.tvMonth);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                viewHolder.tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
                viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvSellState = (TextView) convertView.findViewById(R.id.tvSellState);
                viewHolder.tvUseVoucher = (TextView) convertView.findViewById(R.id.tvUseVoucher);
                viewHolder.interest = (ImageView) convertView.findViewById(R.id.interest);
                viewHolder.ll_share = (LinearLayout) convertView.findViewById(R.id.ll_share);
                viewHolder.ll_month = (LinearLayout) convertView.findViewById(R.id.ll_month);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (mData != null && mData.size() != 0) {
                final NewRecordsBean.RecordListEntity bean = mData.get(position);
                if (bean != null) {
                    String month = bean.getTenderMonth();

                    //判断什么时候显示月份
                    if (position != 0 && !TextUtils.isEmpty(month) && month.equals(mData.get(position - 1).getTenderMonth())) {
                        viewHolder.ll_month.setVisibility(View.GONE);
                    } else {
                        viewHolder.ll_month.setVisibility(View.VISIBLE);
                        viewHolder.tvMonth.setText(month + "月");
                    }

                    int type = bean.getType();
                    switch (type) {
                        case 1://没用券
                            viewHolder.interest.setVisibility(View.GONE);
                            viewHolder.tvUseVoucher.setVisibility(View.GONE);
                            break;
                        case 2://代金券
                            viewHolder.interest.setVisibility(View.VISIBLE);
                            viewHolder.tvUseVoucher.setVisibility(View.VISIBLE);
                            viewHolder.interest.setImageResource(R.drawable.voucher);
                            viewHolder.tvUseVoucher.setText("已使用代金券抵扣" + bean.getVoucherMoney() + "元");
                            break;
                        case 3://加息券
                            viewHolder.interest.setVisibility(View.VISIBLE);
                            viewHolder.tvUseVoucher.setVisibility(View.VISIBLE);
                            viewHolder.interest.setImageResource(R.drawable.interest);
                            viewHolder.tvUseVoucher.setText("已使用加息券加息" + bean.getVoucherMoney() + "元");
                            break;
                    }

                    String loanState = "";
                    switch (bean.getLoanState()) {
                        //1未发布 2进行中 3回款中 4已完成
                        case 1:
                            loanState = "预约中";
                            viewHolder.tvSellState.setText(loanState);
                            viewHolder.tvSellState.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            loanState = "进行中";
                            viewHolder.tvSellState.setText(loanState);
                            viewHolder.tvSellState.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            //                            loanState = "还款中";
                            //                            viewHolder.tvSellState.setText(loanState);
                            viewHolder.tvSellState.setVisibility(View.GONE);
                            break;
                        case 4:
                            //                            loanState = "已完成";
                            //                            viewHolder.tvSellState.setText(loanState);
                            viewHolder.tvSellState.setVisibility(View.GONE);
                            break;
                    }
                    /*if ("1".equals(bean.getIsBook())) {//使用了预约券
                        viewHolder.tvSellState.setVisibility(View.VISIBLE);
                        viewHolder.tvSellState.setText("预约中");
                    }*/

                    if ("false".equals(bean.getHaveRedPacket())) {
                        viewHolder.ll_share.setVisibility(View.GONE);
                    }
                    if ("true".equals(bean.getHaveRedPacket()) && (!TextUtils.isEmpty(bean.getShareUrl()))) {
                        viewHolder.ll_share.setVisibility(View.VISIBLE);
                        viewHolder.ll_share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final File imgFile = new File(mContext.getCacheDir(), "redpack.png");
                                if (imgFile != null && imgFile.exists()) {
                                    //imgFile.delete();
                                } else {
                                    try {
                                        Utils.copyAssetsFileToPath(mContext, "redpack.png", imgFile.getAbsolutePath());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //								showShare("好友发来微品金融红包，个数有限先到先得，快去碰碰手气吧。", imgFile == null ? "" : imgFile.getAbsolutePath(), shareUrl);
                                SharePop sharePop = new SharePop(mContext, bean.getShareUrl(), imgFile == null ? "" : imgFile.getAbsolutePath(),
                                        "好友发来微品金融红包，个数有限先到先得，快去碰碰手气吧。", "微品金融拼手气红包");
                                sharePop.showAtLocation(mViewLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ScreenUtil.getBottomStatusHeight(mContext));
                            }
                        });
                    }


                    viewHolder.tvTitle.setText(bean.getTitle());//项目名字
                    viewHolder.tvMoney.setText(bean.getTenderMoney() + "元");//投资金额
                    viewHolder.tvDate.setText(bean.getTenderTime());//时间

                }
            }
            return convertView;
        }


    }

    class ViewHolder {
        TextView     tvMonth;
        TextView     tvTitle;
        TextView     tvMoney;
        TextView     tvDate;
        TextView     tvUseVoucher;
        TextView     tvSellState;
        ImageView    interest;
        LinearLayout ll_share;
        LinearLayout ll_month;
    }
}
