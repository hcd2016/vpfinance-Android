package cn.vpfinance.vpjr.module.user.transfer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.adapter.BaseRegularListFragment;
import cn.vpfinance.vpjr.model.TransferListItemInfo;
import cn.vpfinance.vpjr.util.Common;

/**
 * 债权转让fragment
 */
public class TransferingProductListFragment extends BaseRegularListFragment{

    public static final String LOAN_TYPE = "loan_type";
    private HttpService mHttpService;
    //1可转让 2转让中3已转让
    private String loanType = "2";
    private static ArrayList<TransferListItemInfo> mList2;
    private int page = 1;
    private Context mContext;
    private int accountType = 0;

    public static TransferingProductListFragment newInstance(int accountType){
        TransferingProductListFragment fragment = new TransferingProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.AccountType,accountType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void getData(int page,int pageSize) {
        mHttpService.getTransferProductList(loanType, "" + page,accountType);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Transfer_Assign_List.ordinal()){
            mRefresh.setRefreshing(false);
            ArrayList<TransferListItemInfo> list = new ArrayList<>();
            HashMap<String,String> otherInfo = mHttpService.onGetTransferProductList(json, list);
            String p = otherInfo.get("totalPage");
            try{
                int totalPage = Integer.parseInt(p);
                if (totalPage >= page && list != null && list.size() != 0){
                    mList2.addAll(list);
                    myAdapter.setData(mList2);
                    page++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            if (mList2 == null || mList2.size() == 0){
                mClickRefresh.setText("暂无数据");
                mClickRefresh.setVisibility(View.VISIBLE);
            }else{
                mClickRefresh.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void initView(View view) {
        Bundle bundle = getArguments();
        if (bundle != null){
            accountType = bundle.getInt(Constant.AccountType);
        }

        if (mHttpService == null)
            mHttpService = new HttpService(getActivity(),this);

        mList2 = new ArrayList<>();

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh.setRefreshing(true);
                page = 1;
                mList2.clear();
                mHttpService.getTransferProductList(loanType, "" + page,accountType);
            }
        });

        mClickRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefresh.setRefreshing(true);
                page = 1;
                mList2.clear();
                mHttpService.getTransferProductList(loanType, "" + page,accountType);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransferListItemInfo item = (TransferListItemInfo) myAdapter.getItem(position);
                if (TextUtils.isEmpty(item.getRecordId())) return;
                Intent intent = new Intent(mContext, TransferProductExecuteActivity.class);
                intent.putExtra(TransferProductExecuteActivity.RECORD_ID, item.getRecordId());
                intent.putExtra(Constant.AccountType,accountType);
                gotoActivity(intent);
            }
        });
    }

    @Override
    public Pair<Integer, Integer> getDateConfig() {
        return new Pair<>(page,10);
    }

    @Override
    public View setViewToList(List mData, int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            view = View.inflate(mContext, R.layout.item_transfer_product_list, null);
            holder.title = (TextView)view.findViewById(R.id.title);
            holder.tenderMoney = (TextView)view.findViewById(R.id.tenderMoney);
            holder.tenderTime = (TextView)view.findViewById(R.id.tenderTime);
            holder.voucherMoney = (TextView)view.findViewById(R.id.voucherMoney);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = ((ViewHolder) view.getTag());
        }
        if (mData != null){
            TransferListItemInfo info = (TransferListItemInfo) mData.get(position);
            holder.title.setText(info.getTitle());
            holder.tenderMoney.setText("¥"+info.getMoney());
            holder.tenderTime.setText(""+info.getTime());
        }
        return view;
    }

    static class ViewHolder{
        TextView title;
        TextView tenderMoney;
        TextView tenderTime;
        TextView voucherMoney;
    }
}
