package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.os.Bundle;
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

/**
 * Created by Administrator on 2015/11/16.
 */
public class VoucherDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText mCode;
    private TextView dialogOk;
    private TextView dialogCancel;
    private onTextConfrimListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_voucher, container, false);

        mCode = (EditText) view.findViewById(R.id.code);
        dialogCancel = (TextView) view.findViewById(R.id.dialogCancel);
        dialogOk = (TextView) view.findViewById(R.id.dialogOk);

        dialogCancel.setOnClickListener(this);
        dialogOk.setOnClickListener(this);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
    public void setOnTextConfrimListener(onTextConfrimListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.dialogOk:
                String value = mCode.getText().toString();
                if (TextUtils.isEmpty(value))
                {
                    Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mListener != null)
                {
                    if (!mListener.onTextConfrim(value))
                    {
                        return;
                    }
                }
                dismiss();
                break;
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
