package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 */
public class ProductCarInfo {

    /**
     * allowTransfer : false
     * borrowing : 捷融作为原始借款的出借方，已成功将款项借出，并且扣押了借入方的车辆， 捷融是原始借款的债权人，现通过微品金融平台将其债权资产通过转让的方 式来进行融资，并且到期承诺回购。
     * safeway : 原债权人担保回购
     * carsInfo : [{"carinfonum":"车辆一","carinfo":{"buy_price":"100","buy_time":"2016-06-07","car_name":"雷克萨斯","car_num":"粤B283A123","debtor_age":"31","debtor_sex":"男","id":31,"kilometres":"2345","loanSign_id":490,"loanperiod":"1个月","pledge_price":"90","predebtor":"李四","remainloanMoney":"900000","sumloanMoney":"900000"},"carinfodetail":[{"car_info_id":31,"id":20,"img_name":"汽车图片","img_url":"http://localhost:8080/upload/carpledge/20160711085542.JPG"}]},{"carinfonum":"车辆二","carinfo":{"buy_price":"80","buy_time":"2016-06-06","car_name":"奥迪A4","car_num":"粤B28374","debtor_age":"30","debtor_sex":"男","id":30,"kilometres":"43242","loanSign_id":490,"loanperiod":"1个月","pledge_price":"60","predebtor":"王五","remainloanMoney":"600000","sumloanMoney":"600000"},"carinfodetail":[{"car_info_id":30,"id":19,"img_name":"汽车图片","img_url":"http://localhost:8080/upload/carpledge/20160711085428.JPG"}]},{"carinfonum":"车辆三","carinfo":{"buy_price":"30000元","buy_time":"1","car_name":"奥迪","car_num":"A12312","debtor_age":"12","debtor_sex":"男","id":29,"kilometres":"213123","loanSign_id":490,"loanperiod":"12","pledge_price":"213123","predebtor":"王五","remainloanMoney":"11000","sumloanMoney":"11000"},"carinfodetail":[]}]
     * showRecord : 1
     * bidEndTime : 1469753480000
     * loansignbasic : {"assure":"1","behoof":"1","bidTime":1,"creditTime":"","guaranteesAmt":0,"loanCategory":0,"loanExplain":"","loanNumber":"1468198287","loanOrigin":"","loanSignTime":"2016-07-11 08:55:53","loanTitle":"车贷标测试","mgtMoney":0,"mgtMoneyScale":0,"overview":"","riskAssess":1,"riskCtrlWay":"","speech":"","views":15,"reward":"0.0","borrower":"上海慧连信息技术有限公司"}
     * loanrecords : [{"username":"131******38","rate":7.5,"tendMoney":100,"payStatus":"支付成功","paytime":"2016-07-11 08:59:03"}]
     * total_tend_money : 100.0
     * loansign : {"allowTransferType":0,"counterparts":1,"deleted":0,"id":490,"incomeStr":"","loanRateRange":"","loanType":1,"loanUnit":100,"loansignType":{"id":1,"maxcredit":1000000,"maxmoney":24,"maxrate":0.24,"mincredit":100,"minmoney":1,"minrate":0.12,"money":12,"typename":"薪金贷"},"loansignTypeId":0,"loanstate":2,"month":1,"poolNum":0,"product":0,"productType":2,"publishTime":"2016-07-11 08:56:04","rate":0.075,"rateRange":"","realDay":0,"refundWay":1,"status":0,"subType":1,"useDay":0,"vipCounterparts":1,"issueLoan":"888888","isAllowTrip":0}
     */

    private String allowTransfer;
    private String borrowing;
    private String safeway;
    private int showRecord;
    private long bidEndTime;
    /**
     * assure : 1
     * behoof : 1
     * bidTime : 1
     * creditTime :
     * guaranteesAmt : 0.0
     * loanCategory : 0
     * loanExplain :
     * loanNumber : 1468198287
     * loanOrigin :
     * loanSignTime : 2016-07-11 08:55:53
     * loanTitle : 车贷标测试
     * mgtMoney : 0.0
     * mgtMoneyScale : 0.0
     * overview :
     * riskAssess : 1
     * riskCtrlWay :
     * speech :
     * views : 15
     * reward : 0.0
     * borrower : 上海慧连信息技术有限公司
     */

