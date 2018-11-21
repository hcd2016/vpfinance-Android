package cn.vpfinance.vpjr.module.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jewelcredit.util.HttpService;

import butterknife.Bind;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.more.MyQRcodeActivity;

import static android.content.Context.CLIPBOARD_SERVICE;

public class InviteGifeDialog extends TBaseDialog {
    private String shareUrl = "";
    private String imgUrl = "";
    private String msg = "";

    @Bind(R.id.qrCodeIv)
    ImageView qrCodeIv;
    @Bind(R.id.tv_wechat)
    TextView tvWechat;
    @Bind(R.id.tv_moments)
    TextView tvMoments;
    @Bind(R.id.tv_qq)
    TextView tvQq;
    @Bind(R.id.tv_sina)
    TextView tvSina;
    @Bind(R.id.tv_qzone)
    TextView tvQzone;
    @Bind(R.id.tv_email)
    TextView tvEmail;
    @Bind(R.id.tv_sms)
    TextView tvSms;
    @Bind(R.id.tv_copy)
    TextView tvCopy;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;

    public InviteGifeDialog(Context context) {
        super(context, R.layout.dialog_invite_gift);
        setWindowParam(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM, 0);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    public void setData(String shareUrl,String imgUrl,String msg) {
        if(!TextUtils.isEmpty(msg)) {
            this.shareUrl = shareUrl + msg;
        }else {
            this.shareUrl = shareUrl;
        }
        this.imgUrl  = imgUrl;
        this.msg = msg;
        if(!TextUtils.isEmpty(imgUrl)) {
            Glide.with(mContext).load(imgUrl).into(qrCodeIv);
        }
    }

    @OnClick({R.id.tv_wechat, R.id.tv_moments, R.id.tv_qq, R.id.tv_sina, R.id.tv_qzone, R.id.tv_email, R.id.tv_sms, R.id.tv_copy, R.id.tv_cancel})
    public void onViewClicked(View view) {
        String platform = "";
        switch (view.getId()) {
            case R.id.tv_wechat:
                platform = Wechat.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.tv_moments:
                platform = WechatMoments.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.tv_qq:
                platform = QQ.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.tv_sina:
                platform = SinaWeibo.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.tv_qzone:
                platform = QZone.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.tv_email:
                platform = Email.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.tv_sms:
                platform = ShortMessage.NAME;
                showShare2Platform(platform, shareUrl);
                break;
            case R.id.tv_copy:
                ClipboardManager myClipboard = (ClipboardManager)mContext.getSystemService(CLIPBOARD_SERVICE);
                ClipData mClip = ClipData.newPlainText("text", shareUrl);
                myClipboard.setPrimaryClip(mClip);
                Toast.makeText(mContext, "已经复制到剪切版", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    public class ShareContentCustomizeDemo implements ShareContentCustomizeCallback {

        public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
            paramsToShare.setText(shareUrl);
            if("SinaWeibo".equals(platform.getName())){
                paramsToShare.setTitle(mContext.getResources().getString(R.string.share));
            }else if("QZone".equals(platform.getName())){
                paramsToShare.setTitle(mContext.getResources().getString(R.string.share));
                paramsToShare.setTitleUrl(HttpService.mBaseUrl);
                paramsToShare.setSite(mContext.getResources().getString(R.string.app_name));
                paramsToShare.setSiteUrl(HttpService.mBaseUrl);
            }else if ("Wechat".equals(platform.getName()) || "WechatMoments".equals(platform.getName())){
                paramsToShare.setTitle("微品金融-足不出户实现财富增值");
                paramsToShare.setUrl(shareUrl);
                paramsToShare.setImageData(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.logo));
            }else if("Email".equals(platform.getName()) || "ShortMessage".equals(platform.getName())){
                paramsToShare.setTitle(mContext.getResources().getString(R.string.share));
            }else if("QQ".equals(platform.getName())) {
                paramsToShare.setTitleUrl(HttpService.mBaseUrl);
                paramsToShare.setTitle("微品金融-足不出户实现财富增值");
                paramsToShare.setImageData(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.logo));
            }
        }
    }

    private void showShare2Platform(String platform,String text){

//        ShareSDK.initSDK(this);
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        //不同平台的分享参数，请看文档
//        oks.setText(text);
        oks.setText(shareUrl);
        //oks.setSilent(silent);
//        oks.setDialogMode(false);
//        oks.disableSSOWhenAuthorize();
        if (platform != null) {
            oks.setPlatform(platform);
        }
        oks.setUrl(shareUrl);
        // 去自定义不同平台的字段内容
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
        oks.show(mContext);
    }
}
