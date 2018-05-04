package cn.vpfinance.vpjr.gson;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 */
public class NewTransferProductBean {


    /**
     * buyCount : 1
     * reward : 0.0
     * remark :
     * status : 2
     * sourceRate : 9.6%
     * canBuyMoney : 1.84
     * data : []
     * loanTitle : V成长-润富-005
     * noRepayList : [{"repaytime":"2016-11-07","periods":5,"repayStatus":1},{"repaytime":"2016-12-07","periods":6,"repayStatus":1}]
     * disType : 平价转让，转让总额即为原始本金
     * rate : 0.0960
     * subType : 2
     * bidEndTime : 1477756800000
     * month : 2个月
     * sourceLoanId : 441
     * refundWay : 按月付息到期还本
     * hadTenderMoney : 100.0
     * isDebt : 1
     * issueloan : 101.85
     * loanId : 728
     */
    public int product;
    public int productType;

    public String buyCount;
    public String reward;
    public String remark;
    public int status;
    public double sourceRate;
    public double canBuyMoney;
    public String loanTitle;
    public String disType;
    public double rate;
    public int subType;
    public long bidEndTime;
    public String month;
    public int sourceLoanId;
    public String refundWay;
    public double hadTenderMoney;
    public String isDebt;
    public double issueloan;
    public int loanId;
    public double originIssueLoan;
    public Integer frequency;
    public int    answerStatus;//1不弹 2弹提示框, 去做风险测评
    /**
     * repaytime : 2016-11-07
     * periods : 5
     * repayStatus : 1
     */

    public List<NoRepayListBean> noRepayList;

    public static class NoRepayListBean implements Parcelable{
        public String repaytime;
        public String periods;
        public String repayStatus;

        protected NoRepayListBean(Parcel in) {
            repaytime = in.readString();
            periods = in.readString();
            repayStatus = in.readString();
        }

        public static final Creator<NoRepayListBean> CREATOR = new Creator<NoRepayListBean>() {
            @Override
            public NoRepayListBean createFromParcel(Parcel in) {
                return new NoRepayListBean(in);
            }

            @Override
            public NoRepayListBean[] newArray(int size) {
                return new NoRepayListBean[size];
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
