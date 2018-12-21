package cn.vpfinance.vpjr.model;

/**
 */
public class PresellProductInfo {

    //注意：首页显示字段和详情字段不一样
    public String borrowId; //标的ID
    public String borrowTitle;  //标的标题
    public String minRate;  //标的年化利率
    public String maxRate;
    public String imageUrl; //标的图片
    public String month;    //标的借款期限
    public String borrowStatus; //借款标状态 注意：（详情-->1未发布  2进行中 3回款中  4已完成) (首页 --> 1未发布、3进行中、5回款中、6已完成)
    public String borrowEndTime;    //标的借款项目截止时间 2015-09-24 17:53:24
    public String issueLoan;    //标的项目总金额
    public String total_tend_money; //已结买的标

    public String totalMoney; //平台出借总金额
    public String success; //是否成功

    public String borrowLoanPercent; // 标已经购买百分比
    public String allowTransfer; //是否可以转让

    public String loanUnit; //最小借出单位
    public String refundWay;//  1按月等额本息 2按月付息到期还本  3到期一次性还本息

    /*public class Loansign{
        public String id;   //标的ID
        public String issueLoan; //借款金额
        public String loanstate; //借款标状态  1未发布  2进行中 3回款中  4已完成
        public String loanType;//1普通标、2天标、3秒标、4流转标 6 债权转让
        public String loanUnit; //最小借出单位
        public String month;// 借款期限
        public String productType; //3位沈阳预售标
        public String subType; //种子类型 1质押，2保证，3抵押，4信用，5实地
        public String refundWay;//
    }

    public class Loansignbasic{
        public String borrower; //借款人
        public String loanTitle; //标题
    }*/



}
