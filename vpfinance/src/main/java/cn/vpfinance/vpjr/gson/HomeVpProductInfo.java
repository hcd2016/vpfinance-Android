package cn.vpfinance.vpjr.gson;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 */
public class HomeVpProductInfo{

    /**
     * totalInterest : 12632347.49
     * loansigns : [{"allowTransfer":"false","imageUrl":"http://192.168.1.190//resources/h5/images/sy/sy-show1.jpg","bidEndTime":1461556800000,"loansignbasic":{"assure":"深圳市银联投资有限公司","behoof":"金融科技产业园（一期）项目产业楼运营资金周转","bidTime":1,"creditTime":"","guaranteesAmt":0,"loanCategory":0,"loanExplain":"","loanNumber":"1456906056","loanOrigin":"","loanSignTime":"2016-04-06 18:14:09","loanTitle":"乾润稳盈001（2-101）","mgtMoney":0,"mgtMoneyScale":0,"overview":"","riskAssess":1,"riskCtrlWay":"","speech":"","views":259,"reward":"0.0"},"total_tend_money":40000,"maxRate":"10.0%","loansign":{"allowTransferType":0,"counterparts":20000,"deleted":0,"id":338,"incomeStr":"","isAllowTrip":0,"loanRateRange":"","loanType":1,"loanUnit":8000,"loansignType":{"id":1,"maxcredit":1000000,"maxmoney":24,"maxrate":0.24,"mincredit":100,"minmoney":1,"minrate":0.12,"money":12,"typename":"薪金贷"},"loansignTypeId":0,"loanstate":2,"month":1,"poolNum":0,"product":0,"productType":3,"publishTime":"2016-01-30 12:53:29","rate":0.072,"rateRange":"","realDay":0,"refundWay":2,"status":0,"subType":2,"useDay":0,"vipCounterparts":20000,"issueLoan":"1.3504E+7"},"minRate":"7.2%","productType":"3"},{"allowTransfer":"false","loansignbasic":{"assure":"深圳市深圳**致远信息科技有限公司","behoof":"采购用款","bidTime":4,"creditTime":"","guaranteesAmt":0,"loanCategory":0,"loanExplain":"","loanNumber":"1459844320","loanOrigin":"","loanSignTime":"2016-04-05 16:26:13","loanTitle":"V成长-智能-017","mgtMoney":0,"mgtMoneyScale":0,"overview":"","riskAssess":1,"riskCtrlWay":"","speech":"","views":94,"reward":"0.0"},"total_tend_money":274337.12,"loansign":{"allowTransferType":0,"counterparts":2600,"deleted":0,"id":337,"incomeStr":"","isAllowTrip":0,"loanRateRange":"","loanType":1,"loanUnit":100,"loansignType":{"id":1,"maxcredit":1000000,"maxmoney":24,"maxrate":0.24,"mincredit":100,"minmoney":1,"minrate":0.12,"money":12,"typename":"薪金贷"},"loansignTypeId":0,"loanstate":2,"month":1,"poolNum":0,"product":0,"productType":0,"publishTime":"2016-04-07 10:00:51","rate":0.078,"rateRange":"","realDay":0,"refundWay":3,"status":0,"subType":2,"useDay":0,"vipCounterparts":2600,"issueLoan":"2600000.0"}},{"allowTransfer":"false","loansignbasic":{"assure":"深圳市**假日国际旅行社有限公司","behoof":"垫付消费者旅游资金","bidTime":4,"creditTime":"","guaranteesAmt":0,"loanCategory":0,"loanExplain":"","loanNumber":"1459842827","loanOrigin":"","loanSignTime":"2016-04-05 16:03:13","loanTitle":"V生活-星雅-006","mgtMoney":0,"mgtMoneyScale":0,"overview":"","riskAssess":1,"riskCtrlWay":"","speech":"","views":636,"reward":"0.0"},"total_tend_money":1556454.14,"loansign":{"allowTransferType":0,"counterparts":25000,"deleted":0,"id":336,"incomeStr":"","isAllowTrip":0,"loanRateRange":"","loanType":1,"loanUnit":100,"loansignType":{"id":1,"maxcredit":1000000,"maxmoney":24,"maxrate":0.24,"mincredit":100,"minmoney":1,"minrate":0.12,"money":12,"typename":"薪金贷"},"loansignTypeId":0,"loanstate":2,"month":2,"poolNum":0,"product":0,"productType":0,"publishTime":"2016-04-06 10:36:42","rate":0.085,"rateRange":"","realDay":0,"refundWay":2,"status":0,"subType":2,"useDay":0,"vipCounterparts":25000,"issueLoan":"2500000.0"}}]
     * totalMoney : 246133809.45
     */

