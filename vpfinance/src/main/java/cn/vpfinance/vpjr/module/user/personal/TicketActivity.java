package cn.vpfinance.vpjr.module.user.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 我的优惠券
 */
public class TicketActivity extends BaseActivity {


    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    private Context mContext;
    private HttpService mHttpService;

    public static final String VOUCHER_COUNT = "voucher_count";
    public static final String ADDRATE_COUNT = "addrate_count";
    public static final String PRESELL_COUNT = "presell_count";
    private int accountType = Constant.AccountBank;

    public static void goThis(Context context,int accountType, String voucher_count, String addrate_count, String presell_count){
        Intent intent = new Intent(context,TicketActivity.class);
        intent.putExtra(Constant.AccountType, accountType);
        intent.putExtra(VOUCHER_COUNT, voucher_count);
        intent.putExtra(ADDRATE_COUNT, addrate_count);
        intent.putExtra(PRESELL_COUNT, presell_count);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        ButterKnife.bind(this);

        mContext = this;
        mHttpService = new HttpService(this, this);
        mTitleBar.setTitle("我的优惠券").setHeadBackVisible(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null){
            String voucher_count = intent.getStringExtra(VOUCHER_COUNT);
            String addrate_count = intent.getStringExtra(ADDRATE_COUNT);
            String presell_count = intent.getStringExtra(PRESELL_COUNT);
            ((TextView) ButterKnife.findById(this, R.id.voucherCount)).setText(voucher_count+"张券可用");
            ((TextView) ButterKnife.findById(this, R.id.addrateCount)).setText(addrate_count+"张券可用");
            ((TextView) ButterKnife.findById(this, R.id.presellCount)).setText(presell_count+"张券可用");
        }
    }

    @OnClick({R.id.clickVoucherTicket, R.id.clickAddrateTicket, R.id.clickPresellTicket})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clickVoucherTicket:
                gotoActivity(VoucherV3Activity.class);
                break;
            case R.id.clickAddrateTicket:
                gotoActivity(AddrateTicketActivity.class);
                break;
            case R.id.clickPresellTicket:
                gotoActivity(PresellTicketActivity.class);
                break;
        }
    }
}
