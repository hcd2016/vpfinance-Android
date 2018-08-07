package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.content.Context;
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
import cn.vpfinance.vpjr.util.CaptchaHelper;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.VerifyUtils;
import cn.vpfinance.vpjr.view.VerificationCodeDialog;

/**
 * Created by Administrator on 2015/11/16.
 */
public class MailDialogFragment extends DialogFragment implements HttpDownloader.HttpDownloaderListener , View.OnClickListener, VerificationCodeDialog.SmsListener {

    private EditText mMail;
    private EditText mCode;
    private TextView dialogOk;
    private TextView dialogCancel;
    private onTextConfrimListener mListener;
    private Button btnGetCode;
    private TextView title;


    private HttpService mHttpService;
    private String cellPhone;
    private String emailPass;
    private Context context;
    private String mail;

    private TextView mVoiceCaptcha;
    private CaptchaHelper mCaptchaHelper;
    private static final int SMS_CAPTCHA = 1;
    private static final int VOICE_CAPTCHA = 2;
    private int captchaType = 0;//1 sms ;2 voice

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHttpService = new HttpService(getActivity(), this);

        View view = inflater.inflate(R.layout.dialog_mail, container, false);

        mMail = (EditText) view.findViewById(R.id.code);
        mCode = (EditText) view.findViewById(R.id.mCode);
        btnGetCode = (Button) view.findViewById(R.id.btnGetCode);
        title = (TextView) view.findViewById(R.id.title);

        dialogCancel = (TextView) view.findViewById(R.id.dialogCancel);
        dialogOk = (TextView) view.findViewById(R.id.dialogOk);
        mVoiceCaptcha = (TextView)view.findViewById(R.id.voiceCaptcha);
        mVoiceCaptcha.setOnClickListener(this);
        mCaptchaHelper = new CaptchaHelper(getActivity(),btnGetCode,mVoiceCaptcha);

        dialogCancel.setOnClickListener(this);
        dialogOk.setOnClickListener(this);
        btnGetCode.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null){
            emailPass = bundle.getString("emailPass");
            cellPhone = bundle.getString("cellPhone");
            if ("1".equals(emailPass)){
                title.setText("更换邮箱");
            }else{
                title.setText("绑定邮箱");
            }
        }
        return view;
    }
    public static MailDialogFragment newInstance(String state,String cellPhone){
        MailDialogFragment mailDialogFragment = new MailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("emailPass",state);
        bundle.putString("cellPhone", cellPhone);
        mailDialogFragment.setArguments(bundle);
        return mailDialogFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context ;
    }

    private void doGetCode(int captchaType) {
        String mail = mMail.getText().toString();
        if (TextUtils.isEmpty(mail)) {
            mMail.requestFocus();
            Toast.makeText(getActivity(),"请填写邮箱",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!VerifyUtils.checkMail(getActivity(),mail)){
            Utils.Toast(getActivity(),"邮箱格式不正确");
            return;
        }
        if (captchaType == SMS_CAPTCHA){
            mCaptchaHelper.smsStart(mHttpService,cellPhone,"0");
//			mHttpService.getVerifyCode(null, null, null, cellPhone, null);
        }else if (captchaType == VOICE_CAPTCHA){
            mCaptchaHelper.voiceStart(mHttpService,cellPhone,"0");
//			mHttpService.getVoiceCaptcha(cellPhone);
        }
//        mHttpService.getVerifyCode(null, null, null, cellPhone, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL,0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCaptchaHelper.onRecycle();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    public void setOnTextConfrimListener(onTextConfrimListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGetCode:
                captchaType = SMS_CAPTCHA;
                doGetCode(SMS_CAPTCHA);
                break;
            case R.id.voiceCaptcha:
                captchaType = VOICE_CAPTCHA;
                doGetCode(VOICE_CAPTCHA);
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.dialogOk:
                mail = mMail.getText().toString();
                if (TextUtils.isEmpty(mail))
                {
                    Toast.makeText(getActivity(), "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                String valueCode = mCode.getText().toString();
                if (TextUtils.isEmpty(valueCode))
                {
                    Toast.makeText(getActivity(), "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!VerifyUtils.checkMail(getActivity(), mail)){
                    Utils.Toast(getActivity(), "邮箱格式不正确");
                    return;
                }
                mHttpService.bindingEmail(valueCode, mail);

                dismiss();
                break;
        }
    }
    public void showDialog() {
        VerificationCodeDialog codeDialog = VerificationCodeDialog.newInstance(cellPhone,captchaType);
        codeDialog.setSmsListener(this);
        codeDialog.show(getFragmentManager(),"VerificationCodeDialog");
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (json == null || (!isAdded()) || Common.isForceLogout(getContext(),json)){
            return;
        }
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

        else if(reqId == ServiceCmd.CmdId.CMD_BIND_EMAIL.ordinal())
        {
            int ret = mHttpService.onBindingEmail(json);
            switch (ret)
            {
                case 1:
                    Toast.makeText(context,"验证码超时",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context,"验证码错误",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(context,"绑定成功.",Toast.LENGTH_LONG).show();
                    if (mListener != null){
                        mListener.onTextConfrim(mail);
                    }
                    dismiss();
                    break;
                case 4:
                    Toast.makeText(context,"发送失败，系统错误",Toast.LENGTH_SHORT).show();
                    break;
            }
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
//        Utils.Toast(context, errmsg);
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
        public boolean onTextConfrim(String value);
    }
}
