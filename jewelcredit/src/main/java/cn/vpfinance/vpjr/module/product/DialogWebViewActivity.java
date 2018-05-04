package cn.vpfinance.vpjr.module.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * Created by Administrator on 2016/5/24.
 */
public class DialogWebViewActivity extends BaseActivity {

    @Bind(R.id.webview_dialog)
    WebView mWebviewDialog;
    public static final String KEY_URL = "WEBVIEW_URL";
    @Bind(R.id.ibClose)
    ImageButton mIbClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_webview);
        ButterKnife.bind(this);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.9);   //高度设置为屏幕的1.0
        p.width = (int) (d.getWidth() * 0.95);    //宽度设置为屏幕的0.8
        p.alpha = 1.0f;      //设置本身透明度
        p.dimAmount = 0.5f;      //设置黑暗度
        getWindow().setAttributes(p);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        String url = intent.getStringExtra(KEY_URL);
        WebSettings settings = mWebviewDialog.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        mWebviewDialog.loadUrl(url);

        mWebviewDialog.setWebChromeClient(new WebChromeClient());
        mWebviewDialog.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        mIbClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
