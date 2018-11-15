package cn.vpfinance.vpjr.module.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.RecordDetailBean;
import cn.vpfinance.vpjr.module.product.record.FundRecordsDetailActivity;

public class PotocolDialog extends TBaseDialog {
    @Bind(R.id.ll_item_container)
    LinearLayout llItemContainer;
    @Bind(R.id.line_gray)
    View lineGray;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    Context context;

    public PotocolDialog(Context context) {
        super(context, R.layout.dialog_potocol);
        this.context = context;
        setWindowParam(0.8f, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, 0);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    public void setData(final List<RecordDetailBean.ProtocolListBean> list) {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        llItemContainer.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View view = View.inflate(mContext,R.layout.item_potocol_dialog,null);
            TextView tv_content = view.findViewById(R.id.tv_content);
            tv_content.setText(list.get(i).title);
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((FundRecordsDetailActivity)context).gotoWeb(list.get(finalI).url,list.get(finalI).title);
                }
            });
            llItemContainer.addView(view);
        }
    }
}
