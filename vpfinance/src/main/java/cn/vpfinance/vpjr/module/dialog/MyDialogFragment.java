package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.setting.PersonalInfoActivity;

/**
 */
public class MyDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TITLE = "title";
    public static final String ETHINT = "etHint";
    public static final String STROK = "strOk";
    public static final String CONTENT = "content";
    private TextView tvTitle;
    private EditText etContent;
    private TextView dialogCancel;
    private TextView dialogOk;
    private onTextConfrimListener listener;
    private Bundle arguments;

    public static MyDialogFragment newInstance(String title,String etHint,String strOk){
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE,title);
        bundle.putString(ETHINT,etHint);
        bundle.putString(STROK,strOk);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }
    public static MyDialogFragment newInstance(String title,String etHint,String strOk,String etContent){
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE,title);
        bundle.putString(ETHINT,etHint);
        bundle.putString(STROK,strOk);
        bundle.putString(CONTENT,etContent);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.dialog_my, null);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        etContent = (EditText) view.findViewById(R.id.etContent);
        dialogCancel = (TextView) view.findViewById(R.id.dialogCancel);
        dialogOk = (TextView) view.findViewById(R.id.dialogOk);

        arguments = getArguments();
        if (arguments != null){
            tvTitle.setText(arguments.getString(TITLE));
            String strEtHine = arguments.getString(ETHINT);
            if (TextUtils.isEmpty(strEtHine)){
                etContent.setVisibility(View.GONE);
            }else{
                etContent.setHint(strEtHine);
            }
            dialogOk.setText(arguments.getString(STROK));

            String content = arguments.getString(CONTENT);
            if (!PersonalInfoActivity.NONENAME.equals(content)){
                if (!TextUtils.isEmpty(content)){
                    etContent.setText(content);
                    etContent.setSelection(content.length());
                }
            }
        }
        dialogCancel.setOnClickListener(this);
        dialogOk.setOnClickListener(this);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.dialogOk:
                String strEtHine = arguments.getString(ETHINT);
                if (TextUtils.isEmpty(strEtHine)){
                    //提示框
                    if (listener != null){
                        listener.onTextConfrim("");
                    }
                }else{
                    //输入框
                    String text = etContent.getText().toString();
                    if (TextUtils.isEmpty(text)){
                        Toast.makeText(getActivity(),"请输入内容",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (listener != null){
                        listener.onTextConfrim(text);
                    }
                }
                dismiss();
                break;
        }
    }
    public void setOnTextConfrimListener(onTextConfrimListener listener){
        this.listener = listener;
    }

    public static interface onTextConfrimListener{
        public boolean onTextConfrim(String value);
    }

}
