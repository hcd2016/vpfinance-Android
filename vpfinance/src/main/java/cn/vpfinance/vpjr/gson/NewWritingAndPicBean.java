package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 */
public class NewWritingAndPicBean {

    /**
     * dataType : 3
     * data : [{"value":"本项目合作方为深圳华汇融资租赁有限公司，为华睿信集团的关联企业。\n华睿信集团总部位于深圳市福田区CBD太平洋金融大厦，并在上海、香港、吉林、合肥等地设有分支机构。华睿信以资产管理，出借管理为核心，股权出借为主，辅助债权出借，实现股债联动，推动实体经济转型、升级、发展。出借重点为国家战略新兴产业，包括：智能设备制造、现代化服务产业、旅游产业、新能源、环保等，出借阶段涵盖天使、VC及PE。\n深圳华汇融资租赁有限公司隶属于华睿信集团旗下,实收资本3500万美元，总资产4.8亿元人民币。公司坐落于深圳市福田区CBD太平洋金融大厦，是专业从事国内外融资租赁业务的中外合资企业，为客户业务发展提供全面的融资解决方案和服务，为企业的转型升级提供助力。华汇租赁自正式开展业务以来，业务投放领域涉及医疗、电子制造、能源、服务等行业，租赁标的物均为承租企业核心设备，具有行业通用性，保值性和流通性较高。截止2016年8月，累计总投放额为2亿多元人民币，并且无逾期及违约项目。\n","type":"1","key":"融资租赁机构介绍"},{"value":"承租企业主要经营无铅汽油、柴油零售、润滑油销售。为了扩大加油站规模，承租人以加油站所需油罐、油管、加油机、防爆防雷、汽油回收设备等作为租赁物以售后回租的形式向华汇融资租赁公司申请融资，并分期缴付租金，租金全部付清后取得租赁物所有权，在此期间所有权归融资租赁公司所有。该融资租赁公司通过微品金融平台转让本融资租赁项目债权。\n本项目原始债权为2500万元，本次合作方将其中1000万债权委托微品金融平台转让。本次标的为第一期，金额300万元。\n","type":"1","key":"资金用途"},{"value":"http://www.vpfinance.cn/upload/borrowersadditionalfiles/recentFlow.jpg","type":"2","key":"交易描述"},{"value":"","type":"3","fileData":[{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/1582201608191813423632.JPG","type":"2","key":"三证合一-融资租赁机构"},{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/6702201608191814203354.JPG","type":"2","key":"开户许可证-融资租赁机构"},{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/611320160819181449129.JPG","type":"2","key":"机构信用代码证-融资租赁机构"},{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/6221201608191815103214.JPG","type":"2","key":"法人代表身份证-融资租赁机构"},{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/427201608191815276804.JPG","type":"2","key":"租赁物采购合同"},{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/7520201608191815456158.JPG","type":"2","key":"融资租赁合同01"},{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/4017201608191815564984.JPG","type":"2","key":"融资租赁合同02"},{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/3611201608191816152935.JPG","type":"2","key":"保证合同01"},{"value":"http://www.vpfinance.cn:80/upload/borrowersadditionalfiles/9185201608191816274171.JPG","type":"2","key":"保证合同02"}],"key":"租赁资料"}]
     */

    public int dataType;
    /**
     * value : 本项目合作方为深圳华汇融资租赁有限公司，为华睿信集团的关联企业。
     华睿信集团总部位于深圳市福田区CBD太平洋金融大厦，并在上海、香港、吉林、合肥等地设有分支机构。华睿信以资产管理，出借管理为核心，股权出借为主，辅助债权出借，实现股债联动，推动实体经济转型、升级、发展。出借重点为国家战略新兴产业，包括：智能设备制造、现代化服务产业、旅游产业、新能源、环保等，出借阶段涵盖天使、VC及PE。
     深圳华汇融资租赁有限公司隶属于华睿信集团旗下,实收资本3500万美元，总资产4.8亿元人民币。公司坐落于深圳市福田区CBD太平洋金融大厦，是专业从事国内外融资租赁业务的中外合资企业，为客户业务发展提供全面的融资解决方案和服务，为企业的转型升级提供助力。华汇租赁自正式开展业务以来，业务投放领域涉及医疗、电子制造、能源、服务等行业，租赁标的物均为承租企业核心设备，具有行业通用性，保值性和流通性较高。截止2016年8月，累计总投放额为2亿多元人民币，并且无逾期及违约项目。

     * type : 1
     * key : 融资租赁机构介绍
     */

    public List<DataBean> data;

    public static class DataBean {
        public String value;
        public int type;
        public String key;
        public List<FileDataBean> fileData;

        @Override
        public String toString() {
            return "DataBean{" +
                    "value='" + value + '\'' +
                    ", type='" + type + '\'' +
                    ", key='" + key + '\'' +
                    ", fileDataBean=" + fileData +
                    '}';
        }
    }


    public static class FileDataBean {
        public String value;
        public int type;
        public String key;

        @Override
        public String toString() {
            return "FileDataBean{" +
                    "value='" + value + '\'' +
                    ", type='" + type + '\'' +
                    ", key='" + key + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "NewWritingAndPicBean{" +
                "dataType=" + dataType +
                ", data=" + data +
                '}';
    }
}
