package cn.vpfinance.vpjr.module.user.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.dialog.RechargeCloseDialog;
import cn.vpfinance.vpjr.module.user.PersonalCardActivity;
import cn.vpfinance.vpjr.module.user.asset.FundFlowActivity;
import cn.vpfinance.vpjr.module.user.asset.FundOverViewActivity;
import cn.vpfinance.vpjr.module.user.asset.FundRecordsActivity;
import cn.vpfinance.vpjr.module.user.asset.InvestSummaryActivity;
import cn.vpfinance.vpjr.module.user.personal.InvestTopActivity;
import cn.vpfinance.vpjr.module.user.personal.InviteGiftActivity;
import cn.vpfinance.vpjr.module.setting.MyDescribeAcitvity;
import cn.vpfinance.vpjr.module.user.personal.MyMedalActivity;
import cn.vpfinance.vpjr.module.setting.PersonalInfoActivity;
import cn.vpfinance.vpjr.module.user.asset.QueryReturnMoneyListActivity;
import cn.vpfinance.vpjr.module.trade.RechargeActivity;
import cn.vpfinance.vpjr.module.common.RegisterActivity;
import cn.vpfinance.vpjr.module.user.asset.ReturnMoneyCalendarActivity;
import cn.vpfinance.vpjr.module.welcome.SetupGuideActivity;
import cn.vpfinance.vpjr.module.user.personal.TicketActivity;
import cn.vpfinance.vpjr.module.user.asset.TransferProductListActivity;
import cn.vpfinance.vpjr.module.user.personal.TrialCoinActivity;
import cn.vpfinance.vpjr.module.trade.WithdrawDepositActivity;
import cn.vpfinance.vpjr.module.dialog.RegistDialog;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.FundOverInfo;
import cn.vpfinance.vpjr.model.RefreshTab;
import cn.vpfinance.vpjr.model.UserHeadEvent;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.util.StatusBarCompat1;
import cn.vpfinance.vpjr.view.ActionBarPullDownLayout;
import cn.vpfinance.vpjr.view.CircleImg;
import cn.vpfinance.vpjr.view.pulltozoomview.PullToZoomScrollViewEx;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/10/22.
 */
