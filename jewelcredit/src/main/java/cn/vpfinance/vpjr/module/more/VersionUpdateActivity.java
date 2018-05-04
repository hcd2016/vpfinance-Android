package cn.vpfinance.vpjr.module.more;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.download.ApkUpdateUtils;
import cn.vpfinance.vpjr.download.DownloadObserver;
import cn.vpfinance.vpjr.util.UpdateAppUtil;
import cn.vpfinance.vpjr.view.ProgressButton;

/**
 */
public class VersionUpdateActivity extends BaseActivity {

    private ActionBarLayout titleBar;

    private Context        mContext;
    private ProgressButton progressButton;
    private boolean updated = false;
    private HttpService mHttpService;
    public static final String UPDATE_LOG     = "updateLog";
    public static final String IS_NEED_UPDATE = "is_need_update";
    public static final String UPDATE_URL     = "update_url";
    private String           mUpdateUrl;
    private long downloadId = -1;
    private Pair<ContentResolver,UpdateAppUtil.DownloadObserver> contentResolverInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update);
        mHttpService = new HttpService(this, this);
        //        mHttpService.checkAppVersion();
        mContext = this;
        titleBar = ((ActionBarLayout) findViewById(R.id.titleBar));

        titleBar.setHeadBackVisible(View.VISIBLE).setTitle("版本更新");
        progressButton = ((ProgressButton) findViewById(R.id.btnVersionUpdate));
        progressButton.setBackgroundColor(Color.parseColor("#e02028"));
        ((TextView) findViewById(R.id.currVer)).setText("当前版本" + Utils.getVersion(mContext));
        Intent intent = getIntent();
//        if (intent != null && (!TextUtils.isEmpty(intent.getStringExtra(UPDATE_LOG)))) {
        if (intent != null) {

            //            progressButton.setEnabled(intent.getBooleanExtra(IS_NEED_UPDATE,false));
            mUpdateUrl = intent.getStringExtra(UPDATE_URL);
            mUpdateUrl = "http://www.vpfinance.cn/app/android/vpjr.apk";
            if (!TextUtils.isEmpty(intent.getStringExtra(UPDATE_LOG))){
                ((TextView) findViewById(R.id.tvUpdateLog)).setText(Html.fromHtml(intent.getStringExtra(UPDATE_LOG)));
            }
        }

        progressButton.setOnProgressButtonClickListener(new ProgressButton.OnProgressButtonClickListener() {
            @Override
            public void onClickListener() {
                /*long downloadId = download(mUpdateUrl);
                if (downloadId != -1) {

                    mDownloadObserver = new DownloadObserver(mHandler, VersionUpdateActivity.this, downloadId);
                    mContentResolver = getContentResolver();
                    mContentResolver.registerContentObserver(
                            Uri.parse("content://downloads/"),
                            true,
                            mDownloadObserver);
                }*/
                if (downloadId != -1){
                    UpdateAppUtil.getInstance(VersionUpdateActivity.this).install(downloadId);
                }else{
                    UpdateAppUtil instance = UpdateAppUtil.getInstance(VersionUpdateActivity.this);
                    long downloadId = instance.download(mUpdateUrl,getResources().getString(R.string.app_name),"下载完成后点击打开");
                    contentResolverInfo = instance.registerDownloadObserver(mHandler, downloadId);
                }
            }
        });

    }
    public long download(String url) {
        if (!ApkUpdateUtils.canDownloadState(this)) {
            Toast.makeText(this, "下载服务未启用,请您启用", Toast.LENGTH_SHORT).show();
            ApkUpdateUtils.showDownloadSetting(this);
            return -1L;
        }
        if (!TextUtils.isEmpty(url)) {
//            url = "http://127.0.0.1:8080/vpjr.apk";
            long id = ApkUpdateUtils.download(this, url, getResources().getString(R.string.app_name));
            return id;
        }
        return -1L;
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UpdateAppUtil.WHAT_DOWNLOADING:
                    int progress = msg.arg1;
                    progressButton.setBackgroundColor(Color.parseColor("#cccccc"));
                    progressButton.setText(progress + "%");
                    progressButton.setProgress(progress);
                    break;
                case UpdateAppUtil.WHAT_DOWNLOADED:
                    downloadId = (long)msg.obj;
                    progressButton.setText("点击安装");
                    progressButton.setBackgroundColor(Color.parseColor("#e02028"));
                    UpdateAppUtil.getInstance(VersionUpdateActivity.this).install(downloadId);
                    break;
            }
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (msg.what < 100) {
                        progressButton.setBackgroundColor(Color.parseColor("#cccccc"));
                        progressButton.setText(msg.what + "%");
                        progressButton.setProgress(msg.what);
                    } else {
                        progressButton.setText("点击安装");
                        progressButton.setBackgroundColor(Color.parseColor("#e02028"));
                    }

                }
            });*/


        }
    };

    //    @Override
    //    public void onHttpSuccess(int reqId, JSONObject json) {
    //        if (reqId == ServiceCmd.CmdId.CMD_APPUPDATE.ordinal()) {
    //            AppUpdateInfo info = new AppUpdateInfo();
    //            String msg = mHttpService.onGetVersion2(json, info);
    //
    //
    //        }
    //    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contentResolverInfo != null) {
            try{
                ContentResolver contentResolver = contentResolverInfo.first;
                UpdateAppUtil.DownloadObserver downloadObserver = contentResolverInfo.second;
                contentResolver.unregisterContentObserver(downloadObserver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
