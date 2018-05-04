package cn.vpfinance.vpjr.module.product.invest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.yintong.pay.utils.Md5Algorithm;

import org.json.JSONObject;

import java.math.BigDecimal;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.setting.PasswordChangeActivity;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.model.BuyResult;
import cn.vpfinance.vpjr.model.FundOverInfo;
import cn.vpfinance.vpjr.model.PresellProductInfo;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.product.success.ProductInvestSuccessActivity;
import cn.vpfinance.vpjr.module.trade.RechargeActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;

/**
 */
public class PresellProductInvestActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibAddCount;
    private ImageButton ibSubCount;
    private EditText etBuyCount;
    private String strCount;
    private ActionBarLayout titleBar;
    private ImageView ivPresellProdutcInvest;
    private TextView tvPresellProductName;
    private TextView tvMinRate;
    private TextView tvMaxRate;
    private TextView tvMonth;
    private TextView tvProductTotalMoney;
    private TextView tvCount;
    private TextView tvBalance;
    private TextView tvUnitPrice;
    private TextView tvPay;
    private Button btnInvest;
    public static final String PID = "pid";
    private HttpService mHttpService;
    private PresellProductInfo presellProductInfo;
    private User user;
    private TextView tvPresellState;
    private TextInputDialogFragment tidf;
    private int canBuyCount;
    private float unit = 0;
    private float balance = 0;
    private float total;//总共
    private float part;//已购
    private float payMoney = 0;
    private String pid = "";
    private TextView tvProtocal;
    private CheckBox mCheckBox;
    private LinearLayout ll_change_checkbox;

    private void initFind() {
        ibAddCount = ((ImageButton) findViewById(R.id.ibAddCount));
        ibSubCount = ((ImageButton) findViewById(R.id.ibSubCount));
        etBuyCount = ((EditText) findViewById(R.id.etBuyCount));
        titleBar = ((ActionBarLayout) findViewById(R.id.titleBar));
        ivPresellProdutcInvest = ((ImageView) findViewById(R.id.ivPresellProdutcInvest));
        tvPresellProductName = ((TextView) findViewById(R.id.tvPresellProductName));
        tvMinRate = ((TextView) findViewById(R.id.tvMinRate));
        tvMaxRate = ((TextView) findViewById(R.id.tvMaxRate));
        tvMonth = ((TextView) findViewById(R.id.tvMonth));
        tvProductTotalMoney = ((TextView) findViewById(R.id.tvProductTotalMoney));
        tvCount = ((TextView) findViewById(R.id.tvCount));
        tvBalance = ((TextView) findViewById(R.id.tvBalance));
        tvUnitPrice = ((TextView) findViewById(R.id.tvUnitPrice));
        tvPay = ((TextView) findViewById(R.id.tvPay));
        btnInvest = ((Button) findViewById(R.id.btnInvest));
        tvPresellState = ((TextView) findViewById(R.id.tvPresellState));

        tvProtocal = ((TextView) findViewById(R.id.tvProtocal));
        mCheckBox = ((CheckBox) findViewById(R.id.mCheckBox));
        ll_change_checkbox = ((LinearLayout) findViewById(R.id.ll_change_checkbox));

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBox.isChecked()){
                    btnInvest.setEnabled(true);
                }else{
                    btnInvest.setEnabled(false);
                }
            }
        });
        tvProtocal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Presell_protocol;
                String method = ServiceCmd.getMethodName(cmdId);
                String url = HttpService.getServiceUrl(method);
                gotoWeb(url, "合同范本");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presell_product_invest);
        mHttpService = new HttpService(this,this);
        mHttpService.getUserInfo();
        initFind();
        initView();
        Intent intent = getIntent();
        if (intent != null){
            String pid = intent.getStringExtra(PID);
            if (!TextUtils.isEmpty(pid)){
                mHttpService.getPresellProductInfo(pid);
            }
        }
        user = DBUtils.getUser(this);
        if (user == null){
            Toast.makeText(this,"请登录.",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        mHttpService.getFundOverInfo("" + user.getUserId(), Constant.AccountLianLain);
    }

    protected void initView() {
        titleBar.setHeadBackVisible(View.VISIBLE);
        ibAddCount.setOnClickListener(this);
        ibSubCount.setOnClickListener(this);
        btnInvest.setOnClickListener(this);
        ll_change_checkbox.setOnClickListener(this);

        etBuyCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String countStr = s.toString();
                if (!TextUtils.isEmpty(countStr)) {
                    try {
                        int num = Integer.parseInt(countStr);
                        if (unit <= 0)  return;
                        if (canBuyCount != 0 && num <= canBuyCount){
                            tvPay.setText(new BigDecimal(num * unit).toString());
                        }else{
                            etBuyCount.setText(""+canBuyCount);
                            Utils.Toast(PresellProductInvestActivity.this,"超过可购份额");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    tvPay.setText("0");
                }
            }
        });
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            mHttpService.onGetUserInfo(json, user);
            if (user != null) {
                this.user = user;
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_PresellProductInfo.ordinal()) {
            if (json == null) {
                return;
            }
            presellProductInfo = mHttpService.onGetPresellProductInfo(json);

            if (presellProductInfo != null){
                if (!TextUtils.isEmpty(presellProductInfo.borrowId)){
                    pid = presellProductInfo.borrowId;
                }

                String title = presellProductInfo.borrowTitle;
                if (!TextUtils.isEmpty(title)){
                    titleBar.setTitle(title);
                    tvPresellProductName.setText(title);
                }

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(false)
                        .cacheOnDisk(true)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                if (!TextUtils.isEmpty(presellProductInfo.imageUrl)) {
                    ImageLoader.getInstance().displayImage(presellProductInfo.imageUrl, ivPresellProdutcInvest, options);
                }
                if (!TextUtils.isEmpty(presellProductInfo.loanUnit)){
                    tvUnitPrice.setText("单价:"+presellProductInfo.loanUnit+"元/份");
                }

                tvMinRate.setText(presellProductInfo.minRate+"%");
                tvMaxRate.setText("预期可超过"+presellProductInfo.maxRate+"%");
                tvMonth.setText("最长"+presellProductInfo.month+"个月(3个月后可转让)");
                tvProductTotalMoney.setText("¥"+presellProductInfo.issueLoan);
                tvPay.setText(presellProductInfo.loanUnit);

                if (!TextUtils.isEmpty(presellProductInfo.issueLoan) && !TextUtils.isEmpty(presellProductInfo.total_tend_money) && !TextUtils.isEmpty(presellProductInfo.loanUnit)){
                    try{
                        total = Float.parseFloat(presellProductInfo.issueLoan);
                        part = Float.parseFloat(presellProductInfo.total_tend_money);
                        unit = Float.parseFloat(presellProductInfo.loanUnit);
                        canBuyCount = (int)((total - part) / unit);
                        tvCount.setText(canBuyCount + "份");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (!TextUtils.isEmpty(presellProductInfo.borrowStatus)){
                    String borrowStatus = presellProductInfo.borrowStatus;
                    String stateStr = "";
                    stateStr = borrowStatus.equals("1") ? getString(R.string.productState1) :
                            borrowStatus.equals("2") ? "进行中" :
                                    borrowStatus.equals("3") ? getString(R.string.productState3) :
                                            borrowStatus.equals("4") ? getString(R.string.productState4) : "";
                    tvPresellState.setText(stateStr);

                    if ("2".equals(borrowStatus)){
                        btnInvest.setEnabled(true);
                    }else{
                        btnInvest.setEnabled(false);
                    }
                }
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_FundOverView.ordinal()) {
            FundOverInfo fundOverInfo = mHttpService.onGetFundOverInfo(json);
            /*可用余额*/
            String mCanBuyMoney = fundOverInfo.getCashBalance();
            try{
                balance = Float.parseFloat(mCanBuyMoney);
            }catch (Exception e){
                e.printStackTrace();
            }
            tvBalance.setText("" + mCanBuyMoney);
        }
        if (reqId == ServiceCmd.CmdId.CMD_PLANK.ordinal()) {
            if (json != null) {

                ArrayMap<String,String> map = new ArrayMap<String,String>();
                map.put("type","Regular");
                map.put("money", "" + RechargeActivity.getStatisticsMoney(payMoney));

                BuyResult res = mHttpService.onPlankFinish(json,false);
                if (res != null) {
                    String redPacketState = json.optString(ProductInvestSuccessActivity.REDPACKETSTATE);
                    int status = res.getStatus();
                    String desc = res.getStatusDesc();
                    map.put("status","" + status);
                    if ("投标成功".equals(desc)) {
                        if (tidf != null) {
                            tidf.dismiss();
                        }
                        //
                        Intent intent = new Intent(this,ProductInvestSuccessActivity.class);
                        intent.putExtra(ProductInvestSuccessActivity.INVESTMONEY, String.format("%.2f", payMoney));
                        intent.putExtra(ProductInvestSuccessActivity.PID,"" + pid);
                        intent.putExtra(ProductInvestSuccessActivity.REDPACKETSTRURL,"" + res.getRedpacketstrurl());
                        intent.putExtra(ProductInvestSuccessActivity.REDPACKETCOUNT, "" + res.getRedPacketCount());
                        intent.putExtra(ProductInvestSuccessActivity.PRODUCT_TYPE, "3");
                        if (!TextUtils.isEmpty(redPacketState)){
                            intent.putExtra(ProductInvestSuccessActivity.REDPACKETSTATE,redPacketState);
                        }
                        startActivity(intent);
                        finish();
                    }

                    if (!"投标成功".equals(desc)){
                        Utils.Toast(this, desc);
                    }
                }
                MobclickAgent.onEvent(this, "Buy", map);
            }
        }
    }
    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        if (reqId == ServiceCmd.CmdId.CMD_PresellProductInfo.ordinal()) {
            titleBar.setTitle("沈阳众筹项目");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = DBUtils.getUser(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnInvest:
                nowInvest();
                break;
            case R.id.ll_change_checkbox:
                mCheckBox.setChecked(!mCheckBox.isChecked());
                if (mCheckBox.isChecked()){
                    btnInvest.setEnabled(true);
                }else{
                    btnInvest.setEnabled(false);
                }
                break;
            case R.id.ibSubCount:
                strCount = etBuyCount.getText().toString();
                try {
                    int count = Integer.parseInt(strCount);
                    if (count > 1){
                        count--;
                        etBuyCount.setText(""+count);
                        if (unit != 0){
                            tvPay.setText(new BigDecimal(count*unit).toString());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.ibAddCount:
                strCount = etBuyCount.getText().toString();
                try {
                    int count = Integer.parseInt(strCount);
                    if (canBuyCount > count){
                        count++;
                        etBuyCount.setText(""+count);
                        if (unit != 0){
                            tvPay.setText(new BigDecimal(count*unit).toString());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
    private void nowInvest() {
        if (!AppState.instance().logined()) {
            startActivity(new Intent(PresellProductInvestActivity.this, LoginActivity.class));
            return;
        }
        String payMoneyStr = tvPay.getText().toString();
        if (TextUtils.isEmpty(payMoneyStr)) {
            Toast.makeText(PresellProductInvestActivity.this, "请输入购买金额", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            payMoney = Float.parseFloat(payMoneyStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (balance != 0 && total != 0 && part != 0){
            if (payMoney > balance){
                Toast.makeText(PresellProductInvestActivity.this, "可用余额不足,请充值", Toast.LENGTH_SHORT).show();
                return;
            }
            if (payMoney > (total-part)){
                if (payMoney > balance){
                    Toast.makeText(PresellProductInvestActivity.this, "输入的购买金额大于该产品的可购金额", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        if (payMoney <= 0){
            Toast.makeText(PresellProductInvestActivity.this, "购买金额必须大于零", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {
            if (!user.getHasTradePassword()) {
                Intent intent = new Intent(PresellProductInvestActivity.this, PasswordChangeActivity.class);
                intent.putExtra(PasswordChangeActivity.EXTRA_KEY_INDEX, 1);
                AlertDialogUtils.showAlertDialog(PresellProductInvestActivity.this, "您未设置交易密码", "取消", "去设置", intent);
                return;
            }
        }

        tidf = TextInputDialogFragment.newInstance("交易密码", "请输入交易密码", true);
        tidf.setOnTextConfrimListener(new TextInputDialogFragment.onTextConfrimListener()
        {
            @Override
            public boolean onTextConfrim(String value)
            {
                if(value!=null)
                {
                    if(presellProductInfo!=null)
                    {
                        String uid = AppState.instance().getSessionCode();
                        Md5Algorithm md5 = Md5Algorithm.getInstance();
                        value = md5.md5Digest(value.getBytes());
                        mHttpService.plank("" + presellProductInfo.borrowId, "" +payMoney, uid, value, false, "","","");
                    }
                }
                return false;
            }
        });
        tidf.show(getSupportFragmentManager(), "inputPwd");
    }
}