    private LoansignbasicBean loansignbasic;
    private double total_tend_money;
    /**
     * allowTransferType : 0
     * counterparts : 1
     * deleted : 0
     * id : 490
     * incomeStr :
     * loanRateRange :
     * loanType : 1
     * loanUnit : 100
     * loansignType : {"id":1,"maxcredit":1000000,"maxmoney":24,"maxrate":0.24,"mincredit":100,"minmoney":1,"minrate":0.12,"money":12,"typename":"薪金贷"}
     * loansignTypeId : 0
     * loanstate : 2
     * month : 1
     * poolNum : 0
     * product : 0
     * productType : 2
     * publishTime : 2016-07-11 08:56:04
     * rate : 0.075
     * rateRange :
     * realDay : 0
     * refundWay : 1
     * status : 0
     * subType : 1
     * useDay : 0
     * vipCounterparts : 1
     * issueLoan : 888888
     * isAllowTrip : 0
     */

    private LoansignBean loansign;
    /**
     * carinfonum : 车辆一
     * carinfo : {"buy_price":"100","buy_time":"2016-06-07","car_name":"雷克萨斯","car_num":"粤B283A123","debtor_age":"31","debtor_sex":"男","id":31,"kilometres":"2345","loanSign_id":490,"loanperiod":"1个月","pledge_price":"90","predebtor":"李四","remainloanMoney":"900000","sumloanMoney":"900000"}
     * carinfodetail : [{"car_info_id":31,"id":20,"img_name":"汽车图片","img_url":"http://localhost:8080/upload/carpledge/20160711085542.JPG"}]
     */

    private List<CarsInfoBean> carsInfo;
    /**
     * username : 131******38
     * rate : 7.5
     * tendMoney : 100.0
     * payStatus : 支付成功
     * paytime : 2016-07-11 08:59:03
     */

    private List<LoanrecordsBean> loanrecords;

    public String getAllowTransfer() {
        return allowTransfer;
    }

    public void setAllowTransfer(String allowTransfer) {
        this.allowTransfer = allowTransfer;
    }

    public String getBorrowing() {
        return borrowing;
    }

    public void setBorrowing(String borrowing) {
        this.borrowing = borrowing;
    }

    public String getSafeway() {
        return safeway;
    }

    public void setSafeway(String safeway) {
        this.safeway = safeway;
    }

    public int getShowRecord() {
        return showRecord;
    }

