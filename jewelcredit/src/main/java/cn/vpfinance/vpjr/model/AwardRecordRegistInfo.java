package cn.vpfinance.vpjr.model;

import java.util.List;

/**
 * Created by Administrator on 2015/11/13.
 * 注册奖励记录bean
 */
public class AwardRecordRegistInfo {
    public String success;

    /*总页数*/
    public String totalPage;

    public List<AwardRecordRegistInfoItem> dataList;
    public class AwardRecordRegistInfoItem{
        /*手机*/
        public String phone;
        /*邀请时间*/
        public String inviteTime;
        /*代金卷奖励*/
        public String reward;
        public String status;
        public String period;
    }
}
