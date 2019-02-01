package cn.vpfinance.vpjr.module.product;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.DifColorTextStringBuilder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.MyClickableSpan;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.NewBaseInfoBean;
import cn.vpfinance.vpjr.gson.ProductTabBean;
import cn.vpfinance.vpjr.gson.TabPermissionBean;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.common.fragment.WebViewFragment;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialogFragment;
import cn.vpfinance.vpjr.module.dialog.InvestmentRiskTipsDialog;
import cn.vpfinance.vpjr.module.product.fragment.BaseInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewBaseInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewCarInfoFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewDepositFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewWritingAndPicFragment;
import cn.vpfinance.vpjr.module.product.fragment.NewWritingFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductPrivateFragment;
import cn.vpfinance.vpjr.module.product.fragment.ProductPrivateNoLoginFragment;
import cn.vpfinance.vpjr.module.product.invest.DepositInvestActivity;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.module.voucher.fragment.NewPersonInfoFragment;
import cn.vpfinance.vpjr.module.voucher.fragment.NewPictureFragment;
import cn.vpfinance.vpjr.module.voucher.fragment.NewType6Fragment;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.MyCountDownTimer;

/**
 * Created by Administrator on 2016/10/24.
 * 标的详情
 */
public class NewRegularProductActivity extends BaseActivity {

