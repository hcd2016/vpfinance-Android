package cn.vpfinance.vpjr.gson;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 */
public class NewBaseInfoBean {


    /**
     * buyCount : 2
     * reward : 0.0
     * publishTime : 1478077784000
     * remark :
     * status : 1
     * canBuyMoney : 499500.00
     * data : [{"value":"1477904984000","type":"2","key":"剩余投标时间"},{"value":"满标后次日开始计息","type":"1","key":"起息标准"},{"value":"按月付息到期还本","type":"1","key":"起息日"},{"value":"沈阳乾润科技产业园有限公司","type":"1","key":"借款人"},{"value":"低","type":"1","key":"风险等级"}]
     * loanTitle : V孵化-沈阳-062
     * rate : 9.6%
     * subType : 2
     * bidEndTime : 1477904984000
     * month : 6个月
     * hadTenderMoney : 500.0
     * isDebt : 2
     * issueloan : 500000.0
     * loanId : 723
     */
    public float process;//进度
    public Integer frequency;//出借记录总条数
    public String buyCount;
    public String givePhone;
    public String imageUrl;
    public double reward;
    public String   publishTime;
    public String remark;
    public String    status;
    public double canBuyMoney;
    public String loanTitle;
    public double rate;
    public int subType;
    public String   bidEndTime;
    public String month;
    public double hadTenderMoney;
    public String lastTenderTime;
    public String allowTransfer;
    public String isAllowTrip;
    public double bookPercent;
    public String bookCouponNumber;
    public String imageTagUrl;
    public String imageUrlJump;
    public String isDebt;
    public double issueloan;
    public long    loanId;
    public int    product;//4 银行存管
    public int    answerStatus;//1不弹 2弹提示框, 去做风险测评
    public int    graceDays; //是否是浮动计息,大于0为浮动计息
    public String    flowInvestReminder; //浮动计息文案
    public int riskLevel;//标的风险等级 1 保守型 2稳健型 3积极型

    /**
     * value : 1477904984000
     * type : 2
     * key : 剩余投标时间
     */

    public List<DataEntity> data;

    public static class DataEntity {
        public String value;
        public String type;
        public String key;
    }

    public List<RepaysEntity> repays;

    public static class RepaysEntity implements Parcelable{
        public String repaytime;
        public String periods;
        public String repayStatus;

        public RepaysEntity(Parcel in) {
            repaytime = in.readString();
            periods = in.readString();
            repayStatus = in.readString();
        }

        public static final Creator<RepaysEntity> CREATOR = new Creator<RepaysEntity>() {
            @Override
            public RepaysEntity createFromParcel(Parcel in) {
                return new RepaysEntity(in);
            }

            @Override
            public RepaysEntity[] newArray(int size) {
                return new RepaysEntity[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(repaytime);
            dest.writeString(periods);
            dest.writeString(repayStatus);
        }
    }
}
