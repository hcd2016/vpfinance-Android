package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.FinanceProduct;

/**
 * 定期理财
 * 
 * @author cheungquentin
 *
 */
public class RegularAdapter extends BaseAdapter {
	private static final String TAG = "RegularAdapter";

	private List<FinanceProduct> list;

	private TopProductData topProductData = new TopProductData();

	private Context        mContext;
	private LayoutInflater mInflater;

	public RegularAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		int count = 0;
		if (topProductData != null && topProductData.hasTopProduct) {
			count = 1;
		}
		if (list != null) {
			count += list.size();
		}
		return count;
	}

	public void setTopUrl(String topUrl) {
		if (!TextUtils.isEmpty(topUrl)) {
			topProductData.hasTopProduct = true;
			topProductData.topProductUrl = topUrl;
		} else {
			topProductData.hasTopProduct = false;
		}
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		Object obj = null;
		if (topProductData.hasTopProduct) {
			if (position == 0) {
				obj = topProductData;
			} else {
				obj = list.get(position - 1);
			}
		} else {
			obj = list.get(position);
		}
		return  obj;
	}

	public void setList(List<FinanceProduct> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public static class TopProductData {
		public boolean hasTopProduct;
		public String topProductUrl;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_regular, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.item_loan_title);
			holder.tvRate = (TextView) convertView.findViewById(R.id.item_loan_rate);
			holder.tvTerm = (TextView) convertView.findViewById(R.id.item_loan_term);
			holder.tvPayType = (TextView) convertView.findViewById(R.id.item_payType);
			holder.tvStatus = (TextView) convertView.findViewById(R.id.status);

			
			//holder.tvTotalTime = (TextView) convertView.findViewById(R.id.item_money);
			//holder.tvProgress = (TextView) convertView.findViewById(R.id.product_progress);
			//holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
			holder.numberProgressBar = (NumberProgressBar) convertView.findViewById(R.id.numberbar);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final FinanceProduct product;

		if (topProductData.hasTopProduct) {
			if (position == 0) {
				holder.tvName.setText("V生活-魔方-001");
				holder.tvRate.setText("9~11%");
				holder.tvTerm.setText("3个月");
				holder.tvPayType.setText("到期一次性还本息");
				holder.tvStatus.setText("预售中");
				holder.numberProgressBar.setProgress(0);
				return convertView;
			} else {
				product = list.get(position - 1);
			}
		} else {
			product = list.get(position);
		}

		if (product != null) {
			holder.tvName.setText(product.getLoanTitle());
			double rate = product.getRate();
			rate *= 100;
			String tmp = String.format("%.1f", rate);
//			if (tmp.endsWith(".0")) {
//				tmp = tmp.substring(0, tmp.length() - 2);
//			}
			holder.tvRate.setText(tmp + "%");
			double pro = 100 * product.getTotal_tend_money()/product.getIssueLoan();
			//holder.tvProgress.setText(String.format("%.0f", pro)+ "%");
			holder.tvTerm.setText(product.getMonth() + "个月");
			//holder.tvTotalTime.setText("" + product.getIssueLoan());
			//holder.progressBar.setProgress((int) pro);
			holder.numberProgressBar.setProgress((float) pro);
			
			String pType = "";//1按月等额本息 2按月付息到期还本 3到期一次性还本息
			switch ((int)product.getRefundWay()) {
			case 1:
				pType = mContext.getString(R.string.refundState1);
				break;
			case 2:
				pType = mContext.getString(R.string.refundState2);
				break;
			case 3:
				pType = mContext.getString(R.string.refundState3);
				break;
			default:
				break;
			}
			Drawable right = null;
			//1质押，2保证，3抵押，4信用，5实地
			switch ((int)product.getSubType()) {
			case 1:
				right = mContext.getResources().getDrawable(R.drawable.stype_zhi);
				break;
			case 2:
				right = mContext.getResources().getDrawable(R.drawable.stype_bao);
				break;
			case 3:
				right = mContext.getResources().getDrawable(R.drawable.stype_ya);
				break;
			case 4:
				right = mContext.getResources().getDrawable(R.drawable.stype_xin);
				break;
			case 5:
				right = mContext.getResources().getDrawable(R.drawable.stype_ya);
				break;
			default:
				break;
			}
			holder.tvName.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
			
			holder.tvPayType.setText(pType);


			String state = "";
			switch ((int)product.getLoanstate())//1未发布 2进行中 3回款中 4已完成
			{
				case 1:
					state = mContext.getString(R.string.productState1);
					break;
				case 2:
					state = mContext.getString(R.string.productState2);
					if (product.getTotal_tend_money() >= product.getIssueLoan()) {
						state = mContext.getString(R.string.productStateFill);
					}
					break;
				case 3:
					state = mContext.getString(R.string.productState3);
					break;
				case 4:
					state = mContext.getString(R.string.productState4);
					break;
			}

			holder.tvStatus.setText(state);
		}

		return convertView;
	}

	private class ViewHolder {
		private TextView tvName;
		private TextView tvRate;
		private TextView tvTerm;
		private TextView tvPayType;

		private TextView tvStatus;
		
		//private TextView tvTotalTime;
		//private TextView tvProgress;
		//private ProgressBar progressBar;
		private NumberProgressBar numberProgressBar;
	}
}