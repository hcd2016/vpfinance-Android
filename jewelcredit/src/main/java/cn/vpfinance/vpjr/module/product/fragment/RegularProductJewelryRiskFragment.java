package cn.vpfinance.vpjr.module.product.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;

/**
 * Created by Administrator on 2016/8/17.
 */
public class RegularProductJewelryRiskFragment extends BaseFragment{

    private static final String ARGS_KEY_LOAN_ID = "loanId";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_jewelry_product_risk_control, container, false);
        return view;
    }

    public static RegularProductJewelryRiskFragment newInstance(long loanId){
        RegularProductJewelryRiskFragment fragment = new RegularProductJewelryRiskFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_KEY_LOAN_ID, loanId);
        fragment.setArguments(args);
        return fragment;
    }
}
