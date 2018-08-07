package com.tdk.control;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.vpfinance.android.R;


public class ProgressDialog extends Dialog
{
	public ProgressDialog(Context context)
	{
		super(context, R.style.progressdialog);
		setContentView(R.layout.progress_dialog);
		Window win = getWindow();
		WindowManager.LayoutParams params = win.getAttributes();
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		win.setAttributes(params);
	}

  
	public ProgressDialog(Context context, int theme)
	{
		super(context, theme);
	}

	public ProgressDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener)	
	{
		super(context, cancelable, cancelListener);
	}
	
	public void setTips(String text)
	{
		TextView view = (TextView)this.findViewById(R.id.progress_tips);
		if(view != null)
			view.setText(text);
	}

}

