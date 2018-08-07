package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.product.deposit.DepositDetailActivity;
import cn.vpfinance.vpjr.gson.DepositTab2Bean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.EndLessOnScrollListener;
import cn.vpfinance.vpjr.util.FormatUtils;

/**
 * 定存宝投资详情Tab 债权列表
 * Created by zzlz13 on 2017/4/12.
 */

public class NewDepositFragment extends BaseFragment {

    private static final String NET_URL = "netUrl";

    @Bind(R.id.deposit_list)
    RecyclerView mDepositList;

    private HttpService mHttpService;
    private String mRequestUrl;
    private MyAdapter myAdapter;
    private int start = 0;
    private int pageSize = 10;
    private List<DepositTab2Bean.LoansignsBean> totalData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_new_deposit, container, false);
        ButterKnife.bind(this, view);
        mHttpService = new HttpService(getActivity(), this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mDepositList.setLayoutManager(linearLayoutManager);
        myAdapter = new MyAdapter(mContext);
        mDepositList.setAdapter(myAdapter);

        mDepositList.addOnScrollListener(new EndLessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                start = start + pageSize;
                mHttpService.getRegularTab(mRequestUrl + "&start=" + start);
            }
        });

       /* mDepositList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                boolean b = isVisBottom(recyclerView);
//                if (b && !TextUtils.isEmpty(mRequestUrl)) {
//                    start = start + pageSize;
//                    mHttpService.getRegularTab(mRequestUrl + "&start=" + start);
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });*/

        Bundle args = getArguments();
        if (args != null) {
            mRequestUrl = args.getString(NET_URL);
        }
        mHttpService.getRegularTab(mRequestUrl + "&start=0");
    }

    public static boolean isVisBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE) {
            return true;
        } else {
            return false;
        }
    }

    public static BaseFragment newInstance(String url) {
        NewDepositFragment fragment = new NewDepositFragment();
        Bundle args = new Bundle();
        args.putString(NET_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Regular_Tab.ordinal()) {
            Gson gson = new Gson();
            DepositTab2Bean bean = gson.fromJson(json.toString(), DepositTab2Bean.class);
            init(bean);
        }
    }

    private void init(DepositTab2Bean bean) {
        if (bean == null) return;

        View header = LayoutInflater.from(mContext).inflate(R.layout.view_deposit_header, null, false);
        String tips = bean.tips;
        if (!TextUtils.isEmpty(tips)) {
            tips = tips.replace("++", "\n");
            ((TextView) header.findViewById(R.id.info)).setText("" + tips);
        }

        LinearLayout productType = (LinearLayout) header.findViewById(R.id.product_type);
        if (bean.loantypes != null) {
            for (String loantype : bean.loantypes) {
                View typeView = LayoutInflater.from(mContext).inflate(R.layout.view_deposit_tab2_type, null);
                ((TextView) typeView.findViewById(R.id.type_name)).setText(loantype);
                productType.addView(typeView);
            }
        } else {
            header.findViewById(R.id.ll_product_type).setVisibility(View.GONE);
        }
        myAdapter.setHeaderView(header);

        if (bean.loansigns != null && bean.loansigns.size() != 0) {
            totalData.addAll(bean.loansigns);
//            Logger.e("bean.loansigns:"+bean.loansigns.size());
        }
        myAdapter.setData(totalData, bean.comeType);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_NORMAL = 1;

        private View mHeaderView;
        private Context context;
        private List<DepositTab2Bean.LoansignsBean> data;
        private int size = 0;
        private String comType;

        public MyAdapter(Context context) {
            this.context = context;
        }

        public void setData(List<DepositTab2Bean.LoansignsBean> data, String comType) {
            if (data == null || data.size() == 0) return;
            this.data = data;
            size += data.size();
            this.comType = comType;
            notifyDataSetChanged();
        }

        public void setHeaderView(View headerView) {
            mHeaderView = headerView;
            size = 1;
            notifyItemInserted(0);
        }

        public View getHeaderView() {
            return mHeaderView;
        }

        @Override
        public int getItemViewType(int position) {
            if (mHeaderView == null) return TYPE_NORMAL;
            if (position == 0) return TYPE_HEADER;
            return TYPE_NORMAL;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (mHeaderView != null && viewType == TYPE_HEADER)
                return new MyViewHolder(mHeaderView);
            View layout = LayoutInflater.from(context).inflate(R.layout.item_deposit_list, viewGroup, false);
            return new MyViewHolder(layout);
        }

        private int itemHeight;

        public int getItemHeight() {
            return itemHeight;
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
            if (getItemViewType(i) == TYPE_HEADER) return;

            if (data != null) {
                final DepositTab2Bean.LoansignsBean loansignsBean = data.get(i - 1);
                myViewHolder.click_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, DepositDetailActivity.class);
                        intent.putExtra("pid", loansignsBean.id);
                        mContext.startActivity(intent);
                    }
                });

                if ("2".equals(comType)) {
                    myViewHolder.my_money_text.setVisibility(View.VISIBLE);
                    myViewHolder.my_money.setVisibility(View.VISIBLE);
                    myViewHolder.my_money.setText("" + FormatUtils.formatDown(loansignsBean.tenderMoney));
                } else {
                    myViewHolder.my_money_text.setVisibility(View.GONE);
                    myViewHolder.my_money.setVisibility(View.GONE);
                }


                //1预售中 2进行中 3还款中 4已完成 8已结清
                switch (loansignsBean.status) {
                    case 1:
//                        myViewHolder.status.setText("预售中");
                        myViewHolder.bottom_content.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
//                        myViewHolder.status.setText("进行中");
                        myViewHolder.bottom_content.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        myViewHolder.bottom_content.setVisibility(View.VISIBLE);
                        myViewHolder.status.setTextColor(Color.parseColor("#FF5050"));
                        myViewHolder.status.setText("还款中");
                        myViewHolder.protocol.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoWeb(loansignsBean.protocolUrl, "债权转让协议");
                            }
                        });
                        break;
                    case 4:
                        myViewHolder.bottom_content.setVisibility(View.VISIBLE);
                        myViewHolder.status.setTextColor(Color.parseColor("#CCCCCC"));
                        myViewHolder.status.setText("已结清");
                        myViewHolder.protocol.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoWeb(loansignsBean.protocolUrl, "债权转让协议");
                            }
                        });
                        break;
                    case 8:
                        myViewHolder.bottom_content.setVisibility(View.VISIBLE);
                        myViewHolder.status.setTextColor(Color.parseColor("#CCCCCC"));
                        myViewHolder.status.setText("已结清");
                        myViewHolder.protocol.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoWeb(loansignsBean.protocolUrl, "债权转让协议");
                            }
                        });
                        break;
                }

                myViewHolder.pid.setText("" + loansignsBean.id);
                myViewHolder.title.setText("" + loansignsBean.loanTitle);
                myViewHolder.total_money.setText("" + FormatUtils.formatDown(loansignsBean.issueLoan));
                myViewHolder.month.setText("" + loansignsBean.month);
                myViewHolder.way.setText("" + loansignsBean.behoof);

            }
        }

        @Override
        public int getItemCount() {
            return size;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            LinearLayout click_detail;
            LinearLayout bottom_content;
            TextView pid;
            TextView title;
            TextView total_money;
            TextView month;
            TextView way;
            TextView my_money;
            TextView my_money_text;
            TextView protocol;
            TextView status;

            public MyViewHolder(final View view) {
                super(view);

                if (view == mHeaderView) return;

                itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        itemHeight = view.getMeasuredHeight();
                        return true;
                    }
                });

                click_detail = ((LinearLayout) view.findViewById(R.id.click_detail));
                bottom_content = ((LinearLayout) view.findViewById(R.id.bottom_content));
                pid = (TextView) view.findViewById(R.id.pid);
                title = (TextView) view.findViewById(R.id.title);
                total_money = (TextView) view.findViewById(R.id.total_money);
                month = (TextView) view.findViewById(R.id.month);
                way = (TextView) view.findViewById(R.id.way);
                my_money = (TextView) view.findViewById(R.id.my_money);
                my_money_text = (TextView) view.findViewById(R.id.my_money_text);
                protocol = (TextView) view.findViewById(R.id.protocol);
                status = (TextView) view.findViewById(R.id.status);
            }
        }
    }
}
