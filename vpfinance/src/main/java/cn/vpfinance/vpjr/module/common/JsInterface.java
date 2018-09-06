package cn.vpfinance.vpjr.module.common;

import android.annotation.SuppressLint;
import android.webkit.JavascriptInterface;

import com.jewelcredit.util.Utils;

import java.lang.ref.WeakReference;

import cn.vpfinance.vpjr.base.BaseActivity;

public class JsInterface {
    private WeakReference<BaseActivity> weakReference;

    public JsInterface(BaseActivity activity) {
        this.weakReference = new WeakReference<BaseActivity>(activity);
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void riskCompleteClick() {
        weakReference.get().finish();
        Utils.Toast("完成按钮点击了!");
    }
}
