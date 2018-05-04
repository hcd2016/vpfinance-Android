package cn.vpfinance.vpjr.module.trade;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.module.setting.BankAddActivity2;
import cn.vpfinance.vpjr.module.user.personal.BankManageActivity;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.module.dialog.WithdrawDialogFragment;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.FundOverInfo;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 提现
 *
 * Created by Administrator on 2015/7/17.
 */
public class WithdrawDepositActivity extends BaseActivity {

	private HttpService mHttpService;

	private BankCard bankCard;

	private EditText etMoney;

	private UserDao userDao;
	private User    user;
	private long    mGetCodeDelay;
	String mUserPhone;

	public static final double WITHDRAWAL_EXTRA_COST = 2.0;        // 提现手续费2.0元
	private Button   btnGetCode;
	private TextView remaining_money;
	private TextView tvWithdrawCost;
	private TextView tvFactMoney;
	private double   cashBalance;
	private double wMoney = 0.00;

	private long lastWithDrawTime = 0;
	private String       uid;
	private FundOverInfo fundOverInfo;
	private LinearLayout ll_info;
	private double       basic;
	private double       unused;
	private double       withdrawMoney;
	private double       maxWithDraw;//最大可提现金额
	private Button       btnSubmit;
	private TextView     getAllMoney;
	private double fee       = 2;
	private double factMoney = 0.0;
	private ImageView mBankLogo;
	private TextView  mBankname;
	private TextView mBankaccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_withdraw_deposit2);

		((ActionBarLayout) findViewById(R.id.titleBar)).setTitle("提现").setHeadBackVisible(View.VISIBLE);
		mHttpService = new HttpService(this, this);
		ll_info = (LinearLayout) findViewById(R.id.ll_info);
		findViewById(R.id.llClickToBank).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoActivity(BankManageActivity.class);
			}
		});
		etMoney = (EditText) findViewById(R.id.etMoney);
		btnSubmit = (Button) findViewById(R.id.submit);
		btnSubmit.setEnabled(false);
		tvFactMoney = (TextView) findViewById(R.id.tvFactMoney);
		mBankLogo = ((ImageView) findViewById(R.id.bankLogo));
		mBankname = (TextView) findViewById(R.id.bankname);
		mBankaccount = (TextView) findViewById(R.id.bankaccount);

		tvWithdrawCost = (TextView) findViewById(R.id.tvWithdrawCost);
		etMoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (TextUtils.isEmpty(s)) {
					tvFactMoney.setText("0元");
					tvWithdrawCost.setText("0元");
					btnSubmit.setEnabled(false);
					return;
				}

				tvFactMoney.setText("计算中..");
				tvWithdrawCost.setText("计算中..");

				String s1 = s.toString().trim();
				if (s1.contains(".")) {
					int indexOf = s1.indexOf(".");
					if (s1.length() - indexOf > 3) {
						Utils.Toast(WithdrawDepositActivity.this, "输入金额只能精确到分");
						s1 = s1.substring(0, indexOf + 3);
						etMoney.setText(s1);
						etMoney.setSelection(s1.length());
					}
				}

				try {
					withdrawMoney = Double.parseDouble(s1);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					withdrawMoney = 0;
				}

				if(withdrawMoney <= 2.00){
					tvFactMoney.setText("0元");
					tvWithdrawCost.setText("2元");
					btnSubmit.setEnabled(false);
//					Utils.Toast(WithdrawDepositActivity.this, "提现费用需超过手续费");
					return;
				}

				if (withdrawMoney > cashBalance){
					btnSubmit.setEnabled(false);
					Utils.Toast(WithdrawDepositActivity.this, "超过可提现余额");
				}else{
					mHttpService.getWithdrawCharge(withdrawMoney+"");
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		remaining_money = (TextView) findViewById(R.id.remaining_money);
		etMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
//				ll_info.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
			}
		});


		DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        BankCardDao bankDao;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME , null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        bankDao = daoSession.getBankCardDao();
		userDao = daoSession.getUserDao();

		if (bankDao != null) {
			QueryBuilder<BankCard> qb = bankDao.queryBuilder();
			List<BankCard> userList = qb.list();
			if (userList != null && userList.size() > 0) {
				bankCard = userList.get(0);
			}
		}
		if (userDao != null) {
			QueryBuilder<User> qb = userDao.queryBuilder();
			List<User> userList = qb.list();
			if (userList != null && userList.size() > 0) {
				user = userList.get(0);
				uid = user.getUserId()+"";
				mHttpService.getFundOverInfo(uid, Constant.AccountLianLain);
			}
		}

		getAllMoney = (TextView) findViewById(R.id.getAllMoney);
		getAllMoney.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				etMoney.setText(String.format("%.2f", maxWithDraw));
			}
		});

		btnSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (factMoney <= 0){
					Toast.makeText(WithdrawDepositActivity.this, "请刷新重试！", Toast.LENGTH_SHORT).show();
					return;
				}

				String m = etMoney.getText().toString();
