package com.jewelcredit.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class ScoreModel extends BaseModel {
	public int recNum = 0;
	public int pageNo = 0;
	public int totalCount = 0;
	public String totalScore = "";
	
	public List<ScoreItem> records = new ArrayList<ScoreItem>();
	
	public ScoreModel(Context context)
	{
		super(context);
	}
}

