package cn.vpfinance.vpjr.model;

/**
 * 微信access_token请求返回model
 */
public class WXAccessTokenAModel {
    /**
     * access_token : 12_P3Ul_S20qhFpvATCn_UqgSGSE-efkGt14vIsmPEflwNN7TXeCCiKtEMbzLIJdXEC6nez-GEQIkA3T7ViB2rrhZoa6m_PPXdEZzno_ipqkCo
     * expires_in : 7200
     * refresh_token : 12_I0yBIN0zU5o_SRcGcyubYVFHyP3xxpr-03ZMpA4aUN9dqBOYZFGvlXT6atH7B2fyy-mCUw_aYm-78vVUYAqBYxDPIH-hCgfn-dQtYoAe8_g
     * openid : oBLz70bg82g1m6saAC2UY1iiRRwE
     * scope : snsapi_userinfo
     * unionid : oKKaa1WSatO6KBI5FWPAzuYfpPD0
     */
    public String refresh_token;
    public String openid;
    public String scope;
    public String unionid;
    public String access_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
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

    public int expires_in;
}
