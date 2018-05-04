package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.vpfinance.android.R;
import com.jewelcredit.model.RechargeInfo;
import com.jewelcredit.model.RechargeModel;

public class RechargeListViewAdapter extends BaseAdapter{
	private Context mContext;
	private RechargeModel mModel;
	
	public RechargeListViewAdapter(Context context, RechargeModel model) {
		mContext = context;
		mModel = model;
	}
	
	
	public void setModel(RechargeModel model){
		mModel = model;
	}
	
	
	public void appendData(RechargeModel model){
		for(int i = 0; i < model.records.size(); i ++)
		{
			mModel.records.add(model.records.get(i));
		}
	}
	

	@Override
	public int getCount() {
		return mModel.records.size();
	}

	
	@Override
	public Object getItem(int position) {
		return mModel.records.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public final class ViewHolder {      
        public TextView status;      
        public TextView money;
        public TextView loanno;
        public TextView timestamp;
    }
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.item_recharge_record, null);
			
			holder = new ViewHolder();
			holder.status = (TextView)convertView.findViewById(R.id.recharge_status);
			holder.money = (TextView)convertView.findViewById(R.id.recharge_money);
			holder.loanno = (TextView)convertView.findViewById(R.id.recharge_loanno);
			holder.timestamp = (TextView)convertView.findViewById(R.id.recharge_timestamp);
			convertView.setTag(holder);     
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		RechargeInfo item = (RechargeInfo)getItem(position);
		holder.status.setText(item.payStatusDesc);
		holder.money.setText("￥" + item.amount);
		holder.loanno.setText(item.no);
		holder.timestamp.setText(item.applyTime);
		return convertView;
	}
}
