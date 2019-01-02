package cn.vpfinance.vpjr.model;

import java.util.List;

/**
 * Created by Administrator on 2015/11/13.
 * 出借奖励记录bean
 */
public class AwardRecordInvestInfo {
    public String success;

    /*总页数*/
    public String totalPage;

    public List<AwardRecordInvestInfoItem> dataList;
    public class AwardRecordInvestInfoItem{
        /*发放记录*/
        public String periods;
        /*发放时间*/
        public String addTime;
        /*累计推荐奖励*/
        public String bonuses;
    }
}
