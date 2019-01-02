package cn.vpfinance.vpjr.model;

import java.util.List;

/**
 * Created by Administrator on 2015/11/10.
 */
public class FundRecordInfo {

    public String success;
    /*总页数*/
    public String totalPage;

    public List<RecordListInfoItem> recordList;
    public class RecordListInfoItem{
        /*标题*/
        public String title;
        /*出借金额*/
        public String tenderMoney;
        /*出借时间*/
        public String tenderTime;
        /*抵扣代金券金额*/
        public String voucherMoney;
        /*是否有红包*/
        public String haveRedPacket;
        /*红包连接*/
        public String shareUrl;
        /*使用加息券张数*/
        public String couponsCount;


        public int recordId;
    }
}
