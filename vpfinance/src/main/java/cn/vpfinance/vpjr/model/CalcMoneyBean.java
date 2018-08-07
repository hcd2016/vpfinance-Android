package cn.vpfinance.vpjr.model;

/**
 * Created by Administrator on 2016/8/10.
 */
public class CalcMoneyBean {
    public String type;//1，从newSelectVoucherFragment过去。2.从NewSelectAddRateFragment过去
    public double calcedMoney;
    public double addRateMoey;

    public CalcMoneyBean() {
    }

    public CalcMoneyBean(String type, double calcedMoney, double addRateMoey) {
        this.type = type;
        this.calcedMoney = calcedMoney;
        this.addRateMoey = addRateMoey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getCalcedMoney() {
        return calcedMoney;
    }

    public void setCalcedMoney(double calcedMoney) {
        this.calcedMoney = calcedMoney;
    }

    public double getAddRateMoey() {
        return addRateMoey;
    }

    public void setAddRateMoey(double addRateMoey) {
        this.addRateMoey = addRateMoey;
    }
}
