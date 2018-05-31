package cn.vpfinance.vpjr.gson;

import java.util.List;

public class CouponBean {


    /**
     * myCouponDto : {"expiredCount":27,"myCouponListDtos":[{"couponType":3,"denomination":"1.22","expiredTm":"2018-05-01 23:59:59","couponName":"7天投资代金券5元","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponName":"7天投资代金券5元","couponType":3,"denomination":"5.0","expiredTm":"2018-04-18 23:59:59","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponName":"7天投资代金券5元","couponType":3,"denomination":"5.0","expiredTm":"2018-04-17 23:59:59","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponName":"7天投资代金券5元","couponType":3,"denomination":"5.0","expiredTm":"2018-04-17 23:59:59","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponName":"7天投资代金券5元","couponType":3,"denomination":"5.0","expiredTm":"2018-02-15 23:59:59","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponType":3,"denomination":"0.33","expiredTm":"2018-02-05 23:59:59"},{"couponType":3,"denomination":"0.18","expiredTm":"2018-02-05 23:59:59"},{"couponType":3,"denomination":"1.28","expiredTm":"2018-02-05 23:59:59"},{"couponType":3,"denomination":"0.49","expiredTm":"2018-02-05 23:59:59"},{"couponName":"预约券","couponType":2,"expiredTm":"2017-10-31 23:59:59","remark":"任意期限产品可使用;"}],"unUseCount":1,"useCount":149}
     */

    public MyCouponDtoBean myCouponDto;

    public static class MyCouponDtoBean {
        /**
         * expiredCount : 27
         * myCouponListDtos : [{"couponType":3,"denomination":"1.22","expiredTm":"2018-05-01 23:59:59"},{"couponName":"7天投资代金券5元","couponType":3,"denomination":"5.0","expiredTm":"2018-04-18 23:59:59","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponName":"7天投资代金券5元","couponType":3,"denomination":"5.0","expiredTm":"2018-04-17 23:59:59","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponName":"7天投资代金券5元","couponType":3,"denomination":"5.0","expiredTm":"2018-04-17 23:59:59","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponName":"7天投资代金券5元","couponType":3,"denomination":"5.0","expiredTm":"2018-02-15 23:59:59","remark":"投资任意定期投资产品可用(限30天以上的产品使用)"},{"couponType":3,"denomination":"0.33","expiredTm":"2018-02-05 23:59:59"},{"couponType":3,"denomination":"0.18","expiredTm":"2018-02-05 23:59:59"},{"couponType":3,"denomination":"1.28","expiredTm":"2018-02-05 23:59:59"},{"couponType":3,"denomination":"0.49","expiredTm":"2018-02-05 23:59:59"},{"couponName":"预约券","couponType":2,"expiredTm":"2017-10-31 23:59:59","remark":"任意期限产品可使用;"}]
         * unUseCount : 1
         * useCount : 149
         */

        public int expiredCount;
        public int unUseCount;
        public int useCount;
        public List<MyCouponListDtosBean> myCouponListDtos;

        public static class MyCouponListDtosBean {
            /**
             * couponType : 3
             * denomination : 1.22
             * expiredTm : 2018-05-01 23:59:59
             * couponName : 7天投资代金券5元
             * remark : 投资任意定期投资产品可用(限30天以上的产品使用)
             */

            public int couponType;
            public String denomination;
            public String expiredTm;
            public String couponName;
            public List<String> remarkList;
            public String getWay;
            public int voucherStatus; //1正常 2冻结
        }
    }
}
