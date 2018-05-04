package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.tdk.utils.HttpDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.setting.ResetPayPasswordActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.util.CaptchaHelper;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.view.VerificationCodeDialog;

/**
 */
public class WithdrawDialogFragment extends DialogFragment implements View.OnClickListener ,HttpDownloader.HttpDownloaderListener, VerificationCodeDialog.SmsListener {

    private EditText mCode;
    private EditText verCode;
    private TextView dialogOk;
    private TextView dialogCancel;
    private onTextConfrimListener mListener;
    private Button getVerCode;

    private TextView mVoiceCaptcha;
    private CaptchaHelper mCaptchaHelper;
    private static final int SMS_CAPTCHA = 1;
    private static final int VOICE_CAPTCHA = 2;
    private int captchaType = 0;//1 sms ;2 voice

    private User user;
    private String wMoney;
    private String okStr;
    private HttpService mHttpService;

    public static WithdrawDialogFragment newInstance(String money,String okStr) {
        WithdrawDialogFragment fragment = new WithdrawDialogFragment();
        Bundle args = new Bundle();
        args.putString("money",money);
        args.putString("okStr",okStr);
        fragment.setArguments(args);
        return fragment;
    }

    public static WithdrawDialogFragment newInstance(String money) {
        WithdrawDialogFragment fragment = new WithdrawDialogFragment();
        Bundle args = new Bundle();
        args.putString("money",money);
        fragment.setArguments(args);
        return fragment;
    }


//    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_withdraw, container, false);
        mHttpService = new HttpService(getContext(), this);

        mVoiceCaptcha = (TextView)view.findViewById(R.id.voiceCaptcha);
        mVoiceCaptcha.setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null){
            wMoney = bundle.getString("money");
            okStr = bundle.getString("okStr");
        }

        mCode = (EditText) view.findViewById(R.id.code);
        verCode = (EditText) view.findViewById(R.id.verCode);
        dialogCancel = (TextView) view.findViewById(R.id.dialogCancel);
        dialogOk = (TextView) view.findViewById(R.id.dialogOk);
        getVerCode = (Button) view.findViewById(R.id.getVerCode);
        view.findViewById(R.id.forgetPwd).setOnClickListener(this);
        mCaptchaHelper = new CaptchaHelper(getActivity(),getVerCode,mVoiceCaptcha);

        if (!TextUtils.isEmpty(okStr)){
            dialogOk.setText(okStr);
        }

        user = DBUtils.getUser(getActivity());
        if (user == null || TextUtils.isEmpty(user.getCellPhone())){
            Toast.makeText(getActivity(), "您还没有登录，请登录", Toast.LENGTH_SHORT).show();
            getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            dismiss();
        }

        dialogCancel.setOnClickListener(this);
        dialogOk.setOnClickListener(this);
        getVerCode.setOnClickListener(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL,0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCaptchaHelper.onRecycle();
    }

    public void setOnTextConfrimListener(onTextConfrimListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.voiceCaptcha:
                captchaType = VOICE_CAPTCHA;
                if (!TextUtils.isEmpty(mCode.getText().toString().trim())){
                    mCaptchaHelper.voiceStart(mHttpService,user.getCellPhone(),"0");
//			        mHttpService.getVoiceCaptcha(user.getCellPhone());
                }else{
                    Toast.makeText(getActivity(),"您还没有输入交易密码",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.getVerCode:
                captchaType = SMS_CAPTCHA;
                if (!TextUtils.isEmpty(mCode.getText().toString().trim())){
                    mCaptchaHelper.smsStart(mHttpService, user.getCellPhone(),"0");
//                    mHttpService.getVerifyCode(null, null, null, user.getCellPhone(), null);
                }else{
                    Toast.makeText(getActivity(),"您还没有输入交易密码",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.forgetPwd:
                startActivity(new Intent(getActivity(),ResetPayPasswordActivity.class));
                dismiss();
                break;
            case R.id.dialogOk:
                String value = mCode.getText().toString();
                String verCode1 = this.verCode.getText().toString().trim();
                if (TextUtils.isEmpty(value))
                {
                    Toast.makeText(getActivity(), "请输入交易密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(verCode1))
                {
                    Toast.makeText(getActivity(), "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mListener != null)
                {
                    if (!mListener.onTextConfrim(value,verCode1))
                    {
//                        return;
                    }
                }
                dismiss();
                break;
        }
    }

    public void showDialog() {
        VerificationCodeDialog codeDialog = VerificationCodeDialog.newInstance(user.getCellPhone(),captchaType);
        codeDialog.setSmsListener(this);
        codeDialog.show(getFragmentManager(),"VerificationCodeDialog");
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (json == null || (!isAdded()) || Common.isForceLogout(getContext(),json)){
            return;
        }
        if(reqId == ServiceCmd.CmdId.CMD_getVerifyCode.ordinal())
        {
            String errmsg = mHttpService.onGetVerifyCode(json);
            int msg = json.optInt("msg", -1);
            if (5 == msg) {
                //状态为5,弹出验证码
                mCaptchaHelper.smsFinish();
                showDialog();
            }else if (6 == msg){
                mCaptchaHelper.smsFinish();
            }
            Utils.Toast(getActivity(), errmsg);
//            mCaptchaHelper.smsFinish();
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
            Utils.Toast(getActivity(), errmsg);
//            mCaptchaHelper.voiceFinish();
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {

    }

    @Override
    public void onHttpCache(int reqId) {

    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
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

    public static interface onTextConfrimListener {
        /**
         *
         * @description 返回true对话框消失不调用回调方法
         * @update 2014年12月25日 上午10:42:00
         */
        public boolean onTextConfrim(String value,String value2);
    }
}
