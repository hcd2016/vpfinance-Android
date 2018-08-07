package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.gson.AutoInvestSettingBean;
import cn.vpfinance.vpjr.module.setting.AutoInvestTypeActivity;

/**
 * Created by zzlz13 on 2017/9/18.
 */

public class AutoSettingAdapter<T extends AutoInvestSettingBean.BaseBean> extends RecyclerView.Adapter<AutoSettingAdapter.AutoSettingViewHolder> {

    private ArrayList<T> data;
    public boolean unlimited;
    private HashSet<CheckBox> checkBoxes = new HashSet<>();
    private Context mContext;
    public HashSet<String> codes = new HashSet<>();
    int gray;
    int black;

    public void updateState(RecyclerView mRecyclerView, String value) {
        if ("0".equals(value)) {
            unlimited = true;
        } else if ("-1".equals(value)) {
            unlimited = false;
            for (int i = 0; i < getItemCount(); i++) {
                View view = mRecyclerView.getChildAt(i);
                AutoSettingViewHolder viewHolder = (AutoSettingViewHolder) mRecyclerView.getChildViewHolder(view);
                if (!TextUtils.isEmpty(viewHolder.code)) {
                    codes.add(viewHolder.code);
                }
            }
        }
        notifyDataSetChanged();
    }

    public AutoSettingAdapter(Context context, ArrayList<T> data, String value) {
        mContext = context;
        black = mContext.getResources().getColor(R.color.text_1c1c1c);
        gray = mContext.getResources().getColor(R.color.text_cccccc);
        this.data = data;
        if ("0".equals(value)) {
            unlimited = true;
        } else {
            unlimited = false;
            String[] split = value.split(",");
            for (String s : split) {
                if (!TextUtils.isEmpty(s) && !s.equals("0") && !s.equals("-1")) {
                    codes.add(s);
                }
            }
        }
    }

    @Override
    public AutoSettingAdapter.AutoSettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AutoSettingAdapter.AutoSettingViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_auto_invest_setting, parent, false));
    }

    @Override
    public void onBindViewHolder(final AutoSettingAdapter.AutoSettingViewHolder holder, int position) {
        AutoInvestSettingBean.BaseBean bean = data.get(position);

        holder.code = bean.key;
        holder.tvTitle.setText(bean.value);
        holder.tvTitle.setTextColor(unlimited ? gray : black);

        if (unlimited) {//不限全部false
            holder.checkBox.setChecked(false);
        } else {//没有不限则判断cb是否勾选
            Iterator<String> iterator = codes.iterator();
            holder.checkBox.setChecked(false);
            while (iterator.hasNext()) {
                if (holder.code.equals(iterator.next())) {
                    holder.checkBox.setChecked(true);
                }
            }
        }
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unlimited) return;
                boolean checked = holder.checkBox.isChecked();
                if (checked) {//检查是否只剩最后一个选择, 如果只有一个则不允许取消勾选
                    if (codes.size() <= 1) {
                        Utils.Toast(Constant.chooseAtLeastOne);
                    } else {
                        codes.remove(holder.code);
                        holder.checkBox.setChecked(false);
                    }
                } else {
                    if (!TextUtils.isEmpty(holder.code)) {
                        codes.add(holder.code);
                    }
                    holder.checkBox.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class AutoSettingViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout rootView;
        public TextView tvTitle;
        public CheckBox checkBox;
        public String code;

        public AutoSettingViewHolder(View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.rootView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkBoxes.add(checkBox);
        }
    }
}
