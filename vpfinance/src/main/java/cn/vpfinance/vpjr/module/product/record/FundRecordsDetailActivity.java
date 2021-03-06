package cn.vpfinance.vpjr.module.product.record;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.DifColorTextStringBuilder;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.MyClickableSpan;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.RecordDetailBean;
import cn.vpfinance.vpjr.module.dialog.PotocolDialog;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.util.FormatUtils;

/**
 * 出借记录详情
 */
public class FundRecordsDetailActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.product_title)
    TextView mProductTitle;
    @Bind(R.id.status)
    TextView mStatus;
    @Bind(R.id.invest_time)
    TextView mInvestTime;
    @Bind(R.id.invest_money)
    TextView mInvestMoney;
    @Bind(R.id.income)
    TextView mIncome;
    //    @Bind(R.id.voucher_income)
//    TextView mVoucherIncome;
    @Bind(R.id.invest_income_type)
    TextView mInvestIncomeType;
    @Bind(R.id.month)
    TextView mMonth;
    @Bind(R.id.refund_way)
    TextView mRefundWay;
    //    @Bind(R.id.product_type)
//    TextView mProductType;
//    @Bind(R.id.refund_time)
//    TextView mRefundTime;
    //    @Bind(R.id.notice)
//    TextView mNotice;
//    @Bind(R.id.protocol)
//    TagCloudView mProtocol;
    @Bind(R.id.voucher_item)
    LinearLayout mVoucherItem;
    @Bind(R.id.voucher_type)
    TextView mVoucherType;
