package com.jewelcredit.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.Utils;

public class BaseModel {

	public Context mContext = null;
	
	public BaseModel(Context context)
	{
		mContext = context;
	}
	
	public String getKey()
	{
		return this.getClass().getName();
	}
	
	
	public void saveToCache(JSONObject json)
	{
		String data = json.toString();
		String key = getKey();
		
		AppState.saveStringByKey(mContext, key, data);
	}
	
	
	public JSONObject loadFromCache()
	{
		String key = getKey();
		String data = AppState.getStringByKey(mContext, key);
		JSONObject json = null;
				
		try
		{
			json = new JSONObject(data);
		}
		catch(JSONException ex)
		{
			json = null;
		}

		return json;
	}
}

