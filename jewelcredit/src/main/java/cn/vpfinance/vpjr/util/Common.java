package cn.vpfinance.vpjr.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.jewelcredit.util.AppState;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;

/**
 * Created by zzlz13 on 2017/1/19.
 * 文案或者图片一些统一的显示的时候抽取到里面
 */
public class Common {
    private static boolean isShowing = false;

    /**
     * 是否强制退出
     *
     * @param context
     * @param json
     * @return true 强制退出
     */
    public static boolean isForceLogout(final Context context, JSONObject json) {
        //success false: 没有登录或者登录被占线
        boolean success = json.optBoolean("success", true);
        if (!success) {
            int loginStatus = json.optInt("loginStatus", -1);
//            Logger.e("loginStatus:" + loginStatus + "--->json:" + json.toString());
            if (loginStatus == 0) {
                if (!isShowing) {
                    isShowing = true;
                    String message = json.optString("message");
                    new AlertDialog.Builder(context)
                            .setMessage(message)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppState.instance().logout();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra(MainActivity.SWITCH_TAB_NUM, 0);
                                    context.startActivity(intent);
                                    isShowing = false;
                                }
                            })
                            .create().show();
                }
            } else if (loginStatus == 2) {
                if (!isShowing) {
                    isShowing = true;
                    String message = json.optString("message");
                    new AlertDialog.Builder(context)
                            .setMessage(message)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppState.instance().logout();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra(MainActivity.SWITCH_TAB_NUM, 0);
                                    context.startActivity(intent);
                                    isShowing = false;
                                }
                            }).create().show();
                }
            }
        }
        return !success;
    }

    public static String isPasswordPass(String password){
        int hasCapital = 0;
        int hasSmall = 0;
        int hasNumber = 0;
        int hasSymbol = 0;
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) >= 'A' && password.charAt(i) <= 'Z') {
                hasCapital = 1;
            } else if (password.charAt(i) >= 'a' && password.charAt(i) <= 'z') {
                hasSmall = 1;
            } else if (password.charAt(i) >= 48 && password.charAt(i) <= 57) {//0-9的ascii码是48-57
                hasNumber = 1;
            } else {//特殊字符
                hasSymbol = 1;
            }
            if ((hasCapital + hasNumber + hasSmall + hasSymbol) >= 2){
                return "";
            }
        }
        if (hasCapital == 1) {
            return "密码不能为纯大写字母";
        } else if (hasSmall == 1){
            return "密码不能为纯小写字母";
        } else if (hasNumber == 1){
            return "密码不能为纯数字";
        }else{
            return "密码不能为纯符号";
        }
    }

    public static String checkPasswordStrength(String password) {
        int length = 0;
        int letter = 0;
        int number = 0;
        int symbol = 0;
        int extra = 0;
        boolean isExistCapitalLetter = false;
        boolean isExistSmallLetter = false;
        int numberCount = 0;
        int symbolCount = 0;
        if (password.length() == 6) {
            length += 5;
        } else if (password.length() == 7) {
            length += 10;
        } else if (password.length() > 7) {
            length += 25;
        }
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) >= 'A' && password.charAt(i) <= 'Z') {
                isExistCapitalLetter = true;
            } else if (password.charAt(i) >= 'a' && password.charAt(i) <= 'z') {
                isExistSmallLetter = true;
            } else if (password.charAt(i) >= 48 && password.charAt(i) <= 57) {//0-9的ascii码是48-57
                numberCount++;
            } else {//特殊字符
                symbolCount++;
            }
        }

        if (isExistCapitalLetter && isExistSmallLetter) {
            letter += 20;
        } else if (isExistCapitalLetter || isExistSmallLetter) {
            letter += 10;
        }
        if (numberCount == 1) {
            number += 10;
        } else if (numberCount > 1) {
            number += 20;
        }
        if (symbolCount == 1) {
            symbol += 10;
        } else if (symbolCount > 1) {
            symbol += 20;
        }
        if (isExistCapitalLetter && isExistSmallLetter && numberCount > 0 && symbolCount > 0) {
            extra += 5;
        } else if ((isExistCapitalLetter || isExistSmallLetter) && numberCount > 0 && symbolCount > 0) {
            extra += 3;
        } else if ((isExistCapitalLetter || isExistSmallLetter) && numberCount > 0) {
            extra += 2;
        }
        int total = length + letter + number + symbol + extra;
        if (total >= 90) {
            return "密码强度: 非常安全";
        } else if (total >= 80) {
            return "密码强度: 安全";
        } else if (total >= 70) {
            return "密码强度: 非常强";
        } else if (total >= 60) {
            return "密码强度: 强";
        } else if (total >= 50) {
            return "密码强度: 一般";
        } else if (total < 50) {
            return "密码强度: 弱";
        }
        return "";
    }

    public static void productSubType(Context mContext, ImageView imageView, int subType) {
        Drawable right = null;
        //1质押，2保证，3抵押，4信用，5实地
        switch (subType) {
            case 1:
                right = mContext.getResources().getDrawable(R.drawable.stype_zhi);
                break;
            case 2:
                right = mContext.getResources().getDrawable(R.drawable.stype_bao);
                break;
            case 3:
                right = mContext.getResources().getDrawable(R.drawable.stype_ya);
                break;
            case 4:
                right = mContext.getResources().getDrawable(R.drawable.stype_xin);
                break;
            case 5:
                right = mContext.getResources().getDrawable(R.drawable.stype_ya);
                break;
            default:
                break;
        }
        if (right != null) {
            imageView.setBackgroundDrawable(right);
        }
    }

    public static void productZhuan(String allowTransfer, ImageView mZhuan) {
        if (mZhuan == null) return;
        if (!TextUtils.isEmpty(allowTransfer) && "true".equals(allowTransfer)) {
            mZhuan.setVisibility(View.VISIBLE);
        } else {
            mZhuan.setVisibility(View.GONE);
        }
    }

    public static void productJing(int productType, ImageView imageView) {
        if (imageView == null) return;
        imageView.setVisibility(5 == productType ? View.VISIBLE : View.GONE);
    }
}
