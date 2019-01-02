package cn.vpfinance.vpjr.module.common;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.loopj.android.http.PersistentCookieStore;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.cookie.Cookie;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.product.invest.BankInvestSuccessHintActivity;
import cn.vpfinance.vpjr.module.welcome.WelcomeActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FileUtil;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.util.StatusBarCompat1;

public class WebViewActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_URL = "WEBVIEW_URL";
    public static final String KEY_TITLE = "WEBVIEW_TITLE";
    public static final String IS_GET_TUI = "IS_GET_TUI";

    private ImageView goForwardBtn;
    private ImageView gobackBtn;
    private ImageView refreshBtn;
    private ProgressBar progressBar;
    private WebView webView;
    private TextView titleTv;
    private String title;
    private static final String DEFAULT_TITLE = "微品金融";
    String str = null;
    private ImageButton mShare;
    private HttpService mHttpService;
    private String mShareUrl = null;
    private String mShareTitle;
    private String mShareImage;
    private String mShareContent;
    private String mShareType;
    private String mTempUrl = "";
    private View mFakeStatusBar;


    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_webview);
        mHttpService = new HttpService(this, this);
        initView();
    }

    protected void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            str = intent.getStringExtra(KEY_URL);
            title = intent.getStringExtra(KEY_TITLE);
            //统一 ： 微品金融
//            title = DEFAULT_TITLE;
            if (intent.getBooleanExtra(IS_GET_TUI, false)) {
                //个推点击了过来的就友盟统计
                ArrayMap<String, String> map = new ArrayMap<String, String>();
                map.put("GeTuiType", "1");
                MobclickAgent.onEvent(WebViewActivity.this, "GeTuiClick", map);
            }
        }


        mShare = ((ImageButton) findViewById(R.id.share));
        mShare.setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        findViewById(R.id.headBack).setOnClickListener(this);
        titleTv = (TextView) findViewById(R.id.title);

        this.progressBar = ((ProgressBar) findViewById(R.id.webview_progress));
        this.gobackBtn = ((ImageView) findViewById(R.id.webview_backward));
        this.gobackBtn.setOnClickListener(this);
        this.goForwardBtn = ((ImageView) findViewById(R.id.webview_forward));
        this.goForwardBtn.setOnClickListener(this);
        this.refreshBtn = ((ImageView) findViewById(R.id.webview_refresh));
        this.refreshBtn.setOnClickListener(this);
        this.webView = ((WebView) findViewById(R.id.webview_main));


        mFakeStatusBar = findViewById(R.id.fake_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mFakeStatusBar.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = mFakeStatusBar.getLayoutParams();
            layoutParams.height = StatusBarCompat1.getStatusBarHeight(this);
            mFakeStatusBar.setLayoutParams(layoutParams);
        } else {
            mFakeStatusBar.setVisibility(View.GONE);
        }
        WebSettings settings = webView.getSettings();
        //vue.js 必须设置, 不然一些api无效
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        String appCachePath = App.getAppContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsInterface(this), "vp_h5");
        // 设置可以支持缩放
        this.webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        this.webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        this.webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        this.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.webView.setWebChromeClient(new MyWebChromeClient());
        this.webView.setWebViewClient(new MyWebViewClient());

        //webview点击js下载
//        webView.setDownloadListener(new MyWebViewDownLoadListener());
        loadUrl(webView, str);
    }

