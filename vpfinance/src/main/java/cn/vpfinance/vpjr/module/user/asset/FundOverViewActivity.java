package cn.vpfinance.vpjr.module.user.asset;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.dialog.RechargeCloseDialog;
import cn.vpfinance.vpjr.module.trade.RechargBankActivity;
import cn.vpfinance.vpjr.module.trade.WithdrawBankActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.CirclePercentView;
import de.greenrobot.dao.query.QueryBuilder;
/**
 * Created by Administrator on 2015/11/2.
 * 资金总览
 */
public class FundOverViewActivity extends BaseActivity implements View.OnClickListener {
    private BankCard bankCard;
    private String mRealname;
    private ActionBarLayout titleBar;
//    private HttpService mHttpService = null;
    private CirclePercentView circle;
    private String userId;
//    private FundOverInfo fundOverInfo;
    private TextView cashBalance;
    private TextView frozenAmtN;
    private TextView inCount;
    private TextView netAsset;
    private ArrayList<Float> percents;
    private String cashBalanceStr;
    private String inCountStr;
    private String frozenAmtNStr;
    private String netAssetStr;
    private int accountType = 0;
    private String rechargingMoney;
    private TextView tv_total_money;
    private TextView tv_recharging_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_overview);
//        mHttpService = new HttpService(this, this);


        Intent intent = getIntent();
        if(intent != null){
            accountType = intent.getIntExtra(Constant.AccountType,0);
            cashBalanceStr = intent.getStringExtra("cashBalance");
            inCountStr = intent.getStringExtra("inCount");
            frozenAmtNStr = intent.getStringExtra("frozenAmtN");
            netAssetStr = intent.getStringExtra("netAsset");
            rechargingMoney = intent.getStringExtra("rechargingMoney");
        }

        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        BankCardDao dao;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME , null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getBankCardDao();

        if (dao != null) {
            QueryBuilder<BankCard> qb = dao.queryBuilder();
            List<BankCard> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                bankCard = userList.get(0);
            }
        }

        UserDao userDao = daoSession.getUserDao();
        List<User> users = userDao.queryBuilder().list();
        if (users != null && users.size() != 0) {
            User master = users.get(0);
            mRealname = master.getRealName();
            userId = master.getUserId()+"";
        }

        initView();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void initView() {
        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setHeadBackVisible(View.VISIBLE).setTitle("资金总览");

        cashBalance = (TextView) findViewById(R.id.cashBalance);
        frozenAmtN = (TextView) findViewById(R.id.frozenAmtN);
        inCount = (TextView) findViewById(R.id.inCount);
        netAsset = (TextView) findViewById(R.id.netAsset);
        tv_total_money = (TextView) findViewById(R.id.tv_total_money);
        tv_recharging_money = (TextView) findViewById(R.id.tv_recharging_money);

//        findViewById(R.id.clickRecharge).setOnClickListener(this);
//        findViewById(R.id.clickWithdrawal).setOnClickListener(this);
        circle = (CirclePercentView) findViewById(R.id.circle);

//        circle.setProgress(percents);

        //可用余额
        this.cashBalance.setText(cashBalanceStr+"元");
        //冻结金额
        this.frozenAmtN.setText(frozenAmtNStr+"元");
        //在投金额
        this.inCount.setText(inCountStr+"元");
        //资金总额
        this.netAsset.setText(netAssetStr+"元");
        //总余额
        if(!TextUtils.isEmpty(rechargingMoney) && !TextUtils.isEmpty(cashBalanceStr)) {
            float r = Float.parseFloat(rechargingMoney);
            float v = Float.parseFloat(cashBalanceStr);
            tv_total_money.setText(r+v+"元");
            if(r == 0) {
                tv_recharging_money.setText("其中"+0+"元T+1个工作日方可到账");
            }else {
                tv_recharging_money.setText("其中"+rechargingMoney+"元T+1个工作日方可到账");
            }
        }

        //数据
        try{
            if ((!TextUtils.isEmpty(cashBalanceStr)) && (!TextUtils.isEmpty(frozenAmtNStr)) && (!TextUtils.isEmpty(inCountStr)) && (!TextUtils.isEmpty(netAssetStr))) {
                percents = new ArrayList<>();
                float netAssetDouble = Float.parseFloat(netAssetStr);

                float v1 = Float.parseFloat(cashBalanceStr) / netAssetDouble;
                float v2 =  Float.parseFloat(inCountStr) / netAssetDouble;
                float v3 = Float.parseFloat(frozenAmtNStr) / netAssetDouble;

                percents.add(v1);
                percents.add(v2);
                percents.add(v3);
                circle.setProgress(percents);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
//            case R.id.clickRecharge:
//                if (accountType == 0){
//                    //充值入口
//                    boolean isAllowRecharge = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_ALLOW_RECHARGE);
//                    if (isAllowRecharge){
//                        AlertDialogUtils.confirmGoRecharg(this);
//                    }else{
//                        new RechargeCloseDialog().show(getFragmentManager(),"RechargeCloseDialog");
//                    }
//                }else{
//                    startActivity(new Intent(this, RechargBankActivity.class));
//                }
//                break;
//
//            case R.id.clickWithdrawal:
//                if (accountType == 0){
////                    startActivity(new Intent(this, WithdrawDepositActivity.class));
//                }else{
//                    Intent intent = new Intent(this, WithdrawBankActivity.class);
//                    intent.putExtra(WithdrawBankActivity.CASHBALANCE,cashBalanceStr);
//                    intent.putExtra(WithdrawBankActivity.FROZENAMTN,frozenAmtNStr);
//                    startActivity(intent);
//                }
//                break;
        }
    }
}
