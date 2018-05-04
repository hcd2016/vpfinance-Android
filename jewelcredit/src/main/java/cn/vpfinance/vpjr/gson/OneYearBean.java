package cn.vpfinance.vpjr.gson;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/5/24.
 */
public class OneYearBean implements Parcelable{

    /**
     * cashTime : 1
     * allowRedPacket : 0
     * allowGetVoucher : 0
     */

    public String cashTime;
    public String allowRedPacket;
    public String allowGetVoucher;

    protected OneYearBean(Parcel in) {
        cashTime = in.readString();
        allowRedPacket = in.readString();
        allowGetVoucher = in.readString();
    }

    public static final Creator<OneYearBean> CREATOR = new Creator<OneYearBean>() {
        @Override
        public OneYearBean createFromParcel(Parcel in) {
            return new OneYearBean(in);
        }

        @Override
        public OneYearBean[] newArray(int size) {
            return new OneYearBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cashTime);
        dest.writeString(allowRedPacket);
        dest.writeString(allowGetVoucher);
    }
}
