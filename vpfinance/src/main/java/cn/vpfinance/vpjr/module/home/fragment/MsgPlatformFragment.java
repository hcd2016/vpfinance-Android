package cn.vpfinance.vpjr.module.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jewelcredit.model.MsgBean;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.NewBaseFragment;
import cn.vpfinance.vpjr.module.home.MsgActivity;
import cn.vpfinance.vpjr.module.home.MsgDetailActivity;
import cn.vpfinance.vpjr.util.EventMsgReadModel;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.GsonUtil;
import de.greenrobot.event.EventBus;

/**
 * 平台消息
 */
public class MsgPlatformFragment extends NewBaseFragment {
    @Bind(R.id.rl_no_data_container)
    RelativeLayout rlNoDataContainer;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<MsgBean> list;
    private HttpService httpService;
    int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_msg_platform, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MsgPlatformAdapter msgPlatformAdapter = new MsgPlatformAdapter(list);
        recyclerView.setAdapter(msgPlatformAdapter);
        if (list.size() == 0) {
            rlNoDataContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            rlNoDataContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        msgPlatformAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //todo
                ((MsgActivity) getActivity()).gotoActivity(MsgDetailActivity.class);
            }
        });
        EventBus.getDefault().register(this);
        httpService = new HttpService(getActivity(), this);
        httpService.getMsgList("2", page + "");
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (json == null) return;
        if (reqId == ServiceCmd.CmdId.CMD_MSG_LIST.ordinal()) {
            MsgBean msgBean = GsonUtil.modelParser(json.toString(), MsgBean.class);
            if (msgBean == null) return;
            new EventBus().post(new EventMsgReadModel("2", msgBean.systemUnread));
        }
    }

    public void onEventMainThread(EventStringModel event) {
        if (event.getCurrentEvent().equals(EventStringModel.EVENT_MSG_ALL_READ_CLICK)) {//全部已读
            for (int i = 0; i < list.size(); i++) {

            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        errmsg.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.rl_no_data_container, R.id.recyclerView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_no_data_container:
                break;
            case R.id.recyclerView:
                break;
        }
    }

    class MsgPlatformAdapter extends BaseQuickAdapter<MsgBean, BaseViewHolder> {

        public MsgPlatformAdapter(@Nullable List<MsgBean> data) {
            super(R.layout.item_message, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MsgBean item) {

//            helper.setText(R.id.tv_title, item);
//            helper.setText(R.id.tv_date, "2018-08-08 12:00:00");
//            helper.setText(R.id.tv_desc, "我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行");
        }
    }
}
