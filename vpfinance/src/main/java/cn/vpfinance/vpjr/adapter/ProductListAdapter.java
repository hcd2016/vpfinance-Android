package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.gson.LoanSignListNewBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import cn.vpfinance.vpjr.view.SmallCircularProgressView;
import de.greenrobot.event.EventBus;

/**
 * Created by zzlz13 on 2017/9/11.
 */

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Integer> list = new ArrayList<>();
    private List<LoanSignListNewBean.LoansignsBean> mLoansigns;
    private Context mContext;
    private static final int ShenYangProductType = 0;//沈阳项目
    private static final int GeneralProductType = 1;//专项投资,存管专区,转让专区

    private int typeList = Constant.TYPE_REGULAR;
    private OnItemClickListener listener;

    public ProductListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<LoanSignListNewBean.LoansignsBean> data, int typeList) {
        if (data != null && data.size() != 0) {
            this.typeList = typeList;
            mLoansigns = data;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {//重写判断返回type
        try {
            LoanSignListNewBean.LoansignsBean product = mLoansigns.get(position);
            LoanSignListNewBean.LoansignsBean.LoansignBean loansign = product.loansign;
            int productType = loansign.productType;
            if (3 == productType) {
                return ShenYangProductType;
            } else {
                return GeneralProductType;
            }
        } catch (Exception e) {
            return GeneralProductType;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ShenYangProductType) {
            return new ShenYangViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_regular2_presell, parent, false));
        } else {
            return new GeneralViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_product_list, parent, false));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == ShenYangProductType){
            ShenYangViewHolder shenyangViewHolder = (ShenYangViewHolder)viewHolder;
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
        }else{
            GeneralViewHolder holder = (GeneralViewHolder)viewHolder;
            final LoanSignListNewBean.LoansignsBean product = mLoansigns.get(position);
            if (product != null){
                //国庆投资送iphone7活动
//                holder.iphone.setVisibility(product.givePhone == 1 ? View.VISIBLE : View.GONE);

                holder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null){
                            listener.onItemClick(product);
                        }
                    }
                });
                holder.countDown.setVisibility(View.GONE);
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

                    Common.productSubType(mContext,holder.ivProductState,loansign.subType);

//                    double pro = 100 * product.total_tend_money/loansign.issueLoan;
//                    String value = FormatUtils.formatDownByProgress(pro);
//                    holder.circular.setProgress((float) pro);
                    String value = product.process+"";
                    holder.progress.setProgress((int) product.process);
                    holder.tv_progress_num.setText(value+"%");


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
//                                holder.tvStatus.setVisibility(View.GONE);
                                holder.progress.setVisibility(View.GONE);
//                                holder.ll_Order.setVisibility(View.VISIBLE);
                                holder.countDown.setVisibility(View.VISIBLE);
//                                holder.rl_no_orlder.setVisibility(View.GONE);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
//                            holder.tvStatus.setVisibility(View.VISIBLE);
                            holder.progress.setVisibility(View.VISIBLE);
//                            holder.ll_Order.setVisibility(View.GONE);
                            holder.countDown.setVisibility(View.GONE);
                        }
                    }else{
//                        holder.tvStatus.setVisibility(View.VISIBLE);
//                        holder.circular.setVisibility(View.VISIBLE);
                        holder.progress.setVisibility(View.VISIBLE);
//                        holder.rl_no_orlder.setVisibility(View.VISIBLE);
//                        holder.ll_Order.setVisibility(View.GONE);
                        holder.countDown.setVisibility(View.GONE);
                    }
//                    holder.circular.setStrokeWidth(5);

                    holder.tv_progress_num.setText(value+"%");
                    holder.ll_progress_container.setVisibility(View.VISIBLE);
                    holder.progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.process_red_bg));

//                    holder.progress.setUnreachedBarColor(R.color.progress_normal);
//                    holder.progress.setReachedBarColor(R.color.progress_complete);
//                    holder.progress.setProgressTextColor(R.color.progress_complete);
                    String state = "";
                    switch (loanstate){ //1未发布 2进行中 3回款中 4已完成
                        case 1://预售
                            holder.iv_home_state.setVisibility(View.GONE);
//                            holder.progress.setVisibility(View.GONE);
                            holder.ll_progress_container.setVisibility(View.GONE);
                            holder.countDown.setVisibility(View.VISIBLE);
//                            state = mContext.getString(R.string.productState1);
//                            holder.iv_home_state.setImageResource(R.mipmap.chanp);

                            holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.red_text));
                            holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.red_text));
