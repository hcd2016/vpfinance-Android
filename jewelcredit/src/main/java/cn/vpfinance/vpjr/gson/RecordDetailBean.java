package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 */
public class RecordDetailBean {


    /**
     * protocolList : [{"title":"投资服务协议","url":"/loaninfo/loan_protocol_2?rid=47392"},{"title":"债权转让及回购协议","url":"/loaninfo/loan_protocol_carloan?rid=47392&loanId=971"}]
     * voucherMoney : 0.00
     * loanType : 定期
     * finishTime : 2016-12-30
     * loanState : 3
     * tenderTime : 2016-11-29 12:30:42
     * expectProfit : 536.68
     * refundWay : 1个月
     * tenderProfit : 年化利率7.8%
     * loanTitle : 车贷宝201611171
     * loanId : 971
     * tenderMoney : 82566.99
     */

    public String voucherMoney;
    public String loanType;
    public String finishTime;
    public int loanState;
    public String tenderTime;
    public String expectProfit;
    public String refundWay;
    public String month;
    public String tenderProfit;
    public String loanTitle;
    public int loanId;
    public double tenderMoney;
    public int type; //1未使用优惠券,不显示,2代金券,3加息券
    public int finshStatus;//1不显示,2已转让,3已消费,4已完成
    public String info; //提示文案
    public int product;
    public int productType;//3为沈阳标
    public int loanTypeNum;//1定期 2债权转让 3权益投资
    public int recordId;

    /**
     * title : 投资服务协议
     * url : /loaninfo/loan_protocol_2?rid=47392
     */

    public List<ProtocolListBean> protocolList;

    public static class ProtocolListBean {
        public String title;
        public String url;
    }
}
