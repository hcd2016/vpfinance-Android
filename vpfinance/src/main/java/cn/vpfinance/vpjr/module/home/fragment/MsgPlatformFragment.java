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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.home.MessageActivity;
import cn.vpfinance.vpjr.module.home.MsgDetailActivity;

/**
 * 平台消息
 */
public class MsgPlatformFragment extends BaseFragment {
    @Bind(R.id.rl_no_data_container)
    RelativeLayout rlNoDataContainer;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_msg_platform, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        list = new ArrayList<>();
        list.add("标题1");
        list.add("标题2");
        list.add("标题3");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MsgPlatformAdapter msgPlatformAdapter = new MsgPlatformAdapter(list);
        recyclerView.setAdapter(msgPlatformAdapter);
        if(list.size() == 0) {
            rlNoDataContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            rlNoDataContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        msgPlatformAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //todo
                ((MessageActivity)getActivity()).gotoActivity(MsgDetailActivity.class);
            }
        });
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

    class MsgPlatformAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MsgPlatformAdapter(@Nullable List<String> data) {
            super(R.layout.item_message, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_title,item);
            helper.setText(R.id.tv_date,"2018-08-08 12:00:00");
            helper.setText(R.id.tv_desc,"我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行我是描述我要超过一行");
        }
    }
}
