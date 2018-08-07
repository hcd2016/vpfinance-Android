package cn.vpfinance.vpjr.model;


/**
 * Created by zzlz13 on 2017/4/21.
 */

public class TransferDepositDetailItem {

    public boolean isHeader;
    public String headerTitle;
    public String contentTitle;
    public String contentValue;

    public TransferDepositDetailItem(boolean isHeader, String headerTitle) {
        this.isHeader = isHeader;
        this.headerTitle = headerTitle;
    }

    public TransferDepositDetailItem(boolean isHeader, String contentTitle, String contentValue) {
        this.isHeader = isHeader;
        this.contentTitle = contentTitle;
        this.contentValue = contentValue;
    }
}
