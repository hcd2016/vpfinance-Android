package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/5/5.
 */
public class BankBean {

    /**
     * appbankimgurl :
     * bankcode : 03080000
     * bankimgurl : /resources/imgs/zhaoshang_01.jpg
     * h5bankimgurl : /resources/h5/images/bank-logo/zhaoshang-icon.svg
     * hasbranch : 0
     * id : 1
     * isdel : 0
     * name : 招商银行
     * platform : 0
     * quickpaymentdaylimit : 10000.0
     * quickpaymentsinglelimit : 10000.0
     */

    public List<BanktypelistEntity> banktypelist;

    public static class BanktypelistEntity {
        public String appbankimgurl;
        public String bankcode;
        public String bankimgurl;
        public String h5bankimgurl;
        public int    hasbranch;
        public int    id;
        public int    isdel;
        public String name;
        public int    platform;
        public double quickpaymentdaylimit;
        public double quickpaymentsinglelimit;
    }
}
