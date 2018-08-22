package cn.vpfinance.vpjr.module.product.record.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.model.RepayFloatModel;

public class RepayFloatAdapter extends BaseQuickAdapter<RepayFloatModel.RepayPlansBean, BaseViewHolder> {

    public RepayFloatAdapter(@Nullable List data) {
        super(R.layout.item_repay_float, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RepayFloatModel.RepayPlansBean item) {
        helper.setText(R.id.tv_repay_time, item.getRepayDate())
                .setText(R.id.tv_repay_amount, item.getCapitalAmount()+"+" + item.getPromitAmount()+"元");
    }
}
