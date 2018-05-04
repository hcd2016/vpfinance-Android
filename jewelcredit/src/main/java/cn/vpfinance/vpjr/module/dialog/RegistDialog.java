package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.tdk.utils.HttpDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.module.user.personal.TrialCoinActivity;
import cn.vpfinance.vpjr.util.Common;

/**
 */
public class RegistDialog extends DialogFragment implements View.OnClickListener,HttpDownloader.HttpDownloaderListener {


    private ImageButton ivClose;
    private TextView tvNowStart;
//    private onGotoActivityLinstener listener;
    public final static String PHONE = "userPhone";
    public final static String PWD = "userPwd";
    private String strPhone;
    private String strPwd;
    private HttpService mHttpService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL, 0);

        mHttpService = new HttpService(getActivity(), this);

//        Bundle arguments = getArguments();
//        strPhone = arguments.getString(PHONE);
//        strPwd = arguments.getString(PWD);

//        mHttpService.userLogin(strPhone.toString(), strPwd);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public static RegistDialog newInstance(Bundle bundle){
        RegistDialog registDialog = new RegistDialog();
        registDialog.setArguments(bundle);
        return registDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.dialog_regist, null);
        ivClose = (ImageButton) view.findViewById(R.id.ibClose);
        ivClose.setOnClickListener(this);
        tvNowStart = (TextView) view.findViewById(R.id.tvNowStart);
        tvNowStart.setOnClickListener(this);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return view;
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (json == null || (!isAdded()) || Common.isForceLogout(getContext(),json)){
            return;
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibClose:
//                if (AppState.instance().logined()){
//                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                    startActivity(intent);
//                }
                dismiss();
                break;
            case R.id.tvNowStart:
                if (AppState.instance().logined()){
                    TrialCoinActivity.goThis(getActivity(), Constant.AccountBank);
//                    Intent intent = new Intent(getActivity(), TrialCoinActivity.class);
//                    startActivity(intent);
                }
                dismiss();
                break;
        }
    }
}
