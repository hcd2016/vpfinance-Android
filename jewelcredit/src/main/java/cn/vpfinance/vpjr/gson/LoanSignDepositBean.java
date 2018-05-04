package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by zzlz13 on 2017/4/14.
 */

public class LoanSignDepositBean {


    public List<LoansignpoolBean> loansignpool;

    public static class LoansignpoolBean {
        /**
         * process : 28.26
         * id : 1
         * remaining : -352717000
         * rate : 7.50
         * publishTime : 1492038227000
         * term : 1
         * refundWay : 3
         * endTime : 1491893999000
         * loanstate : 2
         * loanTitle : 定存宝A170406
         * timeType : 1
         * sumMoney : 350000.0
         */

        public String process;
        public int id;
        public double remaining;
        public String rate;
        public long publishTime;
        public int term;
        public int refundWay;
        public long endTime;
        public int loanstate;
        public String loanTitle;
        public int timeType;
        public double sumMoney;
    }
}
