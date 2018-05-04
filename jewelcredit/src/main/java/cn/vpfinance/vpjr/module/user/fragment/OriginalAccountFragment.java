package cn.vpfinance.vpjr.module.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.module.dialog.OpenBankDialog;
import cn.vpfinance.vpjr.module.dialog.RechargeCloseDialog;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.setting.AutoInvestProtocolActivity;
import cn.vpfinance.vpjr.module.setting.AutoInvestSettingActivity;
import cn.vpfinance.vpjr.module.setting.MyDescribeAcitvity;
import cn.vpfinance.vpjr.module.setting.PersonalInfoActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.module.trade.WithdrawDepositActivity;
import cn.vpfinance.vpjr.module.user.PersonalCardActivity;
import cn.vpfinance.vpjr.module.user.asset.FundFlowActivity;
import cn.vpfinance.vpjr.module.user.asset.FundOverViewActivity;
import cn.vpfinance.vpjr.module.user.asset.FundRecordsActivity;
import cn.vpfinance.vpjr.module.user.asset.InvestSummaryActivity;
import cn.vpfinance.vpjr.module.user.asset.QueryReturnMoneyListActivity;
import cn.vpfinance.vpjr.module.user.asset.ReturnMoneyCalendarActivity;
import cn.vpfinance.vpjr.module.user.asset.TransferProductListActivity;
import cn.vpfinance.vpjr.module.welcome.SetupGuideActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.CircleImg;

/**
 * 连连账户
 * Created by zzlz13 on 2017/7/28.
 */

public class OriginalAccountFragment extends BaseFragment {

    @Bind(R.id.header)
    public RelativeLayout mHeader;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.userHead)
    CircleImg userHead;
    @Bind(R.id.tvUserName)
    TextView tvUserName;
    @Bind(R.id.level)
    TextView level;
    @Bind(R.id.my_describe)
    TextView myDescribe;
    @Bind(R.id.tv_kill_percent)
    TextView tvKillPercent;
    @Bind(R.id.tvTotalMoney)
    TextView tvTotalMoney;
    @Bind(R.id.tvAvailableMoney)
    TextView tvAvailableMoney;
    @Bind(R.id.tvTotalIncome)
    TextView tvTotalIncome;
    @Bind(R.id.tvReturnMoneyCount)
    TextView tvReturnMoneyCount;
    @Bind(R.id.tvReturnMoneyPrincipal)
    TextView tvReturnMoneyPrincipal;
    @Bind(R.id.tvReturnMoneyIncome)
    TextView tvReturnMoneyIncome;


    @Bind(R.id.tv_mine_inform)
    TextView tv_mine_inform;
    @Bind(R.id.content_mine_inform)
    RelativeLayout content_mine_inform;
    @Bind(R.id.content_kill_percent)
    LinearLayout content_kill_percent;

    @Bind(R.id.tv_open_bank_account)
    TextView tvOpenBankAccount;
    @Bind(R.id.click_recharge)
    TextView tvRecharge;
    @Bind(R.id.isAllowRecharge)
    ImageView isAllowRecharge;
    @Bind(R.id.content_setup_guide)
    LinearLayout content_setup_guide;
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;

    private User user;
    private HttpService mHttpService;
    private String accountInform;
    private UserInfoBean mUserInfoBean;

    private int[] guideSetupState = new int[]{0, 0, 0}; //0 默认 1完成 2 未完成

    private int accountType = 0;
    private String killPercent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_original_account, container, false);
        try {
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHttpService = new HttpService(getActivity(), this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FinanceApplication application = (FinanceApplication) getActivity().getApplication();
        if (application.isFirstRegieter) {
            RealnameAuthActivity.goThis(mContext);
            application.isFirstRegieter = false;
        }

        titleBar.reset()
                .setTitle("我的账户")
                .setColor(ContextCompat.getColor(getActivity(), R.color.account_original_header))
                .setActionRight("设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
                    }
                })
                .setActionLeft("返回存管账户", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) getActivity()).switchToTab(2);
                    }
                });

        mRefresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.account_original_header));
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDate();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDate();
        MobclickAgent.onPageStart("MineFragment");