    private String totalInterest;
    private String totalMoney;
    /**
     * allowTransfer : false
     * imageUrl : http://192.168.1.190//resources/h5/images/sy/sy-show1.jpg
     * bidEndTime : 1461556800000
     * loansignbasic : {"assure":"深圳市银联投资有限公司","behoof":"金融科技产业园（一期）项目产业楼运营资金周转","bidTime":1,"creditTime":"","guaranteesAmt":0,"loanCategory":0,"loanExplain":"","loanNumber":"1456906056","loanOrigin":"","loanSignTime":"2016-04-06 18:14:09","loanTitle":"乾润稳盈001（2-101）","mgtMoney":0,"mgtMoneyScale":0,"overview":"","riskAssess":1,"riskCtrlWay":"","speech":"","views":259,"reward":"0.0"}
     * total_tend_money : 40000.0
     * maxRate : 10.0%
     * loansign : {"allowTransferType":0,"counterparts":20000,"deleted":0,"id":338,"incomeStr":"","isAllowTrip":0,"loanRateRange":"","loanType":1,"loanUnit":8000,"loansignType":{"id":1,"maxcredit":1000000,"maxmoney":24,"maxrate":0.24,"mincredit":100,"minmoney":1,"minrate":0.12,"money":12,"typename":"薪金贷"},"loansignTypeId":0,"loanstate":2,"month":1,"poolNum":0,"product":0,"productType":3,"publishTime":"2016-01-30 12:53:29","rate":0.072,"rateRange":"","realDay":0,"refundWay":2,"status":0,"subType":2,"useDay":0,"vipCounterparts":20000,"issueLoan":"1.3504E+7"}
     * minRate : 7.2%
     * productType : 3
     */

    private List<LoansignsBean> loansigns;

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

    public List<LoansignsBean> getLoansigns() {
        return loansigns;
    }

    public void setLoansigns(List<LoansignsBean> loansigns) {
        this.loansigns = loansigns;
    }

    public static class LoansignsBean implements Parcelable{
        private String allowTransfer;
        private String imageUrl;
        private long bidEndTime;
        /**
         * assure : 深圳市银联投资有限公司
         * behoof : 金融科技产业园（一期）项目产业楼运营资金周转
         * bidTime : 1
         * creditTime :
         * guaranteesAmt : 0.0
         * loanCategory : 0
         * loanExplain :
         * loanNumber : 1456906056
         * loanOrigin :
         * loanSignTime : 2016-04-06 18:14:09
         * loanTitle : 乾润稳盈001（2-101）
         * mgtMoney : 0.0
         * mgtMoneyScale : 0.0
         * overview :
         * riskAssess : 1
         * riskCtrlWay :
         * speech :
         * views : 259
         * reward : 0.0
         */

        private String givePhone;//iphone活动,1代表该标参加送iphone活动

        public String getGivePhone() {
            return givePhone;
        }

        public void setGivePhone(String givePhone) {
            this.givePhone = givePhone;
        }

        private LoansignbasicBean loansignbasic;
        private double total_tend_money;
        private String maxRate;
        private long publishTime;

        public long getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(long publishTime) {
            this.publishTime = publishTime;
        }

