package cn.vpfinance.vpjr.module.welcome;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.common.RegisterActivity;
import cn.vpfinance.vpjr.adapter.GuideAdapter;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.util.Logger;

public class WelcomeGuideActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{
	private int[] mImageIds = {R.drawable.guide01, R.drawable.guide02, R.drawable.guide03,R.drawable.guide04};

	private LinearLayout mPageIndicator;
	private ViewPager mViewPager;
	private Button registerNow;

	private Button loginNow;

	private TextView experNow;

	public static final int REQUEST_REGISTER = 1;
	public static final int REQUEST_LOGIN = 2;
	private Button goMain;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//控制底部虚拟键盘
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        //                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
			}
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.activity_welcome_guide);
		initView();

	}


	protected void initView() {

		mViewPager = ((ViewPager)findViewById(R.id.guide_viewpager));
		mPageIndicator = ((LinearLayout)findViewById(R.id.guide_pager_indicator));
		GuideAdapter adapter = new GuideAdapter(this, mImageIds, this);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(0);
//	    Utils.addIndicator(this, mPageIndicator, 0, mImageIds.length, R.drawable.indicator, R.drawable.indicator_sel);

		registerNow = viewById(R.id.register_now);
		loginNow = viewById(R.id.login_now);
		experNow = viewById(R.id.exper_now);

		registerNow.setOnClickListener(this);
		loginNow.setOnClickListener(this);
		experNow.setOnClickListener(this);

		goMain = ((Button) findViewById(R.id.go_main));
		goMain.setOnClickListener(this);
		goMain.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 	REQUEST_REGISTER && resultCode == RESULT_OK) {
			gotoActivityWithFinish(MainActivity.class);
		}

		if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
			gotoActivityWithFinish(MainActivity.class);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.exper_now:
			case R.id.go_main:
				gotoActivityWithFinish(MainActivity.class);
				break;

			case R.id.register_now: {
				Intent intent = new Intent(this, RegisterActivity.class);
				startActivityForResult(intent, REQUEST_REGISTER);
				break;
			}

			case R.id.login_now: {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, REQUEST_LOGIN);
				break;
			}
		}
	}

	public void onPageScrollStateChanged(int paramInt) {
	}

	public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
	}

	public void onPageSelected(int index) {
//		switch (index){
//			case 0:
//				goMain.setVisibility(View.GONE);
//				break;
//			case 1:
//				goMain.setVisibility(View.GONE);
//				break;
//			case 2:
//				goMain.setVisibility(View.VISIBLE);
//				break;
//		}
////		Utils.addIndicator(this, mPageIndicator, index, mImageIds.length, R.drawable.indicator, R.drawable.indicator_sel);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mImageIds = null;
	}
}
