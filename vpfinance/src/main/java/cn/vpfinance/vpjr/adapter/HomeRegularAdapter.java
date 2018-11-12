package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.AppmemberIndexBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;
import de.greenrobot.event.EventBus;

/**
 * 首页普通标
 * Created by zzlz13 on 2017/4/13.
 */

public class HomeRegularAdapter extends BaseAdapter {


    private List<AppmemberIndexBean.LoanDataBean.LoansignsBean> loansigns;
    private Context mContext;

    public void setData(Context context, List<AppmemberIndexBean.LoanDataBean.LoansignsBean> loansigns) {
        this.loansigns = loansigns;
        mContext = context;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return loansigns == null ? 0 : loansigns.size();
    }


    @Override
    public Object getItem(int position) {
        return loansigns == null ? null : loansigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_home_product_regular_new, null);
        TextView mTitle = ((TextView) itemView.findViewById(R.id.title));
        ImageView mAddRateState = ((ImageView) itemView.findViewById(R.id.add_rate_state));
        ImageView mState = ((ImageView) itemView.findViewById(R.id.state));
        ImageView mZhuan = ((ImageView) itemView.findViewById(R.id.zhuan));
//        ImageView mLv = ((ImageView) itemView.findViewById(R.id.lv));
//        ImageView mJing = ((ImageView) itemView.findViewById(R.id.jing));
//        TextView mPresell = ((TextView) itemView.findViewById(R.id.presell));
//        TextView mRateFirst = ((TextView) itemView.findViewById(R.id.rate_first));
//        TextView mRateSecond = ((TextView) itemView.findViewById(R.id.rate_second));
        TextView tv_float = ((TextView) itemView.findViewById(R.id.tv_float));
        TextView mMonth = ((TextView) itemView.findViewById(R.id.month));
        TextView mMoney = ((TextView) itemView.findViewById(R.id.money));
        NumberProgressBar mProgress = ((NumberProgressBar) itemView.findViewById(R.id.progress));
        MyCountDownTimer mCountDownTimer = ((MyCountDownTimer) itemView.findViewById(R.id.countDown));
        TextView mAddRate = ((TextView) itemView.findViewById(R.id.add_rate));
//        ImageView loanSignTypeIcon = (ImageView) itemView.findViewById(R.id.loan_sign_type);
        ImageView ivHomeState = (ImageView) itemView.findViewById(R.id.iv_home_state);
//        ImageView iphone = (ImageView) itemView.findViewById(R.id.iphone7);
        ImageView iv_fdjx = (ImageView) itemView.findViewById(R.id.iv_fdjx);//是否是浮动计息
//        TextView bankAccountStatus = (TextView) itemView.findViewById(R.id.bankAccountStatus);


        AppmemberIndexBean.LoanDataBean.LoansignsBean bean = loansigns.get(position);

