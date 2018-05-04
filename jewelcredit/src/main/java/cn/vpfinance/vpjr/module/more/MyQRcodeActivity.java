package cn.vpfinance.vpjr.module.more;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONObject;

import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.PlatformListFakeActivity;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.util.Common;

//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.MultiFormatWriter;
//import com.google.zxing.WriterException;
//import com.google.zxing.common.BitMatrix;

/**
 * 我的二维码
 */
public class MyQRcodeActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "MyQRcodeActivity";

    public static final String KEY_SHAREURL = "key_shareUrl";
    public static final String KEY_IMAGEURL = "key_imageUrl";
    public static final String KEY_MSG = "key_msg";

    private HttpService mHttpService;

    ImageView qrCodeIv;
    DisplayImageOptions options;

    private String shareUrl;
    private String imageUrl;
    private String msg;

    public static void goQRCodeActivity(Context context, String shareUrl, String imageUrl, String msg) {
        L.e("msg:"+msg);
        if (context != null) {
            Intent intent = new Intent(context, MyQRcodeActivity.class);
            intent.putExtra(KEY_SHAREURL, shareUrl);
            intent.putExtra(KEY_IMAGEURL, imageUrl);
            intent.putExtra(KEY_MSG, msg);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr_cord);
        ActionBarLayout titlebar = (ActionBarLayout) findViewById(R.id.titleBar);
        titlebar.setTitle("我的二维码").setHeadBackVisible(View.VISIBLE);

        Intent intent = getIntent();
        if(intent!=null)
        {
            shareUrl = intent.getStringExtra(KEY_SHAREURL);
            imageUrl = intent.getStringExtra(KEY_IMAGEURL);
            msg = intent.getStringExtra(KEY_MSG);
        }
        shareUrl = msg + shareUrl;
//        findViewById(R.id.inviteFriends).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showShare(shareUrl, null, shareUrl);
//            }
//        });

        mHttpService = new HttpService(this, this);
        qrCodeIv = (ImageView)findViewById(R.id.qrCodeIv);

        mHttpService.getShareInfo();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_qrempty)
                .showImageForEmptyUri(R.drawable.bg_qrempty)
                .showImageOnFail(R.drawable.bg_qrempty)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

//        showShare(shareUrl, null, shareUrl);
        findViewById(R.id.clickWeiXin).setOnClickListener(this);
        findViewById(R.id.clickFriend).setOnClickListener(this);
        findViewById(R.id.clickQQ).setOnClickListener(this);
        findViewById(R.id.clickZone).setOnClickListener(this);
        findViewById(R.id.clickSina).setOnClickListener(this);
        findViewById(R.id.clickEmail).setOnClickListener(this);
        findViewById(R.id.clickMessage).setOnClickListener(this);
        findViewById(R.id.clickCopyLink).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String platform = "";
        switch (v.getId()){
            case R.id.clickWeiXin:
                platform = Wechat.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.clickFriend:
                platform = WechatMoments.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.clickQQ:
                platform = QQ.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.clickZone:
                platform = QZone.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.clickSina:
                platform = SinaWeibo.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.clickEmail:
                platform = Email.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.clickMessage:
                platform = ShortMessage.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.clickCopyLink:
                ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData mClip = ClipData.newPlainText("text", shareUrl);
                myClipboard.setPrimaryClip(mClip);
                Toast.makeText(MyQRcodeActivity.this, "已经复制到剪切版", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static class ShareInfo{
        public String shareUrl;
        public String imageUrl;
    }

    public static ShareInfo parseShareInfo(JSONObject json)
    {
        ShareInfo info = new ShareInfo();
        if(json!=null)
        {
            info.shareUrl = json.optString("shareUrl");
            info.imageUrl = json.optString("imageUrl");
        }
        return info;
    }
    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_GET_SHARE_URL.ordinal()) {
            ShareInfo info = parseShareInfo(json);
            if(info!=null)
            {
                shareUrl = TextUtils.isEmpty(shareUrl) ? info.shareUrl : shareUrl;
                imageUrl = info.imageUrl;
            }
//            L.e("imageUrl:"+imageUrl);

            if (!TextUtils.isEmpty(imageUrl)) {
                ImageLoader.getInstance().displayImage(imageUrl, qrCodeIv, options);
            }
            /*
            if (!TextUtils.isEmpty(shareUrl)) {
                Bitmap bitmap = null;
                try {
                    bitmap = encodeAsBitmap(shareUrl, BarcodeFormat.QR_CODE, 500, 500);
                    if(bitmap!=null)
                    {
                        Log.e(TAG,"bitmap :" + bitmap.getWidth() + "x" + bitmap.getHeight());
                        qrCodeIv.setImageBitmap(bitmap);
                    }
                    else {
                        Log.e(TAG,"bitmap == null");
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
            */

        }
    }

    /*
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            iae.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
    */

    private static String guessAppropriateEncoding(CharSequence contents) {
        return "UTF-8";
        // Very crude at the moment
//        for (int i = 0; i < contents.length(); i++) {
//            if (contents.charAt(i) > 0xFF) {
//                return "UTF-8";
//            }
//        }
//        return null;
    }
    private void showShare2Platform(String platform,String text){

        ShareSDK.initSDK(this);
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        //不同平台的分享参数，请看文档
        oks.setText(text);
        //oks.setSilent(silent);
        oks.setDialogMode();
        oks.disableSSOWhenAuthorize();
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // 去自定义不同平台的字段内容
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
        oks.show(this);
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
    }

    private void showShare(final String text, String imageUrl,String link) {
        ShareSDK.initSDK(this);
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(HttpService.mBaseUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        if(!TextUtils.isEmpty(imageUrl))
        {
            oks.setImagePath(imageUrl);
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(link);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(HttpService.mBaseUrl);

        // 启动分享GUI

        // 参考代码配置章节，设置分享参数
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_copy);
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData mClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(mClip);
                Toast.makeText(MyQRcodeActivity.this, "已经复制到剪切版", Toast.LENGTH_SHORT).show();
            }
        };
        oks.setCustomerLogo(logo,logo,"复制链接",listener);

        oks.setOnShareButtonClickListener(new PlatformListFakeActivity.OnShareButtonClickListener() {
            @Override
            public void onClick(View v, List<Object> checkPlatforms) {
            }
        });

        oks.show(this);
    }
    public class ShareContentCustomizeDemo implements ShareContentCustomizeCallback {

        public void onShare(Platform platform, ShareParams paramsToShare) {
            paramsToShare.setText(shareUrl);
            if("SinaWeibo".equals(platform.getName())){
                paramsToShare.setTitle(getString(R.string.share));
            }else if("QZone".equals(platform.getName())){
                paramsToShare.setTitle(getString(R.string.share));
                paramsToShare.setTitleUrl(HttpService.mBaseUrl);
                paramsToShare.setSite(getString(R.string.app_name));
                paramsToShare.setSiteUrl(HttpService.mBaseUrl);
            }else if ("Wechat".equals(platform.getName()) || "WechatMoments".equals(platform.getName())){
                paramsToShare.setUrl(shareUrl);
            }else if("Email".equals(platform.getName()) || "ShortMessage".equals(platform.getName())){
                paramsToShare.setTitle(getString(R.string.share));
            }
        }
    }

}
