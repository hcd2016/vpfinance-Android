package cn.vpfinance.vpjr.model;

import java.util.List;

/**
 */
public class TransferRefundInfo {


    /**
     * periods : 1/6
     * status : 待回款
     * repayTime : 2016-04-25
     */

    private List<ReturnRecordBean> returnRecord;

    public List<ReturnRecordBean> getReturnRecord() {
        return returnRecord;
    }

    public void setReturnRecord(List<ReturnRecordBean> returnRecord) {
        this.returnRecord = returnRecord;
    }

    public static class ReturnRecordBean {
        private String periods;
        private String status;
        private String repayTime;
        private int color;
        private String money;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getPeriods() {
            return periods;
        }

        public void setPeriods(String periods) {
            this.periods = periods;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRepayTime() {
            return repayTime;
        }

        public void setRepayTime(String repayTime) {
            this.repayTime = repayTime;
        }
    }
}
