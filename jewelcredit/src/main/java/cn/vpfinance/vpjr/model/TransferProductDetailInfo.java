package cn.vpfinance.vpjr.model;

import java.util.List;

/**
 */
public class TransferProductDetailInfo {
    /**
     * recordId : 12404
     * haveReturnMoney : 0
     * stayReturnMoney : 230.48
     * status : 3
     * allowTransferType : 1
     * tenderTime : 219.72
     * productType : 3
     * product : 0
     * title : V生活-万紫-005(转)
     * agreementList : [{"title":"查看协议","url":"http://192.168.1.190/loaninfo/loan_protocol?rid=12404"}]
     * rate : 9.8
     * borrowId : 312
     * subType : 2
     * month : 6个月
     * refundWay : 2
     * tenderMoney : 219.72
     */

    private int recordId;
    private String haveReturnMoney;
    private String stayReturnMoney;
    private String status;
    private String allowTransferType;
    private String tenderTime;
    private String productType;
    private String product;
    private String title;
    private double rate;
    private int borrowId;
    private String subType;
    private String month;
    private int refundWay;
    private String tenderMoney;
    private String finshTime;

    public String getFinshTime() {
        return finshTime;
    }

    public void setFinshTime(String finshTime) {
        this.finshTime = finshTime;
    }

    public double getTransferMinRate() {
        return transferMinRate;
    }

    public void setTransferMinRate(double transferMinRate) {
        this.transferMinRate = transferMinRate;
    }

    private double                  transferMinRate;
    /**
     * title : 查看协议
     * url : http://192.168.1.190/loaninfo/loan_protocol?rid=12404
     */

    private List<AgreementListBean> agreementList;

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getHaveReturnMoney() {
        return haveReturnMoney;
    }

    public void setHaveReturnMoney(String haveReturnMoney) {
        this.haveReturnMoney = haveReturnMoney;
    }

    public String getStayReturnMoney() {
        return stayReturnMoney;
    }

    public void setStayReturnMoney(String stayReturnMoney) {
        this.stayReturnMoney = stayReturnMoney;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAllowTransferType() {
        return allowTransferType;
    }

    public void setAllowTransferType(String allowTransferType) {
        this.allowTransferType = allowTransferType;
    }

    public String getTenderTime() {
        return tenderTime;
    }

    public void setTenderTime(String tenderTime) {
        this.tenderTime = tenderTime;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getRefundWay() {
        return refundWay;
    }

    public void setRefundWay(int refundWay) {
        this.refundWay = refundWay;
    }

    public String getTenderMoney() {
        return tenderMoney;
    }

    public void setTenderMoney(String tenderMoney) {
        this.tenderMoney = tenderMoney;
    }

    public List<AgreementListBean> getAgreementList() {
        return agreementList;
    }

    public void setAgreementList(List<AgreementListBean> agreementList) {
        this.agreementList = agreementList;
    }

    public static class AgreementListBean {
        private String title;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }



   /* *//**
     * haveReturnMoney : 2.09
     * stayReturnMoney : 66.54
     * status : 3
     * allowTransferType : 1
     * tenderTime : 65.5
     * productType : 0
     * title : V成长-电桩-001
     * rate : 9.6
     * borrowId : 199
     * subType : 2
     * month : 6
     * refundWay : 2
     * tenderMoney : 65.5
     *//*

    private String haveReturnMoney;
    private String stayReturnMoney;
    private String status;
    private String allowTransferType;
    private String tenderTime;
    private String productType;
    private String product;
    private String title;
    private double rate;
    private int borrowId;
    private String subType;
    private int month;
    private int refundWay;
    private String tenderMoney;

    public String getProduct(){
        return product;
    }

    public void setProduct(String product){
        this.product = product;
    }

    public String getHaveReturnMoney() {
        return haveReturnMoney;
    }

    public void setHaveReturnMoney(String haveReturnMoney) {
        this.haveReturnMoney = haveReturnMoney;
    }

    public String getStayReturnMoney() {
        return stayReturnMoney;
    }

    public void setStayReturnMoney(String stayReturnMoney) {
        this.stayReturnMoney = stayReturnMoney;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAllowTransferType() {
        return allowTransferType;
    }

    public void setAllowTransferType(String allowTransferType) {
        this.allowTransferType = allowTransferType;
    }

    public String getTenderTime() {
        return tenderTime;
    }

    public void setTenderTime(String tenderTime) {
        this.tenderTime = tenderTime;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getRefundWay() {
        return refundWay;
    }

    public void setRefundWay(int refundWay) {
        this.refundWay = refundWay;
    }

    public String getTenderMoney() {
        return tenderMoney;
    }

    public void setTenderMoney(String tenderMoney) {
        this.tenderMoney = tenderMoney;
    }*/
}
