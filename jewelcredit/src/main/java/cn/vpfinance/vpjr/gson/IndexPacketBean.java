package cn.vpfinance.vpjr.gson;

import java.util.List;

public class IndexPacketBean {


    /**
     * datas : [{"title":"企业贷201712221","shareUrl":"http://192.168.1.129:17192/h5/inviteregister/getMoney?member=04020110&id=08840"},{"title":"企业贷201801151","shareUrl":"http://192.168.1.129:17192/h5/inviteregister/getMoney?member=04020110&id=09410"},{"title":"后台测试","shareUrl":"http://192.168.1.129:17192/h5/inviteregister/getMoney?member=04020110&id=09470"},{"title":"企业贷201702121","shareUrl":"http://192.168.1.129:17192/h5/inviteregister/getMoney?member=04020110&id=09480"}]
     * count : 4
     */

    public int count;
    public List<DatasBean> datas;

    public static class DatasBean {
        /**
         * title : 企业贷201712221
         * shareUrl : http://192.168.1.129:17192/h5/inviteregister/getMoney?member=04020110&id=08840
         */

        public String title;
        public String shareUrl;
    }
}
