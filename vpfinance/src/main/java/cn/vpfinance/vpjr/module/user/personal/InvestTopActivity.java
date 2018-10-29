package cn.vpfinance.vpjr.module.user.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.InvestTopBean;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.user.OtherPersonalCardActivity;
import cn.vpfinance.vpjr.util.Common;

/**
 * Created by Administrator on 2016/6/23.
 */
public class InvestTopActivity extends BaseActivity {

    @Bind(R.id.invest_top_rv)
    ListView           mInvestTopRv;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.textview)
    TextView           mTextview;
    @Bind(R.id.titleBar)
    ActionBarLayout    mTitleBar;
    private boolean isInvest = false;
    private             HttpService   mHttpService;
    private             InvestTopBean mInvestTopBean;
    private             myAdapter     mMyAdapter;
    public static final String        TOP_DES = "top_des";
    private String mNumber;
    private int accountType = Constant.AccountBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.treasure_top_fragment);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            mNumber = intent.getStringExtra(TOP_DES);
            accountType = intent.getIntExtra(Constant.AccountType,Constant.AccountBank);
        }
        init();
    }

    public static void goThis(Context context,int accountType, String mNumber){
        Intent intent = new Intent(context,InvestTopActivity.class);
        intent.putExtra(Constant.AccountType, accountType);
        intent.putExtra(TOP_DES, mNumber);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void init() {

        mTitleBar.setTitle("风云榜").setHeadBackVisible(View.VISIBLE);
        mHttpService = new HttpService(this, this);
        mHttpService.getInvestTop(accountType,"1");

        mRefresh.setColorSchemeColors(Color.RED);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHttpService.getInvestTop(accountType,"1");
                mRefresh.setRefreshing(true);
            }
        });

        mMyAdapter = new myAdapter();
        mInvestTopRv.setAdapter(mMyAdapter);


    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        mTextview.setVisibility(View.GONE);
        mRefresh.setRefreshing(false);
        if (reqId == ServiceCmd.CmdId.CMD_Invest_Top.ordinal()) {
            mInvestTopBean = mHttpService.onGetInvestTop(json);
            if(mInvestTopBean == null) return;
            if ("1".equals(mInvestTopBean.isTender)) {
                isInvest = true;
            } else {
                isInvest = false;
            }

            final List<InvestTopBean.ListEntity> data = mInvestTopBean.list;
            mMyAdapter.setData(data);
            mInvestTopRv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    position = position - 1;
                    if (position >= 0) {
                        Intent intent = new Intent(InvestTopActivity.this, OtherPersonalCardActivity.class);
                        intent.putExtra(OtherPersonalCardActivity.UID, data.get(position).userId);
                        gotoActivity(intent);
                    }
                }
            });

        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        mRefresh.setRefreshing(false);
        Utils.Toast(this, "网络错误,请检查您的网络连接");
    }

    class myAdapter extends BaseAdapter {
        private List<InvestTopBean.ListEntity> mDatas;

        public void setData(List<InvestTopBean.ListEntity> data) {
            mDatas = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mDatas == null || mDatas.size() == 0) {
                return 0;
            }
            return mDatas.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position + 1);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            OtherViewHolder otherViewHolder = null;
            OtherViewHolder2 otherViewHolder2 = null;
            if (getItemViewType(position) == 0) {
                if (!isInvest) {
                    if (convertView == null) {
                        otherViewHolder = new OtherViewHolder();
                        convertView = View.inflate(InvestTopActivity.this, R.layout.top_listview_header, null);
                        otherViewHolder.username = (TextView) convertView.findViewById(R.id.username);
                        otherViewHolder.userHead = (ImageView) convertView.findViewById(R.id.userHead);
                        otherViewHolder.invest_btn = (Button) convertView.findViewById(R.id.invest_btn);
                        convertView.setTag(otherViewHolder);
                    } else {
                        otherViewHolder = (OtherViewHolder) convertView.getTag();
                    }

                    otherViewHolder.username.setText(mInvestTopBean.name);
                    ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + mInvestTopBean.head, otherViewHolder.userHead);
                    otherViewHolder.invest_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InvestTopActivity.this, MainActivity.class);
                            intent.putExtra(MainActivity.SWITCH_TAB_NUM, 1);
                            startActivity(intent);
                            finish();
                        }
                    });

                } else {
                    if (convertView == null) {
                        otherViewHolder2 = new OtherViewHolder2();
                        convertView = View.inflate(InvestTopActivity.this, R.layout.yield_top_listview_header, null);
                        otherViewHolder2 = new OtherViewHolder2();
                        otherViewHolder2.username = (TextView) convertView.findViewById(R.id.username);
                        otherViewHolder2.level = (TextView) convertView.findViewById(R.id.level);
                        otherViewHolder2.syn_yield = (TextView) convertView.findViewById(R.id.syn_yield);
                        otherViewHolder2.userHead = (ImageView) convertView.findViewById(R.id.userHead);
                        otherViewHolder2.strength_des = (TextView) convertView.findViewById(R.id.strength_des);
                        otherViewHolder2.tv_flag = (TextView) convertView.findViewById(R.id.tv_flag);
                        convertView.setTag(otherViewHolder2);
                    } else {
                        otherViewHolder2 = (OtherViewHolder2) convertView.getTag();
                    }
                    try {
                        otherViewHolder2.username.setText(mInvestTopBean.name);
                        otherViewHolder2.level.setText(mInvestTopBean.level);
                        ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + mInvestTopBean.head, otherViewHolder2.userHead);

                        DecimalFormat dcmFmt = new DecimalFormat("#,###.00");
                        double db = Double.parseDouble(mInvestTopBean.sumTenderMoney);
                        otherViewHolder2.syn_yield.setText(dcmFmt.format(db) + "");
                        otherViewHolder2.tv_flag.setText("总投资(元)");
                        otherViewHolder2.strength_des.setText("击败了" + mNumber + "的投资达人");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                position = position - 1;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(InvestTopActivity.this, R.layout.invest_top_item, null);
                    viewHolder.username = (TextView) convertView.findViewById(R.id.username);
                    viewHolder.level = (TextView) convertView.findViewById(R.id.level);
                    viewHolder.syn_yield = (TextView) convertView.findViewById(R.id.syn_yield);
                    viewHolder.userHead = (ImageView) convertView.findViewById(R.id.userHead);
                    viewHolder.iv_top = (ImageView) convertView.findViewById(R.id.iv_top);
                    viewHolder.invest_top_num = (TextView) convertView.findViewById(R.id.invest_top_num);
                    viewHolder.tv_flag = (TextView) convertView.findViewById(R.id.tv_flag);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                try {
                    viewHolder.username.setText(mDatas.get(position).userName);
                    ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + mDatas.get(position).head, viewHolder.userHead);
                    viewHolder.level.setText(mDatas.get(position).level);

                    String tenderMoney = mDatas.get(position).tenderMoney;
                    DecimalFormat dcmFmt = new DecimalFormat("#,###.00");
                    double db = Double.parseDouble(tenderMoney);
                    viewHolder.syn_yield.setText(dcmFmt.format(db) + "");
                    viewHolder.tv_flag.setText("总投资(元)");



                viewHolder.invest_top_num.setText(9 == position ? "10" : "0" + (position + 1));
                switch (position) {
                    case 0:
                        viewHolder.invest_top_num.setTextColor(Color.parseColor("#f5a623"));
                        viewHolder.iv_top.setVisibility(View.VISIBLE);
                        viewHolder.iv_top.setImageResource(R.drawable.crown_one);
                        viewHolder.syn_yield.setTextColor(Color.parseColor("#ed2a31"));
                        break;
                    case 1:
                        viewHolder.invest_top_num.setTextColor(Color.parseColor("#999999"));
                        viewHolder.iv_top.setImageResource(R.drawable.crown_two);
                        viewHolder.iv_top.setVisibility(View.VISIBLE);
                        viewHolder.syn_yield.setTextColor(Color.parseColor("#ed2a31"));
                        break;
                    case 2:
                        viewHolder.invest_top_num.setTextColor(Color.parseColor("#c15205"));
                        viewHolder.iv_top.setImageResource(R.drawable.crown_three);
                        viewHolder.iv_top.setVisibility(View.VISIBLE);
                        viewHolder.syn_yield.setTextColor(Color.parseColor("#ed2a31"));
                        break;
                    default:
                        viewHolder.invest_top_num.setTextColor(Color.parseColor("#eeeeee"));
                        viewHolder.iv_top.setVisibility(View.GONE);
                        viewHolder.syn_yield.setTextColor(Color.parseColor("#333333"));
                        break;
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    class ViewHolder {
        TextView  username;
        TextView  level;
        TextView  syn_yield;
        ImageView userHead;
        ImageView iv_top;
        TextView  invest_top_num;
        TextView  tv_flag;
    }

    class OtherViewHolder {
        TextView  username;
        ImageView userHead;
        Button    invest_btn;
    }

    class OtherViewHolder2 {
        TextView  username;
        TextView  level;
        TextView  syn_yield;
        ImageView userHead;
        TextView  strength_des;
        TextView  tv_flag;
    }

}
