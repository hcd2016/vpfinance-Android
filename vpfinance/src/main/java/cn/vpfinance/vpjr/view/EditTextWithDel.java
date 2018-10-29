package cn.vpfinance.vpjr.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import cn.vpfinance.android.R;


public class EditTextWithDel extends EditText {
	private final static String TAG = "EditTextWithDel";
	private Drawable imgAble;
	private Context  mContext;
	private boolean  focused = false;
	private int mMaxLength;
	private int defMaxLength = 20;

	public EditTextWithDel(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public EditTextWithDel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public EditTextWithDel(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EditTextWithDel, 0, 0);
		mMaxLength = typedArray.getInteger(R.styleable.EditTextWithDel_maxLength, defMaxLength);
		typedArray.recycle();
		init();
	}
	
	private void init() {
		imgAble = mContext.getResources().getDrawable(R.drawable.ic_close_circle);
		setPadding(getPaddingLeft(),getPaddingTop(),10,getPaddingBottom());
		addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				setDrawable();
			}
		});
		setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});
		setDrawable();
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		this.focused = focused;
		Drawable[] compoundDrawables = getCompoundDrawables();
		if (!focused){
			setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], new BitmapDrawable(), compoundDrawables[3]);
		}else{
			if (length() > 1)
				setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], imgAble, compoundDrawables[3]);
		}
	}


	private void setDrawable() {
		Drawable[] compoundDrawables = getCompoundDrawables();
		if(length() < 1 ) {
			setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], new BitmapDrawable(), compoundDrawables[3]);
		} else {
			setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], imgAble, compoundDrawables[3]);
		}
		setCompoundDrawablePadding(getCompoundDrawablePadding());
		setPadding(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom());
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
//            Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);

			int[] location = new int[2];
			getLocationOnScreen(location);//获取在当前屏幕内的绝对坐标
			int width = getWidth();
			int height = getHeight();
			int x = location[0];
			int y = location[1];
//			Log.e(TAG,"width:"+width+",hight:"+height+",x:"+x+",y:"+y);
			if (eventX < (x+width) && eventX > (x+width-100) && eventY > y && eventY < (eventY+height)){
				setText("");
			}
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}
