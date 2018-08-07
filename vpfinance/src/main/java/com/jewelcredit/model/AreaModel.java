package com.jewelcredit.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class AreaModel extends BaseModel {
	public int recNum = 0;
	public int pageNo = 0;
	public int totalCount = 0;
	
	public List<AreaItem> records = new ArrayList<AreaItem>();
	public AreaModel(Context context)
	{
		super(context);
	}
}
