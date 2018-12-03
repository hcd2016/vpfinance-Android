package cn.vpfinance.vpjr.module.user.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.QueryAutoStatusBean;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.model.RefreshTab;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialogFragment;
import cn.vpfinance.vpjr.module.gusturelock.LockActivity;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.setting.AutoInvestProtocolActivity;
import cn.vpfinance.vpjr.module.setting.AutoInvestSettingActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.module.trade.RechargBankActivity;
import cn.vpfinance.vpjr.module.trade.WithdrawBankActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.module.user.asset.FundFlowActivity;
import cn.vpfinance.vpjr.module.user.asset.FundOverViewActivity;
import cn.vpfinance.vpjr.module.user.asset.FundRecordsActivity;
import cn.vpfinance.vpjr.module.user.asset.InvestSummaryActivity;
import cn.vpfinance.vpjr.module.user.asset.QueryReturnMoneyListActivity;
import cn.vpfinance.vpjr.module.user.asset.ReturnMoneyCalendarActivity;
import cn.vpfinance.vpjr.module.user.asset.TransferProductListActivity;
import cn.vpfinance.vpjr.module.user.personal.CouponActivity;
import cn.vpfinance.vpjr.module.user.personal.TrialCoinActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import de.greenrobot.event.EventBus;

/**
 * 银行存管账户
 * Created by zzlz13 on 2017/7/28.
 */

public class BankAccountFragment extends BaseFragment {

    private static boolean isShowing = false;
    @Bind(R.id.header_no_open)
    public LinearLayout mHeaderNoOpen;
    //    @Bind(R.id.header)
//    public RelativeLayout mHeader;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    //    @Bind(R.id.tvUserName)
//    TextView tvUserName;
    @Bind(R.id.tvReturnMoneyCount)
    TextView tvReturnMoneyCount;
    @Bind(R.id.tvReturnMoneyPrincipal)
    TextView tvReturnMoneyPrincipal;
    //    @Bind(R.id.tvReturnMoneyIncome)
//    TextView tvReturnMoneyIncome;
//    @Bind(R.id.myDescribe)
//    TextView myDescribe;
//    @Bind(R.id.userHead)
//    CircleImg userHead;
    @Bind(R.id.tvTotalMoney)
    TextView tvTotalMoney;
    @Bind(R.id.tvAvailableMoney)
    TextView tvAvailableMoney;
    @Bind(R.id.tvTotalIncome)
    TextView tvTotalIncome;
    @Bind(R.id.tv_open_bank_account)
    TextView tvOpenBankAccount;
    @Bind(R.id.open_content)
    LinearLayout mOpenContent;
    @Bind(R.id.click_borrow_menu)
    LinearLayout click_borrow_menu;
    //    @Bind(R.id.ivBandActive)
//    ImageView ivBandActive;
    @Bind(R.id.canUseNum)
    TextView canUseNum;
    @Bind(R.id.img_dot)
    ImageView imgDot;
    //    @Bind(R.id.ivUpdateHxTag)
//    ImageView ivUpdateHxTag;
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.noOpenHidden)
    LinearLayout noOpenHidden;
    //    @Bind(R.id.click_account_e)
//    LinearLayout mCLickAccountE;
    @Bind(R.id.click_open_bank_account)
    Button click_open_bank_account;
    @Bind(R.id.tvNoOpenHint)
    TextView tvNoOpenHint;
    @Bind(R.id.tvNoOpenHint2)
    TextView tvNoOpenHint2;
    @Bind(R.id.tvOpenGuide)
    TextView tvOpenGuide;
    @Bind(R.id.ll_assets_container)
    LinearLayout llAssetsContainer;
    @Bind(R.id.tv_no_data_header)
    TextView tvNoDataHeader;

    private User user;
    private HttpService mHttpService;
    private UserInfoBean mUserInfoBean;
    private int accountType = 1;
    private String realName;
    private boolean isOpen;
    private boolean isBindBank;

//    private int autoInvestStatus = 0; // 0忽略 2超额 3过期


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_bank_account_new, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mHttpService = new HttpService(getActivity(), this);
        tvOpenGuide.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mRefresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.red_text2));
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDate();
            }
        });
        mHeaderNoOpen.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.account_bank_header));

        titleBar.reset()
                .setTitle("我的账户");
