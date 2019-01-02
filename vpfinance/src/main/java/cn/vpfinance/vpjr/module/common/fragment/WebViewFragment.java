package cn.vpfinance.vpjr.module.common.fragment;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;

/**
 */
public class WebViewFragment extends BaseFragment{

//    public static final String TITLE = "title";
    public static final String URL = "url";
    private WebView webView;

    public static WebViewFragment newInstance(String url){
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
//        args.putString(TITLE,title);
        args.putString(URL,url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_webview,null);
        webView = ((WebView) view.findViewById(R.id.webview));
        webView.setWebViewClient(new MyWebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        String appCachePath = FinanceApplication.getAppContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
//        init();
        Bundle arguments = getArguments();
        if (arguments != null){
            String url = arguments.getString(URL);
            webView.loadUrl(url);
        }
        return view;
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            //handler.cancel(); // Android默认的处理方式
            handler.proceed();  // 接受所有网站的证书
            //handleMessage(Message msg); // 进行其他处理
        }
    }
//    private void init() {
//        webView.getSettings().setJavaScriptEnabled(true);
//        // 设置可以支持缩放
//        webView.getSettings().setSupportZoom(true);
//        // 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
//        //扩大比例的缩放
//        webView.getSettings().setUseWideViewPort(true);
//        //自适应屏幕
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webView.setWebChromeClient(new MyWebChromeClient());
//        webView.setWebViewClient(new MyWebViewClient());
//    }
}
