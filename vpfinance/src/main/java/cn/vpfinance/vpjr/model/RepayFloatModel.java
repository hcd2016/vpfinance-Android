package cn.vpfinance.vpjr.model;

import java.util.List;

public class RepayFloatModel {

    /**
     * maxRepayMoney : 50860.46
     * repayMoney : 355.22
     * rate : 8.50%
     * flowInvestReminder :
     * lastRepayDate : 2016-09-20
     * graceRate : 8.50%
     * repayDate : 2016-09-20
     * repayPlans : [{"repayDate":"2016-09-20","promitAmount":"355.22","capitalAmount":"0.00"}]
     */

    private String maxRepayMoney;
    private String repayMoney;
    private String rate;
    private String flowInvestReminder;
    private String lastRepayDate;
    private String graceRate;
    private String repayDate;
    private List<RepayPlansBean> repayPlans;

    public String getMaxRepayMoney() {
        return maxRepayMoney;
    }

    public void setMaxRepayMoney(String maxRepayMoney) {
        this.maxRepayMoney = maxRepayMoney;
    }

    public String getRepayMoney() {
        return repayMoney;
    }

    public void setRepayMoney(String repayMoney) {
        this.repayMoney = repayMoney;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getFlowInvestReminder() {
        return flowInvestReminder;
    }

    public void setFlowInvestReminder(String flowInvestReminder) {
        this.flowInvestReminder = flowInvestReminder;
    }

    public String getLastRepayDate() {
        return lastRepayDate;
    }

    public void setLastRepayDate(String lastRepayDate) {
        this.lastRepayDate = lastRepayDate;
    }

    public String getGraceRate() {
        return graceRate;
    }

    public void setGraceRate(String graceRate) {
        this.graceRate = graceRate;
    }

    public String getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(String repayDate) {
        this.repayDate = repayDate;
    }

    public List<RepayPlansBean> getRepayPlans() {
        return repayPlans;
    }

    public void setRepayPlans(List<RepayPlansBean> repayPlans) {
        this.repayPlans = repayPlans;
    }

    public static class RepayPlansBean {
        /**
         * repayDate : 2016-09-20
         * promitAmount : 355.22
         * capitalAmount : 0.00
         */

        private String repayDate;
        private String promitAmount;
        private String capitalAmount;

        public String getRepayDate() {
            return repayDate;
        }

        public void setRepayDate(String repayDate) {
            this.repayDate = repayDate;
        }

        public String getPromitAmount() {
            return promitAmount;
        }

        public void setPromitAmount(String promitAmount) {
            this.promitAmount = promitAmount;
        }

        public String getCapitalAmount() {
            return capitalAmount;
        }

        public void setCapitalAmount(String capitalAmount) {
            this.capitalAmount = capitalAmount;
        }
    }
}
