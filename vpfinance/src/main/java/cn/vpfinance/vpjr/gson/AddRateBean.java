package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/7/29.
 */
public class AddRateBean {


    /**
     * couponUserRelations : [{"userId":104,"useLimitMoney":100,"addRatePeriod":30,"expiredTm":1470379085000,"couponType":1,"getWay":1,"attribute1":null,"attribute2":null,"attribute3":null,"useRemarks":["100元气使用","3个月标以上起投","固定111标使用","加息日30天"],"enable":false,"couponStatus":null,"couponId":2,"value":"0.005","id":2}]
     * totalPage : 1
     */

    private String                          totalPage;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private int                             totalCount;
    /**
     * userId : 104
     * useLimitMoney : 100.0
     * addRatePeriod : 30
     * expiredTm : 1470379085000
     * couponType : 1
     * getWay : 1
     * attribute1 : null
     * attribute2 : null
     * attribute3 : null
     * useRemarks : ["100元气使用","3个月标以上起投","固定111标使用","加息日30天"]
     * enable : false
     * couponStatus : null
     * couponId : 2
     * value : 0.005
     * id : 2
     */

    private List<CouponUserRelationsEntity> couponUserRelations;

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public void setCouponUserRelations(List<CouponUserRelationsEntity> couponUserRelations) {
        this.couponUserRelations = couponUserRelations;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public List<CouponUserRelationsEntity> getCouponUserRelations() {
        return couponUserRelations;
    }

    public static class CouponUserRelationsEntity {
        private int    userId;
        private double useLimitMoney;
        private int    addRatePeriod;
        private long   expiredTm;
        private int    couponType;
        private String          getWay;
        private Object       attribute1;
        private Object       attribute2;
        private Object       attribute3;
        private String      enable;
        private Object       couponStatus;
        private int          couponId;
        private String       value;
        private int          id;
        private List<String> useRemarks;
        private int voucherStatus;

        public int getVoucherStatus() {
            return voucherStatus;
        }

        public void setVoucherStatus(int voucherStatus) {
            this.voucherStatus = voucherStatus;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setUseLimitMoney(double useLimitMoney) {
            this.useLimitMoney = useLimitMoney;
        }

        public void setAddRatePeriod(int addRatePeriod) {
            this.addRatePeriod = addRatePeriod;
        }

        public void setExpiredTm(long expiredTm) {
            this.expiredTm = expiredTm;
        }

        public void setCouponType(int couponType) {
            this.couponType = couponType;
        }

        public void setGetWay(String getWay) {
            this.getWay = getWay;
        }

        public void setAttribute1(Object attribute1) {
            this.attribute1 = attribute1;
        }

        public void setAttribute2(Object attribute2) {
            this.attribute2 = attribute2;
        }

        public void setAttribute3(Object attribute3) {
            this.attribute3 = attribute3;
        }

        public void setEnable(String enable) {
            this.enable = enable;
        }

        public void setCouponStatus(Object couponStatus) {
            this.couponStatus = couponStatus;
        }

        public void setCouponId(int couponId) {
            this.couponId = couponId;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setUseRemarks(List<String> useRemarks) {
            this.useRemarks = useRemarks;
        }

        public int getUserId() {
            return userId;
        }

        public double getUseLimitMoney() {
            return useLimitMoney;
        }

        public int getAddRatePeriod() {
            return addRatePeriod;
        }

        public long getExpiredTm() {
            return expiredTm;
        }

        public int getCouponType() {
            return couponType;
        }

        public String getGetWay() {
            return getWay;
        }

        public Object getAttribute1() {
            return attribute1;
        }

        public Object getAttribute2() {
            return attribute2;
        }

        public Object getAttribute3() {
            return attribute3;
        }

        public String getEnable() {
            return enable;
        }

        public Object getCouponStatus() {
            return couponStatus;
        }

        public int getCouponId() {
            return couponId;
        }

        public String getValue() {
            return value;
        }

        public int getId() {
            return id;
        }

        public List<String> getUseRemarks() {
            return useRemarks;
        }
    }
}