//                            holder.circle_background.setVisibility(View.GONE);
                            holder.tvTerm.setTextColor(mContext.getResources().getColor(R.color.text_1c1c1c));
                            holder.tvLoanTatol.setTextColor(mContext.getResources().getColor(R.color.text_1c1c1c));
                            holder.tv_addrate.setBackground(mContext.getResources().getDrawable(R.mipmap.jiaxi1));
                            holder.rewardIv.setImageResource(R.mipmap.jiaxi);
                            break;
                        case 2://进行中
//                            holder.circle_background.setVisibility(View.GONE);
                            state = value + "%";
                            holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.red_text));
                            holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.red_text));
                            if (product.process >= 100F) {//满标
                                state = "满标\n审核";
                                holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.red_text));
                                holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.red_text));
                                holder.iv_home_state.setVisibility(View.VISIBLE);
                                holder.iv_home_state.setImageResource(R.mipmap.chanpin_manbiao);
                            }else {
                                holder.iv_home_state.setVisibility(View.GONE);
                            }
//                            holder.circular.setProgress(value);
                            holder.ll_progress_container.setVisibility(View.VISIBLE);
                            holder.tv_progress_num.setText(value+"%");
                            holder.progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.process_red_bg));
//                            holder.progress.setProgress((int) product.process);
//                            holder.progress.setProgressDrawable(d);
//                            holder.progress.setUnreachedBarColor(R.color.progress_normal);
//                            holder.progress.setReachedBarColor(R.color.progress_complete);
//                            holder.progress.setProgressTextColor(R.color.progress_complete);
//                            holder.circular.setProgressColor(mContext.getResources().getColor(R.color.red_text));
//                            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.red_text));
                            holder.tvTerm.setTextColor(mContext.getResources().getColor(R.color.text_1c1c1c));
                            holder.tvLoanTatol.setTextColor(mContext.getResources().getColor(R.color.text_1c1c1c));
                            holder.tv_addrate.setBackground(mContext.getResources().getDrawable(R.mipmap.jiaxi1));
                            holder.rewardIv.setImageResource(R.mipmap.jiaxi);
                            break;
                        case 3://回款中
                            holder.iv_home_state.setVisibility(View.VISIBLE);
                            holder.iv_home_state.setImageResource(R.mipmap.chanpin_huaikuanzhong);
//                            holder.circle_background.setVisibility(View.VISIBLE);
                            state = mContext.getString(R.string.productState3);
                            holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                            holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.text_999999));
//                            holder.circular.setProgress(0);
//                            holder.circular.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
//                            holder.circular.setProgress(100);
//                            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                            holder.ll_progress_container.setVisibility(View.VISIBLE);
                            holder.progress.setProgress(100);


                            holder.ll_progress_container.setVisibility(View.VISIBLE);
                            holder.tv_progress_num.setTextColor(Utils.getColor(R.color.text_999999));
                            holder.tv_progress_num.setText(value+"%");
                            holder.progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.process_gray_bg));

//                            holder.progress.setProgressTextColor(mContext.getResources().getColor(R.color.text_999999));
//                            holder.progress.setReachedBarColor(mContext.getResources().getColor(R.color.process_gray_noamal));
//                            holder.progress.setUnreachedBarColor(mContext.getResources().getColor(R.color.process_gray_noamal));

                            holder.tvTerm.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                            holder.tvLoanTatol.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                            holder.tv_addrate.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rate_gray));
                            holder.rewardIv.setImageResource(R.drawable.ic_rate_gray);
                            break;
                        case 4://已完成
                            holder.iv_home_state.setVisibility(View.VISIBLE);
                            holder.iv_home_state.setImageResource(R.mipmap.chanpin_yiwancheng);
//                            holder.circle_background.setVisibility(View.VISIBLE);
                            state = mContext.getString(R.string.productState4);
                            holder.tvRate.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                            holder.tvRatePercent.setTextColor(mContext.getResources().getColor(R.color.text_999999));
//                            holder.circular.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
//                            holder.circular.setProgress(0);
//                            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_999999));

