package cn.vpfinance.vpjr.model;

/**
 * Created by weipinjinrong on 15/9/24.
 */
public class HomeInfo {
    private String borrowId;
    private String annualRate;//年利率
    private String borrowTitle ;//项目名
    private int borrowStatus;
    private int month;
    private String issueLoan;
    private String totalInterest;
    private String totalMoney; //
    private long borrowEndTime;
    private String reward;
    private int product;

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public long getBorrowEndTime() {
        return borrowEndTime;
    }

    public void setBorrowEndTime(long borrowEndTime) {
        this.borrowEndTime = borrowEndTime;
    }

    public String getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(String totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public float getBorrowLoanPercent() {
        return borrowLoanPercent;
    }

    public void setBorrowLoanPercent(float borrowLoanPercent) {
        this.borrowLoanPercent = borrowLoanPercent;
    }

    private float borrowLoanPercent;

    public String getIssueLoan() {
        return issueLoan;
    }

    public void setIssueLoan(String issueLoan) {
        this.issueLoan = issueLoan;
    }

    public String getAnnualRate() {
        return annualRate;
    }

    public void setAnnualRate(String annualRate) {
        this.annualRate = annualRate;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public int getBorrowStatus() {
        return borrowStatus;
    }

    public void setBorrowStatus(int borrowStatus) {
        this.borrowStatus = borrowStatus;
    }

    public String getBorrowTitle() {
        return borrowTitle;
    }

    public void setBorrowTitle(String borrowTitle) {
        this.borrowTitle = borrowTitle;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
