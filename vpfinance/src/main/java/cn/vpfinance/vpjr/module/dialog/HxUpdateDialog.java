package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jewelcredit.util.Utils;

import cn.vpfinance.android.R;

public class HxUpdateDialog extends DialogFragment{

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
       Dialog dialog = super.onCreateDialog(savedInstanceState);
       dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       Window window = dialog.getWindow();
       window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
       WindowManager.LayoutParams lp = window.getAttributes();
       DisplayMetrics dm = getResources().getDisplayMetrics();
       //修改dialog宽度
       lp.width = dm.widthPixels - Utils.dip2px(getContext(),8);
       window.setAttributes(lp);

       dialog.setCanceledOnTouchOutside(false);
       return dialog;
   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_hx_update, container, false);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