public class MineFragmentNotVPFragment2 extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.scroll_view)
    PullToZoomScrollViewEx mScrollView;
    @Bind(R.id.fake_title_bar)
    RelativeLayout fake_title_bar;

    private View         view;
    private Activity     mHostActivity;
    private LinearLayout clickFundRecord;
    private LinearLayout myMedal;
    private LinearLayout clickTransfer;
    private LinearLayout clickRecharge;
    private LinearLayout clickInvestSummary;
    private LinearLayout clickTrialCoin;
    private LinearLayout clickVoucher;
    private User         user;
    private UserDao dao = null;

    private HttpService mHttpService = null;
    private TextView waitReturnMoney;
    private LinearLayout clickInviteGift;
    private TextView tvReturnMoneyCount;
    private TextView tvReturnMoneyCount2;

    private String             userName;
    private FundOverInfo       fundOverInfo;
    private ImageView          img_dot;
    private TranslateAnimation translateAnimation;
    private TextView           tvTotalMoney;
    private TextView           tvAvailableMoney;
    private TextView           tvTotalIncome;
    private TextView           tvUserName;
    private TextView           canUseNum;
    private TextView           tv_top;
    private ImageView          iv_top;
    private TextView           level;
    private TextView           my_describe;
    private TextView           tvTopInfo;
    private RelativeLayout     rlTopInfo;
    private String             title;
    private LinearLayout           clickTravel;
    private int[] guideSetupState = new int[]{0, 0, 0}; //0 默认 1完成 2 未完成
    private LinearLayout llSetupGuide;
    private CircleImg    mUserHead;
    private String       mHeadImgUrl;
    private TextView     mMy_describe;
    private LinearLayout mInvest_top;
    private Context      mContext;
    private ImageView    ivZoom;
    private String       mNumber;
    private UserInfoBean mUserInfoBean;
    private String voucher_count ;
    private String addrate_count ;
    private String presell_count ;
    private ActionBarPullDownLayout mTitleBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        getUser();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MineFragment");
    }

    private void isShowRedPacket() {
        FinanceApplication application = (FinanceApplication) getActivity().getApplication();
        RegisterActivity.OpenRedPacket openRedPacket = application.openRedPacket;
        if (openRedPacket != null) {
            String resPhone = openRedPacket.resPhone;
            long resTime = openRedPacket.resTime;
            String cellPhone = user.getCellPhone();
            long currentTime = new Date().getTime();
            if (!TextUtils.isEmpty(resPhone) && resTime != 0) {
                if (resPhone.equals(cellPhone) && (currentTime - resTime) < 3600 * 1000) {
                    try {
                        Bundle bundle = new Bundle();
                        RegistDialog registDialog = RegistDialog.newInstance(bundle);
                        registDialog.show(getFragmentManager(), "showRedPacket");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        application.openRedPacket = null;
    }

    public void getUser() {
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        if (dao == null) {
            dbHelper = new DaoMaster.DevOpenHelper(getActivity(), Config.DB_NAME, null);
            db = dbHelper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            dao = daoSession.getUserDao();
        }

        if (dao != null && AppState.instance().logined()) {
            QueryBuilder<User> qb = dao.queryBuilder();
            List<User> userList = qb.list();
            if (userList != null && userList.size() > 0) {
                user = userList.get(0);
                String name = user.getUserName();
                if (!TextUtils.isEmpty(name)) {
                    userName = name;
                }
            }
        }
    }

    public void refresh() {
        if (mHttpService == null) {
            mHttpService = new HttpService(getActivity(), this);
        }

        if (AppState.instance().logined()) {

            mHttpService.getMessageNotice();
            mHttpService.getUserInfo();
            if (user != null)
                mHttpService.getFundOverInfo("" + user.getUserId(),Constant.AccountLianLain);
//            mHttpService.getVoucherlist("" + 1, "");
            mHttpService.liteMoneyInfo(Constant.AccountLianLain);

            String userNo = AppState.instance().getLoginUserInfo().userNo;
            String sesnId = AppState.instance().getSessionCode();

            mHttpService.getBankCard(sesnId);

            String minMoeny = "";
            String maxMoney = "";
            String minTime = "";
            String maxTime = "";
            String pageNum = "";
//            mHttpService.getQueryPage(minMoeny, maxMoney, minTime, maxTime, pageNum);
//            mHttpService.getTradeFlowRecord(user.getUserId() + "", "" + 1, 10 + "", null);

        }
    }

    private void setGuideSetupEvent(final int[] guideSetupState) {
        // 2.6 return
//        if (true)   return;
        if (guideSetupState[0] == 0 || guideSetupState[1] == 0 || guideSetupState[2] == 0) return;
        if (guideSetupState[0] == 2) {
            llSetupGuide.setVisibility(View.VISIBLE);
            llSetupGuide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SetupGuideActivity.class);
                    intent.putExtra(SetupGuideActivity.SETUP_STATE, guideSetupState);
                    gotoActivity(intent);
                }
            });
        } else {
            llSetupGuide.setVisibility(View.GONE);
        }
    }


    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
    }

    @Override
    public void onHttpSuccess(int reqId, final JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_liteMoneyInfo.ordinal()) {
            String msg = json.optString("msg");
            if ("1".equals(msg)) {
                JSONObject lite_money = json.optJSONObject("lite_money");
                if (lite_money != null) {
                    int status = lite_money.optInt("status", 1);
                    if ("0".equals(status + "") || "3".equals(status + "")) {
                        img_dot.setVisibility(View.VISIBLE);
                    } else {
                        img_dot.setVisibility(View.GONE);
                    }
                }
            } else {
                img_dot.setVisibility(View.GONE);
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_Message_Notice.ordinal()) {
            if ("true".equals(json.optString("success"))) {
                title = json.optString("title");
                if (TextUtils.isEmpty(title))
                    return;
                String noShowInfo = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.MINE_NO_SHOW_INFO);
                if (!title.equals(noShowInfo)) {
                    tvTopInfo.setText(title);
                    final String url = json.optString("url");
                    if (!TextUtils.isEmpty(url)) {
                        tvTopInfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoWeb(url, title);
                            }
                        });
                        rlTopInfo.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                rlTopInfo.setVisibility(View.GONE);
            }
            setGuideSetupEvent(guideSetupState);
        }
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            /** 第二种解析*/
            mUserInfoBean = mHttpService.onGetUserInfo(json);
            if (mUserInfoBean == null) return;

            String tradeFlowRecordInfo = ""+mUserInfoBean.returnedCount;
            String text = "近七日有" + tradeFlowRecordInfo + "笔回款";
            tvReturnMoneyCount.setText(text);
