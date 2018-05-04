package cn.vpfinance.vpjr.model;

public class BuyResult {
	int status;
	double scale;
	int maxCopies;
	int buyCount;
	long bidEndTime;
	double money;
	double maxMoney;
	double reMoney;
	int loanunit;
	String statusDesc;
	String doubleFestival;
	String cashTime;
	String imageUrl;

	public String getActivityUrl() {
		return activityUrl;
	}

	public void setActivityUrl(String activityUrl) {
		this.activityUrl = activityUrl;
	}

	public String getDoubleFestival() {
		return doubleFestival;
	}

	public void setDoubleFestival(String doubleFestival) {
		this.doubleFestival = doubleFestival;
	}

	public String getCashTime() {
		return cashTime;
	}

	public void setCashTime(String cashTime) {
		this.cashTime = cashTime;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	String activityUrl;

	public String getRedpacketstrurl() {
		return redpacketstrurl;
	}

	public void setRedpacketstrurl(String redpacketstrurl) {
		this.redpacketstrurl = redpacketstrurl;
	}

	String redpacketstrurl;

	String redPacketCount;

	public String getRedPacketCount() {
		return redPacketCount;
	}

	public void setRedPacketCount(String redPacketCount) {
		this.redPacketCount = redPacketCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public int getMaxCopies() {
		return maxCopies;
	}

	public void setMaxCopies(int maxCopies) {
		this.maxCopies = maxCopies;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public long getBidEndTime() {
		return bidEndTime;
	}

	public void setBidEndTime(long bidEndTime) {
		this.bidEndTime = bidEndTime;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getMaxMoney() {
		return maxMoney;
	}

	public void setMaxMoney(double maxMoney) {
		this.maxMoney = maxMoney;
	}

	public double getReMoney() {
		return reMoney;
	}

	public void setReMoney(double reMoney) {
		this.reMoney = reMoney;
	}

	public int getLoanunit() {
		return loanunit;
	}

	public void setLoanunit(int loanunit) {
		this.loanunit = loanunit;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

}
