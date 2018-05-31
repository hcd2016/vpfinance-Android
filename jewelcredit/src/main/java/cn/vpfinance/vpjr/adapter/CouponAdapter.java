package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.CouponBean;
import cn.vpfinance.vpjr.module.user.fragment.CouponFragment;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder>{

    private Context mContext;
//    private int type;
    private int status;
    private List<CouponBean.MyCouponDtoBean.MyCouponListDtosBean> data;

    public CouponAdapter(Context mContext,  int status) {
        this.mContext = mContext;
//        this.type = type;
        this.status = status;
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CouponViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_coupon,parent,false));
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        CouponBean.MyCouponDtoBean.MyCouponListDtosBean bean = data.get(position);
        if (bean != null){
            if (bean.couponType == 2){//1加息劵 2预约劵  3代金券
                switch (status){
                    case CouponFragment.STATUS_UNUSED:
                        holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_presell_header_usable));
                        break;
                    case CouponFragment.STATUS_USED:
                        holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_presell_header_nousable));
                        break;
                    case CouponFragment.STATUS_INVALID:
                        holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_presell_header_nousable));
                        break;
                }
                if (bean.voucherStatus == 2){
                    holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_presell_header_nousable));
                }
                holder.presellName.setText("预");
                holder.voucher_get.setText(bean.getWay);
                holder.voucher_time.setText("有效期至"+ bean.expiredTm);
                ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.item_coupon_remark, R.id.tvInfo, bean.remarkList);
                holder.mListView.setAdapter(arrayAdapter);
            }else if (bean.couponType == 3){
                switch (status){
                    case CouponFragment.STATUS_UNUSED:
                        holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_voucher_header_usable));
                        break;
                    case CouponFragment.STATUS_USED:
                        holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                        break;
                    case CouponFragment.STATUS_INVALID:
                        holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                        break;
                }
                if (bean.voucherStatus == 2){
                    holder.voucherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_voucher_header_nousable));
                }
                holder.presellName.setText(bean.denomination);
                holder.voucher_get.setText(bean.getWay);
                holder.voucher_time.setText("有效期至"+ bean.expiredTm);
                ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.item_coupon_remark, R.id.tvInfo, bean.remarkList);
                holder.mListView.setAdapter(arrayAdapter);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<CouponBean.MyCouponDtoBean.MyCouponListDtosBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder{
        ImageView voucherState;
        TextView presellName;
        TextView voucher_get;
//        ImageView point1;
//        TextView addrate_info1;
//        ImageView point2;
//        TextView addrate_info2;
//        ImageView point3;
//        TextView addrate_info3;
        TextView voucher_time;
        ListView mListView;
        public CouponViewHolder(View itemView) {
            super(itemView);
            voucherState = (ImageView) itemView.findViewById(R.id.voucherState);
            presellName = (TextView) itemView.findViewById(R.id.presellName);
            voucher_get = (TextView) itemView.findViewById(R.id.voucher_get);
//            point1 = (ImageView) itemView.findViewById(R.id.point1);
//            addrate_info1 = (TextView) itemView.findViewById(R.id.addrate_info1);
//            point2 = (ImageView) itemView.findViewById(R.id.point2);
//            addrate_info2 = (TextView) itemView.findViewById(R.id.addrate_info2);
//            point3 = (ImageView) itemView.findViewById(R.id.point3);
//            addrate_info3 = (TextView) itemView.findViewById(R.id.addrate_info3);
            voucher_time = (TextView) itemView.findViewById(R.id.voucher_time);
            mListView = (ListView)itemView.findViewById(R.id.mListView);
        }
    }
}
