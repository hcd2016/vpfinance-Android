package cn.vpfinance.vpjr.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

public class GlideUtil {

    private static GlideUtil mInstance;

    private GlideUtil() {
    }

    public static GlideUtil getInstance() {
        if (mInstance == null) {
            synchronized (GlideUtil.class) {
                if (mInstance == null) {
                    mInstance = new GlideUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     *      * 加载圆角图片
     *      *
     *      * @param context
     *      * @param url
     *      * @param imageView
     *      
     */
    public void loadRoundImage(final Context context, String url, int placeholder,
                                      final int radius, final ImageView imageView) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);

                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(radius); //设置圆角弧度
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

}
