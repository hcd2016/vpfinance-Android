package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.LoanSignDepositBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import cn.vpfinance.vpjr.view.SmallCircularProgressView;
import de.greenrobot.event.EventBus;

/**
 * 产品列表适配器-->定存宝
 * Created by zzlz13 on 2017/4/14.
 */

public class LoanSignDepositListAdapter extends BaseAdapter {
    private Context mContext;
    private List<LoanSignDepositBean.LoansignpoolBean> loansignpool;

    public LoanSignDepositListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return loansignpool == null ? 0 : loansignpool.size();
    }

    @Override
    public LoanSignDepositBean.LoansignpoolBean getItem(int position) {
        return loansignpool == null ? null : loansignpool.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_loan_sign_deposit,null);
            //findv
            holder.item_loan_title = (TextView) convertView.findViewById(R.id.item_loan_title);
            holder.item_loan_totle = (TextView) convertView.findViewById(R.id.item_loan_totle);
            holder.item_loan_rate = (TextView) convertView.findViewById(R.id.item_loan_rate);
            holder.item_loan_term = (TextView) convertView.findViewById(R.id.item_loan_term);
            holder.deposit_countdown = (MyCountDownTimer) convertView.findViewById(R.id.deposit_countdown);
            holder.ll_deposit_countdown = (LinearLayout) convertView.findViewById(R.id.ll_deposit_countdown);
            holder.ll_presell = (LinearLayout) convertView.findViewById(R.id.ll_presell);
            holder.rl_progress = (RelativeLayout) convertView.findViewById(R.id.rl_progress);
            holder.presell = (MyCountDownTimer) convertView.findViewById(R.id.presell);
            holder.progress = (SmallCircularProgressView) convertView.findViewById(R.id.progress);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.status2 = (TextView) convertView.findViewById(R.id.status2);
            holder.circle_background = convertView.findViewById(R.id.circle_background);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        //初始化数据
        LoanSignDepositBean.LoansignpoolBean loansignpoolBean = loansignpool.get(position);
        if (loansignpoolBean != null){

            holder.item_loan_title.setText(loansignpoolBean.loanTitle);
            double sumMoney = loansignpoolBean.sumMoney;
            if (sumMoney > 10000){
                double v = sumMoney / 10000;
                holder.item_loan_totle.setText(FormatUtils.formatDown2(v)+"万");
            }else{
                holder.item_loan_totle.setText(FormatUtils.formatDown(sumMoney));
            }
            holder.item_loan_rate.setText(loansignpoolBean.rate);
            if (loansignpoolBean.timeType == 1){
                holder.item_loan_term.setText(loansignpoolBean.term+"个月");
            }else if (loansignpoolBean.timeType == 2){
                holder.item_loan_term.setText(loansignpoolBean.term+"天");
            }

            int loanstate = loansignpoolBean.loanstate;
            if (loanstate == 1){//预售
                holder.ll_presell.setVisibility(View.VISIBLE);
                holder.ll_deposit_countdown.setVisibility(View.GONE);
                holder.rl_progress.setVisibility(View.GONE);
                //倒计时
                long publishTime = loansignpoolBean.publishTime;
                holder.presell.setCountDownTime(mContext,publishTime);
                holder.presell.setOnFinishListener(new MyCountDownTimer.onFinish() {
                    @Override
                    public void finish() {
                        EventBus.getDefault().post(new RefreshCountDown(true));
                    }
                });
            }else if (loanstate == 2){
                holder.ll_presell.setVisibility(View.GONE);
                holder.ll_deposit_countdown.setVisibility(View.VISIBLE);
                holder.rl_progress.setVisibility(View.VISIBLE);
                float p = Float.parseFloat(loansignpoolBean.process);

                if (p >= 100){//满标
                    holder.ll_deposit_countdown.setVisibility(View.GONE);
                    holder.status.setText("满标\n审核");
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    holder.circle_background.setVisibility(View.VISIBLE);
                    holder.progress.setProgress(100);
                    holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
                    holder.status2.setVisibility(View.GONE);
                }else if (loansignpoolBean.endTime <= System.currentTimeMillis()){//时间到了
                    holder.status.setText("暂停\n开放");
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    holder.circle_background.setVisibility(View.VISIBLE);
                    holder.ll_deposit_countdown.setVisibility(View.GONE);
                    holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
                    holder.status2.setVisibility(View.GONE);
                }else{
                    holder.status.setText("立即\n加入");
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.red_text));
                    holder.circle_background.setVisibility(View.GONE);
                    holder.ll_deposit_countdown.setVisibility(View.VISIBLE);
                    holder.deposit_countdown.setCountDownTime(mContext,loansignpoolBean.endTime);
                    holder.deposit_countdown.setOnFinishListener(new MyCountDownTimer.onFinish() {
                        @Override
                        public void finish() {
                            EventBus.getDefault().post(new RefreshCountDown(true));
                        }
                    });
                    holder.progress.setProgressColor(mContext.getResources().getColor(R.color.red_text));
                    holder.progress.setProgress(p);
                    holder.status2.setVisibility(View.VISIBLE);
                    holder.status2.setText(p+"%");
                }
            }else if (loanstate == 3){//还款中
                holder.ll_presell.setVisibility(View.GONE);
                holder.ll_deposit_countdown.setVisibility(View.GONE);
                holder.rl_progress.setVisibility(View.VISIBLE);
                holder.status.setText("还款中");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                holder.status2.setVisibility(View.GONE);
                holder.circle_background.setVisibility(View.VISIBLE);
                holder.progress.setProgress(100);
                holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
            }else if (loanstate == 4){//已完成
                holder.ll_presell.setVisibility(View.GONE);
                holder.ll_deposit_countdown.setVisibility(View.GONE);
                holder.rl_progress.setVisibility(View.VISIBLE);
                holder.status.setText("已完成");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                holder.status2.setVisibility(View.GONE);
                holder.circle_background.setVisibility(View.VISIBLE);
                holder.progress.setProgress(100);
                holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
            }else if (loanstate == 5){
                holder.status.setText("暂停\n开放");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                holder.circle_background.setVisibility(View.VISIBLE);
                holder.ll_deposit_countdown.setVisibility(View.GONE);
                holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.status2.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public void setData(List<LoanSignDepositBean.LoansignpoolBean> loansignpool) {
        this.loansignpool = loansignpool;
        notifyDataSetChanged();
    }

    private class ViewHolder{
        private TextView item_loan_title;
        private TextView item_loan_totle;
        private TextView item_loan_rate;
        private TextView item_loan_term;
        private MyCountDownTimer deposit_countdown;
        private LinearLayout ll_deposit_countdown;
        private LinearLayout ll_presell;
        private RelativeLayout rl_progress;
        private MyCountDownTimer presell;
        private SmallCircularProgressView progress;
        private TextView status;
        private TextView status2;
        private View circle_background;
    }
}
