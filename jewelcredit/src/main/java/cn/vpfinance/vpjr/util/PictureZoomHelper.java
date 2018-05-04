package cn.vpfinance.vpjr.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;

import cn.vpfinance.android.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 */
public class PictureZoomHelper {

    private DisplayImageOptions mOptions;
    private HashMap<Integer,Bitmap> bitmaps = new HashMap<>();

    public void showPicture(Context context, Bitmap bitmap){
        final Dialog reNameDialog = new Dialog(context, R.style.VPDialog);
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = mLayoutInflater.inflate(R.layout.dialog_picture_zoom, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        reNameDialog.addContentView(dialogView, params);
        reNameDialog.show();
        final PhotoView pictureZoom = (PhotoView) reNameDialog.getWindow().findViewById(R.id.picturezoom);
        pictureZoom.setImageBitmap(bitmap);
        pictureZoom.setOnSingleFlingListener(new PhotoViewAttacher.OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                Logger.e("velocityX: "+velocityX);
                //<--  -5000
                int temp = curImg;
                if (velocityX < -3000){
                    temp = temp + 1;
                }else if(velocityX > 3000){
                    temp = temp - 1;
                }
                boolean b = bitmaps.containsKey(temp);
                if (b){
                    curImg = temp;
//                    Logger.e("curImg:"+curImg);
                    Bitmap bitmap = bitmaps.get(curImg);
                    pictureZoom.setImageBitmap(bitmap);
                }
                return false;
            }
        });
        pictureZoom.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
            }

            @Override
            public void onOutsidePhotoTap() {
                reNameDialog.dismiss();
            }
        });
    }

    public void showPicture(Context context, Drawable drawable){
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        showPicture(context,bitmap);
    }

    private int curImg;
    public void showPicture(final Context context, String imageUrl,ArrayList<String> images){
        if (images != null){
            for (int i=0; i<images.size(); i++) {
                String image = images.get(i);
                if (imageUrl.equals(image)){
                    curImg = i;
//                Logger.e("curImg:"+curImg);
                }
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(image);
                bitmaps.put(i,bitmap);
            }
        }

        ImageLoader.getInstance().loadImage(imageUrl, getDisplayImgOptions(), new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (null != bitmap) {
                    showPicture(context,bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public DisplayImageOptions getDisplayImgOptions() {

        if(mOptions != null) {
            return mOptions;
        }

        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_loading_vertical)
                .showImageForEmptyUri(R.drawable.img_load_error_vertical)
                .showImageOnFail(R.drawable.img_load_error_vertical)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        return mOptions;
    }
}
