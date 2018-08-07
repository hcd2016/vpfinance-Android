package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.n
 */
public class LoanPersonInfo {


    /**
     * dataType : 5
     * data : [{"title":"","data":[{"title":"基本信息","value":["chunyu287","大专或本科","31","已婚","湖南石门"],"type":"1","key":["借款人","学历","年龄","婚姻","籍贯"]},{"title":"工作信息","value":["互联网/硬件/通信","业务总监","15-50人","7年以下","深圳"],"type":"1","key":["公司行业","工作岗位","公司规模","工作年限","工作城市"]},{"title":"财务状况","value":["25000.0","是","10000.0","是","有商品房","否"],"type":"1","key":["月收入","车产","月支出","房贷","房产","车贷"]}]}]
     */

    public int              dataType;
    /**
     * title :
     * data : [{"title":"基本信息","value":["chunyu287","大专或本科","31","已婚","湖南石门"],"type":"1","key":["借款人","学历","年龄","婚姻","籍贯"]},{"title":"工作信息","value":["互联网/硬件/通信","业务总监","15-50人","7年以下","深圳"],"type":"1","key":["公司行业","工作岗位","公司规模","工作年限","工作城市"]},{"title":"财务状况","value":["25000.0","是","10000.0","是","有商品房","否"],"type":"1","key":["月收入","车产","月支出","房贷","房产","车贷"]}]
     */

    public List<DataEntity> data;

    public static class DataEntity {
        public String title;
        /**
         * title : 基本信息
         * value : ["chunyu287","大专或本科","31","已婚","湖南石门"]
         * type : 1
         * key : ["借款人","学历","年龄","婚姻","籍贯"]
         */

        public List<DataDes> data;

        public static class DataDes {
            public String       icon;
            public String       title;
            public String       type;
            public List<String> value;
            public List<String> key;
        }
    }
}
