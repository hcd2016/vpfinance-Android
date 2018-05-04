package cn.vpfinance.vpjr.greendao;

/**
 * Created by Wang Gensheng on 2015/8/1.
 */
public class BonusProfit {
    public int id;                      //
    public double principal;            // 本金
    public double totalEarning;         // 收益
    public long provideTime;            // 发放时间
    public long activeTime;             // 激活时间
    public long earningExpiryTime;      // 收益失效时间
    public long principalExpiryTime;        // 本金失效时间
    public int expiryFlag;                  // 失效标记
    public int activeFlag;                  // 激活标记
    public int status;
}
