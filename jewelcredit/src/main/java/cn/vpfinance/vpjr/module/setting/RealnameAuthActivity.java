package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.IdcardUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2015/7/17.
 */
public class RealnameAuthActivity extends BaseActivity {

    private HttpService mHttpService;

    private LinearLayout lytSetRealnameAuth;
    private EditText etRealname;
    private EditText etIdCardNo;
    private Button btnSubmit;

    private LinearLayout lytShowRealnameAuth;
    private TextView tvRealname;
    private TextView tvIdCard;

    private UserDao dao;
    private User user;
    private String userRealname;
    private String idCard;

    public static final int RESPOND_AUTH_SUCCESS = 0;
    public static final String KEY_USER_REALNAME = "user_realname";

//    public boolean isNewUser;


    public static void goThis(Context context){
        Intent intent = new Intent(context, RealnameAuthActivity.class);
//        intent.putExtra("isNewUser",isNewUser);
        context.startActivity(intent);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_authenQuery.ordinal()) {
            String msg = mHttpService.onRealnameAuth(json);
            if (msg.contains("成功")) {
                Utils.Toast(this, "认证成功");
                User user = DBUtils.getUser(this);
                gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
                finish();
            } else {
                Utils.Toast(this, msg);
                finish();
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_realnameAuth.ordinal()) {
/*
0: 验证失败
1: 成功
2: 身份证号为空或位数不对
3. 身份证号验证失败
4. 名字不为汉字
5. 名字为空
*/
            int msg = json.optInt("msg");
            String tip = "实名认证成功";
            switch (msg)
            {
                case 0:
                    tip = "实名认证验证失败";
                    break;
                case 1:
                    tip = "实名认证成功";
                    break;
                case 2:
                    tip = "身份证号为空或位数不对";
                    break;
                case 3:
                    tip = "身份证号验证失败";
                    break;
                case 4:
                    tip = "名字不为汉字";
                    break;
                case 5:
                    tip = "名字为空";
                    break;
                case 6:
                    tip = "身份证号己存在";
                    break;
            }

            Toast.makeText(this, tip ,Toast.LENGTH_SHORT).show();

            if (msg == 1) {
                if (user != null)
                {
                    user.setRealName(userRealname);
                    user.setIdentityCard(idCard);
                    if(dao!=null)
                    {
                        dao.insertOrReplace(user);
                    }
                    gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
                }
                finish();
            }

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realname_auth);

        ((ActionBarLayout)findViewById(R.id.titleBar)).setTitle("实名认证").setHeadBackVisible(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null){
//            isNewUser = intent.getBooleanExtra("isNewUser",false);
        }

        mHttpService = new HttpService(this, this);

        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME , null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getUserDao();

        if (dao != null) {
            QueryBuilder<User> qb = dao.queryBuilder();
            List<User> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                user = userList.get(0);
                if(user!=null && AppState.instance().logined())
                {
                    userRealname = user.getRealName();
                    idCard = user.getIdentityCard();
                }
            }
        }

        lytSetRealnameAuth = (LinearLayout) findViewById(R.id.lytSetRealnameAuth);
        lytShowRealnameAuth = (LinearLayout) findViewById(R.id.lytShowRealnameAuth);

        etRealname = (EditText) findViewById(R.id.realName);
        etIdCardNo = (EditText) findViewById(R.id.idCard);
        btnSubmit = (Button) findViewById(R.id.submit);

        tvRealname = (TextView) findViewById(R.id.tvRealname);
        tvIdCard = (TextView) findViewById(R.id.tvIdCard);

        if (TextUtils.isEmpty(userRealname) || TextUtils.isEmpty(idCard)) {
            lytSetRealnameAuth.setVisibility(View.VISIBLE);
            lytShowRealnameAuth.setVisibility(View.GONE);

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doAuth();
                }
            });
        } else {
            lytSetRealnameAuth.setVisibility(View.GONE);
            lytShowRealnameAuth.setVisibility(View.VISIBLE);

            tvRealname.setText(userRealname);
            tvIdCard.setText(FormatUtils.hideIdCard(idCard));
        }
    }

    private void doAuth() {
        String name = etRealname.getText().toString();
        String no = etIdCardNo.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
            etRealname.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(name) && name.length() == 1){
            Toast.makeText(this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
            etRealname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(no)) {
            Toast.makeText(this,"请输入身份证号码",Toast.LENGTH_SHORT).show();
            etIdCardNo.requestFocus();
            return;
        }

        boolean right = IdcardUtils.validateCard(no);
        if (!right) {
            Toast.makeText(this, "请输入正确的身份证号码", Toast.LENGTH_SHORT).show();
            etIdCardNo.requestFocus();
            return;
        }

        userRealname = name.trim();
        idCard = no.trim();
        mHttpService.realnameAuth(userRealname, idCard, AppState.instance().getSessionCode());
    }
}
