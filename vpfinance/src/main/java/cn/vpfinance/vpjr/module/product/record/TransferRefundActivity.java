package cn.vpfinance.vpjr.module.product.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.model.TransferRefundInfo;
import cn.vpfinance.vpjr.util.Common;

/**
 * 回款计划
 */
public class TransferRefundActivity extends BaseActivity {
    private ListView listView;
    private Context mContext;
    private static final String PID = "pid";
    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_refund);
        mContext = this;
        mHttpService = new HttpService(this,this);
        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("回款计划").setHeadBackVisible(View.VISIBLE);
        listView = ((ListView) findViewById(R.id.listView));

        Intent intent = getIntent();
        if (intent != null){
            String pid =intent.getStringExtra(PID);
            if (!TextUtils.isEmpty(pid))
                mHttpService.getTransferProductRefund(pid);
        }

    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Transfer_Assign_Refund.ordinal()){

            TransferRefundInfo refundInfo = mHttpService.onGetTransferProductRefund(json);
            if (refundInfo != null ){
                List<TransferRefundInfo.ReturnRecordBean> data = refundInfo.getReturnRecord();
                if (data != null && data.size() != 0){
                    MyAdapter adapter = new MyAdapter(data,TransferRefundActivity.this);
                    listView.setAdapter(adapter);
                }
            }
        }
    }

    public static void goTransferRefund(Context context, String pid){
        Intent intent = new Intent(context,TransferRefundActivity.class);
        intent.putExtra(PID,pid);
        context.startActivity(intent);
    }

    class MyAdapter extends BaseAdapter {
        private List<TransferRefundInfo.ReturnRecordBean> mdata;
        private Context context;

        public MyAdapter(List<TransferRefundInfo.ReturnRecordBean> mdata, Context context) {
            this.mdata = mdata;
            this.context = context;
        }

        @Override
        public int getCount() {
            return mdata.size() == 0 ? 0 : mdata.size();
        }

        @Override
        public Object getItem(int position) {
            return mdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.item_transfer_available_time, null);
            TextView periods = (TextView) view.findViewById(R.id.periods);
            TextView repayStatus = (TextView) view.findViewById(R.id.repayStatus);
            TextView repaytime = (TextView) view.findViewById(R.id.repaytime);

            if (mdata != null){
                TransferRefundInfo.ReturnRecordBean timeBean = mdata.get(position);
                if (timeBean != null){
                    periods.setText(timeBean.getPeriods());
                    repayStatus.setText(timeBean.getStatus());
                    repaytime.setText(timeBean.getRepayTime());
                }
            }
            return view;
        }
    }
}
