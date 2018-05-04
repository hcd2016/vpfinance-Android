package com.jewelcredit.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.module.common.WebViewActivity;


public class Utils {

    public static String TAG = "JEWEL_CREDIT";
    private static String SHARED_PREFERENCES_NAME = TAG;
    //private static SharedPreferences mShareConfig;
    private static DisplayImageOptions mOptions;

    private static String rsa_pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCC7KyYEoNUYFIX2cZn4eNf ll5CBnZ/NzctLDuzZ3qPayruBfUG2VR8MlUiXfv7b58bUJkXlN4eHz+ebv2b pAvNXf8ielWGIKpG7BpiH4PP1X3rmLNnUpEd6rZ8ApBb2apNapLakx/kOYk+ rqQC5kL7+fxsymLTgKVckxOCJ/MRtQIDAQAB";

    private static Map<String, String> mParamMap = new HashMap<String, String>();


    public static void setParam(String key, String value) {
        mParamMap.put(key, value);
    }

    public static String getParam(String key) {
        if (!mParamMap.containsKey(key))
            return "";

        return mParamMap.get(key);
    }

    public static void copyAssetsFileToPath(Context context, String fileName, String outPath) throws IOException {
        if (context == null) {
            return;
        }
        OutputStream myOutput = new FileOutputStream(outPath);
        InputStream myInput = context.getAssets().open(fileName);
        byte[] buffer = new byte[4096];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    private static Toast toast = null;

    public static void Toast(Context context, String text) {
//		if (toast == null) {
//			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//		} else {
//			toast.setText(text);
//		}
//		toast.show();
        Toast(text);
    }

    public static void Toast(String text) {
        if (toast == null) {
            toast = Toast.makeText(FinanceApplication.getContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }


//	public static void Toast(Context context, int strId)
//	{
//		Toast.makeText(context, strId, Toast.LENGTH_SHORT).show();
//	}

    /**
     * double向下取整保留两位小数
     */
    public static double doubleFloor(double value) {
        if (value != 0) {
            value *= 100;
            return Math.floor(value) / 100;
        } else {
            return 0;
        }
    }

    public static void doRsa(Context context, String text, JsCallback callback) {
		/*
		try
		{
			byte[] keyBytes = Base64.decode(rsa_pub_key, Base64.DEFAULT);
			  
	        // 取得公钥     
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);     
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");     
	        RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(x509KeySpec);     
	  
	        // 对数据加密     
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());     
	        cipher.init(Cipher.ENCRYPT_MODE, publicKey);     
	  
	        byte[] bytes = cipher.doFinal(data.getBytes());
	        return bytes2HexString(bytes);
		}
		catch(Exception e)
		{
			Utils.log(e.getMessage());
		}
		*/


        JsEvaluator jsEvaluator = new JsEvaluator(context);

        String script = "";

        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(context.getAssets().open("jsencrypt.min.js"), "UTF-8"));
            String line;
            while ((line = bf.readLine()) != null) {
                script += line;
            }
        } catch (IOException e) {
            Utils.log(e.getMessage());
        }

        script += "\n";
        script += "function encrypt(key, content) {\n";
        script += "var o = new JSEncrypt();\n";
        script += "o.setPublicKey(key);\n";
        script += "return o.encrypt(content);\n ";
        script += "}\n";


        jsEvaluator.callFunction(script, callback, "encrypt", rsa_pub_key, text);
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static void addIndicator(Context context, LinearLayout layout, int index, int count, int norImage, int curImage) {
        if (count <= 1)
            return;

        layout.removeAllViews();

        int i = 0;
        while (i < count) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utils.dip2px(context, 7.0F), Utils.dip2px(context, 7.0F));
            layoutParams.rightMargin = 10;
            layoutParams.leftMargin = 10;
            imageView.setLayoutParams(layoutParams);


            if (i == index)
                imageView.setImageDrawable(context.getResources().getDrawable(curImage));
            else
                imageView.setImageDrawable(context.getResources().getDrawable(norImage));

            layout.addView(imageView);
            i++;
        }
    }


    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return (networkInfo != null) && (networkInfo.isConnectedOrConnecting());
    }


    /**
     * 签名源串是由除signMsg字段以外的所有非空字段内容按照报文字段的先后顺序依次按照“字段名=字段值”的方式用“&”符号连接。
     * <p>
     * 注意事项：
     * a.只有字段值为非空的字段内容才按上述格式放到签名字符串中参与签名;
     * b.签名字符串头部与尾部需要连接字符串“&”;
     * c.如果有字段内容为汉字，则必须按照指定的或者默认的字符集编码;
     * d.参数需遵循文档中的顺序。
     *
     * @param buffer
     * @return
     * @throws SecurityException
     */
    public static String md5encode(String data) {
        String resultString = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = bytes2HexString(md.digest(data.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return resultString;
    }


    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean netSataus = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null) {
            netSataus = netinfo.isAvailable();
        }
        return netSataus;
        //这样你就可以通过NetworkInfo 来获取网络设备信息了，如：
//		netinfo.isAvailable();//网络是否可用
//		netinfo.getDetailedState();
//		netinfo.isConnected();
//		return true;
    }


    public static String buildGetUrl(Map<String, String> params, boolean needSign) {
        if (params != null && params.size() > 0) {
            StringBuffer buffer = new StringBuffer("?");
            Iterator iter = params.entrySet().iterator();
            while (true) {
                if (!iter.hasNext()) {
                    int index = buffer.lastIndexOf("&");
                    if (index >= 0)
                        buffer.deleteCharAt(index);

                    String md5 = "";

                    if (needSign) {
                        if (buffer.toString().equals(""))
                            md5 = "";
                        else
                            md5 = md5encode(buffer.toString());
                    }

                    if (!md5.equals(""))
                        buffer.append("&signMsg").append("=").append(md5);
                    return buffer.toString();
                }

                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();

                // md5 sign的时候，不能传空参数
                boolean flag = !needSign;
                if (needSign && !value.equals(""))
                    flag = true;

                if (flag)
                    buffer.append(name).append("=").append(value).append("&");
            }
        }

        return "";
    }


    public static void goToWeb(Context context, String url, String title) {
        if (!url.contains("http:") && !url.contains("https")) {
            url = HttpService.mBaseUrl + url;
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.KEY_URL, url);
        intent.putExtra(WebViewActivity.KEY_TITLE, title);
        context.startActivity(intent);
    }


    public static void log(String str) {
        Log.v(Utils.TAG, str);
    }


    public static JSONObject loadJson(Context context, String filename) {
        String content = "";
        JSONObject json = null;


        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(context.getAssets().open(filename), "UTF-8"));
            String line;
            while ((line = bf.readLine()) != null) {
                content += line;
            }

            json = new JSONObject(content);
        } catch (IOException e) {
            Utils.log(e.getMessage());
        } catch (JSONException e) {
            Utils.log(e.getMessage());
        }

        return json;
    }


    public static DisplayImageOptions getDisplayImgOptions() {
        if (mOptions != null) {
            return mOptions;
        }

//		mOptions = new DisplayImageOptions.Builder()
//				.showStubImage(R.drawable.stub_image).cacheInMemory(true)
//				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.img_loading)
                .showImageOnLoading(R.drawable.img_loading)
                .showImageForEmptyUri(R.drawable.img_load_failure)
                .showImageOnFail(R.drawable.img_load_failure)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Config.RGB_565)
                .build();

        return mOptions;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    public static String getVersion(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (Exception e) {

        }

        return versionName;
    }

    /**
     * 根据毫秒值格式化成时间 格式到分
     *
     * @param value
     * @return
     */
    public static String getDate_M(long value) {
        if (value == 0) return "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = sdf.format(new Date(value));
        return date;
    }

    /**
     * 根据毫秒值格式化成时间
     *
     * @param value
     * @return
     */
    public static String getDate(long value) {
        if (value == 0) return "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date(value));
        return date;
    }

    /**
     * 根据时间（字符串格式）比较大小
     *
     * @param s1 开始时间
     * @param s2 结束时间
     * @return
     */
    public static boolean compareDate(String s1, String s2) {
        boolean flag = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            Date d1 = simpleDateFormat.parse(s1);
            Date d2 = simpleDateFormat.parse(s2);
            if (d1.getTime() > d2.getTime()) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 获取现在时间到之前两个月的时间
     *
     * @return List<String>：beginTime,endTime
     */
    public static List<String> getCurrentDate() {
        ArrayList<String> date = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String beginTime = simpleDateFormat.format(new Date());

        String[] split = beginTime.split("-");
        String year = split[0];
        String month = split[1];
        if ("01".equals(split[1])) {
            year = "" + (Integer.parseInt(year) - 1);
            month = "11";
        }
        if ("02".equals(split[1])) {
            year = "" + (Integer.parseInt(year) - 1);
            month = "12";
        } else {
            month = (Integer.parseInt(month) - 2) > 9 ? "" + (Integer.parseInt(month) - 2) : "0" + (Integer.parseInt(month) - 2);
        }
        String endTime = year + "-" + month + "-" + "01";

        date.add(endTime);
        date.add(beginTime);
        return date;
    }

    /**
     * 弹出dialog选择时间
     *
     * @param tv       选择的时间显示在et里面
     * @param mContext
     * @return DatePickerDialog
     */
    public static DatePickerDialog showSelectDialog(final EditText tv, Context mContext) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        DatePickerDialog.OnDateSetListener dateListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker,
                                          int year, int month, int dayOfMonth) {
                        //Calendar月份是从0开始,所以month要加1
                        tv.setText(year + "-" +
                                (month + 1) + "-" + dayOfMonth + "");
                    }
                };
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        return datePickerDialog;
    }


    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }


    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1 == null || version2 == null) {
            throw new IllegalArgumentException("compareVersion error:illegal params.");
        }
        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用.；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
		/*Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号

		m = p.matcher(str);
		b = m.matches();*/
        if (!TextUtils.isEmpty(str) && str.length() == 11) {
            return true;
        }
        return false;
    }

    public static String getNetGeneration(Context context) {
        String ret = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                //Log.d("Type", "3g");
                // for 3g HSDPA networktype will be return as
                // per testing(real) in device with 3g enable
                // data
                // and speed will also matters to decide 3g network type
                ret = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                //Log.d("Type", "4g");
                // No specification for the 4g but from wiki
                // i found(HSPAP used in 4g)
                // http://goo.gl/bhtVT
                ret = "4G";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                //Log.d("Type", "GPRS");
                //break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                //Log.d("Type", "EDGE 2g");
                ret = "2G";
                break;
            // case TelephonyManager.NETWORK_TYPE_CDMA:
            // break;
            // case TelephonyManager.NETWORK_TYPE_EVDO_A:
            // break;
            // case TelephonyManager.NETWORK_TYPE_EVDO_B:
            // break;
            // case TelephonyManager.NETWORK_TYPE_EVDO_0:
            // break;
        }
        if (ret != null) {
            Log.e("TEST", ret);
        }

        return ret;
    }

    /**
     * @param str       总文字
     * @param targetStr 目标文字
     * @param color     颜色
     * @param tv        view
     */
    public static void setTwoTextColor(String str, String targetStr, int color, TextView tv) {
        try {
            int fstart = str.indexOf(targetStr);
            int fend = fstart + targetStr.length();
            SpannableStringBuilder style = new SpannableStringBuilder(str);
            style.setSpan(new ForegroundColorSpan(color), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tv.setText(style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTwoTextSize(String str, String targetStr, int size, TextView tv) {
        int fstart = str.indexOf(targetStr);
        int fend = fstart + targetStr.length();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new AbsoluteSizeSpan(size, true), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv.setText(style);
    }
}


