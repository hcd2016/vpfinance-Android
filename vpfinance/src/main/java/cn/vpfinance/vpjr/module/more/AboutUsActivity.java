package cn.vpfinance.vpjr.module.more;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;


public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

//    private TextView phone1;
//    private TextView phone2;
//    private TextView name1;
//    private TextView name2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("关于我们").setHeadBackVisible(View.VISIBLE);
        ((TextView)findViewById(R.id.currVer)).setText("微品金融 V" + Utils.getVersion(this));

//        findViewById(R.id.clickPhone).setOnClickListener(this);
        findViewById(R.id.clickVP).setOnClickListener(this);
        findViewById(R.id.clickAttent).setOnClickListener(this);
        findViewById(R.id.ll_company_profile).setOnClickListener(this);
        findViewById(R.id.ll_company_phone).setOnClickListener(this);

//        phone1 = (TextView) findViewById(R.id.phone1);
//        phone2 = (TextView) findViewById(R.id.phone2);
//        name1 = (TextView) findViewById(R.id.name1);
//        name2 = (TextView) findViewById(R.id.name2);
//
//        setLink(phone1);
//        setLink(phone2);
//        setLink(name1);
//        setLink(name2);
    }

    private void setLink(TextView tv){
        tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tv.getPaint().setAntiAlias(true);//抗锯齿
        tv.setOnClickListener(this);
    }
    private void copeTv(TextView tv){
        String value = tv.getText().toString();

        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData mClip = ClipData.newPlainText("text", value);
        myClipboard.setPrimaryClip(mClip);
        Utils.Toast(this,"微信号已复制，请打开微信添加朋友");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clickVP:
//                gotoWeb("http://www.vpfinance.cn","微品金融");
                Uri uri = Uri.parse(HttpService.mBaseUrl);
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(uri);
                startActivity(i);
                break;
//            case R.id.clickPhone:
//                Intent intent=new Intent("android.intent.action.DIAL", Uri.parse("tel:0755-86627551"));
//                startActivity(intent);
//                break;
            case R.id.clickAttent:
                gotoActivity(AddWeiXinActivity.class);
                break;
            case R.id.ll_company_profile://公司简介
                //todo
                break;
            case R.id.ll_company_phone://客服热线
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "0755-86627551"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
//            case R.id.phone2:
//                copeTv(phone2);
//                break;
//            case R.id.name1:
//                copeTv(name1);
//                break;
//            case R.id.name2:
//                copeTv(name2);
//                break;
        }
    }
}
