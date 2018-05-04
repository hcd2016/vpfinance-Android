package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.NewAppUpdateInfo;

/**
 */
public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener{
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String VERSION = "version";
    private onAllListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
//        setStyle(R.style.VPDialog,0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        return dialog;
    }


    public static UpdateDialogFragment newInstance(NewAppUpdateInfo info) {
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE,info.updateLog);
        args.putInt(STATUS,info.appStatus);
        args.putString(VERSION,info.appVersion);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_update_dialog, container, false);

        TextView nowOk = (TextView) view.findViewById(R.id.nowOk);//强制更新
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView ok = (TextView) view.findViewById(R.id.ok);

        Bundle arguments = getArguments();
        if (arguments != null){

            String message = arguments.getString(MESSAGE);
            ((TextView) view.findViewById(R.id.message)).setText(TextUtils.isEmpty(message) ? "" : Html.fromHtml(message));

            String version = arguments.getString(VERSION);
            ((TextView) view.findViewById(R.id.version)).setText(TextUtils.isEmpty(version) ? "" : "V"+version);

            LinearLayout normalUpdate = (LinearLayout) view.findViewById(R.id.normalUpdate);

            int status = arguments.getInt(STATUS);
            if (status == 2){
                normalUpdate.setVisibility(View.VISIBLE);
                nowOk.setVisibility(View.GONE);
            }else if(status == 3){
                normalUpdate.setVisibility(View.GONE);
                nowOk.setVisibility(View.VISIBLE);
            }

        }

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        nowOk.setOnClickListener(this);

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
            case R.id.nowOk:
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
