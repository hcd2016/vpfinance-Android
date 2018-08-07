package cn.vpfinance.vpjr.gson;

import java.util.ArrayList;

/**
 * Created by zzlz13 on 2017/11/6.
 */

public class NewType6Bean {

    /**
     * dataType : 6
     * infos : {"title":"基本信息","value":["何鹏鹏","411522198905125158","13689568213","广东深圳","测试玩玩"],"type":"6","key":["姓名","身份证","手机号","所在地","借款用途"]}
     * credits : {"title":"认证信息","value":["/resources/app/img/hz_house_loan/icon_shenfen","/resources/app/img/hz_house_loan/icon_shouji","/resources/app/img/hz_house_loan/shidi","/resources/app/img/hz_house_loan/guanxi"],"type":"6","key":["身份认证","手机认证","实地认证","关系认证"]}
     */

    public int dataType;
    public InfosBean infos;
    public CreditsBean credits;

    public static class InfosBean {
        /**
         * title : 基本信息
         * value : ["何鹏鹏","411522198905125158","13689568213","广东深圳","测试玩玩"]
         * type : 6
         * key : ["姓名","身份证","手机号","所在地","借款用途"]
         */

        public String title;
        public String type;
        public ArrayList<String> value;
        public ArrayList<String> key;
    }

    public static class CreditsBean {
        /**
         * title : 认证信息
         * value : ["/resources/app/img/hz_house_loan/icon_shenfen","/resources/app/img/hz_house_loan/icon_shouji","/resources/app/img/hz_house_loan/shidi","/resources/app/img/hz_house_loan/guanxi"]
         * type : 6
         * key : ["身份认证","手机认证","实地认证","关系认证"]
         */

        public String title;
        public String type;
        public ArrayList<String> value;
        public ArrayList<String> key;
    }
}
