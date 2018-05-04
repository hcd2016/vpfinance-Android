package cn.vpfinance.vpjr.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.jewelcredit.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zzlz13 on 2018/1/5.
 */

public class CameraGalleryUtils {

    private static final String TAG = "CameraGalleryUtils";

    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int CLIP_REQUEST_CODE = 103;
    public static final int GALLERY_KITKAT_REQUEST_CODE = 200;
    public static final int GALLERY_REQUEST_CODE = 201;

    public static final int PERMISSIONS_REQUEST_CAMERA = 200;
    public static final int PERMISSIONS_REQUEST_GALLERY = 201;

    public static Activity mActivity;
    public static File mFile;

    public static void showCameraGalleryByClip(final Activity activity, final String saveName) {
        new AlertDialog.Builder(activity)
                .setMessage("")
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        galleryAndClip(activity, getSaveFile(saveName));

                    }
                })
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cameraAndClip(activity, getSaveFile(saveName));
                    }
                })
                .setNeutralButton("取消", null)
                .show();
    }

    /*public static void gallery(Activity activity, File mGalleryFile) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image*//*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果大于等于7.0使用FileProvider
            Uri uriForFile = FileProvider.getUriForFile(activity, AUTHORITY, mGalleryFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivityForResult(intent, GALLERY_KITKAT_REQUEST_CODE);
        } else {
            activity.startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }*/

    public static void galleryAndClipByPermission(Activity activity, File mGalleryFile) {
        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_GALLERY);
        }else{
            galleryAndClip(activity,mGalleryFile);
        }
    }

    public static void galleryAndClip(Activity activity, File mGalleryFile) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果大于等于7.0使用FileProvider
            Uri uriForFile = FileProvider.getUriForFile(activity, getAuthority(activity), mGalleryFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivityForResult(intent, GALLERY_KITKAT_REQUEST_CODE);
        } else {
            activity.startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }

    public static String getAuthority(Context context){
        return context.getPackageName() + ".fileProvider";
    }

    /*public static void camera(Activity activity, File file) {
        Uri imageUri = FileProvider.getUriForFile(activity, AUTHORITY, file);//通过FileProvider创建一个content类型的Uri
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }*/


    public static void cameraAndClipByPermission(Activity activity, File mCameraFile) {

        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CAMERA);
        }else {
            cameraAndClip(activity,mCameraFile);
        }
    }

    public static void cameraAndClip(Activity activity, File mCameraFile) {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
            Uri uriForFile = FileProvider.getUriForFile(activity, getAuthority(activity), mCameraFile);
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentFromCapture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
        }
        activity.startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
    }

    public static void startPhotoZoom(Activity activity, Uri inputUri, String saveName) {
        if (inputUri == null) {
            Log.e(TAG, "The uri is not exist.");
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Uri outPutUri = Uri.fromFile(getSaveFile(saveName));
            intent.setDataAndType(inputUri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
            intent.putExtra("noFaceDetection", false);//去除默认的人脸识别，否则和剪裁匡重叠
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            Uri outPutUri = Uri.fromFile(getSaveFile(saveName));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String url = GetImagePath.getPath(activity, inputUri);//这个方法是处理4.4以上图片返回的Uri对象不同的处理方法
                intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            } else {
                intent.setDataAndType(inputUri, "image/*");
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        }

        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);

        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
        activity.startActivityForResult(intent, CLIP_REQUEST_CODE);//这里就将裁剪后的图片的Uri返回了
    }

    public static void onCameraActivityResult(Activity activity, String saveName) {
        File file = getSaveFile(saveName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri inputUri = FileProvider.getUriForFile(activity, getAuthority(activity), file);//通过FileProvider创建一个content类型的Uri
            startPhotoZoom(activity, inputUri, saveName);//设置输入类型
        } else {
            Uri inputUri = Uri.fromFile(file);
            startPhotoZoom(activity, inputUri, saveName);
        }
    }

    /*public static Bitmap onCameraNoClipActivityResult(Activity activity, String saveName) {
        File file = getSaveFile(saveName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Uri inputUri = FileProvider.getUriForFile(activity, AUTHORITY, file);//通过FileProvider创建一个content类型的Uri
//            String filePath = getRealFilePath(activity, inputUri);

            Bitmap sourceBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Bitmap bitmap = compress(sourceBitmap);
            saveBitmap(bitmap, saveName);
            return bitmap;
        } else {
//            Uri inputUri = Uri.fromFile(file);
//            String filePath = getRealFilePath(activity, inputUri);

            Bitmap sourceBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Bitmap bitmap = compress(sourceBitmap);
            saveBitmap(bitmap, saveName);
            return bitmap;
        }
    }*/

    /*public static Bitmap onGalleryKitkatNoClipActivityResult(Activity activity, Intent data, String saveName) {
        if (data != null) {
            File file = new File(GetImagePath.getPath(activity, data.getData()));
//            Uri uri = FileProvider.getUriForFile(activity, CameraGalleryUtils.AUTHORITY, file);
//            String filePath = getRealFilePath(activity,uri);

            if (file == null) {
                Utils.Toast("未找到图片");
                return null;
            }
            Bitmap sourceBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (sourceBitmap == null) {
                Utils.Toast("未找到图片");
                return null;
            }
            Bitmap bitmap = compress(sourceBitmap);
            saveBitmap(bitmap, saveName);
            return bitmap;
        }
        return null;
    }*/

    /*public static Bitmap onGalleryNoClipActivityResult(Activity activity, Intent data, String saveName) {
        if (data != null) {
            String filePath = getRealFilePath(activity, data.getData());

            Bitmap sourceBitmap = BitmapFactory.decodeFile(filePath);
            Bitmap bitmap = compress(sourceBitmap);
            saveBitmap(bitmap, saveName);
            return bitmap;
        }
        return null;
    }*/


    public static void onGalleryKitkatActivityResult(Activity activity, Intent data, String saveName) {
        if (data != null) {
            File file = new File(GetImagePath.getPath(activity, data.getData()));
            Uri uri = FileProvider.getUriForFile(activity, CameraGalleryUtils.getAuthority(activity), file);
            startPhotoZoom(activity, uri, saveName);
        }
    }

    public static void onGalleryActivityResult(Activity activity, Intent data, String saveName) {
        if (data != null) {
            startPhotoZoom(activity, data.getData(), saveName);
        }
    }

    public static final File getSaveDir() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/VPFinance");
        if (!(file.exists() && file.isDirectory())) {
            file.mkdirs();
        }
        return file;
    }

    public static final File getSaveFile(String fileName) {
        return new File(getSaveDir().getAbsolutePath() + "/" + fileName);
    }

    /*public static Bitmap compress(Bitmap image) {
        long startTime = System.currentTimeMillis();
        Float pixelW = 345 * 2F;
        Float pixelH = 191 * 2F;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int quailty = 100;
        image.compress(Bitmap.CompressFormat.JPEG, quailty, os);
        *//*int originalSize = os.toByteArray().length / 1024; //kb
        if (originalSize > 1024 *2){
            quailty = 60;
            os.reset();
            image.compress(Bitmap.CompressFormat.JPEG, quailty, os);
        }else{
            quailty = 100;
            os.reset();
            image.compress(Bitmap.CompressFormat.JPEG, quailty, os);
        }*//*
        *//*while (os.toByteArray().length / 1024 > 1024*2 && quailty > 10){
            os.reset();
            quailty -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, quailty, os);
        }*//*
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);

        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1; //不缩放
        if (w > h && w > pixelW) {
            be = (int) (w / pixelW);
        } else if (w < h && h > pixelH) {
            be = (int) (h / pixelH);
        }
        options.inSampleSize = be;// 设置比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, options);
        Log.i(TAG,"图片: " + (bitmap.getByteCount() / 1024.0) + "KB");
        Log.i(TAG,"compress Time: " + (System.currentTimeMillis() - startTime));
        return bitmap;
    }*/

    public static void saveBitmap(Bitmap bitmap, String fileName) {
        long startTime = System.currentTimeMillis();
        File file = getSaveFile(fileName);
        try {
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            Log.i(TAG,"saveBitmap Time: " + (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRealFilePath(Context context, Uri uri) {
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /*public static String getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }*/
}
