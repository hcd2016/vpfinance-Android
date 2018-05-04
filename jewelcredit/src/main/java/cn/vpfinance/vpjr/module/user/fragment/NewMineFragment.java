package cn.vpfinance.vpjr.module.user.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.setting.AutoInvestSettingActivity;
import cn.vpfinance.vpjr.module.setting.NewPersonalInfoActivity;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.module.trade.RechargBankActivity;
import cn.vpfinance.vpjr.module.trade.WithdrawBankActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.module.user.asset.AccountEActivity;
import cn.vpfinance.vpjr.module.user.asset.FundFlowActivity;
import cn.vpfinance.vpjr.module.user.asset.FundOverViewActivity;
import cn.vpfinance.vpjr.module.user.asset.FundRecordsActivity;
import cn.vpfinance.vpjr.module.user.asset.InvestSummaryActivity;
import cn.vpfinance.vpjr.module.user.asset.QueryReturnMoneyListActivity;
import cn.vpfinance.vpjr.module.user.asset.ReturnMoneyCalendarActivity;
import cn.vpfinance.vpjr.module.user.asset.TransferProductListActivity;
import cn.vpfinance.vpjr.module.user.personal.InvestTopActivity;
import cn.vpfinance.vpjr.module.user.personal.InviteGiftActivity;
import cn.vpfinance.vpjr.module.user.personal.MyMedalActivity;
import cn.vpfinance.vpjr.module.user.personal.TicketActivity;
import cn.vpfinance.vpjr.module.user.personal.TrialCoinActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.Logger;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.CircleImg;

/**
 * Created by zzlz13 on 2017/12/19.
 */

public class NewMineFragment extends BaseFragment {

    @Bind(R.id.vContentNoOpen)
    LinearLayout vContentNoOpen;
    @Bind(R.id.vContentOpen)
    LinearLayout vContentOpen;
    @Bind(R.id.vRefresh)
    SwipeRefreshLayout vRefresh;
    @Bind(R.id.vUserHead)
    CircleImg vUserHead;
    @Bind(R.id.tvUserName)
    TextView tvUserName;
    @Bind(R.id.tvDescribe)
    TextView tvDescribe;
    @Bind(R.id.tvTotalMoney)
    TextView tvTotalMoney;
    @Bind(R.id.tvAvailableMoney)
    TextView tvAvailableMoney;
    @Bind(R.id.tvTotalIncome)
    TextView tvTotalIncome;
    @Bind(R.id.tvReturnMoneyPrincipal)
    TextView tvReturnMoneyPrincipal;
    @Bind(R.id.tvReturnMoneyIncome)
    TextView tvReturnMoneyIncome;
    @Bind(R.id.ivBandActive)
    ImageView ivBandActive;
    @Bind(R.id.ivEAccountDot)
    ImageView ivEAccountDot;
    @Bind(R.id.ivTrialCoin)
    ImageView ivTrialCoin;
    @Bind(R.id.tvOpenBankAccount)
    TextView tvOpenBankAccount;
    @Bind(R.id.tvCanUseCoupon)
    TextView tvCanUseCoupon;
    @Bind(R.id.vClickBorrow)
    LinearLayout vClickBorrow;
    @Bind(R.id.tvReturnMoneyCount)
    TextView tvReturnMoneyCount;
    @Bind(R.id.vContentInform)
    RelativeLayout vContentInform;
    @Bind(R.id.tvInform)
    TextView tvInform;
    @Bind(R.id.ibCancel)
    ImageButton ibCancel;
    @Bind(R.id.ivUpdateHxTag)
    ImageView ivUpdateHxTag;

