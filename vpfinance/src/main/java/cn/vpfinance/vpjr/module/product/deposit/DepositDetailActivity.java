package cn.vpfinance.vpjr.module.product.deposit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.DepositItemBean;
import cn.vpfinance.vpjr.model.TransferDepositDetailItem;
import cn.vpfinance.vpjr.util.Common;

/**
 * 定存宝 - 债券详情
 * Created by zzlz13 on 2017/4/12.
 */

public class DepositDetailActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.recycler_view)
    RecyclerView recycler_view;


    private long pid;
    private HttpService mHttpService;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_detail);
        ButterKnife.bind(this);

        titleBar.reset().setTitle("债权详情").setHeadBackVisible(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null){
            pid = intent.getIntExtra("pid",0);
            if (pid == 0)   return;
        }
        mHttpService = new HttpService(this,this);
        mHttpService.getDepositItemInfo(""+pid);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this);
        recycler_view.setAdapter(adapter);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Info.ordinal()){
            Gson gson = new Gson();
            DepositItemBean bean = gson.fromJson(json.toString(), DepositItemBean.class);
            if (bean != null){
                List<DepositItemBean.LoansigninfoBean> loansigninfo = bean.loansigninfo;
                //转化bean
                ArrayList<TransferDepositDetailItem> list = new ArrayList<>();
                if (loansigninfo != null){
                    for (DepositItemBean.LoansigninfoBean loansigninfoBean : loansigninfo) {
                        list.add(new TransferDepositDetailItem(true,loansigninfoBean.info));

                        if (loansigninfoBean.data != null){
                            for (DepositItemBean.LoansigninfoBean.DataBean dataBean : loansigninfoBean.data) {
                                list.add(new TransferDepositDetailItem(false,dataBean.key,dataBean.value));
                            }
                        }
                    }
                }
                adapter.setData(list);
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        private static final int HEADER_TYPE = 0;
        private static final int CONTENT_TYPE = 1;
        private List<TransferDepositDetailItem> data;
        private Context context;
        private View content;
        private View header;

        public MyAdapter(Context context) {
            this.context = context;
        }

        public void setData(List<TransferDepositDetailItem> data) {
            if (data == null)  return;
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            TransferDepositDetailItem bean = data.get(position);
            return bean.isHeader ? HEADER_TYPE : CONTENT_TYPE;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            content = LayoutInflater.from(context).inflate(R.layout.item_deposit_detail_content, null);
            header = LayoutInflater.from(context).inflate(R.layout.item_deposit_detail_header, null);
            if (getItemViewType(i) == HEADER_TYPE){
                return new MyViewHolder(header);
            }else if (getItemViewType(i) == CONTENT_TYPE){
                return new MyViewHolder(content);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
            TransferDepositDetailItem item = data.get(i);
            int itemViewType = getItemViewType(i);
            if (itemViewType == HEADER_TYPE){
                myViewHolder.header_name.setText(item.headerTitle);
            }else if (itemViewType == CONTENT_TYPE){
                myViewHolder.content_name.setText(item.contentTitle);
                myViewHolder.content_value.setText(item.contentValue);
            }
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView header_name;
            TextView content_name;
            TextView content_value;
            public MyViewHolder(View itemView) {
                super(itemView);
                if (itemView == header){
                    header_name = ((TextView) itemView.findViewById(R.id.title));
                }else if (itemView == content){
                    content_name = ((TextView) itemView.findViewById(R.id.key));
                    content_value = ((TextView) itemView.findViewById(R.id.value));
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
