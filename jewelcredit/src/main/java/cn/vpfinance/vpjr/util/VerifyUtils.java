package cn.vpfinance.vpjr.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzlz13 on 2015/12/31.
 */
public class VerifyUtils {
    /*用户名*/
    public static boolean checkUserName(Context context,String value){
        if (TextUtils.isEmpty(value)){
            return false;
        }
        Pattern pattern = Pattern.compile("^[\\w\\-－＿[０-９]\u4e00-\u9fa5\uFF21-\uFF3A\uFF41-\uFF5A]+$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    /*邮箱*/
    public static boolean checkMail(Context context,String value){
        if (TextUtils.isEmpty(value)){
            return false;
        }
        Pattern pattern = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    /*身份证*/
    public static boolean checkIdCard(Context context,String value){
        if (TextUtils.isEmpty(value)){
            return false;
        }
        Pattern pattern = Pattern.compile("^(\\d{14}|\\d{17})(\\d|[xX])$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
