package cn.vpfinance.vpjr.module.product.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.DepositTab1Bean;
import cn.vpfinance.vpjr.gson.NewBaseInfoBean;
import cn.vpfinance.vpjr.module.product.record.NewRepayListActivity;
import cn.vpfinance.vpjr.module.product.record.ProductInvestListActivity;
import cn.vpfinance.vpjr.view.MyCountDownTimer;

/**
 * 标的基本信息fragment v4.0
 */
public class BaseInfoFragment extends BaseFragment {
    @Bind(R.id.ll_obj_info_container)
    LinearLayout llObjInfoContainer;
    @Bind(R.id.tv_loan_num)
    TextView tvLoanNum;
    @Bind(R.id.ll_loan_record_container)
    LinearLayout llLoanRecordContainer;
    @Bind(R.id.line_magin_15)
    View lineMagin15;
    @Bind(R.id.ll_repayment_plan_container)
    LinearLayout llRepaymentPlanContainer;
    private HttpService httpService;
    private NewBaseInfoBean mNewBaseInfoBean;

    public static BaseInfoFragment newInstance(String url) {
        BaseInfoFragment fragment = new BaseInfoFragment();
        Bundle args = new Bundle();
        args.putString("request_url",url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_base_info, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        httpService = new HttpService(getContext(), this);
        String mRequestUrl = "";
        Bundle args = getArguments();
        if (args != null) {
            mRequestUrl = args.getString("request_url");
        }
        if (!TextUtils.isEmpty(mRequestUrl)) {
            httpService.getRegularTab(mRequestUrl);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (reqId == ServiceCmd.CmdId.CMD_Regular_Tab.ordinal()) {
            try {
                Gson gson = new Gson();
                mNewBaseInfoBean = gson.fromJson(json.toString(), NewBaseInfoBean.class);
                setViewData(mNewBaseInfoBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //设置界面数据
    private void setViewData(NewBaseInfoBean mNewBaseInfoBean) {
        if (mNewBaseInfoBean != null) {
            //标的条目信息
            List<NewBaseInfoBean.DataEntity> data = mNewBaseInfoBean.data;
            if (data != null && data.size() != 0) {
                llObjInfoContainer.removeAllViews();
                for (int i = 0; i < data.size(); i++) {
                    NewBaseInfoBean.DataEntity dataEntity = data.get(i);
                    if (dataEntity == null)
                        break;
                    View view = View.inflate(mContext, R.layout.new_base_info_basic_item, null);
                    ((TextView) view.findViewById(R.id.tvKey)).setText(dataEntity.key);
                    ((TextView) view.findViewById(R.id.tvValue)).setText(dataEntity.value);
                    llObjInfoContainer.addView(view);
                }
            }
            //出借记录:
            tvLoanNum.setText("已有" + mNewBaseInfoBean.buyCount + "人出借");

            //回款计划:
            llRepaymentPlanContainer.setVisibility(View.GONE);//默认不展示
            if (mNewBaseInfoBean.status.equals("3") || mNewBaseInfoBean.status.equals("4")) {//回款中或已完成状态
                llRepaymentPlanContainer.setVisibility(View.VISIBLE);
                lineMagin15.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.ll_loan_record_container, R.id.ll_repayment_plan_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_loan_record_container://借款记录
                if (mNewBaseInfoBean != null) {
                    gotoProductInvestList(mNewBaseInfoBean.loanId + "", mNewBaseInfoBean.frequency, false, serverTime);
                }
                break;
            case R.id.ll_repayment_plan_container://回款计划
                if (null != mNewBaseInfoBean) {
                    if (mNewBaseInfoBean != null) {
                        NewRepayListActivity.goNewRepayListActivity(mContext, false, mNewBaseInfoBean.repays);
                    }
                }
                break;
        }
    }

    long serverTime = System.currentTimeMillis();

    private void gotoProductInvestList(String id, int frequency, boolean is_deposit, long serviceTime) {
        try {
            long pid = Long.parseLong(id);
            if (serverTime == 0) {
                ProductInvestListActivity.goProductInvestListActivity(mContext, pid, 0, frequency, is_deposit);
            } else {
                ProductInvestListActivity.goProductInvestListActivity(mContext, pid, 0, frequency, is_deposit, serviceTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
