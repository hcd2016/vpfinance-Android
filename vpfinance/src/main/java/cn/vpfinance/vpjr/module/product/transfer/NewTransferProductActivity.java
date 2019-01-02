package cn.vpfinance.vpjr.module.product.transfer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.NewTransferProductBean;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialogFragment;
import cn.vpfinance.vpjr.module.dialog.InvestLianLianInformDialog;
import cn.vpfinance.vpjr.module.dialog.InvestmentRiskTipsDialog;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.module.product.record.NoRepayListActivity;
import cn.vpfinance.vpjr.module.product.record.ProductInvestListActivity;
import cn.vpfinance.vpjr.module.product.shenyang.PresellProductActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.MyCountDownTimer;

/**
 * Created by Administrator on 2016/10/25.
 * 转让专区
 */
public class NewTransferProductActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_PRODUCT_ID = "id";

    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.tvProductName)
    TextView mTvProductName;
    @Bind(R.id.ivProductState)
    ImageView mIvProductState;
    @Bind(R.id.tvEarningsYield)
    TextView mTvEarningsYield;
    @Bind(R.id.tvProductTotalMoney)
    TextView mTvProductTotalMoney;
    @Bind(R.id.tvProductAvailableMoney)
    TextView mTvProductAvailableMoney;
    @Bind(R.id.tvProductMonth)
    TextView mTvProductMonth;
    @Bind(R.id.presellNumberbar)
    NumberProgressBar mPresellNumberbar;
    @Bind(R.id.clickToNativeProduct)
    LinearLayout mClickToNativeProduct;
    @Bind(R.id.tvProductNativeMoney)
    TextView mTvProductNativeMoney;
    @Bind(R.id.tvProductRate)
    TextView mTvProductRate;
    @Bind(R.id.tvRefund)
    TextView mTvRefund;
    @Bind(R.id.tvTransferWay)
    TextView mTvTransferWay;
    @Bind(R.id.clickToRecord)
    LinearLayout mClickToRecord;
    @Bind(R.id.clickToAvailableTime)
    LinearLayout mClickToAvailableTime;
    @Bind(R.id.btnInvest)
    Button mBtnInvest;
    @Bind(R.id.countDown)
    MyCountDownTimer mCountDownTimer;
    @Bind(R.id.tv_finish)
    TextView tvFinish;
    @Bind(R.id.transfer_risk_desc)
    TextView transferRiskDesc;
    private Context mContext;
    private HttpService mHttpService;
    private long mPid;
    private NewTransferProductBean mBean;
    private int product; //2银行存管


    public static void goNewTransferProductActivity(Context context, long id) {
        if (context != null) {
            Intent intent = new Intent(context, NewTransferProductActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, id);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_product2);
        ButterKnife.bind(this);
        mContext = this;
        mHttpService = new HttpService(this, this);
        Intent intent = getIntent();
        if (intent != null) {
            mPid = intent.getLongExtra(EXTRA_PRODUCT_ID, 0);
        }
        mTitleBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("产品详情");
        mClickToNativeProduct.setOnClickListener(this);
        mClickToRecord.setOnClickListener(this);
        mBtnInvest.setOnClickListener(this);
        mClickToAvailableTime.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPid != 0) {
            mHttpService.getTransferProductInfo("" + mPid);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Transfer_Product_Info.ordinal()) {
            try {
                mBean = new Gson().fromJson(json.toString(), NewTransferProductBean.class);
                if (mBean != null) {
                    product = mBean.product;

                    if (!TextUtils.isEmpty(mBean.loanTitle)) {
                        mTitleBar.setTitle(mBean.loanTitle);
                    }
                    mTvProductName.setText(mBean.loanTitle);
                    //1质押，2保证，3抵押，4信用，5实地
                    Common.productSubType(mContext, mIvProductState, mBean.subType);

                    mTvEarningsYield.setText(FormatUtils.formatDown((mBean.rate * 100)) + "%");
                    mTvProductTotalMoney.setText(FormatUtils.formatDown2(mBean.issueloan) + "元");
                    mTvProductAvailableMoney.setText(FormatUtils.formatDown2(mBean.canBuyMoney) + "元");
                    mTvProductMonth.setText(mBean.month);
                    mTvTransferWay.setText(mBean.disType);
                    mTvRefund.setText(mBean.refundWay);
                    mTvProductRate.setText(FormatUtils.formatRate(mBean.sourceRate * 100) + "%");

                    double originIssueLoan = mBean.originIssueLoan;//原始本金
                    mTvProductNativeMoney.setText("" + originIssueLoan);

                    //进度
                    double pro = (mBean.hadTenderMoney / mBean.issueloan) * 100;
                    //status
                    int status = mBean.status;
                    String state = "我要出借";

                    switch (status) {//1未发布 2进行中 3回款中 4已完成
                        case 1:
                            state = getString(R.string.productState1);//待售
                            mBtnInvest.setEnabled(false);
                            break;
                        case 2:
                            if (100 > pro) {
                                state = getString(R.string.productState2);
                                mBtnInvest.setEnabled(true);
                            } else {
                                state = "满标审核";
                                mCountDownTimer.cancel();
                                mBtnInvest.setEnabled(false);
                            }
                            break;
                        case 3:
                            state = getString(R.string.productState3);
                            pro = 100;
                            mBtnInvest.setEnabled(false);
                            mCountDownTimer.cancel();
                            mCountDownTimer.setVisibility(View.GONE);
                            tvFinish.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            state = getString(R.string.productState4);
                            pro = 100;
                            mBtnInvest.setEnabled(false);
                            mCountDownTimer.cancel();
                            mCountDownTimer.setVisibility(View.GONE);
                            tvFinish.setVisibility(View.VISIBLE);
                            break;
                    }
                    mBtnInvest.setText(state);
                    mPresellNumberbar.setProgress((float) pro);

                    mCountDownTimer.setCountDownTime(mContext, mBean.bidEndTime);

                    //出借要求条目风险文案
                    switch (mBean.riskLevel) {
                        case 1:
                            transferRiskDesc.setText("风险评估级别“保守型”及以上");
                            break;
                        case 2:
                            transferRiskDesc.setText("风险评估级别“稳健型”及以上");
                            break;
                        case 3:
                            transferRiskDesc.setText("风险评估级别“积极型”及以上");
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            if (!AppState.instance().logined() || json.optString("success").equals("false")) {//是否登录
                gotoActivity(LoginActivity.class);
                return;
            }
            UserInfoBean mUserInfoBean = mHttpService.onGetUserInfo(json);
            if (null == mUserInfoBean) return;
            boolean isNewUser = SharedPreferencesHelper.getInstance(mContext).getBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER);
            if (mBean == null) {
                return;
            }
            if (mBean.product != 4 && isNewUser) {//新用户返回存管专区弹窗
                InvestLianLianInformDialog dialog = new InvestLianLianInformDialog();
                dialog.show(getFragmentManager(), "InvestLianLianInformDialog");
                return;
            }

            if (mBean.product == 4 && !mUserInfoBean.isOpen.equals("1")) { //是否开通存管
//                new AlertDialog.Builder(this)
//                        .setTitle("开通存管账户")
//                        .setMessage("根据监管要求，请先开通银行存管账户")
//                        .setPositiveButton("立即开通", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                User user = DBUtils.getUser(mContext);
//                                if (user != null) {
//                                    if (TextUtils.isEmpty(user.getRealName())) {
//                                        RealnameAuthActivity.goThis(NewTransferProductActivity.this);
//                                        Utils.Toast("请先去实名认证");
//                                    } else {
//                                        gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
//                                    }
//                                }
//                            }
//                        })
//                        .setNegativeButton("暂不", null)
//                        .show();
                new CommonTipsDialogFragment.Buidler()
                        .setTitle("开通存管账户")
                        .setContent("根据监管要求，请先开通银行存管账户")
                        .setBtnRight("立即开通")
                        .setOnRightClickListener(new CommonTipsDialogFragment.OnRightClickListner() {
                            @Override
                            public void rightClick() {
                                User user = DBUtils.getUser(mContext);
                                if (user != null) {
                                    if (TextUtils.isEmpty(user.getRealName())) {
                                        RealnameAuthActivity.goThis(NewTransferProductActivity.this);
                                        Utils.Toast("请先去实名认证");
                                    } else {
                                        gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
                                    }
                                }
                            }
                        })
                        .setBtnLeft("暂不")
                        .createAndShow(this);
                return;
            }
            final User user = DBUtils.getUser(mContext);
            if (mBean != null) {
                if (mBean.answerStatus == 0) {
                    Utils.Toast("请先进行风险评测");
                    gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
                    return;
                }
                if (mBean.answerStatus == 2) {//风险评测
//                    new AlertDialog.Builder(mContext)
//                            .setMessage("您很久未进行过出借人风险测评，根据监管要求，请先完成风险测评再进行出借")
//                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if (user != null) {
//                                        gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
//                                    }
//                                }
//                            })
//                            .setNegativeButton("下次再说", null)
//                            .show();
//                    return;
                    new CommonTipsDialogFragment.Buidler()
                            .setContent("为合理优化您的资产配置，请完成风险测评")
                            .setBtnRight("确认")
                            .setOnRightClickListener(new CommonTipsDialogFragment.OnRightClickListner() {
                                @Override
                                public void rightClick() {
                                    if (user != null) {
                                        gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
                                    }
                                }
                            })
                            .setBtnLeft("下次再说")
                            .createAndShow((FragmentActivity) mContext);
                    return;
                }

                //风险提示弹窗
                if (mBean.riskLevel > mUserInfoBean.riskLevel) {//产品风险等级高过个人风险等级
                    InvestmentRiskTipsDialog investmentRiskTipsDialog = new InvestmentRiskTipsDialog(this);
                    switch (mUserInfoBean.riskLevel) {
                        case 1:
                            investmentRiskTipsDialog.setTvDesc1Content("您的风险评估级别为“保守型”,");
                            investmentRiskTipsDialog.setTvDesc2Content("建议出借标的风险级别不超过“保守型”");
                            break;
                        case 2:
                            investmentRiskTipsDialog.setTvDesc1Content("您的风险评估级别为“稳健型”,");
                            investmentRiskTipsDialog.setTvDesc2Content("建议出借标的风险级别不超过“稳健型”");
                            break;
                        case 3:
                            investmentRiskTipsDialog.setTvDesc1Content("您的风险评估级别为“积极型”,");
                            investmentRiskTipsDialog.setTvDesc2Content("建议出借标的风险级别不超过“积极型”");
                            break;
                    }
                    investmentRiskTipsDialog.setOnCheckClickListner(new InvestmentRiskTipsDialog.OnCheckClickListner() {
                        @Override
                        public void onCheckClick() {
                            gotoWeb("/h5/help/riskCheckReason?userId="+user.getUserId(),"查看原因");
                        }
                    });
                    investmentRiskTipsDialog.setOnConfimClickListner(new InvestmentRiskTipsDialog.OnConfimClickListner() {
                        @Override
                        public void onConfimClick() {
                            invest();
                        }
                    });
                    investmentRiskTipsDialog.show();
                } else {
                    invest();
                }
            }
        }
    }

    public void invest() {
        Intent intent = new Intent(mContext, ProductInvestActivity.class);
        intent.putExtra("pid", "" + mBean.loanId);
        intent.putExtra(ProductInvestActivity.PRODUCT_TOTAL_MONEY, mBean.issueloan);//转让总额
        intent.putExtra(ProductInvestActivity.PRODUCT_NATIVE_MONEY, mBean.originIssueLoan);//原始本金
        intent.putExtra(ProductInvestActivity.PRODUCT_TRANSFER_RATE, mBean.rate);//预期年化利率
//                    intent.putExtra(ProductInvestActivity.PRODUCT_NATIVE_MONEY, product.getOriginIssueLoan());//原始本金
        intent.putExtra(ProductInvestActivity.TYPE_PRODUCT, ProductInvestActivity.TYPE_TRANSFER);
        intent.putExtra(Constant.AccountType, product == 4 ? Constant.AccountBank : Constant.AccountLianLain);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clickToNativeProduct:
                if (mBean.product == 0 && mBean.productType == 3) {
                    PresellProductActivity.goPresellProductActivity(mContext, "" + mBean.sourceLoanId);
                } else if (mBean.product == 4) {
                    NewRegularProductActivity.goNewRegularProductActivity(mContext, mBean.sourceLoanId, (int) mPid, mBean.loanTitle, false);
                } else {
                    if (mBean != null && mBean.sourceLoanId != 0) {
                        NewRegularProductActivity.goNewRegularProductActivity(mContext, mBean.sourceLoanId, (int) mPid, mBean.loanTitle, false);
                    }
                }
                break;
            case R.id.clickToRecord:
                if (mBean != null) {
                    if (mBean.frequency == null) {
                        mBean.frequency = -1;
                    }
                    ProductInvestListActivity.goProductInvestListActivity(this, mBean.loanId, -1, mBean.frequency, false);
                }
//                if (product != null && rList != null) {
//                    ProductInvestListActivity.goProductInvestListActivity(mContext, product.getPid(), product.getType());
//                }
                break;
            case R.id.clickToAvailableTime:
                if (mBean != null) {//剩余回款计划
                    NoRepayListActivity.goActivity(mContext, (ArrayList<NewTransferProductBean.NoRepayListBean>) mBean.noRepayList);
                }
                break;
            case R.id.btnInvest://转让按钮
                if (!AppState.instance().logined()) {
                    gotoActivity(LoginActivity.class);
                    return;
                }
                mHttpService.getUserInfo();
                break;
        }
    }
}
