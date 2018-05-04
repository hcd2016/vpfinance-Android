package com.jewelcredit.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class LoanerModel extends BaseModel {
	
	
	public class LoanerAttr
	{
		public String name;
		public String value;
	};
	
	
	public LoanerAttr  newAttr()
	{
		return new LoanerAttr();
	}
	
	
	public List<LoanerAttr> attrs = new ArrayList<LoanerAttr>();
	
	public LoanerModel(Context context)
	{
		super(context);
	}
}
