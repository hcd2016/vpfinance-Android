package cn.vpfinance.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.jewelcredit.util.HttpService;
import com.tdk.utils.HttpDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.common.WebViewActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * Created by 张清田 on 15/11/12.
 */
public class PushReceiver extends BroadcastReceiver implements HttpDownloader.HttpDownloaderListener{

    private static final String TAG = "PushReceiver";

    private NotificationManager mNotifMan;

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    private void showNotification(String data,Context context) {
        try {
            JSONObject json = new JSONObject(data);
            if(json!=null)
            {

                String title = TextUtils.isEmpty(json.optString("title")) ? "" : json.optString("title");
                String subTitle = TextUtils.isEmpty(json.optString("subTitle")) ? "" : json.optString("subTitle");
                int type = json.optInt("type");
                String content = TextUtils.isEmpty(json.optString("content")) ? "" : json.optString("content");
                Intent intent = null;
                switch (type)
                {
                    case 1:
                        intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.KEY_TITLE, title);
                        intent.putExtra(WebViewActivity.KEY_URL, content);
                        intent.putExtra(WebViewActivity.IS_GET_TUI,true);
                        break;
                    case 2:
                        try{
                            long pid = Long.parseLong(content);
                            intent = new Intent(context, NewRegularProductActivity.class);
                            intent.putExtra(NewRegularProductActivity.EXTRA_PRODUCT_ID, pid);
                            intent.putExtra(NewRegularProductActivity.NATIVE_PRODUCT_ID, 0);
                            intent.putExtra(NewRegularProductActivity.PRODUCT_TITLE, "产品详情");
                            intent.putExtra(NewRegularProductActivity.IS_GE_TUI, true);
                        }catch (Exception e){
                            e.printStackTrace();
                            intent = new Intent(context, MainActivity.class);
                            intent.putExtra(MainActivity.IS_GET_TUI,true);
                        }

//                        intent = new Intent(context, RegularProductActivity.class);
//                        intent.putExtra(RegularProductActivity.EXTRA_PRODUCT_ID, content);
//                        intent.putExtra(RegularProductActivity.EXTRA_PRODUCT_TYPE, -1);
//                        intent.putExtra(RegularProductActivity.EXTRA_PRODUCT_DATABSE_ID, -1);
//                        intent.putExtra(RegularProductActivity.IS_GET_TUI,true);
                        break;
                    default:
                        intent = new Intent(context, MainActivity.class);
                        intent.putExtra(MainActivity.IS_GET_TUI,true);
                        break;
                }

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

                NotificationCompat.Builder bd = new NotificationCompat.Builder(context);
                bd.setContentTitle(title)
                        .setSmallIcon(R.drawable.push)
                        //.setSubText(subTitle)
                        .setContentText(subTitle)
                        .setOngoing(false)
                        .setContentIntent(pendingIntent);


                Notification notification = bd.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;

                mNotifMan.notify((int)System.currentTimeMillis(),notification);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(mNotifMan==null)
        {
            mNotifMan = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                //Log.e(TAG, "第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);//{type:2,title:"安卓个推测试",subTitle:"安卓个推测试",content:138}

                    showNotification(data,context);

                    Log.e(TAG, "receiver payload : " + data);

                    payloadData.append(data);
                    payloadData.append("\n");

                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                HttpService mHttpService = new HttpService(context, this);

                SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(context);
                String savedCid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_GETUI_CLIENT_ID);
                if(!TextUtils.isEmpty(cid) && !cid.equals(savedCid))
                {
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_GETUI_CLIENT_ID,cid);
                }
                //String uid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);

//                if(AppState.instance().logined() && !TextUtils.isEmpty(uid))
//                {
//                    mHttpService.getuiLoginSendMess(uid, cid);
//                }
                Log.e(TAG, "GET_CLIENTID:" + cid);
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }

    @Override
    public void onHttpCache(int reqId) {

    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {

    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {

    }

    @Override
    public void onHttpError(int reqId, String errmsg) {

    }
}