//        FinanceApplication application = (FinanceApplication) getActivity().getApplication();
//        Map<String, Boolean> guideConfig = application.guideConfig;
//        if (guideConfig != null && guideConfig.get(FinanceApplication.SHOW_SETUP_GUIDE) != null && guideConfig.get(FinanceApplication.SHOW_SETUP_GUIDE)) {
//            Intent intent = new Intent(getActivity(), SetupGuideActivity.class);
//            int[] data = new int[]{2, 2, 2};
//            intent.putExtra(SetupGuideActivity.SETUP_STATE, data);
//            gotoActivity(intent);
//
//            application.guideConfig.put(FinanceApplication.SHOW_SETUP_GUIDE, false);
//        }

        user = DBUtils.getUser(mContext);
    }

    @Override
    protected void loadDate() {
        mHttpService.getUserInfo();
        if (AppState.instance().logined()) {
            mHttpService.getMessageNotice();
//            if (user != null) {
//                mHttpService.getFundOverInfo("" + user.getUserId(),accountType);
//            }

            String userNo = AppState.instance().getLoginUserInfo().userNo;
            String sesnId = AppState.instance().getSessionCode();
            mHttpService.getBankCard(sesnId);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, final JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (((MainActivity) getActivity()).mLastRadioId == R.id.maintab_mine_radiobtn) {
            if (Common.isForceLogout(mContext, json)) return;
        }
        mRefresh.setRefreshing(false);

        if (reqId == ServiceCmd.CmdId.CMD_Message_Notice.ordinal()) {
            if ("true".equals(json.optString("success"))) {
                accountInform = json.optString("title");
                if (TextUtils.isEmpty(accountInform)) return;
                String noShowInfo = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.MINE_NO_SHOW_INFO);
                if (!accountInform.equals(noShowInfo)) {
                    tv_mine_inform.setText(accountInform);
                    final String url = json.optString("url");
                    if (!TextUtils.isEmpty(url)) {
                        tv_mine_inform.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoWeb(url, accountInform);
                            }
                        });
                        content_mine_inform.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                content_mine_inform.setVisibility(View.GONE);
            }
            setGuideSetupEvent(guideSetupState);
        } else if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            /** 第二种解析*/
            mUserInfoBean = mHttpService.onGetUserInfo(json);
            if (mUserInfoBean == null) return;
            if ("1".equals(mUserInfoBean.isAllowRecharge)) {
                isAllowRecharge.setVisibility(View.GONE);
                tvRecharge.setTextColor(ContextCompat.getColor(mContext, R.color.text_333333));
            } else {
                isAllowRecharge.setVisibility(View.VISIBLE);
                tvRecharge.setTextColor(ContextCompat.getColor(mContext, R.color.text_999999));
            }
            /*String message = json.optString("message");
            if (!TextUtils.isEmpty(message) && message.contains("没有登陆")) {
//            if (true) {
                int currentItem = ((MainActivity) getActivity()).mViewPager.getCurrentItem();
                if (currentItem == 2){
                    gotoActivity(LoginActivity.class);
                }
                return;
            }*/
            //老用户未开通银行存管
            FinanceApplication application = (FinanceApplication) getActivity().getApplication();
            try{
                if (DBUtils.getUser(mContext) != null && application.isLogin && !"1".equals(mUserInfoBean.isOpen) && !"1".equals(mUserInfoBean.isNewUser)) {
                    OpenBankDialog dialog = new OpenBankDialog();
                    dialog.show(getActivity().getFragmentManager(), "OpenBankDialog");
                    application.isLogin = false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

//            if (!"0".equals(mUserInfoBean.accountType)) return;

            /*可用余额*/
            String cashBalance = mUserInfoBean.cashBalance;
            tvAvailableMoney.setText(TextUtils.isEmpty(cashBalance) ? "" : FormatUtils.formatDown(cashBalance));
            /*资金总额*/
            String netAsset = mUserInfoBean.netAsset;
            tvTotalMoney.setText(TextUtils.isEmpty(netAsset) ? "" : FormatUtils.formatDown(netAsset));
            /*累计收益*/
            String realMoney = mUserInfoBean.realMoney;
            tvTotalIncome.setText(TextUtils.isEmpty(realMoney) ? "" : FormatUtils.formatDown(realMoney));

            tvUserName.setText("你好!" + mUserInfoBean.userName);

            String tradeFlowRecordInfo = "" + mUserInfoBean.returnedCount;
            String text = "近七日有" + tradeFlowRecordInfo + "笔回款";
            tvReturnMoneyCount.setText(text);

            tvReturnMoneyPrincipal.setText(mUserInfoBean.capitalAmountSum);//待回款本金
            tvReturnMoneyIncome.setText(mUserInfoBean.profitAmountSum);//待回款利息

            if (TextUtils.isEmpty(mUserInfoBean.level)) {
                level.setVisibility(View.GONE);
            } else {
                level.setVisibility(View.VISIBLE);
                level.setText(mUserInfoBean.level);//等级
            }
            myDescribe.setText(TextUtils.isEmpty(mUserInfoBean.signature) ? "未设置签名" : mUserInfoBean.signature);//个人签名
            tvOpenBankAccount.setText("1".equals(mUserInfoBean.isAutoTender) ? "已开启" : "未开启");

            String isTender = mUserInfoBean.isTender;
            content_kill_percent.setVisibility("1".equals(isTender) ? View.VISIBLE : View.GONE);

            SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(getActivity());

            //打败人数百分比
            killPercent = mUserInfoBean.number;
            if (!TextUtils.isEmpty(killPercent)) {
                tvKillPercent.setText(killPercent);
            }

            if (user != null) {
//                mHttpService.getFundOverInfo("" + user.getUserId(),accountType);
                mHttpService.onGetUserInfo(json, user);

                String mHeadImgUrl = json.optString("headImg");
                mHeadImgUrl = HttpService.mBaseUrl + mHeadImgUrl;
                String userHeadUrl = sp.getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
                if (TextUtils.isEmpty(userHeadUrl) || !mHeadImgUrl.equals(userHeadUrl)) {
                    sp.putStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId(), mHeadImgUrl);
                }

                if (TextUtils.isEmpty(user.getRealName())) {
                    //未实名
                    guideSetupState[0] = 2;
                } else {
                    guideSetupState[0] = 1;
                }
                guideSetupState[2] = user.getHasTradePassword() ? 1 : 2;

                String headUrl = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
                if (headUrl == null) {
                    userHead.setImageResource(R.drawable.user_head);
                } else {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(headUrl, userHead);
                }
                /*
                if (dao != null && AppState.instance().logined()) {
                    dao.insertOrReplace(user);
                    String name = user.getUserName();
                    userName = TextUtils.isEmpty(name) ? userName : name;
                    isShowRedPacket();
                    String headUrl = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
                    if (headUrl == null) {
                        userHead.setImageResource(R.drawable.user_head);
                    } else {
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(headUrl, userHead);
                    }
                }*/
            }

            if (user != null) {
                String cellPhone = user.getCellPhone();
                if (TextUtils.isEmpty(user.getRealName())) {
                    //未实名
                    guideSetupState[0] = 2;
                } else {
                    guideSetupState[0] = 1;
                }
                guideSetupState[2] = user.getHasTradePassword() ? 1 : 2;
            }

            setGuideSetupEvent(guideSetupState);
        } else if (reqId == ServiceCmd.CmdId.CMD_getBankCard.ordinal()) {
            BankCard card = mHttpService.onGetBankCard(getActivity(), json);
            guideSetupState[1] = card == null ? 2 : 1;
            setGuideSetupEvent(guideSetupState);
        }
