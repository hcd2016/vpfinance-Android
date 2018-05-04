package cn.vpfinance.vpjr.module.more;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.Utils;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 */
public class AddWeiXinActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weixin);
        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("关于我们").setHeadBackVisible(View.VISIBLE);
//        公众号已经复制，请打开微信添加关注。
        findViewById(R.id.addWeixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData mClip = ClipData.newPlainText("text", "ZTE-vpfinance");
                myClipboard.setPrimaryClip(mClip);
                Utils.Toast(AddWeiXinActivity.this,"公众号已经复制，请打开微信添加关注。");
            }
        });
    }
}
