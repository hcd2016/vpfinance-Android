package com.jewelcredit.util;

import android.app.Activity;
import android.graphics.Matrix;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.vpfinance.android.R;

public class TitleBar {
	
	private Activity mContext;
	private View mContainerView;
	private OnClickListener mCloseListener;
	
	public TitleBar(Activity context)
	{
		mContext = context;
	}
	
	public void setContainerView(View view)
	{
		mContainerView = view;
	}
	
	
	public void init(String title)
    {
		setTitle(title);

		mCloseListener = new OnClickListener(){
			public void onClick(View v)
			{
				if(mContext.getClass().getName().contains("LoginActivity"))
				{
					//清理login present标志，防止多弹login
					HttpService.clearPresentLoginFlag();
				}
				
				mContext.finish();
			}
		};

		
		
    	View view = findSubView(R.id.titlebar_back_btn);    	
    	if(view != null)
    	{
    		view.setOnClickListener(mCloseListener);
    		view.setVisibility(View.GONE);
    	}
    	
    	view = findSubView(R.id.titlebar_close_btn);
    	if(view != null)
    	{
    		view.setOnClickListener(mCloseListener);
    		view.setVisibility(View.GONE);
    	}
    	
    	view = findSubView(R.id.titlebar_textbtn);
    	if(view != null)
    	{
			view.setVisibility(View.GONE);
    	}
    }
    
	
	public void setTitle(String title)
	{
		TextView textView = (TextView)findSubView(R.id.titlebar_title);
    	if(textView != null)
    	{
			textView.setText(title);
    	}
	}
    
    public View enableBack(boolean flag)
    {
    	View view = findSubView(R.id.titlebar_back_btn);
    	if(view != null)
    	{
    		view.setVisibility(flag ? View.VISIBLE : View.GONE);
    	}
    	
    	return view;
    }
    
    
    public View enableClose(boolean flag)
    {
    	View view = findSubView(R.id.titlebar_close_btn);
    	if(view != null)
    	{
    		view.setVisibility(flag ? View.VISIBLE : View.GONE);
    	}

    	view = findSubView(R.id.titlebar_textbtn);
    	if(view != null)
    	{
    		view.setVisibility(View.GONE);
    	}
    	
    	return view;
    }
    
    
    
    public View enableTextButton(boolean flag)
    {
    	View view = findSubView(R.id.titlebar_textbtn);
    	if(view != null)
    	{
    		view.setVisibility(flag ? View.VISIBLE : View.GONE);
    	}
    	
    	View view2 = findSubView(R.id.titlebar_close_btn);
    	if(view2 != null)
    	{
    		view2.setVisibility(View.GONE);
    	}
    	
    	return view;
    }
    
    
    
    private View findSubView(int id)
    {
    	View view = null;
    	
    	if(mContext != null)
    	{
    		view = mContext.findViewById(id);
    	}
    	
    	if(view == null)
    	{
    		if(mContainerView != null)
    		{
    			view = mContainerView.findViewById(id);
    		}
    	}
    	
    	return view;
    }
}