//				String m = "" + factMoney;
				if (TextUtils.isEmpty(m)) {
					Toast.makeText(WithdrawDepositActivity.this, "请输入提现金额！", Toast.LENGTH_SHORT).show();
					return;
				}
				double mon = 0;

				m = m.trim();
				if (m.contains(".")) {
					int dotIndex = m.indexOf(".");
					if (m.length() - dotIndex > 3) {
						Toast.makeText(WithdrawDepositActivity.this, "输入金额要求精确到分", Toast.LENGTH_SHORT).show();
						etMoney.setText(m.substring(0, dotIndex + 3));
						return;
					}
				}
				try {
					mon = Double.parseDouble(m);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				if (mon > 0) {
					double v1 = mon - maxWithDraw;
					if (v1 > 0) {
						Toast.makeText(WithdrawDepositActivity.this, "超过可提现金额!", Toast.LENGTH_SHORT).show();
						return;
					}

					if (maxWithDraw - fee  <= 0){
						Toast.makeText(WithdrawDepositActivity.this, "余额不足扣除手续费!", Toast.LENGTH_SHORT).show();
						return;
					}

					if (user != null) {

						String mRealname = user.getRealName();
						String idCard = user.getIdentityCard();

						if (TextUtils.isEmpty(mRealname) || TextUtils.isEmpty(idCard)) {
							AlertDialogUtils.showAlertDialog(WithdrawDepositActivity.this, "您未实名认证", "取消", "去认证", RealnameAuthActivity.class);
							return;
						}
						if (!user.getHasTradePassword()) {
//							Toast.makeText(WithdrawDepositActivity.this, "请先设置交易密码", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(WithdrawDepositActivity.this, PasswordChangeActivity.class);
							intent.putExtra(PasswordChangeActivity.EXTRA_KEY_INDEX, 1);
							AlertDialogUtils.showAlertDialog(WithdrawDepositActivity.this, "您未设置交易密码", "取消", "去设置", intent);
//							startActivity(intent);
							return;
						}
					}

					if (bankCard == null) {
						AlertDialogUtils.showAlertDialog(WithdrawDepositActivity.this, "您未绑定银行卡", "取消", "去绑定", BankAddActivity2.class);
						return;
					}

					wMoney = mon;

					WithdrawDialogFragment dialog = WithdrawDialogFragment.newInstance(wMoney + "");
					dialog.setOnTextConfrimListener(new WithdrawDialogFragment.onTextConfrimListener() {
						@Override
						public boolean onTextConfrim(String value, String value2) {
							String uid = AppState.instance().getSessionCode();
							//需要验证码
							Md5Algorithm md5 = Md5Algorithm.getInstance();
							String pwd = md5.md5Digest(value.getBytes());
							mHttpService.withdraw(uid, "" + wMoney, value2, pwd);
							return false;
						}
					});
					dialog.show(getSupportFragmentManager(), "WithdrawDialog");
				} else {
					Toast.makeText(WithdrawDepositActivity.this, "请输入正确的金额", Toast.LENGTH_LONG).show();
				}
			}
		});

