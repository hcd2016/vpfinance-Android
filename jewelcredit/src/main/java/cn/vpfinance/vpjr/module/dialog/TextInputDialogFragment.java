/*
 * Copyright (c) 2014 The MaMaHelp_small_withReceiver_6.0.0 Project,
 *
 * 深圳市新网智创科技有限公司. All Rights Reserved.
 */

package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.vpfinance.android.R;

/**
 * @Function: 文本输入弹出框
 * @author 张清田
 * @version
 * @Date: 2014年11月18日 下午6:38:24
 */
public class TextInputDialogFragment extends DialogFragment implements View.OnClickListener{
	
	private static final String KEY_TITLE = "title";
	private static final String KEY_TIP = "tip";
	
	private EditText inputEt;
//	private TextView errorTip;

	private onTextConfrimListener mListener;
	private onTextConfrimListener mCancleListener;
	
	public void setOnTextConfrimListener(onTextConfrimListener listener) {
		this.mListener = listener;
	}

	public void setOnTextCancleListener(onTextConfrimListener listener) {
		this.mCancleListener = listener;
	}

	public static TextInputDialogFragment newInstance(String title, String tip,boolean cancelabl)
	{
		TextInputDialogFragment pf = new TextInputDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(KEY_TITLE, title);
		if (tip != null) {
			bundle.putString(KEY_TIP, tip);
		}
		pf.setArguments(bundle);
		pf.setCancelable(cancelabl);
		return pf;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout to use as dialog or embedded fragment
		View view = inflater.inflate(R.layout.fragment_input_text, container, false);
		
		TextView dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
		
//		TextView tipTv = (TextView) view.findViewById(R.id.tipTv);
		inputEt = (EditText) view.findViewById(R.id.inputEt);
//		errorTip = (TextView) view.findViewById(R.id.errorTip);
		
		inputEt.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
//				if(errorTip!=null)
//				{
//					errorTip.setVisibility(View.GONE);
//				}
			}
		});
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			String title = bundle.getString(KEY_TITLE);
			String tip = bundle.getString(KEY_TIP);
			dialogTitle.setText(title);
//			tipTv.setText(tip);
			inputEt.setHint(tip);
		}
		view.findViewById(R.id.btnCancel).setOnClickListener(this);
		view.findViewById(R.id.btnOK).setOnClickListener(this);
//		view.findViewById(R.id.closeIv).setOnClickListener(this);
        return view;
	}
	
//	public void setErrorTip(String tip)
//	{
//		if(errorTip!=null && isAdded())
//		{
//			errorTip.setVisibility(View.VISIBLE);
//			errorTip.setText(tip);
//		}
//	}
	
	@Override
	public void onClick(View view)
	{
		String value = inputEt.getText().toString();
		switch (view.getId())
		{
			case R.id.btnOK:
				if (TextUtils.isEmpty(value))
				{
					Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mListener != null)
				{
					if (!mListener.onTextConfrim(value))
					{
						return;
					}
				}
				dismiss();
				break;
			case R.id.btnCancel:
				if (mCancleListener != null)
				{
					if (!mCancleListener.onTextConfrim(value))
					{
						return;
					}
				}
				dismiss();
//			case R.id.closeIv:
				dismiss();
				break;
			default:
				break;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
			
	}


	public static interface onTextConfrimListener {
		/**
		 * 
		 * @description 返回true对话框消失不调用回调方法
		 * @update 2014年12月25日 上午10:42:00
		 */
		public boolean onTextConfrim(String value);
	}

}

	