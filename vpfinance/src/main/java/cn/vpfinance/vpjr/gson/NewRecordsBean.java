package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */
public class NewRecordsBean {


    /**
     * totalPage : 1
     * success : true
     * recordList : [{"recordId":47405,"title":"车贷宝2016120501","tenderMoney":"1000.00","tenderTime":"2016-12-06 11:30:32","voucherMoney":"0.58","tenderMonth":"12","loanState":1,"haveRedPacket":"false","shareUrl":"","couponsCount":1,"type":3,"isBook":"1","addRate":"1"},{"recordId":47404,"title":"车贷宝2016120501","tenderMoney":"100.00","tenderTime":"2016-12-06 11:14:11","voucherMoney":"0.00","tenderMonth":"12","loanState":1,"haveRedPacket":"true","shareUrl":"http://192.168.1.192/h5/inviteregister/getMoney?member=01040&id=0474040","couponsCount":0,"type":1,"isBook":"1"},{"recordId":47402,"title":"V管家-购车-024","tenderMoney":"1111.00","tenderTime":"2016-12-05 18:01:45","voucherMoney":"0.00","tenderMonth":"12","loanState":2,"haveRedPacket":"false","shareUrl":"","couponsCount":0,"type":1},{"recordId":47401,"title":"V成长-智能-038","tenderMoney":"200.00","tenderTime":"2016-12-05 18:01:22","voucherMoney":"0.00","tenderMonth":"12","loanState":2,"haveRedPacket":"false","shareUrl":"","couponsCount":0,"type":1},{"recordId":47395,"title":"珠宝贷201611151","tenderMoney":"2000.00","tenderTime":"2016-12-01 12:17:52","voucherMoney":"0.00","tenderMonth":"12","loanState":3,"haveRedPacket":"false","shareUrl":"","couponsCount":0,"type":1},{"recordId":47389,"title":"车贷宝201611171","tenderMoney":"12345.00","tenderTime":"2016-11-29 12:28:07","voucherMoney":"0.00","tenderMonth":"11","loanState":2,"haveRedPacket":"false","shareUrl":"","couponsCount":0,"type":1},{"recordId":47388,"title":"车贷宝201611171","tenderMoney":"9999.00","tenderTime":"2016-11-29 12:27:53","voucherMoney":"0.00","tenderMonth":"11","loanState":2,"haveRedPacket":"false","shareUrl":"","couponsCount":0,"type":1},{"recordId":47387,"title":"车贷宝201611171","tenderMoney":"1111.00","tenderTime":"2016-11-29 12:27:42","voucherMoney":"0.00","tenderMonth":"11","loanState":2,"haveRedPacket":"false","shareUrl":"","couponsCount":0,"type":1},{"recordId":47386,"title":"车贷宝201611171","tenderMoney":"5555.00","tenderTime":"2016-11-29 12:27:18","voucherMoney":"0.00","tenderMonth":"11","loanState":2,"haveRedPacket":"false","shareUrl":"","couponsCount":0,"type":1},{"recordId":47385,"title":"车贷宝201611171","tenderMoney":"11111.00","tenderTime":"2016-11-29 12:27:05","voucherMoney":"1.17","tenderMonth":"11","loanState":2,"haveRedPacket":"false","shareUrl":"","couponsCount":0,"type":2}]
     */

    private String totalPage;
    private String success;
    /**
     * recordId : 47405
     * title : 车贷宝2016120501
     * tenderMoney : 1000.00
     * tenderTime : 2016-12-06 11:30:32
     * voucherMoney : 0.58
     * tenderMonth : 12
     * loanState : 1
     * haveRedPacket : false
     * shareUrl :
     * couponsCount : 1
     * type : 3
     * isBook : 1
     * addRate : 1
     */

    private List<RecordListEntity> recordList;

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setRecordList(List<RecordListEntity> recordList) {
        this.recordList = recordList;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public String getSuccess() {
        return success;
    }

    public List<RecordListEntity> getRecordList() {
        return recordList;
    }

    public static class RecordListEntity {
        private int    recordId;
        private String title;
        private String tenderMoney;
        private String tenderTime;
        private String voucherMoney;
        private String tenderMonth;
        private int    loanState;
        private String haveRedPacket;
        private String shareUrl;
        private int    couponsCount;
        private int    type;
        private String isBook;
        private String addRate;
        private String recordPoolId;//有值就是定存宝
        private String preProfit;//预计收益

        public String getPreProfit() {
            return preProfit;
        }
        public void setPreProfit(String preProfit) {
            this.preProfit = preProfit;
        }

        private String deadline;//期限
        public String getDeadline() {
            return deadline;
        }
        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }

        public String getRecordPoolId() {
            return recordPoolId;
        }

        public void setRecordPoolId(String recordPoolId) {
            this.recordPoolId = recordPoolId;
        }

        public void setRecordId(int recordId) {
            this.recordId = recordId;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setTenderMoney(String tenderMoney) {
            this.tenderMoney = tenderMoney;
        }

        public void setTenderTime(String tenderTime) {
            this.tenderTime = tenderTime;
        }

        public void setVoucherMoney(String voucherMoney) {
            this.voucherMoney = voucherMoney;
        }

        public void setTenderMonth(String tenderMonth) {
            this.tenderMonth = tenderMonth;
        }

        public void setLoanState(int loanState) {
            this.loanState = loanState;
        }

        public void setHaveRedPacket(String haveRedPacket) {
            this.haveRedPacket = haveRedPacket;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

        public void setCouponsCount(int couponsCount) {
            this.couponsCount = couponsCount;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setIsBook(String isBook) {
            this.isBook = isBook;
        }

        public void setAddRate(String addRate) {
            this.addRate = addRate;
        }

        public int getRecordId() {
            return recordId;
        }

        public String getTitle() {
            return title;
        }

        public String getTenderMoney() {
            return tenderMoney;
        }

        public String getTenderTime() {
            return tenderTime;
        }

        public String getVoucherMoney() {
            return voucherMoney;
        }

        public String getTenderMonth() {
            return tenderMonth;
        }

        public int getLoanState() {
            return loanState;
        }

        public String getHaveRedPacket() {
            return haveRedPacket;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public int getCouponsCount() {
            return couponsCount;
        }

        public int getType() {
            return type;
        }

        public String getIsBook() {
            return isBook;
        }

        public String getAddRate() {
            return addRate;
        }
    }
}