        /**
         * allowTransferType : 0
         * counterparts : 20000
         * deleted : 0
         * id : 338
         * incomeStr :
         * isAllowTrip : 0
         * loanRateRange :
         * loanType : 1
         * loanUnit : 8000
         * loansignType : {"id":1,"maxcredit":1000000,"maxmoney":24,"maxrate":0.24,"mincredit":100,"minmoney":1,"minrate":0.12,"money":12,"typename":"薪金贷"}
         * loansignTypeId : 0
         * loanstate : 2
         * month : 1
         * poolNum : 0
         * product : 0
         * productType : 3
         * publishTime : 2016-01-30 12:53:29
         * rate : 0.072
         * rateRange :
         * realDay : 0
         * refundWay : 2
         * status : 0
         * subType : 2
         * useDay : 0
         * vipCounterparts : 20000
         * issueLoan : 1.3504E+7
         */

        private LoansignBean loansign;
        private String minRate;
        private String productType;

        protected LoansignsBean(Parcel in) {
            allowTransfer = in.readString();
            imageUrl = in.readString();
            bidEndTime = in.readLong();
            total_tend_money = in.readDouble();
            maxRate = in.readString();
            minRate = in.readString();
            productType = in.readString();
            publishTime = in.readLong();
        }

        public static final Creator<LoansignsBean> CREATOR = new Creator<LoansignsBean>() {
            @Override
            public LoansignsBean createFromParcel(Parcel in) {
                return new LoansignsBean(in);
            }

            @Override
            public LoansignsBean[] newArray(int size) {
                return new LoansignsBean[size];
            }
        };

        public String getAllowTransfer() {
            return allowTransfer;
        }

