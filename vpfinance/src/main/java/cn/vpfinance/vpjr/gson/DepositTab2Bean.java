package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by zzlz13 on 2017/4/17.
 */

public class DepositTab2Bean {


    /**
     * loantypes : ["消费分期","车贷分期"]
     * tips : 您投入的2000元己根据您认可的项目类型随机分配到以下债权中.您认可的项目类型:
     * loansigns : [{"month":"2个月","issueLoan":1000000,"behoof":"消费分期","tenderMoney":0,"id":1,"loanTitle":"测试","userName":"张三"},{"month":"2个月","issueLoan":10000,"behoof":"消费分期","tenderMoney":0,"id":2,"loanTitle":"测试测试","userName":"张三"},{"month":"2个月","issueLoan":10000,"behoof":"消费分期","tenderMoney":0,"id":3,"loanTitle":"测试测试","userName":"张三"},{"month":"6个月","issueLoan":20000,"behoof":null,"tenderMoney":0,"id":4,"loanTitle":"凑钱买豪车","userName":"徐华"},{"month":"3个月","issueLoan":100000,"behoof":null,"tenderMoney":0,"id":5,"loanTitle":"贷款买x5","userName":"华仔"},{"month":"3个月","issueLoan":10000,"behoof":null,"tenderMoney":0,"id":6,"loanTitle":"众筹买房","userName":"华仔"},{"month":"3个月","issueLoan":200010,"behoof":null,"tenderMoney":0,"id":7,"loanTitle":"众筹买房1","userName":"华仔"}]
     */

    public String tips;
    public List<String> loantypes;
    public List<LoansignsBean> loansigns;
    public String comeType;//1入口为标的详情 2.入口为投资记录的标的详情

    public static class LoansignsBean {
        /**
         * month : 2个月
         * issueLoan : 1000000.0
         * behoof : 消费分期
         * tenderMoney : 0
         * id : 1
         * loanTitle : 测试
         * userName : 张三
         */

        public String month;
        public double issueLoan;
        public String behoof;
        public double tenderMoney;
        public int id;
        public String loanTitle;
        public String userName;
        public String protocolUrl;
        public int status;
    }
}
