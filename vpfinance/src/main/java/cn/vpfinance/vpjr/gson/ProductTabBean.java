package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 */
public class ProductTabBean {


    /**
     * dataType : 0
     * sort : 1
     * showType : 1
     * tabName : 基本信息
     * url : /AppLoan/showTabContent?loanId=723&dataType=0&tabNum=0&loanType=0
     */

    public List<TabListBean> tabList;

    public static class TabListBean {
        public String dataType;
        public String sort;
        public String showType;
        public String tabName;
        public String url;
    }
}
