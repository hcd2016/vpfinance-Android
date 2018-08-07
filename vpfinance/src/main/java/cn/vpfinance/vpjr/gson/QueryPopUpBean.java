package cn.vpfinance.vpjr.gson;

import java.util.List;

public class QueryPopUpBean {


    /**
     * bigType : addCardReward
     * couponDetail : [{"time":"","couponName":"优惠券"}]
     * returnType : 5
     */

    public String bigType;
    public int returnType;
    public List<CouponDetailBean> couponDetail;

    public static class CouponDetailBean {
        /**
         * time :
         * couponName : 优惠券
         */

        public String time;
        public String couponName;
    }
}
