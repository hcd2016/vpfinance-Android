package cn.vpfinance.vpjr.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "FINANCE_PRODUCT".
 */
public class FinanceProduct {

    private Long id;
    private String buyCount;
    private Long bidEndTime;
    private int type;
    private double total_tend_money;
    private long pid;
    private long totalPage;
    private String assure;
    private String borrower;
    private String riskGrade;
    private String behoof;
    private String bidTime;
    private String creditTime;
    private double guaranteesAmt;
    private long loanCategory;
    private String loanExplain;
    private String loanNumber;
    private String loanOrigin;
    private String loanSignTime;
    private String loanTitle;
    private double mgtMoney;
    private double mgtMoneyScale;
    private String overview;
    private long riskAssess;
    private String riskCtrlWay;
    private double rate;
    private String reward;
    private double issueLoan;
    private long loanType;
    private long loanstate;
    private long refundWay;
    private double loanUnit;
    private long month;
    private String publishTime;
    private long subType;
    private long product;
    private String productType;
    private String allowTransfer;
    private String imageUrl;
    private String maxRate;
    private String minRate;
    private Double promitRate;
    private long status;
    private Integer isAllowTrip;
    private String borrowing;
    private String safeway;
    private String bookCouponNumber;
    private Double bookPercent;
    private double amount;
    private double openAmount;
    private double yearInterest;
    private int poolNum;
    private int soldOut;
    private double scale;
    private double maxMoney;
    private String repaytime;
    private Integer periods;
    private String repayStatus;
    private Integer totalPeriod;
    private Integer originLoanId;
    private String noRepayList;
    private String debtUser;
    private Double originIssueLoan;
    private double appreciation;
    private double discountMoney;
    private int distype;
    private int share;
    private double tenderMoney;
    private long userAuth;
    private long userDebt;
    private long borrowerId;
    private String isCard;
    private long age;
    private double money;
    private String realName;
    private double credit;
    private String remark;
    private long updateTime;
    private String lastTenderTime;
    private String creditTime2;
    private String repays;
    private String imageUrlByIphone;
    private String givePhone;

    public FinanceProduct() {
    }

    public FinanceProduct(Long id) {
        this.id = id;
    }

