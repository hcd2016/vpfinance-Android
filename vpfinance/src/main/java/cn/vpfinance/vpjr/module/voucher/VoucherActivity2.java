package cn.vpfinance.vpjr.module.voucher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.dialog.TextInputDialogFragment;
import cn.vpfinance.vpjr.module.dialog.VoucherDialogFragment;
import cn.vpfinance.vpjr.model.PromoteLinks;
import cn.vpfinance.vpjr.model.Voucher;
import cn.vpfinance.vpjr.model.VoucherArray;
import cn.vpfinance.vpjr.module.user.personal.InviteGiftActivity;


/**
 * Created by Administrator on 2015/10/29.
 * 代金券
 */
public class VoucherActivity2 extends BaseActivity implements AbsListView.OnScrollListener {

    public static final String NAME_SELECT = "select";
    public static final String NAME_SELECT_RESULT = "select_result";
    private boolean select;

    private ListView voucherLV;
    private HttpService mHttpService = null;
    private PromoteLinks mPromoteLinks = null;
    private TextInputDialogFragment tidf;
    private ArrayList<Voucher> allVoucherList= new ArrayList<Voucher>();
    private MyAdapter adapter;
    private TextView textview;
    private View btnOk;

    private HashSet<String> selectSet = new HashSet<>();
    private int page = 1;
    private boolean isLastPage = false;
    private int totalPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher2);
        mHttpService = new HttpService(this, this);
        mHttpService.getPromoteLinks();

        Intent intent = getIntent();
        if (null != intent) {
            select = intent.getBooleanExtra(NAME_SELECT,false);
        }

        initView();