//            Utils.setTwoTextColor(text,tradeFlowRecordInfo,getResources().getColor(R.color.red_text),tvReturnMoneyCount);

            canUseNum.setText("0".equals(mUserInfoBean.canUseTotal) ? "" : mUserInfoBean.canUseTotal + "张券可使用");
            voucher_count = TextUtils.isEmpty(mUserInfoBean.canUseVoucher) ? "" :mUserInfoBean.canUseVoucher;
            addrate_count = TextUtils.isEmpty(mUserInfoBean.canUseCoupon) ? "" : mUserInfoBean.canUseCoupon;
            presell_count = TextUtils.isEmpty(mUserInfoBean.canUseBookCoupon) ? "" : mUserInfoBean.canUseBookCoupon;

            waitReturnMoney.setText(mUserInfoBean.capitalAmountSum);//待回款本金
            tvReturnMoneyCount2.setText(mUserInfoBean.profitAmountSum);//待回款利息
            //tvReturnMoneyCount.setText("近7日有"+mUserInfoBean.returnedCount+"笔回款");
            if (TextUtils.isEmpty(mUserInfoBean.level)) {
                level.setVisibility(View.GONE);
            } else {
                level.setVisibility(View.VISIBLE);
                level.setText(mUserInfoBean.level);//等级
            }
            my_describe.setText(TextUtils.isEmpty(mUserInfoBean.signature)? "未设置签名" : mUserInfoBean.signature);//个人签名

            String isTender = mUserInfoBean.isTender;
            tv_top.setVisibility("1".equals(isTender) ? View.VISIBLE : View.GONE);
            iv_top.setVisibility("1".equals(isTender) ? View.VISIBLE : View.GONE);

            String bg_url = HttpService.mBaseUrl + mUserInfoBean.background;
            SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(getActivity());

            if (user != null){
                String userBgUrl = sp.getStringValue(SharedPreferencesHelper.USER_BACKGROUND_URL + user.getUserId());
                if (TextUtils.isEmpty(userBgUrl) || !userBgUrl.equals(bg_url)) {
                    sp.putStringValue(SharedPreferencesHelper.USER_BACKGROUND_URL + user.getUserId(), bg_url);
                }
            }
            mNumber = mUserInfoBean.number;
            if (!TextUtils.isEmpty(mNumber)) {

                String str = "总投资打败了" + mNumber + "的用户";
                Utils.setTwoTextColor(str,mNumber,Color.YELLOW,tv_top);
            }
            if (user != null){
                mHttpService.getFundOverInfo("" + user.getUserId(), Constant.AccountLianLain);
                mHttpService.onGetUserInfo(json, user);
            }

            mHeadImgUrl = json.optString("headImg");
            mHeadImgUrl = HttpService.mBaseUrl + mHeadImgUrl;
            String userHeadUrl = sp.getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
            if (TextUtils.isEmpty(userHeadUrl) || !mHeadImgUrl.equals(userHeadUrl)) {
                sp.putStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId(), mHeadImgUrl);
            }
            if (user != null) {
                if (dao != null && AppState.instance().logined()) {
                    dao.insertOrReplace(user);
                    String name = user.getUserName();
                    userName = TextUtils.isEmpty(name) ? userName : name;

                    isShowRedPacket();
                    setHeadImage();
                    setUserBg();
                }
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
        if (reqId == ServiceCmd.CmdId.CMD_FundOverView.ordinal()) {

            fundOverInfo = mHttpService.onGetFundOverInfo(json);
            /*可用余额*/
            String cashBalance = fundOverInfo.getCashBalance();
            tvAvailableMoney.setText(TextUtils.isEmpty(cashBalance) ? "" : FormatUtils.formatDown(cashBalance));
            /*资金总额*/
            String netAsset = fundOverInfo.getNetAsset();
            tvTotalMoney.setText(TextUtils.isEmpty(netAsset) ? "" : FormatUtils.formatDown(netAsset));
            /*累计收益*/
            String realMoney = fundOverInfo.realMoney;
            tvTotalIncome.setText(TextUtils.isEmpty(realMoney) ? "" : FormatUtils.formatDown(realMoney));
            if (!TextUtils.isEmpty(userName)) {
                tvUserName.setText("你好!"+userName);
            }
        }

        /*if (reqId == ServiceCmd.CmdId.CMD_TradeFlowRecord.ordinal()) {
            TradeFlowRecordInfo tradeFlowRecordInfo = mHttpService.onGetTradeFlowRecord(json);
            //if ("true".equals(tradeFlowRecordInfo.success)) {
            if (tradeFlowRecordInfo != null) {
//                waitReturnMoney.setText(tradeFlowRecordInfo.returnedSumMoney);
                int count = tradeFlowRecordInfo.dataList.size();

                if (!TextUtils.isEmpty(tradeFlowRecordInfo.returnedCount)){
                    String text = "你有" + tradeFlowRecordInfo.returnedCount + "笔待回款事项";
                    *//*int fstart = text.indexOf(tradeFlowRecordInfo.returnedCount);
                    int fend = fstart + tradeFlowRecordInfo.returnedCount.length();
                    SpannableStringBuilder style = new SpannableStringBuilder(text);
                    style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red_top)), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tvReturnMoneyCount.setText(style);*//*
//                    Utils.setTwoTextColor(text,tradeFlowRecordInfo.returnedCount,getResources().getColor(R.color.red_text),tvReturnMoneyCount);
                }

            }
            // }
        }*/

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        mHttpService = new HttpService(getActivity(), this);

        if (isAdded() && AppState.instance().logined()) {
            mHttpService.getUserInfo();
            dbHelper = new DaoMaster.DevOpenHelper(getActivity(), Config.DB_NAME, null);
            db = dbHelper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            dao = daoSession.getUserDao();

            if (dao != null) {
                QueryBuilder<User> qb = dao.queryBuilder();
                List<User> userList = qb.list();
                if (userList != null && userList.size() > 0) {
                    user = userList.get(0);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (translateAnimation != null) {
            translateAnimation.cancel();
        }
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_mine_not_vp2, null);
        ButterKnife.bind(this, view);
        mTitleBar = ((ActionBarPullDownLayout) view.findViewById(R.id.titleBar));
        initView(view);
        return view;
    }

    private void initHead(){
        View mHeadView = LayoutInflater.from(mContext).inflate(R.layout.part_mine2_header, null, false);
        mUserHead = (CircleImg) mHeadView.findViewById(R.id.userHead);
//        marqueeTextView = ((MarqueeTextView) view.findViewById(R.id.marqueeTextView));
//        marqueeTextView.setText("你跑啊，你倒是跑啊");
//        marqueeTextView.Start();
        mMy_describe = (TextView) mHeadView.findViewById(R.id.my_describe);
        mHeadView.findViewById(R.id.clickFundOverView).setOnClickListener(this);
        tvUserName = (TextView) mHeadView.findViewById(R.id.tvUserName);
        tv_top = (TextView) mHeadView.findViewById(R.id.tv_top);
        iv_top = (ImageView) mHeadView.findViewById(R.id.iv_top);
        level = (TextView) mHeadView.findViewById(R.id.level);
        my_describe = (TextView) mHeadView.findViewById(R.id.my_describe);
        mScrollView.setHeaderView(mHeadView);
    }

    private void initZoom(){
        View mZoomView = LayoutInflater.from(mContext).inflate(R.layout.part_mine2_zoom, null, false);
        ivZoom = ((ImageView) mZoomView.findViewById(R.id.iv_zoom));
        ivZoom.setOnClickListener(this);
        mScrollView.setZoomView(mZoomView);
    }

    private void initContent(){
        View mContentView = LayoutInflater.from(mContext).inflate(R.layout.part_mine2_content, null, false);

        //暂时不跳
//        translateAnimation = new TranslateAnimation(0, 0, 0, -20);
//        translateAnimation.setDuration(700);
//        translateAnimation.setRepeatCount(Animation.INFINITE);
//        translateAnimation.setRepeatMode(Animation.REVERSE);
//        jumpBall.setAnimation(translateAnimation);
//        translateAnimation.startNow();
        canUseNum = (TextView) mContentView.findViewById(R.id.canUseNum);
        tvTotalMoney = (TextView) mContentView.findViewById(R.id.tvTotalMoney);
        tvAvailableMoney = (TextView) mContentView.findViewById(R.id.tvAvailableMoney);
        tvTotalIncome = (TextView) mContentView.findViewById(R.id.tvTotalIncome);
        img_dot = (ImageView) mContentView.findViewById(R.id.img_dot);
        /*投资排行榜*/
        mInvest_top = (LinearLayout) mContentView.findViewById(R.id.invest_top);

        mContentView.findViewById(R.id.btnWithdraw).setOnClickListener(this);
        mContentView.findViewById(R.id.btnRecharge).setOnClickListener(this);

        waitReturnMoney = (TextView) mContentView.findViewById(R.id.waitReturnMoney);
        /*回款*/
        tvReturnMoneyCount = (TextView) mContentView.findViewById(R.id.tvReturnMoneyCount);
        tvReturnMoneyCount2 = (TextView) mContentView.findViewById(R.id.tvReturnMoneyCount2);

        /*我的勋章*/
        myMedal = (LinearLayout) mContentView.findViewById(R.id.my_medal);
        /*投资记录*/
        clickFundRecord = (LinearLayout) mContentView.findViewById(R.id.clickFundRecord);
        /*债权转让*/
        clickTransfer = (LinearLayout) mContentView.findViewById(R.id.clickTransfer);
        /*资金流水*/
        clickRecharge = (LinearLayout) mContentView.findViewById(R.id.clickRecharge);
        /*财务统计*/
        clickInvestSummary = (LinearLayout) mContentView.findViewById(R.id.clickInvestSummary);
        /*体验金*/
        clickTrialCoin = (LinearLayout) mContentView.findViewById(R.id.clickTrialCoin);
        /*我的代金券*/
        clickVoucher = (LinearLayout) mContentView.findViewById(R.id.clickVoucher);
        /*邀请有礼*/
        clickInviteGift = (LinearLayout) mContentView.findViewById(R.id.clickInviteGift);
        /*玩赚旅游*/
        clickTravel = (LinearLayout) mContentView.findViewById(R.id.clickTravel);

        clickFundRecord.setOnClickListener(this);
        clickTransfer.setOnClickListener(this);
        myMedal.setOnClickListener(this);
        clickRecharge.setOnClickListener(this);
        clickInvestSummary.setOnClickListener(this);
        clickTrialCoin.setOnClickListener(this);
        clickVoucher.setOnClickListener(this);
        clickInviteGift.setOnClickListener(this);
        clickTravel.setOnClickListener(this);
        mUserHead.setOnClickListener(this);
        mMy_describe.setOnClickListener(this);
        mInvest_top.setOnClickListener(this);

        mContentView.findViewById(R.id.clickRebackFund2).setOnClickListener(this);
        mContentView.findViewById(R.id.clickRebackFund3).setOnClickListener(this);
        mScrollView.setScrollContentView(mContentView);
        mScrollView.setParallax(false);
    }

    private void initView(View view) {
        view.findViewById(R.id.ibClose).setOnClickListener(this);
        tvTopInfo = ((TextView) view.findViewById(R.id.tvTopInfo));
        rlTopInfo = ((RelativeLayout) view.findViewById(R.id.rlTopInfo));
        llSetupGuide = (LinearLayout) view.findViewById(R.id.llSetupGuide);
        ((ImageButton) view.findViewById(R.id.ibCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSetupGuide.setVisibility(View.GONE);
            }
        });
        initHead();
        initZoom();
        initContent();

        mTitleBar.reset().setTitle(getString(R.string.maintab_mine)).showPullDown(false).setActionRight("设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
            }
        });
        /**/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fake_title_bar.getLayoutParams();
            lp.setMargins(0, StatusBarCompat1.getStatusBarHeight(mContext),0,0);
            fake_title_bar.setLayoutParams(lp);
            mTitleBar.setFakeStatusBar(true);
        }

        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mTitleBar.measure(w, h);
        final int height = mTitleBar.getMeasuredHeight();
        ViewHelper.setAlpha(mTitleBar, 0);
        mScrollView.setOnScrollerValueListener(new PullToZoomScrollViewEx.onScrollerValueListener() {
            @Override
            public void onScrollerValue(int left, int top, int oldLeft, int oldTop) {
//                Logger.e("top:"+top+",oldTop"+oldTop);
                if (top < height * 3){
                    ViewHelper.setAlpha(mTitleBar, (float) top / (float) (height * 3));
                }else{
                    ViewHelper.setAlpha(mTitleBar, 1);
                }
            }
        });
    }

    private void setHeadImage() {
        String headUrl = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
        if (headUrl == null) {
            mUserHead.setImageResource(R.drawable.user_head);
        } else {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(headUrl, mUserHead);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setUserBg() {
        String bgUrl = SharedPreferencesHelper.getInstance(getActivity()).getStringValue(SharedPreferencesHelper.USER_BACKGROUND_URL + user.getUserId());
        if (bgUrl == null || bgUrl.equals(HttpService.mBaseUrl)) {
            ivZoom.setImageDrawable(getResources().getDrawable(R.drawable.bg_personalcard_header));
            return;
        }
        ImageLoader.getInstance().loadImage(bgUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                ivZoom.setImageDrawable(getResources().getDrawable(R.drawable.bg_personalcard_header));
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(null, bitmap);
                ivZoom.setImageDrawable(drawable);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });


    }


    public static MineFragmentNotVPFragment2 newInstance() {
        return new MineFragmentNotVPFragment2();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHostActivity = activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_medal:
                MyMedalActivity.goThis(mContext,Constant.AccountLianLain);
                break;
            case R.id.clickFundOverView:
                navNext(FundOverViewActivity.class);
                break;
            case R.id.clickFundRecord://我的投资
//                navNext(FundRecordsActivity2.class);
                navNext(FundRecordsActivity.class);
                break;
            case R.id.clickInvestSummary:
                navNext(InvestSummaryActivity.class);
                break;
            case R.id.clickTransfer:
                navNext(TransferProductListActivity.class);
                break;
            case R.id.clickRebackFund2: //回款
            case R.id.clickRebackFund3:
                SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(FinanceApplication.getAppContext());
                String state = sp.getStringValue(SharedPreferencesHelper.STATE_RETURN_CALENDER_OR_LIST);
                if ("2".equals(state)) {
                    navNext(QueryReturnMoneyListActivity.class);
                } else {
                    navNext(ReturnMoneyCalendarActivity.class);
                }
                break;
            case R.id.clickRecharge:
                navNext(FundFlowActivity.class);
                break;
            case R.id.clickTrialCoin:
                TrialCoinActivity.goThis(mContext,Constant.AccountLianLain);
                break;

            case R.id.clickVoucher:
                TicketActivity.goThis(mContext,Constant.AccountLianLain,voucher_count,addrate_count,presell_count);
                break;
            case R.id.clickInviteGift:
                InviteGiftActivity.goThis(mContext);
                break;
            case R.id.clickTravel:
                Long id = user.getUserId();
//                gotoWeb(HttpService.mBaseUrl + "/AppContent/tourism?uid="+id, "玩赚旅游");
                gotoWeb("/h5/xingya/appitem?uid=" + id, "玩赚旅游");
                break;
            case R.id.btnRecharge:
                //充值入口
                boolean isAllowRecharge = SharedPreferencesHelper.getInstance(mContext).getBooleanValue(SharedPreferencesHelper.KEY_ALLOW_RECHARGE);
                if (isAllowRecharge){
                    AlertDialogUtils.confirmGoRecharg(mContext);
                }else{
                    new RechargeCloseDialog().show(getActivity().getFragmentManager(),"RechargeCloseDialog");
                }
                break;
            case R.id.btnWithdraw:
                navNext(WithdrawDepositActivity.class);
                break;
            case R.id.userHead:
                navNext(PersonalCardActivity.class);
                break;
            case R.id.ibClose:
                rlTopInfo.setVisibility(View.GONE);
                SharedPreferencesHelper.getInstance(getActivity()).putStringValue(SharedPreferencesHelper.MINE_NO_SHOW_INFO, title);
                break;
            case R.id.llSetupGuide:
                Intent intent = new Intent(getActivity(), SetupGuideActivity.class);
                intent.putExtra(SetupGuideActivity.SETUP_STATE, guideSetupState);
                gotoActivity(intent);
                break;
            case R.id.my_describe:
                Intent intent1 = new Intent(getActivity(), MyDescribeAcitvity.class);
                gotoActivity(intent1);
                break;
            case R.id.invest_top:
                InvestTopActivity.goThis(mContext,Constant.AccountLianLain,mNumber);
                break;

        }
    }

    private void navNext(Class<? extends Activity> activity) {
        Intent intent = new Intent(mHostActivity, activity);
        startActivity(intent);
        mHostActivity.overridePendingTransition(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left);
    }

    public void onEventMainThread(UserHeadEvent event) {
        if (!isAdded()) return;
        if (event != null) {
            mUserHead.setImageDrawable(event.getUserHead());
        }
    }

    public void onEventMainThread(RefreshTab event) {
        if (!isAdded()) return;
        if (event != null && event.tabType == RefreshTab.TAB_MINE) {
            refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
