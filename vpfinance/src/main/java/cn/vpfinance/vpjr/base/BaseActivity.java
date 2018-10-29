package cn.vpfinance.vpjr.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.jewelcredit.util.KeybordUtil;
import com.jewelcredit.util.TitleBar;
import com.jewelcredit.util.Utils;
import com.tdk.utils.HttpDownloader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.gusturelock.LockActivity;
import cn.vpfinance.vpjr.module.welcome.WelcomeActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.util.StatusBarCompat1;

public class BaseActivity extends FragmentActivity implements HttpDownloader.HttpDownloaderListener{
	protected TitleBar mTitleBar;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		StatusBarCompat1.translucentStatusBar(this);
	}

	protected  <T extends View> T viewById(int resId) {
		return (T) super.findViewById(resId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		//更新时间
//		SharedPreferencesHelper.getInstance(this).putLongValue(SharedPreferencesHelper.KEY_LAST_PAUSE_TIME, System.currentTimeMillis());
	}

	protected void initView(){}
	protected void loadDate(){}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
		String lockPattenString = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING, null);
		if(!(this instanceof WelcomeActivity))
		{
			if (!TextUtils.isEmpty(lockPattenString)) {
				long lastPauseTime = preferencesHelper.getLongValue(SharedPreferencesHelper.KEY_LAST_PAUSE_TIME, System.currentTimeMillis());
//				Logger.e("System.currentTimeMillis() - lastPauseTime: "+(System.currentTimeMillis() - lastPauseTime));
				if(System.currentTimeMillis() - lastPauseTime > LockActivity.TIME_LOCK)
				{
					Intent intent = new Intent(this, LockActivity.class);
//					intent.putExtra(LockActivity.NAME_AUTO_LOGIN, true);
					startActivity(intent);
				}
			}
		}

		MobclickAgent.onResume(this);
	}

	protected void onDestroy()
	{
		HttpDownloader.mClient.cancelRequests(this, true);
		super.onDestroy();
//		ImageLoader.getInstance().stop();
//		if(KeybordUtil.isSoftInputShow(this)) {
//			KeybordUtil.closeKeybord(this.getCurrentFocus(),this);
//		}
	}
	
    public void onHttpSuccess(int reqId, JSONObject json) {
	}
    
    
    public void onHttpCache(int reqId)
    {
    }

    protected boolean isHttpHandle(JSONObject json){
		if (json == null || isFinishing() || Common.isForceLogout(this,json))	return false;
		return true;
	}

//	private static boolean isToast = false;
    public void onHttpError(int reqId, String errmsg)
    {
    }
    
    public void gotoActivity(Class<? extends Activity> activityClass)
	{
		startActivity(new Intent(this, activityClass));
		this.overridePendingTransition(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left);
	}

	public void gotoActivity(Intent intent)
	{
		startActivity(intent);
		this.overridePendingTransition(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left);
	}
    
    public void gotoActivityWithFinish(Class<? extends Activity> activityClass)
	{
		startActivity(new Intent(this, activityClass));
		overridePendingTransition(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left);
		finish();
	}
    
    public void gotoWeb(String url, String title)
    {
    	Utils.goToWeb(this, url, title);
    }


	@Override
	public void onHttpSuccess(int reqId, JSONArray json) {
	}
}

