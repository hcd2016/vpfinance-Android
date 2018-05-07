package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */
public class IphoneDesBean {

    /**
     * productNumber : 94
     * successContent : ["1.您已选择xApple iPhone 7手机（128G）;","2.产品满标后3个工作日内，微品金融工作人员将与您联系;","3.活动详询客服热线：0755 - 86627551;"]
     * activityRules : ["活动规则","1.活动时间：10月1日-10月15日;","2.iPhone7为国行现货，可到苹果售后保修，可选玫瑰金和金色两款颜色;","3.活动期间，投资指定6个月产品，单笔投资20万元，即可选择领取iPhone 7;","4.参与本活动不能使用代金券和加息券;","5.参与活动购买的产品不支持债权转让;","6.产品满标后3个工作日内，微品金融工作人员将与领取了手机的用户取得联系，获取寄货地址，确认配送事宜","iPhone7将于确认地址后的5个工作日内寄出，收货时间以物流时间为准;","7.本次活动的iPhone7均由中兴微品供货，如遇需沟通的问题，用户可联系微品金融客服，客服会协助与供货商沟通，也可直接拨打中兴微品售后电话：4000288600","活动详情可咨询：0755-86627551，或添加客服微信详询：小微（vp_xiaowei）、小金（vpjrkf）;","8.本次活动与Apple Inc.无关。本活动最终解释权归微品金融所有."]
     */

    private String productNumber;
    private List<String> successContent;
    private List<String> activityRules;

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public void setSuccessContent(List<String> successContent) {
        this.successContent = successContent;
    }

    public void setActivityRules(List<String> activityRules) {
        this.activityRules = activityRules;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public List<String> getSuccessContent() {
        return successContent;
    }

    public List<String> getActivityRules() {
        return activityRules;
    }
}
