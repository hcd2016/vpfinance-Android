package cn.vpfinance.vpjr.module.welcome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jewelcredit.model.VersionModel;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.message.PushAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

public class WelcomeActivity extends BaseActivity {

	ImageView mSplashImage;
	private HttpService mHttpService;
	private VersionModel mVersionModel;
	DisplayImageOptions imageOptions;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		SharedPreferencesHelper.getInstance(this).removeKey(SharedPreferencesHelper.MINE_NO_SHOW_INFO);
		imageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.welcome_general_v1)
				.showImageForEmptyUri(R.drawable.welcome_general_v1)
				.showImageOnFail(R.drawable.welcome_general_v1)
				.cacheInMemory(false)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();

		Utils.getNetGeneration(this);

//		PushManager.getInstance().initialize(this.getApplicationContext(), null);
//		PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);

		Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		mSplashImage = (ImageView)findViewById(R.id.welcome_image);

		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.enable();
		//mPushAgent.disable();
		//mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);

		PushAgent.getInstance(this).onAppStart();

		mHttpService = new HttpService(this, this);
		mVersionModel = new VersionModel();

		mSplashImage.startAnimation(animation);

		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation paramAnonymousAnimation) {
//				mHttpService.checkAppVersion();
				goHome();
				return;
			}

			public void onAnimationRepeat(Animation paramAnonymousAnimation) {
			}

			public void onAnimationStart(Animation paramAnonymousAnimation) {
			}
		});

		mHttpService.guidePage();

	}

	private void goHome() {
		Intent intent = new Intent();
		if(AppState.instance().appUsed()) {
//		if(false) {
			intent.setClass(WelcomeActivity.this, MainActivity.class);
		} else {
			intent.setClass(WelcomeActivity.this, WelcomeGuideActivity.class);
		}
		startActivity(intent);
		finish();
	}

	public boolean onKeyDown(int code, KeyEvent event)
	{
		return false;
	}



	@Override
	public void onHttpSuccess(int req, JSONObject json) {
		if (!isHttpHandle(json)) return;
		if (req == ServiceCmd.CmdId.CMD_GUIDE_PAGE.ordinal()) {
			boolean success = json.optBoolean("success", false);
			//Log.e("Wel","success:" + success);
			if(success) {
				JSONArray banners = json.optJSONArray("banners");
				if(banners!=null && banners.length()>0){
					JSONObject ban = banners.optJSONObject(0);
					String imgUrl = ban.optString("imgurl");
					if(!TextUtils.isEmpty(imgUrl)) {
						ImageLoader.getInstance().displayImage(imgUrl, mSplashImage, imageOptions);
					}
				}
			}
		}
	}


	public void onHttpError(int reqId, String errmsg)
	{
		goHome();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){
			goHome();
		}
	}

}
