package cn.vpfinance.vpjr.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/8/11.
 */
public class AddRateInfo implements Parcelable{

    public int temp;
    public int addRateId;
    public double addRateMoey;
    private double value;//加息利率
    private int    addRatePeriod;//加息天数
    public String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected AddRateInfo(Parcel in) {
        temp = in.readInt();
        addRateId = in.readInt();
        addRateMoey = in.readDouble();
        value = in.readDouble();
        addRatePeriod = in.readInt();
        type = in.readString();
    }

    public static final Creator<AddRateInfo> CREATOR = new Creator<AddRateInfo>() {
        @Override
        public AddRateInfo createFromParcel(Parcel in) {
            return new AddRateInfo(in);
        }

        @Override
        public AddRateInfo[] newArray(int size) {
            return new AddRateInfo[size];
        }
    };

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getAddRateId() {
        return addRateId;
    }

    public void setAddRateId(int addRateId) {
        this.addRateId = addRateId;
    }

    public double getAddRateMoey() {
        return addRateMoey;
    }

    public void setAddRateMoey(double addRateMoey) {
        this.addRateMoey = addRateMoey;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getAddRatePeriod() {
        return addRatePeriod;
    }

    public void setAddRatePeriod(int addRatePeriod) {
        this.addRatePeriod = addRatePeriod;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(temp);
        dest.writeInt(addRateId);
        dest.writeDouble(addRateMoey);
        dest.writeDouble(value);
        dest.writeInt(addRatePeriod);
        dest.writeString(type);
    }
}
