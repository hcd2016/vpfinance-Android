package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductListFragment2;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import cn.vpfinance.vpjr.view.SmallCircularProgressView;
import de.greenrobot.event.EventBus;

/**
 * 定期
 * 
 * @author cheungquentin
 *
 */
public class RegularAdapter2 extends BaseAdapter {
	private static final String TAG = "RegularAdapter";

	private final static int RegularProductType = 0;  //注意type从0开始
	private final static int PresellProductType = 1;

	private List<FinanceProduct> list;
//	private LongSparseArray<MyCounter> map = new LongSparseArray<MyCounter>();


	private Context mContext;
	private LayoutInflater mInflater;
	private int typeList = RegularProductListFragment2.REGULAR_PRODUCT_LIST;

	public RegularAdapter2(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		int count = 0;
		if (list != null) {
			count += list.size();
		}
		return  count;
	}

	@Override
	public Object getItem(int position) {
		Object obj = null;
		obj = list.get(position);
		return  obj;
	}

	public void setList(List<FinanceProduct> list,int type) {
		this.list = list;
		this.typeList = type;
		notifyDataSetChanged();
	}

	public static class TopProductData {
		public boolean hasTopProduct;
		public String topProductUrl;
	}

	@Override
	public int getItemViewType(int position) {//重写判断返回type
		try{
			FinanceProduct product = list.get(position);
			String productType = product.getProductType();

			if ("3".equals(productType)){
				return PresellProductType;
			}else {
				return RegularProductType;
			}
		}catch (Exception e){
			return RegularProductType;
		}
	}

