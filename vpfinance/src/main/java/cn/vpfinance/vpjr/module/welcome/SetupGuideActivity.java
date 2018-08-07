package cn.vpfinance.vpjr.module.welcome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.BankBean;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.ProvinceAndCity;
import cn.vpfinance.vpjr.util.BankCardUtil;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.IdcardUtils;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import cn.vpfinance.vpjr.view.wheelcity.OnWheelChangedListener;
import cn.vpfinance.vpjr.view.wheelcity.WheelView;
import cn.vpfinance.vpjr.view.wheelcity.adapters.AbstractWheelTextAdapter;
import cn.vpfinance.vpjr.view.wheelcity.adapters.ArrayWheelAdapter;
import de.greenrobot.dao.query.QueryBuilder;

/**
 */
public class SetupGuideActivity extends BaseActivity {

    @Bind(R.id.tvSetupTitle)
    TextView tvSetupTitle;
    @Bind(R.id.ivSetupFinish1)
    ImageView ivSetupFinish1;
    @Bind(R.id.vSetupPadding1)
    View vSetupPadding1;
    @Bind(R.id.etRealname)
    EditTextWithDel etRealname;
    @Bind(R.id.etRealIdcard)
    EditTextWithDel etRealIdcard;
    @Bind(R.id.llSetupContent1)
    LinearLayout llSetupContent1;
    @Bind(R.id.tvSetupTitle2)
    TextView tvSetupTitle2;
    @Bind(R.id.ivSetupFinish2)
    ImageView ivSetupFinish2;
    @Bind(R.id.vSetupPadding2)
    View vSetupPadding2;
    @Bind(R.id.tvBankType)
    TextView tvBankType;
    @Bind(R.id.etBankNum)
    EditTextWithDel etBankNum;
    @Bind(R.id.tvBankArea)
    TextView tvBankArea;
    @Bind(R.id.etBankAreaBranch)
    EditTextWithDel etBankAreaBranch;
    @Bind(R.id.llSetupContent2)
    LinearLayout llSetupContent2;
    @Bind(R.id.tvSetupTitle3)
    TextView tvSetupTitle3;
    @Bind(R.id.ivSetupFinish3)
    ImageView ivSetupFinish3;
    @Bind(R.id.etTradePwd)
    EditTextWithDel etTradePwd;
    @Bind(R.id.etTradePwdAgain)
    EditTextWithDel etTradePwdAgain;
    @Bind(R.id.llSetupContent3)
    LinearLayout llSetupContent3;
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;

    public static final String SETUP_STATE = "setup_state";
    @Bind(R.id.tvSetupSubTitle)
    TextView tvSetupSubTitle;
    @Bind(R.id.tvSetupSubTitle2)
    TextView tvSetupSubTitle2;
    @Bind(R.id.tvSetupSubTitle3)
    TextView tvSetupSubTitle3;

