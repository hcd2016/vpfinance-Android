package cn.vpfinance.vpjr.module.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.jewelcredit.util.HttpService;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseActivity;

public class ServerDownActivity extends BaseActivity{

    private long exitTime = 0;
    private long exitDelay = 2000;
    private HttpService mHttpService;

    public static void goThis(Context context){
        Intent intent = new Intent(context,ServerDownActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_down);
        mHttpService = new HttpService(this, this);
        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Map<Integer, ServerDownRequestInfo> requestInfoMap = App.getAppContext().requestInfoMap;
                Iterator<Map.Entry<Integer, ServerDownRequestInfo>> iterator = requestInfoMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<Integer, ServerDownRequestInfo> next = iterator.next();
                    Integer key = next.getKey();
                    ServerDownRequestInfo value = next.getValue();
                    //先移除,在请求,失败则再次添加进去
//                    App.getAppContext().requestInfoMap.remove(key);

                    if (value.getMethodType() == ServerDownRequestInfo.METHOD.GET){
                        mHttpService.httpClient.doGet(value.getUrl(),value.getParams(),key,false);
                    }else{
                        mHttpService.httpClient.doPost(value.getUrl(),value.getParams(),key,false);
                    }
                    Logger.e("ServerDownActivity:remove"+key+",url:"+value.getUrl());
                }*/
                mHttpService.getAppmemberIndex(true);
            }
        });
    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {
        super.onHttpSuccess(reqId, json);
        finish();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > exitDelay) {
                Toast.makeText(this, "再按一次退出微品金融.", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                App.appExit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
