package cn.vpfinance.vpjr.model;

import java.util.ArrayList;

import cn.vpfinance.vpjr.greendao.QueryPage;

public class QueryPageInfo {

	private double a_money;
	private double oneMonth;
	private double threeMonth;
	private double sixMonth;
	private ArrayList<QueryPage> list;

	public double getA_money() {
		return a_money;
	}

	public void setA_money(double a_money) {
		this.a_money = a_money;
	}

	public double getOneMonth() {
		return oneMonth;
	}

	public void setOneMonth(double oneMonth) {
		this.oneMonth = oneMonth;
	}

	public double getThreeMonth() {
		return threeMonth;
	}

	public void setThreeMonth(double threeMonth) {
		this.threeMonth = threeMonth;
	}

	public double getSixMonth() {
		return sixMonth;
	}

	public void setSixMonth(double sixMonth) {
		this.sixMonth = sixMonth;
	}

	public ArrayList<QueryPage> getList() {
		return list;
	}

	public void setList(ArrayList<QueryPage> list) {
		this.list = list;
	}

}
