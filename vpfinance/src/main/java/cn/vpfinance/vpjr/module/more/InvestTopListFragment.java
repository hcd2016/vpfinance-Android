package cn.vpfinance.vpjr.module.more;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.NewBaseFragment;
import cn.vpfinance.vpjr.gson.InvestTopBean;
import cn.vpfinance.vpjr.module.product.fragment.BaseInfoFragment;
import cn.vpfinance.vpjr.view.CircleImg;

public class InvestTopListFragment extends NewBaseFragment {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private HttpService httpService;
    private InvestTopBean mInvestTopBean;
    private List<InvestTopBean.ListEntity> list;
    private MyAdapter myAdapter;
    private int currentPosition;//0为总榜,1为月榜,2为周榜

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_invest_top_list, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    public static InvestTopListFragment newInstance(int position) {
        InvestTopListFragment fragment = new InvestTopListFragment();
        Bundle args = new Bundle();
        args.putInt("position",position);
        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        Bundle args = getArguments();
        if (args != null) {
            currentPosition = args.getInt("position");
        }
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myAdapter = new MyAdapter(list);
        View emptyView = View.inflate(getActivity(),R.layout.layout_data_empty,null);
        myAdapter.setEmptyView(emptyView);
        recyclerView.setAdapter(myAdapter);
        httpService = new HttpService(getActivity(),this);
        httpService.getInvestTop(1,"1");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (reqId == ServiceCmd.CmdId.CMD_Invest_Top.ordinal()) {
            mInvestTopBean = httpService.onGetInvestTop(json);
            initData(mInvestTopBean);
        }
    }

    private void initData(InvestTopBean mInvestTopBean) {
        if(mInvestTopBean == null) return;

        List<InvestTopBean.ListEntity> listEntityList = mInvestTopBean.list;
        List<InvestTopBean.ListEntity> monthMapList = mInvestTopBean.monthMapList;
        List<InvestTopBean.ListEntity> weekMapList = mInvestTopBean.weekMapList;
        if(currentPosition == 0) {
            if(listEntityList != null && listEntityList.size() != 0) {
                list.addAll(listEntityList);
            }
        }else if(currentPosition == 1) {
            if(monthMapList != null && monthMapList.size() != 0) {
                list.addAll(monthMapList);
            }
        }else if(currentPosition == 2) {
            if(weekMapList != null && weekMapList.size() != 0) {
                list.addAll(weekMapList);
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends BaseQuickAdapter<InvestTopBean.ListEntity,BaseViewHolder> {

        public MyAdapter(@Nullable List<InvestTopBean.ListEntity> data) {
            super(R.layout.item_invest_top_list, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, InvestTopBean.ListEntity item) {
            ImageView iv_top123 = helper.getView(R.id.iv_top123);
            TextView tv_ranking_num = helper.getView(R.id.tv_ranking_num);
            CircleImg iv_avatar = helper.getView(R.id.iv_avatar);
            if(helper.getLayoutPosition() < 3) {
                iv_top123.setVisibility(View.VISIBLE);
                tv_ranking_num.setVisibility(View.GONE);
            }else {
                iv_top123.setVisibility(View.GONE);
                tv_ranking_num.setVisibility(View.VISIBLE);
            }
            if(helper.getLayoutPosition() == 0 ) {
                iv_top123.setImageResource(R.mipmap.no1);
            }else if(helper.getLayoutPosition() == 1) {
                iv_top123.setImageResource(R.mipmap.no2);
            }else if(helper.getLayoutPosition() == 2) {
                iv_top123.setImageResource(R.mipmap.no3);
            }else {
                tv_ranking_num.setText(helper.getLayoutPosition()+1+"");
            }

            Glide.with(getActivity()).load(HttpService.mBaseUrl + item.head).
                    dontAnimate().error(R.mipmap.profile).into(iv_avatar);

            helper.setText(R.id.tv_phone_num,item.userName);
            helper.setText(R.id.tv_total_amount,item.tenderMoney);
        }
    }
}