	@Override
	public int getViewTypeCount() {//返回type数目
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View regularItemView = null;
		View presellItemView = null;
//		convertView = null;
		int type = getItemViewType(position);
//		if (type == RegularProductType){
			ViewHolder holder = null;
			if (convertView == null){
				holder = new ViewHolder();
				regularItemView = mInflater.inflate(R.layout.item_regular2, null);
				holder.ll_Order = (LinearLayout) regularItemView.findViewById(R.id.ll_Order);
				holder.rl_no_orlder = (RelativeLayout) regularItemView.findViewById(R.id.rl_no_orlder);
				holder.tv_addrate = (TextView) regularItemView.findViewById(R.id.tv_addrate);
				//				holder.presellStatus = (TextView) regularItemView.findViewById(R.id.presellStatus);
				holder.tvName = (TextView) regularItemView.findViewById(R.id.item_loan_title);
				holder.circle_background = regularItemView.findViewById(R.id.circle_background);
				holder.tvRate = (TextView) regularItemView.findViewById(R.id.item_loan_rate);
				holder.tvRatePercent = (TextView) regularItemView.findViewById(R.id.item_loan_rate_percent);
				holder.tv_rate_des = (TextView) regularItemView.findViewById(R.id.tv_rate_des);
				holder.tvTerm = (TextView) regularItemView.findViewById(R.id.item_loan_term);
//				holder.tvPayType = (TextView) regularItemView.findViewById(R.id.item_payType);
				holder.tvStatus = (TextView) regularItemView.findViewById(R.id.status);
				holder.tvLoanTatol = (TextView) regularItemView.findViewById(R.id.item_loan_totle);
				holder.tvTotalMoneyInfo = (TextView) regularItemView.findViewById(R.id.tvTotalMoneyInfo);
				holder.tvMonthInfo = (TextView) regularItemView.findViewById(R.id.tvMonthInfo);
				holder.rewardIv = (ImageView) regularItemView.findViewById(R.id.rewardIv);
				holder.ivAllowTransfer = (ImageView) regularItemView.findViewById(R.id.ivAllowTransfer);
				holder.ivProductState = (ImageView) regularItemView.findViewById(R.id.ivProductState);
				holder.circular = (SmallCircularProgressView) regularItemView.findViewById(R.id.circle);
				holder.ivAllowTrip = (ImageView) regularItemView.findViewById(R.id.isAllowTrip);
				holder.ivClean = (ImageView) regularItemView.findViewById(R.id.ivClean);
				holder.ivIphone7 = (ImageView) regularItemView.findViewById(R.id.iphone7);

				holder.countDown = (MyCountDownTimer)regularItemView.findViewById(R.id.countDown);
//				holder.countDown = regularItemView.findViewById(R.id.countDown);
//				holder.countdown_day = (TextView)regularItemView.findViewById(R.id.countdown_day);
//				holder.countdown_hour = (TextView)regularItemView.findViewById(R.id.countdown_hour);
//				holder.countdown_minute = (TextView)regularItemView.findViewById(R.id.countdown_minute);
//				holder.countdown_second = (TextView)regularItemView.findViewById(R.id.countdown_second);
//				holder.tvStartBuy = (TextView)regularItemView.findViewById(R.id.tvStartBuy);
//				holder.rlCountDown = (LinearLayout)regularItemView.findViewById(R.id.rlCountDown);
				regularItemView.setTag(holder);
				convertView = regularItemView;
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			//初始化数据
			final FinanceProduct product;
		if(list.size() == 0) return convertView;//size: 0-----position:64 快速滑动中瞬间切换转让专区会crash
			product = list.get(position);
			if (product != null) {
				if (product.getGivePhone() != null && "1".equals(product.getGivePhone())){
					holder.ivIphone7.setVisibility(View.VISIBLE);
				}else{
					holder.ivIphone7.setVisibility(View.GONE);
				}
				holder.tvMonthInfo.setText(typeList == RegularProductListFragment2.REGULAR_PRODUCT_LIST ? "项目期限" : "剩余期限");
				holder.tvTotalMoneyInfo.setText(typeList == RegularProductListFragment2.REGULAR_PRODUCT_LIST ? "项目总额" : "转让总额");

				holder.tvName.setText(product.getLoanTitle());
				Double promitRate = product.getPromitRate();
				promitRate *= 100;
				double rate = product.getRate();
				rate *= 100;

				String reward = product.getReward();
				holder.rewardIv.setVisibility(View.INVISIBLE);

				String allowTransfer = product.getAllowTransfer();
				if (!TextUtils.isEmpty(allowTransfer) && "true".equals(allowTransfer)){
					holder.ivAllowTransfer.setVisibility(View.VISIBLE);
				}else{
					holder.ivAllowTransfer.setVisibility(View.GONE);
				}

				Integer isAllowTrip = product.getIsAllowTrip();
				if (!TextUtils.isEmpty(isAllowTrip + "") && isAllowTrip == 1) {
					holder.ivAllowTrip.setVisibility(View.VISIBLE);
				} else {
					holder.ivAllowTrip.setVisibility(View.GONE);
				}

				String productType = product.getProductType();
//				Logger.e("productType:"+productType+",product.getType:"+product.getType());
				if (!TextUtils.isEmpty(productType) && product.getProduct() == 3 && "5".equals(product.getProductType())){
					holder.ivClean.setVisibility(View.VISIBLE);
				}else{
					holder.ivClean.setVisibility(View.GONE);
				}

				float val = 0;
				if(!TextUtils.isEmpty(reward))
				{
					try {
						//reward = reward.replaceAll("%","");
						val = Float.parseFloat(reward);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}

				if (typeList == RegularProductListFragment2.REGULAR_PRODUCT_LIST){
						holder.tv_rate_des.setText("约定年利率");
					if (val > 0) {
						val *= 100;
						String tmp = String.format("%.1f", rate-val);
						holder.tvRate.setText(tmp);
						holder.tv_addrate.setVisibility(View.VISIBLE);
						holder.tv_addrate.setText(String.format("+" + "%.1f", val) + "%");
						holder.rewardIv.setVisibility(View.VISIBLE);
					}
					else
					{
						String tmp = String.format("%.1f", rate);
						/*if (tmp.endsWith(".0")) {
							tmp = tmp.substring(0, tmp.length() - 2);
						}*/
						holder.tvRate.setText(tmp);
						holder.tv_addrate.setVisibility(View.GONE);
					}
				}else if (typeList == RegularProductListFragment2.TRANSFER_PRODUCT_LIST) {
					if (val > 0) {
						holder.rewardIv.setVisibility(View.VISIBLE);
					}
					holder.tv_addrate.setVisibility(View.GONE);
					String tmp = String.format("%.2f", promitRate);
					holder.tvRate.setText(tmp);
					holder.tv_rate_des.setText("约定年利率");
					holder.rewardIv.setVisibility(View.INVISIBLE);
				}

//				double p = 100 * product.getTotal_tend_money() / product.getIssueLoan();
//				String value = new DecimalFormat("#0.00").format(p);
//				if (value != null && value.startsWith("100") && product.getTotal_tend_money() < product.getIssueLoan()) {
//					value = "99.99";
//				}

//				double pro = 100 * product.getTotal_tend_money()/product.getIssueLoan();
				//holder.tvProgress.setText(String.format("%.0f", pro)+ "%");
				if (product.getLoanType() == 2){
					holder.tvTerm.setText(product.getMonth() + "天");
				}else{
					holder.tvTerm.setText(product.getMonth() + "个月");
				}
				if (typeList == RegularProductListFragment2.REGULAR_PRODUCT_LIST){
					holder.tvLoanTatol.setText(FormatUtils.formatDown2(product.getIssueLoan() / 10000) + "万");
				}else if (typeList == RegularProductListFragment2.TRANSFER_PRODUCT_LIST){
					if (product.getIssueLoan() >= 10000){
						holder.tvLoanTatol.setText(FormatUtils.formatDown2(product.getIssueLoan() / 10000) + "万");
					}else{
						holder.tvLoanTatol.setText(FormatUtils.formatDown2(product.getIssueLoan()));
					}
				}
				double pro = 100 * product.getTotal_tend_money()/product.getIssueLoan();
				String value = FormatUtils.formatDownByProgress(pro);

				holder.circular.setProgress((float) pro);

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

				Common.productSubType(mContext,holder.ivProductState,(int)product.getSubType());

//				holder.tvPayType.setText(pType);

				int loanstate = (int) product.getLoanstate();
				if (loanstate == 1 && (!TextUtils.isEmpty(product.getPublishTime()))){
					try{
						long virtualTime = Long.parseLong(product.getPublishTime());
						if (virtualTime != 0){
							long time = virtualTime - System.currentTimeMillis();

							time = time/1000;
							if(time>0 && time < 30*24*60*60)
							{
								Interval interval;
								interval = new Interval(System.currentTimeMillis(),virtualTime);//
								Period period = interval.toPeriod();
								time = Math.max(time,0);

//								holder.countdown_day.setText("" + (period.getWeeks() * 7 + period.getDays()));
//								holder.countdown_hour.setText("" + period.getHours());
//								holder.countdown_minute.setText("" + period.getMinutes());
//								holder.countdown_second.setText("" + period.getSeconds());

//								MyCounter counter = map.get(product.getId());
//								if(counter!=null)
//								{
//									counter.cancel();
//								}
								String tag = "" + product.getId();
								holder.countDown.setTag(tag);
								holder.countDown.setCountDownTime(mContext,virtualTime);
								holder.countDown.setOnFinishListener(new MyCountDownTimer.onFinish() {
									@Override
									public void finish() {
										EventBus.getDefault().post(new RefreshCountDown(true));
									}
								});
//								counter = new MyCounter(virtualTime - System.currentTimeMillis(), 1000, holder.countDown,virtualTime,tag);
//								counter.start();
//								map.put(product.getId(), counter);
							}
							holder.tvStatus.setVisibility(View.GONE);
							holder.circular.setVisibility(View.GONE);
							holder.ll_Order.setVisibility(View.VISIBLE);
							holder.rl_no_orlder.setVisibility(View.GONE);
//							holder.presellStatus.setVisibility(View.VISIBLE);
//							holder.tvStartBuy.setVisibility(View.VISIBLE);
//							holder.rlCountDown.setVisibility(View.VISIBLE);
						}
					}catch (Exception e){
						e.printStackTrace();
						holder.tvStatus.setVisibility(View.VISIBLE);
						holder.circular.setVisibility(View.VISIBLE);
						holder.ll_Order.setVisibility(View.GONE);
//						holder.rlCountDown.setVisibility(View.GONE);
//						holder.tvStartBuy.setVisibility(View.GONE);
//						holder.presellStatus.setVisibility(View.GONE);
					}
				}else{
					holder.tvStatus.setVisibility(View.VISIBLE);
					holder.circular.setVisibility(View.VISIBLE);
					holder.rl_no_orlder.setVisibility(View.VISIBLE);
					holder.ll_Order.setVisibility(View.GONE);
//					holder.presellStatus.setVisibility(View.GONE);
//					holder.rlCountDown.setVisibility(View.GONE);
//					holder.tvStartBuy.setVisibility(View.GONE);
				}
				holder.circular.setStrokeWidth(5);
				String state = "";
				switch (loanstate)//1未发布 2进行中 3回款中 4已完成
				{
					case 1:
						state = mContext.getString(R.string.productState1);
						holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.red_text));
						holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.red_text));
						holder.circle_background.setVisibility(View.GONE);
						holder.tvTerm.setTextColor(mContext.getResources().getColor(R.color.text_1c1c1c));
						holder.tvLoanTatol.setTextColor(mContext.getResources().getColor(R.color.text_1c1c1c));
						holder.tv_addrate.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rate));
						holder.rewardIv.setImageResource(R.drawable.ic_rate);
						break;
					case 2:
						holder.circle_background.setVisibility(View.GONE);
						state = value + "%";
						holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.red_text));
						holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.red_text));
						if (product.getTotal_tend_money() >= product.getIssueLoan()) {
							state = "满标\n审核";
							holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.red_text));
							holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.red_text));
						}
						holder.circular.setProgress(value);
						holder.circular.setProgressColor(mContext.getResources().getColor(R.color.red_text));
						holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.red_text));
						holder.tvTerm.setTextColor(mContext.getResources().getColor(R.color.text_1c1c1c));
						holder.tvLoanTatol.setTextColor(mContext.getResources().getColor(R.color.text_1c1c1c));
						holder.tv_addrate.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rate));
						holder.rewardIv.setImageResource(R.drawable.ic_rate);
						break;
					case 3:
						holder.circle_background.setVisibility(View.VISIBLE);
						state = mContext.getString(R.string.productState3);
						holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.circular.setProgress(0);