//    @Bind(R.id.image_status)
//    ImageView mImageStatus;
//    @Bind(R.id.click_look_refund_time)
//    LinearLayout mClickLookRefundTime;


    public static final String UID = "uid";
    @Bind(R.id.ll_warning_desc_container)
    LinearLayout llWarningDescContainer;
    @Bind(R.id.tv_warning_desc)
    TextView tvWarningDesc;
    @Bind(R.id.ll_loan_container)
    LinearLayout llLoanContainer;
    @Bind(R.id.ll_subject_detail_container)
    LinearLayout llSubjectDetailContainer;
    @Bind(R.id.ll_after_loan_container)
    LinearLayout llAfterLoanContainer;
    @Bind(R.id.ll_repayment_plan_container)
    LinearLayout llRepaymentPlanContainer;
    @Bind(R.id.ll_protocol)
    LinearLayout llProtocol;
    @Bind(R.id.ll_transfer_container)
    LinearLayout llTransferContainer;
    @Bind(R.id.tv_repayment_counts)
    TextView tvRepaymentCounts;
    @Bind(R.id.line_gray)
    View lineGray;
    @Bind(R.id.tv_repayment_money)
    TextView tvRepaymentMoney;
    @Bind(R.id.tv_repayment_earnings)
    TextView tvRepaymentEarnings;
    @Bind(R.id.tv_finish_time)
    TextView tvFinishTime;
    @Bind(R.id.tv_update_time)
    TextView tvUpdateTime;
    private HttpService mHttpService;
    private int pid;
    private String mLoanTitle;
    private int mBeanProductType;
    private int mBeanProduct;
    private int mBeanLoanTypeNum;
    private int mRecordId;
    private RecordDetailBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_record_detail_new);
        ButterKnife.bind(this);
        mTitleBar.setHeadBackVisible(View.VISIBLE).setTitle("出借详情");

        Intent intent = getIntent();
        if (intent == null) return;

        int recordId = intent.getIntExtra(UID, 0);
        if (recordId != 0) {
            mHttpService = new HttpService(this, this);
            mHttpService.getRecordDetailInfo("" + recordId);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Record_Detail_info.ordinal()) {
            bean = mHttpService.onGetgetRecordDetailInfo(json);
            if (bean != null) {
                pid = bean.loanId;
                mRecordId = bean.recordId;

                mLoanTitle = bean.loanTitle;
                mProductTitle.setText(mLoanTitle);
                String[] split = bean.tenderTime.split(" ");
                mInvestTime.setText(split[0]);
                String text = FormatUtils.formatDown(bean.tenderMoney);
                mInvestMoney.setText(text);
                mIncome.setText(bean.expectProfit);
                mInvestIncomeType.setText(bean.tenderProfit);
                mMonth.setText(bean.month);
                mRefundWay.setText(bean.refundWay);
//                mProductType.setText(bean.loanType);
//                mRefundTime.setText(bean.finishTime);

                List<RecordDetailBean.ProtocolListBean> protocolList = bean.protocolList;
                final ArrayList<String> names = new ArrayList<>();
                final ArrayList<String> urls = new ArrayList<>();
                for (int i = 0; i < protocolList.size(); i++) {
                    RecordDetailBean.ProtocolListBean protocolListBean = protocolList.get(i);
                    names.add(protocolListBean.title);
                    urls.add(protocolListBean.url);
                }
//                mProtocol.setTags(names);
//                mProtocol.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
//                    @Override
//                    public void onTagClick(int position) {
//                        String name = names.get(position);
//                        String url = urls.get(position);
//                        gotoWeb(url, name);
//                    }
//                });

//                if (TextUtils.isEmpty(bean.info)) {
//                    mNotice.setVisibility(View.GONE);
//                } else {
//                    mNotice.setText(bean.info);
//                    mNotice.setVisibility(View.VISIBLE);
//                }

                switch (bean.type) {
                    case 1:
                        mVoucherItem.setVisibility(View.GONE);
                        break;
                    case 2:
                        mVoucherItem.setVisibility(View.VISIBLE);
                        mVoucherType.setText("已使用代金券抵扣" + bean.voucherMoney + "元");
//                        mVoucherIncome.setText();
                        break;
                    case 3:
                        mVoucherItem.setVisibility(View.VISIBLE);
                        mVoucherType.setText("已使用加息券加息加息" + bean.voucherMoney + "元");
                        break;
                }
                String loanState = "";
                switch (bean.loanState) {
                    //1未发布 2进行中 3回款中 4已完成
                    case 1:
                        loanState = "预售中";
                        mStatus.setText(loanState);
                        mStatus.setVisibility(View.VISIBLE);
//                        mClickLookRefundTime.setVisibility(View.GONE);
                        break;
                    case 2:
                        loanState = "进行中";
                        mStatus.setText(loanState);
                        mStatus.setVisibility(View.VISIBLE);
//                        mClickLookRefundTime.setVisibility(View.GONE);
                        break;
                    case 3:
                        mStatus.setVisibility(View.GONE);
//                        loanState = "回款中";
//                        mClickLookRefundTime.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        mStatus.setVisibility(View.GONE);
//                        loanState = "已完成";
//                        mClickLookRefundTime.setVisibility(View.VISIBLE);
                        break;
                }

                int resImg = 0;
                switch (bean.finshStatus) {//1不显示,2已转让,3已消费,4已完成
                    case 1:
                        break;
                    case 2:
                        resImg = R.drawable.iv_home_state_transfer;
                        break;
                    case 3:
                        resImg = R.drawable.iv_home_state_used;
                        break;
                    case 4:
                        resImg = R.drawable.iv_status_finish;
                        break;
                }
//                if (resImg == 0) {
//                    mImageStatus.setVisibility(View.GONE);
//                } else {
//                    mImageStatus.setVisibility(View.VISIBLE);
//                    Drawable drawable = getResources().getDrawable(resImg);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        mImageStatus.setBackground(drawable);
//                    }
//                }

                mBeanProductType = bean.productType;
                mBeanProduct = bean.product;
                mBeanLoanTypeNum = bean.loanTypeNum;

                //条目显示与隐藏处理
                if (bean.loanState > 2) {
                    llRepaymentPlanContainer.setVisibility(View.VISIBLE);
                } else {
                    llRepaymentPlanContainer.setVisibility(View.GONE);
                }
//                if (bean.protocolList == null || bean.protocolList.size() == 0) {
//                    llProtocol.setVisibility(View.GONE);
//                } else {
                llProtocol.setVisibility(View.VISIBLE);
//                }
                if (mBeanLoanTypeNum == 2) {
                    llTransferContainer.setVisibility(View.VISIBLE);
                } else {
                    llTransferContainer.setVisibility(View.GONE);
                }

                //提示处理
                String content = bean.flowInvestReminder + "  了解详情>>";
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
                                gotoWeb("/h5/help/floatProductTips?loanId=" + getIntent().getStringExtra("loanId"), "");
                            }
                        })
                        .setTextView(tvWarningDesc)
                        .create();
                if (bean.graceDays > 0) {//是浮动计息
                    llWarningDescContainer.setVisibility(View.VISIBLE);
                } else {
                    llWarningDescContainer.setVisibility(View.GONE);
                }

                //待回款数据
                tvRepaymentCounts.setText(bean.unPeriods);
                tvRepaymentMoney.setText(bean.unAmount+"元");
                tvRepaymentEarnings.setText(bean.unProfit+"元");
                tvFinishTime.setText(bean.finishDate);
                tvUpdateTime.setText("更新至"+bean.afterLoanUpdateTime);
            }
        }
    }

