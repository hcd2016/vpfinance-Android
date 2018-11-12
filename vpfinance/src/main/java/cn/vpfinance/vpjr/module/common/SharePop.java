package cn.vpfinance.vpjr.module.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.vpfinance.android.R;

/**
 * Created by Administrator on 2016/11/25.
 */
public class SharePop extends PopupWindow {


    public static final String SHARE_URL   = "share_Url";
    public static final String IMAGE_URL   = "image_Url";
    public static final String SHARE_MSG   = "share_msg";
    public static final String SHARE_TITLE = "share_title";
    @Bind(R.id.clickWeiXin)
    LinearLayout mClickWeiXin;
    @Bind(R.id.clickFriend)
    LinearLayout mClickFriend;
    @Bind(R.id.clickQQ)
    LinearLayout mClickQQ;
    @Bind(R.id.clickWeiBo)
    LinearLayout mClickWeiBo;
    @Bind(R.id.tvCancel)
    TextView     mTvCancel;
    private Context mContext;
    private String  mShareUrl;
    private String  mImageUrl;
    private String  mMsg;
    private String  mTitle;
    private final View mView;

    public SharePop(Context context, String shareUrl, String imageUrl, String msg, String title) {
        super(context);
        mView = LayoutInflater.from(context).inflate(R.layout.share_dialog, null);
        setContentView(mView);
        ButterKnife.bind(this, mView);
        mContext = context;
        mShareUrl = shareUrl;
        mImageUrl = imageUrl;
        mMsg = msg;
        mTitle = title;

//        Log.d("aa", "SharePop: mShareUrl: "+ mShareUrl +"\n" + "mImageUrl:　"
//                + mImageUrl+ "\n" +"mMsg: "+mMsg +"\n" + "mTitle: " +mTitle);

        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = mView.findViewById(R.id.pop_top).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    @OnClick({R.id.clickWeiXin, R.id.clickFriend, R.id.clickQQ, R.id.clickWeiBo, R.id.tvCancel})
    public void onClick(View view) {
        String platform = "";
        switch (view.getId()) {
            case R.id.clickWeiXin:
                platform = Wechat.NAME;
                showShare2Platform(platform, mShareUrl);
                dismiss();
                break;
            case R.id.clickFriend:
                platform = WechatMoments.NAME;
                showShare2Platform(platform, mShareUrl);
                dismiss();
                break;
            case R.id.clickQQ:
                platform = QQ.NAME;
                showShare2Platform(platform, mShareUrl);
                dismiss();
                break;
            case R.id.clickWeiBo:
                platform = SinaWeibo.NAME;
                showShare2Platform(platform, mShareUrl);
                dismiss();
                break;
            case R.id.tvCancel:
                dismiss();
                break;
        }
    }

    private void showShare2Platform(String platform, String link) {

        if (TextUtils.isEmpty(link))
            return;

//        ShareSDK.initSDK(mContext);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        //不同平台的分享参数，请看文档
        oks.setText(mMsg);
        oks.setTitle(mTitle);
        oks.setTitleUrl(link);
        oks.setUrl(link);
        oks.setSite(mTitle);
        oks.setSiteUrl(link);
        //oks.setSilent(silent);
//        oks.setDialogMode();
        if(!TextUtils.isEmpty(mImageUrl))
        {
            oks.setImagePath(mImageUrl);
        }
        oks.disableSSOWhenAuthorize();
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // 去自定义不同平台的字段内容
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
        oks.show(mContext);
    }



    public class ShareContentCustomizeDemo implements ShareContentCustomizeCallback {

        public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
            paramsToShare.setText(mShareUrl);
            if ("SinaWeibo".equals(platform.getName())) {
                paramsToShare.setText(mMsg + " " + mShareUrl);
            } else if ("QZone".equals(platform.getName())) {
                paramsToShare.setTitle(mTitle);
                paramsToShare.setTitleUrl(mShareUrl);
                paramsToShare.setSite(mTitle);
                paramsToShare.setSiteUrl(mShareUrl);
            } else if ("Wechat".equals(platform.getName()) || "WechatMoments".equals(platform.getName())) {
                paramsToShare.setUrl(mShareUrl);
                paramsToShare.setTitle(mTitle);
                paramsToShare.setText(mMsg);
            } else if ("Email".equals(platform.getName()) || "ShortMessage".equals(platform.getName())) {
                paramsToShare.setTitle(mTitle);
            }else if ("qq".equals(platform.getName())) {
                paramsToShare.setUrl(mShareUrl);
                paramsToShare.setTitle(mTitle);
                paramsToShare.setText(mMsg);
            }
        }
    }


}