//						if (typeList == RegularProductListFragment2.TRANSFER_PRODUCT_LIST) {
//							state = "转让\n完成";
//						}
						holder.circular.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
						holder.circular.setProgress(100);
						holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.tvTerm.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.tvLoanTatol.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.tv_addrate.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rate_gray));
						holder.rewardIv.setImageResource(R.drawable.ic_rate_gray);
						break;
					case 4:
						holder.circle_background.setVisibility(View.VISIBLE);
						state = mContext.getString(R.string.productState4);
						holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.circular.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
						holder.circular.setProgress(0);
						holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.tvTerm.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.tvLoanTatol.setTextColor(mContext.getResources().getColor(R.color.text_999999));
						holder.tv_addrate.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rate_gray));
						holder.rewardIv.setImageResource(R.drawable.ic_rate_gray);
						break;
				}
				holder.tvStatus.setText(state);
			}

//		}
		/*else if (type == PresellProductType){
			PresellViewHolder presellViewHolder = null;
			if (convertView == null){
				presellViewHolder = new PresellViewHolder();
				presellItemView = mInflater.inflate(R.layout.item_regular2_presell,null);

				presellViewHolder.tvPresellName = (TextView)presellItemView.findViewById(R.id.tvPresellName);
				presellViewHolder.tvPresellRate = (TextView)presellItemView.findViewById(R.id.tvPresellRate);
				presellViewHolder.tvPresellState = (TextView)presellItemView.findViewById(R.id.tvPresellState);
				presellViewHolder.ivPresellPic = (ImageView)presellItemView.findViewById(R.id.ivPresellPic);
				presellViewHolder.ivAllowTransfer = (ImageView)presellItemView.findViewById(R.id.ivAllowTransfer);
				presellViewHolder.isAllowTrip = (ImageView)presellItemView.findViewById(R.id.isAllowTrip);
				presellViewHolder.presellCountDown = presellItemView.findViewById(R.id.presellCountDown);
				presellViewHolder.presellNumberProgressBar = (NumberProgressBar)presellItemView.findViewById(R.id.presellNumberProgressBar);

				presellViewHolder.countdown_day = (TextView)presellItemView.findViewById(R.id.countdown_day);
				presellViewHolder.countdown_hour = (TextView)presellItemView.findViewById(R.id.countdown_hour);
				presellViewHolder.countdown_minute = (TextView)presellItemView.findViewById(R.id.countdown_minute);
				presellViewHolder.countdown_second = (TextView)presellItemView.findViewById(R.id.countdown_second);
				presellItemView.setTag(presellViewHolder);
				convertView = presellItemView;
			}else{
				presellViewHolder = (PresellViewHolder)convertView.getTag();
			}
			//初始化数据
			final FinanceProduct presellProduct;
			presellProduct = list.get(position);

			if (presellProduct != null){

				if (!TextUtils.isEmpty(presellProduct.getImageUrl())){
					DisplayImageOptions options = new DisplayImageOptions.Builder()
						.cacheInMemory(false)
						.cacheOnDisk(true)
						.imageScaleType(ImageScaleType.EXACTLY)
						.bitmapConfig(Bitmap.Config.RGB_565)
						.build();
					ImageLoader.getInstance().displayImage(presellProduct.getImageUrl(), presellViewHolder.ivPresellPic, options);
				}

				presellViewHolder.tvPresellName.setText(presellProduct.getLoanTitle());
//				double rate = presellProduct.getRate();
//				rate *= 100;
//				presellViewHolder.tvPresellRate.setText(String.format("%.1f",rate) + "%");
				presellViewHolder.tvPresellRate.setText(presellProduct.getMinRate()+"+浮动收益(预期可超过"+presellProduct.getMaxRate()+")");
				if (presellProduct.getLoanstate() != 0){
					long loanstate = presellProduct.getLoanstate();
					String stateStr = "";
					stateStr = loanstate == 1 ? mContext.getString(R.string.productState1) :
							loanstate == 2 ? "进行中" :
									loanstate == 3 ? mContext.getString(R.string.productState3) :
											loanstate == 4 ? mContext.getString(R.string.productState4) : "";
					presellViewHolder.tvPresellState.setText(stateStr);
				}

				double p = 100 * presellProduct.getTotal_tend_money() / presellProduct.getIssueLoan();
				String value = new DecimalFormat("#0.0").format(p);
				if (value != null && value.startsWith("100") && presellProduct.getTotal_tend_money() < presellProduct.getIssueLoan()) {
					value = "99.9";
				}

				double pro = 100 * presellProduct.getTotal_tend_money()/presellProduct.getIssueLoan();
				if("99.9".equals(value))
				{
					pro = 99;
				}
				presellViewHolder.presellNumberProgressBar.setProgress((float)pro);
				String allowTransfer = presellProduct.getAllowTransfer();
				if (!TextUtils.isEmpty(allowTransfer) && "true".equals(allowTransfer)){
					presellViewHolder.ivAllowTransfer.setVisibility(View.VISIBLE);
				}else{
					presellViewHolder.ivAllowTransfer.setVisibility(View.GONE);
				}
				Integer isAllowTrip = presellProduct.getIsAllowTrip();
				if (!TextUtils.isEmpty(isAllowTrip + "") && isAllowTrip == 1) {
					presellViewHolder.isAllowTrip.setVisibility(View.VISIBLE);
				} else {
					presellViewHolder.isAllowTrip.setVisibility(View.GONE);
				}

				int day = 0;
				int hour = 0;
				int minute = 0;
				int second = 0;

				presellViewHolder.countdown_day.setText("" + day);
				presellViewHolder.countdown_hour.setText("" + hour);
				presellViewHolder.countdown_minute.setText("" + minute);
				presellViewHolder.countdown_second.setText("" + second);

				long virtualTime = 0;
				virtualTime = presellProduct.getBidEndTime();

				if (virtualTime != 0){
					long time = virtualTime - System.currentTimeMillis();

//            time = 20*24*60*60 * 1000 + 8*60*60 * 1000 + 18*60 * 1000   + 16 * 1000;
					time = time/1000;
					if(time>0 && time < 30*24*60*60)
					{
						//System.currentTimeMillis() + 20*24*60*60 * 1000 + 8*60*60 * 1000   + 18*60 * 1000   + 16 * 1000
						Interval interval;
						interval = new Interval(System.currentTimeMillis(),virtualTime);//
						Period period = interval.toPeriod();
						time = Math.max(time,0);
						//Period p = Period.seconds((int)time);

						presellViewHolder.countdown_day.setText("" + (period.getWeeks() * 7 + period.getDays()));
						presellViewHolder.countdown_hour.setText("" + period.getHours());
						presellViewHolder.countdown_minute.setText("" + period.getMinutes());
						presellViewHolder.countdown_second.setText("" + period.getSeconds());
//						countDown.setText(p.getDays()+"天 " + p.getHours()+ "时 "+p.getMinutes()+"分 "+p.getSeconds()+" 秒");

						MyCounter counter = map.get(presellProduct.getId());
						if(counter!=null)
						{
							counter.cancel();
						}
						String tag = "" + presellProduct.getId();
						presellViewHolder.presellCountDown.setTag(tag);
						counter = new MyCounter(virtualTime - System.currentTimeMillis(), 1000, presellViewHolder.presellCountDown,virtualTime,tag);
						counter.start();
						map.put(presellProduct.getId(), counter);
					}
				}
			}
		}*/
		return convertView;
	}

	private class ViewHolder {
		private RelativeLayout rl_no_orlder;
		private LinearLayout ll_Order;
		private TextView tv_addrate;
		private TextView tvRatePercent;
		private View circle_background;
//		private LinearLayout rlCountDown;
//		private View countDown;
//		private TextView presellStatus;
//		private TextView countdown_day;
//		private TextView countdown_hour;
//		private TextView countdown_minute;
//		private TextView countdown_second;
//		private TextView tvStartBuy;
		private MyCountDownTimer countDown;

		private TextView tvMonthInfo;
		private TextView tvTotalMoneyInfo;

		private TextView tvName;
		private TextView tvRate;
		private TextView tv_rate_des;
		private TextView tvTerm;
//		private TextView tvPayType;
		private TextView tvLoanTatol;
		private TextView tvStatus;
		private ImageView rewardIv;
		private ImageView ivAllowTransfer;
		private ImageView ivProductState;
		private ImageView ivAllowTrip;
		private ImageView ivClean;
		private ImageView ivIphone7;

		private SmallCircularProgressView circular;
	}

	private class PresellViewHolder{
		private TextView tvPresellName;
		private TextView tvPresellRate;
		private TextView tvPresellState;
		private View presellCountDown;
		private NumberProgressBar presellNumberProgressBar;
		private ImageView ivPresellPic;
		private ImageView ivAllowTransfer;
		private ImageView isAllowTrip;

		private TextView countdown_day;
		private TextView countdown_hour;
		private TextView countdown_minute;
		private TextView countdown_second;
	}
	/* 定义一个倒计时的内部类 */
	/*class MyCounter extends CountDownTimer
	{
		private WeakReference<View> viewRef;
		private long endTime;
		private String tag;
		public MyCounter(long millisInFuture, long countDownInterval,View countDown,long endTime,String tag)
		{
			super(millisInFuture, countDownInterval);
			viewRef = new WeakReference<View>(countDown);
			this.endTime = endTime;
			this.tag = tag;
		}

		@Override
		public void onFinish()
		{
			if (viewRef != null)
			{
				View countDown = viewRef.get();
				if (countDown != null)
				{
					
					EventBus.getDefault().post(new RefreshListInfo(true));
					countDown.setVisibility(View.GONE);
				}
			}
		}

		@Override
		public void onTick(long millisUntilFinished)
		{
			if (viewRef != null)
			{
				View countDown = viewRef.get();
				if (countDown != null && tag.equals((String)countDown.getTag()))
				{
					TextView countdown_day = (TextView)countDown.findViewById(R.id.countdown_day);
					TextView countdown_hour = (TextView)countDown.findViewById(R.id.countdown_hour);
					TextView countdown_minute = (TextView)countDown.findViewById(R.id.countdown_minute);
					TextView countdown_second = (TextView)countDown.findViewById(R.id.countdown_second);


					long time = millisUntilFinished;
//            time = 20*24*60*60 * 1000 + 8*60*60 * 1000 + 18*60 * 1000   + 16 * 1000;
					time = time/1000;
					if(time>0 && time < 30*24*60*60)
					{
						//System.currentTimeMillis() + 20*24*60*60 * 1000 + 8*60*60 * 1000   + 18*60 * 1000   + 16 * 1000
						Interval interval = new Interval(System.currentTimeMillis(), endTime);//
						Period p = interval.toPeriod();
						time = Math.max(time,0);
						//Period p = Period.seconds((int)time);
						countDown.setVisibility(View.VISIBLE);

						countdown_day.setText("" + (p.getWeeks() * 7 + p.getDays()));
						countdown_hour.setText("" + p.getHours());
						countdown_minute.setText("" + p.getMinutes());
						countdown_second.setText("" + p.getSeconds());
						//countDown.setText(p.getDays()+"天 " + p.getHours()+ "时 "+p.getMinutes()+"分 "+p.getSeconds()+" 秒");
					}
				}
			}
		}
	}*/
}