//    @OnClick({R.id.click_look_refund_time})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.click_look_product_detail:
//                if (mBeanProduct == 0 && mBeanProductType == 1) {//定存宝
//
//                    NewRegularProductActivity.goNewRegularProductActivity(this, (long) pid, 0, "", true);
//
//                } else if (mBeanLoanTypeNum == 2) {//1定期 2债权转让 3权益出借
//                    NewTransferProductActivity.goNewTransferProductActivity(this, (long) pid);
//                } else if (mBeanProductType == 3) {
//                    PresellProductActivity.goPresellProductActivity(this, "" + (long) pid);
//                } else if (mBeanProduct == 4) {
//                    NewRegularProductActivity.goNewRegularProductActivity(this, (long) pid, 0, mLoanTitle, false);
//                } else {
//                    NewRegularProductActivity.goNewRegularProductActivity(this, (long) pid, 0, mLoanTitle, false);
//                }

//                break;
//            case R.id.click_look_refund_time:
//                if (null != bean) {
//                    if (bean.graceDays > 0) {//是浮动计息
//                        RepayFloatActivity.startRepayFloatActivity(this, mRecordId + "", bean.loanState + "", bean.loanId + "");
//                    } else {
//                        InvestRecordRefundActivity.goInvestRecordRefund(FundRecordsDetailActivity.this, 0, "" + mRecordId);
//                    }
//                }
//                break;
//        }
//    }

    @OnClick({R.id.ll_subject_detail_container, R.id.ll_after_loan_container, R.id.ll_repayment_plan_container, R.id.ll_protocol, R.id.ll_transfer_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_subject_detail_container://查看项目详情
                NewRegularProductActivity.goNewRegularProductActivity(this, (long) pid, 0, mLoanTitle, false);
                break;
            case R.id.ll_after_loan_container://查看贷后情况
                //todo
                gotoWeb("", "贷后情况");
                break;
            case R.id.ll_repayment_plan_container://查看回款计划
                if (null != bean) {
                    if (bean.graceDays > 0) {//是浮动计息
                        RepayFloatActivity.startRepayFloatActivity(this, mRecordId + "", bean.loanState + "", bean.loanId + "");
                    } else {
                        InvestRecordRefundActivity.goInvestRecordRefund(FundRecordsDetailActivity.this, 0, "" + mRecordId);
                    }
                }
                break;
            case R.id.ll_protocol://查看相关合同
                if (bean.protocolList.size() > 0 && bean.protocolList.size() == 1) {//只有一条直接跳转
                    gotoWeb(bean.protocolList.get(0).url, bean.protocolList.get(0).title);
                } else {//超过1条弹窗
                    showPotocolDialog();
                }
                break;
            case R.id.ll_transfer_container://查看转让详情
                //todo
                break;
        }
    }

    PotocolDialog potocolDialog = null;

    private void showPotocolDialog() {
        if (potocolDialog == null) {
            potocolDialog = new PotocolDialog(this);
            potocolDialog.setData(bean.protocolList);
            potocolDialog.show();
        }
    }
}