        if (bean != null) {

//            iphone.setVisibility("1".equals(bean.givePhone) ? View.VISIBLE : View.GONE);

            final AppmemberIndexBean.LoanDataBean.LoansignsBean.LoansignBean loansign = bean.loansign;
            AppmemberIndexBean.LoanDataBean.LoansignsBean.LoansignbasicBean loansignbasic = bean.loansignbasic;
            int mLoanstate;
            if (loansign != null) {

                int product = loansign.product;
//                if (product == 4){
//                    bankAccountStatus.setVisibility(View.VISIBLE);
//                }else{
//                    bankAccountStatus.setVisibility(View.GONE);
//                }

                //1、车贷宝，2、消费贷，8、供应链，10、企业贷，11、珠宝贷，12、融租宝,13个人贷
                int loansignTypeId = loansign.loansignTypeId;
                Drawable drawable = null;
                switch (loansignTypeId) {
                    case 1:
                        drawable = mContext.getResources().getDrawable(R.drawable.icon_che);
                        break;
                    case 2:
                        drawable = mContext.getResources().getDrawable(R.drawable.icon_xiao);
                        break;
                    case 8:
                        drawable = mContext.getResources().getDrawable(R.drawable.icon_gong);
                        break;
                    case 10:
                        drawable = mContext.getResources().getDrawable(R.drawable.icon_qi);
                        break;
                    case 11:
                        drawable = mContext.getResources().getDrawable(R.drawable.icon_zhu);
                        break;
                    case 12:
                        drawable = mContext.getResources().getDrawable(R.drawable.icon_rong);
                        break;
                    case 13:
                        drawable = mContext.getResources().getDrawable(R.drawable.icon_ge);
                        break;
                }
//                if (drawable == null) {
//                    loanSignTypeIcon.setVisibility(View.GONE);
//                } else {
//                    loanSignTypeIcon.setImageDrawable(drawable);
//                }
                //1预售
                mLoanstate = loansign.loanstate;

                if (loansignbasic != null) {
                    mTitle.setText(TextUtils.isEmpty(loansignbasic.loanTitle) ? "" : loansignbasic.loanTitle);
                    //加息
                    String rewardStr = loansignbasic.reward;
                    float reward = 0;
                    try {
                        reward = Float.parseFloat(rewardStr);
                        reward *= 100;
                        if (reward == 0) {
                            mAddRate.setVisibility(View.INVISIBLE);
                            mAddRateState.setVisibility(View.INVISIBLE);
                        } else {
                            mAddRate.setVisibility(View.VISIBLE);
                            mAddRate.setText("+" + reward + "%");
                            mAddRateState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //约定年利率
                    double rate = loansign.rate * 100 - reward;
                    String rateStr = String.format("%.1f", rate);
//                    String[] split = rateStr.split("\\.");
//                    if (split.length >= 1)
//                        mRateFirst.setText(split[0]);
//                    if (split.length >= 2)
//                        mRateSecond.setText("." + split[1] + "%");
                    //项目期限
                    if (loansign.loanType == 2) {
                        mMonth.setText(loansign.month + "天");
                    } else {
                        mMonth.setText(loansign.month + "个月");
                    }
                    tv_float.setText(rateStr);
                    //金额
                    String issueLoanStr = loansign.issueLoan;
                    if(!TextUtils.isEmpty(issueLoanStr) ){
                        double v = Double.parseDouble(loansign.issueLoan);
                        if(v >= 10000) {
                            mMoney.setText(FormatUtils.formatDown2(v / 10000) + "万");
                        }else {
                            mMoney.setText(FormatUtils.formatDown2(v ) + "元");
                        }
                    }

                    //点击事件
                    final int pid = loansign.id;
                    itemView.findViewById(R.id.product_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int accountType = loansign.product == 4 ? 1 : 0;
                            NewRegularProductActivity.goNewRegularProductActivity(mContext, pid, 0, "产品详情",false);
                        }
                    });

                    Common.productSubType(mContext, mState, loansign.subType);

                    //转
                    String allowTransfer = bean.allowTransfer;
                    Common.productZhuan(allowTransfer, mZhuan);

                    //净
                    int productType = bean.productType;
//                    Common.productJing(productType, mJing);

                    if (mLoanstate == 1) {//预售
                        mProgress.setVisibility(View.GONE);
                        mCountDownTimer.setVisibility(View.VISIBLE);
//                        mPresell.setVisibility(View.VISIBLE);

                        //倒计时
                        long publishTime = bean.publishTime;
                        mCountDownTimer.setCountDownTime(mContext,publishTime);
                        mCountDownTimer.setOnFinishListener(new MyCountDownTimer.onFinish() {
                            @Override
                            public void finish() {
                                EventBus.getDefault().post(new RefreshCountDown(true));
                            }
                        });
                    } else {//普通标
//                        mPresell.setVisibility(View.GONE);

                        mProgress.setVisibility(View.VISIBLE);
                        mCountDownTimer.setVisibility(View.GONE);
                        //进度条
                        float process = bean.process;
                        mProgress.setProgress(process);
                        if (process >= 100 && loansign.loanstate == 2){//满标
//                            mProgress.setReachedBarColor(mContext.getResources().getColor(R.color.text_999999));
//                            mRateFirst.setTextColor(mContext.getResources().getColor(R.color.text_999999));
//                            mRateSecond.setTextColor(mContext.getResources().getColor(R.color.text_999999));
//                            mAddRate.setTextColor(mContext.getResources().getColor(R.color.text_999999));
//                            mAddRateState.setBackgroundResource(R.drawable.icon_jiaxi_dis);
//
                            ivHomeState.setVisibility(View.VISIBLE);
                            ivHomeState.setImageResource(R.mipmap.chanpin_manbiao);
                        }
                        /*double total_tend_money = bean.total_tend_money;
                        if (!TextUtils.isEmpty(issueLoanStr) && total_tend_money != 0) {
                            try {
                                double issueLoan = Double.parseDouble(issueLoanStr);
                                if (issueLoan != 0) {
                                    float progress = (float) (total_tend_money * 100 / issueLoan);

                                    String s = FormatUtils.formatDownByProgress(progress);
                                    try{
                                        float v = Float.parseFloat(s);
                                        mProgress.setProgress(v);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    if (progress >= 100 && loansign.loanstate == 2) {
                                        //满标
//                                                mProgress.setReachedBarColor(mContext.getResources().getColor(R.color.text_999999));
//                                        mRateFirst.setTextColor(mContext.getResources().getColor(R.color.text_999999));
//                                        mRateSecond.setTextColor(mContext.getResources().getColor(R.color.text_999999));
//                                        mAddRate.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                                        mAddRateState.setBackgroundResource(R.drawable.icon_jiaxi_dis);

                                        ivHomeState.setVisibility(View.VISIBLE);
                                        ivHomeState.setImageResource(R.drawable.iv_home_state_fill);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                    if(loansign.graceDays > 0) {//是否是浮动计息
                        iv_fdjx.setVisibility(View.VISIBLE);
                    }else {
                        iv_fdjx.setVisibility(View.GONE);
                    }
                }
                int loanstate = loansign.loanstate;
                if (loanstate == 3) {
                    ivHomeState.setVisibility(View.VISIBLE);
                    ivHomeState.setImageResource(R.mipmap.chanpin_huaikuanzhong);
                    mProgress.setProgress(100);
                } else if (loanstate == 4) {
                    ivHomeState.setVisibility(View.VISIBLE);
                    ivHomeState.setImageResource(R.mipmap.chanpin_yiwancheng);
                    mProgress.setProgress(100);
                }
            }

        }
        return itemView;
    }
}