        public void setAllowTransfer(String allowTransfer) {
            this.allowTransfer = allowTransfer;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public long getBidEndTime() {
            return bidEndTime;
        }

        public void setBidEndTime(long bidEndTime) {
            this.bidEndTime = bidEndTime;
        }

        public LoansignbasicBean getLoansignbasic() {
            return loansignbasic;
        }

        public void setLoansignbasic(LoansignbasicBean loansignbasic) {
            this.loansignbasic = loansignbasic;
        }

        public double getTotal_tend_money() {
            return total_tend_money;
        }

        public void setTotal_tend_money(double total_tend_money) {
            this.total_tend_money = total_tend_money;
        }

        public String getMaxRate() {
            return maxRate;
        }

        public void setMaxRate(String maxRate) {
            this.maxRate = maxRate;
        }

        public LoansignBean getLoansign() {
            return loansign;
        }

        public void setLoansign(LoansignBean loansign) {
            this.loansign = loansign;
        }

        public String getMinRate() {
            return minRate;
        }

        public void setMinRate(String minRate) {
            this.minRate = minRate;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(allowTransfer);
            dest.writeString(imageUrl);
            dest.writeLong(bidEndTime);
            dest.writeDouble(total_tend_money);
            dest.writeString(maxRate);
            dest.writeString(minRate);
            dest.writeString(productType);
            dest.writeLong(publishTime);
        }

        public static class LoansignbasicBean implements Parcelable{
            private String assure;
            private String behoof;
            private int bidTime;
            private String creditTime;
            private double guaranteesAmt;
            private int loanCategory;
            private String loanExplain;
            private String loanNumber;
            private String loanOrigin;
            private String loanSignTime;
            private String loanTitle;
            private double mgtMoney;
            private double mgtMoneyScale;
            private String overview;
            private int riskAssess;
            private String riskCtrlWay;
            private String speech;
            private int views;
            private String reward;

            protected LoansignbasicBean(Parcel in) {
                assure = in.readString();
                behoof = in.readString();
                bidTime = in.readInt();
                creditTime = in.readString();
                guaranteesAmt = in.readDouble();
                loanCategory = in.readInt();
                loanExplain = in.readString();
                loanNumber = in.readString();
                loanOrigin = in.readString();
                loanSignTime = in.readString();
                loanTitle = in.readString();
                mgtMoney = in.readDouble();
                mgtMoneyScale = in.readDouble();
                overview = in.readString();
                riskAssess = in.readInt();
                riskCtrlWay = in.readString();
                speech = in.readString();
                views = in.readInt();
                reward = in.readString();
            }

            public static final Creator<LoansignbasicBean> CREATOR = new Creator<LoansignbasicBean>() {
                @Override
                public LoansignbasicBean createFromParcel(Parcel in) {
                    return new LoansignbasicBean(in);
                }

                @Override
                public LoansignbasicBean[] newArray(int size) {
                    return new LoansignbasicBean[size];
                }
            };

            public String getAssure() {
                return assure;
            }

            public void setAssure(String assure) {
                this.assure = assure;
            }

            public String getBehoof() {
                return behoof;
            }

            public void setBehoof(String behoof) {
                this.behoof = behoof;
            }

            public int getBidTime() {
                return bidTime;
            }

            public void setBidTime(int bidTime) {
                this.bidTime = bidTime;
            }

            public String getCreditTime() {
                return creditTime;
            }

            public void setCreditTime(String creditTime) {
                this.creditTime = creditTime;
            }

            public double getGuaranteesAmt() {
                return guaranteesAmt;
            }

            public void setGuaranteesAmt(double guaranteesAmt) {
                this.guaranteesAmt = guaranteesAmt;
            }

            public int getLoanCategory() {
                return loanCategory;
            }

            public void setLoanCategory(int loanCategory) {
                this.loanCategory = loanCategory;
            }

            public String getLoanExplain() {
                return loanExplain;
            }

            public void setLoanExplain(String loanExplain) {
                this.loanExplain = loanExplain;
            }

            public String getLoanNumber() {
                return loanNumber;
            }

            public void setLoanNumber(String loanNumber) {
                this.loanNumber = loanNumber;
            }

            public String getLoanOrigin() {
                return loanOrigin;
            }

            public void setLoanOrigin(String loanOrigin) {
                this.loanOrigin = loanOrigin;
            }

            public String getLoanSignTime() {
                return loanSignTime;
            }

            public void setLoanSignTime(String loanSignTime) {
                this.loanSignTime = loanSignTime;
            }

            public String getLoanTitle() {
                return loanTitle;
            }

            public void setLoanTitle(String loanTitle) {
                this.loanTitle = loanTitle;
            }

            public double getMgtMoney() {
                return mgtMoney;
            }

            public void setMgtMoney(double mgtMoney) {
                this.mgtMoney = mgtMoney;
            }

            public double getMgtMoneyScale() {
                return mgtMoneyScale;
            }

            public void setMgtMoneyScale(double mgtMoneyScale) {
                this.mgtMoneyScale = mgtMoneyScale;
            }

            public String getOverview() {
                return overview;
            }

            public void setOverview(String overview) {
                this.overview = overview;
            }

            public int getRiskAssess() {
                return riskAssess;
            }

            public void setRiskAssess(int riskAssess) {
                this.riskAssess = riskAssess;
            }

            public String getRiskCtrlWay() {
                return riskCtrlWay;
            }

            public void setRiskCtrlWay(String riskCtrlWay) {
                this.riskCtrlWay = riskCtrlWay;
            }

            public String getSpeech() {
                return speech;
            }

            public void setSpeech(String speech) {
                this.speech = speech;
            }

            public int getViews() {
                return views;
            }

            public void setViews(int views) {
                this.views = views;
            }

            public String getReward() {
                return reward;
            }

            public void setReward(String reward) {
                this.reward = reward;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(assure);
                dest.writeString(behoof);
                dest.writeInt(bidTime);
                dest.writeString(creditTime);
                dest.writeDouble(guaranteesAmt);
                dest.writeInt(loanCategory);
                dest.writeString(loanExplain);
                dest.writeString(loanNumber);
                dest.writeString(loanOrigin);
                dest.writeString(loanSignTime);
                dest.writeString(loanTitle);
                dest.writeDouble(mgtMoney);
                dest.writeDouble(mgtMoneyScale);
                dest.writeString(overview);
                dest.writeInt(riskAssess);
                dest.writeString(riskCtrlWay);
                dest.writeString(speech);
                dest.writeInt(views);
                dest.writeString(reward);
            }
        }

        public static class LoansignBean implements Parcelable{
            private int allowTransferType;
            private int counterparts;
            private int deleted;
            private int id;
            private String incomeStr;
            private int isAllowTrip;
            private String loanRateRange;
            private int loanType;
            private int loanUnit;
            /**
             * id : 1
             * maxcredit : 1000000.0
             * maxmoney : 24
             * maxrate : 0.24
             * mincredit : 100.0
             * minmoney : 1
             * minrate : 0.12
             * money : 12
             * typename : 薪金贷
             */

            private LoansignTypeBean loansignType;
            private int loansignTypeId;
            private int loanstate;
            private int month;
            private int poolNum;
            private int product;
            private int productType;
            private String publishTime;
            private double rate;
            private String rateRange;
            private int realDay;
            private int refundWay;
            private int status;
            private int subType;
            private int useDay;
            private int vipCounterparts;
            private String issueLoan;

            protected LoansignBean(Parcel in) {
                allowTransferType = in.readInt();
                counterparts = in.readInt();
                deleted = in.readInt();
                id = in.readInt();
                incomeStr = in.readString();
                isAllowTrip = in.readInt();
                loanRateRange = in.readString();
                loanType = in.readInt();
                loanUnit = in.readInt();
                loansignTypeId = in.readInt();
                loanstate = in.readInt();
                month = in.readInt();
                poolNum = in.readInt();
                product = in.readInt();
                productType = in.readInt();
                publishTime = in.readString();
                rate = in.readDouble();
                rateRange = in.readString();
                realDay = in.readInt();
                refundWay = in.readInt();
                status = in.readInt();
                subType = in.readInt();
                useDay = in.readInt();
                vipCounterparts = in.readInt();
                issueLoan = in.readString();
            }

            public static final Creator<LoansignBean> CREATOR = new Creator<LoansignBean>() {
                @Override
                public LoansignBean createFromParcel(Parcel in) {
                    return new LoansignBean(in);
                }

                @Override
                public LoansignBean[] newArray(int size) {
                    return new LoansignBean[size];
                }
            };

            public int getAllowTransferType() {
                return allowTransferType;
            }

            public void setAllowTransferType(int allowTransferType) {
                this.allowTransferType = allowTransferType;
            }

            public int getCounterparts() {
                return counterparts;
            }

            public void setCounterparts(int counterparts) {
                this.counterparts = counterparts;
            }

            public int getDeleted() {
                return deleted;
            }

            public void setDeleted(int deleted) {
                this.deleted = deleted;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getIncomeStr() {
                return incomeStr;
            }

            public void setIncomeStr(String incomeStr) {
                this.incomeStr = incomeStr;
            }

            public int getIsAllowTrip() {
                return isAllowTrip;
            }

            public void setIsAllowTrip(int isAllowTrip) {
                this.isAllowTrip = isAllowTrip;
            }

            public String getLoanRateRange() {
                return loanRateRange;
            }

            public void setLoanRateRange(String loanRateRange) {
                this.loanRateRange = loanRateRange;
            }

            public int getLoanType() {
                return loanType;
            }

            public void setLoanType(int loanType) {
                this.loanType = loanType;
            }

            public int getLoanUnit() {
                return loanUnit;
            }

            public void setLoanUnit(int loanUnit) {
                this.loanUnit = loanUnit;
            }

            public LoansignTypeBean getLoansignType() {
                return loansignType;
            }

            public void setLoansignType(LoansignTypeBean loansignType) {
                this.loansignType = loansignType;
            }

            public int getLoansignTypeId() {
                return loansignTypeId;
            }

            public void setLoansignTypeId(int loansignTypeId) {
                this.loansignTypeId = loansignTypeId;
            }

            public int getLoanstate() {
                return loanstate;
            }

            public void setLoanstate(int loanstate) {
                this.loanstate = loanstate;
            }

            public int getMonth() {
                return month;
            }

            public void setMonth(int month) {
                this.month = month;
            }

            public int getPoolNum() {
                return poolNum;
            }

            public void setPoolNum(int poolNum) {
                this.poolNum = poolNum;
            }

            public int getProduct() {
                return product;
            }

            public void setProduct(int product) {
                this.product = product;
            }

            public int getProductType() {
                return productType;
            }

            public void setProductType(int productType) {
                this.productType = productType;
            }

            public String getPublishTime() {
                return publishTime;
            }

            public void setPublishTime(String publishTime) {
                this.publishTime = publishTime;
            }

            public double getRate() {
                return rate;
            }

            public void setRate(double rate) {
                this.rate = rate;
            }

            public String getRateRange() {
                return rateRange;
            }

            public void setRateRange(String rateRange) {
                this.rateRange = rateRange;
            }

            public int getRealDay() {
                return realDay;
            }

            public void setRealDay(int realDay) {
                this.realDay = realDay;
            }

            public int getRefundWay() {
                return refundWay;
            }

            public void setRefundWay(int refundWay) {
                this.refundWay = refundWay;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getSubType() {
                return subType;
            }

            public void setSubType(int subType) {
                this.subType = subType;
            }

            public int getUseDay() {
                return useDay;
            }

            public void setUseDay(int useDay) {
                this.useDay = useDay;
            }

            public int getVipCounterparts() {
                return vipCounterparts;
            }

            public void setVipCounterparts(int vipCounterparts) {
                this.vipCounterparts = vipCounterparts;
            }

            public String getIssueLoan() {
                return issueLoan;
            }

            public void setIssueLoan(String issueLoan) {
                this.issueLoan = issueLoan;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(allowTransferType);
                dest.writeInt(counterparts);
                dest.writeInt(deleted);
                dest.writeInt(id);
                dest.writeString(incomeStr);
                dest.writeInt(isAllowTrip);
                dest.writeString(loanRateRange);
                dest.writeInt(loanType);
                dest.writeInt(loanUnit);
                dest.writeInt(loansignTypeId);
                dest.writeInt(loanstate);
                dest.writeInt(month);
                dest.writeInt(poolNum);
                dest.writeInt(product);
                dest.writeInt(productType);
                dest.writeString(publishTime);
                dest.writeDouble(rate);
                dest.writeString(rateRange);
                dest.writeInt(realDay);
                dest.writeInt(refundWay);
                dest.writeInt(status);
                dest.writeInt(subType);
                dest.writeInt(useDay);
                dest.writeInt(vipCounterparts);
                dest.writeString(issueLoan);
            }

            public static class LoansignTypeBean {
                private int id;
                private double maxcredit;
                private int maxmoney;
                private double maxrate;
                private double mincredit;
                private int minmoney;
                private double minrate;
                private int money;
                private String typename;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public double getMaxcredit() {
                    return maxcredit;
                }

                public void setMaxcredit(double maxcredit) {
                    this.maxcredit = maxcredit;
                }

                public int getMaxmoney() {
                    return maxmoney;
                }

                public void setMaxmoney(int maxmoney) {
                    this.maxmoney = maxmoney;
                }

                public double getMaxrate() {
                    return maxrate;
                }

                public void setMaxrate(double maxrate) {
                    this.maxrate = maxrate;
                }

                public double getMincredit() {
                    return mincredit;
                }

                public void setMincredit(double mincredit) {
                    this.mincredit = mincredit;
                }

                public int getMinmoney() {
                    return minmoney;
                }

                public void setMinmoney(int minmoney) {
                    this.minmoney = minmoney;
                }

                public double getMinrate() {
                    return minrate;
                }

                public void setMinrate(double minrate) {
                    this.minrate = minrate;
                }

                public int getMoney() {
                    return money;
                }

                public void setMoney(int money) {
                    this.money = money;
                }

                public String getTypename() {
                    return typename;
                }

                public void setTypename(String typename) {
                    this.typename = typename;
                }
            }
        }

        @Override
        public String toString() {
            return "LoansignsBean{" +
                    "allowTransfer='" + allowTransfer + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", bidEndTime=" + bidEndTime +
                    ", loansignbasic=" + loansignbasic +
                    ", total_tend_money=" + total_tend_money +
                    ", maxRate='" + maxRate + '\'' +
                    ", loansign=" + loansign +
                    ", minRate='" + minRate + '\'' +
                    ", productType='" + productType + '\'' +
                    '}';
        }
    }
}
