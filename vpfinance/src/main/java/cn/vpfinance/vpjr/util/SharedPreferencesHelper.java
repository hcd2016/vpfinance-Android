package cn.vpfinance.vpjr.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPreferencesHelper {

    private SharedPreferences sp;

    private SharedPreferences.Editor       editor;

    private static final String PRES_NAME              = "ifinance";

    private static SharedPreferencesHelper helper;

    public static final String KEY_FIRST_START = "first_start";
    public static final String KEY_GESTURE_PASSWD = "gesture_passwd";
    public static final String KEY_GESTURE_ERROR = "gesture_error";

    public static final String KEY_SAVE_LOGIN = "user_login";
    public static final String KEY_SAVE_USER_NAME = "user_name";
    public static final String KEY_SAVE_USER_ID = "user_id";
    public static final String KEY_SAVE_USER_GETUI_CLIENT_ID = "user_getui_client_id";
    public static final String KEY_SAVE_USER_GETUI_LAST_SENT = "user_getui_last_sent";

    public static final String KEY_LOCK_USER_NAME= "key_lock_user_name";
    public static final String KEY_LOCK_USER_ID= "key_lock_user_id";
    public static final String KEY_LOCK_USER_PWD= "key_lock_user_pwd";
    public static final String KEY_LOCK_STRING = "key_lock_string";
    public static final String KEY_LAST_PAUSE_TIME = "key_last_pause_time";
    public static final String MINE_NO_SHOW_INFO = "mine_no_show_info";
    public static final String USER_HEAD_URL = "user_head_url";
    public static final String USER_BACKGROUND_URL = "user_background_url";
    public static final String STATE_RETURN_CALENDER_OR_LIST= "state_return_calender_or_list";// 1 为回款日历状态。 2位回款列表状态 默认日历
    public static final String VOUCHER_ISSHOW_DIALOG = "voucher_isshow_dialog";//

    public static final String mSesnCode = "mSesnCode";
    public static final String KEY_IS_OPEN_BANK_ACCOUNT = "key_is_open_bank_account";//判断是否开通了银行存管 0 没开通 1开通 2正在处理
    public static final String KEY_IS_NEW_USER = "key_is_new_user";//
    public static final String KEY_IS_BIND_BANK = "key_is_bind_bank";//
    public static final String KEY_ALLOW_RECHARGE = "key_allow_recharge_lianlian";//
    public static final String KEY_SHOW_AUTO_STATUS_TIME = "key_show_auto_status_time";//首页显示自动投标状态的时间
    public static final String KEY_HX_UPDATE_DIALOG_SHOW = "key_hx_update_dialog_show";//华兴接口更新了显示弹窗一次
    public static final String KEY_ISPERSONTYPE = "key_is_person_type";//是否是个人用户,true为是,false为企业用户
    public static final String KEY_WX_BIND_IS_FROM_SETTING = "key_wx_bind_is_from_setting";//微信绑定是否来源于设置,true为是,false为不是
    public static final String KEY_IS_FIRST_REGISTER = "key_is_first_register";//是否是第一次注册

    private SharedPreferencesHelper(Context context){
        sp = context.getSharedPreferences(PRES_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SharedPreferencesHelper getInstance(Context context){
        if(helper == null){
            helper = new SharedPreferencesHelper(context.getApplicationContext());
        }

        return helper;
    }

    public void putStringValue(String key, String value){
        editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putSetValue(String key, Set<String> values){
        editor = sp.edit();
        editor.putStringSet(key,values);
        editor.commit();
    }

    public Set<String> getSetValue(String key, Set<String> defaultSet){
       return sp.getStringSet(key,defaultSet);
    }

    public void removeKey(String key)
    {
        editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public void putBooleanValue(String key, boolean value){
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putIntValue(String key, int value){
        editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLongValue(String key, long value){
        editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public boolean getBooleanValue(String key){
        return sp.getBoolean(key, false);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public String getStringValue(String key){
        return sp.getString(key, null);
    }

    public String getStringValue(String key, String defalutValue) {
        return sp.getString(key, defalutValue);
    }

    public int getIntValue(String key, int defaultValue){
        return sp.getInt(key, defaultValue);
    }

    public long getLongValue(String key, long defalutValue){
        return sp.getLong(key, defalutValue);
    }

    public void clearPreference(){
        editor = sp.edit();
        editor.clear();
        editor.commit();
    }

}
