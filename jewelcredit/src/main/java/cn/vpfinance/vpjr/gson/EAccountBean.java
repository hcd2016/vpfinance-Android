package cn.vpfinance.vpjr.gson;

/**
 * Created by zzlz13 on 2017/8/14.
 */

public class EAccountBean {

    /**
     * accountSumMoney : 0.00
     * frozenAmtN : 0.00
     * acno : 8970660100000040000
     * status : 1
     * isBindHxBank : 1
     * name : 景
     * bankName : 广东华兴银行
     * cashBalance : 0.00
     */

    private String accountSumMoney;
    private String frozenAmtN;
    private String acno;
    private Integer status;
    private Integer isBindHxBank;
    private String name;
    private String bankName;
    private String cashBalance;
    private String vpSuperviseAccount;//华兴转账公共账户
    private int customerType; //1个人账户2对公账户

    public String getVpSuperviseAccount() {
        return vpSuperviseAccount;
    }

    public void setVpSuperviseAccount(String vpSuperviseAccount) {
        this.vpSuperviseAccount = vpSuperviseAccount;
    }

    public int getCustomerType() {
        return customerType;
    }

    public void setCustomerType(int customerType) {
        this.customerType = customerType;
    }

    public String getAccountSumMoney() {
        return accountSumMoney;
    }

    public void setAccountSumMoney(String accountSumMoney) {
        this.accountSumMoney = accountSumMoney;
    }

    public String getFrozenAmtN() {
        return frozenAmtN;
    }

    public void setFrozenAmtN(String frozenAmtN) {
        this.frozenAmtN = frozenAmtN;
    }

    public String getAcno() {
        return acno;
    }

    public void setAcno(String acno) {
        this.acno = acno;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsBindHxBank() {
        return isBindHxBank;
    }

    public void setIsBindHxBank(Integer isBindHxBank) {
        this.isBindHxBank = isBindHxBank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(String cashBalance) {
        this.cashBalance = cashBalance;
    }
}
