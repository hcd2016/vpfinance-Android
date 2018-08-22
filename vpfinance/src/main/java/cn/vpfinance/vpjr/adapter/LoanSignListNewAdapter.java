package cn.vpfinance.vpjr.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.gson.LoanSignListNewBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import cn.vpfinance.vpjr.view.SmallCircularProgressView;
import de.greenrobot.event.EventBus;

/**
 * 产品列表适配器-->转让专区+ 定期
 */
public class LoanSignListNewAdapter extends BaseAdapter{

    private List<LoanSignListNewBean.LoansignsBean> mLoansigns;
    private Context mContext;
    private static final int ShenYangProductType = 0;
    private static final int RegularProductType = 1;

    private int typeList = Constant.TYPE_REGULAR;

    public LoanSignListNewAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<LoanSignListNewBean.LoansignsBean> data,int typeList){
        if (data != null && data.size() != 0){
            this.typeList = typeList;
            mLoansigns = data;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {//重写判断返回type
        try{
            LoanSignListNewBean.LoansignsBean product = mLoansigns.get(position);
            LoanSignListNewBean.LoansignsBean.LoansignBean loansign = product.loansign;
            int productType = loansign.productType;
            if (3 == productType){
                return ShenYangProductType;
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
    public int getCount() {
        return mLoansigns == null ? 0 : mLoansigns.size();
    }

    @Override
    public LoanSignListNewBean.LoansignsBean getItem(int position) {
        return mLoansigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        if (type == RegularProductType){
            ViewHolder holder = null;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_regular2,parent,false);
                holder.ll_Order = (LinearLayout) convertView.findViewById(R.id.ll_Order);
                holder.rl_no_orlder = (RelativeLayout) convertView.findViewById(R.id.rl_no_orlder);
                holder.tv_addrate = (TextView) convertView.findViewById(R.id.tv_addrate);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_loan_title);
                holder.circle_background = convertView.findViewById(R.id.circle_background);
                holder.tvRate = (TextView) convertView.findViewById(R.id.item_loan_rate);
                holder.tvRatePercent = (TextView) convertView.findViewById(R.id.item_loan_rate_percent);
                holder.tv_rate_des = (TextView) convertView.findViewById(R.id.tv_rate_des);
                holder.tvTerm = (TextView) convertView.findViewById(R.id.item_loan_term);
                holder.tvStatus = (TextView) convertView.findViewById(R.id.status);
                holder.tvLoanTatol = (TextView) convertView.findViewById(R.id.item_loan_totle);
                holder.tvTotalMoneyInfo = (TextView) convertView.findViewById(R.id.tvTotalMoneyInfo);
                holder.tvMonthInfo = (TextView) convertView.findViewById(R.id.tvMonthInfo);
                holder.rewardIv = (ImageView) convertView.findViewById(R.id.rewardIv);
                holder.ivAllowTransfer = (ImageView) convertView.findViewById(R.id.ivAllowTransfer);
//                holder.ivProductState = (ImageView) convertView.findViewById(R.id.ivProductState);
                holder.circular = (SmallCircularProgressView) convertView.findViewById(R.id.circle);
//                holder.ivAllowTrip = (ImageView) convertView.findViewById(R.id.isAllowTrip);
//                holder.ivClean = (ImageView) convertView.findViewById(R.id.ivClean);
                holder.ivIphone7 = (ImageView) convertView.findViewById(R.id.iphone7);
//                holder.iv_sign_type = (ImageView) convertView.findViewById(R.id.iv_sign_type);
//                holder.bankAccountStatus = (TextView) convertView.findViewById(R.id.bankAccountStatus);

                holder.countDown = (MyCountDownTimer)convertView.findViewById(R.id.countDown);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            if(mLoansigns.size() == 0) return convertView;//size: 0-----position:64 快速滑动中瞬间切换转让专区会crash

            LoanSignListNewBean.LoansignsBean product = mLoansigns.get(position);
            if (product != null){
                if (product.givePhone == 1){
                    holder.ivIphone7.setVisibility(View.VISIBLE);
                }else{
                    holder.ivIphone7.setVisibility(View.GONE);
                }

                String allowTransfer = product.allowTransfer;
                if (!TextUtils.isEmpty(allowTransfer) && "true".equals(allowTransfer)){
                    holder.ivAllowTransfer.setVisibility(View.VISIBLE);
                }else{
                    holder.ivAllowTransfer.setVisibility(View.GONE);
                }
                double rate = 0;
                LoanSignListNewBean.LoansignsBean.LoansignBean loansign = product.loansign;
                if (loansign != null){

                    rate = loansign.rate;
                    rate *= 100;

                    Integer isAllowTrip = loansign.isAllowTrip;
//                    if (!TextUtils.isEmpty(isAllowTrip + "") && isAllowTrip == 1) {
//                        holder.ivAllowTrip.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.ivAllowTrip.setVisibility(View.GONE);
//                    }
                    int productType = loansign.productType;
//                    if (productType != 0 && loansign.product == 3 && productType == 5){
//                        holder.ivClean.setVisibility(View.VISIBLE);
//                    }else{
//                        holder.ivClean.setVisibility(View.GONE);
//                    }

//                    switch (loansign.loansignTypeId) {//区分标的类型
//                        case 1://车贷宝
//                            holder.iv_sign_type.setImageResource(R.drawable.icon_che);
//                            break;
//                        case 2://消费贷
//                            holder.iv_sign_type.setImageResource(R.drawable.icon_xiao);
//                            break;
//                        case 8://供应链
//                            holder.iv_sign_type.setImageResource(R.drawable.icon_gong);
//                            break;
//                        case 10://企业贷
//                            holder.iv_sign_type.setImageResource(R.drawable.icon_qi);
//                            break;
//                        case 11://珠宝贷
//                            holder.iv_sign_type.setImageResource(R.drawable.icon_zhu);
//                            break;
//                        case 12://融租宝
//                            holder.iv_sign_type.setImageResource(R.drawable.icon_rong);
//                            break;
//                        case 13://个人标
//                            holder.iv_sign_type.setImageResource(R.drawable.icon_ge);
//                            break;
//                    }

//                    Common.productSubType(mContext,holder.ivProductState,loansign.subType);

//                    double pro = 100 * product.total_tend_money/loansign.issueLoan;
//                    String value = FormatUtils.formatDownByProgress(pro);
//                    holder.circular.setProgress((float) pro);
                    String value = product.process+"";
                    holder.circular.setProgress(product.process);

                    int loanstate = loansign.loanstate;
                    if (loanstate == 1 && (!TextUtils.isEmpty(loansign.publishTime))){
                        try{
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = formatter.parse(loansign.publishTime);
                            long virtualTime = date.getTime();
                            if (virtualTime != 0){
                                long time = virtualTime - System.currentTimeMillis();

                                time = time/1000;
                                if(time>0 && time < 30*24*60*60) {
                                    Interval interval;
                                    interval = new Interval(System.currentTimeMillis(),virtualTime);//
                                    Period period = interval.toPeriod();
                                    time = Math.max(time,0);

                                    String tag = "" + loansign.id;
                                    holder.countDown.setTag(tag);
                                    holder.countDown.setCountDownTime(mContext,virtualTime);
                                    holder.countDown.setOnFinishListener(new MyCountDownTimer.onFinish() {
                                        @Override
                                        public void finish() {
                                            EventBus.getDefault().post(new RefreshCountDown(true));
                                        }
                                    });
                                }
                                holder.tvStatus.setVisibility(View.GONE);
                                holder.circular.setVisibility(View.GONE);
                                holder.ll_Order.setVisibility(View.VISIBLE);
                                holder.rl_no_orlder.setVisibility(View.GONE);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            holder.tvStatus.setVisibility(View.VISIBLE);
                            holder.circular.setVisibility(View.VISIBLE);
                            holder.ll_Order.setVisibility(View.GONE);
                        }
                    }else{
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.circular.setVisibility(View.VISIBLE);
                        holder.rl_no_orlder.setVisibility(View.VISIBLE);
                        holder.ll_Order.setVisibility(View.GONE);
                    }
                    holder.circular.setStrokeWidth(5);

                    String state = "";
                    switch (loanstate){ //1未发布 2进行中 3回款中 4已完成
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
                            if (product.process >= 100F) {
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

                holder.rewardIv.setVisibility(View.INVISIBLE);
                LoanSignListNewBean.LoansignsBean.LoansignbasicBean loansignbasic = product.loansignbasic;
                if (loansignbasic != null){
                    holder.tvName.setText(""+loansignbasic.loanTitle);
                    String rewardStr = loansignbasic.reward;
                    double reward = 0;
                    try{
                        reward = Double.parseDouble(rewardStr);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (typeList == Constant.TYPE_REGULAR || typeList == Constant.TYPE_BANK){
//                        if (loansign.product == 4){//银行存管
//                            holder.bankAccountStatus.setVisibility(View.VISIBLE);
//                        }else{
//                            holder.bankAccountStatus.setVisibility(View.GONE);
//                        }
                        if (loansign.loanType == 2){
                            holder.tvTerm.setText(loansign.month + "天");
                        }else{
                            holder.tvTerm.setText(loansign.month + "个月");
                        }
                        holder.tvLoanTatol.setText(FormatUtils.formatDown2(loansign.issueLoan / 10000) + "万");
                        if (reward > 0) {
                            reward *= 100;
                            String tmp = FormatUtils.formatRate(rate-reward);
                            holder.tvRate.setText(tmp);
                            holder.tv_addrate.setVisibility(View.VISIBLE);
                            holder.tv_addrate.setText("+"+FormatUtils.formatRate(reward) + "%");
                            holder.rewardIv.setVisibility(View.VISIBLE);
                        }else{
                            String tmp = FormatUtils.formatRate(rate);
                            holder.tvRate.setText(tmp);
                            holder.tv_addrate.setVisibility(View.GONE);
                        }
                        holder.tv_rate_des.setText("约定年利率");
                    }else if (typeList == Constant.TYPE_TRANSFER){
                        holder.tvMonthInfo.setText("剩余期限");
                        holder.tvTotalMoneyInfo.setText("转让总额");
//                        holder.bankAccountStatus.setVisibility(View.GONE);
                        if (loansign.loanType == 6 && loansign.refundWay == 3){
                            holder.tvTerm.setText(loansign.month + "天");
                        }else{
                            holder.tvTerm.setText(loansign.month + "个月");
                        }
                        if (loansign.issueLoan >= 10000){
                            holder.tvLoanTatol.setText(FormatUtils.formatDown2(loansign.issueLoan / 10000) + "万");
                        }else{
                            holder.tvLoanTatol.setText(FormatUtils.formatDown2(loansign.issueLoan));
                        }
                        if (reward > 0) {
                            holder.rewardIv.setVisibility(View.VISIBLE);
                        }
                        holder.tv_addrate.setVisibility(View.GONE);
                        double promitRate = product.promitRate*100;
                        String tmp = FormatUtils.formatDown(promitRate);
                        holder.tvRate.setText(tmp);
                        holder.tv_rate_des.setText("约定年利率");
                        holder.rewardIv.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }else if (type == ShenYangProductType){
            ShenyangViewHolder shenyangViewHolder = null;
            if (convertView == null){
                shenyangViewHolder = new ShenyangViewHolder();
                convertView = View.inflate(mContext,R.layout.item_regular2_presell,null);

                shenyangViewHolder.tvPresellName = (TextView)convertView.findViewById(R.id.tvPresellName);
                shenyangViewHolder.tvPresellRate = (TextView)convertView.findViewById(R.id.tvPresellRate);
                shenyangViewHolder.tvPresellState = (TextView)convertView.findViewById(R.id.tvPresellState);
                shenyangViewHolder.ivPresellPic = (ImageView)convertView.findViewById(R.id.ivPresellPic);
                shenyangViewHolder.ivAllowTransfer = (ImageView)convertView.findViewById(R.id.ivAllowTransfer);
                shenyangViewHolder.isAllowTrip = (ImageView)convertView.findViewById(R.id.isAllowTrip);
                shenyangViewHolder.presellCountDown = (MyCountDownTimer) convertView.findViewById(R.id.presellCountDown);
                shenyangViewHolder.presellNumberProgressBar = (NumberProgressBar)convertView.findViewById(R.id.presellNumberProgressBar);

                convertView.setTag(shenyangViewHolder);
            }else{
                shenyangViewHolder = (ShenyangViewHolder)convertView.getTag();
            }
            //初始化数据
            if(mLoansigns.size() == 0) return convertView;//size: 0-----position:64 快速滑动中瞬间切换转让专区会crash

            LoanSignListNewBean.LoansignsBean shenyangProduct = mLoansigns.get(position);
            if (shenyangProduct != null){
                String imageUrl = shenyangProduct.imageUrl;
                if (!TextUtils.isEmpty(imageUrl)){
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(false)
                            .cacheOnDisk(true)
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    ImageLoader.getInstance().displayImage(imageUrl, shenyangViewHolder.ivPresellPic, options);
                }
                shenyangViewHolder.tvPresellRate.setText(shenyangProduct.minRate+"+浮动收益(预期可超过"+shenyangProduct.maxRate+")");

                LoanSignListNewBean.LoansignsBean.LoansignbasicBean loansignbasic = shenyangProduct.loansignbasic;
                if (loansignbasic != null){
                    shenyangViewHolder.tvPresellName.setText(loansignbasic.loanTitle);
                }

                LoanSignListNewBean.LoansignsBean.LoansignBean loansign = shenyangProduct.loansign;
                if (loansign != null){
                    int loanstate = loansign.loanstate;
                    if (loanstate != 0){
                        String stateStr = "";
                        stateStr = loanstate == 1 ? mContext.getString(R.string.productState1) :
                                loanstate == 2 ? "进行中" :
                                        loanstate == 3 ? mContext.getString(R.string.productState3) :
                                                loanstate == 4 ? mContext.getString(R.string.productState4) : "";
                        shenyangViewHolder.tvPresellState.setText(stateStr);
                    }

                    double pro = 100 * shenyangProduct.total_tend_money/loansign.issueLoan;
                    shenyangViewHolder.presellNumberProgressBar.setProgress((float) pro);

                    String allowTransfer = shenyangProduct.allowTransfer;
                    if (!TextUtils.isEmpty(allowTransfer) && "true".equals(allowTransfer)){
                        shenyangViewHolder.ivAllowTransfer.setVisibility(View.VISIBLE);
                    }else{
                        shenyangViewHolder.ivAllowTransfer.setVisibility(View.GONE);
                    }

                    int isAllowTrip = loansign.isAllowTrip;
                    if (!TextUtils.isEmpty(isAllowTrip + "") && isAllowTrip == 1) {
                        shenyangViewHolder.isAllowTrip.setVisibility(View.VISIBLE);
                    } else {
                        shenyangViewHolder.isAllowTrip.setVisibility(View.GONE);
                    }

                    if (loanstate == 1 && (!TextUtils.isEmpty(loansign.publishTime))) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = formatter.parse(loansign.publishTime);
                            long virtualTime = date.getTime();
                            if (virtualTime != 0) {
                                long time = virtualTime - System.currentTimeMillis();

                                time = time / 1000;
                                if (time > 0 && time < 30 * 24 * 60 * 60) {

                                    String tag = "" + loansign.id;
                                    shenyangViewHolder.presellCountDown.setTag(tag);
                                    shenyangViewHolder.presellCountDown.setCountDownTime(mContext,virtualTime);
                                    shenyangViewHolder.presellCountDown.setOnFinishListener(new MyCountDownTimer.onFinish() {
                                        @Override
                                        public void finish() {
                                            EventBus.getDefault().post(new RefreshCountDown(true));
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return convertView;
    }

    private class ShenyangViewHolder{
        private TextView tvPresellName;
        private TextView tvPresellRate;
        private TextView tvPresellState;
        private NumberProgressBar presellNumberProgressBar;
        private ImageView ivPresellPic;
        private ImageView ivAllowTransfer;
        private ImageView isAllowTrip;
        private MyCountDownTimer presellCountDown;
    }

    private class ViewHolder {
        private RelativeLayout rl_no_orlder;
        private LinearLayout ll_Order;
        private TextView tv_addrate;
        private TextView tvRatePercent;
        private View circle_background;
        private MyCountDownTimer countDown;

        private TextView tvMonthInfo;
        private TextView tvTotalMoneyInfo;

//        private TextView bankAccountStatus;
        private TextView tvName;
        private TextView tvRate;
        private TextView tv_rate_des;
        private TextView tvTerm;
        private TextView tvLoanTatol;
        private TextView tvStatus;
        private ImageView rewardIv;
        private ImageView ivAllowTransfer;
//        private ImageView ivProductState;
//        private ImageView ivAllowTrip;
//        private ImageView ivClean;
        private ImageView ivIphone7;
//        private ImageView iv_sign_type;

        private SmallCircularProgressView circular;
    }
}
