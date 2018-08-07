package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/7/29.
 */
public class SelectVoucherBean {


    /**
     * canUseVoucher : 21
     * amount : 1207.769999999999
     * voucherrate : 0.005
     * alAmount : 337.29000000000013
     * expAmount : 320.47999999999985
     * aviAmount : 550.0
     * voucherlist : [{"allowDraw":1,"hasTimes":1,"expireDate":1470326399000,"voucher":{"rate":0.005,"amount":30,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券30元，有效期7天","createdAt":1459130337000,"updatedAt":1459130337000,"name":"7天投资代金券30元","id":11,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469687729000,"updatedAt":1469687729000,"useTime":null,"expireDateMillSecs":1470326399000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"限3个月及以上定期产品可用","id":81736},{"allowDraw":1,"hasTimes":1,"expireDate":1470326399000,"voucher":{"rate":0.005,"amount":20,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券20元，有效期7天","createdAt":1433815137000,"updatedAt":1433815137000,"name":"7天投资代金券20元","id":2,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469687667000,"updatedAt":1469687667000,"useTime":null,"expireDateMillSecs":1470326399000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"投资任意定期产品可用","id":81735},{"allowDraw":1,"hasTimes":1,"expireDate":1470239999000,"voucher":{"rate":0.005,"amount":20,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券20元，有效期7天","createdAt":1433815137000,"updatedAt":1433815137000,"name":"7天投资代金券20元","id":2,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469617758000,"updatedAt":1469617758000,"useTime":null,"expireDateMillSecs":1470239999000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"投资任意定期产品可用","id":81522},{"allowDraw":1,"hasTimes":1,"expireDate":1470239999000,"voucher":{"rate":0.005,"amount":5,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券5元，有效期7天","createdAt":null,"updatedAt":null,"name":"7天投资代金券5元","id":6,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":1,"phoneStr":"134****3191","createdAt":1469609518000,"updatedAt":1469609518000,"useTime":null,"expireDateMillSecs":1470239999000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"投资任意定期产品可用","id":81495},{"allowDraw":1,"hasTimes":1,"expireDate":1470239999000,"voucher":{"rate":0.005,"amount":30,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券30元，有效期7天","createdAt":1459130337000,"updatedAt":1459130337000,"name":"7天投资代金券30元","id":11,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469605969000,"updatedAt":1469605969000,"useTime":null,"expireDateMillSecs":1470239999000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"限3个月及以上定期产品可用","id":81414},{"allowDraw":1,"hasTimes":1,"expireDate":1470239999000,"voucher":{"rate":0.005,"amount":20,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券20元，有效期7天","createdAt":1433815137000,"updatedAt":1433815137000,"name":"7天投资代金券20元","id":2,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469605935000,"updatedAt":1469605935000,"useTime":null,"expireDateMillSecs":1470239999000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"投资任意定期产品可用","id":81413},{"allowDraw":1,"hasTimes":1,"expireDate":1470239999000,"voucher":{"rate":0.005,"amount":30,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券30元，有效期7天","createdAt":1459130337000,"updatedAt":1459130337000,"name":"7天投资代金券30元","id":11,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469600594000,"updatedAt":1469600594000,"useTime":null,"expireDateMillSecs":1470239999000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"限3个月及以上定期产品可用","id":81393},{"allowDraw":1,"hasTimes":1,"expireDate":1470239999000,"voucher":{"rate":0.005,"amount":50,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券50元，有效期7天","createdAt":1433815137000,"updatedAt":1433815137000,"name":"7天投资代金券50元","id":1,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469592564000,"updatedAt":1469592564000,"useTime":null,"expireDateMillSecs":1470239999000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"限3个月及以上定期产品可用","id":81173},{"allowDraw":1,"hasTimes":1,"expireDate":1470239999000,"voucher":{"rate":0.005,"amount":30,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券30元，有效期7天","createdAt":1459130337000,"updatedAt":1459130337000,"name":"7天投资代金券30元","id":11,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469581786000,"updatedAt":1469581786000,"useTime":null,"expireDateMillSecs":1470239999000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"限3个月及以上定期产品可用","id":81086},{"allowDraw":1,"hasTimes":1,"expireDate":1470239999000,"voucher":{"rate":0.005,"amount":50,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券50元，有效期7天","createdAt":1433815137000,"updatedAt":1433815137000,"name":"7天投资代金券50元","id":1,"type":1},"usedTimes":0,"voucher_money":null,"shareNumber":0,"getWay":0,"phoneStr":"134****3191","createdAt":1469581786000,"updatedAt":1469581786000,"useTime":null,"expireDateMillSecs":1470239999000,"exchangeCode":null,"activity_type":null,"activity_record_id":null,"useRuleExplain":"限3个月及以上定期产品可用","id":81087}]
     * totalPage : 12
     */

