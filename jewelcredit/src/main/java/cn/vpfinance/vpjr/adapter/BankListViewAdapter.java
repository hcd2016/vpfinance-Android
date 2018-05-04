package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.util.FileUtil;
import cn.vpfinance.vpjr.util.FormatUtils;

public class BankListViewAdapter extends BaseAdapter {
	private Context mContext;
	private List<BankCard> list;
	
	public BankListViewAdapter(Context context, List<BankCard> list) {
		mContext = context;
		this.list = list;
	}
	

	public void setModel(List<BankCard> list){
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
        public ImageView logo;
        public TextView name;
        public TextView no;
    }
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.item_bankcard, null);
			
			holder = new ViewHolder();
			holder.logo = (ImageView)convertView.findViewById(R.id.bankcard_img_logo);
			holder.name = (TextView)convertView.findViewById(R.id.bankcard_name);
			holder.no = (TextView)convertView.findViewById(R.id.bankcard_no);
			convertView.setTag(holder);     
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		BankCard card = (BankCard)getItem(position);

		if (card != null){
			holder.name.setText(card.getBankname());
			holder.no.setText(FormatUtils.hideBank(card.getBankAccount()));

			int res = FileUtil.getBankLogo(card);
			if (res > 0){
				holder.logo.setVisibility(View.VISIBLE);
				holder.logo.setBackgroundResource(res);
			}
		}else{
			holder.logo.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
}
