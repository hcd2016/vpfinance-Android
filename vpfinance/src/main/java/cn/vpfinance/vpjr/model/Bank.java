package cn.vpfinance.vpjr.model;

public enum Bank {
	B1(1,"招商银行"),
	B2(2,"中国银行"),
	B3(3,"交通银行"),
	B4(4,"平安银行"),
	B5(5,"兴业银行"),
	B6(6,"光大银行"),
	B7(7,"华夏银行"),
	B8(8,"中信银行"),
	B9(9,"中国农业银行"),
	B10(10,"中国工商银行"),
	B11(11,"中国民生银行"),
	B12(12,"深圳发展银行"),
	B13(13,"广东发展银行"),
	B14(14,"中国建设银行"),
	B15(15,"上海浦东发展银行"),
	B16(16,"中国储蓄邮政银行"),
	B17(17,"其他银行");
	
	int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String name;
	
	Bank(int id,String name)
	{
		this.id = id;
		this.name = name;
	}
}
