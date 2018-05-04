package cn.vpfinance.vpjr.gson;

import java.util.ArrayList;
import java.util.Map;

/**
 */
public class InvestSummaryTab3Bean {


    /**
     * inviteMoney : 271.93
     * otherMoney : 0.00
     * data : [{"2016-06":"21223.00"},{"2016-05":"514969.00"},{"2016-04":"405.61"},{"2016-03":"2111.33"},{"2016-02":"257.92"},{"2016-01":"701.32"},{"2015-12":"1278.15"},{"2015-11":"2945.99"},{"2015-10":"383.50"},{"2015-09":"709.50"},{"2015-08":"1700.50"},{"2015-07":"500.00"}]
     * allTotalMoney : 459.06
     * voucherSumMoney : 43.05
     * capitalMoney : 416.01
     */

    private double inviteMoney;
    private double otherMoney;
    private double allTotalMoney;
    private double voucherSumMoney;
    private double capitalMoney;
    private ArrayList<Map<String, Double>> data;

    public ArrayList<Map<String, Double>> getData() {
        return data;
    }

    public void setData(ArrayList<Map<String, Double>> data) {
        this.data = data;
    }

    public double getInviteMoney() {
        return inviteMoney;
    }

    public void setInviteMoney(double inviteMoney) {
        this.inviteMoney = inviteMoney;
    }

    public double getOtherMoney() {
        return otherMoney;
    }

    public void setOtherMoney(double otherMoney) {
        this.otherMoney = otherMoney;
    }

    public double getAllTotalMoney() {
        return allTotalMoney;
    }

    public void setAllTotalMoney(double allTotalMoney) {
        this.allTotalMoney = allTotalMoney;
    }

    public double getVoucherSumMoney() {
        return voucherSumMoney;
    }

    public void setVoucherSumMoney(double voucherSumMoney) {
        this.voucherSumMoney = voucherSumMoney;
    }

    public double getCapitalMoney() {
        return capitalMoney;
    }

    public void setCapitalMoney(double capitalMoney) {
        this.capitalMoney = capitalMoney;
    }

    @Override
    public String toString() {
        return "InvestSummaryTab3Bean{" +
                "inviteMoney='" + inviteMoney + '\'' +
                ", otherMoney='" + otherMoney + '\'' +
                ", allTotalMoney='" + allTotalMoney + '\'' +
                ", voucherSumMoney='" + voucherSumMoney + '\'' +
                ", capitalMoney='" + capitalMoney + '\'' +
                ", data=" + data +
                '}';
    }
}
