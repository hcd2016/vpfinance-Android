package com.jewelcredit.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class RechargeModel extends BaseModel {
	public int recNum = 0;
	public int pageNo = 0;
	public int totalCount = 0;
	
	public List<RechargeInfo> records = new ArrayList<RechargeInfo>();
	
	public RechargeModel(Context context)
	{
		super(context);
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