    public void setShowRecord(int showRecord) {
        this.showRecord = showRecord;
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

    public LoansignBean getLoansign() {
        return loansign;
    }

    public void setLoansign(LoansignBean loansign) {
        this.loansign = loansign;
    }

    public List<CarsInfoBean> getCarsInfo() {
        return carsInfo;
    }

    public void setCarsInfo(List<CarsInfoBean> carsInfo) {
        this.carsInfo = carsInfo;
    }

    public List<LoanrecordsBean> getLoanrecords() {
        return loanrecords;
    }

    public void setLoanrecords(List<LoanrecordsBean> loanrecords) {
        this.loanrecords = loanrecords;
    }

    public static class LoansignbasicBean {
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
        private String borrower;

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

        public String getBorrower() {
            return borrower;
        }

        public void setBorrower(String borrower) {
            this.borrower = borrower;
        }
    }

    public static class LoansignBean {
        private int allowTransferType;
        private int counterparts;
        private int deleted;
        private int id;
        private String incomeStr;
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
        private int isAllowTrip;

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

        public int getIsAllowTrip() {
            return isAllowTrip;
        }

        public void setIsAllowTrip(int isAllowTrip) {
            this.isAllowTrip = isAllowTrip;
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

    public static class CarsInfoBean {
        private String carinfonum;
        /**
         * buy_price : 100
         * buy_time : 2016-06-07
         * car_name : 雷克萨斯
         * car_num : 粤B283A123
         * debtor_age : 31
         * debtor_sex : 男
         * id : 31
         * kilometres : 2345
         * loanSign_id : 490
         * loanperiod : 1个月
         * pledge_price : 90
         * predebtor : 李四
         * remainloanMoney : 900000
         * sumloanMoney : 900000
         */

        private CarinfoBean carinfo;
        /**
         * car_info_id : 31
         * id : 20
         * img_name : 汽车图片
         * img_url : http://localhost:8080/upload/carpledge/20160711085542.JPG
         */

        private List<CarinfodetailBean> carinfodetail;

        public String getCarinfonum() {
            return carinfonum;
        }

        public void setCarinfonum(String carinfonum) {
            this.carinfonum = carinfonum;
        }

        public CarinfoBean getCarinfo() {
            return carinfo;
        }

        public void setCarinfo(CarinfoBean carinfo) {
            this.carinfo = carinfo;
        }

        public List<CarinfodetailBean> getCarinfodetail() {
            return carinfodetail;
        }

        public void setCarinfodetail(List<CarinfodetailBean> carinfodetail) {
            this.carinfodetail = carinfodetail;
        }

        public static class CarinfoBean {
            private String buy_price;
            private String buy_time;
            private String car_name;
            private String car_num;
            private String debtor_age;
            private String debtor_sex;
            private int id;
            private String kilometres;
            private int loanSign_id;
            private String loanperiod;
            private String pledge_price;
            private String predebtor;
            private String remainloanMoney;
            private String sumloanMoney;

            public String getBuy_price() {
                return buy_price;
            }

            public void setBuy_price(String buy_price) {
                this.buy_price = buy_price;
            }

            public String getBuy_time() {
                return buy_time;
            }

            public void setBuy_time(String buy_time) {
                this.buy_time = buy_time;
            }

            public String getCar_name() {
                return car_name;
            }

            public void setCar_name(String car_name) {
                this.car_name = car_name;
            }

            public String getCar_num() {
                return car_num;
            }

            public void setCar_num(String car_num) {
                this.car_num = car_num;
            }

            public String getDebtor_age() {
                return debtor_age;
            }

            public void setDebtor_age(String debtor_age) {
                this.debtor_age = debtor_age;
            }

            public String getDebtor_sex() {
                return debtor_sex;
            }

            public void setDebtor_sex(String debtor_sex) {
                this.debtor_sex = debtor_sex;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getKilometres() {
                return kilometres;
            }

            public void setKilometres(String kilometres) {
                this.kilometres = kilometres;
            }

            public int getLoanSign_id() {
                return loanSign_id;
            }

            public void setLoanSign_id(int loanSign_id) {
                this.loanSign_id = loanSign_id;
            }

            public String getLoanperiod() {
                return loanperiod;
            }

            public void setLoanperiod(String loanperiod) {
                this.loanperiod = loanperiod;
            }

            public String getPledge_price() {
                return pledge_price;
            }

            public void setPledge_price(String pledge_price) {
                this.pledge_price = pledge_price;
            }

            public String getPredebtor() {
                return predebtor;
            }

            public void setPredebtor(String predebtor) {
                this.predebtor = predebtor;
            }

            public String getRemainloanMoney() {
                return remainloanMoney;
            }

            public void setRemainloanMoney(String remainloanMoney) {
                this.remainloanMoney = remainloanMoney;
            }

            public String getSumloanMoney() {
                return sumloanMoney;
            }

            public void setSumloanMoney(String sumloanMoney) {
                this.sumloanMoney = sumloanMoney;
            }
        }

        public static class CarinfodetailBean {
            private int car_info_id;
            private int id;
            private String img_name;
            private String img_url;

            public int getCar_info_id() {
                return car_info_id;
            }

            public void setCar_info_id(int car_info_id) {
                this.car_info_id = car_info_id;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImg_name() {
                return img_name;
            }

            public void setImg_name(String img_name) {
                this.img_name = img_name;
            }

            public String getImg_url() {
                return img_url;
            }

            public void setImg_url(String img_url) {
                this.img_url = img_url;
            }
        }
    }

    public static class LoanrecordsBean {
        private String username;
        private double rate;
        private double tendMoney;
        private String payStatus;
        private String paytime;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public double getTendMoney() {
            return tendMoney;
        }

        public void setTendMoney(double tendMoney) {
            this.tendMoney = tendMoney;
        }

        public String getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(String payStatus) {
            this.payStatus = payStatus;
        }

        public String getPaytime() {
            return paytime;
        }

        public void setPaytime(String paytime) {
            this.paytime = paytime;
        }
    }
}
