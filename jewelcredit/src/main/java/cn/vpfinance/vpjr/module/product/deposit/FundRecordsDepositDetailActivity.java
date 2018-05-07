package cn.vpfinance.vpjr.module.product.deposit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import cn.vpfinance.vpjr.gson.RecordDepositDetailBean;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.record.InvestRecordRefundActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.LinearLayoutForListView;
import cn.vpfinance.vpjr.view.TagCloudView;

/**
 * Created by zzlz13 on 2017/6/2.
 */

public class FundRecordsDepositDetailActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.ll_listview)
    LinearLayoutForListView ll_listview;
    @Bind(R.id.product_title)
    TextView product_title;
    @Bind(R.id.status)
    TextView status;
    @Bind(R.id.protocol)
    TagCloudView mProtocol;
    @Bind(R.id.invest_time)
    TextView invest_time;
    @Bind(R.id.refund_time)
    TextView refund_time;
    @Bind(R.id.click_look_refund_time)
    LinearLayout click_look_refund_time;

    private HttpService mHttpService;
    private int poolId;
    private String recordPoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_record_deposit_detail);

        ButterKnife.bind(this);
        mTitleBar.setHeadBackVisible(View.VISIBLE).setTitle("投资详情");

        mHttpService = new HttpService(this,this);
        Intent intent = getIntent();
        if (intent != null){
            String id = intent.getStringExtra("poolId");
            recordPoolId = id;
            mHttpService.getRecordDepositDetailInfo(id);
        }
    }

    @OnClick({R.id.click_look_product_detail, R.id.click_look_refund_time})
    public void click(View view){
        switch (view.getId()){
            case R.id.click_look_product_detail:
                if (poolId != 0){
                    NewRegularProductActivity.goNewRegularProductActivity(this,poolId ,0,"",true,recordPoolId);
                }
                break;
            case R.id.click_look_refund_time:
                //TODO
                InvestRecordRefundActivity.goInvestRecordRefund(this,1,""+recordPoolId);
                break;
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Record_Deposit_Detail_info.ordinal()){

            RecordDepositDetailBean bean = mHttpService.onGetgetRecordDepositDetailInfo(json);
            poolId = bean.poolId;

            List<RecordDepositDetailBean.ProcotolListBean> protocolList = bean.procotolList;
            final ArrayList<String> names = new ArrayList<>();
            final ArrayList<String> urls = new ArrayList<>();
            for (int i = 0; i < protocolList.size(); i++) {
                RecordDepositDetailBean.ProcotolListBean protocolListBean = protocolList.get(i);
                names.add(protocolListBean.title);
                urls.add(HttpService.mBaseUrl+protocolListBean.url);
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

            invest_time.setText("投资时间："+bean.tenderTime);
            product_title.setText(bean.title);

            String loanState = "";
            switch (bean.status){
                //1未发布 2进行中 3回款中 4已完成
                case 1:
                    loanState = "预售中";
                    status.setText(loanState);
                    status.setVisibility(View.VISIBLE);
//                    click_look_refund_time.setVisibility(View.GONE);
                    break;
                case 2:
                    loanState = "进行中";
                    status.setText(loanState);
                    status.setVisibility(View.VISIBLE);
//                    click_look_refund_time.setVisibility(View.GONE);
                    break;
                case 3:
                    status.setVisibility(View.GONE);
//                    click_look_refund_time.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    status.setVisibility(View.GONE);
//                    click_look_refund_time.setVisibility(View.VISIBLE);
                    break;
            }
            if (bean.status > 2){
                refund_time.setText(""+bean.finishTime);
                click_look_refund_time.setVisibility(View.VISIBLE);
            }else{
                click_look_refund_time.setVisibility(View.GONE);
            }

            MyAdapter myAdapter = new MyAdapter(this);
            myAdapter.setContentList(bean.contentList);
            ll_listview.setAdapter(myAdapter);
        }
    }

    private class MyAdapter extends BaseAdapter{
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        private List<List<RecordDepositDetailBean.ContentListBean>> contentList;

        public void setContentList(List<List<RecordDepositDetailBean.ContentListBean>> contentList) {
            if (contentList == null)    return;
            this.contentList = contentList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return contentList == null ? 0 : contentList.size();
        }

        @Override
        public List<RecordDepositDetailBean.ContentListBean> getItem(int position) {
            return contentList == null ? null : contentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            List<RecordDepositDetailBean.ContentListBean> contentListBeen = contentList.get(position);

            View view = LayoutInflater.from(context).inflate(R.layout.item_fund_record_deposit, parent, false);
            LinearLayoutForListView insideListView = (LinearLayoutForListView) view.findViewById(R.id.ll_inside_listview);
            insideListView.setAdapter(new MyInsideAdapter(context,contentListBeen));
            return view;
        }
    }

    private class MyInsideAdapter extends BaseAdapter{

        private Context context;
        private List<RecordDepositDetailBean.ContentListBean> data;

        public MyInsideAdapter(Context context, List<RecordDepositDetailBean.ContentListBean> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public RecordDepositDetailBean.ContentListBean getItem(int position) {
            return data == null ? null : data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecordDepositDetailBean.ContentListBean bean = data.get(position);
            View view = LayoutInflater.from(context).inflate(R.layout.item_fund_record_deposit_inside, parent, false);
            ((TextView) view.findViewById(R.id.title)).setText(bean.title+"");
            ((TextView) view.findViewById(R.id.val_black)).setText(bean.blackVal+"");
            ((TextView) view.findViewById(R.id.val_red)).setText(bean.redVal+"");
            return view;
        }
    }
}
