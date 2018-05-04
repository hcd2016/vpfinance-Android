package cn.vpfinance.vpjr.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jewelcredit.util.Utils;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.AddRateBean;

/**
 * Created by Administrator on 2016/8/1.
 */
public class SingleAdapter extends BaseAdapter {
    private Activity          mActivity;
    private List<AddRateBean.CouponUserRelationsEntity> list;
    private LayoutInflater inflater = null;
    private int            temp     = -1;
    private boolean isEnable = true;

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public SingleAdapter(Activity activity) {
        mActivity = activity;
        inflater = LayoutInflater.from(activity);

    }

    public void setData(List<AddRateBean.CouponUserRelationsEntity> list) {
        this.list = list;
        notifyDataSetChanged();
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

    public void setEnable(boolean b) {
        isEnable = b;
    }

    @Override
    public boolean isEnabled(int position) {
        return isEnable;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.item_addrate_selector, null);
            holder = new ViewHolder();
            holder.voucher_bg = (RelativeLayout) convertView.findViewById(R.id.voucher_bg);
            holder.voucher_time = (TextView) convertView.findViewById(R.id.voucher_time);
            holder.addrate_info1 = (TextView) convertView.findViewById(R.id.addrate_info1);
            holder.addrate_info2 = (TextView) convertView.findViewById(R.id.addrate_info2);
            holder.addrate_info3 = (TextView) convertView.findViewById(R.id.addrate_info3);
            holder.voucher_get = (TextView) convertView.findViewById(R.id.voucher_get);
            holder.point1 = (ImageView) convertView.findViewById(R.id.point1);
            holder.point2 = (ImageView) convertView.findViewById(R.id.point2);
            holder.point3 = (ImageView) convertView.findViewById(R.id.point3);
            holder.voucher_money = (TextView) convertView.findViewById(R.id.voucher_money);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.voucherState = (ImageView) convertView.findViewById(R.id.voucherState);
            holder.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AddRateBean.CouponUserRelationsEntity bean = list.get(position);
        if (bean != null) {
            try{
                double v = Double.parseDouble(bean.getValue());
                holder.voucher_money.setText(""+(v*100));
            }catch (Exception e){
                e.printStackTrace();
            }
            holder.voucher_time.setText("有效期至"+ Utils.getDate(bean.getExpiredTm()));
            holder.voucher_get.setText(bean.getGetWay());
            List<String> useRemarks = bean.getUseRemarks();
            if (useRemarks != null){
                if (!TextUtils.isEmpty(useRemarks.get(0))){
                    holder.addrate_info1.setText(useRemarks.get(0));
                    holder.point1.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(useRemarks.get(1))){
                    holder.addrate_info2.setText(useRemarks.get(1));
                    holder.point2.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(useRemarks.get(2))){
                    holder.addrate_info3.setText(useRemarks.get(2));
                    holder.point3.setVisibility(View.VISIBLE);
                }
            }
            if ("true".equals(bean.getEnable())) {
                isEnable = true;
//                holder.voucher_bg.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_coupon));
                holder.voucherState.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_addrate_header_usable));
            } else {
                isEnable = false;
//                holder.voucher_bg.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_addrate_nousable));
                holder.voucherState.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_addrate_header_nousable));
            }
            holder.status = bean.getVoucherStatus();
            if (bean.getVoucherStatus() == 2){
                holder.ivStatus.setVisibility(View.VISIBLE);
                holder.checkBox.setVisibility(View.GONE);
                holder.voucherState.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_addrate_header_nousable));
            }else{
                holder.ivStatus.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.VISIBLE);
            }
        }

        holder.checkBox.setId(position);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (temp != -1 && temp != buttonView.getId()) {

                        CheckBox tempCheckBox = (CheckBox) mActivity.findViewById(temp);
                        if (tempCheckBox != null)
                            tempCheckBox.setChecked(false);
                    }
                    temp = buttonView.getId();
                }
            }


        });
//        Log.d("aa", "temp:" + temp);
//        Log.d("aa", "position:" + position);
        if (position == temp)
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);
        return convertView;
    }

    public static class ViewHolder {
        TextView       voucher_money;
        TextView       voucher_time;
        TextView       addrate_info1;
        TextView       addrate_info2;
        TextView       addrate_info3;
        TextView       voucher_get;
        ImageView      point1;
        ImageView      point2;
        ImageView      point3;
        RelativeLayout voucher_bg;
        ImageView      voucherState;
        ImageView      ivStatus;
        public CheckBox       checkBox;
        public int       status;
    }

}


