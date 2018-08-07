package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/6/20.
 */
public class InvestTopBean {


    /**
     * level : 初窥门径Ⅲ
     * background : /upload/backgroundimage/20160628171125.JPG
     * name : xzlang
     * sumTenderMoney : 567076.82
     * currentRank : 1
     * promit : 5.88
     * list : [{"level":"","userId":"395660","userName":"13809026804","promit":"10.51%","head":"/resources/imgs/headimg.jpg","tenderMoney":"4606904.24"},{"level":"","userId":"1436","userName":"13825250231","promit":"8.84%","head":"/resources/imgs/headimg.jpg","tenderMoney":"3798779.29"},{"level":"","userId":"452","userName":"13395195902","promit":"9.16%","head":"/resources/imgs/headimg.jpg","tenderMoney":"3649714.00"},{"level":"","userId":"393375","userName":"18616029683","promit":"10.48%","head":"/resources/imgs/headimg.jpg","tenderMoney":"3412000.00"},{"level":"","userId":"1595","userName":"13774368182","promit":"10.90%","head":"/resources/imgs/headimg.jpg","tenderMoney":"3087220.00"},{"level":"","userId":"802","userName":"18651855999","promit":"10.14%","head":"/resources/imgs/headimg.jpg","tenderMoney":"3035513.47"},{"level":"","userId":"1282","userName":"18682062912","promit":"9.88%","head":"/resources/imgs/headimg.jpg","tenderMoney":"2997689.04"},{"level":"","userId":"397185","userName":"CA","promit":"9.55%","head":"http://192.168.1.193:80/upload/user/20160629104325.jpg","tenderMoney":"2997462.24"},{"level":"","userId":"1778","userName":"ouweiming","promit":"9.44%","head":"/resources/imgs/headimg.jpg","tenderMoney":"2841285.25"},{"level":"","userId":"1017","userName":"13540017936","promit":"7.90%","head":"/resources/imgs/headimg.jpg","tenderMoney":"2763879.67"}]
     * number : 100.00%
     * head : /upload/userheader/20160518120919.JPG
     * isTender : 1
     */

    public String level;
    public String background;
    public String name;
    public String sumTenderMoney;
    public int    currentRank;
    public double promit;
    public String number;
    public String head;
    public String isTender;
    /**
     * level :
     * userId : 395660
     * userName : 13809026804
     * promit : 10.51%
     * head : /resources/imgs/headimg.jpg
     * tenderMoney : 4606904.24
     */

    public List<ListEntity> list;

    public static class ListEntity {
        public String level;
        public String userId;
        public String userName;
        public String promit;
        public String head;
        public String tenderMoney;
    }
}
