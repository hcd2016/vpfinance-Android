package cn.vpfinance.vpjr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.vpfinance.vpjr.module.home.MainActivity;

public class GuideAdapter extends PagerAdapter{
	private Context mContext;
	private int[] mImageIds;
	private LayoutInflater mInflater;
	private View.OnClickListener mListener;
	
	public GuideAdapter(Context context, int[] imageIds, View.OnClickListener listener)
	{
		mContext = context;
		mImageIds = imageIds;
		mInflater = LayoutInflater.from(context);
		mListener = listener;
	}
	
	public void destroyItem(ViewGroup viewGroup, int index, Object item)
	{
		((ViewPager)viewGroup).removeView((View)item);
	}
	
	public void finishUpdate(ViewGroup viewGroup)
	{
		super.finishUpdate(viewGroup);
	}
	
	
	public int getCount()
	{
		if (mImageIds == null)
			return 0;
		return mImageIds.length;
	}
	
	
	public int getItemPosition(Object item)
	{
		return super.getItemPosition(item);
	}
	
	
	public Object instantiateItem(ViewGroup viewGroup, int index)
	{
//		if (index == mImageIds.length - 1)
//		{
//			View view = mInflater.inflate(R.layout.item_guide_last, null);
//			view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
//			((ImageView)view.findViewById(R.id.guide_last_page_content)).setImageResource(mImageIds[index]);
//			((ImageView)view.findViewById(R.id.use_now_btn)).setOnClickListener(mListener);
//			((ViewPager)viewGroup).addView(view);
//			return view;
//		}

		ImageView imageView = new ImageView(mContext);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		imageView.setImageResource(mImageIds[index]);
		if (index == mImageIds.length - 1){
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mContext.startActivity(new Intent(mContext,MainActivity.class));
					((Activity) mContext).finish();
				}
			});
		}
	    ((ViewPager)viewGroup).addView(imageView);
	    return imageView;
	}

	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}
}
