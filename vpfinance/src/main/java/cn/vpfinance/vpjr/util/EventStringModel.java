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
    public static final String EVENT_WEIXIN_LOGIN_SUCCESS = "weixin_login_sucess";//微信登录成功
    public static final String EVENT_WEIXIN_REGISTER_SUCCESS = "weixin_regiter_sucess";//微信注册成功
    public static final String EVENT_RESET_PWD_SUCCESS = "reset_pwd_sucess";//重置密码成功
    public static final String EVENT_BIND_EMAIL_SUCCESS = "bind_email_sucess";//绑定邮箱成功
}