    public static final String EXTRA_PRODUCT_ID = "id";
    public static final String NATIVE_PRODUCT_ID = "native_product_id";//如果查看原标传转让标id，不是传0
    public static final String PRODUCT_TITLE = "product_title";//标名
    public static final String IS_GE_TUI = "is_ge_tui";//是否个推
    public static final String IS_DEPOSIT = "is_deposit";//是否定存宝
    public static final String RecordPoolId = "recordPoolId";//如果是从定存宝的出借详情进去标的详情,传递(RecordPoolId),其他的不传递
    public int answerStatus;//1不弹 2弹提示框, 去做风险测评

    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.pager)
    ViewPager mPager;
    @Bind(R.id.item_loan_title)
    TextView itemLoanTitle;
    @Bind(R.id.ivAllowTransfer)
    ImageView ivAllowTransfer;
    @Bind(R.id.iv_fdjx)
    ImageView ivFdjx;
    @Bind(R.id.ivProductState)
    ImageView ivProductState;
    @Bind(R.id.item_loan_rate)
    TextView itemLoanRate;
    @Bind(R.id.item_loan_rate_percent)
    TextView itemLoanRatePercent;
    @Bind(R.id.tv_addrate)
    TextView tvAddrate;
    @Bind(R.id.tv_deadline)
    TextView tvDeadline;
    @Bind(R.id.item_loan_term)
    TextView itemLoanTerm;
    @Bind(R.id.tv_money_desc)
    TextView tvMoneyDesc;
    @Bind(R.id.item_loan_totle)
    TextView itemLoanTotle;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.tv_progress_num)
    TextView tvProgressNum;
    @Bind(R.id.ll_progress_container)
    LinearLayout llProgressContainer;
    @Bind(R.id.countDown)
    MyCountDownTimer countDown;
    @Bind(R.id.tv_warning_desc)
    TextView tvWarningDesc;
    @Bind(R.id.rewardIv)
    ImageView rewardIv;
    @Bind(R.id.iv_home_state)
    ImageView ivHomeState;
    @Bind(R.id.rootView)
    RelativeLayout rootView;
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;
//    @Bind(R.id.line_gray)
//    View lineGray;
    @Bind(R.id.invest)
    Button btnInverst;
    @Bind(R.id.coutdowntime_going)
    MyCountDownTimer coutdowntimeGoing;
    private HttpService mHttpService;
    private long mPid;
    private int mNativeId;
    private ArrayList<BaseFragment> mFragments;
    private ArrayList<String> mTabName;
    private String mTitle;
    private TabPermissionBean mPermissionBean;
    private boolean isDeposit;
    private String recordPoolId = "";
    private ProductTabBean productTabBean;
    private long serverTime;
    private NewBaseInfoBean newBaseInfoBean;

    public static void goNewRegularProductActivity(Context context, long id, int nativeProductId, String title, boolean isDeposit) {
        if (context != null) {
            Intent intent = new Intent(context, NewRegularProductActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, id);
            intent.putExtra(NATIVE_PRODUCT_ID, nativeProductId);
            intent.putExtra(PRODUCT_TITLE, title);
            intent.putExtra(IS_DEPOSIT, isDeposit);
            context.startActivity(intent);
        }
    }

    public static void goNewRegularProductActivity(Context context, long id, int nativeProductId, String title, boolean isDeposit, String recordPoolId) {
        if (context != null) {
            Intent intent = new Intent(context, NewRegularProductActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, id);
            intent.putExtra(NATIVE_PRODUCT_ID, nativeProductId);
            intent.putExtra(PRODUCT_TITLE, title);
            intent.putExtra(IS_DEPOSIT, isDeposit);
            intent.putExtra(RecordPoolId, recordPoolId);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_detail);
        ButterKnife.bind(this);

        mHttpService = new HttpService(this, this);
        Intent intent = getIntent();
        if (intent != null) {
            mPid = intent.getLongExtra(EXTRA_PRODUCT_ID, -1);
            mNativeId = intent.getIntExtra(NATIVE_PRODUCT_ID, 0);
            String titleStr = intent.getStringExtra(PRODUCT_TITLE);
            mTitle = TextUtils.isEmpty(titleStr) ? "产品详情" : titleStr;
            boolean isGeTui = intent.getBooleanExtra(IS_GE_TUI, false);
            isDeposit = intent.getBooleanExtra(IS_DEPOSIT, false);
            recordPoolId = intent.getStringExtra(RecordPoolId);
            if (isGeTui) {
                //个推点击了过来的就友盟统计
                ArrayMap<String, String> map = new ArrayMap<String, String>();
                map.put("GeTuiType", "2");
                MobclickAgent.onEvent(this, "GeTuiClick", map);
            }
        }
        mTitleBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("微品金融");
        mHttpService.getServiceTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDeposit) {
            mHttpService.onGetDepositPermission("" + mPid);
        } else {
            mHttpService.onGetPermission("" + mPid, mNativeId + "");
        }
        mPager.setCurrentItem(0);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Tab_Permission.ordinal()) {
            //解析
            Gson gson = new Gson();
            mPermissionBean = gson.fromJson(json.toString(), TabPermissionBean.class);
            if (isDeposit) {
                mHttpService.getDepositProductInfo(mPid, recordPoolId);
            } else {
                mHttpService.getFixProductNew("" + mPid, "" + mNativeId);
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo_New.ordinal()) {
            Gson gson = new Gson();
            productTabBean = gson.fromJson(json.toString(), ProductTabBean.class);
            mHttpService.getServiceTime();
            initData(productTabBean);
            initView();
        }

        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Pool_Info.ordinal()) {
            Gson gson = new Gson();
            ProductTabBean productTabBean = gson.fromJson(json.toString(), ProductTabBean.class);
            initData(productTabBean);
            initView();
        }

        if (reqId == ServiceCmd.CmdId.CMD_Regular_Tab.ordinal()) {
            try {
                Gson gson = new Gson();
                newBaseInfoBean = gson.fromJson(json.toString(), NewBaseInfoBean.class);
                answerStatus = newBaseInfoBean.answerStatus;
                mTitleBar.setTitle(newBaseInfoBean.loanTitle);
                initHeaderData(newBaseInfoBean);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_SERVICE_TIME.ordinal()) {
            serverTime = json.optLong("serverTime");

            long difTime = serverTime - System.currentTimeMillis();
        }
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            if (!AppState.instance().logined() || json.optString("success").equals("false")) {//未登录
                gotoActivity(LoginActivity.class);
                return;
            }
            UserInfoBean mUserInfoBean = mHttpService.onGetUserInfo(json);
            if (null == mUserInfoBean) return;
            if (newBaseInfoBean.product == 4 &&
                    !mUserInfoBean.isOpen.equals("1")) {
//                new AlertDialog.Builder(NewRegularProductActivity.this)
//                        .setTitle("开通存管账户")
//                        .setMessage("根据监管要求，请先开通银行存管账户")
//                        .setPositiveButton("立即开通", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                User user = DBUtils.getUser(NewRegularProductActivity.this);
//                                if (user != null) {
//                                    if (TextUtils.isEmpty(user.getRealName())) {
//                                        RealnameAuthActivity.goThis(NewRegularProductActivity.this);
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
                                User user = DBUtils.getUser(NewRegularProductActivity.this);
                                if (user != null) {
                                    if (TextUtils.isEmpty(user.getRealName())) {
                                        RealnameAuthActivity.goThis(NewRegularProductActivity.this);
                                        Utils.Toast("请先去实名认证");
                                    } else {
                                        gotoWeb("/hx/account/create?userId=" + user.getUserId(), "");
                                    }
                                }
                            }
                        })
                        .setBtnLeft("暂不")
                        .createAndShow(NewRegularProductActivity.this);
                return;
            }

            final User user = DBUtils.getUser(NewRegularProductActivity.this);
            if (newBaseInfoBean.answerStatus == 0) {
                Utils.Toast("请先进行风险评测");
                gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
                return;
            }
            if (newBaseInfoBean.answerStatus == 2) {
//                new AlertDialog.Builder(NewRegularProductActivity.this)
//                        .setMessage("您很久未进行过出借人风险测评，根据监管要求，请先完成风险测评再进行出借")
//                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (user != null) {
//                                    gotoWeb("/h5/help/riskInvestigation?userId=" + user.getUserId(), "风险评测");
//                                }
//                            }
//                        })
//                        .setNegativeButton("下次再说", null)
//                        .show();
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
                        .createAndShow(NewRegularProductActivity.this);
                return;
            }

            //风险提示弹窗
            if (newBaseInfoBean.riskLevel > mUserInfoBean.riskLevel) {//产品风险等级高过个人风险等级
                InvestmentRiskTipsDialog investmentRiskTipsDialog = new InvestmentRiskTipsDialog(NewRegularProductActivity.this);
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
                        gotoWeb("/h5/help/riskCheckReason?userId=" + user.getUserId(), "查看原因");
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

    //出借跳转
    public void invest() {
        if (newBaseInfoBean != null) {
            Intent intent = new Intent(NewRegularProductActivity.this, ProductInvestActivity.class);
            if ("1".equals(newBaseInfoBean.imageUrlJump)) {//跳转本地活动出借页面
                intent.putExtra(ProductInvestActivity.IPHONE, newBaseInfoBean.givePhone);
            }
            intent.putExtra("pid", "" + newBaseInfoBean.loanId);
            int accountType = Constant.AccountLianLain;
            if (newBaseInfoBean.product == 4) {
                accountType = Constant.AccountBank;
            }
            intent.putExtra(Constant.AccountType, accountType);
            intent.putExtra(DepositInvestActivity.IS_ORDER, false);
            App myApp = (App) getApplication();
            myApp.currentPid = "" + newBaseInfoBean.loanId;
            intent.putExtra("isGraceDays", newBaseInfoBean.graceDays);//是否是浮动计息产品
            startActivity(intent);
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (isFinishing()) return;
        if (reqId == ServiceCmd.CmdId.CMD_Tab_Permission.ordinal()) {//接口请求失败默认可以查看
            mPermissionBean = new TabPermissionBean();
            mPermissionBean.showRecord = 1;
            mHttpService.getFixProductNew("" + mPid, "" + mNativeId);
        }
    }

    /**
     * 设置高亮提示内容
     *
     * @param mNewBaseInfoBean
     */
    private void setWarningContent(final NewBaseInfoBean mNewBaseInfoBean) {
        //        final String content = "该产品52.12%采用浮动计息36.85%方式，最大还款日40.50%期为1个月+7天；1个月内还款年利率为7.2%，超过1个月的7天浮动计息期每天以7.5%的年利率计息。了解详情>>";
        String content = mNewBaseInfoBean.flowInvestReminder + "  了解详情>>";
        List<String> floatPercent = Utils.getFloatPercent(content);
        DifColorTextStringBuilder difColorTextStringBuilder = new DifColorTextStringBuilder();
        difColorTextStringBuilder.setContent(content);
        for (int i = 0; i < floatPercent.size(); i++) {
            difColorTextStringBuilder.setHighlightContent(floatPercent.get(i), R.color.red_text);
        }
        difColorTextStringBuilder.setHighlightContent("了解详情>>", R.color.red_text)
                .setHighlightContent("了解详情>>", new MyClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        gotoWeb("/h5/help/floatProductTips?loanId=" + mNewBaseInfoBean.loanId, "");
                    }
                })
                .setTextView(tvWarningDesc)
                .create();
    }

    //设置头布局数据
    private void initHeaderData(NewBaseInfoBean newBaseInfoBean) {
        if (newBaseInfoBean != null) {
            itemLoanTitle.setText(newBaseInfoBean.loanTitle);
            if ("true".equals(newBaseInfoBean.allowTransfer)) {//是否允许转让
                ivAllowTransfer.setVisibility(View.VISIBLE);
            } else {
                ivAllowTransfer.setVisibility(View.GONE);
            }
            if (newBaseInfoBean.graceDays > 0) {//是浮动计息
                ivFdjx.setVisibility(View.VISIBLE);
//                lineGray.setVisibility(View.VISIBLE);
                tvWarningDesc.setVisibility(View.VISIBLE);//提示
                setWarningContent(newBaseInfoBean);
            } else {
//                lineGray.setVisibility(View.GONE);
                ivFdjx.setVisibility(View.GONE);
                tvWarningDesc.setVisibility(View.GONE);
            }
            ivProductState.setVisibility(View.GONE);//第三方担保
            Common.productSubType(this, ivProductState, newBaseInfoBean.subType);

            double v = (newBaseInfoBean.rate - newBaseInfoBean.reward) * 100;
            itemLoanRate.setText(FormatUtils.formatRate(v));//约定年利率

            ivHomeState.setVisibility(View.GONE);
            if (newBaseInfoBean.reward != 0) {//加息
                rewardIv.setVisibility(View.VISIBLE);
                tvAddrate.setVisibility(View.VISIBLE);
                tvAddrate.setText("+" + FormatUtils.formatRate(newBaseInfoBean.reward * 100) + "%");
            } else {
                rewardIv.setVisibility(View.GONE);
//                ivHomeState.setVisibility(View.GONE);
                tvAddrate.setVisibility(View.GONE);
            }
            itemLoanTerm.setText(newBaseInfoBean.month);//项目期限
            if (newBaseInfoBean.issueloan > 10000) {
                itemLoanTotle.setText(FormatUtils.formatDown2(newBaseInfoBean.issueloan / 10000) + "万");//项目总额
            } else {
                itemLoanTotle.setText(FormatUtils.formatDown2(newBaseInfoBean.issueloan) + "元");
            }

            //默认状态
            llProgressContainer.setVisibility(View.VISIBLE);
            countDown.setVisibility(View.GONE);
//            rewardIv.setVisibility(View.GONE);
            String state = "";
            coutdowntimeGoing.setVisibility(View.GONE);
            btnInverst.setEnabled(true);
            float pro = 0.00f;
            switch (newBaseInfoBean.status) {
                case "1"://预售中
                    if (!TextUtils.isEmpty(newBaseInfoBean.bookCouponNumber) && Integer.parseInt(newBaseInfoBean.bookCouponNumber) > 0) {//有预约券
                        double canOrderMoney = newBaseInfoBean.issueloan * newBaseInfoBean.bookPercent;
                        if (canOrderMoney <= newBaseInfoBean.tenderMoney) {//预约已满额
                            btnInverst.setEnabled(false);
                            state = "预约已满额";
                        } else {
                            btnInverst.setEnabled(true);
                            state = "我要预约";
                        }
                    } else {
                        btnInverst.setEnabled(false);
                        state = "您没有预约券";
                    }
                    llProgressContainer.setVisibility(View.GONE);
                    countDown.setVisibility(View.VISIBLE);
//                    rewardIv.setVisibility(View.GONE);
                    countDown.setCountDownTime(this, Long.parseLong(newBaseInfoBean.publishTime));
                    countDown.setOnFinishListener(new MyCountDownTimer.onFinish() {
                        @Override
                        public void finish() {
                            countDown.postDelayed(new Runnable() {//计时完成,刷新数据
                                @Override
                                public void run() {
                                    mHttpService.getRegularTab(productTabBean.tabList.get(0).url);
                                }
                            }, 1000);
                        }
                    });
                    break;
                case "2"://进行中
                    if (newBaseInfoBean.process >= 100) {//满标审核
                        state = getString(R.string.productStateFill);
                        tvProgressNum.setText("100%");
//                        rewardIv.setVisibility(View.VISIBLE);
//                        rewardIv.setImageResource(R.mipmap.chanpin_manbiao);
                        if (newBaseInfoBean.canBuyMoney <= 0.005) {
                            btnInverst.setEnabled(false);
                        }
                        pro = 100.00f;
                    } else {
                        state = getString(R.string.productState2);
                        btnInverst.setEnabled(true);
                        pro = newBaseInfoBean.process;
                        coutdowntimeGoing.setVisibility(View.VISIBLE);//剩余投标时间倒计时
                        coutdowntimeGoing.setCountDownTime(this, Long.parseLong(newBaseInfoBean.bidEndTime));
                        coutdowntimeGoing.setOnFinishListener(new MyCountDownTimer.onFinish() {
                            @Override
                            public void finish() {
                                mHttpService.getRegularTab(productTabBean.tabList.get(0).url);
                            }
                        });
                    }
                    break;
                case "3"://还款中
                    pro = 100.00f;
                    btnInverst.setEnabled(false);
                    state = getString(R.string.productState3);
//                    rewardIv.setVisibility(View.VISIBLE);
//                    rewardIv.setImageResource(R.mipmap.chanpin_huaikuanzhong);
                    break;
                case "4"://已完成
                    pro = 100.00f;
                    btnInverst.setEnabled(false);
                    state = getString(R.string.productState4);
//                    rewardIv.setVisibility(View.VISIBLE);
//                    rewardIv.setImageResource(R.mipmap.chanpin_yiwancheng);
                    break;
            }
            //设置进度
            progress.setProgress((int) pro);
            tvProgressNum.setText(FormatUtils.formatAbout(pro) + "%");
            btnInverst.setText(state);

        }
    }

    protected void initView() {
        try {
//            mTabs.setIndicatorColor(0xFFFF3035);
//            mTabs.setSelectedTabIndicatorColor(Utils.getColor(R.color.red_text2));
            mPager.setPageMargin(Utils.dip2px(this, 4));
            mPager.setOffscreenPageLimit(5);
            MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
            mPager.setAdapter(myAdapter);
//            mTabs.setViewPager(mPager);
            if (mFragments.size() <= 3) {
                mTabs.setTabMode(TabLayout.MODE_FIXED);
            } else {
                mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
            mTabs.setupWithViewPager(mPager);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        initAppbar();
    }

    //防止AppBarLayout头部滑动不了，需要在数据加载出来后调用该方法
    public void initAppbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return true;
            }
        });
    }

    private void initData(ProductTabBean productTabBean) {

        if (mFragments == null) {
            mFragments = new ArrayList<>();
            mTabName = new ArrayList<>();
        } else {
            mFragments.clear();
            mTabName.clear();
        }
        List<ProductTabBean.TabListBean> tabList = productTabBean.tabList;
        if (tabList != null) {
            for (int i = 0; i < tabList.size(); i++) {
                ProductTabBean.TabListBean bean = tabList.get(i);
                if (bean != null) {
                    String tabName = bean.tabName;
                    String dataType = bean.dataType;
                    String showType = bean.showType;
                    String url = bean.url;

                    if (i == 0) {
                        mTabName.add(tabName);
                        mFragments.add(createFragment(dataType, showType, url));
                        mHttpService.getRegularTab(url);
                    } else {//判断权限
                        int showRecord;
                        if (mPermissionBean == null) {
                            showRecord = 1;
                        } else {
                            showRecord = mPermissionBean.showRecord;
                        }
                        mTabName.add(bean.tabName);

                        BaseFragment fragment = null;
                        if (mPermissionBean == null) {
                            //默认可以查看
                            fragment = createFragment(dataType, showType, url);
                        } else if (showRecord == 1) {
                            //可以查看
                            fragment = createFragment(dataType, showType, url);
                        } else if (showRecord == 2) {
                            //需要登录 这儿登录状态换成本地检测，直接new ProductPrivateNoLoginFragment()会造成登录后还是需要登录界面
//                            fragment = createFragment(dataType,showType,url);
                            fragment = new ProductPrivateNoLoginFragment();
                        } else if (showRecord == 3) {
                            //提示信息
                            String info = TextUtils.isEmpty(mPermissionBean.info) ? "" : mPermissionBean.info;
                            ProductPrivateFragment productPrivateFragment = new ProductPrivateFragment();
                            Bundle args = new Bundle();
                            args.putString(ProductPrivateFragment.HINT_TEXT, info);
                            productPrivateFragment.setArguments(args);
                            fragment = productPrivateFragment;
                        }
                        mFragments.add(fragment);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    public BaseFragment createFragment(String dataType, String showType, String url) {
//        showType = "2";
//        url = "http://192.168.1.192//h5/loan/jewelInfo?platform=android";
        BaseFragment fragment = null;
        if ("2".equals(showType)) {
            fragment = WebViewFragment.newInstance(url);
        } else {
            switch (dataType) {
                case "0":
//                    fragment = NewBaseInfoFragment.newInstance(url, isDeposit);
                    fragment = BaseInfoFragment.newInstance(url);
                    break;
                case "1"://完成
                    fragment = NewWritingFragment.newInstance(url);
                    break;
                case "2"://完成
                    fragment = NewPictureFragment.newInstance(url);
                    break;
                case "3"://完成
                    fragment = NewWritingAndPicFragment.newInstance(url);
                    break;
                case "4"://完成
                    fragment = NewCarInfoFragment.newInstance(url);
                    break;
                case "5"://完成
                    fragment = NewPersonInfoFragment.newInstance(url);
                    break;
                case "7"://定存宝债权列表
                    fragment = NewDepositFragment.newInstance(url);
                    break;
                case "6"://担保贷,借款人信息
                    fragment = NewType6Fragment.newInstance(url);
                    break;
            }
        }

        return fragment;
    }

    @OnClick({R.id.invest})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.invest://立即出借
                if (!AppState.instance().logined()) {//未登录
                    gotoActivity(LoginActivity.class);
                    break;
                }
                if (newBaseInfoBean == null) return;
                mHttpService.getUserInfo();//请求个人中心
                break;
        }
    }

    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            return mFragments.get(position);
//        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabName == null ? "" : mTabName.get(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            super.setPrimaryItem(container, position, object);
        }
    }
}
