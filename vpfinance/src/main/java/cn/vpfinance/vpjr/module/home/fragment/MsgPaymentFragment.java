package cn.vpfinance.vpjr.module.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jewelcredit.model.MsgBean;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.NewBaseFragment;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialogFragment;
import cn.vpfinance.vpjr.util.EventMsgReadModel;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.GsonUtil;
import de.greenrobot.event.EventBus;

/**
 * 还款公告
 */
public class MsgPaymentFragment extends NewBaseFragment {
    @Bind(R.id.textview)
    TextView textview;
    @Bind(R.id.rl_no_data_container)
    RelativeLayout rlNoDataContainer;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout refresh;
    private List<MsgBean.RepayListBean> list;
    private HttpService httpService;
    int page = 1;
    private MsgPaymentAdapter msgPaymentAdapter;
    public String type = "1"; //消息类型,1为还款公告,2为平台公告

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_msg_payment, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }


    public static MsgPaymentFragment newInstance(String type) {
        MsgPaymentFragment msgPaymentFragment = new MsgPaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        msgPaymentFragment.setArguments(bundle);
        return msgPaymentFragment;
    }

    private void initView() {
        Bundle arguments = getArguments();
        if(arguments != null) {
            String myType = arguments.getString("type");
            type = myType;
        }

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        msgPaymentAdapter = new MsgPaymentAdapter(list);
        recyclerView.setAdapter(msgPaymentAdapter);
        if (list.size() == 0) {
            rlNoDataContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            rlNoDataContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        EventBus.getDefault().register(this);
        msgPaymentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                list.get(position).isRead = "1";
                msgPaymentAdapter.notifyDataSetChanged();
                gotoWeb(list.get(position).url, "消息详情");
            }
        });
        msgPaymentAdapter.setEnableLoadMore(true);
        msgPaymentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                httpService.getMsgList(type, page + "");
            }
        },recyclerView);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                page = 1;
                refresh.setRefreshing(true);
                httpService.getMsgList(type, page + "");
            }
        });
        refresh.setColorSchemeColors(Utils.getColor(R.color.red_text2));
        httpService = new HttpService(getActivity(), this);
        httpService.getMsgList(type, page + "");
    }

    public void onEventMainThread(EventStringModel event) {
        if (event.getCurrentEvent().equals(EventStringModel.EVENT_MSG_ALL_READ_CLICK)) {//全部已读
            for (int i = 0; i < list.size(); i++) {
                list.get(i).isRead = "1";
                msgPaymentAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (json == null) return;
        if (reqId == ServiceCmd.CmdId.CMD_MSG_LIST.ordinal()) {
            refresh.setRefreshing(false);
            MsgBean msgBean = GsonUtil.modelParser(json.toString(), MsgBean.class);
            if (msgBean != null) {
                EventBus.getDefault().post(new EventMsgReadModel(type, msgBean.systemUnread));
                List<MsgBean.RepayListBean> repayList = msgBean.repayList;
                if (repayList != null && repayList.size() != 0) {
                    list.addAll(repayList);
                    msgPaymentAdapter.notifyDataSetChanged();
                    msgPaymentAdapter.loadMoreComplete();
                }else {
                    msgPaymentAdapter.loadMoreEnd();
                }
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        if(refresh != null) {
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class MsgPaymentAdapter extends BaseQuickAdapter<MsgBean.RepayListBean, BaseViewHolder> {

        public MsgPaymentAdapter(@Nullable List<MsgBean.RepayListBean> data) {
            super(R.layout.item_message, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MsgBean.RepayListBean item) {
            TextView tv_title = helper.getView(R.id.tv_title);
            TextView tv_date = helper.getView(R.id.tv_date);
            TextView tv_desc = helper.getView(R.id.tv_desc);

            helper.setText(R.id.tv_title, item.title);
            helper.setText(R.id.tv_date, item.time);
            helper.setText(R.id.tv_desc, item.content);
            if (item.isRead.equals("0") || !AppState.instance().logined()) {//未读
                tv_title.setTextColor(Utils.getColor(R.color.text_333333));
                tv_desc.setTextColor(Utils.getColor(R.color.text_666666));
            } else {
                tv_title.setTextColor(Utils.getColor(R.color.text_999999));
                tv_desc.setTextColor(Utils.getColor(R.color.text_999999));
            }
        }
    }
}
