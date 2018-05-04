package cn.vpfinance.vpjr.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jewelcredit.util.AppState;

import java.util.List;

import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import de.greenrobot.dao.query.QueryBuilder;

/**
 */
public class DBUtils {
    public static User getUser(Context mContext){
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        dbHelper = new DaoMaster.DevOpenHelper(mContext, Config.DB_NAME , null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        UserDao userDao = daoSession.getUserDao();

        if(AppState.instance().logined())
        {
            if (userDao != null) {
                QueryBuilder<User> qb = userDao.queryBuilder();
                List<User> userList = qb.list();
                if (userList != null && userList.size() > 0) {
                    return userList.get(0);
                }
            }
        }
        else
        {
//            Toast.makeText(mContext, "请登录.", Toast.LENGTH_SHORT).show();
//            context.startActivity(new Intent(context, LoginActivity.class));
//            finish();
        }
        return null;
    }
    public static BankCard getBankCardByDB(Context mContext){
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        BankCardDao bankDao;

        dbHelper = new DaoMaster.DevOpenHelper(mContext, Config.DB_NAME , null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        bankDao = daoSession.getBankCardDao();

        if (bankDao != null) {
            QueryBuilder<BankCard> qb = bankDao.queryBuilder();
            List<BankCard> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                return userList.get(0);
            }
        }
        return null;
    }

    public static String getPhone(Context context) {
        User user = getUser(context);
        if (user != null) {
            return user.getCellPhone();
        }
        return "";
    }
}
