package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.gson.PersonalInfo;
import cn.vpfinance.vpjr.model.RegularProductInfo;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import de.greenrobot.event.EventBus;

/**
 */
public class RegularProductBorrowerInfoFragment extends BaseFragment implements View.OnClickListener {

    private static final String ARGS_KEY_LOAN_ID = "loanId";
    private Context mContext = null;
    private HttpService mHttpService = null;
    private long mLoanId = 1;

    private View basicInfoView,workInfoView,financeInfoView, usageView;
    private TextView       tvBorrower;
    private TextView       tvEducation;
    private TextView       tvAge;
    private TextView       tvIsMarry;
    private TextView       tvBirthPlace;
    private TextView       tvIndustry;
    private TextView       tvJob;
    private TextView       tvCompanySize;
    private TextView       tvJobExperience;
    private TextView       tvAddress;
    private TextView       tvMonthIncome;
    private TextView       tvMonthOut;
    private TextView       tvHaveHouse;
    private TextView       tvHaveCar;
    private TextView       tvHaveHouseLoan;
    private TextView       tvHaveCarLoan;
    private TextView       tvMonthUsage;
    private FinanceProduct product;
    private Button         btnInvest;

    public static RegularProductBorrowerInfoFragment newInstance(long loanId) {
        RegularProductBorrowerInfoFragment fragment = new RegularProductBorrowerInfoFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_KEY_LOAN_ID, loanId);
        fragment.setArguments(args);
        return fragment;
    }

    private void initFind(View view) {
        basicInfoView = view.findViewById(R.id.basicInfo);
        workInfoView = view.findViewById(R.id.workInfo);
        financeInfoView = view.findViewById(R.id.financeInfo);
        usageView = view.findViewById(R.id.usage);
        tvBorrower = ((TextView) view.findViewById(R.id.tvBorrower));
        tvEducation = ((TextView) view.findViewById(R.id.tvEducation));
        tvAge = ((TextView) view.findViewById(R.id.tvAge));
        tvIsMarry = ((TextView) view.findViewById(R.id.tvIsMarry));
        tvBirthPlace = ((TextView) view.findViewById(R.id.tvBirthPlace));
        tvIndustry = ((TextView) view.findViewById(R.id.tvIndustry));
        tvJob = ((TextView) view.findViewById(R.id.tvJob));
        tvCompanySize = ((TextView) view.findViewById(R.id.tvCompanySize));
        tvJobExperience = ((TextView) view.findViewById(R.id.tvJobExperience));
        tvAddress = ((TextView) view.findViewById(R.id.tvAddress));
        tvMonthIncome = ((TextView) view.findViewById(R.id.tvMonthIncome));
        tvMonthOut = ((TextView) view.findViewById(R.id.tvMonthOut));
        tvHaveHouse = ((TextView) view.findViewById(R.id.tvHaveHouse));
        tvHaveCar = ((TextView) view.findViewById(R.id.tvHaveCar));
        tvHaveHouseLoan = ((TextView) view.findViewById(R.id.tvHaveHouseLoan));
        tvHaveCarLoan = ((TextView) view.findViewById(R.id.tvHaveCarLoan));
        tvMonthUsage = ((TextView) view.findViewById(R.id.tvMonthUsage));
        btnInvest = ((Button) view.findViewById(R.id.btnInvest));
        btnInvest.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        if (args != null) {
            mLoanId = args.getLong(ARGS_KEY_LOAN_ID, 1);
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(PersonalInfo event) {
        if (event != null && isAdded()) {
            PersonalInfo.BorrowerInfo borrowerInfo = event.borrowerInfo;
            if (borrowerInfo != null)
            {
                basicInfoView.setVisibility(View.GONE);
                PersonalInfo.BasicInfo basicInfo = borrowerInfo.basicInfo;
                if(basicInfo!=null)
                {
                    basicInfoView.setVisibility(View.VISIBLE);
                    tvBorrower.setText(FormatUtils.hideName(basicInfo.userName));
                    tvAge.setText(""+basicInfo.age);
                    tvIsMarry.setText(basicInfo.marryStatus);
                    tvEducation.setText(basicInfo.qualifications);
                    tvBirthPlace.setText(basicInfo.homeTown);

                }

                workInfoView.setVisibility(View.GONE);
                PersonalInfo.JobInfo jobInfo = borrowerInfo.jobInfo;
                if(jobInfo!=null)
                {
                    workInfoView.setVisibility(View.VISIBLE);
                    tvIndustry.setText(jobInfo.tradeType);
                    tvCompanySize.setText(jobInfo.companyScale);
                    tvJob.setText(jobInfo.duty);
                    tvAddress.setText(jobInfo.workCity);
                    tvJobExperience.setText(jobInfo.workYear);
                }
                PersonalInfo.FinanceInfo financeInfo = borrowerInfo.financeInfo;
                financeInfoView.setVisibility(View.GONE);
                if(financeInfo!=null)
                {
                    financeInfoView.setVisibility(View.VISIBLE);
                    tvMonthIncome.setText(""+financeInfo.income+"元");
                    tvMonthOut.setText(""+financeInfo.pay+"元");
                    tvHaveHouse.setText(financeInfo.housecondition);
                    tvHaveCar.setText(financeInfo.isBuycar);
                    tvHaveHouseLoan.setText(financeInfo.havaHouseLoan);
                    tvHaveCarLoan.setText(financeInfo.havaCarLoan);
                }
                //PersonalInfo.BorrowedUse borrowedUse = borrowerInfo.borrowedUse;
                usageView.setVisibility(View.GONE);
//                    if(borrowedUse!=null)
//                    {
//                        usageView.setVisibility(View.VISIBLE);
//                        tvMonthUsage.setText(borrowedUse.moneyUse);
//                    }

            }
        }
    }
    public void onEventMainThread(RegularProductInfo event) {
        if (event != null && isAdded()) {
            product = event.product;
            int pro = (int) (100 * product.getTotal_tend_money() / product.getIssueLoan());

            String state = "立即投资";
            switch ((int) product.getLoanstate())//1未发布 2进行中 3回款中 4已完成
            {
                case 1:
                    state = getString(R.string.productState1);//待售
                    btnInvest.setEnabled(false);
                    break;
                case 2:
                    if (100 > pro) {
                        state = getString(R.string.productState2);
                        btnInvest.setEnabled(true);
                    } else {
                        state = "进行中";
                        btnInvest.setEnabled(false);
                    }
                    break;
                case 3:
                    state = getString(R.string.productState3);
                    pro = 100;
                    btnInvest.setEnabled(false);
                    break;
                case 4:
                    state = getString(R.string.productState4);
                    pro = 100;
                    btnInvest.setEnabled(false);
                    break;
            }
            btnInvest.setText(state);
            if (pro >= 100) {
                if (btnInvest != null) {
                    btnInvest.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    btnInvest.setTextColor(Color.parseColor("#999999"));
                    btnInvest.setEnabled(false);
                } else {
                    btnInvest.setEnabled(true);
                }
            }
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mContext = activity;
        mHttpService = new HttpService(mContext, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_borrower_info, container, false);

        initFind(view);
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_commonLoanDesc.ordinal() && isAdded()) {

        }
    }

    private void productInvest() {
        if (product != null){
            Intent intent = new Intent(getActivity(), ProductInvestActivity.class);
            intent.putExtra("pid", "" + product.getPid());
            FinanceApplication myApp = (FinanceApplication) getActivity().getApplication();
            myApp.currentPid = ""+product.getPid();
            startActivity(intent);
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnInvest){
            productInvest();
        }
    }
}
