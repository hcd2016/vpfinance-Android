package cn.vpfinance.vpjr.module.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.util.CaptchaHelper;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.EditTextWithDel;
import cn.vpfinance.vpjr.view.VerificationCodeDialog;

public class ResetLoginPswActivity extends BaseActivity implements OnClickListener, VerificationCodeDialog.SmsListener {

	private HttpService mHttpService;

	private EditText edtPhone;
	private EditTextWithDel  edtNewPwd;
	private EditTextWithDel  edtNewPwdAgain;
	private Button btnGetCode;
	private EditText edtCode;
	private TextView tvPasswordStrength;
	private Button btnSubmit;

//	private Handler mHandler = new Handler();

//	private long mGetCodeDelay;

//	private Runnable mCountDownTimer = new Runnable() {
//		@Override
//		public void run() {
//			if (mGetCodeDelay > 0) {
//				mHandler.postDelayed(this, 1000);
//				btnGetCode.setText(String.format("重新获取(%ds)", mGetCodeDelay));
//			} else {
//				btnGetCode.setText("重新获取");
//				btnGetCode.setEnabled(true);
//			}
//			mGetCodeDelay--;
//		}
//	};
	private TextView mVoiceCaptcha;
	private CaptchaHelper mCaptchaHelper;
	private static final int SMS_CAPTCHA = 1;
	private static final int VOICE_CAPTCHA = 2;
	private int captchaType = 0;//1 sms ;2 voice
	private String mPhone;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_reset_login_password);

		mHttpService = new HttpService(this, this);
        initView();
    }

	protected void initView() {
		((ActionBarLayout)findViewById(R.id.titleBar)).setTitle("找回密码").setHeadBackVisible(View.VISIBLE);

		mVoiceCaptcha = (TextView)findViewById(R.id.voiceCaptcha);
		tvPasswordStrength = (TextView)findViewById(R.id.tvPasswordStrength);
		mVoiceCaptcha.setOnClickListener(this);
		edtPhone = (EditText ) findViewById(R.id.phone);
		edtNewPwd = (EditTextWithDel ) findViewById(R.id.newPwd);
		edtNewPwdAgain = (EditTextWithDel ) findViewById(R.id.newPwdAgain);
		btnGetCode = (Button) findViewById(R.id.btnGetCode);
		edtCode = (EditText) findViewById(R.id.confirmCode);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnGetCode.setOnClickListener(this);
		mCaptchaHelper = new CaptchaHelper(this,btnGetCode,mVoiceCaptcha);
		btnSubmit.setOnClickListener(this);

		edtNewPwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String strength = Common.checkPasswordStrength(s.toString());
				tvPasswordStrength.setText(strength);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCaptchaHelper.onRecycle();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnGetCode) {
			captchaType = SMS_CAPTCHA;
			doGetCode(SMS_CAPTCHA);
		}
		if (v.getId() == R.id.voiceCaptcha){
			captchaType = VOICE_CAPTCHA;
			doGetCode(VOICE_CAPTCHA);
		}
		if (v.getId() == R.id.btnSubmit) {
			doSubmit();
		}
	}

	private void doGetCode(int captchaType) {
		mPhone = edtPhone.getText().toString();
		String newP = edtNewPwd.getText().toString();
		String newP2 = edtNewPwdAgain.getText().toString();
		String code = edtCode.getText().toString();

		if (TextUtils.isEmpty(mPhone) && TextUtils.isEmpty(mPhone)) {
			edtPhone.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(newP)) {
			edtNewPwd.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(newP2)) {
			edtNewPwdAgain.requestFocus();
			return;
		}
		if (!newP.equals(newP2)) {
			Utils.Toast(this, "两次密码输入不一致");
			return;
		}

		if (newP.length() < 6) {
			Utils.Toast(this, "密码不能低于6位字符!");
			return;
		}

		String passwordPass = Common.isPasswordPass(newP);
		if (!TextUtils.isEmpty(passwordPass)){
			Utils.Toast(this, passwordPass);
			return;
		}

		if (captchaType == SMS_CAPTCHA){
			mCaptchaHelper.smsStart(mHttpService, mPhone,"0");
//			mHttpService.getVerifyCode(null, null, null, phone, null);
		}else if (captchaType == VOICE_CAPTCHA){
			mCaptchaHelper.voiceStart(mHttpService, mPhone,"0");
//			mHttpService.getVoiceCaptcha(phone);
		}
	}

	public void showDialog() {
		VerificationCodeDialog codeDialog = VerificationCodeDialog.newInstance(mPhone,captchaType);
		codeDialog.setSmsListener(this);
		codeDialog.show(getSupportFragmentManager(),"VerificationCodeDialog");
	}


	private void doSubmit() {
		String phone = edtPhone.getText().toString();
		String newP = edtNewPwd.getText().toString();
		String newP2 = edtNewPwdAgain.getText().toString();
		String code = edtCode.getText().toString();

		if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(phone)) {
			edtPhone.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(newP)) {
			edtNewPwd.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(newP2)) {
			edtNewPwdAgain.requestFocus();
			return;
		}
		if (!newP.equals(newP2)) {
			Utils.Toast(this, "两次密码输入不一致");
			return;
		}

		if (TextUtils.isEmpty(code)) {
			edtCode.requestFocus();
			return;
		}

		if (!(newP.length() >= 6 && newP.length() <= 20)){
			Utils.Toast(this,"请输入6至20位密码");
			return;
		}

		Md5Algorithm md5 = Md5Algorithm.getInstance();
		String newP_md5 = md5.md5Digest((newP + HttpService.LOG_KEY).getBytes());
		mHttpService.forgetLoginPassword(phone, newP_md5, code);
	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		if (!isHttpHandle(json)) return;
		if (reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal()) {
			String errmsg = mHttpService.onGetVerifyCode(json);
			int msg = json.optInt("msg", -1);
			if (5 == msg) {
				//状态为5,弹出验证码
				mCaptchaHelper.smsFinish();
				showDialog();
			}else if (6 == msg){
				mCaptchaHelper.smsFinish();
			}
			Utils.Toast(this, errmsg);
//			mCaptchaHelper.smsFinish();
//			if (msg.contains("成功")) {
//				mGetCodeDelay = HttpService.getDelaySeconds();
//				mHandler.post(mCountDownTimer);
//				btnGetCode.setEnabled(false);
//			} else {
//				Utils.Toast(this, msg);
//			}
		}
		if(reqId == ServiceCmd.CmdId.CMD_Voice_Captcha.ordinal())
		{
			String errmsg = mHttpService.onGetVoiceCaptcha(json);
			int msg = json.optInt("msg", -1);
			if (5 == msg) {
				//状态为5,弹出验证码
				mCaptchaHelper.voiceFinish();
				showDialog();
			}else if (6 == msg){
				mCaptchaHelper.voiceFinish();
			}
			Utils.Toast(this, errmsg);
//			mCaptchaHelper.voiceFinish();
		}


		if (reqId == ServiceCmd.CmdId.CMD_forgetLoginPassword.ordinal()) {
			String msg = mHttpService.onForgetLoginPassword(json);
			if (msg.contains("成功")) {
				Utils.Toast(this,"操作成功，请重新登录！");
				finish();
			} else {
				Utils.Toast(this, msg);
			}
		}
	}

	@Override
	public void onHttpError(int reqId, String errmsg) {
		super.onHttpError(reqId, errmsg);
		if(reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal())
		{
			mCaptchaHelper.smsFinish();
		}
		if (reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal()){
			mCaptchaHelper.voiceFinish();
		}
	}

	@Override
	public void smsStart(int type) {
		if (type == 1) {
			mCaptchaHelper.smsStart(null, "", "");
		}else if (type == 2) {
			mCaptchaHelper.voiceStart(null, "", "");
		}
	}
}