//        requestVoucherList();
        mHttpService.getVoucherlist("" + page,"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    protected void initView() {
        ActionBarLayout titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setTitle("代金券").setHeadBackVisible(View.VISIBLE);
        titleBar.setImageButtonRight(R.drawable.ic_help_voucher, mRightClickListener);
//        titleBar.setImageButtonRight(R.drawable.ic_voucher_add, mRightClickListener);

        findViewById(R.id.llRecommendVoucher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(VoucherActivity2.this,"推荐好友",Toast.LENGTH_SHORT).show();
                InviteGiftActivity.goThis(VoucherActivity2.this);
            }
        });

        (findViewById(R.id.llConvertVoucher)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoucherDialogFragment voucherDialogFragment = new VoucherDialogFragment();
                voucherDialogFragment.setOnTextConfrimListener(new VoucherDialogFragment.onTextConfrimListener() {
                    @Override
                    public boolean onTextConfrim(String value) {
                        if (value != null) {
                            mHttpService.voucherExchange(value);
                        }
                        return true;
                    }
                });
                voucherDialogFragment.show(getSupportFragmentManager(), "VoucherDialog");
            }
        });

        textview = (TextView) findViewById(R.id.textview);
        voucherLV = (ListView) findViewById(R.id.voucherLV);
        voucherLV.setOnScrollListener(this);

        adapter = new MyAdapter(select);
        voucherLV.setAdapter(adapter);

        findViewById(R.id.selectConfirm).setVisibility(select ? View.VISIBLE : View.GONE);

        btnOk = findViewById(R.id.btnOK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String[] retArray = new String[selectSet.size()];
                selectSet.toArray(retArray);
                intent.putExtra(NAME_SELECT_RESULT, retArray);
                setResult(0, intent);
                finish();
            }
        });
    }

    private View.OnClickListener addVoucherListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            tidf = TextInputDialogFragment.newInstance("交易密码", "请输入交易密码", true);
            tidf.setOnTextConfrimListener(new TextInputDialogFragment.onTextConfrimListener()
            {
                @Override
                public boolean onTextConfrim(String value)
                {
                    if(value!=null)
                    {
                        mHttpService.voucherExchange(value);
                    }
                    return false;
                }
            });
            tidf.show(getSupportFragmentManager(), "addVoucher");
        }
    };

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_getPromoteLinks.ordinal()) {
            mPromoteLinks = mHttpService.onPromoteLinks(json);
        }
        if (reqId == ServiceCmd.CmdId.CMD_voucherExchange.ordinal()) {
            if (json == null) {
                return;
            }
            String msg = json.optString("msg");
            String money = json.optString("money");
            if ("1".equals(msg)) {
                Toast.makeText(this, "恭喜您兑换了" + money + "元代金券!", Toast.LENGTH_LONG).show();
//                EventBus.getDefault().post(1 + "");
                allVoucherList.clear();
                isLastPage = false;
                page = 1;
                mHttpService.getVoucherlist("" + page,"");
            } else if ("0".equals(msg)) {
                Toast.makeText(VoucherActivity2.this, "输入兑换码有误！", Toast.LENGTH_SHORT).show();
            } else if ("2".equals(msg)) {
                Toast.makeText(VoucherActivity2.this, "兑换码已使用！", Toast.LENGTH_SHORT).show();
            }
        }//代金券列表
        if (reqId == ServiceCmd.CmdId.CMD_VOUCHERLIST_V2.ordinal()) {
            if (json != null) {
                VoucherArray voucherArray = mHttpService.onVoucherArray(json);
                try{
                    totalPage = Integer.parseInt(json.optString("totalPage"));
                }catch (Exception e){
                    e.printStackTrace();
                }

                ArrayList<Voucher> voucherList = voucherArray.getVoucherList();
                //Logger.e("voucherList.size:"+voucherList.size());
                if (voucherList != null && voucherList.size() != 0) {

                    allVoucherList.addAll(voucherList);
                    adapter.setData(allVoucherList);
                }
                if (allVoucherList.size() == 0 || allVoucherList == null) {
                    textview.setVisibility(View.VISIBLE);
                    textview.setText("您没有代金券！");
                } else {
                    textview.setVisibility(View.GONE);
                }
            }
        }
    }

    private View.OnClickListener mRightClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            gotoWeb("/AppContent/voucher", "代金券");
        }
    };

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (view.getLastVisiblePosition() == view.getCount() - 1 ) {
            page++;
            isLastPage = page > totalPage ? true : false;
            if (isLastPage) return;
            mHttpService.getVoucherlist("" + page,"");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }


    class MyAdapter extends BaseAdapter {
        private ArrayList<Voucher> datas;
        private boolean select;
        public void setData(ArrayList<Voucher> datas){
            this.datas = datas;
            notifyDataSetChanged();
        }

        public MyAdapter(boolean select)
        {
            this.select = select;;
        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas == null ? null : datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =null;
            ViewHolder holder =null;
            if (convertView == null){
                view =  View.inflate(VoucherActivity2.this, R.layout.item_voucher_ticket, null);
                holder =new ViewHolder();
                holder.voucher_bg = (RelativeLayout)view.findViewById(R.id.voucher_bg);
                holder.voucher_money = (TextView)view.findViewById(R.id.voucher_money);
                holder.voucher_state = (TextView)view.findViewById(R.id.voucher_state);
                holder.voucher_description = (TextView)view.findViewById(R.id.voucher_description);
                holder.checkBox = (CheckBox)view.findViewById(R.id.checkBox);
                view.setTag(holder);
            }else{
                view = convertView;
                holder = (ViewHolder)view.getTag();
            }
            if (datas !=null && datas.size() != 0){
                final Voucher voucher = datas.get(position);
                if ("未使用".equals(voucher.getName()) || "可使用".equals(voucher.getName())){
                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_item_blue));
                    if (select)
                    {
                        holder.checkBox.setVisibility(View.VISIBLE);
                        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(b)
                                {
                                    selectSet.add(""+voucher.getId());
                                }
                                else
                                {
                                    selectSet.remove("" + voucher.getId());
                                }
                            }
                        });
                    }
                    else
                    {
                        holder.checkBox.setVisibility(View.GONE);
                    }

                    holder.voucher_state.setText(Utils.getDate(voucher.getExpireDate())+"到期");
                }else if("已过期".equals(voucher.getName())){
                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_item_gray));
                    holder.voucher_state.setText(voucher.getName());
                    holder.checkBox.setVisibility(View.GONE);
                }else if("已使用".equals(voucher.getName())){
                    holder.voucher_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_voucher_item_gray));
                    holder.voucher_state.setText(voucher.getName());
                    holder.checkBox.setVisibility(View.GONE);
                }

                holder.voucher_money.setText(""+voucher.getAmount());
                holder.voucher_description.setText(voucher.getUseRuleExplain());
//                holder.voucher_time.setText("有效期"+ Utils.getDate(voucher.getCreatedAt())+"至"+);
            }
            return view;
        }
    }
    static class ViewHolder{
        TextView voucher_money;
        TextView voucher_state;
        TextView voucher_description;
        RelativeLayout voucher_bg;
        CheckBox checkBox;
    }
}
