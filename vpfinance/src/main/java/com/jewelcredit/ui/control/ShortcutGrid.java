package com.jewelcredit.ui.control;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class ShortcutGrid extends RelativeLayout{

	private Context mContext;
	
	public enum EditStatus
	{
		NORMAL,
		EDIT,
		ANIMNATE_ADD,
		ANIMNATE_DEL
	}
	
	
	
	public ShortcutGrid(Context context)
	{
		super(context);
		mContext = context;
	}
	
	public ShortcutGrid(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
	}
	
	
	
	private void sendBroadCast(String name)
	{
		Intent intent = new Intent();
		intent.setAction(name);
		mContext.sendBroadcast(intent);
	}
	
	
	
	public void onClick(View view)
	{
		return ;
	}
	
	
	public boolean onLongClick(View view)
	{
		return false;
	}
	
	
}
