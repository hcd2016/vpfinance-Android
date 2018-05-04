package cn.vpfinance.vpjr.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import cn.vpfinance.vpjr.greendao.MyInvestRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MY_INVEST_RECORD".
*/
public class MyInvestRecordDao extends AbstractDao<MyInvestRecord, Long> {

    public static final String TABLENAME = "MY_INVEST_RECORD";

    /**
     * Properties of entity MyInvestRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Page = new Property(1, long.class, "page", false, "PAGE");
        public final static Property PageSize = new Property(2, long.class, "pageSize", false, "PAGE_SIZE");
        public final static Property TotalPage = new Property(3, long.class, "totalPage", false, "TOTAL_PAGE");
        public final static Property Iid = new Property(4, long.class, "iid", false, "IID");
        public final static Property Borrowername = new Property(5, String.class, "borrowername", false, "BORROWERNAME");
        public final static Property Deadline = new Property(6, String.class, "deadline", false, "DEADLINE");
        public final static Property InterestRate = new Property(7, double.class, "interestRate", false, "INTEREST_RATE");
        public final static Property IssueLoan = new Property(8, double.class, "issueLoan", false, "ISSUE_LOAN");
        public final static Property LoanTitle = new Property(9, String.class, "loanTitle", false, "LOAN_TITLE");
        public final static Property Money = new Property(10, double.class, "money", false, "MONEY");
        public final static Property RefundWay = new Property(11, String.class, "refundWay", false, "REFUND_WAY");
        public final static Property Schedule = new Property(12, double.class, "schedule", false, "SCHEDULE");
        public final static Property TenderMoney = new Property(13, double.class, "tenderMoney", false, "TENDER_MONEY");
        public final static Property TenderTime = new Property(14, String.class, "tenderTime", false, "TENDER_TIME");
    };


    public MyInvestRecordDao(DaoConfig config) {
        super(config);
    }
    
    public MyInvestRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MY_INVEST_RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PAGE\" INTEGER NOT NULL ," + // 1: page
                "\"PAGE_SIZE\" INTEGER NOT NULL ," + // 2: pageSize
                "\"TOTAL_PAGE\" INTEGER NOT NULL ," + // 3: totalPage
                "\"IID\" INTEGER NOT NULL ," + // 4: iid
                "\"BORROWERNAME\" TEXT," + // 5: borrowername
                "\"DEADLINE\" TEXT," + // 6: deadline
                "\"INTEREST_RATE\" REAL NOT NULL ," + // 7: interestRate
                "\"ISSUE_LOAN\" REAL NOT NULL ," + // 8: issueLoan
                "\"LOAN_TITLE\" TEXT," + // 9: loanTitle
                "\"MONEY\" REAL NOT NULL ," + // 10: money
                "\"REFUND_WAY\" TEXT," + // 11: refundWay
                "\"SCHEDULE\" REAL NOT NULL ," + // 12: schedule
                "\"TENDER_MONEY\" REAL NOT NULL ," + // 13: tenderMoney
                "\"TENDER_TIME\" TEXT);"); // 14: tenderTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MY_INVEST_RECORD\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MyInvestRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPage());
        stmt.bindLong(3, entity.getPageSize());
        stmt.bindLong(4, entity.getTotalPage());
        stmt.bindLong(5, entity.getIid());
 
        String borrowername = entity.getBorrowername();
        if (borrowername != null) {
            stmt.bindString(6, borrowername);
        }
 
        String deadline = entity.getDeadline();
        if (deadline != null) {
            stmt.bindString(7, deadline);
        }
        stmt.bindDouble(8, entity.getInterestRate());
        stmt.bindDouble(9, entity.getIssueLoan());
 
        String loanTitle = entity.getLoanTitle();
        if (loanTitle != null) {
            stmt.bindString(10, loanTitle);
        }
        stmt.bindDouble(11, entity.getMoney());
 
        String refundWay = entity.getRefundWay();
        if (refundWay != null) {
            stmt.bindString(12, refundWay);
        }
        stmt.bindDouble(13, entity.getSchedule());
        stmt.bindDouble(14, entity.getTenderMoney());
 
        String tenderTime = entity.getTenderTime();
        if (tenderTime != null) {
            stmt.bindString(15, tenderTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public MyInvestRecord readEntity(Cursor cursor, int offset) {
        MyInvestRecord entity = new MyInvestRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // page
            cursor.getLong(offset + 2), // pageSize
            cursor.getLong(offset + 3), // totalPage
            cursor.getLong(offset + 4), // iid
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // borrowername
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // deadline
            cursor.getDouble(offset + 7), // interestRate
            cursor.getDouble(offset + 8), // issueLoan
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // loanTitle
            cursor.getDouble(offset + 10), // money
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // refundWay
            cursor.getDouble(offset + 12), // schedule
            cursor.getDouble(offset + 13), // tenderMoney
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14) // tenderTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MyInvestRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPage(cursor.getLong(offset + 1));
        entity.setPageSize(cursor.getLong(offset + 2));
        entity.setTotalPage(cursor.getLong(offset + 3));
        entity.setIid(cursor.getLong(offset + 4));
        entity.setBorrowername(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDeadline(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setInterestRate(cursor.getDouble(offset + 7));
        entity.setIssueLoan(cursor.getDouble(offset + 8));
        entity.setLoanTitle(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setMoney(cursor.getDouble(offset + 10));
        entity.setRefundWay(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setSchedule(cursor.getDouble(offset + 12));
        entity.setTenderMoney(cursor.getDouble(offset + 13));
        entity.setTenderTime(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MyInvestRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MyInvestRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
