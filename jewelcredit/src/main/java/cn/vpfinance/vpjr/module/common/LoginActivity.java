package cn.vpfinance.vpjr.module.common;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.test.mock.MockApplication;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.ui.widget.ActionBarWhiteLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.setting.ResetLoginPswActivity;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.module.gusturelock.LockSetupActivity;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import de.greenrobot.dao.query.QueryBuilder;

public class LoginActivity extends BaseActivity implements OnClickListener{
	private HttpService mHttpService;
	private EditTextWithDel usernameEd;
	private EditTextWithDel passwordEd;
	private TextView loginRegister;
	private TextView loginForget;
//	private ImageView mCheckBoxImage = null;
//	private LinearLayout mCheckBox = null;
	
	//private CheckBox mCheckBox;
	private String savedPassword = "";

	private UserDao dao = null;
	private User user;

	private String uname;

	private String logPwd;
	
	private Handler mHandler = new Handler();
	
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_v2);
        setContentView(R.layout.activity_login_v2);
        initView();
    }
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//清理login present标志
			HttpService.clearPresentLoginFlag();
		}
		
		return super.onKeyDown(keyCode, event);
	}

	public void getUser() {
		DaoMaster.DevOpenHelper dbHelper;
		SQLiteDatabase db;
		DaoMaster daoMaster;
		DaoSession daoSession;

		if (dao == null) {
			dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
			db = dbHelper.getWritableDatabase();
			daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			dao = daoSession.getUserDao();
		}

		if (dao != null  && AppState.instance().logined()) {
			QueryBuilder<User> qb = dao.queryBuilder();
			List<User> userList = qb.list();
			if (userList != null && userList.size() > 0) {
				user = userList.get(0);
			}
		}
	}
	
	protected void initView() {
		ActionBarWhiteLayout titlebar = (ActionBarWhiteLayout) findViewById(R.id.titleBar);
		titlebar.setTitle("登录").setImageButtonLeft(R.drawable.toolbar_x, new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		usernameEd = (EditTextWithDel)findViewById(R.id.login_username);
		usernameEd.setText(AppState.instance().getLoginUserName());

		SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
		String name = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME);
		usernameEd.setText(name);
		usernameEd.requestFocus();
		if (!TextUtils.isEmpty(name)){
			usernameEd.setSelection(name.length());
		}

		passwordEd = (EditTextWithDel)findViewById(R.id.login_password);
		loginRegister = (TextView) findViewById(R.id.login_register);
		loginForget = (TextView) findViewById(R.id.login_forget);
		
//		mCheckBox = (LinearLayout)findViewById(R.id.login_checkbox);
//		mCheckBoxImage = (ImageView)findViewById(R.id.login_checkbox_image);
//
//		mCheckBox.setOnClickListener(this);
//		mCheckBox.setTag(mCheckBox);
//		mCheckBoxImage.setImageResource(R.drawable.checked);
		
		//mCheckBox = (CheckBox)findViewById(R.id.login_checkbox);
		loginRegister.setOnClickListener(this);
		loginForget.setOnClickListener(this);
		findViewById(R.id.login_commit_btn).setOnClickListener(this);

//		passwordEd.setText("******");
//		savedPassword = AppState.getStringByKey(this, "login_password");
//		if(AppState.getStringByKey(this, "login_checkbox").equals("1"))
//		{
//			mCheckBox.setTag(mCheckBox);
//			mCheckBoxImage.setImageResource(R.drawable.checked);
//			//mCheckBox.setChecked(true);
//
//		}
//		else
//		{
//			mCheckBox.setTag(null);
//			mCheckBoxImage.setImageResource(R.drawable.uncheck);
//		}
		
		/*
		if(!Utils.existKey(this, "login_checkbox"))
		{
			//默认不记住 
			mCheckBox.setTag(null);
			mCheckBoxImage.setImageResource(R.drawable.uncheck);
		}
		*/
		
		mHttpService = new HttpService(this, this);
		
		passwordEd.addTextChangedListener(new TextWatcher() {

	        @Override
	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	savedPassword = "";
	        } 

	    });
	}
	
	
	
	@Override 
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.login_register:
			startActivityForResult(new Intent(this, RegisterActivity.class), 1);
			break;
			
		case R.id.login_forget:
			gotoActivity(ResetLoginPswActivity.class);
			break;
			
		case R.id.login_commit_btn:
			clearDB();
			doLogin();
			break;
		}
	}

	private void clearDB() {
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
		bankDao.deleteAll();
		daoSession.getUserDao().deleteAll();
	}


	private void doLogin()
	{
		uname = ((TextView)findViewById(R.id.login_username)).getText().toString();
		final String name = uname.trim();
		logPwd = ((TextView)findViewById(R.id.login_password)).getText().toString();

		if("".equals(name) || "".equals(logPwd))
		{
			Utils.Toast(this, "用户名或密码不能为空！请重新输入.");
			return;
		}

		if(logPwd.length() <= 0 && logPwd.length() > 0)
		{
			Utils.Toast(this, "登录密码不能为纯空格!");
			return;
		}
		
		if(logPwd.length() < 6)
		{
			Utils.Toast(this, "登录密码不能少于6位!");
			return;
		}
		
//		if(logPwd.length() > 16)
//		{
//			Utils.Toast(this, "登录密码不能多于16位!");
//			return;
//		}
		
		Md5Algorithm md5 = Md5Algorithm.getInstance();
		logPwd = md5.md5Digest((logPwd + HttpService.LOG_KEY).getBytes());
		mHttpService.userLogin(name, logPwd);
	}
	
	
	
	
	@Override
	public void onHttpSuccess(int req, JSONObject json)
	{
		if (!isHttpHandle(json)) return;
		if (req == ServiceCmd.CmdId.CMD_member_center.ordinal() && (!isFinishing())) {
//			mHttpService.getFundOverInfo(""+user.getUserId());
			mHttpService.onGetUserInfo(json, user);

			String message = json.optString("message");
			if (!TextUtils.isEmpty(message) && message.contains("没有登陆")) {
			}else{
				String isNewUser = json.optString("isNewUser","0");
				boolean isNewUserBoolean = "1".equals(isNewUser) ? true : false;
				SharedPreferencesHelper.getInstance(this).putBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER,isNewUserBoolean);
			}
			int needUpdatePwd = json.optInt("needUpdatePwd",0);//1就是需要修改密码
			if (needUpdatePwd == 1){
				((FinanceApplication) getApplication()).isNeedUpdatePwd = true;
			}

			if (user != null) {
				if (dao != null && AppState.instance().logined()) {
					dao.insertOrReplace(user);
				}
				SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
				preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, uname);
				preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME, uname);
				if (user != null) {
					preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_ID, "" + user.getId());
				}
				preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD, logPwd);

				String uid = AppState.instance().getSessionCode();
				String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
				if(!TextUtils.isEmpty(uid) && !uid.equals(savedUid))
				{
					preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID, uid);
				}

				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				//清理login present标志
				HttpService.clearPresentLoginFlag();
				((FinanceApplication) getApplication()).login = true;
				startActivity(new Intent(this, LockSetupActivity.class));
				finish();
			}
		}
		if(req == ServiceCmd.CmdId.CMD_userLogin.ordinal() && (!isFinishing()))
		{
			String msg = mHttpService.onUserLogin(this,json);
			if(!msg.equals(""))
			{
				Utils.Toast(this, msg);
				findViewById(R.id.login_commit_btn).setEnabled(true);
				return;
			}
			else
			{
				((FinanceApplication) getApplication()).isLogin = true;
				mHttpService.getUserInfo();
				getUser();
//				SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
//				preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, uname);
//				preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME, uname);
//				if (user != null) {
//					preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_ID, "" + user.getId());
//				}
//				preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD, logPwd);
//
//				String uid = AppState.instance().getSessionCode();
//				String savedUid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID);
//				if(!TextUtils.isEmpty(uid) && !uid.equals(savedUid))
//				{
//					preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_ID,uid);
//				}
			}
			
			//String uid = AppState.instance().getSessionCode();
			//mHttpService.getBankCard(uid);



		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
        if(requestCode == 1 && AppState.instance().logined()){
        	// 回到首页
        	Intent intent = new Intent();  
	        setResult(RESULT_OK, intent);
	      //清理login present标志
	        HttpService.clearPresentLoginFlag();
	        finish(); 
        }
    }
}
