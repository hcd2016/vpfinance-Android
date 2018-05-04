package cn.vpfinance.vpjr.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.greendao.BankCard;

/**
 * Created by Administrator on 2016/5/9.
 */
public class FileUtil {
    private static String localPath = "";

    /**
     * imageLoader根据网络地址保存图片
     * @param picurl
     * @return  返回本地地址
     */
    public static String netPath2LocalPath(final String picurl){
        localPath = "";
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.NONE)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        File file = null;
        DiskCache diskCache = imageLoader.getDiskCache();
        if (diskCache != null) {
            file = diskCache.get(picurl);
        }
        if(file!=null && file.exists())
        {
            localPath = file.getAbsolutePath();
        }else{
            imageLoader.loadImage(picurl, imageOptions ,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    File file = null;
                    DiskCache diskCache = imageLoader.getDiskCache();
                    if (diskCache != null) {
                        file = diskCache.get(picurl);
                    }
                    if (file != null && file.exists()) {
                        localPath = file.getAbsolutePath();
                    }
                }
                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }
        return localPath;
    }

    /**
     * 将Bitmap 图片保存到本地路径，并返回路径
     * @param c
     * @param fileName 文件名称
     * @param bitmap 图片
     * @return
     */
    public static String saveFile(Context c, String fileName, Bitmap bitmap) {
        return saveFile(c, "", fileName, bitmap);
    }

    public static String saveFile(Context c, String filePath, String fileName, Bitmap bitmap) {
        byte[] bytes = bitmapToBytes(bitmap);
        return saveFile(c, filePath, fileName, bytes);
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static String saveFile(Context c, String filePath, String fileName, byte[] bytes) {
        FileOutputStream fos = null;
        File file = null;

        try {
            file = new File(c.getFilesDir(),fileName);
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }

    public static int getBankLogo(BankCard bankCard){
        if (bankCard == null || TextUtils.isEmpty(bankCard.getAccountName()))   return -1;

        String name = bankCard.getBankname();
        if (name.contains("北京")){
            return R.drawable.bank_beijing;
        }else if (name.contains("工商")){
            return R.drawable.bank_gongshang;
        }else if (name.contains("光大")){
            return R.drawable.bank_guangda;
        }else if (name.contains("广发")){
            return R.drawable.bank_guangfa;
        }else if (name.contains("杭州")){
            return R.drawable.bank_hangzhou;
        }else if (name.contains("华夏")){
            return R.drawable.bank_huaxia;
        }else if (name.contains("汇付")){
            return R.drawable.bank_huifu;
        }else if (name.contains("建设")){
            return R.drawable.bank_jianshe;
        }else if (name.contains("交通")){
            return R.drawable.bank_jiaotong;
        }else if (name.contains("块钱")){
            return R.drawable.bank_kuaiqian;
        }else if (name.contains("民生")){
            return R.drawable.bank_minsheng;
        }else if (name.contains("南京")){
            return R.drawable.bank_nanjing;
        }else if (name.contains("宁波")){
            return R.drawable.bank_ningbo;
        }else if (name.contains("农业")){
            return R.drawable.bank_nongye;
        }else if (name.contains("平安")){
            return R.drawable.bank_pingan;
        }else if (name.contains("浦发")){
            return R.drawable.bank_pufa;
        }else if (name.contains("上海")){
            return R.drawable.bank_shanghai;
        }else if (name.contains("财付通")){
            return R.drawable.bank_tenpay;
        }else if (name.contains("兴业")){
            return R.drawable.bank_xingye;
        }else if (name.contains("易宝")){
            return R.drawable.bank_yibao;
        }else if (name.contains("邮政")){
            return R.drawable.bank_youzheng;
        }else if (name.contains("招商")){
            return R.drawable.bank_zhaoshang;
        }else if (name.contains("中国")){
            return R.drawable.bank_zhongguo;
        }else if (name.contains("中信")){
            return R.drawable.bank_zhongxin;
        }else{
            return -1;
        }
    }
}
