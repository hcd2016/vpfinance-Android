package cn.vpfinance.vpjr.adapter;

import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


public class ViewPagerAdapter extends PagerAdapter{
	private Context mContext;
	private int mCurrentItem;
	private List<Integer>	mIds;
	
	public ViewPagerAdapter(List<Integer> ids, Context context)
	{
		mIds = ids;
		mContext = context;
	}
	
	
	public void destroyItem(ViewGroup viewGroup, int index, Object object)
	{
	}
	
	
	public void finishUpdate(ViewGroup viewGroup)
	{
		super.finishUpdate(viewGroup);
	}

	public int getCount()
	{
		return mIds.size();
	}
	
	
	public int getItemPosition(Object object)
	{
		return super.getItemPosition(object);
	}
	
	
	
	public Object instantiateItem(ViewGroup viewGroup, int index)
	{

		int i = index % mIds.size();
	    View view = viewGroup.findViewById(index);
	    if (view == null)
	    {
	    	view = View.inflate(mContext, mIds.get(i), null);
	    	view.setId(index);
	    	((ViewPager)viewGroup).addView(view);
	    }
	    
	    
	    return view;
	}
	
	
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}


	public Parcelable saveState()
	{
		return null;
	}

	public void setCurrentItem(int index)
	{
		mCurrentItem = index;
	}
}
