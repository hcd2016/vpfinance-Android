package com.jewelcredit.model;

import android.content.Context;

public class AuthenModel extends BaseModel {

	public String idType = "";
	public String realName = "";
	public String idNo = "";
	public String authStatus = "";
	public String provCd = "";
	public String cityCd = "";
	public String zoneCd = "";
	public String addr = "";
	
	
	public AuthenModel(Context context)
	{
		super(context);
	}
	
}
