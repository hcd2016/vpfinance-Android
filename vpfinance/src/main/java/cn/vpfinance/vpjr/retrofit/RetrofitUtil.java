package cn.vpfinance.vpjr.retrofit;

import com.jewelcredit.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.network.ApiService;
import cn.vpfinance.vpjr.network.HttpConstant;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * retrofit请求封装
 */
public class RetrofitUtil {
    /**
     * 超时时间（单位s）
     */
    private static final int DEFAULT_TIMEOUT = 60;


    public static <T> T create(Class<T> service) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(HttpConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .callFactory(genericClient());
        Retrofit mRetrofit = builder.build();
        return mRetrofit.create(service);
    }

    public static ApiService create() {
        return create(ApiService.class);
    }

    /**
     * 配置OkHttpClient
     */
    public static OkHttpClient genericClient() {
        final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        //用于打印http信息的拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .addInterceptor(interceptor)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
//                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                                .addHeader("Accept-Encoding", "gzip, deflate")
//                                .addHeader("Connection", "keep-alive")
//                                .addHeader("Accept", "*/*")
//                                .addHeader("Cookie", "add cookies here")
                                .addHeader("APP-VERSION", Utils.getVersion(FinanceApplication.getAppContext()))
                                .build();
                        return chain.proceed(request);
                    }
                })
                .cookieJar(new CookieJar() {//携带cookie
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();

        return httpClient;
    }
}
