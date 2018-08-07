package cn.vpfinance.vpjr.module.product.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;

import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.NewTransferProductBean;

/**
 * 剩余回款计划
 */
public class NoRepayListActivity extends BaseActivity {

    private ListView listView;
    private Context mContext;
    private static final String NO_REPAY_LIST = "no_repay_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_available_time);
        mContext = this;

        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("剩余回款计划").setHeadBackVisible(View.VISIBLE);
        listView = ((ListView) findViewById(R.id.listView));

        Intent intent = getIntent();
        if (intent != null){
            ArrayList<NewTransferProductBean.NoRepayListBean> list = intent.getParcelableArrayListExtra(NO_REPAY_LIST);
            if (list != null){
                listView.setAdapter(new MyAdapter(list,mContext));
            }else{
                //
            }
        }
    }

    public static void goActivity(Context context, ArrayList<NewTransferProductBean.NoRepayListBean> noRepayList) {
        Intent intent = new Intent(context, NoRepayListActivity.class);
        intent.putParcelableArrayListExtra(NO_REPAY_LIST, noRepayList);
        context.startActivity(intent);
    }

    class MyAdapter extends BaseAdapter {
        private ArrayList<NewTransferProductBean.NoRepayListBean> data;
        private Context context;

        public MyAdapter(ArrayList<NewTransferProductBean.NoRepayListBean> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size() == 0 ? 0 : data.size();
        }

        @Override
        public NewTransferProductBean.NoRepayListBean getItem(int position) {
            return data == null ? null : data.get(position);
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

            if (data != null) {
                NewTransferProductBean.NoRepayListBean bean = data.get(position);
                if (bean != null) {
                    periods.setText(""+bean.periods);
                    repayStatus.setText(bean.repayStatus);
                    repaytime.setText(bean.repaytime);
                }
            }
            return view;
        }
    }

}
