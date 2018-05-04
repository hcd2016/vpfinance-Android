package com.tdk.control;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import cn.vpfinance.android.R;

//Pull to Refresh && Auto Load List View
public class PullToRefreshView extends LinearLayout {
	private static final String TAG = "PullToRefreshView";
	// pull state
	private static final int PULL_UP_STATE = 0;
	private static final int PULL_DOWN_STATE = 1;
	// refresh states
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	private static final int REFRESH_COMPLETE = 5;
	
	private static final float FRICTION = 0.4F;
	private static final int SCROLL_DURATION = 250;
	
	private int mLastMotionY;

	private Context mContext;
	private Scroller mScroller;
	private LayoutInflater mInflater;
	
	private ListView mListView;

	
	private View mHeaderView;
	private int mHeaderViewHeight;
	private ImageView mHeaderImageView;
	private TextView mHeaderTextView;
	private ProgressBar mHeaderProgressBar;
	private int mHeaderState;
	private OnHeaderRefreshListener mOnHeaderRefreshListener;
	
	private int mPullState;
	
	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;
	private String mLastUpdateTime;

	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public PullToRefreshView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	/**
	 * init
	 * 
	 * @description
	 */
	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		
		mScroller = new Scroller(mContext, new LinearInterpolator());
		mInflater = LayoutInflater.from(getContext());
		
		// Load all of the animations we need in code rather than through XML
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(SCROLL_DURATION);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(SCROLL_DURATION);
		mReverseFlipAnimation.setFillAfter(true);

