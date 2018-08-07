package com.jewelcredit.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jewelcredit.util.Utils;

public class UserBankModel extends BaseModel{
	public List<BankItem> records;
	private Context mContext;

	
	public UserBankModel(Context context)
	{
		super(context);
		mContext = context;
		records = new ArrayList<BankItem>();
	}
	
	public int getCount()
	{
		return records.size();
	}
	
	public BankItem getItem(int index)
	{
		{
			if(index < 0 || index >= records.size())
			{
				return null;
			}
			
			return records.get(index);
		}
		
	}
	
	
	public BankItem getItemByName(String name)
	{
		for(int i = 0; i < records.size(); i ++)
		{
			BankItem item = records.get(i);
			if(item.bankName.equals(name))
			{
				return item;
			}
		}
		
		return new BankItem();
	}

	public String[] getNames()
	{
		int size = records.size();
        String[] sArray = new String[size];
        
		for(int i = 0; i < records.size(); i ++)
		{
			sArray[i] = records.get(i).bankName;
		}
		
		return sArray;
	}
	
}
