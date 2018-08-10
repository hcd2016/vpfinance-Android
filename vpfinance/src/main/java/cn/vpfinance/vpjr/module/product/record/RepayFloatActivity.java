package cn.vpfinance.vpjr.module.product.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.model.RepayFloatModel;
import cn.vpfinance.vpjr.module.product.record.adapter.RepayFloatAdapter;
import cn.vpfinance.vpjr.util.GsonUtil;

/**
 * 浮动计息回款计划
 */
public class RepayFloatActivity extends BaseActivity {

    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<RepayFloatModel.RepayPlansBean> list;
    private RepayFloatAdapter repayFloatAdapter;
    private HttpService httpService;
    private String inRecordId;
    private RepayFloatModel repayFloatModel;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repay_float);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("还款计划").setHeadBackVisible(View.VISIBLE);
        inRecordId = getIntent().getStringExtra("inRecordId");
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repayFloatAdapter = new RepayFloatAdapter(list);
        initHeaderView(repayFloatAdapter);
        recyclerView.setAdapter(repayFloatAdapter);
        requestData();
    }

    private void requestData() {
        httpService = new HttpService(this, this);
        httpService.getRepayPlanFloat(inRecordId);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId, json);
        if (reqId == ServiceCmd.CmdId.CMD_REPAY_PLAN_FLOAT.ordinal()) {
            repayFloatModel = GsonUtil.modelParser(json.toString(), RepayFloatModel.class);
            setViewData(repayFloatModel);
        }
    }

    private void setViewData(RepayFloatModel repayFloatModel) {
        setHeaderViewData(repayFloatModel);
        setListData(repayFloatModel);
    }

    private void setListData(RepayFloatModel repayFloatModel) {
        List<RepayFloatModel.RepayPlansBean> repayPlans = repayFloatModel.getRepayPlans();
        if (null != repayPlans && repayPlans.size() != 0) {
            list.addAll(repayPlans);
            repayFloatAdapter.notifyDataSetChanged();
        }
    }

    //头view数据
    private void setHeaderViewData(RepayFloatModel repayFloatModel) {
        TextView tvMostRepayAmount = headerView.findViewById(R.id.tv_most_repay_amount);
        RelativeLayout rlMostRepayContainer = headerView.findViewById(R.id.rl_most_repay_container);
        TextView tvLastRepayDate = headerView.findViewById(R.id.tv_last_repay_date);
        TextView tvRealRepayAmount = headerView.findViewById(R.id.tv_real_repay_amount);
        TextView tvRealRepayDate = headerView.findViewById(R.id.tv_real_repay_date);
        TextView tvWarningDesc = headerView.findViewById(R.id.tv_warning_desc);
        RelativeLayout rlLastRepayAmountContainer = headerView.findViewById(R.id.rl_last_repay_date_container);
        RelativeLayout rlLastRepayDateContainer = headerView.findViewById(R.id.tv_real_repay_amount_container);
        RelativeLayout rlRealRepayDateContainer = headerView.findViewById(R.id.rl_real_repay_date_container);
        RelativeLayout rlWarningDescContainer = headerView.findViewById(R.id.rl_warning_desc_container);
        ImageView ivWarning = headerView.findViewById(R.id.iv_warning);

        tvMostRepayAmount.setText(repayFloatModel.getMaxRepayMoney());
        tvLastRepayDate.setText(repayFloatModel.getMaxRepayMoney());
        tvRealRepayAmount.setText(repayFloatModel.getRepayMoney());
        tvRealRepayDate.setText(repayFloatModel.getRepayDate());

        //提示处理
        String content = repayFloatModel.getFlowInvestReminder();
        String target1 = repayFloatModel.getRate();
        String target2 = repayFloatModel.getGraceRate();
        DifColorTextStringBuilder difColorTextStringBuilder = new DifColorTextStringBuilder();
        difColorTextStringBuilder.setContent(content)
                .setHighlightContent(target1,R.color.red_text)
                .setHighlightContent(target2,R.color.red_text)
                .setHighlightContent("了解详情>>",R.color.red_text)
                .setHighlightContent("了解详情>>", new MyClickableSpan() {
                    @Override
                    public void onClick(View widget) {
//                        todo
                        Utils.Toast("点击了了解详情");
                    }
                })
                .setTextView(tvWarningDesc)
                .create();
    }

    private void initHeaderView(RepayFloatAdapter adapter) {
        headerView = View.inflate(this, R.layout.header_repay_float, null);
        adapter.addHeaderView(headerView);
    }

    /**
     * 开启本页
     *
     * @param inRecordId //投资记录id
     */
    public static void startRepayFloatActivity(Context context, String inRecordId) {
        Intent intent = new Intent(context, RepayFloatActivity.class);
        intent.putExtra("inRecordId", inRecordId);
        context.startActivity(intent);
    }
}
