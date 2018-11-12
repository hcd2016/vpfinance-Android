package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.gson.PersonalInfo;
import cn.vpfinance.vpjr.model.RegularProductInfo;
import de.greenrobot.event.EventBus;

/**
 */
public class RegularProductBorrowerCreditFragment extends BaseFragment implements View.OnClickListener {

    private static final String      ARGS_KEY_LOAN_ID = "loanId";
    private              Context     mContext         = null;
    private              HttpService mHttpService     = null;
    private              long        mLoanId          = 1;


    private FinanceProduct product;
    private Button         btnInvest;
    private View           view;

    public static RegularProductBorrowerCreditFragment newInstance(long loanId) {
        RegularProductBorrowerCreditFragment fragment = new RegularProductBorrowerCreditFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_KEY_LOAN_ID, loanId);
        fragment.setArguments(args);
        return fragment;
    }

    private void initFind(View view) {
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
            PersonalInfo.CreditAssess creditAssess = event.creditAssess;
            if (creditAssess != null)
            {
               if (null != creditAssess.creditInfo){
                   ((TextView) view.findViewById(R.id.pubBorrwNum)).setText(""+creditAssess.creditInfo.pubBorrwNum + "笔");
                   ((TextView) view.findViewById(R.id.creditAmt)).setText(""+creditAssess.creditInfo.creditAmt + "元");
                   ((TextView) view.findViewById(R.id.lendedAmt)).setText(""+creditAssess.creditInfo.lendedAmt + "元");
                   ((TextView) view.findViewById(R.id.delayAmt)).setText(""+creditAssess.creditInfo.delayAmt + "元");
                   ((TextView) view.findViewById(R.id.succBorrwNum)).setText(""+creditAssess.creditInfo.succBorrwNum + "笔");
                   ((TextView) view.findViewById(R.id.borrowedAmt)).setText(""+creditAssess.creditInfo.borrowedAmt + "元");
                   ((TextView) view.findViewById(R.id.retningAmt)).setText(""+creditAssess.creditInfo.retningAmt + "元");
                   ((TextView) view.findViewById(R.id.delayNum)).setText(""+creditAssess.creditInfo.delayNum + "元");
                   ((TextView) view.findViewById(R.id.payedNum)).setText(""+creditAssess.creditInfo.payedNum + "笔");
                   ((TextView) view.findViewById(R.id.payingAmt)).setText(""+creditAssess.creditInfo.payingAmt + "元");
               }
               if (null != creditAssess.creditLV){
                   ((TextView) view.findViewById(R.id.level)).setText(TextUtils.isEmpty(creditAssess.creditLV.level) ? "" : (creditAssess.creditLV.level + "级"));
               }

               if (null != creditAssess.creditRpt){
                    if (null != creditAssess.creditRpt.creditCard){
                        ((TextView) view.findViewById(R.id.num)).setText(""+creditAssess.creditRpt.creditCard.num);
                        ((TextView) view.findViewById(R.id.creditAmt3)).setText(""+creditAssess.creditRpt.creditCard.creditAmt + "元");
                        ((TextView) view.findViewById(R.id.delayNum3)).setText(""+creditAssess.creditRpt.creditCard.delayNum + "次");
                        ((TextView) view.findViewById(R.id.delayAmt3)).setText(""+creditAssess.creditRpt.creditCard.delayAmt + "元");
                    }
                   if (null != creditAssess.creditRpt.borrwLoanInfo){
                       ((TextView) view.findViewById(R.id.loanNum)).setText(""+creditAssess.creditRpt.borrwLoanInfo.loanNum + "笔");
                       ((TextView) view.findViewById(R.id.loanAmt)).setText(""+creditAssess.creditRpt.borrwLoanInfo.loanAmt + "元");
                       ((TextView) view.findViewById(R.id.remainAmt)).setText(""+creditAssess.creditRpt.borrwLoanInfo.remainAmt  + "元");
                       ((TextView) view.findViewById(R.id.endDt)).setText(TextUtils.isEmpty(creditAssess.creditRpt.borrwLoanInfo.endDt) ? "" : creditAssess.creditRpt.borrwLoanInfo.endDt);
                       ((TextView) view.findViewById(R.id.delayNum4)).setText(""+creditAssess.creditRpt.borrwLoanInfo.delayNum  + "次");
                       ((TextView) view.findViewById(R.id.delayAmt4)).setText(""+creditAssess.creditRpt.borrwLoanInfo.delayAmt + "元");
                   }
               }

            }
        }
    }
    public void onEventMainThread(RegularProductInfo event) {
        if (event != null && isAdded()) {
            product = event.product;
            int pro = (int) (100 * product.getTotal_tend_money() / product.getIssueLoan());

            String state = "我要出借";
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
        view = inflater.inflate(R.layout.fragment_borrower_credit, container, false);

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
            App myApp = (App) getActivity().getApplication();
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
