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
