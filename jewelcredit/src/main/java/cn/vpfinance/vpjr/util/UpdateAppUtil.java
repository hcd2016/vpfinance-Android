package cn.vpfinance.vpjr.util;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Pair;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cn.vpfinance.vpjr.util.CameraGalleryUtils;
import cn.vpfinance.vpjr.util.Logger;


/**
 * Created by zzlz13 on 2018/1/11.
 */

public class UpdateAppUtil {

    private static UpdateAppUtil instance;
    private Context context;
    private static DownloadManager downloadManager;

    public static final String APK_NAME = "update.apk";

    private UpdateAppUtil(Context context) {
        this.context = context.getApplicationContext();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static UpdateAppUtil getInstance(Context context) {
        if (instance == null) {
            instance = new UpdateAppUtil(context);
        }
        return instance;
    }

    public static void install(Context context, String filePath) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File apkFile = new File(filePath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //这里的authority和Manifest.xml中添加provider中的authorities保持一致
                Uri contentUri = FileProvider.getUriForFile(context, CameraGalleryUtils.getAuthority(context), apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Logger.e("安装APK失败:" + e.toString());
        }
    }

    public void install(long downId) {
        Logger.e("install.downId = " + downId);
        if (downId == -1) return;
//        int downloadStatus = getDownloadStatus(downId);
        String apkPath = getPathByDownId(downId);
//       boolean compare = compare(getApkInfo(context, apkPath), context);
//        if (compare || downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {}
        install(context,apkPath);
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            File file = (new File(apkPath));
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, CameraGalleryUtils.AUTHORITY, file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);*/

    }

    /**
     * 获取下载状态
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @return int
     * @see DownloadManager#STATUS_PENDING
     * @see DownloadManager#STATUS_PAUSED
     * @see DownloadManager#STATUS_RUNNING
     * @see DownloadManager#STATUS_SUCCESSFUL
     * @see DownloadManager#STATUS_FAILED
     */
    public int getDownloadStatus(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                }
            } finally {
                c.close();
            }
        }
        return -1;
    }

    public String getPathByDownId(long downId) {
        Logger.e("getPathByDownId.downId = " + downId);
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downId);
        Cursor c = downloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    int fileUriIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                    String fileUri = c.getString(fileUriIdx);
                    String fileName = null;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        if (fileUri != null) {
                            fileName = Uri.parse(fileUri).getPath();
                            Logger.e("fileName1 = " + fileName);
                        }
                    } else {
                        //Android 7.0以上的方式：请求获取写入权限，这一步报错
                        //过时的方式：DownloadManager.COLUMN_LOCAL_FILENAME
                        int fileNameIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                        fileName = c.getString(fileNameIdx);
                        Logger.e("fileName2 = " + fileName);
                    }
                    return fileName;
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

    //"下载完成后点击打开"
    public long download(String downUrl, String title, String description) {
        if (!canDownloadState(context)) {
            Toast.makeText(context, "下载服务未启用,请您启用", Toast.LENGTH_SHORT).show();
            showDownloadSetting(context);
            return -1L;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //下载完成后显示通知栏提示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(title);
        request.setDescription(description);
        //文件保存路径
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, APK_NAME);
        return downloadManager.enqueue(request);
    }

    private PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            //String packageName = info.packageName;
            //String version = info.versionName;
            //Log.d(TAG, "packageName:" + packageName + ";version:" + version);
            //String appName = pm.getApplicationLabel(appInfo).toString();
            //Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息
            return info;
        }
        return null;
    }

    //如果当前应用版本小于apk的版本则返回true
    private boolean compare(PackageInfo apkInfo, Context context) {
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
//                Log.d("aa", "apkInfo.versionCode: " + apkInfo.versionCode + "-----packageInfo.versionCode:" + packageInfo.versionCode);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 弹出开启下载服务框
     *
     * @param context
     */
    public void showDownloadSetting(Context context) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));

        PackageManager packageManager = context.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            context.startActivity(intent);
        }
    }


    /**
     * 判断是否启用下载服务
     *
     * @return
     */
    public boolean canDownloadState(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*private OnDownloadProgressListener listener;

    interface OnDownloadProgressListener {
        void downloadProgress(int progress);
    }

    public void setListener(OnDownloadProgressListener listener) {
        this.listener = listener;
    }*/

    //注意: unregister
    public Pair<ContentResolver,DownloadObserver> registerDownloadObserver(Handler handler, long downId) {
        Logger.e("registerDownloadObserver");
        DownloadObserver downloadObserver = new DownloadObserver(handler, downId);
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://downloads/"), true, downloadObserver);
        return new Pair<>(contentResolver,downloadObserver);
    }
    public static final int WHAT_DOWNLOADING = 100;
    public static final int WHAT_DOWNLOADED = 101;

    public class DownloadObserver extends ContentObserver {

        private Cursor cursor;
        private int progress;
        private Handler handler;
        private DownloadManager.Query query;
        private long downId;
        private boolean isFinished = false;

        public DownloadObserver(Handler downLoadHandler, long downId) {
            super(downLoadHandler);
            this.handler = downLoadHandler;
            this.downId = downId;
            query = new DownloadManager.Query().setFilterById(downId);
            Logger.e("DownloadObserver初始化");
        }

        @Override
        public void onChange(boolean selfChange) {
            // 每当/data/data/com.android.providers.download/database/database.db变化后，触发onCHANGE，开始具体查询
            super.onChange(selfChange);
            if (!isFinished){
                try {
                    cursor = downloadManager.query(query);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    progress = ((bytes_downloaded * 100) / bytes_total);
                    Logger.e("progress:" + progress);
                    if (handler != null) {
                        Message message = new Message();
                        message.what = WHAT_DOWNLOADING;
                        message.arg1 = progress;
                        handler.sendMessageDelayed(message, 100);
//                        handler.sendEmptyMessageDelayed(progress, 100);
                    }
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        Message message = new Message();
                        message.what = WHAT_DOWNLOADED;
                        message.obj = downId;
                        handler.sendMessageDelayed(message, 100);
                        isFinished = true;
//                        handler.sendEmptyMessageDelayed(WHAT_DOWNLOADED, 100);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
    }
}
