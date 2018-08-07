package cn.vpfinance.vpjr.gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zzlz13 on 2017/9/15.
 */

public class AutoInvestSettingBean {
    /**
     * isHXAutoPlank : 1
     * loanPeriodBegin : 1
     * coupons : 2
     * rateEnd : 0.12
     * userMaxLoanMoney : 1000
     * isAutoPlank : 1
     * loanPeriodEnd : 24
     * rateBegin : 0.01
     * userId : 393633
     * loanType : 2
     * refundWay : 1,2
     * userRemainingMoney : 0
     * securityLevel : 1
     * options : {"coupons":[{"value":"息券","key":"1"},{"value":"代金券","key":"2"}],"loanType":[{"value":"薪金贷","key":"1"},{"value":"转盈宝","key":"2"},{"value":"抵押宝","key":"8"}],"refundWay":[{"value":"按月等额本息","key":"1"},{"value":"按月付息到期还本","key":"2"},{"value":"到期一次性还本息","key":"3"}],"securityLevel":[{"value":"低","key":"1"},{"value":"中","key":"2"},{"value":"高","key":"3"}]}
     */

    public int isHXAutoPlank;
    public int loanPeriodBegin;
    public String coupons;
    public int rateEnd;
    public double userMaxLoanMoney;
    public int isAutoPlank;
    public int loanPeriodEnd;
    public int rateBegin;
    public int userId;
    public String loanType;
    public String refundWay;
    public double userRemainingMoney;
    public String securityLevel;
    public OptionsBean options;

    public static class OptionsBean implements Serializable {
        public ArrayList<CouponsBean> coupons;
        public ArrayList<LoanTypeBean> loanType;
        public ArrayList<RefundWayBean> refundWay;
        public ArrayList<SecurityLevelBean> securityLevel;

        public static class CouponsBean extends BaseBean {
            /**
             * value : 息券
             * key : 1
             */

//            public String value;
//            public String key;
        }

        public static class LoanTypeBean extends BaseBean {
            /**
             * value : 薪金贷
             * key : 1
             */

//            public String value;
//            public String key;
        }

        public static class RefundWayBean extends BaseBean {
            /**
             * value : 按月等额本息
             * key : 1
             */
//            public String value;
//            public String key;
        }

        public static class SecurityLevelBean extends BaseBean {
            /**
             * value : 低
             * key : 1
             */
//            public String value;
//            public String key;
        }
    }

    public static class BaseBean implements Serializable{
        public String value;
        public String key;
    }

    /*public int isHXAutoPlank;
    public int loanPeriodBegin;
    public String coupons;
    public double rateEnd;
    public double userMaxLoanMoney;
    public int isAutoPlank;
    public int loanPeriodEnd;
    public double rateBegin;
    public long userId;
    public String loanType;
    public String refundWay;
    public double userRemainingMoney;
    public String securityLevel;

    public ArrayList<AutoInvestSettingItem> couponsOption = new ArrayList<>();
    public ArrayList<AutoInvestSettingItem> loanTypeOption = new ArrayList<>();
    public ArrayList<AutoInvestSettingItem> refundWayOption = new ArrayList<>();
    public ArrayList<AutoInvestSettingItem> securityLevelOption = new ArrayList<>();*/

}
