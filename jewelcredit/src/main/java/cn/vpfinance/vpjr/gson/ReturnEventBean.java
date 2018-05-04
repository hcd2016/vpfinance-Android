package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 */
public class ReturnEventBean {


    private List<AlreadyReturnRecordEntity>                         stayReturnRecord;
    /**
     * periods : 4/6
     * capitalAmount : 0.00
     * profitAmount : 1.18
     * loanTitle : V孵化-沈阳-046
     */

    private List<AlreadyReturnRecordEntity> alreadyReturnRecord;

    public void setStayReturnRecord(List<AlreadyReturnRecordEntity> stayReturnRecord) {
        this.stayReturnRecord = stayReturnRecord;
    }

    public void setAlreadyReturnRecord(List<AlreadyReturnRecordEntity> alreadyReturnRecord) {
        this.alreadyReturnRecord = alreadyReturnRecord;
    }

    public List<AlreadyReturnRecordEntity> getStayReturnRecord() {
        return stayReturnRecord;
    }

    public List<AlreadyReturnRecordEntity> getAlreadyReturnRecord() {
        return alreadyReturnRecord;
    }

    public static class AlreadyReturnRecordEntity {
        private String periods;
        private String capitalAmount;
        private String profitAmount;
        private String loanTitle;
        private String attribute1;
        private String attribute2;

        public String getAttribute1() {
            return attribute1;
        }

        public void setAttribute1(String attribute1) {
            this.attribute1 = attribute1;
        }

        public String getAttribute2() {
            return attribute2;
        }

        public void setAttribute2(String attribute2) {
            this.attribute2 = attribute2;
        }

        public void setPeriods(String periods) {
            this.periods = periods;
        }

        public void setCapitalAmount(String capitalAmount) {
            this.capitalAmount = capitalAmount;
        }

        public void setProfitAmount(String profitAmount) {
            this.profitAmount = profitAmount;
        }

        public void setLoanTitle(String loanTitle) {
            this.loanTitle = loanTitle;
        }

        public String getPeriods() {
            return periods;
        }

        public String getCapitalAmount() {
            return capitalAmount;
        }

        public String getProfitAmount() {
            return profitAmount;
        }

        public String getLoanTitle() {
            return loanTitle;
        }
    }
}
