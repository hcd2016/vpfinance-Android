package com.jewelcredit.model;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 回款查询bean
 * Created by Administrator on 2015/11/11.
 */
public class TradeFlowRecordInfo {

    public String success;
    /*总页数*/
    public String totalPage;
    /*返回上一次值给服务器*/
    public String returnValue;
    /*回款金额总数*/
    public String returnedSumMoney;
    /*回款次数*/
    public String returnedCount;
    /*回款信息*/
    public ArrayList<DataListItem> dataList;

    public class DataListItem{
        /*项目名称*/
        public String loanTitle;
        /*近期回款时间*/
        public String returnMoneyTime;
        /*待回款利息*/
        public String preRepayMoneySum;
        /*已回款利息*/
        public String repayMoneySum;
        /*项目总额*/
        public String tenderMoneySum;
        public ArrayList<DataListItem2> dataList2;

        /*回款时间戳*/
        public String id;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            DataListItem that = (DataListItem) o;

            return !(id != null ? !id.equals(that.id) : that.id != null);

        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        public class DataListItem2 {
            /*回款期次*/
            public String periods;
            /*回款金额*/
            public String preRepayMoney;
            /*回款时间*/
            public String repayTime;
            /*回款状态*/
            public String status;
            /*提前回款  attribute1=于2017-11-30 */
            public String attribute1;
            /*attribute2=提前还款*/
            public String attribute2;
        }

        @Override
        public String toString() {
            return "DataListItem{" +
                    "loanTitle='" + loanTitle + '\'' +
                    ", returnMoneyTime='" + returnMoneyTime + '\'' +
                    ", preRepayMoneySum='" + preRepayMoneySum + '\'' +
                    ", repayMoneySum='" + repayMoneySum + '\'' +
                    ", tenderMoneySum='" + tenderMoneySum + '\'' +
                    ", dataList2=" + dataList2 +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TradeFlowRecordInfo{" +
                "success='" + success + '\'' +
                ", totalPage='" + totalPage + '\'' +
                ", returnValue='" + returnValue + '\'' +
                ", returnedSumMoney='" + returnedSumMoney + '\'' +
                ", returnedCount='" + returnedCount + '\'' +
                ", dataList=" + dataList +
                '}';
    }
}
