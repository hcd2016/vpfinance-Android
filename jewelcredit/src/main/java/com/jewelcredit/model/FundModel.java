package com.jewelcredit.model;

import android.content.Context;

public class FundModel extends BaseModel {
	public String userNo = "";
	public String sumMoney = "";
	public String balanace = "";
	public String freezeAmount = "";
	public String payerTotalPayment = "";
	public String totalPayment = "";
	public String investment = "";
	
	public FundModel(Context context)
	{
		super(context);
	}
}
