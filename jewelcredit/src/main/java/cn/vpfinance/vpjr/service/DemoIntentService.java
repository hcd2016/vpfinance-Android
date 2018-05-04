package cn.vpfinance.vpjr.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;

import org.json.JSONException;
import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.common.WebViewActivity;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class DemoIntentService extends GTIntentService {

    private static final String TAG = "GetuiSdkDemo";

    public static StringBuilder payloadData = new StringBuilder();

    private NotificationManager mNotifMan;

    /**
     * 为了观察透传数据变化.
     */
//    private static int cnt;

    public DemoIntentService() {
        Log.d(TAG, "DemoIntentService: --------------");
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();



        if(mNotifMan==null)
        {
            mNotifMan = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
//        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

//        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
//                + "\ncid = " + cid);

        /*if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.d(TAG, "receiver payload = " + data);

            // 测试消息为了观察数据变化
            *//*if (data.equals("收到一条透传测试消息")) {
                data = data + "-" + cnt;
                cnt++;
            }*//*

            sendMessage(data, 0);
        }*/

        if (payload != null) {
            String data = new String(payload);//{type:2,title:"安卓个推测试",subTitle:"安卓个推测试",content:138}

            showNotification(data, context);

//            Log.d(TAG, "receiver payload : " + data);

            payloadData.append(data);
            payloadData.append("\n");

        }

//        Log.d(TAG, "----------------------------------------------------------------------------------------------");
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);

        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(context);
        String savedCid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_GETUI_CLIENT_ID);
        if(!TextUtils.isEmpty(clientid) && !clientid.equals(savedCid))
        {
            preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_GETUI_CLIENT_ID,clientid);
        }

//        sendMessage(clientid, 1);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
//        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
//        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

//        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
//                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

    private void sendMessage(String data, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = data;
//        DemoApplication.sendMessage(msg);
    }

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
}
