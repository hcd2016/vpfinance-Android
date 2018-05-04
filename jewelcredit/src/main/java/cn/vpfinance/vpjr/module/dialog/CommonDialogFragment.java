package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import cn.vpfinance.android.R;

/**
 */
public class CommonDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String OK = "okStr";
    private static final String CANCEL = "cancelStr";
    private onAllListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL,0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public static CommonDialogFragment newInstance(String title,String message,String okStr,String cancelStr) {
        CommonDialogFragment fragment = new CommonDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE,title);
        args.putString(MESSAGE,message);
        args.putString(OK,okStr);
        args.putString(CANCEL,cancelStr);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_common_dialog, container, false);
        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
        TextView tvOk = (TextView) view.findViewById(R.id.ok);
        Bundle arguments = getArguments();
        if (arguments != null){
            String title = arguments.getString(TITLE);
            ((TextView) view.findViewById(R.id.title)).setText(TextUtils.isEmpty(title) ? "" : title);

            String message = arguments.getString(MESSAGE);
            TextView tvMessage = (TextView) view.findViewById(R.id.message);
            if (TextUtils.isEmpty(message)){
                tvMessage.setVisibility(View.GONE);
            }else{
                tvMessage.setText(Html.fromHtml(message));
            }

            String cancel = arguments.getString(CANCEL);

            tvCancel.setText(TextUtils.isEmpty(cancel) ? "取消" : cancel);

            String ok = arguments.getString(OK);
            tvOk.setText(TextUtils.isEmpty(ok) ? "确定" : ok);
        }

        tvCancel.setOnClickListener(this);
        tvOk.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if (listener != null){
                    listener.clickCancel();
                }
                dismiss();
                break;
            case R.id.ok:
                if (listener != null){
                    listener.clickOk();
                }
                dismiss();
                break;
        }
    }

    public void setOnAllLinstener(onAllListener listener){
        this.listener = listener;
    }

    public interface onAllListener{
        void clickOk();
        void clickCancel();
    }
}