    public FinanceProduct(Long id, String buyCount, Long bidEndTime, int type, double total_tend_money, long pid, long totalPage, String assure, String borrower, String riskGrade, String behoof, String bidTime, String creditTime, double guaranteesAmt, long loanCategory, String loanExplain, String loanNumber, String loanOrigin, String loanSignTime, String loanTitle, double mgtMoney, double mgtMoneyScale, String overview, long riskAssess, String riskCtrlWay, double rate, String reward, double issueLoan, long loanType, long loanstate, long refundWay, double loanUnit, long month, String publishTime, long subType, long product, String productType, String allowTransfer, String imageUrl, String maxRate, String minRate, Double promitRate, long status, Integer isAllowTrip, String borrowing, String safeway, String bookCouponNumber, Double bookPercent, double amount, double openAmount, double yearInterest, int poolNum, int soldOut, double scale, double maxMoney, String repaytime, Integer periods, String repayStatus, Integer totalPeriod, Integer originLoanId, String noRepayList, String debtUser, Double originIssueLoan, double appreciation, double discountMoney, int distype, int share, double tenderMoney, long userAuth, long userDebt, long borrowerId, String isCard, long age, double money, String realName, double credit, String remark, long updateTime, String lastTenderTime, String creditTime2, String repays, String imageUrlByIphone, String givePhone) {
        this.id = id;
        this.buyCount = buyCount;
        this.bidEndTime = bidEndTime;
        this.type = type;
        this.total_tend_money = total_tend_money;
        this.pid = pid;
        this.totalPage = totalPage;
        this.assure = assure;
        this.borrower = borrower;
        this.riskGrade = riskGrade;
        this.behoof = behoof;
        this.bidTime = bidTime;
        this.creditTime = creditTime;
        this.guaranteesAmt = guaranteesAmt;
        this.loanCategory = loanCategory;
        this.loanExplain = loanExplain;
        this.loanNumber = loanNumber;
        this.loanOrigin = loanOrigin;
        this.loanSignTime = loanSignTime;
        this.loanTitle = loanTitle;
        this.mgtMoney = mgtMoney;
        this.mgtMoneyScale = mgtMoneyScale;
        this.overview = overview;
        this.riskAssess = riskAssess;
        this.riskCtrlWay = riskCtrlWay;
        this.rate = rate;
        this.reward = reward;
        this.issueLoan = issueLoan;
        this.loanType = loanType;
        this.loanstate = loanstate;
        this.refundWay = refundWay;
        this.loanUnit = loanUnit;
        this.month = month;
        this.publishTime = publishTime;
        this.subType = subType;
        this.product = product;
        this.productType = productType;
        this.allowTransfer = allowTransfer;
        this.imageUrl = imageUrl;
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.promitRate = promitRate;
        this.status = status;
        this.isAllowTrip = isAllowTrip;
        this.borrowing = borrowing;
        this.safeway = safeway;
        this.bookCouponNumber = bookCouponNumber;
        this.bookPercent = bookPercent;
        this.amount = amount;
        this.openAmount = openAmount;
        this.yearInterest = yearInterest;
        this.poolNum = poolNum;
        this.soldOut = soldOut;
        this.scale = scale;
        this.maxMoney = maxMoney;
        this.repaytime = repaytime;
        this.periods = periods;
        this.repayStatus = repayStatus;
        this.totalPeriod = totalPeriod;
        this.originLoanId = originLoanId;
        this.noRepayList = noRepayList;
        this.debtUser = debtUser;
        this.originIssueLoan = originIssueLoan;
        this.appreciation = appreciation;
        this.discountMoney = discountMoney;
        this.distype = distype;
        this.share = share;
        this.tenderMoney = tenderMoney;
        this.userAuth = userAuth;
        this.userDebt = userDebt;
        this.borrowerId = borrowerId;
        this.isCard = isCard;
        this.age = age;
        this.money = money;
        this.realName = realName;
        this.credit = credit;
        this.remark = remark;
        this.updateTime = updateTime;
        this.lastTenderTime = lastTenderTime;
        this.creditTime2 = creditTime2;
        this.repays = repays;
        this.imageUrlByIphone = imageUrlByIphone;
        this.givePhone = givePhone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(String buyCount) {
        this.buyCount = buyCount;
    }

    public Long getBidEndTime() {
        return bidEndTime;
    }

    public void setBidEndTime(Long bidEndTime) {
        this.bidEndTime = bidEndTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getTotal_tend_money() {
        return total_tend_money;
    }

    public void setTotal_tend_money(double total_tend_money) {
        this.total_tend_money = total_tend_money;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public String getAssure() {
        return assure;
    }

    public void setAssure(String assure) {
        this.assure = assure;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public String getRiskGrade() {
        return riskGrade;
    }

    public void setRiskGrade(String riskGrade) {
        this.riskGrade = riskGrade;
    }

    public String getBehoof() {
        return behoof;
    }

    public void setBehoof(String behoof) {
        this.behoof = behoof;
    }

    public String getBidTime() {
        return bidTime;
    }

    public void setBidTime(String bidTime) {
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

    public long getLoanCategory() {
        return loanCategory;
    }

    public void setLoanCategory(long loanCategory) {
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

    public long getRiskAssess() {
        return riskAssess;
    }

    public void setRiskAssess(long riskAssess) {
        this.riskAssess = riskAssess;
    }

    public String getRiskCtrlWay() {
        return riskCtrlWay;
    }

    public void setRiskCtrlWay(String riskCtrlWay) {
        this.riskCtrlWay = riskCtrlWay;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public double getIssueLoan() {
        return issueLoan;
    }

    public void setIssueLoan(double issueLoan) {
        this.issueLoan = issueLoan;
    }

    public long getLoanType() {
        return loanType;
    }

    public void setLoanType(long loanType) {
        this.loanType = loanType;
    }

    public long getLoanstate() {
        return loanstate;
    }

    public void setLoanstate(long loanstate) {
        this.loanstate = loanstate;
    }

    public long getRefundWay() {
        return refundWay;
    }

    public void setRefundWay(long refundWay) {
        this.refundWay = refundWay;
    }

    public double getLoanUnit() {
        return loanUnit;
    }

    public void setLoanUnit(double loanUnit) {
        this.loanUnit = loanUnit;
    }

    public long getMonth() {
        return month;
    }

    public void setMonth(long month) {
        this.month = month;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public long getSubType() {
        return subType;
    }

    public void setSubType(long subType) {
        this.subType = subType;
    }

    public long getProduct() {
        return product;
    }

    public void setProduct(long product) {
        this.product = product;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

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

    public String getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(String maxRate) {
        this.maxRate = maxRate;
    }

    public String getMinRate() {
        return minRate;
    }

    public void setMinRate(String minRate) {
        this.minRate = minRate;
    }

    public Double getPromitRate() {
        return promitRate;
    }

    public void setPromitRate(Double promitRate) {
        this.promitRate = promitRate;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public Integer getIsAllowTrip() {
        return isAllowTrip;
    }

    public void setIsAllowTrip(Integer isAllowTrip) {
        this.isAllowTrip = isAllowTrip;
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

    public String getBookCouponNumber() {
        return bookCouponNumber;
    }

    public void setBookCouponNumber(String bookCouponNumber) {
        this.bookCouponNumber = bookCouponNumber;
    }

    public Double getBookPercent() {
        return bookPercent;
    }

    public void setBookPercent(Double bookPercent) {
        this.bookPercent = bookPercent;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getOpenAmount() {
        return openAmount;
    }

    public void setOpenAmount(double openAmount) {
        this.openAmount = openAmount;
    }

    public double getYearInterest() {
        return yearInterest;
    }

    public void setYearInterest(double yearInterest) {
        this.yearInterest = yearInterest;
    }

    public int getPoolNum() {
        return poolNum;
    }

    public void setPoolNum(int poolNum) {
        this.poolNum = poolNum;
    }

    public int getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(int soldOut) {
        this.soldOut = soldOut;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(double maxMoney) {
        this.maxMoney = maxMoney;
    }

    public String getRepaytime() {
        return repaytime;
    }

    public void setRepaytime(String repaytime) {
        this.repaytime = repaytime;
    }

    public Integer getPeriods() {
        return periods;
    }

    public void setPeriods(Integer periods) {
        this.periods = periods;
    }

    public String getRepayStatus() {
        return repayStatus;
    }

    public void setRepayStatus(String repayStatus) {
        this.repayStatus = repayStatus;
    }

    public Integer getTotalPeriod() {
        return totalPeriod;
    }

    public void setTotalPeriod(Integer totalPeriod) {
        this.totalPeriod = totalPeriod;
    }

    public Integer getOriginLoanId() {
        return originLoanId;
    }

    public void setOriginLoanId(Integer originLoanId) {
        this.originLoanId = originLoanId;
    }

    public String getNoRepayList() {
        return noRepayList;
    }

    public void setNoRepayList(String noRepayList) {
        this.noRepayList = noRepayList;
    }

    public String getDebtUser() {
        return debtUser;
    }

    public void setDebtUser(String debtUser) {
        this.debtUser = debtUser;
    }

    public Double getOriginIssueLoan() {
        return originIssueLoan;
    }

    public void setOriginIssueLoan(Double originIssueLoan) {
        this.originIssueLoan = originIssueLoan;
    }

    public double getAppreciation() {
        return appreciation;
    }

    public void setAppreciation(double appreciation) {
        this.appreciation = appreciation;
    }

    public double getDiscountMoney() {
        return discountMoney;
    }

    public void setDiscountMoney(double discountMoney) {
        this.discountMoney = discountMoney;
    }

    public int getDistype() {
        return distype;
    }

    public void setDistype(int distype) {
        this.distype = distype;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public double getTenderMoney() {
        return tenderMoney;
    }

    public void setTenderMoney(double tenderMoney) {
        this.tenderMoney = tenderMoney;
    }

    public long getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(long userAuth) {
        this.userAuth = userAuth;
    }

    public long getUserDebt() {
        return userDebt;
    }

    public void setUserDebt(long userDebt) {
        this.userDebt = userDebt;
    }

    public long getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(long borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getIsCard() {
        return isCard;
    }

    public void setIsCard(String isCard) {
        this.isCard = isCard;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getLastTenderTime() {
        return lastTenderTime;
    }

    public void setLastTenderTime(String lastTenderTime) {
        this.lastTenderTime = lastTenderTime;
    }

    public String getCreditTime2() {
        return creditTime2;
    }

    public void setCreditTime2(String creditTime2) {
        this.creditTime2 = creditTime2;
    }

    public String getRepays() {
        return repays;
    }

    public void setRepays(String repays) {
        this.repays = repays;
    }

    public String getImageUrlByIphone() {
        return imageUrlByIphone;
    }

    public void setImageUrlByIphone(String imageUrlByIphone) {
        this.imageUrlByIphone = imageUrlByIphone;
    }

    public String getGivePhone() {
        return givePhone;
    }

    public void setGivePhone(String givePhone) {
        this.givePhone = givePhone;
    }

}
