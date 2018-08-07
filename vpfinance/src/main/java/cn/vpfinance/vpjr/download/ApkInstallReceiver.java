package cn.vpfinance.vpjr.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Administrator on 2016/9/9.
 */
public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long id = SpUtils.getInstance(context).getLong("downloadId", -1L);
            if (downloadApkId == id) {
                installApk(context, downloadApkId);
            }
        }
    }

    private static void installApk(Context context, long downloadApkId) {

        try{
            Intent install = new Intent(Intent.ACTION_VIEW);
            DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
            if (downloadFileUri != null) {
                Uri uri = FileDownloadManager.getInstance(context).getDownloadUri(downloadApkId);
                install.setDataAndType(uri, "application/vnd.android.package-archive");
//            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
//            android.os.Process.killProcess(android.os.Process.myPid());
            } else {
//            Log.e("aa", "下载失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
