package cn.vpfinance.vpjr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zzlz13 on 2017/4/19.
 */

public class DepositInvestInfo implements Serializable{

    public String title;
    public double rate;
    public String month;
    public double raminMoney;//canBuyMoney 能买多少
    public double planMoney;
    public String returnWay;
    public String investScopeInfo;
    //    public HashMap<String,Long> investScope;
    public int timeType;//1月标,2天标
    public long publishTime;//预售时间
    public int bookCouponNumber;//预售券张数

    public double bookPercent;//可以预售多少百分比
    public double sumMoney;
    public double tenderMoney; //购买了多少
    public double surplusMoney; //剩余金额
    public List<InvestScopeBean> investScope;
    public String byStagesType;//标的类型选择

    public static class InvestScopeBean implements Serializable{
        public String key;
        public double money;
        public String type;
        public Long value;

    }

}
