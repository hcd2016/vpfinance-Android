package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by zzlz13 on 2017/4/20.
 */

public class DepositItemBean {


    public List<LoansigninfoBean> loansigninfo;

    public static class LoansigninfoBean {
        /**
         * data : [{"value":"测试测试","type":"1","key":"产品编号"},{"value":"1","type":"1","key":"借款类型"},{"value":"10000.0","type":"1","key":"借款总额"},{"value":"消费分期","type":"1","key":"借款用途"},{"value":"2","type":"1","key":"借款期限"}]
         * info : 项目信息
         */

        public String info;
        public List<DataBean> data;

        public static class DataBean {
            /**
             * value : 测试测试
             * type : 1
             * key : 产品编号
             */

            public String value;
            public String type;
            public String key;
        }
    }
}