		// header view 在此添加,保证是第一个添加到linearlayout的最上端
		addHeaderView();
	}

	private void addHeaderView() {
		// header view
		mHeaderView = mInflater.inflate(R.layout.refresh_header, this, false);

		mHeaderImageView = (ImageView) mHeaderView
				.findViewById(R.id.pull_to_refresh_image);
		mHeaderTextView = (TextView) mHeaderView
				.findViewById(R.id.pull_to_refresh_text);
		mHeaderProgressBar = (ProgressBar) mHeaderView
				.findViewById(R.id.pull_to_refresh_progress);
		// header layout
		measureView(mHeaderView);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mHeaderViewHeight);
		// 设置topMargin的值为负的header View高度,即将其隐藏在最上方
		params.topMargin = -(mHeaderViewHeight);
		// mHeaderView.setLayoutParams(params1);
		addView(mHeaderView, params);

	}


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initContentAdapterView();
	}

	
	/**
	 * init AdapterView like ListView,GridView and so on;or init ScrollView
	 * 
	 */
	private void initContentAdapterView() {
		int count = getChildCount();
		if (count < 2) {
			throw new IllegalArgumentException(
					"this layout must contain 2 child views,and AdapterView or ScrollView must in the second position!");
		}
		
		View view = null;
		for (int i = 0; i < count; ++i) {
			view = getChildAt(i);
			
			if (view instanceof AutoLoadListView) {
				mListView = (AutoLoadListView) view;
				break;
			}
			
//			if (view instanceof SwipeListView) {
//				mListView = (SwipeListView) view;
//				break;
//			}
		}
		
		if (mListView == null) {
			throw new IllegalArgumentException(
					"must contain a listView in this layout!");
		}
	}

	
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		
		child.measure(childWidthSpec, childHeightSpec);
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int y = (int) e.getRawY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 首先拦截down事件,记录y坐标
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// deltaY > 0 是向下运动,< 0是向上运动
			int deltaY = y - mLastMotionY;
			if (Math.abs(deltaY) >= 5 && isRefreshViewScroll(deltaY)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}
	

	/*
	 * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return
	 * false)则由PralRefreshView 的子View来处理;否则由下面的方法来处理(即由PralRefreshView自己来处理)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// onInterceptTouchEvent已经记录
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			if (mPullState == PULL_DOWN_STATE) {
				// PralRefreshView执行下拉
				Log.i(TAG, " pull down!parent view move!");
				headerPrepareToRefresh(deltaY);
				// setHeaderPadding(-mHeaderViewHeight);
			}
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			int scrollY = getScrollY();
			if (mPullState == PULL_DOWN_STATE) {
				if(scrollY <= -mHeaderViewHeight) {
					// 开始刷新
					headerRefreshing();
				} else {
					// 还没有执行刷新，重新隐藏
					resetScrollHeight();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 是否应该到了父View,即PralRefreshView滑动
	 * 
	 * @param deltaY
	 *            , deltaY > 0 是向下运动,< 0是向上运动
	 * @return
	 */
	private boolean isRefreshViewScroll(int deltaY) {
		if (mHeaderState == REFRESHING) {
			return false;
		}
		//对于ListView和GridView
		if (mListView != null) {
			// 子view(ListView or GridView)滑动到最顶端
			if (deltaY > 0) {

				View child = mListView.getChildAt(0);
				if (child == null) {
					// 如果mAdapterView中没有数据,不拦截
					return false;
				}
				if (mListView.getFirstVisiblePosition() == 0
						&& child.getTop() == 0) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}
				int top = child.getTop();
				int padding = mListView.getPaddingTop();
				if (mListView.getFirstVisiblePosition() == 0
						&& Math.abs(top - padding) <= 8) {//这里之前用3可以判断,但现在不行,还没找到原因
					mPullState = PULL_DOWN_STATE;
					return true;
				}

			}
		}
		
		return false;
	}

	
	private void changingScrollPosition(int deltaY)
	{
		scrollBy(0, -(int)(FRICTION * deltaY));
		if (getScrollY() > 0)
			scrollTo(0, getScrollY());
		invalidate();
	}
	
	private void resetScrollHeight()
	{
	    mHeaderState = REFRESH_COMPLETE;
	    int scrollY = getScrollY();
	    mScroller.startScroll(0, scrollY, 0, -scrollY, SCROLL_DURATION);
	    invalidate();
	}

	public void computeScroll()
	{
		if (mScroller.computeScrollOffset()) {
			int y = mScroller.getCurrY();
			scrollTo(0, y);
			
			if((mHeaderState == REFRESH_COMPLETE) &&  y == mScroller.getFinalY())
			{
				// reset init status
				mHeaderImageView.setVisibility(View.VISIBLE);
				mHeaderImageView.setImageResource(R.drawable.pulltorefresh_arrow);
				mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
				mHeaderProgressBar.setVisibility(View.GONE);
				mHeaderState = PULL_TO_REFRESH;
			}
			
			postInvalidate();  
		}
     
		super.computeScroll();
	}
	
	/**
	 * header 准备刷新,手指移动过程,还没有释放
	 * 
	 * @param deltaY
	 *            ,手指滑动的距离
	 */
	private void headerPrepareToRefresh(int deltaY) {
		changingScrollPosition(deltaY);
		if (-getScrollY() >= mHeaderViewHeight && mHeaderState != RELEASE_TO_REFRESH) {
			mHeaderTextView.setText(R.string.pull_to_refresh_release_label);
			mHeaderImageView.clearAnimation();
			mHeaderImageView.startAnimation(mFlipAnimation);
			mHeaderState = RELEASE_TO_REFRESH;
		} else if (-getScrollY() < mHeaderViewHeight && mHeaderState == RELEASE_TO_REFRESH) {// 拖动时没有释放
			mHeaderImageView.clearAnimation();
			mHeaderImageView.startAnimation(mReverseFlipAnimation);
			// mHeaderImageView.
			mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
			mHeaderState = PULL_TO_REFRESH;
		}
	}


	/**
	 * header refreshing
	 * 
	 */
	private void headerRefreshing() {
		mHeaderState = REFRESHING;
		int scrollY = getScrollY();
	    int offsetY = Math.abs(-mHeaderViewHeight - scrollY);
	    mScroller.startScroll(0, scrollY, 0, -mHeaderViewHeight - scrollY, offsetY * 100 / mHeaderViewHeight);
	    invalidate();
		mHeaderImageView.setVisibility(View.GONE);
		mHeaderImageView.clearAnimation();
		mHeaderImageView.setImageDrawable(null);
		mHeaderProgressBar.setVisibility(View.VISIBLE);
		mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label);
		if (mOnHeaderRefreshListener != null) {
			mOnHeaderRefreshListener.onHeaderRefresh(this);
		}
	}


	/**
	 * header view 完成更新后恢复初始状态
	 * 
	 */
	public void onHeaderRefreshComplete() {
		resetScrollHeight();
	}




	/**
	 * set headerRefreshListener
	 * 
	 * @description
	 * @param headerRefreshListener
	 */
	public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
		mOnHeaderRefreshListener = headerRefreshListener;
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid header
	 * view should be refreshed.
	 */
	public interface OnHeaderRefreshListener {
		public void onHeaderRefresh(PullToRefreshView view);
	}
	
	
	

}
