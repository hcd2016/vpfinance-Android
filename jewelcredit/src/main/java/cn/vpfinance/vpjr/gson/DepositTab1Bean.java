package cn.vpfinance.vpjr.gson;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zzlz13 on 2017/4/15.
 */

public class DepositTab1Bean{


    /**
     * buyCount : 14
     * sumMoney : 680010.0
     * loanType : [{"type":"1","value":"1","key":"消费分期"},{"type":"1","value":"2","key":"车贷分期"}]
     * data : [{"type":"1","value":"2017-04-12 16:03:47.0-2017-04-19 23:59:59.0","key":"开放时间段"},{"type":"1","value":"满标后次日开始计息","key":"起息标准"},{"type":"1","value":"按月等额本息","key":"还款方式"},{"type":"1","value":"3个月","key":"锁定期"},{"type":"1","value":"低","key":"风险等级"},{"type":"1","value":"2","key":"车贷分期"}]
     * bookCouponNumber : 0
     * issueloan : 680010.00
     * loanTitle : 定存宝A170406
     * repays : [{"repaytime":"2017-05-15","periods":"1/3","repayStatus":"待还款"},{"repaytime":"2017-06-15","periods":"2/3","repayStatus":"待还款"},{"repaytime":"2017-07-15","periods":"3/3","repayStatus":"待还款"}]
     * bookPercent : 0.5
     * totalPeriod : 3
     * canBuyMoney : 0
     * rate : 7.5
     * scope : 投标范围++您投资的每一笔金额将由系统根据您选择的产品类型自动分配到以下债权列表的其中一个或以上的标的中
     * poolId : 1
     * term : 3个月
     * loanState : 2
     */

    public Integer frequency;//投资记录总条数
    public int buyCount;
    public double sumMoney;
    public int bookCouponNumber;//预售券张数
    public double issueloan;
    public String loanTitle;
    public double bookPercent;//预售百分比
    public int totalPeriod;
    public double rate;
    public String scope;
    public int poolId;
    public int loanId;
    public String term;
    public int loanState;
    public List<LoanTypeBean> loanType;
    public List<DataBean> data;
    public List<RepaysBean> repays;
    public int timeType;//1月标,2天标
    public long publishTime;//预售时间
    public long endTime;//
    public double process; //进度条
    public double canBuyMoney;//可购金额
    public double tenderMoney; //购买了多少
    public double surplusMoney; //剩余金额
    public String byStagesType;//标的类型选择



    public static class LoanTypeBean {
        /**
         * type : 1
         * value : 1
         * key : 消费分期
         */
        public String type;
        public Long value;
        public String key;
        public double money;
    }

    public static class DataBean {
        /**
         * type : 1
         * value : 2017-04-12 16:03:47.0-2017-04-19 23:59:59.0
         * key : 开放时间段
         */

        public String type;
        public String value;
        public String key;
    }

    public static class RepaysBean implements Parcelable{
        /**
         * repaytime : 2017-05-15
         * periods : 1/3
         * repayStatus : 待还款
         */

        public String repaytime;
        public String periods;
        public String repayStatus;

        protected RepaysBean(Parcel in) {
            repaytime = in.readString();
            periods = in.readString();
            repayStatus = in.readString();
        }

        public static final Creator<RepaysBean> CREATOR = new Creator<RepaysBean>() {
            @Override
            public RepaysBean createFromParcel(Parcel in) {
                return new RepaysBean(in);
            }

            @Override
            public RepaysBean[] newArray(int size) {
                return new RepaysBean[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(repaytime);
            dest.writeString(periods);
            dest.writeString(repayStatus);
        }
    }
}
