package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jewelcredit.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.SelectVoucherBean;

/**
 * Created by Administrator on 2016/7/28.
 */
public class GroupAdapter extends BaseAdapter {
    private Context                                   context;
    private List<SelectVoucherBean.VoucherlistEntity> list;
    //控制CheckBox选中情况
    private static HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public GroupAdapter(Context context) {
        this.context = context;
        isSelected = new HashMap<Integer, Boolean>();
        list = new ArrayList<>();

    }

    public void setData(List<SelectVoucherBean.VoucherlistEntity> list) {
        this.list = list;
        initData();
        notifyDataSetChanged();
    }

    private void initData() {
        for (int i = 0; i < list.size(); i++) {
            if (!isSelected.containsKey(i)) {
                getIsSelected().put(i, false);
            }

        }
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context,R.layout.item_voucher_selector_new, null);
            holder = new ViewHolder();
//            holder.voucher_bg = (RelativeLayout) convertView.findViewById(R.id.voucher_bg);
            holder.voucher_time = (TextView) convertView.findViewById(R.id.voucher_time);
            holder.voucher_info = (TextView) convertView.findViewById(R.id.voucher_info);
            holder.voucher_money = (TextView) convertView.findViewById(R.id.voucher_money);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.voucherState = (ImageView) convertView.findViewById(R.id.voucherState);
//            holder.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);
            convertView.setTag(holder);

        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        SelectVoucherBean.VoucherlistEntity bean = list.get(position);
        if (bean != null) {
            SelectVoucherBean.VoucherlistEntity.VoucherEntity voucher = bean.getVoucher();
            if ("1".equals(bean.getActivity_type())) {//抢红包获得代金券
                holder.voucher_money.setText(bean.getVoucher_money() + "");
            } else {
                if (voucher != null) {
                    holder.voucher_money.setText(voucher.getAmount() +"");
                }
            }
            holder.voucher_info.setText(TextUtils.isEmpty(bean.getUseRuleExplain()) ? "" : bean.getUseRuleExplain());
            holder.voucher_time.setText("有效期至"+ Utils.getDate(bean.getExpireDate()));
            holder.status = bean.getVoucherStatus();
            if (bean.getVoucherStatus() == 2){
//                holder.ivStatus.setVisibility(View.VISIBLE);
                holder.checkBox.setVisibility(View.GONE);
                holder.voucherState.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.card_dongjie));
            }else{
//                holder.ivStatus.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.voucherState.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.card_daijinquan));
            }
        }
        holder.checkBox.setChecked(getIsSelected().get(position));
        return convertView;
    }


    public static class ViewHolder {
        TextView       voucher_money;
        TextView       voucher_time;
        TextView       voucher_info;
//        RelativeLayout voucher_bg;
        ImageView      voucherState;
//        ImageView      ivStatus;
        public CheckBox checkBox;
        public int status;
    }

    public  HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }


    public  void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        GroupAdapter.isSelected = isSelected;
    }

}

