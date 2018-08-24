package cn.vpfinance.vpjr.gson;

import java.io.Serializable;

/**
 * 用于注册传递的UserBean
 */
public class UserRegisterBean implements Serializable {
    public boolean isPersonType = true;//是否是个人用户
    public String phoneNum;//用户手机号
    public String referrerNum;//推荐人手机号
    public String uPwd;//用户密码
    public String captcha;//手机验证码
    public int pwdSetType = 1;//设置密码/重设密码类型 注册为1,重设为2
    public String email;//邮箱

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFromWeixin() {
        return isFromWeixin;
    }

    //微信相关
    public String refresh_token;
    public String openid;
    public String scope;
    public String unionid;
    public String access_token;
    public boolean isFromWeixin;//true为是微信流程
    public boolean isClickWXFromSetting;//微信绑定点击来源,true为来自设置
    public boolean isWxPhoneRegister;//微信绑定时输入的手机号已注册标识(用于发短信按钮的更改)

    public boolean isWxPhoneRegister() {
        return isWxPhoneRegister;
    }

    public void setWxPhoneRegister(boolean wxPhoneRegister) {
        isWxPhoneRegister = wxPhoneRegister;
    }





    //企业信息
    public String companyName;//企业名称
    public String companyCreditCode;//18位社会信用码
    public String companyLegal;//企业法人
    public String companyAddress;//企业地址

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCreditCode() {
        return companyCreditCode;
    }

    public void setCompanyCreditCode(String companyCreditCode) {
        this.companyCreditCode = companyCreditCode;
    }

    public String getCompanyLegal() {
        return companyLegal;
    }

    public void setCompanyLegal(String companyLegal) {
        this.companyLegal = companyLegal;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public boolean getIsFromWeixin() {
        return isFromWeixin;
    }

    public void setFromWeixin(boolean fromWeixin) {
        isFromWeixin = fromWeixin;
    }

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

    public boolean isClickWXFromSetting() {
        return isClickWXFromSetting;
    }

    public void setClickWXFromSetting(boolean clickWXFromSetting) {
        isClickWXFromSetting = clickWXFromSetting;
    }


}
