package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 */
public class ReturnMonthBean {


    /**
     * returnCount : 9
     * eventDays : ["20161103","20161105","20161107","20161109","20161111","20161128","20161129"]
     * returnMoney : 228.28
     */

    private String returnCount;
    private String       returnMoney;
    private List<String> eventDays;

    private String       unRepayCount;//当月待回款笔数
    private String       unRepayAmount;//当月待回款金额
    private String       repayCount;//当月已回款笔数
    private String       repayAmount;//当月已回款金额

    public String getUnRepayCount() {
        return unRepayCount;
    }

    public void setUnRepayCount(String unRepayCount) {
        this.unRepayCount = unRepayCount;
    }

    public String getUnRepayAmount() {
        return unRepayAmount;
    }

    public void setUnRepayAmount(String unRepayAmount) {
        this.unRepayAmount = unRepayAmount;
    }

    public String getRepayCount() {
        return repayCount;
    }

    public void setRepayCount(String repayCount) {
        this.repayCount = repayCount;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }

    public void setReturnCount(String returnCount) {
        this.returnCount = returnCount;
    }

    public void setReturnMoney(String returnMoney) {
        this.returnMoney = returnMoney;
    }

    public void setEventDays(List<String> eventDays) {
        this.eventDays = eventDays;
    }

    public String getReturnCount() {
        return returnCount;
    }

    public String getReturnMoney() {
        return returnMoney;
    }

    public List<String> getEventDays() {
        return eventDays;
    }
}
