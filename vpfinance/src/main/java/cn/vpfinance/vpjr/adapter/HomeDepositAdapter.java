package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.gson.AppmemberIndexBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import de.greenrobot.event.EventBus;

/**
 * 首页定存宝
 * Created by zzlz13 on 2017/4/13.
 */

public class HomeDepositAdapter extends BaseAdapter {


    private List<AppmemberIndexBean.LoanDataBean.LoansignPoolBean> loansignPool;
    private Context mContext;

    @Override
    public int getCount() {
        return loansignPool == null ? 0 : loansignPool.size();
    }


    @Override
    public Object getItem(int position) {
        return loansignPool == null ? null : loansignPool.get(position);
    }

    public void setData(Context mContext, List<AppmemberIndexBean.LoanDataBean.LoansignPoolBean> loansignPool) {
        this.loansignPool = loansignPool;
        this.mContext = mContext;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_home_product_deposit_new, null);
        TextView mTitle = ((TextView) itemView.findViewById(R.id.title));
//        TextView mPresell = ((TextView) itemView.findViewById(R.id.presell));
//        TextView mRateFirst = ((TextView) itemView.findViewById(R.id.rate_first));
//        TextView mRateSecond = ((TextView) itemView.findViewById(R.id.rate_second));
        TextView tv_float = ((TextView) itemView.findViewById(R.id.tv_float));
        TextView mMonth = ((TextView) itemView.findViewById(R.id.month));
        TextView mMoney = ((TextView) itemView.findViewById(R.id.money));
        NumberProgressBar mProgress = ((NumberProgressBar) itemView.findViewById(R.id.progress));
        MyCountDownTimer mCountDownTimer = ((MyCountDownTimer) itemView.findViewById(R.id.countDown));
        ImageView ivHomeState = (ImageView) itemView.findViewById(R.id.iv_home_state);

        AppmemberIndexBean.LoanDataBean.LoansignPoolBean bean = loansignPool.get(position);
        if (bean != null){
            mTitle.setText(bean.loanTitle);

//            String[] split = bean.rate.split("\\.");
//            if (split.length >= 1){
//                mRateFirst.setText(split[0]);
//            }
//            if (split.length >= 2){
//                String s = split[1];
//                if ("00".equals(s)){
//                    s = "0";
//                }
//                mRateSecond.setText("." + s + "%");
//            }else{
//                mRateSecond.setText(".0" + "%");
//            }
            tv_float.setText(bean.rate);

            final int pid = bean.id;//标id
            itemView.findViewById(R.id.product_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewRegularProductActivity.goNewRegularProductActivity(mContext,(long)pid,0,"",true);
                }
            });

            mMonth.setText(bean.term+"个月");

            float progress = Float.parseFloat(bean.process);
            mMoney.setText(FormatUtils.formatDown(bean.sumMoney));
            int loanstate = bean.loanstate;
            if (loanstate == 1){//预售
                mProgress.setVisibility(View.GONE);
                mCountDownTimer.setVisibility(View.VISIBLE);
//                mPresell.setVisibility(View.VISIBLE);
                ivHomeState.setVisibility(View.GONE);

                //倒计时
                long publishTime = bean.publishTime;
                mCountDownTimer.setCountDownTime(mContext,publishTime);
                mCountDownTimer.setOnFinishListener(new MyCountDownTimer.onFinish() {
                    @Override
                    public void finish() {
//                        Logger.e("开始post");
                        EventBus.getDefault().post(new RefreshCountDown(true));
                    }
                });
            }else if (loanstate == 2){//正在购买
//                mPresell.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                mCountDownTimer.setVisibility(View.GONE);
                mProgress.setProgress(progress);

                if (progress >= 100){//满标
//                    mRateFirst.setTextColor(mContext.getResources().getColor(R.color.text_999999));
//                    mRateSecond.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    ivHomeState.setVisibility(View.VISIBLE);
                    ivHomeState.setImageResource(R.mipmap.chanpin_manbiao);
                }
            }else if (loanstate == 3) {
                ivHomeState.setVisibility(View.VISIBLE);
                ivHomeState.setImageResource(R.mipmap.chanpin_huaikuanzhong);
                mCountDownTimer.setVisibility(View.GONE);
//                mPresell.setVisibility(View.GONE);
                mProgress.setProgress(100);
//                mProgress.setReachedBarColor(mContext.getResources().getColor(R.color.text_999999));
            } else if (loanstate == 4) {
                ivHomeState.setVisibility(View.VISIBLE);
                ivHomeState.setImageResource(R.mipmap.chanpin_yiwancheng);
                mCountDownTimer.setVisibility(View.GONE);
//                mPresell.setVisibility(View.GONE);
                mProgress.setProgress(100);
//                mProgress.setReachedBarColor(mContext.getResources().getColor(R.color.text_999999));
            }else{
//                mPresell.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                mCountDownTimer.setVisibility(View.GONE);
                mProgress.setProgress(progress);
                if (bean.endTime > System.currentTimeMillis()){//到时间募集结束
                    ivHomeState.setVisibility(View.VISIBLE);
                    ivHomeState.setImageResource(R.drawable.iv_home_state_stop);
                }
            }
        }
        return itemView;
    }
}
