package cn.vpfinance.vpjr.module.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.jewelcredit.util.Utils;

import java.lang.ref.WeakReference;

import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.more.AboutUsActivity;

public class JsInterface {
    private WeakReference<BaseActivity> weakReference;

    public JsInterface(BaseActivity activity) {
        this.weakReference = new WeakReference<BaseActivity>(activity);
    }

    @JavascriptInterface
    public void riskCompleteClick() {
//        weakReference.get().finish();
//        Intent intent = new Intent(((BaseActivity)weakReference.get()),AboutUsActivity.class);
//        ((BaseActivity)weakReference.get()).gotoActivity(intent);
//        Utils.Toast("完成按钮点击了!");
    }
}
