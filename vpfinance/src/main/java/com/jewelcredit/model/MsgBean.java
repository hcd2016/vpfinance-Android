package com.jewelcredit.model;

import java.util.List;

public class MsgBean  {
    /**
     * systemUnread : 0
     * repayList : [{"isRead":"0","title":"testestxuhua","url":"to/infoDisclosure?newsType=20#id=590","content":"testest..."},{"isRead":"0","title":"测试噢噢噢噢噢","url":"","content":"贷后调查1贷后调查1贷后调查1..."}]
     */

    public String systemUnread;
    public List<RepayListBean> repayList;

    public static class RepayListBean {
        /**
         * isRead : 0
         * title : testestxuhua
         * url : to/infoDisclosure?newsType=20#id=590
         * content : testest...
         */

        public String isRead;
        public String title;
        public String url;
        public String content;
        public String time;
    }
}
