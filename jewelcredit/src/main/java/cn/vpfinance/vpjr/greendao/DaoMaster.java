package cn.vpfinance.vpjr.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.greendao.FinanceProductDao;
import cn.vpfinance.vpjr.greendao.LoanRecordDao;
import cn.vpfinance.vpjr.greendao.BorrowerInfoDao;
import cn.vpfinance.vpjr.greendao.BannerDao;
import cn.vpfinance.vpjr.greendao.InvestRecordDao;
import cn.vpfinance.vpjr.greendao.MyInvestRecordDao;
import cn.vpfinance.vpjr.greendao.QueryPageDao;
import cn.vpfinance.vpjr.greendao.BankCardDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 24): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 24;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        UserDao.createTable(db, ifNotExists);
        FinanceProductDao.createTable(db, ifNotExists);
        LoanRecordDao.createTable(db, ifNotExists);
        BorrowerInfoDao.createTable(db, ifNotExists);
        BannerDao.createTable(db, ifNotExists);
        InvestRecordDao.createTable(db, ifNotExists);
        MyInvestRecordDao.createTable(db, ifNotExists);
        QueryPageDao.createTable(db, ifNotExists);
        BankCardDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        UserDao.dropTable(db, ifExists);
        FinanceProductDao.dropTable(db, ifExists);
        LoanRecordDao.dropTable(db, ifExists);
        BorrowerInfoDao.dropTable(db, ifExists);
        BannerDao.dropTable(db, ifExists);
        InvestRecordDao.dropTable(db, ifExists);
        MyInvestRecordDao.dropTable(db, ifExists);
        QueryPageDao.dropTable(db, ifExists);
        BankCardDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(UserDao.class);
        registerDaoClass(FinanceProductDao.class);
        registerDaoClass(LoanRecordDao.class);
        registerDaoClass(BorrowerInfoDao.class);
        registerDaoClass(BannerDao.class);
        registerDaoClass(InvestRecordDao.class);
        registerDaoClass(MyInvestRecordDao.class);
        registerDaoClass(QueryPageDao.class);
        registerDaoClass(BankCardDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
