package com.tdk.control;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import cn.vpfinance.android.R;



// autoload list view
public class AutoLoadListView extends ListView implements AbsListView.OnScrollListener, View.OnClickListener{
	
	public enum Status{
		NORMAL,
		LOADING,
		LOADCOMPLETE,
		NETWORK_DISABLE
	}
	
	
	public enum FooterViewStatus{
		HIDE_FOOTERVIEW,
		SHOW_FOOTERVIEW,
		SHOW_NODATA_FOOTERVIEW
	}
	
	
	private Context mContext;
	private Button mFooterBtn;
	private View mFooterView;
	private View mFooterView2;	// 提示没数据了
	private OnFooterLoadingListener onFooterLoadingListener = null;
	private Status mStatus = Status.NORMAL;
	private RelativeLayout mTipMain;
	private View mActiveFooterView = null;
	
	public AutoLoadListView(Context context)
	{
	    super(context);
	    mContext = context;
	    setOnScrollListener(this);
	}
	
	
	public AutoLoadListView(Context context, AttributeSet attrs)
	{
	    super(context, attrs);
	    mContext = context;
	    setOnScrollListener(this);
	    mFooterView = LayoutInflater.from(context).inflate(R.layout.refresh_footer, null);
	    mFooterView2 = LayoutInflater.from(context).inflate(R.layout.nodata_footer, null);
	    mTipMain = ((RelativeLayout)mFooterView.findViewById(R.id.footer_view_tip_main));
	    mFooterBtn = ((Button)mFooterView.findViewById(R.id.footer_view_btn));
	    mFooterBtn.setOnClickListener(this);
	}
	
	
	public void setAdapter2(BaseAdapter adapter)
	{
		addFooterView(mFooterView);
		setAdapter(adapter);
		removeFooterView(mFooterView);
	}
	
	private void doRefresh()
	{
		if (onFooterLoadingListener != null)
	    {
			mStatus = Status.LOADING;
			onFooterLoadingListener.onFooterLoading(this);
	    }
	}
	
	
	
	public void resetTips()
	{
		mFooterBtn.setVisibility(View.GONE);
	    mTipMain.setVisibility(View.VISIBLE);
	    mStatus = Status.NORMAL;
	}
	
	
	public void onClick(View child)
	{
		if(child.getId() == R.id.footer_view_btn)
		{
			resetTips();
		    doRefresh();
		}
	}
	
	
	public void onFooterLoadComplete(int totalCount)
	{

		int i = 10;
		deleteFooterView();	// 免得存在footerView使count不一样
		
		// load completed
		if(totalCount <= i || getCount() >= totalCount)
		{
			mStatus = Status.LOADCOMPLETE;
			
			//if(getFooterViewsCount() > 0)
			//	removeFooterView(mFooterView);
			showFooterView(2);
			return;
		}
		
		// not load completed
		if(totalCount > i)
		{
			mStatus = Status.NORMAL;
			
			//if(getFooterViewsCount() <= 0)
			//	addFooterView(mFooterView);
			showFooterView(1);
			return;
		}
	}
	
	public void onLoadFailure()
	{
	    mFooterBtn.setVisibility(View.VISIBLE);
	    mTipMain.setVisibility(View.GONE);
	    mStatus = Status.NETWORK_DISABLE;
	}
	
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{

	    if ( mStatus == Status.NORMAL && (totalItemCount != 0) && (firstVisibleItem + visibleItemCount == totalItemCount))
	    {
	    	doRefresh();
	    }
	    
	    /*
	    if((mStatus != Status.LOADCOMPLETE) || (firstVisibleItem + visibleItemCount) != totalItemCount)
	    {
	    	return;
	    }
	    */
	        	
//    	if(totalItemCount > 0)
//    		Toast.makeText(this.mContext, R.string.no_more_data_tips, 1).show();
	}
	
	
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
	}
	
	
	private void deleteFooterView()
	{
		if(mActiveFooterView != null)
		{
			removeFooterView(mActiveFooterView);
		}
		
		mActiveFooterView = null;
	}
	
	
	private void appendFooterView(View v)
	{
		if(v != mActiveFooterView)
		{
			deleteFooterView();
		}
		
		
		if(getFooterViewsCount() <= 0)
		{
			addFooterView(v);
			mActiveFooterView = v;
		}
	}
	
	
	/*
	 * type: 0， 未加载完，隐藏；	1, 未加载完，显示;	2，加载完了，显示无数据
	 */
	public void showFooterView(int type)
	{
		switch(type)
		{
		case 0:
			deleteFooterView();
			break;
			
		case 1:
			appendFooterView(mFooterView);
			break;
			
		case 2:
			appendFooterView(mFooterView2);
			break;
		}
	}
	
	
	
	public void setOnFooterLoadingListener(OnFooterLoadingListener listener)
	{
		onFooterLoadingListener = listener;
	}
	
	
	public abstract interface OnFooterLoadingListener
	{
		public abstract void onFooterLoading(AutoLoadListView view);
	}
	
	
}
