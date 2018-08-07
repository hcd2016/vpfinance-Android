package cn.vpfinance.vpjr.module.product.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.jewelcredit.util.HttpService;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.view.InsideWebView;

/**
 */
public class H5Fragment extends BaseFragment{
    private Context mContext;
    private InsideWebView webView;
    private static final String LoadUrl = "loadUrl";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public static H5Fragment newInstance(String url){
        H5Fragment h5Fragment = new H5Fragment();
        Bundle bundle = new Bundle();
        bundle.putString(LoadUrl,url);
        h5Fragment.setArguments(bundle);
        return h5Fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(mContext, R.layout.fragment_h5, null);
        webView = ((InsideWebView) view.findViewById(R.id.webView));
        init();
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init() {
        String loadurl = getArguments().getString(LoadUrl);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        if (!TextUtils.isEmpty(loadurl)){
            webView.loadUrl(HttpService.mBaseUrl+loadurl);
        }
    }

}