//		if (bankCard == null) {
//			AlertDialogUtils.showAlertDialog(WithdrawDepositActivity.this, "您未绑定银行卡", "取消", "去绑定", BankAddActivity2.class);
//			return;
//		}
	}

	@Override
	public void onHttpError(int reqId, String errmsg) {
		super.onHttpError(reqId, errmsg);
		if (isFinishing())	return;
		if (reqId == ServiceCmd.CmdId.CMD_withdrawCharge.ordinal()) {
			tvFactMoney.setText("计算中..");
			tvWithdrawCost.setText("计算中..");
			btnSubmit.setEnabled(false);
		}
	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		if (!isHttpHandle(json)) return;
		if (reqId == ServiceCmd.CmdId.CMD_FundOverView.ordinal()) {
			fundOverInfo = mHttpService.onGetFundOverInfo(json);
			/*可用余额*/
//			String cashBalance1 = fundOverInfo.getCashBalance();
//			double cash = 0;
//			try {
//				cash = Double.parseDouble(cashBalance1);
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//			}
//			maxWithDraw = Utils.doubleFloor(cash);
//			maxWithDraw = Utils.doubleFloor(Double.parseDouble(fundOverInfo.getCashBalance()));
			String cashBalanceStr = FormatUtils.formatDown(fundOverInfo.getCashBalance());
			remaining_money.setText(cashBalanceStr);
			try {
//				cashBalance = Double.parseDouble(fundOverInfo.getCashBalance());
				cashBalance = Double.parseDouble(cashBalanceStr);
				maxWithDraw = cashBalance;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (reqId == ServiceCmd.CmdId.CMD_withdrawCharge.ordinal()) {
			btnSubmit.setEnabled(true);
			try {
				if (json != null){

					basic = json.optDouble("basic");
					unused = json.optDouble("unused");

					fee = basic + unused;
					tvWithdrawCost.setText(String.format("%.2f", fee) + "元");

					String mt = etMoney.getText().toString();
					double d = 0;
					try {
						if(!TextUtils.isEmpty(mt))
						{
							d = Double.parseDouble(mt);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}

					if (withdrawMoney != d) {
						return;
					}
					double v = d - fee;
					if (v > 0) {
						factMoney = v;
						tvFactMoney.setText(String.format("%.2f", v) + "元");
					}
					else
					{
						tvFactMoney.setText(0 + "元");
					}
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		if (reqId == ServiceCmd.CmdId.CMD_getBankCard.ordinal()) {
			if(json == null ) return;
			bankCard = mHttpService.onGetBankCard(this, json);
			JSONObject userbank = json.optJSONObject("userbank");
			if (userbank != null) {
				JSONObject banktype = userbank.optJSONObject("banktype");
				if (banktype != null) {

					String bankimgurl = banktype.optString("bankimgurl");
					ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + bankimgurl, mBankLogo);
					mBankname.setText(bankCard.getBankname());
					mBankaccount.setText(FormatUtils.hideBank(bankCard.getBankAccount()));
				}
			}

		}
		if (reqId == ServiceCmd.CmdId.CMD_WITHDRAW.ordinal()) {
			if(json!=null)
			{
				mHttpService.getFundOverInfo(uid,Constant.AccountLianLain);
				int msg = json.optInt("msg");
				if(1==msg)
				{
					Toast.makeText(WithdrawDepositActivity.this,"提现申请成功.",Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(WithdrawDepositActivity.this, WithdrawSuccessActivity.class);
					intent.putExtra(WithdrawSuccessActivity.BANKNAME,bankCard.getBankname());
					intent.putExtra(WithdrawSuccessActivity.BANKCARDNUM,bankCard.getBankAccount());
					intent.putExtra(WithdrawSuccessActivity.WITHDRAWMONEY, etMoney.getText().toString());
					String s = String.format("%.2f", factMoney);
					intent.putExtra(WithdrawSuccessActivity.FACT_MONEY,s);
					startActivity(intent);
					finish();
				}
				else {
					String str = "";
					switch (msg)
					{
						case 0:
							break;
						case 2:
							str = "您输入的金额不正确";
							break;
						case 3:
							str = "您的余额不足以发起此次提现";
							break;
						case 4:
							str = "验证码不正确";
							break;
						case 5:
							str = "交易密码错误";
							break;
					}
					Toast.makeText(WithdrawDepositActivity.this,"提现申请失败:" + str, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mHttpService != null) {
			mHttpService.getBankCard(AppState.instance().getSessionCode());
		}

		if(AppState.instance().logined())
		{
			if (userDao != null) {
				QueryBuilder<User> qb = userDao.queryBuilder();
				List<User> userList = qb.list();
				if (userList != null && userList.size() > 0) {
					user = userList.get(0);
					mUserPhone = user.getCellPhone();
				}
			}
		}
	}
}