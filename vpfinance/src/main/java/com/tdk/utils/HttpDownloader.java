package com.tdk.utils;

import com.jewelcredit.util.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.Map;

import cn.vpfinance.vpjr.App;

public class HttpDownloader {
	public static AsyncHttpClient mClient = newHttpClient();

	public HttpDownloader()
	{
//		mClient.setMaxRetriesAndTimeout(3, 60000000);
		mClient.setMaxConnections(20);
		mClient.setConnectTimeout(36000);
		mClient.setTimeout(36000);
		mClient.setResponseTimeout(36000);
		mClient.setEnableRedirects(true);

	}

	public static void doGet(String url, JsonHttpResponseHandler respHandler)
	{
		mClient.get(url, respHandler);
	}


	public static void doGetFile(String url, FileAsyncHttpResponseHandler respHandler)
	{
		mClient.get(url, respHandler);
	}

	public static void doPost(String url, Map<String, String> params, JsonHttpResponseHandler respHandler)
	{
		mClient.post(url, new RequestParams(params), respHandler);
	}

	public static void doPostFile(String url, Map<String, String> params, FileAsyncHttpResponseHandler respHandler)
	{
		mClient.post(url, new RequestParams(params), respHandler);
	}

	private static AsyncHttpClient newHttpClient()
	{
//		InputStream ins = null;
		try
		{

//			ins = context.getAssets().open("app_pay.cer"); //下载的证书放到项目中的assets目录中
//			CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
//			Certificate cer = cerFactory.generateCertificate(ins);
//			KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, null);
//			keyStore.setCertificateEntry("trust", cer);
			SSLSocketFactoryEx socketFactory = new SSLSocketFactoryEx(keyStore);
			socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", socketFactory, 443));
//			schemeRegistry.register(new Scheme("https", socketFactory, 8443));
			AsyncHttpClient asyncHttpClient = new AsyncHttpClient(schemeRegistry);
			asyncHttpClient.addHeader("APP-VERSION", Utils.getVersion(App.getAppContext()));

			//保存cookie，自动保存到了shareprefercece
			PersistentCookieStore myCookieStore = new PersistentCookieStore(App.getAppContext());
			asyncHttpClient.setCookieStore(myCookieStore);

			return asyncHttpClient;
		} catch (Exception e) {

		}
//		finally {
//			try {
//				if (ins != null)
//					ins.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}

		return new AsyncHttpClient();
	}



	public static abstract interface HttpDownloaderListener
	{
		public abstract void onHttpSuccess(int reqId, JSONObject json);
		public abstract void onHttpSuccess(int reqId, JSONArray json);
		public abstract void onHttpCache(int reqId);
		public abstract void onHttpError(int reqId, String errmsg);
	}
}

