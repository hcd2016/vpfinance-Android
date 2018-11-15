package cn.vpfinance.vpjr.module.product.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;

/**
 * 标的详情中的 借款记录类型
 */
public class LoanRecordFragment extends BaseFragment {
    @Bind(R.id.tv_principal_counts)
    TextView tvPrincipalCounts;
    @Bind(R.id.tv_principal_total_money)
    TextView tvPrincipalTotalMoney;
    @Bind(R.id.line_magin_15)
    View lineMagin15;
    @Bind(R.id.ll_repayment_principal_container)
    LinearLayout llRepaymentPrincipalContainer;
    @Bind(R.id.tv_interest_counts)
    TextView tvInterestCounts;
    @Bind(R.id.tv_interest_total_money)
    TextView tvInterestTotalMoney;
    @Bind(R.id.ll_repayment_interest_container)
    LinearLayout llRepaymentInterestContainer;
    private List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_loan_record, null);
        ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        initView();
        return view;
    }

    private void initView() {
        list = new ArrayList<>();

        setRepaymentPrincipalData();
        setRepaymentInterestData();
    }


    /**
     * 应还本金数据
     */
    private void setRepaymentPrincipalData() {
        llRepaymentPrincipalContainer.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View view = View.inflate(getActivity(),R.layout.item_loan_record,null);
            TextView tv_desc = view.findViewById(R.id.tv_desc);
            TextView tv_counts = view.findViewById(R.id.tv_counts);
            TextView tv_total_money = view.findViewById(R.id.tv_total_money);
        }
    }

    //应还利息数据
    private void setRepaymentInterestData() {
        llRepaymentPrincipalContainer.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View view = View.inflate(getActivity(),R.layout.item_loan_record,null);
            TextView tv_desc = view.findViewById(R.id.tv_desc);
            TextView tv_counts = view.findViewById(R.id.tv_counts);
            TextView tv_total_money = view.findViewById(R.id.tv_total_money);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