//        else if (reqId == ServiceCmd.CmdId.CMD_FundOverView.ordinal()) {
//            FundOverInfo fundOverInfo = mHttpService.onGetFundOverInfo(json);
//            /*可用余额*/
//            String cashBalance = fundOverInfo.getCashBalance();
//            tvAvailableMoney.setText(TextUtils.isEmpty(cashBalance) ? "" : FormatUtils.formatDown(cashBalance));
//            /*资金总额*/
//            String netAsset = fundOverInfo.getNetAsset();
//            tvTotalMoney.setText(TextUtils.isEmpty(netAsset) ? "" : FormatUtils.formatDown(netAsset));
//            /*累计收益*/
//            String realMoney = fundOverInfo.realMoney;
//            tvTotalIncome.setText(TextUtils.isEmpty(realMoney) ? "" : FormatUtils.formatDown(realMoney));
//        }
    }

    private void setGuideSetupEvent(final int[] guideSetupState) {
        // 2.6 return
//        if (true)   return;
        if (guideSetupState[0] == 0 || guideSetupState[1] == 0 || guideSetupState[2] == 0) return;
        if (guideSetupState[0] == 2 && guideSetupState[1] == 2 && guideSetupState[2] == 2) {
            content_setup_guide.setVisibility(View.VISIBLE);
        } else {
            content_setup_guide.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (mRefresh != null)
            mRefresh.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MineFragment");
    }

    @OnClick({R.id.clickFundOverView,
            R.id.click_return_money,
            R.id.click_my_invest,
            R.id.click_my_transfer,
            R.id.click_fund_flow,
            R.id.click_invest_summary,
            R.id.clickTravel,
            R.id.click_auto_invest_setting,
            R.id.click_withdraw,
            R.id.click_recharge,
            R.id.click_setup_guide,
            R.id.my_describe,
            R.id.userHead,
            R.id.ib_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_cancel:
                content_setup_guide.setVisibility(View.GONE);
                break;
            case R.id.userHead:
                gotoActivity(PersonalCardActivity.class);
                break;
            case R.id.my_describe:
                gotoActivity(MyDescribeAcitvity.class);
                break;
            case R.id.click_setup_guide:
                Intent intent = new Intent(getActivity(), SetupGuideActivity.class);
                intent.putExtra(SetupGuideActivity.SETUP_STATE, guideSetupState);
                gotoActivity(intent);
                break;
            case R.id.click_withdraw:
                gotoActivity(WithdrawDepositActivity.class);
                break;
            case R.id.click_recharge:
                //充值入口
                boolean isAllowRecharge = SharedPreferencesHelper.getInstance(mContext).getBooleanValue(SharedPreferencesHelper.KEY_ALLOW_RECHARGE, true);
                if (isAllowRecharge) {
                    AlertDialogUtils.confirmGoRecharg(mContext);
                } else {
                    new RechargeCloseDialog().show(getActivity().getFragmentManager(), "RechargeCloseDialog");
                }
                break;
            case R.id.clickFundOverView://资产总览
                if (mUserInfoBean != null) {
                    Intent intent1 = new Intent(mContext, FundOverViewActivity.class);
                    intent1.putExtra("cashBalance", TextUtils.isEmpty(mUserInfoBean.cashBalance) ? "" : mUserInfoBean.cashBalance);
                    intent1.putExtra("inCount", TextUtils.isEmpty(mUserInfoBean.inCount) ? "" : mUserInfoBean.inCount);
                    intent1.putExtra("frozenAmtN", TextUtils.isEmpty(mUserInfoBean.frozenAmtN) ? "" : mUserInfoBean.frozenAmtN);
                    intent1.putExtra("netAsset", TextUtils.isEmpty(mUserInfoBean.netAsset) ? "" : mUserInfoBean.netAsset);
                    gotoActivity(intent1);
                }
                break;
            case R.id.click_return_money://回款查询
                SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(FinanceApplication.getAppContext());
                String state = sp.getStringValue(SharedPreferencesHelper.STATE_RETURN_CALENDER_OR_LIST);
                if ("2".equals(state)) {
                    gotoActivity(new Intent(mContext, QueryReturnMoneyListActivity.class).putExtra(Constant.AccountType, accountType));
                } else {
                    gotoActivity(new Intent(mContext, ReturnMoneyCalendarActivity.class).putExtra(Constant.AccountType, accountType));
                }
                break;
            case R.id.click_my_invest:
                gotoActivity(new Intent(mContext, FundRecordsActivity.class).putExtra(Constant.AccountType, accountType));
                break;
            case R.id.click_my_transfer://我要转让
                gotoActivity(new Intent(mContext, TransferProductListActivity.class).putExtra(Constant.AccountType, accountType));
                break;
            case R.id.click_fund_flow:
                gotoActivity(new Intent(mContext, FundFlowActivity.class).putExtra(Constant.AccountType, accountType));
                break;
            case R.id.click_invest_summary:
                gotoActivity(new Intent(mContext, InvestSummaryActivity.class).putExtra(Constant.AccountType, accountType));
                break;

            case R.id.clickTravel:
                Long id = user.getUserId();
                gotoWeb("/h5/xingya/appitem?uid=" + id, "玩赚旅游");
                break;
            case R.id.click_auto_invest_setting:

                if (mUserInfoBean != null) {
                    String cashBalance = mUserInfoBean.cashBalance;
                    String isAutoTender = mUserInfoBean.isAutoTender;
                    int accountType = Constant.AccountLianLain;
                    String isBindBank = ((FinanceApplication) getActivity().getApplication()).isBindBank;

                    if (mUserInfoBean != null && mUserInfoBean.autoTenderProtocol != 1){
                        AutoInvestProtocolActivity.goThis(getContext(),accountType,cashBalance, isAutoTender,isBindBank);
                        return;
                    }

                    Intent autoInvestIntent = new Intent(getContext(), AutoInvestSettingActivity.class);
                    autoInvestIntent.putExtra(Constant.AccountType, Constant.AccountLianLain);
                    autoInvestIntent.putExtra(AutoInvestSettingActivity.ACCOUNT_BALANCE, cashBalance);
                    autoInvestIntent.putExtra(AutoInvestSettingActivity.IS_OPEN_AUTO_INVEST, isAutoTender);
                    autoInvestIntent.putExtra(AutoInvestSettingActivity.IS_OPEN_BANK_ACCOUNT,isBindBank);
                    gotoActivity(autoInvestIntent);
                }
                break;

        }
    }

//    public void onEventMainThread(RefreshMineData event) {
//        if (event != null && isAdded()) {
//            loadDate();
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
//        EventBus.getDefault().unregister(this);
    }
}
