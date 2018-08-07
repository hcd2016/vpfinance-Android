package com.jewelcredit.ui.control;

public class ShortcutGridItem {
	/*
	private Context context;
	private ImageView delImg = null;
	private boolean isRegisted = false;
	private boolean isShakeItem = false;
	private ShakeItemBroadcastReceiver receiver;
	
	
	
	public ShortcutGridItem(Context context, int paramInt1, int paramInt2, boolean paramBoolean)
	{
		super(context);
	    this.context = context;
	    this.isShakeItem = paramBoolean;
	    this.dataMap = new ShortcutMenuDataMap(paramContext);
	    createShakeItem(paramBoolean, paramInt1, paramInt2);
	}
	
	
	private void createShakeItem(boolean paramBoolean, int id, int paramInt2)
	{
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
	    localLayoutParams1.addRule(10);
	    setId(id);
	    ImageView localImageView = new ImageView(this.context);
	    localImageView.setId(2131427343);
	    localImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
	    localImageView.setImageResource(this.dataMap.getItemImgId(id));
	    localImageView.setLayoutParams(localLayoutParams1);
	    addView(localImageView);
	    if (paramBoolean)
	    {
	    	IntentFilter localIntentFilter = new IntentFilter();
	    	localIntentFilter.addAction("action_shake_to_edit");
	    	localIntentFilter.addAction("action_stop_shake");
	    	localIntentFilter.addAction("action_unregister_broadcast");
	    	registeMyReciver(localIntentFilter);
	      
	    	RelativeLayout.LayoutParams localLayoutParams2 = new RelativeLayout.LayoutParams(Math.round(paramInt2 * 44 / 100.0F), Math.round(paramInt2 * 44 / 100.0F));
	    	localLayoutParams2.addRule(6, this.dataMap.getItemImgId(paramInt1));
	    	this.delImg = new ImageView(this.context);
	    	this.delImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
	    	this.delImg.setImageResource(2130837546);
	    	this.delImg.setLayoutParams(localLayoutParams2);
	    	this.delImg.setVisibility(8);
	    	addView(this.delImg);
	    }
	    
	    TextView localTextView = new TextView(this.context);
	    localTextView.setText(getResources().getString(this.dataMap.getItemTxtId(paramInt1)));
	    RelativeLayout.LayoutParams localLayoutParams3 = new RelativeLayout.LayoutParams(-1, -2);
	    localLayoutParams3.addRule(12);
	    localTextView.setLayoutParams(localLayoutParams3);
	    localTextView.setGravity(17);
	    localTextView.setTextSize(12.0F);
	    localTextView.setTextColor(getResources().getColor(17170444));
	    addView(localTextView);
	}
	*/
}
