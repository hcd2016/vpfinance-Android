package cn.vpfinance.vpjr.model;

import java.util.ArrayList;

public class VoucherArray {
    private double amount = 0;//代金券总金额
    private double alAmount = 0;//己用代金券金额
    private double expAmount = 0;//过期代金券金额
    private double aviAMount = 0;//可用代金券金额
    public int useable = 0;//可用
    private double voucherrate;
    private int type;
    private ArrayList<Voucher> voucherList;

    public double getVoucherrate() {
        return voucherrate;
    }

    public void setVoucherrate(double voucherrate) {
        this.voucherrate = voucherrate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAlAmount() {
        return alAmount;
    }

    public void setAlAmount(double alAmount) {
        this.alAmount = alAmount;
    }


    public double getExpAmount() {
        return expAmount;
    }

    public void setExpAmount(double expAmount) {
        this.expAmount = expAmount;
    }


    public double getAviAMount() {
        return aviAMount;
    }

    public void setAviAMount(double aviAMount) {
        this.aviAMount = aviAMount;
    }

    public ArrayList<Voucher> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(ArrayList<Voucher> voucherList) {
        this.voucherList = voucherList;
    }
}