    private int[] setupState;
    private HttpService mHttpService;
    private String realname;
    private String realIdcard;
    private User user;
    private UserDao dao;
    private String bankNum;
    private List<BankBean.BanktypelistEntity> mBanktypelist;
    private String[] bankNames;
    private BankBean.BanktypelistEntity mBank;
    private String cityTxt;
    private int cityId;
    private int provinceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_guide);
        ButterKnife.bind(this);

        mHttpService = new HttpService(this, this);
        mHttpService.getbBankTypeList();

        titleBar
                .setHeadBackVisible(View.VISIBLE)
                .setTitle("完善资料")
                .setActionRight("跳过", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

//        user = DBUtils.getUser(this);
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getUserDao();

        if (dao != null) {
            QueryBuilder<User> qb = dao.queryBuilder();
            List<User> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                user = userList.get(0);
            }
        }

        Intent intent = getIntent();
        if (intent == null) return;
        setupState = intent.getIntArrayExtra(SETUP_STATE);

        if (setupState[0] == 2) {
            //setup realname
            setupGuide(1);
        } else if (setupState[1] == 2) {
            //setup band card
            setupGuide(2);
        } else if (setupState[2] == 2) {
            //setup transfer pwd
            setupGuide(3);
        } else {
            //all setup finish
            setupGuide(0);
        }
    }

    private void setupGuide(int step) {
        initView(step);
        switch (step) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                finish();
                break;
        }
    }

    private void initView(int step) {
        llSetupContent1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        llSetupContent2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        llSetupContent3.setVisibility(step == 3 ? View.VISIBLE : View.GONE);

        ivSetupFinish1.setImageResource(setupState[0] == 1 ? R.drawable.ic_guide_finish : R.drawable.ic_guide_nofinish);
        ivSetupFinish2.setImageResource(setupState[1] == 1 ? R.drawable.ic_guide_finish : R.drawable.ic_guide_nofinish);
        ivSetupFinish3.setImageResource(setupState[2] == 1 ? R.drawable.ic_guide_finish : R.drawable.ic_guide_nofinish);

        ivSetupFinish1.setVisibility(llSetupContent1.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        ivSetupFinish2.setVisibility(llSetupContent2.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        ivSetupFinish3.setVisibility(llSetupContent3.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

        tvSetupSubTitle.setVisibility(llSetupContent1.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);
        tvSetupSubTitle2.setVisibility(llSetupContent2.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);
        tvSetupSubTitle3.setVisibility(llSetupContent3.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);

        vSetupPadding1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        vSetupPadding2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Bind(R.id.tvErrorRealName)
    TextView tvErrorRealName;
    @Bind(R.id.tvErrorRealIdcard)
    TextView tvErrorRealIdcard;

    private void commitSetup1() {
        boolean isError = false;
        tvErrorRealName.setText("");
        tvErrorRealIdcard.setText("");
        realname = etRealname.getText().toString().trim();
        realIdcard = etRealIdcard.getText().toString().trim();
        if (TextUtils.isEmpty(realname)) {
            tvErrorRealName.setText("请输入真实姓名");
            etRealname.requestFocus();
            isError = true;
        }
        if (TextUtils.isEmpty(realIdcard)) {
            tvErrorRealIdcard.setText("请输入身份证号码");
            etRealIdcard.requestFocus();
            isError = true;
        } else {
            boolean right = IdcardUtils.validateCard(realIdcard);
            if (!right) {
                tvErrorRealIdcard.setText("请输入正确的身份证号码");
                etRealIdcard.requestFocus();
                isError = true;
            }
        }
        if (isError) return;
        mHttpService.realnameAuth(realname, realIdcard, AppState.instance().getSessionCode());
    }

    @Bind(R.id.tvErrorBankNum)
    TextView tvErrorBankNum;
    @Bind(R.id.tvErrorBankArea)
    TextView tvErrorBankArea;
    @Bind(R.id.tvErrorBankAreaBranch)
    TextView tvErrorBankAreaBranch;

    private void commitSetup2() {
        boolean isError = false;


        bankNum = etBankNum.getText().toString().trim();
        if (TextUtils.isEmpty(bankNum)) {
            isError = true;
            tvErrorBankNum.setText("银行卡号不能为空");
        } else {
            if (!BankCardUtil.validateCard(bankNum)) {
                isError = true;
                tvErrorBankNum.setText("银行卡号格式有误");
            } else {
                tvErrorBankNum.setText("");
            }
        }
        String bankArea = tvBankArea.getText().toString().trim();
        if (TextUtils.isEmpty(bankArea)) {
            isError = true;
            tvErrorBankArea.setText("请选择开户行所在地");
        } else {
            tvErrorBankArea.setText("");
        }

        String bankAreaBranch = etBankAreaBranch.getText().toString().trim();
        if (TextUtils.isEmpty(bankAreaBranch)) {
            isError = true;
            tvErrorBankAreaBranch.setText("请填写支行名称");
        } else {
            if (bankAreaBranch.length() >= 50) {
                isError = true;
                tvErrorBankAreaBranch.setText("支行名称过长");
            } else {
                tvErrorBankAreaBranch.setText("");
            }
        }

        if (mBank == null){
            Utils.Toast(this,"请选择银行");
            return;
        }

        if (isError) return;

        String bankName = mBank.name;
        String bankAccount = etBankNum.getText().toString().trim();
        String branch = etBankAreaBranch.getText().toString().trim();
        User user = DBUtils.getUser(this);
        if (user != null) {
            realname = user.getRealName();
        }

        mHttpService.addBankCard("" + mBank.id, bankAccount, realname, branch, "" + provinceId, "" + cityId, bankName);
    }

    @Bind(R.id.tvErrorTradePwdAgain)
    TextView tvErrorTradePwdAgain;
    @Bind(R.id.tvErrorTradePwd)
    TextView tvErrorTradePwd;

    private void commitSetup3() {
        boolean isError = false;
        String newP = "";
        newP = etTradePwd.getText().toString().trim();
        if (TextUtils.isEmpty(newP)) {
            isError = true;
            tvErrorTradePwd.setText("交易密码不能为空");
        } else {
            if (!(newP.length() >= 6 && newP.length() <= 20)) {
                isError = true;
                tvErrorTradePwd.setText("请输入6至20位密码");
            } else {
                tvErrorTradePwd.setText("");
            }
        }
        String newP2 = etTradePwdAgain.getText().toString().trim();
        if (TextUtils.isEmpty(newP2)) {
            isError = true;
            tvErrorTradePwdAgain.setText("交易密码不能为空");
        } else {
            if (!newP.equals(newP2)) {
                isError = true;
                tvErrorTradePwdAgain.setText("两次密码输入不一致");
            } else {
                tvErrorTradePwd.setText("");
            }
        }
        if (isError) return;
        Md5Algorithm md5 = Md5Algorithm.getInstance();
        String newP_md5 = md5.md5Digest(newP.getBytes());
        mHttpService.resetTradePassword2New(newP_md5, AppState.instance().getSessionCode());
    }

    private void showBankType() {
        // 选择银行
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		final Bank[] banks = Bank.values();
        if (mBanktypelist == null) {
            return;
        }
        ArrayList<String> bankNameList = new ArrayList<String>();
        for (BankBean.BanktypelistEntity bk : mBanktypelist) {
            bankNameList.add(bk.name);
        }
        bankNames = new String[bankNameList.size()];
        bankNameList.toArray(bankNames);

        builder.setItems(bankNames,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        mBank = mBanktypelist.get(arg1);
                        tvBankType.setText(mBank.name);
                    }
                });
        builder.show();
    }

    private void showBankArea() {
        View view = dialogm();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvBankArea.setText(cityTxt);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private View dialogm() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(3);
        country.setViewAdapter(new CountryAdapter(this));


        final WheelView city = (WheelView) contentView
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(0);

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                ArrayList<ProvinceAndCity.City> cities = ProvinceAndCity.getCity(ProvinceAndCity.getProvince()[newValue]);
                updateCities(city, cities, newValue);
                ProvinceAndCity.Province currentProvince = ProvinceAndCity.getProvince()[country.getCurrentItem()];
                ProvinceAndCity.City currentCity = ProvinceAndCity.getCity(currentProvince).get(city.getCurrentItem());
                cityTxt = currentProvince.getName() + " " + currentCity.getName();
                cityId = currentCity.getId();
                provinceId = currentProvince.getId();
            }
        });

        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                ProvinceAndCity.Province currentProvince = ProvinceAndCity.getProvince()[country.getCurrentItem()];
                ProvinceAndCity.City currentCity = ProvinceAndCity.getCity(currentProvince).get(city.getCurrentItem());
                cityTxt = currentProvince.getName() + " " + currentCity.getName();
                cityId = currentCity.getId();
                provinceId = currentProvince.getId();
            }
        });
        country.setCurrentItem(1);// 设置北京
        city.setCurrentItem(1);
        return contentView;
    }

    @OnClick({R.id.btnSetup1, R.id.btnSetup2, R.id.btnSetup3, R.id.tvBankType, R.id.tvBankArea,R.id.tvLimitAmount})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvLimitAmount:
                //限额
                gotoWeb("/AppContent/tosupportbank","银行卡限额说明");
                break;
            case R.id.btnSetup1:
                commitSetup1();
                break;
            case R.id.btnSetup2:
                commitSetup2();
                break;
            case R.id.btnSetup3:
                commitSetup3();
                break;
            case R.id.tvBankType:
                showBankType();
                break;
            case R.id.tvBankArea:
                showBankArea();
                break;
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_resetTradePasswordNew.ordinal()) {
            String msg = mHttpService.onResetTradePassword2(json);
//            0:用户不存在
//            1: 操作成功
//            5.内部错误
            if (msg.contains("成功") || msg.equals("1")) {
                if(user==null)
                {
                    user = DBUtils.getUser(this);
                }
                user.setHasTradePassword(true);
                if(dao!=null)
                {
                    dao.insertOrReplace(user);
                }
                Utils.Toast(this, msg);
                finish();
            } else {
                Utils.Toast(this, msg);
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_addCard.ordinal()) {
            BankCard card = new BankCard();
            String msg = mHttpService.onAddBankCard(json, card);
            if (msg.contains("成功")) {
                Utils.Toast(this, "绑定成功");
                setupState[1] = 1;
                DaoMaster.DevOpenHelper dbHelper;
                SQLiteDatabase db;
                DaoMaster daoMaster;
                DaoSession daoSession;
                BankCardDao dao;

                dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
                db = dbHelper.getWritableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                dao = daoSession.getBankCardDao();

                if (dao != null) {
                    QueryBuilder<BankCard> qb = dao.queryBuilder();
                    qb.buildDelete().executeDeleteWithoutDetachingEntities();
                    dao.insertInTx(card);
                }

            } else {
                Utils.Toast(this, msg);
            }

//            setResult(1);
            if (setupState[2] == 2) {
                setupGuide(3);
            } else {
                setupGuide(0);
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_Bank_Type_List.ordinal()) {
            BankBean bankBean = mHttpService.onGetBankTypeList(json);
            mBanktypelist = bankBean.banktypelist;
        }

        if (reqId == ServiceCmd.CmdId.CMD_realnameAuth.ordinal()) {
            int msg = json.optInt("msg");
            String tip = "实名认证成功";
            switch (msg) {
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

            Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();

            if (msg == 1) {
                if (user != null) {
                    setupState[0] = 1;
                    user.setRealName(realname);
                    user.setIdentityCard(realIdcard);
                    if (dao != null) {
                        dao.insertOrReplace(user);
                    }
                }
                setupGuide(2);
            }
        }
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, ArrayList<ProvinceAndCity.City> cities, int index) {
        String[] cityNames = new String[cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            String name = cities.get(i).getName();
            cityNames[i] = name;
        }
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
                cityNames);
        adapter.setTextSize(14);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        ProvinceAndCity.Province[] provinces = ProvinceAndCity.getProvince();

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return provinces.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return provinces[index].getName();
        }
    }
}
