package cn.vpfinance.vpjr.model;

/**
 * Created by Administrator on 2015/11/6.
 * 资金总览info
 */
public class FundOverInfo {
    /*可用余额*/
    private String cashBalance;
    /*冻结金额*/
    private String frozenAmtN;
    /*在投金额*/
    private String inCount ;
    /*总资产*/
    private String netAsset;
    /*累计收益*/
    public String realMoney;

    public String getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(String cashBalance) {
        this.cashBalance = cashBalance;
    }

    public String getFrozenAmtN() {
        return frozenAmtN;
    }

    public void setFrozenAmtN(String frozenAmtN) {
        this.frozenAmtN = frozenAmtN;
    }

    public String getInCount() {
        return inCount;
    }

    public void setInCount(String inCount) {
        this.inCount = inCount;
    }

    public String getNetAsset() {
        return netAsset;
    }

    public void setNetAsset(String netAsset) {
        this.netAsset = netAsset;
    }
}
