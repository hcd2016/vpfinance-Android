package cn.vpfinance.vpjr.util;

import android.util.Log;

/**
 */
public final class Logger {

    private static final String MY_TAG = "vp_";
    private Logger() {
    }
    //项目上线一定要把开关  关闭
    private static boolean flag = false;

    public static void v(String msg){
        if(!flag){
            Log.i(MY_TAG, msg);
        }
    }
    public static void d(String msg){
        if(!flag){
            Log.d(MY_TAG, msg);
        }
    }
    public static void i(String msg){
        if(!flag){
            Log.i(MY_TAG, msg);
        }
    }
    public static void w(String msg){
        if(!flag){
            Log.w(MY_TAG, msg);
        }
    }
    public static void e(String msg){
        if(!flag){
            Log.e(MY_TAG, msg);
        }
    }

    public static void v(String tag,String msg){
        if(!flag){
            Log.i(tag, msg);
        }
    }
    public static void d(String tag,String msg){
        if(!flag){
            Log.d(tag, msg);
        }
    }
    public static void i(String tag,String msg){
        if(!flag){
            Log.i(tag, msg);
        }
    }
    public static void w(String tag,String msg){
        if(!flag){
            Log.w(tag, msg);
        }
    }
    public static void e(String tag,String msg){
        if(!flag){
            Log.e(tag, msg);
        }
    }

}
