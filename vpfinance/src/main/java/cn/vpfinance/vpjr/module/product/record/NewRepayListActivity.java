package cn.vpfinance.vpjr.module.product.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.DepositTab1Bean;
import cn.vpfinance.vpjr.gson.NewBaseInfoBean;

/**
 * 剩余回款计划
 */
public class NewRepayListActivity extends BaseActivity {

    private ListView listView;
    private Context mContext;
    private static final String NO_REPAY_LIST = "no_repay_list";
    private boolean isDeposit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_available_time);
        mContext = this;

        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("回款计划").setHeadBackVisible(View.VISIBLE);
        listView = ((ListView) findViewById(R.id.listView));

        Intent intent = getIntent();
        if (intent != null){
            isDeposit = intent.getBooleanExtra("isDeposit",false);
            if (isDeposit){
                List<DepositTab1Bean.RepaysBean> list = intent.getParcelableArrayListExtra(NO_REPAY_LIST);
                if (list != null){
                    listView.setAdapter(new MyDepositAdapter(list,mContext));
                }
            }else{
                List<NewBaseInfoBean.RepaysEntity> list = intent.getParcelableArrayListExtra(NO_REPAY_LIST);
                if (list != null){
                    listView.setAdapter(new MyAdapter(list,mContext));
                }
            }
        }
    }

    public static void goNewRepayListActivity(Context context,boolean isDeposit,List<NewBaseInfoBean.RepaysEntity> noRepayList) {
        Intent intent = new Intent(context, NewRepayListActivity.class);
        intent.putParcelableArrayListExtra(NO_REPAY_LIST, (ArrayList<? extends Parcelable>) noRepayList);
        intent.putExtra("isDeposit",isDeposit);
        context.startActivity(intent);
    }

    public static void goNewRepayListByDepositActivity(Context mContext,boolean isDeposit, List<DepositTab1Bean.RepaysBean> repays) {
        Intent intent = new Intent(mContext, NewRepayListActivity.class);
        intent.putParcelableArrayListExtra(NO_REPAY_LIST, (ArrayList<? extends Parcelable>) repays);
        intent.putExtra("isDeposit",isDeposit);
        mContext.startActivity(intent);
    }

    class MyDepositAdapter extends BaseAdapter {
        private List<DepositTab1Bean.RepaysBean> data;
        private Context context;

        public MyDepositAdapter(List<DepositTab1Bean.RepaysBean> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size() == 0 ? 0 : data.size();
        }

        @Override
        public DepositTab1Bean.RepaysBean getItem(int position) {
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
                DepositTab1Bean.RepaysBean bean = data.get(position);
                if (bean != null) {
                    periods.setText(""+bean.periods);
                    repayStatus.setText(bean.repayStatus);
                    repaytime.setText(bean.repaytime);
                }
            }
            return view;
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<NewBaseInfoBean.RepaysEntity> data;
        private Context context;

        public MyAdapter(List<NewBaseInfoBean.RepaysEntity> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size() == 0 ? 0 : data.size();
        }

        @Override
        public NewBaseInfoBean.RepaysEntity getItem(int position) {
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
                NewBaseInfoBean.RepaysEntity bean = data.get(position);
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
