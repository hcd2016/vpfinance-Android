package cn.vpfinance.vpjr.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/7/29.
 */
public class VoucherEvent implements Parcelable{

    public String                    type;
    public double                    voucherValue;
    public double                    voucherrate;
    public double                    calcedMoney;
    public int[]                     vouchersArray;
    public HashMap<Integer, Boolean> isSelected;
    public SparseArray<Object>       selectArray;
    public int temp;//不用
    public int addRateId;//不用

    protected VoucherEvent(Parcel in) {
        type = in.readString();
        voucherValue = in.readDouble();
        voucherrate = in.readDouble();
        calcedMoney = in.readDouble();
        vouchersArray = in.createIntArray();
        temp = in.readInt();
        mValue = in.readDouble();
        mAddRatePeriod = in.readInt();
        isSelected = in.readHashMap(ClassLoader.getSystemClassLoader());
        selectArray = in.readSparseArray(ClassLoader.getSystemClassLoader());
        addRateId = in.readInt();
    }

    public static final Creator<VoucherEvent> CREATOR = new Creator<VoucherEvent>() {
        @Override
        public VoucherEvent createFromParcel(Parcel in) {
            return new VoucherEvent(in);
        }

        @Override
        public VoucherEvent[] newArray(int size) {
            return new VoucherEvent[size];
        }
    };

    public SparseArray<Object> getSelectArray() {
        return selectArray;
    }

    public void setSelectArray(SparseArray<Object> selectArray) {
        this.selectArray = selectArray;
    }

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

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        this.isSelected = isSelected;
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        mValue = value;
    }

    public int getAddRatePeriod() {
        return mAddRatePeriod;
    }

    public void setAddRatePeriod(int addRatePeriod) {
        mAddRatePeriod = addRatePeriod;
    }

    private double mValue;
    private int    mAddRatePeriod;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public double getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(double voucherValue) {
        this.voucherValue = voucherValue;
    }

    public double getVoucherrate() {
        return voucherrate;
    }

    public void setVoucherrate(double voucherrate) {
        this.voucherrate = voucherrate;
    }

    public double getCalcedMoney() {
        return calcedMoney;
    }

    public void setCalcedMoney(double calcedMoney) {
        this.calcedMoney = calcedMoney;
    }

    public int[] getVouchersArray() {
        return vouchersArray;
    }

    public void setVouchersArray(int[] vouchersArray) {
        this.vouchersArray = vouchersArray;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeDouble(voucherValue);
        dest.writeDouble(voucherrate);
        dest.writeDouble(calcedMoney);
        dest.writeIntArray(vouchersArray);
        dest.writeInt(temp);
        dest.writeDouble(mValue);
        dest.writeInt(mAddRatePeriod);
        dest.writeMap(isSelected);
        dest.writeSparseArray(selectArray);
        dest.writeInt(addRateId);
    }

    @Override
    public String toString() {
        return "VoucherEvent{" +
                "type='" + type + '\'' +
                ", voucherValue=" + voucherValue +
                ", voucherrate=" + voucherrate +
                ", calcedMoney=" + calcedMoney +
                ", vouchersArray=" + Arrays.toString(vouchersArray) +
                ", isSelected=" + isSelected +
                ", selectArray=" + selectArray +
                ", temp=" + temp +
                ", mValue=" + mValue +
                ", mAddRatePeriod=" + mAddRatePeriod +
                '}';
    }

}
