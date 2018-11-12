package cn.vpfinance.vpjr;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;

import com.jewelcredit.ui.widget.BaseRefreshHeaderView;
import com.jewelcredit.util.AppState;
import com.mob.MobSDK;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.ImageLoaderWithCookie;

public class App extends Application {
    public String currentPid;
    public boolean isCheckUpdate = true;
    public String isBindBank = "1";
    public boolean isOpenHx = false;
    public int saveMineTabSelected = 0;

    public long differTime = 0L;
    public int currentListTabType = Constant.TYPE_REGULAR;
    public boolean isPersonType = true;
//    public static final String SHOW_SETUP_GUIDE = "show_setup_guide";
    /**
     * 是否进入向导设置
     */
//    public Map<String, Boolean> guideConfig = new HashMap<>();
    public boolean isFirstRegieter = false;
    public boolean isLogin = false;
    public boolean isNeedUpdatePwd = false;

    private static App mAppContext;
    private static Context appContext;
    public boolean login = false;
    // IWXAPI是第三方app和微信通信的接口
    public IWXAPI mWxApi;

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white);//全局设置主题颜色
                return new BaseRefreshHeaderView(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.gray_bg, R.color.white);//全局设置主题颜色
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    class FishActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            pushActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {
            SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(activity);
            preferencesHelper.putLongValue(SharedPreferencesHelper.KEY_LAST_PAUSE_TIME, System.currentTimeMillis());
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (null == mActivitys && mActivitys.isEmpty()) {
                return;
            }
            if (mActivitys.contains(activity)) {
                /**
                 *  监听到 Activity销毁事件 将该Activity 从list中移除
                 */
                popActivity(activity);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = this;
        appContext = getApplicationContext();

        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // TODO 设置开发设备
//        Bugly.setIsDevelopmentDevice(getApplication(), true);
        // 调试时，将第三个参数改为true
        Bugly.init(this, "e0817679fa", true);

        MobSDK.init(this,"770d9303300c","1a46b4a2687480f5b71dba8e6c83b1f0");

        //初始化微信登录
        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APP_ID, false);
        mWxApi.registerApp(Constant.WEIXIN_APP_ID);

        if (Build.VERSION.SDK_INT >= 14) {
            registerActivityLifecycleCallbacks(new FishActivityLifecycleCallbacks());
        }

//        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "554c085a67e58e76a2005d67");

        AppState.instance().init(this);
        AppState.instance().genUserKey();

        // LeakCanary
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/

        //imageloader
        final String CACHE_DIR_NAME = "imageCache";

        File cacheDir = StorageUtils.getOwnCacheDirectory(this, CACHE_DIR_NAME);
        if (isExternalStorageMounted()) {
            cacheDir = getExternalCacheDir();
        } else {
            cacheDir = getCacheDir();
        }

        if (cacheDir == null) {
            cacheDir = getCacheDir();
            if (cacheDir == null) {
                cacheDir = StorageUtils.getOwnCacheDirectory(this, CACHE_DIR_NAME);
            }
        }

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        final Runtime runtime = Runtime.getRuntime();
        long availableMemory = runtime.maxMemory();
        final int BYTES_IN_MEGABYTE = 1024 * 1024;
        int percent = 10;
        float maxMb = availableMemory / BYTES_IN_MEGABYTE;
        if (maxMb >= 128) {
            percent = (int) (100 * (maxMb - 128) / maxMb);
        }
        percent = Math.max(10, percent);
        percent = Math.min(percent, 70);
        int availableProcessors = runtime.availableProcessors();
        int threadPoolSize = 4;

        threadPoolSize = Math.max(threadPoolSize, availableProcessors);

        long maxAge = 30 * 24 * 60 * 60;//seconds
        DiskCache diskCache = new LimitedAgeDiskCache(cacheDir, maxAge);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 1)//Thread.NORM_PRIORITY - 1
                .denyCacheImageMultipleSizesInMemory()
                .threadPoolSize(threadPoolSize)//ImageLoaderConfiguration.Builder.DEFAULT_THREAD_POOL_SIZE=3
                .tasksProcessingOrder(QueueProcessingType.LIFO)//QueueProcessingType.FIFO LIFO
                .diskCacheSize(100 * 1024 * 1024)
                .diskCacheFileCount(500)
                .memoryCacheSizePercentage(percent)
                .diskCache(diskCache)
                .defaultDisplayImageOptions(imageOptions) // default
                .imageDownloader(new ImageLoaderWithCookie(this))
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(this);
        // 安装tinker
        Beta.installTinker();
    }

    // 判断SD卡是否存在
    public static boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static App getAppContext() {
        return mAppContext;
    }

    public static Context getContext() {
        return appContext;
    }

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    public void pushActivity(Activity activity) {
        mActivitys.add(activity);
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        mActivitys.remove(activity);
    }


    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return null;
        }
        Activity activity = mActivitys.get(mActivitys.size() - 1);
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        Activity activity = mActivitys.get(mActivitys.size() - 1);
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        if (activity != null) {
            mActivitys.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        for (Activity activity : mActivitys) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 按照指定类名找到activity
     *
     * @param cls
     * @return
     */
    public static Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (mActivitys != null) {
            for (Activity activity : mActivitys) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    /**
     * @return 作用说明 ：获取当前最顶部activity的实例
     */
    public Activity getTopActivity() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity;

    }

    /**
     * @return 作用说明 ：获取当前最顶部的acitivity 名字
     */
    public String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity.getClass().getName();
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (mActivitys == null) {
            return;
        }
        for (Activity activity : mActivitys) {
            activity.finish();
        }
        mActivitys.clear();
    }

    private static List<Activity> mActivitys = Collections
            .synchronizedList(new LinkedList<Activity>());

    /**
     * 退出应用程序
     */
    public static void appExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }
}