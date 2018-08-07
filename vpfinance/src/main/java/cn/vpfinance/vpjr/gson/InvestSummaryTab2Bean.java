package cn.vpfinance.vpjr.gson;

import java.util.ArrayList;
import java.util.Map;

/**
 */
public class InvestSummaryTab2Bean {


    /**
     * frozenMoney : 169159.50
     * data : [{"2016-06":"21223.00"},{"2016-05":"514969.00"},{"2016-04":"405.61"},{"2016-03":"2111.33"},{"2016-02":"257.92"},{"2016-01":"701.32"},{"2015-12":"1278.15"},{"2015-11":"2945.99"},{"2015-10":"383.50"},{"2015-09":"709.50"},{"2015-08":"1700.50"},{"2015-07":"500.00"}]
     * cashMoney : 5821148.38
     * returnMoney : 215958.86
     * totalMoney : 6206266.75
     */

    private double frozenMoney;
    private double cashMoney;
    private double returnMoney;
    private double totalMoney;
    private ArrayList<Map<String, Double>> data;

    public ArrayList<Map<String, Double>> getData() {
        return data;
    }

    public void setData(ArrayList<Map<String, Double>> data) {
        this.data = data;
    }

    public double getFrozenMoney() {
        return frozenMoney;
    }

    public void setFrozenMoney(double frozenMoney) {
        this.frozenMoney = frozenMoney;
    }

    public double getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(double cashMoney) {
        this.cashMoney = cashMoney;
    }

    public double getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(double returnMoney) {
        this.returnMoney = returnMoney;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Override
    public String toString() {
        return "InvestSummaryTab2Bean{" +
                "frozenMoney=" + frozenMoney +
                ", cashMoney=" + cashMoney +
                ", returnMoney=" + returnMoney +
                ", totalMoney=" + totalMoney +
                ", data=" + data +
                '}';
    }
}
