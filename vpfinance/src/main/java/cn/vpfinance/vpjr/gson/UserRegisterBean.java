package cn.vpfinance.vpjr.gson;

import java.io.Serializable;

/**
 * 用于注册传递的UserBean
 */
public class UserRegisterBean implements Serializable {
    public boolean isPersonType;//是否是个人用户
    public String phoneNum;//用户手机号
    public String referrerNum;//推荐人手机号
    public String uPwd;//用户密码
    public String captcha;//手机验证码
    public int pwdSetType;//设置密码/重设密码类型
    //微信相关
    public String refresh_token;
    public String openid;
    public String scope;
    public String unionid;
    public String access_token;

    public boolean isPersonType() {
        return isPersonType;
    }

    public void setPersonType(boolean personType) {
        isPersonType = personType;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }


    public int getPwdSetType() {
        return pwdSetType;
    }

    public void setPwdSetType(int pwdSetType) {
        this.pwdSetType = pwdSetType;
    }


    public boolean getUserType() {
        return isPersonType;
    }

    public void setUserType(boolean userType) {
        this.isPersonType = userType;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getReferrerNum() {
        return referrerNum;
    }

    public void setReferrerNum(String referrerNum) {
        this.referrerNum = referrerNum;
    }

    public String getuPwd() {
        return uPwd;
    }

    public void setuPwd(String uPwd) {
        this.uPwd = uPwd;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

}
