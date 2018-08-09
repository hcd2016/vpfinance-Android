package cn.vpfinance.vpjr.network;

import android.support.v4.util.ArrayMap;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    static{
        mOkHttpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS);
    }

    /**
     * 该不会开启异步线程。
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException{
        return mOkHttpClient.newCall(request).execute();
    }


    private static RequestBody newFinanceRequest(ArrayMap<String,String> param)
    {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if(param!=null)
        {
            param.put("os","Android");
            param.put("version","1.0");
            for(Map.Entry<String, String> entry :param.entrySet())
            {
                builder.addFormDataPart(entry.getKey(),entry.getValue());
            }
        }
        return builder.build();
    }


    public static Request newPostRequest(String url, ArrayMap<String,String> param)
    {
        Request request = new Request.Builder()
                .url(url)
                .post(newFinanceRequest(param))
                .build();
        return request;
    }

    /**
     * 开启异步线程访问网络
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback){
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     * @param request
     */
    public static void enqueue(Request request){
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }

        });
    }

    public static String getStringFromServer(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
}