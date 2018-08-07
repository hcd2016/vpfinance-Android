package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 */
public class AddrateTicketTabInfo {

    /**
     * mapList : [{"userId":104,"addRatePeriod":30,"expiredTm":1470379085000,"couponType":1,"getWay":null,"attribute1":null,"attribute2":null,"attribute3":null,"couponStatus":"1","useRemarks":["100元气使用","3个月标以上起投","固定111标使用","加息日30天"],"enable":false,"useLimitMoney":null,"couponId":2,"value":"0.005","id":2}]
     * totalPage : 1
     */

    private String totalPage;
    /**
     * userId : 104
     * addRatePeriod : 30
     * expiredTm : 1470379085000
     * couponType : 1
     * getWay : null
     * attribute1 : null
     * attribute2 : null
     * attribute3 : null
     * couponStatus : 1
     * useRemarks : ["100元气使用","3个月标以上起投","固定111标使用","加息日30天"]
     * enable : false
     * useLimitMoney : null
     * couponId : 2
     * value : 0.005
     * id : 2
     */

    private List<MapListBean> mapList;

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public List<MapListBean> getMapList() {
        return mapList;
    }

    public void setMapList(List<MapListBean> mapList) {
        this.mapList = mapList;
    }

    public static class MapListBean {
        private int userId;
        private int addRatePeriod;
        private long expiredTm;
        private int couponType;
        private String getWay;
        private Object attribute1;
        private Object attribute2;
        private Object attribute3;
        private String couponStatus;
        private boolean enable;
        private Object useLimitMoney;
        private int couponId;
        private String value;
        private int id;
        private List<String> useRemarks;
        private int voucherStatus;

        public int getVoucherStatus() {
            return voucherStatus;
        }

        public void setVoucherStatus(int voucherStatus) {
            this.voucherStatus = voucherStatus;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getAddRatePeriod() {
            return addRatePeriod;
        }

        public void setAddRatePeriod(int addRatePeriod) {
            this.addRatePeriod = addRatePeriod;
        }

        public long getExpiredTm() {
            return expiredTm;
        }

        public void setExpiredTm(long expiredTm) {
            this.expiredTm = expiredTm;
        }

        public int getCouponType() {
            return couponType;
        }

        public void setCouponType(int couponType) {
            this.couponType = couponType;
        }

        public String getGetWay() {
            return getWay;
        }

        public void setGetWay(String getWay) {
            this.getWay = getWay;
        }

        public Object getAttribute1() {
            return attribute1;
        }

        public void setAttribute1(Object attribute1) {
            this.attribute1 = attribute1;
        }

        public Object getAttribute2() {
            return attribute2;
        }

        public void setAttribute2(Object attribute2) {
            this.attribute2 = attribute2;
        }

        public Object getAttribute3() {
            return attribute3;
        }

        public void setAttribute3(Object attribute3) {
            this.attribute3 = attribute3;
        }

        public String getCouponStatus() {
            return couponStatus;
        }

        public void setCouponStatus(String couponStatus) {
            this.couponStatus = couponStatus;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public Object getUseLimitMoney() {
            return useLimitMoney;
        }

        public void setUseLimitMoney(Object useLimitMoney) {
            this.useLimitMoney = useLimitMoney;
        }

        public int getCouponId() {
            return couponId;
        }

        public void setCouponId(int couponId) {
            this.couponId = couponId;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<String> getUseRemarks() {
            return useRemarks;
        }

        public void setUseRemarks(List<String> useRemarks) {
            this.useRemarks = useRemarks;
        }
    }
}
