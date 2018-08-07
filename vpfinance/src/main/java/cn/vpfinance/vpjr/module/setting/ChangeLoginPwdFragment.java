package cn.vpfinance.vpjr.module.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.EditTextWithDel;

public class ChangeLoginPwdFragment extends BaseFragment implements View.OnClickListener {

    private Activity mHostActivity;

    private HttpService mHttpService = null;

    private static ChangeLoginPwdFragment self;

    private EditTextWithDel  etOldPwd;
    private EditTextWithDel  etNewPwd;
    private EditTextWithDel  etNewPwdAgain;
    private TextView tvPasswordStrength;

    private Button btnSubmit;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHostActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.layout_change_login_pwd, null);

        mHttpService = new HttpService(mHostActivity, this);
        initView(view);
        return view;
    }

    public static Fragment newInstance() {
        if (self == null) {
            self = new ChangeLoginPwdFragment();
        }
        return self;
    }


    private void initView(View view) {
        etOldPwd = (EditTextWithDel )view.findViewById(R.id.oldLoginPwd);
        etNewPwd = (EditTextWithDel )view.findViewById(R.id.newPwd);
        etNewPwdAgain = (EditTextWithDel )view.findViewById(R.id.newPwdAgain);
        btnSubmit = (Button) view.findViewById(R.id.submit);
        tvPasswordStrength = (TextView) view.findViewById(R.id.tvPasswordStrength);
        btnSubmit.setOnClickListener(this);

        etNewPwd.addTextChangedListener(new TextWatcher() {
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
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.submit:
                doModifyPassword();
                break;
        }
    }

    private void doModifyPassword() {
        String old = etOldPwd.getText().toString();
        String newpwd = etNewPwd.getText().toString();
        String newpwd2 = etNewPwdAgain.getText().toString();

        if (TextUtils.isEmpty(old)) {
            etOldPwd.requestFocus();
            Utils.Toast(getActivity(), "输入原登录密码");
            return;
        } else if (TextUtils.isEmpty(newpwd)) {
            etNewPwd.requestFocus();
            Utils.Toast(getActivity(), "新密码不能为空");
            return;
        } else if (TextUtils.isEmpty(newpwd2)) {
            etNewPwdAgain.requestFocus();
            Utils.Toast(getActivity(), "再次输入新密码");
            return;
        } else if (!newpwd.equals(newpwd2)) {
            etNewPwd.requestFocus();
            etNewPwd.setText("");
            etNewPwdAgain.setText("");
            Toast.makeText(mHostActivity, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newpwd.length() < 6) {
            Utils.Toast(getContext(), "密码不能低于6位字符!");
            return;
        }

        String passwordPass = Common.isPasswordPass(newpwd);
        if (!TextUtils.isEmpty(passwordPass)){
            Utils.Toast(getContext(), passwordPass);
            return;
        }
        Md5Algorithm md5 = Md5Algorithm.getInstance();
        String oldpwd_md5 = md5.md5Digest((old + HttpService.LOG_KEY).getBytes());
        String newpwd_md5 = md5.md5Digest((newpwd  + HttpService.LOG_KEY).getBytes());
        mHttpService.resetLoginPassword2(AppState.instance().getSessionCode(),oldpwd_md5, newpwd_md5);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if(reqId == ServiceCmd.CmdId.CMD_resetLoginPassword.ordinal()) {
            String msg = mHttpService.onResetLoginPassword2(json);
            if (msg.contains("成功")) {
                Utils.Toast(mHostActivity, "修改密码成功!");
                /*Intent intent = new Intent(mHostActivity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                mHostActivity.finish();
            } else {
                Utils.Toast(mHostActivity, msg);
                etOldPwd.requestFocus();
                etOldPwd.setText("");
            }
//            mHostActivity.finish();
        }
    }

    class Extra {
        private boolean isSelect;

        public Extra(boolean isSelect) {
            this.isSelect = isSelect;
        }
    }

    @Override
    public void onHttpError(int reqId, String msg) {
        super.onHttpError(reqId, msg);
    }

}