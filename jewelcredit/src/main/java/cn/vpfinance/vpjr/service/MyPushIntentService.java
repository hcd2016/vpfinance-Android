package cn.vpfinance.vpjr.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.common.WebViewActivity;

/**
 * Created on 2015/8/24.
 */
public class MyPushIntentService extends UmengBaseIntentService {
    @Override
    protected void onMessage(Context context, Intent intent) {
        // 需要调用父类的函数，否则无法统计到消息送达
        super.onMessage(context, intent);
        try {
            //可以通过MESSAGE_BODY取得消息体
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            UTrack.getInstance(context).trackMsgClick(msg);
            if(null != msg.url){
                Intent webIntent = new Intent(context, WebViewActivity.class);
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                webIntent.putExtra("WEBVIEW_URL", msg.url);
                if(null != msg.title){
                    webIntent.putExtra("WEBVIEW_TITLE", msg.title);
                }
                notify(webIntent, msg.title, msg.custom, msg.url);
            }
            // 完全自定义消息的处理方式，点击或者忽略
            boolean isClickOrDismissed = true;
            if(isClickOrDismissed) {
                //完全自定义消息的点击统计
                UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
            } else {
                //完全自定义消息的忽略统计
                UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void notify(Intent intent, String title, String custom, String url){
        PendingIntent resultPendingIntent = PendingIntent.getActivity(MyPushIntentService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 建立所要创建的Notification的配置信息，并有notifyBuilder来保存。
        Notification notification = new Notification.Builder(MyPushIntentService.this)
                // 触摸之后，通知立即消失
                .setAutoCancel(true)
                        // 显示的时间
                .setWhen(System.currentTimeMillis())
                        // 设置通知的小图标
                .setSmallIcon(R.drawable.ic_launcher)
                        // 设置状态栏显示的文本
                .setTicker(title)
                        // 设置通知的标题
                .setContentTitle(title)
                        // 设置通知的内容
                .setContentText(custom)
                        // 设置声音（系统默认的）
                .setDefaults(Notification.DEFAULT_SOUND)
                        // 设置跳转的activity
                .setContentIntent(resultPendingIntent).build();

        // 创建NotificationManager对象，并发布和管理所要创建的Notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(100, notification);
    }
}