//                            holder.progress.setReachedBarColor(mContext.getResources().getColor(R.color.process_gray_noamal));
//                            holder.progress.setUnreachedBarColor(mContext.getResources().getColor(R.color.process_gray_noamal));
//                            holder.progress.setProgress(0);
//                            holder.progress.setProgressTextColor(mContext.getResources().getColor(R.color.text_999999));
                            holder.progress.setProgress(0);
                            holder.ll_progress_container.setVisibility(View.VISIBLE);
                            holder.tv_progress_num.setTextColor(Utils.getColor(R.color.text_999999));
                            holder.tv_progress_num.setText(value+"%");
                            holder.progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.process_gray_bg));



                            holder.tvTerm.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                            holder.tvLoanTatol.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                            holder.tv_addrate.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rate_gray));
                            holder.rewardIv.setImageResource(R.drawable.ic_rate_gray);
                            break;
                    }
//                    holder.tvStatus.setText(state);
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
//                        holder.tv_rate_des.setText("约定年利率");

                    }else if (typeList == Constant.TYPE_TRANSFER){
//                        holder.tvMonthInfo.setText("剩余期限");
//                        holder.tvTotalMoneyInfo.setText("转让总额");
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
//                        holder.tv_rate_des.setText("转让利率");
                        holder.rewardIv.setVisibility(View.INVISIBLE);
                    }
                }
                if(loansign.graceDays > 0) {//是浮动计息
                    holder.ivFdjx.setVisibility(View.VISIBLE);
                }else {
                    holder.ivFdjx.setVisibility(View.GONE);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return mLoansigns == null ? 0 : mLoansigns.size();
    }

    class GeneralViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rootView)
        RelativeLayout rootView;
//        @Bind(R.id.ll_Order)
//        LinearLayout ll_Order;
//        @Bind(R.id.rl_no_orlder)
//        RelativeLayout rl_no_orlder;
        @Bind(R.id.item_loan_title)
        TextView tvName;
//        @Bind(R.id.circle_background)
//        View circle_background;
        @Bind(R.id.item_loan_rate)
        TextView tvRate;
        @Bind(R.id.item_loan_rate_percent)
        TextView tvRatePercent;
//        @Bind(R.id.tv_rate_des)
//        TextView tv_rate_des;
        @Bind(R.id.item_loan_term)
        TextView tvTerm;
//        @Bind(R.id.status)
//        TextView tvStatus;
        @Bind(R.id.item_loan_totle)
        TextView tvLoanTatol;
//        @Bind(R.id.tvTotalMoneyInfo)
//        TextView tvTotalMoneyInfo;
//        @Bind(R.id.tvMonthInfo)
//        TextView tvMonthInfo;
        @Bind(R.id.rewardIv)
        ImageView rewardIv;
        @Bind(R.id.ivAllowTransfer)
        ImageView ivAllowTransfer;
        @Bind(R.id.ivProductState)
        ImageView ivProductState;
//        @Bind(R.id.circle)
//        SmallCircularProgressView circular;
//        @Bind(R.id.isAllowTrip)
//        ImageView ivAllowTrip;
//        @Bind(R.id.ivClean)
//        ImageView ivClean;
//        @Bind(R.id.iv_sign_type)
//        ImageView iv_sign_type;
//        @Bind(R.id.bankAccountStatus)
//        TextView bankAccountStatus;
        @Bind(R.id.countDown)
        MyCountDownTimer countDown;
        @Bind(R.id.tv_addrate)
        TextView tv_addrate;
//        @Bind(R.id.iphone7)
//        ImageView iphone;
        @Bind(R.id.iv_fdjx)
        ImageView ivFdjx;
        @Bind(R.id.iv_home_state)
        ImageView iv_home_state;
        @Bind(R.id.progress)
        ProgressBar progress;
        @Bind(R.id.tv_progress_num)
        TextView tv_progress_num;
        @Bind(R.id.ll_progress_container)
        LinearLayout ll_progress_container;



        public GeneralViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    class ShenYangViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvPresellName)
        TextView tvPresellName;
        @Bind(R.id.tvPresellRate)
        TextView tvPresellRate;
        @Bind(R.id.tvPresellState)
        TextView tvPresellState;
        @Bind(R.id.ivPresellPic)
        ImageView ivPresellPic;
        @Bind(R.id.ivAllowTransfer)
        ImageView ivAllowTransfer;
        @Bind(R.id.isAllowTrip)
        ImageView isAllowTrip;
        @Bind(R.id.presellCountDown)
        MyCountDownTimer presellCountDown;
        @Bind(R.id.presellNumberProgressBar)
        NumberProgressBar presellNumberProgressBar;
        public ShenYangViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(LoanSignListNewBean.LoansignsBean bean);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
