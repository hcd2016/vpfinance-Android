package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class PersonalCardBean {


    /**
     * imageUrl : /upload/userheader/20160629150843.JPG
     * loginStatus : 刚刚
     * returnRate : {"avgProfit":"9.47","myProfit":"5.88","maxProfit":"14.51"}
     * userMedals : [{"id":"16","logo":"http://192.168.1.193/resources/imgs/medal/medal5_yes_","condition":"单个产品最后一个用户出借","description":"即时更新，有效期永久(可叠加)","name":"一锤定音","medalCategoryId":"3"},{"id":"15","logo":"http://192.168.1.193/resources/imgs/medal/medal4_yes_","condition":"用APP出借产品","description":"即时更新，有效期永久(并列)","name":"掌控全局","medalCategoryId":"2"},{"id":"18","logo":"http://192.168.1.193/resources/imgs/medal/medal7_yes_","condition":"单个产品出借金额最大的用户","description":"即时更新，有效期永久(可叠加)","name":"一鸣惊人","medalCategoryId":"3"}]
     * month5 : 0.54
     * month4 : 9.89
     * level : 初窥门径Ⅲ
     * month1 : 47.80
     * month3 : 18.68
     * name : xzlang
     * month2 : 23.07
     * background : /upload/backgroundimage/20160629161709.JPG
     * myFriends : [{"imgUrl":"/resources/imgs/headimg.jpg","userId":"1341","userName":"ghxchina","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"1160","userName":"rebeccaq","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"465","userName":"appe2015e","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"218","userName":"wubuy","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"211","userName":"soulharber","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"177","userName":"13631685096","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"137","userName":"18923892692","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"133","userName":"00145611","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"126","userName":"audrey","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"397036","userName":"18818681727","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"393376","userName":"Majiannan","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"393621","userName":"l_fun","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"393659","userName":"hxj_118","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"395112","userName":"hearing","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"396106","userName":"15013797669","signature":""},{"imgUrl":"/resources/imgs/headimg.jpg","userId":"398017","userName":"wangneil","signature":""}]
     * signature : 红红火火恍恍惚惚
     */

    public String imageUrl;
    public String loginStatus;
    //添加两个字段  控制隐私显示
    public boolean isShowFriends = false;
    public boolean isShowInvestInfo = false;

    /**
     * avgProfit : 9.47
     * myProfit : 5.88
     * maxProfit : 14.51
     */

    public ReturnRateEntity returnRate;
    public String month5;
    public String month4;
    public String level;
    public String month1;
    public String month3;
    public String name;
    public String month2;
    public String background;
    public String signature;

    /**
     * id : 16
     * logo : http://192.168.1.193/resources/imgs/medal/medal5_yes_
     * condition : 单个产品最后一个用户出借
     * description : 即时更新，有效期永久(可叠加)
     * name : 一锤定音
     * medalCategoryId : 3
     */

    public List<UserMedalsEntity> userMedals;
    /**
     * imgUrl : /resources/imgs/headimg.jpg
     * userId : 1341
     * userName : ghxchina
     * signature :
     */

    public List<MyFriendsEntity>  myFriends;

    public static class ReturnRateEntity {
        public String avgProfit;
        public String myProfit;
        public String maxProfit;
    }

    public static class UserMedalsEntity {
        public String id;
        public String logo;
        public String condition;
        public String description;
        public String name;
        public String medalCategoryId;
        public int number;
    }

    public static class MyFriendsEntity {
        public String imgUrl;
        public String userId;
        public String userName;
        public String signature;
    }
}
