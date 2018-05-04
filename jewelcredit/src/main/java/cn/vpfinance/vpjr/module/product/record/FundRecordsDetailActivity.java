package cn.vpfinance.vpjr.module.product.record;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.RecordDetailBean;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.shenyang.PresellProductActivity;
import cn.vpfinance.vpjr.module.product.transfer.NewTransferProductActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.view.TagCloudView;

/**
 * 投资记录详情
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
    @Bind(R.id.voucher_income)
    TextView mVoucherIncome;
    @Bind(R.id.invest_income_type)
    TextView mInvestIncomeType;
    @Bind(R.id.month)
    TextView mMonth;
    @Bind(R.id.refund_way)
    TextView mRefundWay;
    @Bind(R.id.product_type)
    TextView mProductType;
    @Bind(R.id.refund_time)
    TextView mRefundTime;
    @Bind(R.id.notice)
    TextView mNotice;
    @Bind(R.id.protocol)
    TagCloudView mProtocol;
    @Bind(R.id.voucher_item)
    LinearLayout mVoucherItem;
    @Bind(R.id.voucher_type)
    TextView mVoucherType;
    @Bind(R.id.image_status)
    ImageView mImageStatus;
    @Bind(R.id.click_look_refund_time)
    LinearLayout mClickLookRefundTime;


    public static final String UID = "uid";
    private HttpService mHttpService;
    private int pid;
    private String mLoanTitle;
    private int mBeanProductType;
    private int mBeanProduct;
    private int mBeanLoanTypeNum;
    private int mRecordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_record_detail);
        ButterKnife.bind(this);
        mTitleBar.setHeadBackVisible(View.VISIBLE).setTitle("投资详情");

        Intent intent = getIntent();
        if (intent == null) return;

        int recordId = intent.getIntExtra(UID, 0);
        if (recordId != 0){
            mHttpService = new HttpService(this,this);
            mHttpService.getRecordDetailInfo(""+recordId);
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Record_Detail_info.ordinal()){
            RecordDetailBean bean = mHttpService.onGetgetRecordDetailInfo(json);
            if (bean != null){
                pid = bean.loanId;
                mRecordId = bean.recordId;

                mLoanTitle = bean.loanTitle;
                mProductTitle.setText(mLoanTitle);
                mInvestTime.setText("投资时间："+bean.tenderTime);
                String text = FormatUtils.formatDown(bean.tenderMoney);
                mInvestMoney.setText(text);
                mIncome.setText(bean.expectProfit);
                mInvestIncomeType.setText(bean.tenderProfit);
                mMonth.setText(bean.month);
                mRefundWay.setText(bean.refundWay);
                mProductType.setText(bean.loanType);
                mRefundTime.setText(bean.finishTime);

                List<RecordDetailBean.ProtocolListBean> protocolList = bean.protocolList;
                final ArrayList<String> names = new ArrayList<>();
                final ArrayList<String> urls = new ArrayList<>();
                for (int i = 0; i < protocolList.size(); i++) {
                    RecordDetailBean.ProtocolListBean protocolListBean = protocolList.get(i);
                    names.add(protocolListBean.title);
                    urls.add(protocolListBean.url);
                }
                mProtocol.setTags(names);
                mProtocol.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
                    @Override
                    public void onTagClick(int position) {
                        String name = names.get(position);
                        String url = urls.get(position);
                        gotoWeb(url,name);
                    }
                });

                if (TextUtils.isEmpty(bean.info)){
                    mNotice.setVisibility(View.GONE);
                }else{
                    mNotice.setText(bean.info);
                    mNotice.setVisibility(View.VISIBLE);
                }

                switch (bean.type){
                    case 1:
                        mVoucherItem.setVisibility(View.GONE);
                        break;
                    case 2:
                        mVoucherItem.setVisibility(View.VISIBLE);
                        mVoucherType.setText("已使用代金券抵扣");
                        mVoucherIncome.setText(bean.voucherMoney);
                        break;
                    case 3:
                        mVoucherItem.setVisibility(View.VISIBLE);
                        mVoucherType.setText("已使用加息券加息");
                        mVoucherIncome.setText(bean.voucherMoney);
                        break;
                }
                String loanState = "";
                switch (bean.loanState){
                    //1未发布 2进行中 3回款中 4已完成
                    case 1:
                        loanState = "预售中";
                        mStatus.setText(loanState);
                        mStatus.setVisibility(View.VISIBLE);
                        mClickLookRefundTime.setVisibility(View.GONE);
                        break;
                    case 2:
                        loanState = "进行中";
                        mStatus.setText(loanState);
                        mStatus.setVisibility(View.VISIBLE);
                        mClickLookRefundTime.setVisibility(View.GONE);
                        break;
                    case 3:
                        mStatus.setVisibility(View.GONE);
//                        loanState = "回款中";
                        mClickLookRefundTime.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        mStatus.setVisibility(View.GONE);
//                        loanState = "已完成";
                        mClickLookRefundTime.setVisibility(View.VISIBLE);
                        break;
                }

                int resImg = 0;
                switch (bean.finshStatus){//1不显示,2已转让,3已消费,4已完成
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
                if (resImg == 0){
                    mImageStatus.setVisibility(View.GONE);
                }else{
                    mImageStatus.setVisibility(View.VISIBLE);
                    Drawable drawable = getResources().getDrawable(resImg);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mImageStatus.setBackground(drawable);
                    }
                }

                mBeanProductType = bean.productType;
                mBeanProduct = bean.product;
                mBeanLoanTypeNum = bean.loanTypeNum;

            }
        }
    }

    @OnClick({R.id.click_look_product_detail, R.id.click_look_refund_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.click_look_product_detail:
                if (mBeanProduct == 0 && mBeanProductType == 1){//定存宝

                    NewRegularProductActivity.goNewRegularProductActivity(this,(long) pid,0,"",true,0);

                }else if (mBeanLoanTypeNum == 2){//1定期理财 2债权转让 3权益投资
                    NewTransferProductActivity.goNewTransferProductActivity(this, (long)pid);
                }else if (mBeanProductType == 3){
                    PresellProductActivity.goPresellProductActivity(this, "" + (long)pid);
                }else if (mBeanProduct == 4){
                    NewRegularProductActivity.goNewRegularProductActivity(this,(long)pid,0,mLoanTitle,false,1);
                }else{
                    NewRegularProductActivity.goNewRegularProductActivity(this,(long)pid,0,mLoanTitle,false,0);
                }

                break;
            case R.id.click_look_refund_time:
                InvestRecordRefundActivity.goInvestRecordRefund(FundRecordsDetailActivity.this,0,""+mRecordId);
                break;
        }
    }
}
