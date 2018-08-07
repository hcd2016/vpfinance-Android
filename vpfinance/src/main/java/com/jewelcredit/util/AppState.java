package com.jewelcredit.util;

import android.content.Context;
import android.text.TextUtils;

import com.jewelcredit.model.AuthenModel;
import com.jewelcredit.model.LoginUserInfo;

import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

public class AppState {
	
	private static CacheDB mDB;
	private Context mContext;
	private static AppState self;

//	private String mSesnCode = "";
	private final String mDummySesnCode = "DUMMY_SESSION_ID";

	private LoginUserInfo userInfo = new LoginUserInfo();
	private AuthenModel authenInfo = null;
	
	private AppState()
	{
		if(mDB == null)
		{
			mDB = new CacheDB();
		}
	}
	
	
	public LoginUserInfo getLoginUserInfo()
	{
		return userInfo;
	}
	
	
	public void clearLoginUserInfo()
	{
		userInfo = new LoginUserInfo();
		genUserKey();
	}

	
	public AuthenModel getAuthenInfo()
	{
		return authenInfo;
	}
	
	public void setAuthenInfo(AuthenModel info)
	{
		authenInfo = info;
	}
	
	public static AppState instance()
	{
		if(self == null)
		{
			self = new AppState();
		}
		
		return self;
	}
	
	
	public String genUserKey()
	{
		/*
		 * CI_KEY = ""+(100000000000+parseInt(Math.random(0,1)*10000000000));
		 */
		
		double seed = 1000000000;
		seed += (Math.random() * seed);

		long seed2 = (long)seed;
		String userKey = "10" + "" + seed2;
		
		if(userInfo.userKey.equals(""))
			userInfo.userKey = userKey;
		
		return userKey;
	}
	
	
	public void init(Context context)
	{
		mContext = context;
		mDB.initialize(mContext);
	}
	
	
	public boolean appUsed()
	{
		String key = "appUsed";
		String value = getStringByKey(mContext, key);
		saveStringByKey(mContext, key, "1");
		return value.equals("1");

	}

	
	public String getSessionCode()
	{
		String s = SharedPreferencesHelper.getInstance(mContext).getStringValue(SharedPreferencesHelper.mSesnCode);
		return TextUtils.isEmpty(s) ? "" : s;
//		return mSesnCode;
	}
	
	
	public void setSessionCode(String sesncode)
	{
		SharedPreferencesHelper.getInstance(mContext).putStringValue(SharedPreferencesHelper.mSesnCode,sesncode);
//		mSesnCode = sesncode;
	}
	
	
	
	public boolean logined()
	{
		String mSesnCode = getSessionCode();
		return !mSesnCode.equals("") && !mSesnCode.equals(mDummySesnCode);
	}
	
	
	public void setLoginUserName(String content)
	{
		String key = "LoginUserName";
		saveStringByKey(mContext, key, content);
	}
	
	
	public String getLoginUserName()
	{
		String key = "LoginUserName";
		return getStringByKey(mContext, key);
	}
	
	public void setSelectedShortcuts(String content)
	{
		String key = "SelectedShortcuts";
		saveStringByKey(mContext, key, content);
	}
	
	
	public String getSelectedShortcuts()
	{
		String key = "SelectedShortcuts";
		return getStringByKey(mContext, key);
	}
	
	
	public void logout()
	{
		clearLoginUserInfo();
		setAuthenInfo(null);
		setSessionCode(mDummySesnCode);
	}
	
	
	

	public static void saveStringByKey(Context context, String key, String value)
	{
		if(!key.equals(""))
		{
			mDB.setValue(key, value);
		}
	}
	
	
	public static String getStringByKey(Context context, String key)
	{
		if(!key.equals(""))
		{
			return mDB.getValue(key);
		}
		
	    return "";
	}
	
	
	
}