    private HttpService mHttpService;
    private User user;
    private UserInfoBean mUserInfoBean;
    private String voucher_count;
    private String addrate_count;
    private String presell_count;
    private String killPercent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_mine_new, container, false);
        ButterKnife.bind(this, view);
        mHttpService = new HttpService(getActivity(), this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ActionBarLayout) view.findViewById(R.id.titleBar))
                .reset()
                .setTitle("我的账户")
                .setColor(ContextCompat.getColor(getActivity(), R.color.account_bank_header))
                .setActionRight("设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), NewPersonalInfoActivity.class));
                    }
                });

        FinanceApplication application = (FinanceApplication) getActivity().getApplication();
        if (application.isFirstRegieter) {
            RealnameAuthActivity.goThis(mContext);
            application.isFirstRegieter = false;
        }

        vRefresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.account_bank_header));
        vRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDate();
            }
        });
        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(mContext);
        boolean isOpen = sp.getBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT, false);
        vContentNoOpen.setVisibility(isOpen ? View.GONE : View.VISIBLE);
        vContentOpen.setVisibility(!isOpen ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        user = DBUtils.getUser(mContext);
        loadDate();

        if (user != null){
            boolean booleanValue = SharedPreferencesHelper.getInstance(getActivity()).getBooleanValue("firstInto_" + user.getUserId(),true);
            if (booleanValue){
                ivUpdateHxTag.setVisibility(View.VISIBLE);
            }else{
                ivUpdateHxTag.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (((MainActivity) getActivity()).mLastRadioId == R.id.maintab_mine_radiobtn){
            if (Common.isForceLogout(mContext,json))    return;
        }
        vRefresh.setRefreshing(false);
        if (reqId == ServiceCmd.CmdId.CMD_liteMoneyInfo.ordinal()) {
            String msg = json.optString("msg");
            if ("1".equals(msg)) {
                JSONObject lite_money = json.optJSONObject("lite_money");
                if (lite_money != null) {
                    int status = lite_money.optInt("status", 1);
                    if ("0".equals(status + "") || "3".equals(status + "")) {
                        ivTrialCoin.setVisibility(View.VISIBLE);
                    } else {
                        ivTrialCoin.setVisibility(View.GONE);
                    }
                }
            } else {
                ivTrialCoin.setVisibility(View.GONE);
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_Message_Notice.ordinal()) {
            if ("true".equals(json.optString("success"))) {
                final String accountInform = json.optString("title");
                if (TextUtils.isEmpty(accountInform)) return;
                String noShowInfo = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.MINE_NO_SHOW_INFO);
                if (!accountInform.equals(noShowInfo)) {
                    tvInform.setText(accountInform);
                    final String url = json.optString("url");
                    if (!TextUtils.isEmpty(url)) {
                        tvInform.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoWeb(url, accountInform);
                            }
                        });
                        vContentInform.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                vContentInform.setVisibility(View.GONE);
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            /** 第二种解析*/
            mUserInfoBean = mHttpService.onGetUserInfo(json);
            if (mUserInfoBean == null)  return;

            ivBandActive.setVisibility("1".equals(mUserInfoBean.isBindHxBank) ? View.GONE : View.VISIBLE);
            if (!TextUtils.isEmpty(mUserInfoBean.customerType) && "2".equals(mUserInfoBean.customerType)) {
                ivBandActive.setVisibility(View.GONE);
            }
            //打败人数百分比
            killPercent = mUserInfoBean.number;
//            if (!TextUtils.isEmpty(killPercent)) {
//                tvKillPercent.setText(killPercent);
//            }

            vClickBorrow.setVisibility((!TextUtils.isEmpty(mUserInfoBean.isShowBorrowMenu) && "1".equals(mUserInfoBean.isShowBorrowMenu)) ? View.VISIBLE : View.GONE);
            /*可用余额*/
            String cashBalance = mUserInfoBean.cashBalance;
            tvAvailableMoney.setText(TextUtils.isEmpty(cashBalance) ? "0.00" : FormatUtils.formatDown(cashBalance));
            /*资金总额*/
            String netAsset = mUserInfoBean.netAsset;
            tvTotalMoney.setText(TextUtils.isEmpty(netAsset) ? "0.00" : FormatUtils.formatDown(netAsset));
            /*累计收益*/
            String realMoney = mUserInfoBean.realMoney;
            tvTotalIncome.setText(TextUtils.isEmpty(realMoney) ? "0.00" : FormatUtils.formatDown(realMoney));

            tvUserName.setText("你好!" + mUserInfoBean.userName);
            tvOpenBankAccount.setText("1".equals(mUserInfoBean.isAutoTender) ? "已开启" : "未开启");

            boolean isOpen = "1".equals(mUserInfoBean.isOpen) ? true : false;
            vContentNoOpen.setVisibility(isOpen ? View.GONE : View.VISIBLE);
            vContentOpen.setVisibility(!isOpen ? View.GONE : View.VISIBLE);

            String tradeFlowRecordInfo = "" + mUserInfoBean.returnedCount;
            String text = "近七日有" + tradeFlowRecordInfo + "笔回款";
            tvReturnMoneyCount.setText(text);
            tvCanUseCoupon.setText("0".equals(mUserInfoBean.canUseTotal) ? "" : mUserInfoBean.canUseTotal + "张券可使用");
            voucher_count = TextUtils.isEmpty(mUserInfoBean.canUseVoucher) ? "" : mUserInfoBean.canUseVoucher;
            addrate_count = TextUtils.isEmpty(mUserInfoBean.canUseCoupon) ? "" : mUserInfoBean.canUseCoupon;
            presell_count = TextUtils.isEmpty(mUserInfoBean.canUseBookCoupon) ? "" : mUserInfoBean.canUseBookCoupon;

            tvReturnMoneyPrincipal.setText(mUserInfoBean.capitalAmountSum);//待回款本金
            tvReturnMoneyIncome.setText(mUserInfoBean.profitAmountSum);//待回款利息

            tvDescribe.setText(TextUtils.isEmpty(mUserInfoBean.signature) ? "未设置签名" : mUserInfoBean.signature);//个人签名

            SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(getActivity());

//            realName = mUserInfoBean.realName;

            if (user != null) {

                String mHeadImgUrl = json.optString("headImg");
                mHeadImgUrl = HttpService.mBaseUrl + mHeadImgUrl;
                String userHeadUrl = sp.getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
                if (TextUtils.isEmpty(userHeadUrl) || !mHeadImgUrl.equals(userHeadUrl)) {
                    sp.putStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId(), mHeadImgUrl);
                }
                String headUrl = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
                if (headUrl == null) {
                    vUserHead.setImageResource(R.drawable.user_head);
                } else {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(headUrl, vUserHead);
                }
            }
            if (!TextUtils.isEmpty(mUserInfoBean.isBindHxBank)) {
                ((FinanceApplication) getActivity().getApplication()).isBindBank = mUserInfoBean.isBindHxBank;
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (vRefresh != null) vRefresh.setRefreshing(false);
    }

    @Override
    protected void loadDate() {
        mHttpService.getUserInfoBank();
        if (AppState.instance().logined()) {
            mHttpService.getMessageNotice();
            mHttpService.liteMoneyInfo(Constant.AccountBank);
            String userNo = AppState.instance().getLoginUserInfo().userNo;
            String sesnId = AppState.instance().getSessionCode();
            mHttpService.getBankCard(sesnId);
        }
    }

    @OnClick({R.id.ivBandActive, R.id.vClickBorrow, R.id.vClickWithdraw, R.id.vClickRecharge, R.id.vClickAutoInvestSetting, R.id.vClickEAccount, R.id.vClickOpenBankAccount,
            R.id.vClickReturnMoney, R.id.vClickMyInvest, R.id.vClickFundOverView, R.id.vClickMyTransfer, R.id.vClickFundFlow, R.id.vClickInvestSummary, R.id.vClickVoucher,
            R.id.vClickTrialCoin, R.id.vClickInviteGift, R.id.vClickInvestTop, R.id.vClickMedal, R.id.vClickRisk})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.ivBandActive:
                if (user != null) {
                    Long userId = user.getUserId();
                    //跳转到存管绑卡第三方界面
                    BindBankHintActivity.goThis(mContext,userId.toString());
                } else {
                    gotoActivity(LoginActivity.class);
                }
                break;
            case R.id.vClickBorrow:
                String url = "h5/user/toBorrow?uid=" + DBUtils.getUser(getActivity()).getUserId() + "&accountType=1";
                gotoWeb(url, "");
                break;
            case R.id.vClickWithdraw:
                Intent intent = new Intent(getActivity(), WithdrawBankActivity.class);
                String cashBalance1 = "0.0";
                String frozenAmtN = "0.0";
                if (mUserInfoBean != null) {
                    cashBalance1 = mUserInfoBean.cashBalance;
                    frozenAmtN = mUserInfoBean.frozenAmtN;
                }
                intent.putExtra(WithdrawBankActivity.CASHBALANCE, cashBalance1);
                intent.putExtra(WithdrawBankActivity.FROZENAMTN, frozenAmtN);
                startActivity(intent);
                break;
            case R.id.vClickRecharge:
                gotoActivity(RechargBankActivity.class);
                break;
            case R.id.vClickAutoInvestSetting:
                Intent autoInvestIntent = new Intent(getContext(), AutoInvestSettingActivity.class);
                autoInvestIntent.putExtra(Constant.AccountType, Constant.AccountBank);
                String cashBalance = "0.0";
                String isAutoTender = "false";
                if (mUserInfoBean != null) {
                    cashBalance = mUserInfoBean.cashBalance;
                    isAutoTender = mUserInfoBean.isAutoTender;
                }
                autoInvestIntent.putExtra(AutoInvestSettingActivity.ACCOUNT_BALANCE, cashBalance);
                autoInvestIntent.putExtra(AutoInvestSettingActivity.IS_OPEN_AUTO_INVEST, isAutoTender);
                autoInvestIntent.putExtra(AutoInvestSettingActivity.IS_OPEN_BANK_ACCOUNT, ((FinanceApplication)getActivity().getApplication()).isBindBank);
                gotoActivity(autoInvestIntent);
                break;
            case R.id.vClickEAccount:
                gotoActivity(AccountEActivity.class);
                break;
            case R.id.vClickOpenBankAccount:
                if (user != null) {
                    Long userId = user.getUserId();
                    if (TextUtils.isEmpty(user.getRealName())) {
                        Utils.Toast(mContext, "开通存管账户前请先进行实名认证");
                        gotoActivity(RealnameAuthActivity.class);
                    } else {
                        gotoWeb("/hx/account/create?userId=" + userId, "");
                    }
                } else {
                    gotoActivity(LoginActivity.class);
                }
                break;
            case R.id.vClickReturnMoney:
                SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(FinanceApplication.getAppContext());
                String state = sp.getStringValue(SharedPreferencesHelper.STATE_RETURN_CALENDER_OR_LIST);
                if ("2".equals(state)) {
                    gotoActivity(new Intent(mContext, QueryReturnMoneyListActivity.class).putExtra(Constant.AccountType, Constant.AccountBank));
                } else {
                    gotoActivity(new Intent(mContext, ReturnMoneyCalendarActivity.class).putExtra(Constant.AccountType, Constant.AccountBank));
                }
                break;
            case R.id.vClickMyInvest:
                gotoActivity(new Intent(mContext, FundRecordsActivity.class).putExtra(Constant.AccountType, Constant.AccountBank));
                break;
            case R.id.vClickFundOverView:
                Intent intent1 = new Intent(mContext, FundOverViewActivity.class);
                intent1.putExtra("cashBalance", TextUtils.isEmpty(mUserInfoBean.cashBalance) ? "" : mUserInfoBean.cashBalance);
                intent1.putExtra("inCount", TextUtils.isEmpty(mUserInfoBean.inCount) ? "" : mUserInfoBean.inCount);
                intent1.putExtra("frozenAmtN", TextUtils.isEmpty(mUserInfoBean.frozenAmtN) ? "" : mUserInfoBean.frozenAmtN);
                intent1.putExtra("netAsset", TextUtils.isEmpty(mUserInfoBean.netAsset) ? "" : mUserInfoBean.netAsset);
                intent1.putExtra(Constant.AccountType, Constant.AccountBank);
                gotoActivity(intent1);
                break;
            case R.id.vClickMyTransfer://我要转让
                gotoActivity(new Intent(mContext, TransferProductListActivity.class).putExtra(Constant.AccountType, Constant.AccountBank));
                break;
            case R.id.vClickFundFlow:
                gotoActivity(new Intent(mContext, FundFlowActivity.class).putExtra(Constant.AccountType, Constant.AccountBank));
                break;
            case R.id.vClickInvestSummary:
                gotoActivity(new Intent(mContext, InvestSummaryActivity.class).putExtra(Constant.AccountType, Constant.AccountBank));
                break;
            case R.id.vClickVoucher:
                TicketActivity.goThis(mContext, Constant.AccountBank, voucher_count, addrate_count, presell_count);
                break;
            case R.id.vClickTrialCoin:
                TrialCoinActivity.goThis(mContext, Constant.AccountBank);
                break;
            case R.id.vClickInviteGift:
                InviteGiftActivity.goThis(mContext);
                break;
            case R.id.vClickInvestTop:
                InvestTopActivity.goThis(mContext, Constant.AccountBank, killPercent);
                break;
            case R.id.vClickMedal:
                MyMedalActivity.goThis(mContext, Constant.AccountBank);
                break;
            case R.id.vClickRisk:
                if (user != null){
                    gotoWeb("/h5/help/riskInvestigation?userId="+user.getUserId(),"风险评测");
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
