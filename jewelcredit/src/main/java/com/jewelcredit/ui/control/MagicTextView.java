package com.jewelcredit.ui.control;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

public class MagicTextView extends TextView {

	private enum Status
	{
		NORMAL,
		ANIMING
	}
	
	
	private static int ANIM_TIME = 0;
	private static final int REFRESH = 1;
	private static final int SCROLL = 2;
	DecimalFormat fnum = new DecimalFormat(",###.##");
	private double mCurValue;
	private double mGalValue;
	private double mRate;
	private double mValue;
	private int rate = 1;
	private Scroller scroller;
	private Status status = Status.NORMAL;
  
	
	public MagicTextView(Context context)
	{
		super(context);
	}

	public MagicTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.scroller = new Scroller(context, new LinearInterpolator());
	}
	
	public MagicTextView(Context context, AttributeSet attrs, int paramInt)
	{
		super(context, attrs, paramInt);
	}

	public void computeScroll()
	{
		if ((this.scroller.computeScrollOffset()) && (this.status == Status.ANIMING))
		{
			int i = this.scroller.getCurrX();
			setText(this.fnum.format(this.mGalValue / 1000.0D * i));
			if (i == this.scroller.getFinalX())
			{
				  this.scroller.abortAnimation();
				  this.status = Status.NORMAL;
			}
		}
		
		invalidate();
		getRootView().invalidate();
	}

	public void doScroll()
	{
		this.mGalValue = this.mValue;
		this.status = Status.ANIMING;
		this.scroller.startScroll(0, 0, 1000, 0, ANIM_TIME);
		invalidate();
		getRootView().invalidate();
	}

	public void setValue(double paramDouble)
	{
		this.mCurValue = 0.0D;
		this.mGalValue = paramDouble;
		this.mValue = paramDouble;
		this.mRate = (this.mValue / 20.0D);
		this.mRate = new BigDecimal(this.mRate).setScale(2, 4).doubleValue();
	}
}
