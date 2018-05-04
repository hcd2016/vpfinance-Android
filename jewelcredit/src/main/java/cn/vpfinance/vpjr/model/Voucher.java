package cn.vpfinance.vpjr.model;

public class Voucher {

    private int id;
    private long expireDate;//失效时间

    private String name;
    private double amount;//金额
    private double rate;//抵扣比例
    private long createdAt;//创建时间
    private boolean selected;
    private boolean checked;
    private String useRuleExplain;//使用规则
    private int voucherStatus;//1.正常显示2.冻结置灰

    public int getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(int voucherStatus) {
        this.voucherStatus = voucherStatus;
    }

    public void setUseRuleExplain(String useRuleExplain){
        this.useRuleExplain = useRuleExplain;
    }

    public String getUseRuleExplain(){
        return useRuleExplain;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
