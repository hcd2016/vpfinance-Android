package cn.vpfinance.vpjr.module.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.adapter.AutoSettingAdapter;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.AutoInvestSettingBean;

/**
 * 自动投标设置 - 优惠券
 * Created by zzlz13 on 2017/7/31.
 */

public class AutoInvestCouponActivity extends BaseActivity {


    @Bind(R.id.title_bar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private CouponsAdapter myAdapter;
    private String couponValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_coupon);
        ButterKnife.bind(this);
        mTitleBar.reset().setTitle("优惠券").setHeadBackVisible(View.VISIBLE);

        ButterKnife.findById(this, R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAction();
            }
        });
        loadDate();
    }

    @Override
    protected void loadDate() {
        Intent intent = getIntent();
        if (intent != null) {
            couponValue = intent.getStringExtra(AutoInvestSettingActivity.ARGS_VOUCHER_VALUE);
            ArrayList<AutoInvestSettingBean.OptionsBean.CouponsBean> couponsList = (ArrayList<AutoInvestSettingBean.OptionsBean.CouponsBean>) intent.getSerializableExtra(AutoInvestSettingActivity.ARGS_VOUCHER);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            myAdapter = new CouponsAdapter(AutoInvestCouponActivity.this, couponsList, couponValue);
            mRecyclerView.setAdapter(myAdapter);
        }
    }

    private void finishAction() {
        String result = "";
        if (myAdapter != null || mRecyclerView != null) {
            int itemCount = myAdapter.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                View view = mRecyclerView.getChildAt(i);
                CouponsAdapter.MyViewHolder viewHolder = (CouponsAdapter.MyViewHolder) mRecyclerView.getChildViewHolder(view);
                if (viewHolder.checkBox.isChecked()) {
                    if (TextUtils.isEmpty(result)) {
                        result = viewHolder.code;
                    } else {
                        result = result + "," + viewHolder.code;
                    }
                }
            }
        }
        setResult(200, getIntent().putExtra(AutoInvestSettingActivity.ARGS_VOUCHER_VALUE, result));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.MyViewHolder> {

        private ArrayList<AutoInvestSettingBean.OptionsBean.CouponsBean> data;
        private String[] checkCode;
        private Context mContext;

        public CouponsAdapter(Context context, ArrayList<AutoInvestSettingBean.OptionsBean.CouponsBean> data, String value) {
            this.data = data;
            this.checkCode = value.split(",");
            mContext = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_auto_invest_setting, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            AutoInvestSettingBean.OptionsBean.CouponsBean bean = data.get(position);
            holder.code = bean.key;
            holder.tvTitle.setText(bean.value);

            for (String s : checkCode) {
                if (s.equals(bean.key)) {
                    holder.checkBox.setChecked(true);
                    break;
                }
            }
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = holder.checkBox.isChecked();
                    holder.checkBox.setChecked(!checked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout rootView;
            public TextView tvTitle;
            public CheckBox checkBox;
            public String code;

            public MyViewHolder(View itemView) {
                super(itemView);
                rootView = (LinearLayout) itemView.findViewById(R.id.rootView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            }
        }
    }
}
