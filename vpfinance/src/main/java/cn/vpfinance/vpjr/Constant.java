package cn.vpfinance.vpjr;

/**
 * Created by zzlz13 on 2017/6/28.
 */

public interface Constant {

    long delay = 1000;

    String chooseAtLeastOne = "请至少选择一个";

    String AccountType = "accountType";

    int AccountBank = 1;//银行存管账户
    int AccountLianLain = 0;//连连账户

    /** 首页列表类型*/
    int TYPE_REGULAR = 1; //定期
    int TYPE_TRANSFER = 2; //债权转让
    int TYPE_BANK = 4; // "存管专区"
    int TYPE_POOL = 5; // "智存投资"
}
