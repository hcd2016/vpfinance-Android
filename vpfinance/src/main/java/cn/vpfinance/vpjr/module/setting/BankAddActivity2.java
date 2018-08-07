package cn.vpfinance.vpjr.module.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.IdcardUtils;
import cn.vpfinance.vpjr.view.wheelcity.OnWheelChangedListener;
import cn.vpfinance.vpjr.view.wheelcity.WheelView;
import cn.vpfinance.vpjr.view.wheelcity.adapters.AbstractWheelTextAdapter;
import cn.vpfinance.vpjr.view.wheelcity.adapters.ArrayWheelAdapter;
import de.greenrobot.dao.query.QueryBuilder;

public class BankAddActivity2 extends BaseActivity implements View.OnClickListener {

	private EditText     etReName;
	private EditText     etIdCard;
	private EditText     etBankCardNum;
	private LinearLayout llSelectBankType;
	private LinearLayout llSelectBankArea;
	private EditText     etBankName;
	private TextView     tvSelectBankArea;
	private TextView     tvSelectBankType;
	private String[]     bankNames;
	private String       cityTxt;
	private int          provinceId;
	private int          cityId;
	private Button       btnSave;
	private HttpService  mHttpService;
	private User         user;
	private String       realName;
	private UserDao      userDao;
	//	private boolean hasReName = false;
	//	private boolean hasIdcard = false;
	private boolean hasSendRenameRequest = true;
	private List<BankBean.BanktypelistEntity> mBanktypelist;
	private BankBean.BanktypelistEntity mBank;

	private void findView() {
		etReName = ((EditText) findViewById(R.id.etReName));
		etIdCard = ((EditText) findViewById(R.id.etIdCard));
		etBankCardNum = ((EditText) findViewById(R.id.etBankCardNum));
		llSelectBankType = ((LinearLayout) findViewById(R.id.llSelectBankType));
		llSelectBankArea = ((LinearLayout) findViewById(R.id.llSelectBankArea));
		etBankName = ((EditText) findViewById(R.id.etBankName));
		tvSelectBankArea = ((TextView) findViewById(R.id.tvSelectBankArea));
		tvSelectBankType = ((TextView) findViewById(R.id.tvSelectBankType));
		btnSave = ((Button) findViewById(R.id.btnSave));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_bank_add2);
		mHttpService = new HttpService(this, this);

		DaoMaster.DevOpenHelper dbHelper;
		SQLiteDatabase db;
		DaoMaster daoMaster;
		DaoSession daoSession;

		dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
		db = dbHelper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		userDao = daoSession.getUserDao();

		mHttpService.getbBankTypeList();
		findView();
		initView();
	}

	protected void initView() {
		((ActionBarLayout) findViewById(R.id.titleBar)).setTitle(getString(R.string.bankadd_title)).setHeadBackVisible(View.VISIBLE);
		findViewById(R.id.tvLimitAmount).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoWeb("/AppContent/tosupportbank","银行卡限额说明");
			}
		});

		user = DBUtils.getUser(this);
//		Logger.d(user + "");
		realName = user.getRealName();
		if (!TextUtils.isEmpty(realName)) {
			etReName.setText(realName);
			etReName.setFocusable(false);
			etReName.setFocusableInTouchMode(false);
		}
		String idCard = user.getIdentityCard();
		if (!TextUtils.isEmpty(idCard) && IdcardUtils.validateCard(idCard)) {
			etIdCard.setText(idCard);
			etIdCard.setFocusable(false);
			etIdCard.setFocusableInTouchMode(false);
			hasSendRenameRequest = false;
		}

		llSelectBankType.setOnClickListener(this);
		llSelectBankArea.setOnClickListener(this);
		btnSave.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.llSelectBankType:
				showBankType();
				break;
			case R.id.llSelectBankArea:
				showBankArea();
				break;
			case R.id.btnSave:
				Save();
				break;
		}
	}

	private void Save() {
		String name = etReName.getText().toString();
		String no = etIdCard.getText().toString();

		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
			etReName.requestFocus();
			return;
		}

		if (TextUtils.isEmpty(no)) {
			Toast.makeText(this,"请输入身份证号码",Toast.LENGTH_SHORT).show();
			etBankCardNum.requestFocus();
			return;
		}

		boolean right = IdcardUtils.validateCard(no);
		if (!right) {
			Toast.makeText(this, "请输入正确的身份证号码", Toast.LENGTH_SHORT).show();
			etBankCardNum.requestFocus();
			return;
		}

		String bankAccount = etBankCardNum.getText().toString().trim();
		String branch = etBankName.getText().toString().trim();

		if (mBank == null) {
			Utils.Toast(this, "请选择银行");
			return;
		}

		if(bankAccount.equals(""))
		{
			Utils.Toast(this, "银行卡号不能为空!");
			return;
		}

		if (!BankCardUtil.validateCard(bankAccount)){
			Utils.Toast(this, "银行卡号格式有误!");
			return;
		}
