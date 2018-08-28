package cn.vpfinance.vpjr.module.welcome;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.message.PushAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

public class WelcomeActivity extends BaseActivity {

    ImageView mSplashImage;
    @Bind(R.id.welcome_image)
    ImageView welcomeImage;
    private HttpService mHttpService;
    DisplayImageOptions imageOptions;
    private TextView btnGoHome;
    private ImageButton btnGoEvent;

    public static final String WelcomeEventUrl = "https://www.vpfinance.cn/AppContent/content?topicId=1238";
//    private CountDownTimer countDownTimer;
//    public boolean isCountDownTimerRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        btnGoHome = ((TextView) findViewById(R.id.btnGoHome));
        btnGoEvent = ((ImageButton) findViewById(R.id.btnGoEvent));

        SharedPreferencesHelper.getInstance(this).removeKey(SharedPreferencesHelper.MINE_NO_SHOW_INFO);
        imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.welcome_general)
                .showImageForEmptyUri(R.drawable.welcome_general)
                .showImageOnFail(R.drawable.welcome_general)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        Utils.getNetGeneration(this);

//		PushManager.getInstance().initialize(this.getApplicationContext(), null);
//		PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mSplashImage = (ImageView) findViewById(R.id.welcome_image);

        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        //mPushAgent.disable();
        //mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);

        PushAgent.getInstance(this).onAppStart();

        mHttpService = new HttpService(this, this);

        mSplashImage.startAnimation(animation);

//        countDownTimer = new CountDownTimer(4000, 1000) {
//            @Override
//            public void onTick(long l) {
////						Logger.e("l: "+l);
//                if (l == 4000) {
//                    l = 3999;
//                } else if (l == 3000) {
//                    l = 2999;
//                } else if (l == 2000) {
//                    l = 1999;
//                }
//                btnGoHome.setText("跳过 " + (l / 1000));
//                Log.i("CountDownTimerTest", "onTick millisUntilFinished = " + l);
////            }
//
//            @Override
//            public void onFinish() {
//                isCountDownTimerRunning = false;
//                goHome();
//            }
//        };

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation paramAnonymousAnimation) {
//                if (!isCountDownTimerRunning) {
//                    countDownTimer.start();
//                    isCountDownTimerRunning = true;
//                }
                goHome();
            }

            public void onAnimationRepeat(Animation paramAnonymousAnimation) {
            }

            public void onAnimationStart(Animation paramAnonymousAnimation) {
            }
        });
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });
        btnGoEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                countDownTimer.cancel();
                gotoWeb(WelcomeEventUrl, "");
                finish();
            }
        });
        mHttpService.guidePage();
    }

    private void goHome() {
        Intent intent = new Intent();
        if (AppState.instance().appUsed()) {
            intent.setClass(WelcomeActivity.this, MainActivity.class);
        } else {
            intent.setClass(WelcomeActivity.this, WelcomeGuideActivity.class);
        }
        startActivity(intent);
        finish();
    }

    public boolean onKeyDown(int code, KeyEvent event) {
        return false;
    }


    @Override
    public void onHttpSuccess(int req, JSONObject json) {
//		if (!isHttpHandle(json)) return;
        if (req == ServiceCmd.CmdId.CMD_GUIDE_PAGE.ordinal()) {
            boolean success = json.optBoolean("success", false);
            //Log.e("Wel","success:" + success);
            if (success) {
                btnGoEvent.setVisibility(View.VISIBLE);
                JSONArray banners = json.optJSONArray("banners");
                if (banners != null && banners.length() > 0) {
                    JSONObject ban = banners.optJSONObject(0);
                    String imgUrl = ban.optString("imgurl");
                    if (!TextUtils.isEmpty(imgUrl)) {
                        ImageLoader.getInstance().displayImage(imgUrl, mSplashImage, imageOptions);
                    }
                }
            } else {
                btnGoEvent.setVisibility(View.GONE);
            }
        }
    }

    public void onHttpError(int reqId, String errmsg) {
        goHome();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            goHome();
        }
    }

}
