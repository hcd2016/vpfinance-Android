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

import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 剩余还款日
 */
public class TransferAvailableTimeActivity extends BaseActivity {

    public static final String IS_TRANSFER_PRODUCT = "is_transfer_product";
    private ListView listView;
    private Context mContext;
    private static final String TRANSFER_AVAILABLE_TIME_JSON = "transfer_available_time_json";
    private static final String TOTAL_PERIOD = "totalPeriod";
    private ArrayList<TransferAvailableTimeBean> mList = new ArrayList<TransferAvailableTimeBean>();
    private String totalPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_available_time);
        mContext = this;

        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("剩余回款计划").setHeadBackVisible(View.VISIBLE);
        listView = ((ListView) findViewById(R.id.listView));

        Intent intent = getIntent();
        if (intent != null && (!TextUtils.isEmpty(intent.getStringExtra(TRANSFER_AVAILABLE_TIME_JSON)))) {
            String json = intent.getStringExtra(TRANSFER_AVAILABLE_TIME_JSON);
            totalPeriod = intent.getStringExtra(TOTAL_PERIOD);

            if (TextUtils.isEmpty(json))    return;
            try {
                String[] items = json.split("\\|");
                for (String item : items) {
                    String[] column = item.split(",");
                    mList.add(new TransferAvailableTimeBean(column[0], column[1], column[2]));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mList != null && (!mList.isEmpty())) {
            listView.setAdapter(new MyAdapter(mList, totalPeriod, mContext));
        }

    }

    public static void goTransferAvailableTime(Context context, String json, String totalPeriod) {
        Intent intent = new Intent(context, TransferAvailableTimeActivity.class);
        intent.putExtra(TRANSFER_AVAILABLE_TIME_JSON, json);
        intent.putExtra(TOTAL_PERIOD, totalPeriod);
        context.startActivity(intent);
    }

    class MyAdapter extends BaseAdapter {
        private ArrayList<TransferAvailableTimeBean> mdata;
        private Context context;
        private String totalPeriod;

        public MyAdapter(ArrayList<TransferAvailableTimeBean> mdata, String totalPeriod, Context context) {
            this.mdata = mdata;
            this.context = context;
            this.totalPeriod = totalPeriod;
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

            if (mdata != null) {
                TransferAvailableTimeBean timeBean = mdata.get(position);
                if (timeBean != null) {
                    periods.setText(timeBean.periods + "/" + totalPeriod);
                    repayStatus.setText("1".equals(timeBean.repayStatus) ? "待回款" : "已回款");
                    repaytime.setText(timeBean.repaytime);
                }
            }
            return view;
        }
    }

    class TransferAvailableTimeBean {
        public String periods;
        public String repayStatus;
        public String repaytime;

        public TransferAvailableTimeBean(String periods, String repayStatus, String repaytime) {
            this.periods = periods;
            this.repayStatus = repayStatus;
            this.repaytime = repaytime;
        }
    }

}
