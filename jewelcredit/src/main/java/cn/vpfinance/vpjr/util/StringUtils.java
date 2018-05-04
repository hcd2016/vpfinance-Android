package cn.vpfinance.vpjr.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

import cn.vpfinance.android.R;

/**
 * Created by zzlz13 on 2017/4/18.
 */

public class StringUtils {

    public static String spannablePercent(Context context, String text){
        String rateContent = text + "%";
        int lenRate = rateContent.length();
        SpannableString rate = new SpannableString(rateContent);
        rate.setSpan(new TextAppearanceSpan(context, R.style.item_rate_text_style1), 0, lenRate-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rate.setSpan(new TextAppearanceSpan(context, R.style.item_rate_text_style2), lenRate-1, lenRate, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return rate.toString();
    }
}