    private int canUseVoucher;
    private String amount;
    private String voucherrate;
    private String alAmount;
    private String expAmount;
    private String aviAmount;
    private String totalPage;
    /**
     * allowDraw : 1
     * hasTimes : 1
     * expireDate : 1470326399000
     * voucher : {"rate":0.005,"amount":30,"times":1,"expValidation":7,"expUnit":3,"isUnique":0,"memo":"投资代金券30元，有效期7天","createdAt":1459130337000,"updatedAt":1459130337000,"name":"7天投资代金券30元","id":11,"type":1}
     * usedTimes : 0
     * voucher_money : null
     * shareNumber : 0
     * getWay : 0
     * phoneStr : 134****3191
     * createdAt : 1469687729000
     * updatedAt : 1469687729000
     * useTime : null
     * expireDateMillSecs : 1470326399000
     * exchangeCode : null
     * activity_type : null
     * activity_record_id : null
     * useRuleExplain : 限3个月及以上定期产品可用
     * id : 81736
     */

    private List<VoucherlistEntity> voucherlist;

    public void setCanUseVoucher(int canUseVoucher) {
        this.canUseVoucher = canUseVoucher;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setVoucherrate(String voucherrate) {
        this.voucherrate = voucherrate;
    }

    public void setAlAmount(String alAmount) {
        this.alAmount = alAmount;
    }

    public void setExpAmount(String expAmount) {
        this.expAmount = expAmount;
    }

    public void setAviAmount(String aviAmount) {
        this.aviAmount = aviAmount;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public void setVoucherlist(List<VoucherlistEntity> voucherlist) {
        this.voucherlist = voucherlist;
    }

    public int getCanUseVoucher() {
        return canUseVoucher;
    }

    public String getAmount() {
        return amount;
    }

    public String getVoucherrate() {
        return voucherrate;
    }

    public String getAlAmount() {
        return alAmount;
    }

    public String getExpAmount() {
        return expAmount;
    }

    public String getAviAmount() {
        return aviAmount;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public List<VoucherlistEntity> getVoucherlist() {
        return voucherlist;
    }

    public static class VoucherlistEntity {
        private int  allowDraw;
        private int  hasTimes;
        private long expireDate;
        /**
         * rate : 0.005
         * amount : 30.0
         * times : 1
         * expValidation : 7
         * expUnit : 3
         * isUnique : 0
         * memo : 投资代金券30元，有效期7天
         * createdAt : 1459130337000
         * updatedAt : 1459130337000
         * name : 7天投资代金券30元
         * id : 11
         * type : 1
         */

        private VoucherEntity voucher;
        private String    usedTimes;
        private double voucher_money;
        private String    shareNumber;
        private String    getWay;
        private String phoneStr;
        private String   createdAt;
        private String   updatedAt;
        private Object useTime;
        private String   expireDateMillSecs;
        private Object exchangeCode;
        private String activity_type;
        private Object activity_record_id;
        private String useRuleExplain;
        private int voucherStatus;
        private int    id;

        public int getVoucherStatus() {
            return voucherStatus;
        }

        public void setVoucherStatus(int voucherStatus) {
            this.voucherStatus = voucherStatus;
        }

        public void setAllowDraw(int allowDraw) {
            this.allowDraw = allowDraw;
        }

        public void setHasTimes(int hasTimes) {
            this.hasTimes = hasTimes;
        }

        public void setExpireDate(long expireDate) {
            this.expireDate = expireDate;
        }

        public void setVoucher(VoucherEntity voucher) {
            this.voucher = voucher;
        }

        public void setUsedTimes(String usedTimes) {
            this.usedTimes = usedTimes;
        }

        public void setVoucher_money(double voucher_money) {
            this.voucher_money = voucher_money;
        }

        public void setShareNumber(String shareNumber) {
            this.shareNumber = shareNumber;
        }

        public void setGetWay(String getWay) {
            this.getWay = getWay;
        }

        public void setPhoneStr(String phoneStr) {
            this.phoneStr = phoneStr;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setUseTime(Object useTime) {
            this.useTime = useTime;
        }

        public void setExpireDateMillSecs(String expireDateMillSecs) {
            this.expireDateMillSecs = expireDateMillSecs;
        }

        public void setExchangeCode(Object exchangeCode) {
            this.exchangeCode = exchangeCode;
        }

        public void setActivity_type(String activity_type) {
            this.activity_type = activity_type;
        }

        public void setActivity_record_id(Object activity_record_id) {
            this.activity_record_id = activity_record_id;
        }

        public void setUseRuleExplain(String useRuleExplain) {
            this.useRuleExplain = useRuleExplain;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAllowDraw() {
            return allowDraw;
        }

        public int getHasTimes() {
            return hasTimes;
        }

        public long getExpireDate() {
            return expireDate;
        }

        public VoucherEntity getVoucher() {
            return voucher;
        }

        public String getUsedTimes() {
            return usedTimes;
        }

        public double getVoucher_money() {
            return voucher_money;
        }

        public String getShareNumber() {
            return shareNumber;
        }

        public String getGetWay() {
            return getWay;
        }

        public String getPhoneStr() {
            return phoneStr;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public Object getUseTime() {
            return useTime;
        }

        public String getExpireDateMillSecs() {
            return expireDateMillSecs;
        }

        public Object getExchangeCode() {
            return exchangeCode;
        }

        public String getActivity_type() {
            return activity_type;
        }

        public Object getActivity_record_id() {
            return activity_record_id;
        }

        public String getUseRuleExplain() {
            return useRuleExplain;
        }

        public int getId() {
            return id;
        }

        public static class VoucherEntity {
            private double rate;
            private double amount;
            private int    times;
            private int    expValidation;
            private int    expUnit;
            private int    isUnique;
            private String memo;
            private long   createdAt;
            private long   updatedAt;
            private String name;
            private int    id;
            private int    type;

            public void setRate(double rate) {
                this.rate = rate;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public void setTimes(int times) {
                this.times = times;
            }

            public void setExpValidation(int expValidation) {
                this.expValidation = expValidation;
            }

            public void setExpUnit(int expUnit) {
                this.expUnit = expUnit;
            }

            public void setIsUnique(int isUnique) {
                this.isUnique = isUnique;
            }

            public void setMemo(String memo) {
                this.memo = memo;
            }

            public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
            }

            public void setUpdatedAt(long updatedAt) {
                this.updatedAt = updatedAt;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setId(int id) {
                this.id = id;
            }

            public void setType(int type) {
                this.type = type;
            }

            public double getRate() {
                return rate;
            }

            public double getAmount() {
                return amount;
            }

            public int getTimes() {
                return times;
            }

            public int getExpValidation() {
                return expValidation;
            }

            public int getExpUnit() {
                return expUnit;
            }

            public int getIsUnique() {
                return isUnique;
            }

            public String getMemo() {
                return memo;
            }

            public long getCreatedAt() {
                return createdAt;
            }

            public long getUpdatedAt() {
                return updatedAt;
            }

            public String getName() {
                return name;
            }

            public int getId() {
                return id;
            }

            public int getType() {
                return type;
            }
        }
    }
}
