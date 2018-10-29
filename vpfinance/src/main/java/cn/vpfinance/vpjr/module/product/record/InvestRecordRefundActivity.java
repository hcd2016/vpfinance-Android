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
 * 出借记录的回款记录
 */
public class InvestRecordRefundActivity extends BaseActivity {

    private ListView listView;
    private Context mContext;
    private static final String RECORDID = "recordId";
    private static final String TYPE = "type";//1定存宝
    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_record_refund);
        mContext = this;
        mHttpService = new HttpService(this,this);
        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("还款计划").setHeadBackVisible(View.VISIBLE);
        listView = ((ListView) findViewById(R.id.listView));

        Intent intent = getIntent();
        if (intent != null){
            String pid =intent.getStringExtra(RECORDID);
            int type =intent.getIntExtra(TYPE,0);
            if (!TextUtils.isEmpty(pid))
                mHttpService.getInvestRecordRefund(type,pid);
        }

    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Invest_Record_Refund.ordinal()){

            TransferRefundInfo refundInfo = mHttpService.onGetTransferProductRefund(json);
            if (refundInfo != null ){
                List<TransferRefundInfo.ReturnRecordBean> data = refundInfo.getReturnRecord();
                if (data != null && data.size() != 0){
                    MyAdapter adapter = new MyAdapter(data,InvestRecordRefundActivity.this);
                    listView.setAdapter(adapter);
                }
            }
        }
    }

    public static void goInvestRecordRefund(Context context, int type,String recordId){
        Intent intent = new Intent(context,InvestRecordRefundActivity.class);
        intent.putExtra(RECORDID,recordId);
        intent.putExtra(TYPE,type);
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
            View view = View.inflate(context, R.layout.item_invest_record_refund, null);
            TextView periods = (TextView) view.findViewById(R.id.periods);
            TextView repayMoney = (TextView) view.findViewById(R.id.repayMoney);
            TextView repayStatus = (TextView) view.findViewById(R.id.repayStatus);
            TextView repaytime = (TextView) view.findViewById(R.id.repaytime);

            if (mdata != null){
                TransferRefundInfo.ReturnRecordBean timeBean = mdata.get(position);
                int color = timeBean.getColor();//1红色 2灰色 3黑色
                int c = 0;
                if (color == 1){
                    c = context.getResources().getColor(R.color.red_text);
                }else if (color == 2){
                    c = context.getResources().getColor(R.color.text_999999);
                }else if (color == 3){
                    c = context.getResources().getColor(R.color.text_1c1c1c);
                }
                periods.setTextColor(c);
                repayMoney.setTextColor(c);
                repayStatus.setTextColor(c);
                repaytime.setTextColor(c);

                if (timeBean != null){
                    periods.setText(timeBean.getPeriods());
                    repayStatus.setText(timeBean.getStatus());
                    repaytime.setText(timeBean.getRepayTime());
                    repayMoney.setText(timeBean.getMoney());
                }
            }
            return view;
        }
    }
}
