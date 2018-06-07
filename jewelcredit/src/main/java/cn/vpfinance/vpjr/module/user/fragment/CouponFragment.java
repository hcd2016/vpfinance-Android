package cn.vpfinance.vpjr.module.user.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.CouponBean;
import cn.vpfinance.vpjr.model.EventBusCoupon;
import cn.vpfinance.vpjr.module.user.personal.InviteGiftActivity;
import de.greenrobot.event.EventBus;

public class CouponFragment extends BaseFragment {

    public static final int STATUS_UNUSED = 1;
    public static final int STATUS_USED = 2;
    public static final int STATUS_INVALID = 3;

    //1代金券 2预约卷
    public static final int TYPE_ALL = 0;
    public static final int TYPE_VOUCHER = 1;
    public static final int TYPE_PRESELL = 2;

    private int status = STATUS_UNUSED;
    private int type = TYPE_ALL;

    @Bind(R.id.mEmpty)
    LinearLayout mEmpty;
    @Bind(R.id.mRecommend)
    Button mRecommend;
    @Bind(R.id.mRefresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    private int page = 1;
    private HttpService mHttpService;
    private CouponAdapter adapter;
    private List<CouponBean.MyCouponDtoBean.MyCouponListDtosBean> data = new ArrayList<>();
    private int totalCount = 0;

    public static CouponFragment getInstance(int type, int status) {
        CouponFragment fragment = new CouponFragment();
        Bundle args = new Bundle();
        args.putInt("status", status);
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_coupon, container, false);
        ButterKnife.bind(this, view);
        mHttpService = new HttpService(getContext(), this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            status = arguments.getInt("status");
            type = arguments.getInt("type");
        }

        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                totalCount = 0;
                data.clear();
                request();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CouponAdapter(getContext(), status);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == totalItemCount - 1
                        && visibleItemCount > 0) {
                    //加载更多
                    if (data.size() < totalCount){
                        request();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        request();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        mRefresh.setRefreshing(false);
        if (reqId == ServiceCmd.CmdId.CMD_COUPON_LIST.ordinal()) {
            CouponBean couponBean = new Gson().fromJson(json.toString(), CouponBean.class);
            if (couponBean != null && couponBean.myCouponDto != null){
                if (couponBean.myCouponDto.myCouponListDtos != null && couponBean.myCouponDto.myCouponListDtos.size() != 0) {
                    data.addAll(couponBean.myCouponDto.myCouponListDtos);
                    adapter.setData(data);
                }
                if (status == CouponFragment.STATUS_USED){
                    totalCount = couponBean.myCouponDto.useCount;
                }else if (status == CouponFragment.STATUS_UNUSED){
                    totalCount = couponBean.myCouponDto.unUseCount;
                }else if (status == CouponFragment.STATUS_INVALID){
                    totalCount = couponBean.myCouponDto.expiredCount;
                }
            }

            if (data.size() == 0){
                mEmpty.setVisibility(View.VISIBLE);
            }else{
                mEmpty.setVisibility(View.GONE);
            }
        }
    }

    public void onEventMainThread(EventBusCoupon event) {
        mRefresh.setRefreshing(true);
        type = event.type;
        page = 1;
        totalCount = 0;
        data.clear();
        request();
    }

    private void request() {
        mHttpService.getCouponList(type, status, page);
        page++;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.mRecommend})
    public void click(View view){
        switch (view.getId()){
            case R.id.mRecommend:
                InviteGiftActivity.goThis(getContext());
                break;
        }
    }

    public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder>{

        private Context mContext;
        //    private int type;
//        private int status;
        private List<CouponBean.MyCouponDtoBean.MyCouponListDtosBean> data;

        public CouponAdapter(Context mContext,  int status) {
            this.mContext = mContext;
//        this.type = type;
//            this.status = status;
        }

        @Override
        public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CouponViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_coupon,parent,false));
        }

        @Override
        public void onBindViewHolder(CouponViewHolder holder, int position) {
//        Logger.e("onBindViewHolder.position: "+position);
            //强制关闭复用
//            holder.setIsRecyclable(false);
            CouponBean.MyCouponDtoBean.MyCouponListDtosBean bean = data.get(position);
            if (bean != null){
                if (bean.couponType == 2){//1代金券 2预约券
                    switch (status){
                        case CouponFragment.STATUS_UNUSED:
                            holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_presell_header_usable));
                            break;
                        case CouponFragment.STATUS_USED:
                            holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_presell_header_nousable));
                            break;
                        case CouponFragment.STATUS_INVALID:
                            holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_presell_header_nousable));
                            break;
                    }
                    if (bean.voucherStatus == 2){
                        holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_presell_header_nousable));
                    }
                    holder.presellName.setText("预");
                    holder.voucher_get.setText(bean.getWay);
                    holder.voucher_time.setText("有效期至"+ bean.expiredTm);
                    ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.item_coupon_remark, R.id.tvInfo, bean.remarkList);
                    holder.mListView.setAdapter(arrayAdapter);
                }else if (bean.couponType == 1){
                    switch (status){
                        case CouponFragment.STATUS_UNUSED:
                            holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_voucher_header_usable));
                            break;
                        case CouponFragment.STATUS_USED:
                            holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                            break;
                        case CouponFragment.STATUS_INVALID:
                            holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                            break;
                    }
                    if (bean.voucherStatus == 2){
                        holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                    }
                    holder.presellName.setText(bean.denomination);
                    holder.voucher_get.setText(bean.getWay);
                    holder.voucher_time.setText("有效期至"+ bean.expiredTm);
                    ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.item_coupon_remark, R.id.tvInfo, bean.remarkList);
                    holder.mListView.setAdapter(arrayAdapter);
                }
            }
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public void setData(List<CouponBean.MyCouponDtoBean.MyCouponListDtosBean> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        public class CouponViewHolder extends RecyclerView.ViewHolder{
            ImageView voucherState;
            TextView presellName;
            TextView voucher_get;
            TextView voucher_time;
            ListView mListView;
            public CouponViewHolder(View itemView) {
                super(itemView);
                voucherState = (ImageView) itemView.findViewById(R.id.voucherState);
                presellName = (TextView) itemView.findViewById(R.id.presellName);
                voucher_get = (TextView) itemView.findViewById(R.id.voucher_get);
                voucher_time = (TextView) itemView.findViewById(R.id.voucher_time);
                mListView = (ListView)itemView.findViewById(R.id.mListView);
            }
        }
    }

}
