package cn.vpfinance.vpjr.module.setting;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

public class BindPhoneByUnableCodeActivity extends BaseActivity{

    @Bind(R.id.mActionBar)
    ActionBarWhiteLayout mActionBar;

    public static void goThis(Context context){
        Intent intent = new Intent(context, BindPhoneByUnableCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_by_unable);
        ButterKnife.bind(this);

        mActionBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("验证手机号");
    }

    @OnClick({R.id.vClickCall,R.id.tvWeiXin1,R.id.tvWeiXin2})
    public void click(View view){
        switch (view.getId()){
            case R.id.vClickCall:
            Intent intent=new Intent("android.intent.action.DIAL", Uri.parse("tel:0755-86627551"));
            startActivity(intent);
                break;
            case R.id.tvWeiXin1:
                TextView weiXin1 = (TextView) findViewById(R.id.tvWeiXin1);
                copeTv(weiXin1);
                break;
            case R.id.tvWeiXin2:
                TextView weiXin2 = (TextView) findViewById(R.id.tvWeiXin2);
                copeTv(weiXin2);
                break;
        }
    }

    private void copeTv(TextView tv){
        String value = tv.getText().toString();

        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData mClip = ClipData.newPlainText("text", value);
        myClipboard.setPrimaryClip(mClip);
        Utils.Toast(this,"微信号已复制，请打开微信添加朋友");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