//		if(bankAccount.length() < 10 || bankAccount.length() > 20)
//		{
//			Utils.Toast(this, "银行卡号格式有误!");
//			return;
//		}

		if (TextUtils.isEmpty(tvSelectBankArea.getText().toString().trim())) {
			Utils.Toast(this, "请选择开户行所在地!");
			return;
		}
		String[] cityTxts = cityTxt.split(" ");

		if("".equals(cityTxts[0]))
		{
			Utils.Toast(this, "请选择开户行所在的省!");
			return;
		}

		if("".equals(cityTxts[1]))
		{
			Utils.Toast(this, "请选择开户行所在的市!");
			return;
		}

		if(TextUtils.isEmpty(branch))
		{
			Utils.Toast(this, "请填写支行名称!");
			return;
		}

		if(branch.length() >= 50)
		{
			Utils.Toast(this, "支行名称过长!");
			return;
		}

//		if(mSms.getText().equals(""))
//		{
//			Utils.Toast(this, "手机验证码不能为空!");
//			return;
//		}

		if (hasSendRenameRequest){
			mHttpService.realnameAuth(name, no, AppState.instance().getSessionCode());
		}else{
			mHttpService.addBankCard("" + mBank.id, bankAccount, realName, branch, "" + provinceId, "" + cityId, mBank.name);
		}
	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		if (!isHttpHandle(json)) return;
		if(reqId == ServiceCmd.CmdId.CMD_addCard.ordinal()) {
			BankCard card = new BankCard();
			String msg = mHttpService.onAddBankCard(json, card);
			if(msg.contains("成功")) {
				Utils.Toast(this, "绑定成功");

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
					qb.buildDelete().executeDeleteWithoutDetachingEntities();
					dao.insertInTx(card);
				}

			} else {
				Utils.Toast(this, msg);
			}

			setResult(1);
			finish();
		}

		if (reqId == ServiceCmd.CmdId.CMD_realnameAuth.ordinal()) {
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
			if (msg == 1) {

				if (user != null)
				{
					user.setRealName(realName);
					user.setIdentityCard(etBankCardNum.getText().toString());
					if(userDao!=null)
					{
						userDao.insertOrReplace(user);
					}
				}

				String bankName = mBank.name;
				String bankAccount = etBankCardNum.getText().toString().trim();
				String branch = etBankName.getText().toString().trim();

				mHttpService.addBankCard("" + mBank.id, bankAccount, realName, branch, "" + provinceId, "" + cityId, bankName);
			}else{
				Utils.Toast(BankAddActivity2.this,tip);
			}

		}

		if (reqId == ServiceCmd.CmdId.CMD_Bank_Type_List.ordinal()) {
			BankBean bankBean = mHttpService.onGetBankTypeList(json);
			mBanktypelist = bankBean.banktypelist;
		}
	}

	private void showBankArea() {
		View view = dialogm();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				tvSelectBankArea.setText(cityTxt);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
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
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						mBank = mBanktypelist.get(arg1);
						BankAddActivity2.this.tvSelectBankType
								.setText(mBank.name);
					}
				});
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