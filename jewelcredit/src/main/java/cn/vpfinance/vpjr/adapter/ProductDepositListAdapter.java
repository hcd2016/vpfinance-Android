package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.LoanSignDepositBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import cn.vpfinance.vpjr.view.SmallCircularProgressView;
import de.greenrobot.event.EventBus;

/**
 * Created by zzlz13 on 2017/9/11.
 */

public class ProductDepositListAdapter extends RecyclerView.Adapter<ProductDepositListAdapter.DepositViewHolder> {

    private Context mContext;
    private List<LoanSignDepositBean.LoansignpoolBean> loansignpool;
    private OnItemClickListener listener;

    public ProductDepositListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<LoanSignDepositBean.LoansignpoolBean> loansignpool) {
        this.loansignpool = loansignpool;
        notifyDataSetChanged();
    }

    @Override
    public DepositViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DepositViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_loan_sign_deposit, parent, false));
    }

    @Override
    public void onBindViewHolder(DepositViewHolder holder, int position) {
        final LoanSignDepositBean.LoansignpoolBean loansignpoolBean = loansignpool.get(position);
        if (loansignpoolBean != null) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(loansignpoolBean);
                    }
                }
            });
            holder.item_loan_title.setText(loansignpoolBean.loanTitle);
            double sumMoney = loansignpoolBean.sumMoney;
            if (sumMoney > 10000) {
                double v = sumMoney / 10000;
                holder.item_loan_totle.setText(FormatUtils.formatDown2(v) + "万");
            } else {
                holder.item_loan_totle.setText(FormatUtils.formatDown(sumMoney));
            }
            holder.item_loan_rate.setText(loansignpoolBean.rate);
            if (loansignpoolBean.timeType == 1) {
                holder.item_loan_term.setText(loansignpoolBean.term + "个月");
            } else if (loansignpoolBean.timeType == 2) {
                holder.item_loan_term.setText(loansignpoolBean.term + "天");
            }

            int loanstate = loansignpoolBean.loanstate;
            if (loanstate == 1) {//预售
                holder.ll_presell.setVisibility(View.VISIBLE);
                holder.ll_deposit_countdown.setVisibility(View.GONE);
                holder.rl_progress.setVisibility(View.GONE);
                //倒计时
                long publishTime = loansignpoolBean.publishTime;
                holder.presell.setCountDownTime(mContext, publishTime);
                holder.presell.setOnFinishListener(new MyCountDownTimer.onFinish() {
                    @Override
                    public void finish() {
                        EventBus.getDefault().post(new RefreshCountDown(true));
                    }
                });
            } else if (loanstate == 2) {
                holder.ll_presell.setVisibility(View.GONE);
                holder.ll_deposit_countdown.setVisibility(View.VISIBLE);
                holder.rl_progress.setVisibility(View.VISIBLE);
                float p = Float.parseFloat(loansignpoolBean.process);

                if (p >= 100) {//满标
                    holder.ll_deposit_countdown.setVisibility(View.GONE);
                    holder.status.setText("满标\n审核");
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    holder.circle_background.setVisibility(View.VISIBLE);
                    holder.progress.setProgress(100);
                    holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
                    holder.status2.setVisibility(View.GONE);
                } else if (loansignpoolBean.endTime <= System.currentTimeMillis()) {//时间到了
                    holder.status.setText("暂停\n开放");
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    holder.circle_background.setVisibility(View.VISIBLE);
                    holder.ll_deposit_countdown.setVisibility(View.GONE);
                    holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
                    holder.status2.setVisibility(View.GONE);
                } else {
                    holder.status.setText("立即\n加入");
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.red_text));
                    holder.circle_background.setVisibility(View.GONE);
                    holder.ll_deposit_countdown.setVisibility(View.VISIBLE);
                    holder.deposit_countdown.setCountDownTime(mContext, loansignpoolBean.endTime);
                    holder.deposit_countdown.setOnFinishListener(new MyCountDownTimer.onFinish() {
                        @Override
                        public void finish() {
                            EventBus.getDefault().post(new RefreshCountDown(true));
                        }
                    });
                    holder.progress.setProgressColor(mContext.getResources().getColor(R.color.red_text));
                    holder.progress.setProgress(p);
                    holder.status2.setVisibility(View.VISIBLE);
                    holder.status2.setText(p + "%");
                }
            } else if (loanstate == 3) {//还款中
                holder.ll_presell.setVisibility(View.GONE);
                holder.ll_deposit_countdown.setVisibility(View.GONE);
                holder.rl_progress.setVisibility(View.VISIBLE);
                holder.status.setText("还款中");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                holder.status2.setVisibility(View.GONE);
                holder.circle_background.setVisibility(View.VISIBLE);
                holder.progress.setProgress(100);
                holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
            } else if (loanstate == 4) {//已完成
                holder.ll_presell.setVisibility(View.GONE);
                holder.ll_deposit_countdown.setVisibility(View.GONE);
                holder.rl_progress.setVisibility(View.VISIBLE);
                holder.status.setText("已完成");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                holder.status2.setVisibility(View.GONE);
                holder.circle_background.setVisibility(View.VISIBLE);
                holder.progress.setProgress(100);
                holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
            } else if (loanstate == 5) {
                holder.status.setText("暂停\n开放");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                holder.circle_background.setVisibility(View.VISIBLE);
                holder.ll_deposit_countdown.setVisibility(View.GONE);
                holder.progress.setProgressColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.status2.setVisibility(View.GONE);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(LoanSignDepositBean.LoansignpoolBean bean);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return loansignpool == null ? 0 : loansignpool.size();
    }

    class DepositViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rootView)
        RelativeLayout rootView;
        @Bind(R.id.item_loan_title)
        TextView item_loan_title;
        @Bind(R.id.item_loan_totle)
        TextView item_loan_totle;
        @Bind(R.id.item_loan_rate)
        TextView item_loan_rate;
        @Bind(R.id.item_loan_term)
        TextView item_loan_term;
        @Bind(R.id.deposit_countdown)
        MyCountDownTimer deposit_countdown;
        @Bind(R.id.ll_deposit_countdown)
        LinearLayout ll_deposit_countdown;
        @Bind(R.id.ll_presell)
        LinearLayout ll_presell;
        @Bind(R.id.rl_progress)
        RelativeLayout rl_progress;
        @Bind(R.id.presell)
        MyCountDownTimer presell;
        @Bind(R.id.progress)
        SmallCircularProgressView progress;
        @Bind(R.id.status)
        TextView status;
        @Bind(R.id.status2)
        TextView status2;
        @Bind(R.id.circle_background)
        View circle_background;

        public DepositViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
