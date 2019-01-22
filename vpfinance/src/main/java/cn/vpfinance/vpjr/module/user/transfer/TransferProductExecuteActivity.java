package cn.vpfinance.vpjr.module.user.transfer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.shenyang.PresellProductActivity;
import cn.vpfinance.vpjr.module.product.invest.RegularProductActivity;
import cn.vpfinance.vpjr.model.TransferProductDetailInfo;
import cn.vpfinance.vpjr.module.product.record.TransferRefundActivity;
import cn.vpfinance.vpjr.util.Common;

/**
 * 债权转让转出详情界面
 */
public class TransferProductExecuteActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvRate)
    TextView tvRate;
    @Bind(R.id.tvMonth)
    TextView tvMonth;
    @Bind(R.id.tvRefundWay)
    TextView tvRefundWay;
    @Bind(R.id.tvInvestMoney)
    TextView tvInvestMoney;
    @Bind(R.id.tvInvestTime)
    TextView tvInvestTime;
    @Bind(R.id.ivProductState)
    ImageView ivProductState;
    @Bind(R.id.btnTransfer)
    Button btnTransfer;
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
//    @Bind(R.id.ivSubType)
//    ImageView ivSubType;
//    @Bind(R.id.ivAllowTransfer)
//    ImageView ivAllowTransfer;
    @Bind(R.id.clickLookProtocol2)
    TextView clickLookProtocol2;
    @Bind(R.id.notice)
    LinearLayout mNotice;

    private String recordId;
    public static final String RECORD_ID = "recordId";
    private Context mContext;
    private HttpService mHttpService;
