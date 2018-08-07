package com.jewelcredit.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class BidRecordsModel extends BaseModel{
	public int recNum = 0;
	public int pageNo = 0;
	public int totalCount = 0;
	
	public List<BidRecord> records = new ArrayList<BidRecord>();
	
	public BidRecordsModel(Context context)
	{
		super(context);
	}

}

