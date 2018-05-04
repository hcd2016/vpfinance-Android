package cn.vpfinance.vpjr.util;

import android.text.TextUtils;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 */
public class FormatUtils {

    /**
     * 保留两位小数, 0.0变为0.00
     * @param value
     * @return
     */
/*    public static String formatSaveAfterTwo(String value){
        int i = value.indexOf(".");
        if (i+2 == value.length()){
            value+="0";
        }
        return value;
    }
    public static String formatSaveAfterTwo(double value){
        String s = value+"";
        String s1 = formatSaveAfterTwo(s);
        return s1;
    }*/

    /**
     * 四舍五入
     * @param value
     * @return
     */
    public static String formatAbout(float value){
        return String.format("%.2f", value);
    }

    public static String formatAbout(double value){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(value);
    }

    public static String formatAbout(String value){
        try{
            double v = Double.parseDouble(value);
            return formatAbout(v);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "0.00";
    }


    /**
     * 向下取整，保留两位小数
     * @param string
     * @return
     */
    public static String formatDown(String string){
        try{
            double v = Double.parseDouble(string);
            return formatDown(v);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "00.00";
    }
    /**
     * 向下取整，保留两位小数
     * @param progress
     * @return
     */
    public static String formatDown(double progress){
        if (progress > 0 && progress <= 0.01){
            return "0.01";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return decimalFormat.format(progress);
    }

    /**
     * 向下取整,最少保留一位小数,最多两位
     * @param progress
     * @return
     */
    public static String formatRate(double progress){
        if (progress > 0 && progress <= 0.01){
            return "0.01";
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.0#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return decimalFormat.format(progress);
    }

    /**
     * 向下取整，保留两位小数,不保留零
     * @param progress
     * @return
     */
    public static String formatDown2(double progress){
        if (progress > 0 && progress <= 0.01){
            return "0.01";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return decimalFormat.format(progress);
    }

    /**
     * 向下取整，数字没三位用逗号隔开，保留两位小数,不保留零
     * @param progress
     * @return
     */
    public static String formatDown3(double progress){
        if (progress > 0 && progress <= 0.01){
            return "0.01";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(progress);
    }

    /**
     * 向下取整，保留两位小数(进度格式化)
     * @param progress
     * @return
     */
    public static String formatDownByProgress(double progress){
        if (progress > 0 && progress <= 0.01){
            return "0.01";
        }else if (progress >= 100){
            return "100";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return decimalFormat.format(progress);
    }

    /**
     * 向下取整，不保留小数
     * @param progress
     * @return
     */
    public static String formatNumberUnit(float progress){
        DecimalFormat decimalFormat = new DecimalFormat("0");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return decimalFormat.format(progress);
    }

    /**
     * 格式化数字单位万（10027436.00 -> 1,002万）
     * 向下取整
     * @param numberStr
     * @return
     */
    public static String formatNumberToTenThousand(String numberStr){
        if (TextUtils.isEmpty(numberStr)){
            return "";
        }
        try {
            float number = Float.parseFloat(numberStr);
            float numberToThousand = number / 10000;
            String numberToThousandStr = ("" + numberToThousand);
            numberToThousandStr = numberToThousandStr.substring(0,numberToThousandStr.indexOf("."));
            String numberToThousandNewStr = "";
            int index = numberToThousandStr.length() % 3;
            numberToThousandNewStr += numberToThousandStr.substring(0,index);
            for (int i= 0; i < numberToThousandStr.length() / 3; i++){
                if (TextUtils.isEmpty(numberToThousandNewStr)){
                    numberToThousandNewStr = numberToThousandStr.substring(index+i*3,index+i*3+3);
                }else{
                    numberToThousandNewStr = numberToThousandNewStr + "," + numberToThousandStr.substring(index+i*3,index+i*3+3);
                }
            }
            return numberToThousandNewStr + "万";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 格式化数字（10027436.00 -> 10，027,436。00）
     * @param number
     * @return
     */
    public static String formatNumber(String number){
        if (TextUtils.isEmpty(number)){
            return "";
        }
        String pointBehindStr = number.substring(number.indexOf("."));
        String pointFrontStr = number.substring(0,number.indexOf("."));
        String pointFrontNewStr = "";
        int index = pointFrontStr.length() % 3;
        pointFrontNewStr += pointFrontStr.substring(0,index);
        for (int i= 0; i < pointFrontStr.length() / 3; i++){
            if (TextUtils.isEmpty(pointFrontNewStr)){
                pointFrontNewStr = pointFrontStr.substring(index+i*3,index+i*3+3);
            }else{
                pointFrontNewStr = pointFrontNewStr + "," + pointFrontStr.substring(index+i*3,index+i*3+3);
            }
        }
        return pointFrontNewStr + pointBehindStr;
    }
    /**
     * 用户名隐藏
     * @param name
     * @return
     */
    public static String hideName(String name){
        if (TextUtils.isEmpty(name)){
            return "";
        }
        int length = name.length();
        String hideStr = "";
        String lastStr = name.substring(length - length / 2);
        for (int i = 0; i < length/2; i++){
            hideStr += "*";
        }
        return hideStr+lastStr;
    }

    /**
     * 手机号隐藏
     * @param phone
     * @return
     */
    public static String hidePhone(String phone){
        if (TextUtils.isEmpty(phone)){
            return "";
        }
        String fristStr = phone.substring(0, 3);
        String lastStr = phone.substring(phone.length() - 4);

        return fristStr+"******"+lastStr;
    }

    /**
     * 银行卡号隐藏
     * @param bank
     * @return
     */
    public static String hideBank(String bank){
        if (TextUtils.isEmpty(bank)){
            return "";
        }
        if (bank.length() <= 5){
            return bank;
        }
        String fristStr = bank.substring(0, 5);
        String lastStr = bank.substring(bank.length() - 4);

        return fristStr+"****"+lastStr;
    }
    /**
     * 身份证隐藏
     * @param idCard
     * @return
     */
    public static String hideIdCard(String idCard){
        if (TextUtils.isEmpty(idCard)){
            return "";
        }
        String fristStr = idCard.substring(0, 5);
        String lastStr = idCard.substring(idCard.length() - 4);

        return fristStr+"*********"+lastStr;
    }

    private static final int POW_10[] = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
    };

    public static String formatNumber(float number, int digitCount, boolean separateThousands) {
        return formatNumber(number, digitCount, separateThousands, ',');
    }

    public static String formatNumber(float number, int digitCount, boolean separateThousands,
                                      char separateChar) {

        char[] out = new char[35];

        boolean neg = false;
        if (number == 0) {
            return "0";
        }

        boolean zero = false;
        if (number < 1 && number > -1) {
            zero = true;
        }

        if (number < 0) {
            neg = true;
            number = -number;
        }

        if (digitCount > POW_10.length) {
            digitCount = POW_10.length - 1;
        }

        number *= POW_10[digitCount];
        long lval = Math.round(number);
        int ind = out.length - 1;
        int charCount = 0;
        boolean decimalPointAdded = false;

        while (lval != 0 || charCount < (digitCount + 1)) {
            int digit = (int) (lval % 10);
            lval = lval / 10;
            out[ind--] = (char) (digit + '0');
            charCount++;

            // add decimal point
            if (charCount == digitCount) {
                out[ind--] = ',';
                charCount++;
                decimalPointAdded = true;

                // add thousand separators
            } else if (separateThousands && lval != 0 && charCount > digitCount) {

                if (decimalPointAdded) {

                    if ((charCount - digitCount) % 4 == 0) {
                        out[ind--] = separateChar;
                        charCount++;
                    }

                } else {

                    if ((charCount - digitCount) % 4 == 3) {
                        out[ind--] = separateChar;
                        charCount++;
                    }
                }
            }
        }

        // if number around zero (between 1 and -1)
        if (zero) {
            out[ind--] = '0';
            charCount += 1;
        }

        // if the number is negative
        if (neg) {
            out[ind--] = '-';
            charCount += 1;
        }

        int start = out.length - charCount;

        // use this instead of "new String(...)" because of issue < Android 4.0
        return String.valueOf(out, start, out.length - start);
    }

    public static String checkDot(String text){
        String temp = "";
        String[] split = text.split(",");
        for (int i=0; i<split.length; i++){
            String s = split[i];
            if (null != s && s.length() != 0){
                temp = temp + s + ",";
            }
        }
        if (",".equals(temp.substring(temp.length()-1))){
            temp = temp.substring(0,temp.length()-1);
        }
        return temp;
    }
}
