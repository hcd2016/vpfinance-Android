package com.jewelcredit.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class LoanModel extends BaseModel{

	public boolean inHomePager = false;
	
	public int recNum = 10;
	public int pageNo = 1;
	public int totalCount = 0;
	public String totalAmount = "";
	
	public List<LoanItem> records = new ArrayList<LoanItem>();
	
	public String loanStatus = "";
	
	@Override 
	public String getKey()
	{
		return this.getClass().getName() + "-" + loanStatus;
	}
	
	public void setLoanStatus(String status)
	{
		loanStatus = status;
	}
	
	public LoanModel(Context context)
	{
		super(context);
	}
	
	public void setInHomePager(boolean flag)
	{
		inHomePager = flag;
	}
	
	public int getHomeBidCount()
	{
		if(!inHomePager)
			return 0;
		
		
		int count = records.size();
		return count > 3 ? 3 : count;
	}
	
	
	public int getPageCount()
	{
		int pageNum = totalCount / recNum;
		int left = totalCount % recNum;
		
		if(left > 0)
			pageNum ++;
		
		
		if(pageNo < pageNum)
			return recNum;
		
		return left;
	}
	
}