//    private class MyWebViewDownLoadListener implements DownloadListener {
//
//        @Override
//        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//            Log.i("tag", "url="+url);
//            Log.i("tag", "userAgent="+userAgent);
//            Log.i("tag", "contentDisposition="+contentDisposition);
//            Log.i("tag", "mimetype="+mimetype);
//            Log.i("tag", "contentLength="+contentLength);
//            Uri uri = Uri.parse(url);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
//        }
//    }

    /**
     * 追加参数,判断平台
     *
     * @param url
     */
    private void loadUrl(WebView web, String url) {
        if (TextUtils.isEmpty(url)) return;

        if (url.contains("close_hx_window")) {
            boolean value = SharedPreferencesHelper.getInstance(WebViewActivity.this).getBooleanValue(BankInvestSuccessHintActivity.BANK_INVEST_SUCCESS_IS_SHOW, true);
            if (value) {
                gotoActivity(BankInvestSuccessHintActivity.class);
            }
            finish();
            return;
        }
        if (!url.contains("regChannel=1")) {
            if (url.contains("?")) {
                url += "&regChannel=1";
            } else {
                url += "?regChannel=1";
            }
        }
        if (!url.contains("weixin")) {
            if (url.contains("?")) {
                url += "&platform=android";
            } else {
                url += "?platform=android";
            }
        }
        if (!isFinishing()) {
            String cookieText = getCookieText();
            syncCookie(url, cookieText);
            if (url.contains(",1") && url.contains("hx/repayment/repay")) {
                url = url.replace(",1", "");
            }
            if (url.contains("riskCompleteClick")) {
                finish();
                return;
            }
            web.loadUrl(url);
        }
    }

    /**
     * 将cookie同步到WebView
     *
     * @param url    WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public boolean syncCookie(String url, String cookie) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(WebViewActivity.this);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        String[] split = cookie.split(";");
        for (String s : split) {
            cookieManager.setCookie(url, s);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
        }
        String newCookie = cookieManager.getCookie(url);
        return TextUtils.isEmpty(newCookie) ? false : true;
    }

    /**
     * 获取标准 Cookie
     */
    private String getCookieText() {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(WebViewActivity.this);
        List<Cookie> cookies = myCookieStore.getCookies();
        Logger.d("cookies.size() = " + cookies.size());
//        Util.setCookies(cookies);
        for (Cookie cookie : cookies) {
            Logger.d(cookie.getName() + " = " + cookie.getValue());
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();
            if (!TextUtils.isEmpty(cookieName)
                    && !TextUtils.isEmpty(cookieValue)) {
                sb.append(cookieName + "=");
                sb.append(cookieValue + ";");
            }
        }
        Logger.e("cookie", sb.toString());
        return sb.toString();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }

    protected void onDestroy() {
        if (webView != null) {
            webView.setVisibility(View.GONE);
        }
        super.onDestroy();
    }

    private void canBackOrForward() {
        if (this.webView.canGoBack())
            this.gobackBtn.setImageResource(R.drawable.backward_sel);
        else
            this.gobackBtn.setImageResource(R.drawable.backward);

        if (this.webView.canGoForward())
            this.goForwardBtn.setImageResource(R.drawable.forward_sel);
        else
            this.goForwardBtn.setImageResource(R.drawable.forward);
    }

    public boolean onKeyDown(int code, KeyEvent event) {
        if (code == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            isShare(webView.getOriginalUrl());
            return true;
        }
        return super.onKeyDown(code, event);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share:
                showShare(mShareTitle, mShareContent, mShareImage, mShareUrl);
                break;
            case R.id.headBack:
            case R.id.webview_backward:
                if (this.webView.canGoBack()) {
                    this.webView.goBack();
                } else {
                    if (WelcomeActivity.WelcomeEventUrl.equals(str)) {
                        Intent main = new Intent(this, MainActivity.class);
                        startActivity(main);
                    }
                    finish();
                }
                isShare(webView.getOriginalUrl());
                break;
            case R.id.webview_forward:
                if (this.webView.canGoForward())
                    this.webView.goForward();
                break;
            case R.id.close:
                if (WelcomeActivity.WelcomeEventUrl.equals(str)) {
                    Intent main = new Intent(this, MainActivity.class);
                    startActivity(main);
                }
                finish();
                break;
            case R.id.webview_refresh:
                if ((this.webView.getProgress() < 100) && (this.webView.getProgress() > 0)) {
                    this.webView.stopLoading();
                    return;
                } else {
                    this.webView.reload();
                }

                break;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        public void onCloseWindow(WebView wevbiew) {
            super.onCloseWindow(wevbiew);
        }

        public void onProgressChanged(WebView wevbiew, int progress) {
            super.onProgressChanged(wevbiew, progress);
            WebViewActivity.this.progressBar.setProgress(progress);
        }

        public void onReceivedTitle(WebView wevbiew, String titleStr) {
            super.onReceivedTitle(wevbiew, titleStr);
            if (!TextUtils.isEmpty(title)) {
                titleTv.setText(title);
            } else {
                titleTv.setText(TextUtils.isEmpty(titleStr) ? DEFAULT_TITLE : titleStr);
            }
        }

        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            WebViewActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg);
        }

        // For Lollipop 5.0+ Devices
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                return false;
            }
            return true;
        }
    }

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //登录成功后uid替换
        if (!TextUtils.isEmpty(mTempUrl)) {
            Uri uri = Uri.parse(mTempUrl);
//        if (!TextUtils.isEmpty(str)) {
//            Uri uri = Uri.parse(str);
            String path = uri.getPath();
            String uid = uri.getQueryParameter("uid");
            String type = uri.getQueryParameter("type");
            String isShare = uri.getQueryParameter("isShare");
            User user = DBUtils.getUser(this);
            if (user != null && user.getUserId() != null) {
                Long userId = user.getUserId();
                loadUrl(webView, HttpService.mBaseUrl + path + "?uid=" + userId + "&isShare=" + isShare + "&type=" + type);
            }
        }
    }

    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private class MyWebViewClient extends WebViewClient {

        private MyWebViewClient() {
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            //handler.cancel(); // Android默认的处理方式
            handler.proceed();  // 接受所有网站的证书
            //handleMessage(Message msg); // 进行其他处理
        }

        public void doUpdateVisitedHistory(WebView webview, String url, boolean isReload) {
            super.doUpdateVisitedHistory(webview, url, isReload);
            WebViewActivity.this.canBackOrForward();
        }

        public void onPageFinished(WebView webview, String url) {
            super.onPageFinished(webview, url);
            WebViewActivity.this.progressBar.setVisibility(View.GONE);
            isShare(url);
            Logger.e("onPageFinished:" + url);
        }

        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            super.onPageStarted(webview, url, favicon);
            WebViewActivity.this.progressBar.setVisibility(View.VISIBLE);
        }

        public void onReceivedError(WebView webview, int errorCode, String description, String failingUrl) {
            super.onReceivedError(webview, errorCode, description, failingUrl);
            Utils.Toast(WebViewActivity.this, description);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            } else {
                url = request.toString();
            }
            Logger.e("shouldOverrideUrlLoading:" + url);
            if (TextUtils.isEmpty(url)) return true;
            //vpfinance://toLogin
            //如果跳转链接出现vpfinance://开头的就去调用相对应的方法
            if (!TextUtils.isEmpty(url) && url.contains("vpfinance://")) {
                String methodName = url.substring("vpfinance://".length());
//                Logger.e("qqq:"+methodName);
                if ("toLogin".equals(methodName)) {
                    mTempUrl = webView.getUrl();
                    gotoActivity(LoginActivity.class);
                    //?uid=&isShare=1&type=4   成功后添加uid参数
                }
            }
            return true;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.e("shouldOverrideUrlLoading:" + url);
            if (TextUtils.isEmpty(url)) return true;
            //vpfinance://toLogin
            //如果跳转链接出现vpfinance://开头的就去调用相对应的方法
            if (!TextUtils.isEmpty(url) && url.contains("vpfinance://")) {
                String methodName = url.substring("vpfinance://".length());
//                Logger.e("qqq:"+methodName);
                if ("toLogin".equals(methodName)) {
                    mTempUrl = webView.getUrl();
                    gotoActivity(LoginActivity.class);
                    //?uid=&isShare=1&type=4   成功后添加uid参数
                }
                return true;
            }
            if (!TextUtils.isEmpty(url) && url.contains("tel:")) {
                String[] split = url.split(":");
                if (split.length > 1) {
                    String phoneNum = split[1];
                    if (!TextUtils.isEmpty(phoneNum)) {
                        Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phoneNum));
                        startActivity(intent);
                        return true;
                    }
                }
            }

            if (null != getProtocol(url) && getProtocol(url).equals("app") && null != getSite(url) && getSite(url).equals("register")) {
                if (AppState.instance().logined()) {
                    Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
                    intent.putExtra("isfromweb", true);
                    startActivity(intent);
                    WebViewActivity.this.finish();
                } else {
                    startActivity(new Intent(WebViewActivity.this, LoginActivity.class));
                }
            } else {
                loadUrl(view, url);
            }
            //自己处理,不调用外部浏览器
            return true;
        }
    }

    /**
     * 如果url中isShare=1,显示分享按钮
     *
     * @param url
     */
    private void isShare(String url) {
        String isShareValue = null;
        try {
            isShareValue = Uri.parse(url).getQueryParameter("isShare");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Logger.e("isShareValue:"+isShareValue);
        if (mShare == null) return;
        if (!TextUtils.isEmpty(isShareValue) && "1".equals(isShareValue)) {
            //可以分享的就其中还有一个type,根据活动请求url获得分享内容
            //如:/activity/doubleFestivalActivityH5?uid=104&isShare=1&type=4
            mShare.setVisibility(View.VISIBLE);
            mShareType = Uri.parse(url).getQueryParameter("type");
            mShareUrl = url;
            if (!TextUtils.isEmpty(mShareType)) {
                mHttpService.getWebViewShareContent(mShareType);
            }
        } else {
            mShareType = "";
            mShareUrl = "";
            mShareContent = "";
            mShareImage = "";
            mShareTitle = "";
            mShare.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Webview_Share_Content.ordinal()) {
            mShareContent = json.optString("content");
            mShareImage = json.optString("shareImage");
            mShareTitle = json.optString("title");
        }
    }

    /**
     * 通知服务器分享成功
     */
    private void startRequestInformSuccess() {
        User user = DBUtils.getUser(WebViewActivity.this);
        String phone = "";
        if (user != null && !TextUtils.isEmpty(user.getCellPhone())) {
            phone = user.getCellPhone();
        }

        mHttpService.getWebViewShareSuccess(phone, mShareType);
    }

    private void showShare(String title, String text, String imageUrl, String link) {
        if (TextUtils.isEmpty(link)) return;
        //去掉后面的get参数
//        link = HttpService.mBaseUrl + Uri.parse(link).getPath();
//        Logger.e("title:"+title+",text:"+text+",imageUrl:"+imageUrl+",link:"+link);
        //imageUrl为空时，就算有link也只是单纯的文字，不能跳转（微信）
        // 注意:大坑,图片先要下载到本地
        //link为空时，只会有图片，连接文案都没有（微信）

        imageUrl = FileUtil.netPath2LocalPath(imageUrl);//网络地址转为本地地址
        if (TextUtils.isEmpty(imageUrl)) return;
//        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(link);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        if (!TextUtils.isEmpty(imageUrl)) {
            oks.setImagePath(imageUrl);
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(link);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(title);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(link);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                Logger.e("onComplete");
                startRequestInformSuccess();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
//                Logger.e("onError");
            }

            @Override
            public void onCancel(Platform platform, int i) {
//                Logger.e("onCancel");
            }
        });
        // 启动分享GUI
        oks.show(this);
    }

    private String getProtocol(String url) {
        if (null == url) {
            return null;
        }
        int pos = url.indexOf("://");
        if (-1 != pos) {
            return url.substring(0, pos);
        } else {
            return null;
        }
    }

    private String getSite(String url) {
        if (null == url) {
            return null;
        }
        int pos = url.indexOf("://");
        if (-1 != pos) {
            return url.substring(pos + 3, url.length());
        } else {
            return null;
        }
    }
}
