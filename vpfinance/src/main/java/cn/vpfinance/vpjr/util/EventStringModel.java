package cn.vpfinance.vpjr.util;

/**
 * eventbus发送String消息统一管理类
 */
public class EventStringModel {
    public String currentEvent;
    public String getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(String currentEvent) {
        this.currentEvent = currentEvent;
    }
    public EventStringModel(String event) {
        setCurrentEvent(event);
    }

    public static final String EVENT_REGISTER_FINISH = "register_finish";//注册完成
    public static final String EVENT_WEIXIN_LOGIN_SUCCESS = "weixin_login_success";//微信登录成功
    public static final String EVENT_WEIXIN_REGISTER_SUCCESS = "weixin_regiter_success";//微信注册成功
    public static final String EVENT_RESET_PWD_SUCCESS = "reset_pwd_success";//重置密码成功
    public static final String EVENT_BIND_EMAIL_SUCCESS = "bind_email_success";//绑定邮箱成功
    public static final String EVENT_BIND_WEIXIN_SUCCESS_FROM_SETTING = "bind_email_success_from_setting";//微信绑定成功(设置)
    public static final String EVENT_CHANGE_PHONE_SUCCESS= "change_phone_success";//手机号修改成功
    public static final String EVENT_PRODUCT_LIST_LOAD_SUCCECC= "product_list_load_success";//产品列表加载成功
    public static final String EVENT_MSG_ALL_READ_CLICK= "msg_all_read_click";//点击全部已读

}
