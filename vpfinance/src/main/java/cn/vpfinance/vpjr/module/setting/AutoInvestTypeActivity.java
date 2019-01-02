package cn.vpfinance.vpjr.module.setting;

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
import android.widget.Switch;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.AutoInvestSettingBean;

/**
 * 自动投标设置 - 投标种类
 * Created by zzlz13 on 2017/7/31.
 */

public class AutoInvestTypeActivity extends BaseActivity {


    @Bind(R.id.title_bar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.switchOpen)
    Switch switchOpen;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private int black;
    private int gray;
    private MyAdapter myAdapter;
    private String loanTypeValue;
    //1、车贷宝，2、消费宝，8、供应链，10、企业贷，11、珠宝贷，12、融租宝,13个人贷,14智存出借

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_invest_type);
        ButterKnife.bind(this);
        mTitleBar.reset().setTitle("投标种类").setHeadBackVisible(View.VISIBLE);

        ButterKnife.findById(this, R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAction();
            }
        });
        black = getResources().getColor(R.color.text_1c1c1c);
        gray = getResources().getColor(R.color.text_cccccc);

        loadDate();
    }

    protected void loadDate() {
        Intent intent = getIntent();
        if (intent != null) {
            loanTypeValue = intent.getStringExtra(AutoInvestSettingActivity.ARGS_INVEST_TYPE_VALUE);
            ArrayList<AutoInvestSettingBean.OptionsBean.LoanTypeBean> investTypeList = (ArrayList<AutoInvestSettingBean.OptionsBean.LoanTypeBean>) intent.getSerializableExtra(AutoInvestSettingActivity.ARGS_INVEST_TYPE);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            myAdapter = new MyAdapter(investTypeList, loanTypeValue);
            mRecyclerView.setAdapter(myAdapter);
            if ("0".equals(loanTypeValue)) {//不限
                switchOpen.setChecked(true);
            } else {
                switchOpen.setChecked(false);
            }
        }
        switchOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAdapter != null) {
                    if (switchOpen.isChecked()) {
                        myAdapter.updateState("0");//0 不限
                    } else {
                        myAdapter.updateState("-1");//-1代表默认
                    }
                }
            }
        });
    }

    private void finishAction() {
        String result = "";
        if (myAdapter != null || mRecyclerView != null) {
            if (myAdapter.unlimited) {
                result = "0";
            } else {
                Iterator<String> iterator = myAdapter.codes.iterator();
                while (iterator.hasNext()) {
                    result = result + "," + iterator.next();
                }
            }
        }
        if (TextUtils.isEmpty(result)) {
            Utils.Toast(Constant.chooseAtLeastOne);
            return;
        }
        setResult(200, getIntent().putExtra(AutoInvestSettingActivity.ARGS_INVEST_TYPE_VALUE, result));
        finish();
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.LoanTypeVH> {

        private ArrayList<AutoInvestSettingBean.OptionsBean.LoanTypeBean> data;
        public boolean unlimited;
        private HashSet<CheckBox> checkBoxes = new HashSet<>();
        public HashSet<String> codes = new HashSet<>();

        public void updateState(String value) {
            if ("0".equals(value)) {
                unlimited = true;
            } else if ("-1".equals(value)) {
                unlimited = false;
                for (int i = 0; i < getItemCount(); i++) {
                    View view = mRecyclerView.getChildAt(i);
                    MyAdapter.LoanTypeVH viewHolder = (MyAdapter.LoanTypeVH) mRecyclerView.getChildViewHolder(view);
                    if (!TextUtils.isEmpty(viewHolder.code)) {
                        codes.add(viewHolder.code);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public MyAdapter(ArrayList<AutoInvestSettingBean.OptionsBean.LoanTypeBean> data, String value) {
            this.data = data;
            if ("0".equals(value)) {
                unlimited = true;
            } else {
                unlimited = false;
                String[] split = value.split(",");
                for (String s : split) {
                    if (!TextUtils.isEmpty(s) && !s.equals("0") && !s.equals("-1")) {
                        codes.add(s);
                    }
                }
            }
        }

        @Override
        public LoanTypeVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LoanTypeVH(LayoutInflater.from(AutoInvestTypeActivity.this).inflate(R.layout.item_auto_invest_setting, parent, false));
        }

        @Override
        public void onBindViewHolder(final LoanTypeVH holder, int position) {
            AutoInvestSettingBean.OptionsBean.LoanTypeBean bean = data.get(position);
            holder.code = bean.key;
            holder.tvTitle.setText(bean.value);
            holder.tvTitle.setTextColor(unlimited ? gray : black);

            if (unlimited) {//不限全部false
                holder.checkBox.setChecked(false);
            } else {//没有不限则判断cb是否勾选
                Iterator<String> iterator = codes.iterator();
                holder.checkBox.setChecked(false);
                while (iterator.hasNext()) {
                    if (holder.code.equals(iterator.next())) {
                        holder.checkBox.setChecked(true);
                    }
                }
            }
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (unlimited) return;
                    boolean checked = holder.checkBox.isChecked();
                    if (checked) {//检查是否只剩最后一个选择, 如果只有一个则不允许取消勾选
                        if (codes.size() <= 1) {
                            Utils.Toast(Constant.chooseAtLeastOne);
                        } else {
                            codes.remove(holder.code);
                            holder.checkBox.setChecked(false);
                        }
                    } else {
                        if (!TextUtils.isEmpty(holder.code)) {
                            codes.add(holder.code);
                        }
                        holder.checkBox.setChecked(true);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class LoanTypeVH extends RecyclerView.ViewHolder {
            LinearLayout rootView;
            TextView tvTitle;
            CheckBox checkBox;
            String code;

            public LoanTypeVH(View itemView) {
                super(itemView);
                rootView = (LinearLayout) itemView.findViewById(R.id.rootView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
                if (!checkBoxes.contains(checkBox)) {
                    checkBoxes.add(checkBox);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
