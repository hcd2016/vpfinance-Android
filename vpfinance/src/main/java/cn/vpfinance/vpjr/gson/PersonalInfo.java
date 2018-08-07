package cn.vpfinance.vpjr.gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vfishv on 16/1/29.
 */
public class PersonalInfo implements Serializable {

    public CreditAssess creditAssess;
    public BorrowerInfo borrowerInfo;
    public List<FkInfo> fkInfo;
    public String loanId;
    public BorrowerAudit borrowerAudit;

    public static class CreditAssess {
        public CreditInfo creditInfo;
        public CreditLV creditLV;
        public CreditRpt creditRpt;
    }

    public static class CreditInfo {
        public int pubBorrwNum;
        public double creditAmt;
        public double lendedAmt;
        public double delayAmt;
        public double borrowedAmt;
        public int succBorrwNum;
        public double retningAmt;
        public int delayNum;
        public int payedNum;
        public double payingAmt;
    }

    public static class CreditLV {
        public String level;
    }

    public static class CreditRpt {
        public CreditCard creditCard;
        public BorrwLoanInfo borrwLoanInfo;
    }

    public static class CreditCard {
        public int num;
        public double creditAmt;
        public int delayNum;
        public double delayAmt;
    }

    public static class BorrwLoanInfo {
        public int loanNum;
        public double loanAmt;
        public double remainAmt;
        public String endDt;
        public String delayNum;
        public double delayAmt;
    }

    public static class BorrowerInfo {
        public BasicInfo basicInfo;
        public JobInfo jobInfo;
        public FinanceInfo financeInfo;
        public BorrowedUse borrowedUse;
    }

    public static class BasicInfo {
        public String userName;
        public int age;
        public String marryStatus;
        public String qualifications;
        public String homeTown;
    }
    public static class JobInfo {
        public String tradeType;
        public String companyScale;
        public String workCity;
        public String duty;
        public String workYear;
    }
    public static class FinanceInfo {
        public double income;
        public double pay;
        public String housecondition;
        public String isBuycar;
        public String havaHouseLoan;
        public String havaCarLoan;
    }
    public static class BorrowedUse {
        public String moneyUse;
    }

    public static class FkInfo {
        public String content;
        public String name;
        public int sort;
    }

    public static class BorrowerAudit {
        public List<AuditList> auditList;
        public List<Files> files;
    }

    public static class AuditList {
        public String itemName;
        public String auditStatus;
        public String auditResult;
        public String auditDate;
    }

    public static class Files {
        public String fileName;
        public String auditStatus;
    }
}