//                .setImageButtonRight(R.mipmap.icon_set, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
//                    }
//                })
//
//        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(mContext);
//        isOpen = sharedPreferencesHelper.getBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT, false);
//        isBindBank = sharedPreferencesHelper.getBooleanValue(SharedPreferencesHelper.KEY_IS_BIND_BANK, false);
//        if (!isOpen) {
//            mHeaderNoOpen.setVisibility(View.VISIBLE);
//            mOpenContent.setVisibility(View.GONE);
//            noOpenHidden.setVisibility(View.GONE);
//            click_open_bank_account.setText("开通存管账户");
//            tvNoOpenHint.setText("为了保障您的资金安全 需先开通存管账户");
//            tvNoOpenHint2.setVisibility(View.VISIBLE);
//            tvNoOpenHint2.setText("开通后10分钟左右可以查看开通结果 请勿重复操作");
//
//        } else if (!isBindBank) {
//            mHeaderNoOpen.setVisibility(View.VISIBLE);
//            mOpenContent.setVisibility(View.GONE);
//            noOpenHidden.setVisibility(View.GONE);
//            click_open_bank_account.setText("绑卡激活");
//            tvNoOpenHint.setText("开通了银行存管的用户 需绑定银行卡激活账户");
//            tvNoOpenHint2.setVisibility(View.GONE);
//        } else {
//            mHeaderNoOpen.setVisibility(View.GONE);
//            mOpenContent.setVisibility(View.VISIBLE);
//            noOpenHidden.setVisibility(View.VISIBLE);
//        }
    }


    public void onEventMainThread(RefreshTab event) {
        if (event != null && isAdded() && event.tabType == RefreshTab.TAB_MINE) {//切换我要投资TAB时
            if (mHttpService != null) {
                loadDate();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        user = DBUtils.getUser(mContext);
        if (user != null) {
            realName = user.getRealName();
//            boolean booleanValue = SharedPreferencesHelper.getInstance(getActivity()).getBooleanValue("firstInto_" + user.getUserId(), true);
//            if (booleanValue) {
//                ivUpdateHxTag.setVisibility(View.VISIBLE);
//            } else {
//                ivUpdateHxTag.setVisibility(View.GONE);
//            }
        }
        if (((MainActivity) getActivity()).mLastRadioId == R.id.maintab_mine_radiobtn) {
            loadDate();
        }
//        loadDate();
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
    boolean isRequestSucess = false;

    @Override
    public void onHttpSuccess(int reqId, final JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal() && ((MainActivity) getActivity()).mLastRadioId == R.id.maintab_mine_radiobtn) {
            isRequestSucess = true;
            if (isForceLogout(getActivity(), json)) return;
        }
        mRefresh.setRefreshing(false);
        if (reqId == ServiceCmd.CmdId.CMD_liteMoneyInfo.ordinal()) {
            String msg = json.optString("msg");
            if ("1".equals(msg)) {
                JSONObject lite_money = json.optJSONObject("lite_money");
                if (lite_money != null) {
                    int status = lite_money.optInt("status", 1);
                    if ("0".equals(status + "") || "3".equals(status + "")) {
                        imgDot.setVisibility(View.VISIBLE);
                    } else {
                        imgDot.setVisibility(View.GONE);
                    }
                }
            } else {
                imgDot.setVisibility(View.GONE);
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            isRequestSucess = true;
            /** 第二种解析*/
            mUserInfoBean = mHttpService.onGetUserInfo(json);
            initUser();
        } else if (reqId == ServiceCmd.CmdId.CMD_QUERY_AUTO_PLANK_STATUS.ordinal()) {
            QueryAutoStatusBean autoStatusBean = new Gson().fromJson(json.toString(), QueryAutoStatusBean.class);
            if (autoStatusBean != null && !TextUtils.isEmpty(autoStatusBean.autoPlankStatus)) {
                if ("2".equals(autoStatusBean.autoPlankStatus)) {//超额
                    tvOpenBankAccount.setText("已超额");
                } else if ("3".equals(autoStatusBean.autoPlankStatus)) {//过期
                    tvOpenBankAccount.setText("已过期");
                }
            }
        }
//        else if (reqId == ServiceCmd.CmdId.CMD_HX_IS_UPDATE.ordinal()) {
//            mCLickAccountE.setVisibility(View.GONE);
//            if (user != null) { 去除hx弹窗
//                mHttpService.getCreateAccountTime(user.getUserId().toString());
//            }
//        }
//        else if (reqId == ServiceCmd.CmdId.CMD_CREATE_ACCOUNT_TIME.ordinal()) {
//            String status = json.optString("status");
//            if ("true".equalsIgnoreCase(status)) {
//                boolean isShow = SharedPreferencesHelper.getInstance(getContext()).getBooleanValue(SharedPreferencesHelper.KEY_HX_UPDATE_DIALOG_SHOW, false);
//                if (!isShow) {
//                    new HxUpdateDialog().show(getChildFragmentManager(), "HxUpdateDialog");
//                    SharedPreferencesHelper.getInstance(getContext()).putBooleanValue(SharedPreferencesHelper.KEY_HX_UPDATE_DIALOG_SHOW, true);
//                }
//            }
//        }
    }



    private String killPercent;
    private String voucher_count;
    private String addrate_count;
    private String presell_count;

    private void initUser() {
        if (mUserInfoBean == null) return;

        /*if ("0".equals(mUserInfoBean.isNewUser)) {//老用户
            titleBar.setActionLeft("查看连连账户", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).switchToTab(4);
                }
            });
        } else {
            titleBar.setActionLeftGone();
        }*/

//        ivBandActive.setVisibility("1".equals(mUserInfoBean.isBindHxBank) ? View.GONE : View.VISIBLE);
//        if (!TextUtils.isEmpty(mUserInfoBean.customerType) && "2".equals(mUserInfoBean.customerType)) {
//            ivBandActive.setVisibility(View.GONE);
//        }
        click_borrow_menu.setVisibility((!TextUtils.isEmpty(mUserInfoBean.isShowBorrowMenu) && "1".equals(mUserInfoBean.isShowBorrowMenu)) ? View.VISIBLE : View.GONE);
        /*可用余额*/
        String cashBalance = mUserInfoBean.cashBalance;
        tvAvailableMoney.setText(TextUtils.isEmpty(cashBalance) ? "0.00" : FormatUtils.formatDown(cashBalance));
        /*资金总额*/
        String netAsset = mUserInfoBean.netAsset;
        tvTotalMoney.setText(TextUtils.isEmpty(netAsset) ? "0.00" : FormatUtils.formatDown(netAsset));
        /*累计收益*/
        String realMoney = mUserInfoBean.realMoney;
        tvTotalIncome.setText(TextUtils.isEmpty(realMoney) ? "0.00" : FormatUtils.formatDown(realMoney));

//            if (!"1".equals(mUserInfoBean.accountType)) return;

//        tvUserName.setText("你好!" + mUserInfoBean.userName);
        tvOpenBankAccount.setText("1".equals(mUserInfoBean.isAutoTender) ? "已开启" : "未开启");

        isOpen = "1".equals(mUserInfoBean.isOpen) ? true : false;
//            SharedPreferencesHelper.getInstance(mContext).putBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT,isOpen);
//        mHeaderNoOpen.setVisibility(isOpen ? View.GONE : View.VISIBLE);
//        mOpenContent.setVisibility(!isOpen ? View.GONE : View.VISIBLE);
//        noOpenHidden.setVisibility(!isOpen ? View.GONE : View.VISIBLE);
        ((App) getActivity().getApplication()).isOpenHx = isOpen;

        String tradeFlowRecordInfo = "" + mUserInfoBean.returnedCount;
        String text = "近七日有" + tradeFlowRecordInfo + "笔回款";
        tvReturnMoneyCount.setText(text);

        canUseNum.setText((TextUtils.isEmpty(mUserInfoBean.canUseTotal) || "0".equals(mUserInfoBean.canUseTotal)) ? "" : mUserInfoBean.canUseTotal + "张券可使用");

        tvReturnMoneyPrincipal.setText(mUserInfoBean.capitalAmountSum);//待回款本金
//        tvReturnMoneyIncome.setText(mUserInfoBean.profitAmountSum);//待回款利息

//        myDescribe.setText(TextUtils.isEmpty(mUserInfoBean.signature) ? "未设置签名" : mUserInfoBean.signature);//个人签名

        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(getActivity());

        realName = mUserInfoBean.realName;

        voucher_count = TextUtils.isEmpty(mUserInfoBean.canUseVoucher) ? "" : mUserInfoBean.canUseVoucher;
        addrate_count = TextUtils.isEmpty(mUserInfoBean.canUseCoupon) ? "" : mUserInfoBean.canUseCoupon;
        presell_count = TextUtils.isEmpty(mUserInfoBean.canUseBookCoupon) ? "" : mUserInfoBean.canUseBookCoupon;

        //打败人数百分比
        killPercent = mUserInfoBean.number;

        if (user != null) {
//                mHttpService.getFundOverInfo("" + user.getUserId(),accountType);

            String mHeadImgUrl = mUserInfoBean.headImg;
            mHeadImgUrl = HttpService.mBaseUrl + mHeadImgUrl;
            String userHeadUrl = sp.getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
            if (TextUtils.isEmpty(userHeadUrl) || !mHeadImgUrl.equals(userHeadUrl)) {
                sp.putStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId(), mHeadImgUrl);
            }
            String headUrl = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
//            if (headUrl == null) {
//                userHead.setImageResource(R.drawable.user_head);
//            } else {
//                ImageLoader imageLoader = ImageLoader.getInstance();
//                imageLoader.displayImage(headUrl, userHead);
//            }
            //查询是否自动投标 超额或过期
            mHttpService.getQueryAutoPlankStatus(user.getUserId().toString());
        }
        if (!TextUtils.isEmpty(mUserInfoBean.isBindHxBank)) {
            ((App) getActivity().getApplication()).isBindBank = mUserInfoBean.isBindHxBank;
        }

        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(mContext);
        isOpen = sharedPreferencesHelper.getBooleanValue(SharedPreferencesHelper.KEY_IS_OPEN_BANK_ACCOUNT, false);
        isBindBank = sharedPreferencesHelper.getBooleanValue(SharedPreferencesHelper.KEY_IS_BIND_BANK, false);
        if (!isOpen) {
//            mHeader.setVisibility(View.GONE);
            titleBar.setColor(getResources().getColor(R.color.account_bank_header));
            mHeaderNoOpen.setVisibility(View.VISIBLE);
            mOpenContent.setVisibility(View.GONE);
            noOpenHidden.setVisibility(View.GONE);
            click_open_bank_account.setText("开通存管账户");
            tvNoOpenHint.setText("为了保障您的资金安全 需先开通存管账户");
            tvNoOpenHint2.setVisibility(View.VISIBLE);
            tvNoOpenHint2.setText("开通后10分钟左右可以查看开通结果 请勿重复操作");
            mRefresh.setColorSchemeColors(Utils.getColor(R.color.account_bank_header));
        } else if (!isBindBank) {
            titleBar.setColor(getResources().getColor(R.color.account_bank_header));
//            mHeader.setVisibility(View.GONE);
            mHeaderNoOpen.setVisibility(View.VISIBLE);
            mOpenContent.setVisibility(View.GONE);
            noOpenHidden.setVisibility(View.GONE);
            click_open_bank_account.setText("绑卡激活");
            tvNoOpenHint.setText("开通了银行存管的用户 需绑定银行卡激活账户");
            tvNoOpenHint2.setVisibility(View.GONE);
            mRefresh.setColorSchemeColors(Utils.getColor(R.color.account_bank_header));
        } else {
            titleBar.setColor(getResources().getColor(R.color.red_text2));
//            mHeader.setVisibility(View.VISIBLE);
            mHeaderNoOpen.setVisibility(View.GONE);
            mOpenContent.setVisibility(View.VISIBLE);
            noOpenHidden.setVisibility(View.VISIBLE);
            mRefresh.setColorSchemeColors(Utils.getColor(R.color.red_text2));
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (mRefresh != null)
            mRefresh.setRefreshing(false);
        if(reqId ==  ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            if(!isRequestSucess) {
                tvNoDataHeader.setVisibility(View.VISIBLE);
                llAssetsContainer.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.withdraw, R.id.recharge, R.id.click_auto_invest_setting, R.id.click_open_bank_account, R.id.clickFundRecord, R.id.click_return_money_header, R.id.click_my_transfer, R.id.click_fund_flow, R.id.click_invest_summary,
            R.id.click_borrow_menu,
            R.id.clickTrialCoin,
            R.id.clickVoucher,
            R.id.ll_assets_container,
            R.id.tvOpenGuide,
    })
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tvOpenGuide:
                boolean isPersonType = SharedPreferencesHelper.getInstance(getActivity()).getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE, true);
                if (!isOpen) {
                    //图文指引
                    if (isPersonType) {
                        gotoWeb("https://www.vpfinance.cn/h5/help/hxGuideOpen", null);
                    } else {
                        gotoWeb("https://www.vpfinance.cn/h5/help/hxGuideOpen?company=1", null);
                    }
                } else if (!isBindBank) {
                    if (isPersonType) {
                        gotoWeb("https://www.vpfinance.cn/h5/help/hxGuideBind?bind1=1", null);
                    } else {
                        gotoWeb("https://www.vpfinance.cn/h5/help/hxGuideBind?bind2=1", null);
                    }
                }
                break;
//            case R.id.clickInviteGift:
//                InviteGiftActivity.goThis(mContext);
//                break;
//            case R.id.my_medal:
//                MyMedalActivity.goThis(mContext, Constant.AccountBank);
//                break;
//            case R.id.click_risk_evaluating:
//
//                if (user != null) {
//                    gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
//                }
//                break;

//            case R.id.invest_top:
//                InvestTopActivity.goThis(mContext, Constant.AccountBank, killPercent);
//                break;
//            case R.id.ivBandActive:
//                user = DBUtils.getUser(mContext);
//                if (user != null) {
//                    Long userId = user.getUserId();
//                    //跳转到存管绑卡第三方界面
//                    BindBankHintActivity.goThis(mContext, userId.toString());
//                } else {
//                    gotoActivity(LoginActivity.class);
//                }
//                break;
            case R.id.click_borrow_menu:
                String url = "h5/user/toBorrow?uid=" + DBUtils.getUser(getActivity()).getUserId() + "&accountType=1";
                gotoWeb(url, "");
                break;
            case R.id.withdraw:
                Intent intent = new Intent(getActivity(), WithdrawBankActivity.class);
                if (mUserInfoBean != null) {
                    String cashBalance1 = mUserInfoBean.cashBalance;
                    String frozenAmtN = mUserInfoBean.frozenAmtN;
                    intent.putExtra(WithdrawBankActivity.CASHBALANCE, cashBalance1);
                    intent.putExtra(WithdrawBankActivity.FROZENAMTN, frozenAmtN);
                    startActivity(intent);
                }
                break;
            case R.id.recharge:
                gotoActivity(RechargBankActivity.class);
                break;
            case R.id.click_auto_invest_setting://自动投标
                if (mUserInfoBean != null) {
                    String cashBalance = mUserInfoBean.cashBalance;
                    String isAutoTender = mUserInfoBean.isAutoTender;
                    int accountType = Constant.AccountLianLain;
                    String isBindBank = ((App) getActivity().getApplication()).isBindBank;

                    if (mUserInfoBean != null && mUserInfoBean.autoTenderProtocol != 1) {
                        AutoInvestProtocolActivity.goThis(getContext(), accountType, cashBalance, isAutoTender, isBindBank);
                        return;
                    }

                    Intent autoInvestIntent = new Intent(getContext(), AutoInvestSettingActivity.class);
                    autoInvestIntent.putExtra(Constant.AccountType, accountType);
                    autoInvestIntent.putExtra(AutoInvestSettingActivity.ACCOUNT_BALANCE, cashBalance);
                    autoInvestIntent.putExtra(AutoInvestSettingActivity.IS_OPEN_AUTO_INVEST, isAutoTender);
                    gotoActivity(autoInvestIntent);
                }
                break;
            case R.id.clickTrialCoin:
                TrialCoinActivity.goThis(mContext, Constant.AccountBank);
                break;
            case R.id.clickVoucher:
//                TicketActivity.goThis(mContext, Constant.AccountBank, voucher_count, addrate_count, presell_count);
                CouponActivity.goThis(mContext);
                break;
//            case R.id.click_account_e:
//                gotoActivity(AccountEActivity.class);
//                break;
            case R.id.click_open_bank_account:
                user = DBUtils.getUser(mContext);
                if (user != null) {
                    Long userId = user.getUserId();
                    if (!isOpen) {
                        if (mUserInfoBean != null) {
                            boolean isRealName = !TextUtils.isEmpty(mUserInfoBean.realName);
                            if (!isRealName) {
                                Utils.Toast("开通存管账户前请先进行实名认证");
                                RealnameAuthActivity.goThis(getContext());
                            } else {
                                gotoWeb("/hx/account/create?userId=" + userId, "");
                            }
                        }
                    } else if (!isBindBank) {
                        //跳转到存管绑卡第三方界面
                        BindBankHintActivity.goThis(getContext(), userId.toString());
                    }
                } else {
                    gotoActivity(LoginActivity.class);
                }
                break;
//            case R.id.click_return_money_content://回款查询
            case R.id.click_return_money_header://回款日历/回款查询
                SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(App.getAppContext());
                String state = sp.getStringValue(SharedPreferencesHelper.STATE_RETURN_CALENDER_OR_LIST);
                if ("2".equals(state)) {
                    goAction(QueryReturnMoneyListActivity.class);
                } else {
                    goAction(ReturnMoneyCalendarActivity.class);
                }
                break;
            case R.id.clickFundRecord://我的投资
                goAction(FundRecordsActivity.class);
                break;
            case R.id.ll_assets_container://资产总览
                if (mUserInfoBean != null) {
                    Intent intent1 = new Intent(mContext, FundOverViewActivity.class);
                    intent1.putExtra("cashBalance", TextUtils.isEmpty(mUserInfoBean.cashBalance) ? "" : mUserInfoBean.cashBalance);
                    intent1.putExtra("inCount", TextUtils.isEmpty(mUserInfoBean.inCount) ? "" : mUserInfoBean.inCount);
                    intent1.putExtra("frozenAmtN", TextUtils.isEmpty(mUserInfoBean.frozenAmtN) ? "" : mUserInfoBean.frozenAmtN);
                    intent1.putExtra("netAsset", TextUtils.isEmpty(mUserInfoBean.netAsset) ? "" : mUserInfoBean.netAsset);
                    intent1.putExtra("rechargingMoney", TextUtils.isEmpty(mUserInfoBean.rechargingMoney) ? "" : mUserInfoBean.rechargingMoney);
                    intent1.putExtra(Constant.AccountType, accountType);
                    gotoActivity(intent1);
                }
                break;
            case R.id.click_my_transfer://我要转让
                goAction(TransferProductListActivity.class);
                break;
            case R.id.click_fund_flow:
                goAction(FundFlowActivity.class);
                break;
            case R.id.click_invest_summary:
                goAction(InvestSummaryActivity.class);
                break;
        }
    }

    /**
     * 是否强制退出
     *
     * @param context
     * @param json
     * @return true 强制退出
     */
    public static boolean isForceLogout(final Activity context, JSONObject json) {
        //success false: 没有登录或者登录被占线
        boolean success = json.optBoolean("success", true);
        if (!success) {
            int loginStatus = json.optInt("loginStatus", -1);
            if (loginStatus == 0) {
                Utils.Toast("登录已过期,请重新登录");
                final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(context);
                String patternString = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING, null);
                String saved_uid = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_ID);
                String saved_logPwd = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD);
                if (patternString == null || TextUtils.isEmpty(saved_uid) || TextUtils.isEmpty(saved_logPwd)) {//单独处理,只要用户名,密码或手势密码为空,都需重新登录
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivityForResult(intent, 10086);
                } else {
                    Intent intent = new Intent(context, LockActivity.class);
//                    intent.putExtra(LockActivity.NAME_AUTO_LOGIN, true);
                    context.startActivity(intent);
                }
            } else if (loginStatus == 2) {
                if (!isShowing) {
                    isShowing = true;
                    String message = json.optString("message");
//                    new AlertDialog.Builder(context)
//                            .setMessage(message)
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    AppState.instance().logout();
//                                    Intent intent = new Intent(context, MainActivity.class);
//                                    intent.putExtra(MainActivity.SWITCH_TAB_NUM, 0);
//                                    context.startActivity(intent);
//                                    isShowing = false;
//                                }
//                            }).create().show();
                    new CommonTipsDialogFragment.Buidler()
                            .setContent(message)
                            .setBtnRight("确定")
                            .setOnRightClickListener(new CommonTipsDialogFragment.OnRightClickListner() {
                                @Override
                                public void rightClick() {
                                    AppState.instance().logout();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra(MainActivity.SWITCH_TAB_NUM, 0);
                                    context.startActivity(intent);
                                    isShowing = false;
                                }
                            })
                            .createAndShow((FragmentActivity) context);
                }
            }
        }
        return !success;
    }

    private void goAction(Class clazz) {
        gotoActivity(new Intent(mContext, clazz).putExtra(Constant.AccountType, accountType));
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