//    private int borrowId = -1;
    private long borrowId = -1;
    private TransferProductDetailInfo info;
    private TransferProductDetailInfo.AgreementListBean protocolInfo;
    private TransferProductDetailInfo.AgreementListBean protocolInfo2;
    private int accountType = Constant.AccountLianLain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_product_execute);
        ButterKnife.bind(this);
        mContext = this;
        titleBar.setTitle("出借详情").setHeadBackVisible(View.VISIBLE);

        mHttpService = new HttpService(this, this);

        Intent intent = getIntent();
        if (intent != null) {
            recordId = TextUtils.isEmpty(intent.getStringExtra(RECORD_ID)) ? "" : intent.getStringExtra(RECORD_ID);
            accountType = intent.getIntExtra(Constant.AccountType,Constant.AccountLianLain);
            mHttpService.getTransferProductDetail(recordId);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(recordId))
            mHttpService.getTransferProductDetail(recordId);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Transfer_Assign_Detail.ordinal()) {
            info = mHttpService.onGetTransferProductDetail(json);

            borrowId = info.getBorrowId();

            tvTitle.setText(TextUtils.isEmpty(info.getTitle()) ? "" : info.getTitle());
            tvRate.setText(info.getRate() == 0 ? "" : Utils.doubleFloor(info.getRate()) + "%");
            tvMonth.setText(info.getMonth());
            tvInvestMoney.setText(TextUtils.isEmpty(info.getTenderMoney()) ? "" : "¥" + info.getTenderMoney());
            tvInvestTime.setText(TextUtils.isEmpty(info.getTenderTime()) ? "" : info.getTenderTime());

            String finshTime = info.getFinshTime();//finshTime=2016-12-09
            if (!TextUtils.isEmpty(finshTime)){
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = simpleDateFormat.parse(finshTime);
                    long time = date.getTime();
                    //+5天
                    time = time - 1000 * 60 * 60 * 24 * 5;
                    String nowDay = simpleDateFormat.format(new Date());

                    long nowDayTime = simpleDateFormat.parse(nowDay).getTime();
                    if (time > nowDayTime){
                        btnTransfer.setEnabled(true);
                        mNotice.setVisibility(View.GONE);
                    }else{
                        btnTransfer.setEnabled(false);
                        mNotice.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<TransferProductDetailInfo.AgreementListBean> agreementList = info.getAgreementList();
            if (agreementList != null && agreementList.size() != 0) {
                if (agreementList.size() == 1) {
                    protocolInfo = agreementList.get(0);
                } else if (agreementList.size() == 2) {
                    protocolInfo = agreementList.get(0);
                    clickLookProtocol2.setVisibility(View.VISIBLE);
                    protocolInfo2 = agreementList.get(1);
                }
            }

            //1按月等额本息、2按月付息到期还本、3到期一次性还本息
            tvRefundWay.setText(info.getRefundWay() == 1 ? "按月等额本息" :
                    info.getRefundWay() == 2 ? "按月付息到期还本" :
                            info.getRefundWay() == 3 ? "到期一次性还本息" : "");
            //是否允许转让：0否1是
            String allowTransferTypeStr = TextUtils.isEmpty(info.getAllowTransferType()) ? "0" : info.getAllowTransferType();
//            if ("0".equals(allowTransferTypeStr)){
//                ivAllowTransfer.setVisibility(View.GONE);
//            }else{
//                ivAllowTransfer.setVisibility(View.VISIBLE);
//            }
            //1质押，2保证，3抵押，4信用，5实地
            String subTypeStr = info.getSubType();
            try {
                int subType = Integer.parseInt(subTypeStr);
                if (subType != 0)
                    ivProductState.setVisibility(View.VISIBLE);
//                Common.productSubType(this,ivSubType,subType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String stateStr = info.getStatus();
            try {
                //1回款中 2已完成 3转让中 4已转让
                int state = Integer.parseInt(stateStr);
                switch (state) {
                    case 1:
                        btnTransfer.setVisibility(View.VISIBLE);
                        ivProductState.setImageResource(R.drawable.iv_refund);
                        break;
                    case 2:
                        btnTransfer.setVisibility(View.GONE);
                        ivProductState.setImageResource(R.drawable.iv_transfer_finish);
                        break;
                    case 3:
                        btnTransfer.setVisibility(View.GONE);
                        ivProductState.setImageResource(R.drawable.iv_transfer_finishing);
                        break;
                    case 4:
                        btnTransfer.setVisibility(View.GONE);
                        ivProductState.setImageResource(R.drawable.iv_transfer_finished);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @OnClick({R.id.clickLookNativeProduct, R.id.clickLookRefund, R.id.clickLookProtocol, R.id.clickLookProtocol2,R.id.btnTransfer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clickLookNativeProduct:
                if (borrowId == -1) return;
                String productTypeStr = info.getProductType();
                String productStr = info.getProduct();
                try{
                    //产品类别：0、散标，1、定存标，2-保理，3-个人借贷
                    int product = Integer.parseInt(productStr);

                    //产品类别：0、普通标，1.魔方 2.车贷 3.沈阳众筹
                    int productType = Integer.parseInt(productTypeStr);

                    if(productType == 3){
                        PresellProductActivity.goPresellProductActivity(
                                mContext, "" + borrowId);
                    }else if (productType == 0){
//                        RegularProductActivity.goRegularProductActivity(
//                                mContext, "" + borrowId,
//                                -1, 0, -1,""+0);
                        NewRegularProductActivity.goNewRegularProductActivity(
                                mContext, borrowId,
                                0, "", false,"");
                    }else if(product == 3){
                        NewRegularProductActivity.goNewRegularProductActivity(
                                mContext, borrowId,
                                0, "", false,"");
//                        RegularProductActivity.goRegularProductActivity(
//                                mContext, "" + borrowId,
//                                -1, 3, -1,""+0);
                    }else if (productType == 0){
//                        RegularProductActivity.goRegularProductActivity(
//                                mContext, "" + borrowId,
//                                -1, 0, -1,""+0);
                        NewRegularProductActivity.goNewRegularProductActivity(
                                mContext, borrowId,
                                0, "", false,"");
                    }else if (product == 2){
                        NewRegularProductActivity.goNewRegularProductActivity(
                                mContext, borrowId,
                                0, "", false,"");
//                        RegularProductActivity.goRegularProductActivity(
//                                mContext, "" + borrowId,
//                                -1, 2, -1,""+0);
//                        NewRegularProductActivity.goNewRegularProductActivity();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.clickLookRefund:
                if (borrowId != -1)
                    TransferRefundActivity.goTransferRefund(mContext, ""+borrowId);
                break;
            case R.id.clickLookProtocol:
                if (protocolInfo != null) {
                    String title = TextUtils.isEmpty(protocolInfo.getTitle()) ? getResources().getString(R.string.app_name) : protocolInfo.getTitle();
                    String url = protocolInfo.getUrl();
                    if (TextUtils.isEmpty(url)) return;
                    gotoWeb(url, title);
                }
                break;
            case R.id.clickLookProtocol2:
                if (protocolInfo2 != null) {
                    String title = TextUtils.isEmpty(protocolInfo2.getTitle()) ? getResources().getString(R.string.app_name) : protocolInfo2.getTitle();
                    String url = protocolInfo2.getUrl();
                    if (TextUtils.isEmpty(url)) return;
                    gotoWeb(url, title);
                }
                break;
            case R.id.btnTransfer:
                if (TextUtils.isEmpty(recordId)) return;
                if (info == null)   return;
                String title = TextUtils.isEmpty(info.getTitle()) ? "" : info.getTitle();
                String tenderMoney = TextUtils.isEmpty(info.getTenderMoney()) ? "" : info.getTenderMoney();
                String haveReturnMoney = TextUtils.isEmpty(info.getHaveReturnMoney()) ? "0" : info.getHaveReturnMoney();
                String stayReturnMoney = TextUtils.isEmpty(info.getStayReturnMoney()) ? "0" : info.getStayReturnMoney();
                double transferMinRate = info.getTransferMinRate();
                TransferProductExecuteNowActivity.gotoTransferProductExecuteNowActivity(mContext,recordId,""+borrowId,title,tenderMoney,haveReturnMoney,stayReturnMoney,transferMinRate,accountType);
                break;
        }
    }
}
