package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by zzlz13 on 2017/6/2.
 */

public class RecordDepositDetailBean {


    /**
     * title : 定存宝001
     * status : 2
     * contentList : [[{"title":"约定年利率","redVal":"","blackVal":"7.199999999999999%"},{"title":"我加入的金额","redVal":"","blackVal":"400.00元"}],[{"title":"已结清本金","redVal":"2000.00","blackVal":"元"},{"title":"已结清利息","redVal":"24.28","blackVal":"元"}],[{"title":"在投本金","redVal":"231.18","blackVal":"元"},{"title":"待收","redVal":"1.50","blackVal":"元"}],[{"title":"还款方式","redVal":"","blackVal":"等额本息"},{"title":"锁定期","redVal":"","blackVal":"1个月"}],[{"title":"预计还款时间","redVal":"","blackVal":""}]]
     * procotolList : [{"num":"1","title":"消费定存服务协议","url":"/loaninfo/debt_protocol?rid="}]
     * poolId : 7
     */

    public String title;
    public int status;
    public int poolId;
    public String finishTime;//预计还款时间
    public String tenderTime;//出借时间
    public List<List<ContentListBean>> contentList;
    public List<ProcotolListBean> procotolList;

    public static class ContentListBean {
        /**
         * title : 约定年利率
         * redVal :
         * blackVal : 7.199999999999999%
         */

        public String title;
        public String redVal;
        public String blackVal;
    }

    public static class ProcotolListBean {
        /**
         * num : 1
         * title : 消费定存服务协议
         * url : /loaninfo/debt_protocol?rid=
         */

        public String num;
        public String title;
        public String url;
    }
}
