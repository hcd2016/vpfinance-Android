package cn.vpfinance.vpjr.module.user.asset;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.FundFlowInfo;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialogFragment;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.StatusBarCompat1;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

import static android.view.View.GONE;

/**
 * Created by Administrator on 2015/10/30.
 * 资金流水
 */
public class FundFlowActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {


    private TextView title;
    private ImageView title_state;
    private ImageView headBack;
    /*显示状态 0 1 2*/
    private int showState = 0;
    private View popwindow;
    private PopupWindow menu;
    private HttpService mHttpService;
    private UserDao userDao;
    private User user;
    private ListView mListView;
    private TextView mTextView;
    private SwipeRefreshLayout mRefresh = null;
    private boolean isRefreshing = false;
    private int page =1;
    private final static int PAGE_SIZE = 10;
    private MyAdapter mAdapter;
    private String totalPage;
    private ArrayList<FundFlowInfo.FlowList> allList = new ArrayList<FundFlowInfo.FlowList>();
    private List<String> currentDate;
    private EditText beginTime;
    private EditText endTime;
    private Button find;
    private int accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_flow);
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        if (intent != null){
            accountType = intent.getIntExtra(Constant.AccountType,0);
        }
        findViewById(R.id.tvBankHint).setVisibility(accountType == Constant.AccountLianLain ? View.GONE : View.VISIBLE);

        mHttpService = new HttpService(this, this);
        title = (TextView) findViewById(R.id.title);
        headBack = (ImageView) findViewById(R.id.headBack);
        headBack.setOnClickListener(this);

        View mFakeStatusBar = findViewById(R.id.fake_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mFakeStatusBar.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = mFakeStatusBar.getLayoutParams();
            layoutParams.height = StatusBarCompat1.getStatusBarHeight(this);
            mFakeStatusBar.setLayoutParams(layoutParams);
        }else{
            mFakeStatusBar.setVisibility(View.GONE);
        }

        title_state = (ImageView) findViewById(R.id.title_state);
        title_state.setBackgroundResource(R.drawable.arrow_down_state);

        beginTime = (EditText) findViewById(R.id.beginTime);
        endTime = (EditText) findViewById(R.id.endTime);
        find = (Button) findViewById(R.id.find);

        beginTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        beginTime.setOnFocusChangeListener(this);
        endTime.setOnFocusChangeListener(this);
        find.setOnClickListener(this);

        mAdapter = new MyAdapter(this);

        getUser();

        title.setOnClickListener(this);
        title_state.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mTextView = (TextView) findViewById(R.id.textview);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allList.clear();
                page = 1;
                startFindRecord(showState, beginTime.getText()+"", endTime.getText()+"");
            }
        });

        mRefresh = (SwipeRefreshLayout)findViewById(R.id.refresh);
        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                page = 1;
                allList.clear();
                startFindRecord(showState, beginTime.getText()+"", endTime.getText()+"");
            }
        });



        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
//                    isRefreshing = true;

                    //判断是否是最后一页
                    if (Integer.parseInt(totalPage) >= page+1) {
                        page++;
                        startFindRecord(showState, beginTime.getText()+"", endTime.getText()+"");
                    }else{
//                        Toast.makeText(FundFlowActivity.this,"数据加载完了！",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppState.instance().logined())
        {
            if (userDao != null) {
                QueryBuilder<User> qb = userDao.queryBuilder();
                List<User> userList = qb.list();
                if (userList != null && userList.size() > 0) {
                    user = userList.get(0);
                }
            }
        }

        currentDate = Utils.getCurrentDate();
        beginTime.setText(currentDate.get(0));
        endTime.setText(currentDate.get(1));
        isShowRechargeRecord(0);
    }
    public void onEventMainThread(List<FundFlowInfo.FlowList> event) {
        if (isFinishing())  return;

        if (event == null){
            return;
        }
        allList.addAll(event);
        if (allList.size()==0 || allList == null){
            mTextView.setText("暂无数据");
            mTextView.setVisibility(View.VISIBLE);
        }else{
            mTextView.setVisibility(View.GONE);
        }
        if (mAdapter !=null && allList != null)
            mAdapter.setInfos(allList);

        if(mRefresh.isRefreshing()) {
            mRefresh.setRefreshing(false);
        }
        isRefreshing = false;
    }
    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_CancelWithdraw.ordinal()){
            if (json != null){
                String type = json.optString("type");
                if ("1".equals(type)){
                    Utils.Toast(FundFlowActivity.this,"已经提现的不能取消");
                    isShowRechargeRecord(2);
                }else if ("2".equals(type)){
                    Utils.Toast(FundFlowActivity.this,"取消成功");
                }else if ("3".equals(type)){
                    Utils.Toast(FundFlowActivity.this,"取消失败");
                }
                isRefreshing = true;
                page = 1;
                allList.clear();
                startFindRecord(showState, beginTime.getText()+"", endTime.getText()+"");
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_FundFlow.ordinal()) {
            FundFlowInfo fundFlowInfo = mHttpService.onGetFundFlowInfo(json);
            if ("true".equals(fundFlowInfo.success)){
                mTextView.setVisibility(View.GONE);
                totalPage = fundFlowInfo.totalPage;
                List<FundFlowInfo.FlowList> list = fundFlowInfo.list;
                EventBus.getDefault().post(list);
            }else{
                if(mRefresh.isRefreshing()) {
                    mRefresh.setRefreshing(false);
                }
                isRefreshing = false;
                Toast.makeText(FundFlowActivity.this,"服务器获取数据失败！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        if (mRefresh.isRefreshing()){
            mRefresh.setRefreshing(false);
        }
        isRefreshing = false;
    }

    private void getUser() {
        if (mHttpService ==null){
            mHttpService =new HttpService(this,this);
        }
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME , null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();

        if(AppState.instance().logined())
        {
            if (userDao != null) {
                QueryBuilder<User> qb = userDao.queryBuilder();
                List<User> userList = qb.list();
                if (userList != null && userList.size() > 0) {
                    user = userList.get(0);
                }
            }
        }
        else
        {
            Toast.makeText(this, "请登录.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }



    private class MyAdapter extends BaseAdapter{
        private List<FundFlowInfo.FlowList> list;
        private Context mContext;

        public MyAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setInfos(List<FundFlowInfo.FlowList> list){
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (list !=null){
                return list.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (list !=null){
                return list.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent) {
            View view=null;
            ViewHolder holder =null;
            if (convertView ==null){
                view = View.inflate(mContext, R.layout.item_fund_flow_record, null);
                holder=new ViewHolder();
                holder.tvRecordTime=(TextView)view.findViewById(R.id.tvRecordTime);
                holder.tvRecordState=(TextView)view.findViewById(R.id.tvRecordState);
                holder.tvRecordMoney=(TextView)view.findViewById(R.id.tvRecordMoney);
                holder.tvRecordUsedMoney=(TextView)view.findViewById(R.id.tvRecordUsedMoney);
                holder.tvWithdrawState = (TextView)view.findViewById(R.id.tvWithdrawState);
                holder.btnWithdrawOk = (Button)view.findViewById(R.id.btnWithdrawOk);
                holder.llBtnParent = (LinearLayout)view.findViewById(R.id.llBtnParent);
                holder.llState = (LinearLayout)view.findViewById(R.id.llState);
                view.setTag(holder);
            }else{
                view=convertView;
                holder =(ViewHolder)view.getTag();
            }
            if (list !=null){
                try{
                    FundFlowInfo.FlowList info = list.get(position);
                    String time = info.getTime();
                    if (time.length() !=0 && time !=null){
                        String[] times = time.split(" ");
                        String[] split = times[0].split("-");
                        holder.tvRecordTime.setText(split[0] + "-" + split[1] + "-" + split[2] + " " + times[1]);
                    }
                    holder.tvRecordState.setText(info.getExplan());
                    if (!TextUtils.isEmpty(info.getFlowMoney())){
                        String flowMoney = info.getFlowMoney();
                        int color = flowMoney.substring(0,1).equals("-") ? R.color.text_black : R.color.dark_red;

                        holder.tvRecordMoney.setText(flowMoney);
                        holder.tvRecordMoney.setTextColor(getResources().getColor(color));
                    }else{
                        holder.tvRecordMoney.setText("+0.00");
                        holder.tvRecordMoney.setTextColor(getResources().getColor(R.color.dark_red));
                    }

                    holder.tvRecordUsedMoney.setText("余额¥"+info.getMoney());
                    if ((1 == showState) || (0 == showState)){
//                    holder.tvWithdrawState.setVisibility(View.GONE);
//                    holder.llBtnParent.setVisibility(View.GONE);
                        holder.llState.setVisibility(View.GONE);
                    }else if(2 == showState){
//                    holder.tvWithdrawState.setVisibility(View.VISIBLE);
//                    holder.llBtnParent.setVisibility(View.VISIBLE);
                        holder.llState.setVisibility(View.VISIBLE);
                        //set state and button
                        String status = info.getStatus();
                        if (!TextUtils.isEmpty(status)){

                            if ( "1".equals(status)){
                                holder.tvWithdrawState.setText("已审核");
                                holder.btnWithdrawOk.setVisibility(View.GONE);
                                holder.btnWithdrawOk.setTextColor(getResources().getColor(R.color.text_black));
                            }else if("2".equals(status)){
                                holder.tvWithdrawState.setText("已取消");
                                holder.btnWithdrawOk.setVisibility(View.GONE);
                                holder.btnWithdrawOk.setTextColor(getResources().getColor(R.color.text_black));
                            }else if("0".equals(status)){
                                final String applyId = info.getApplyId();
                                holder.tvWithdrawState.setText("未审核");
                                holder.btnWithdrawOk.setVisibility(View.VISIBLE);
                                holder.btnWithdrawOk.setTextColor(getResources().getColor(R.color.white));
                                holder.btnWithdrawOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        AlertDialog.Builder builder = new AlertDialog.Builder(FundFlowActivity.this);
//                                        builder.setMessage("是否撤回提现");
//                                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                mHttpService.getCancelWithdraw(applyId);
//                                            }
//                                        });
//                                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                            }
//                                        });
//                                        builder.show();
                                        new CommonTipsDialogFragment.Buidler()
                                                .setTitleVisibility(GONE)
                                                .setContent("是否撤回提现")
                                                .setBtnRight("确认")
                                                .setOnRightClickListener(new CommonTipsDialogFragment.OnRightClickListner() {
                                                    @Override
                                                    public void rightClick() {
                                                        mHttpService.getCancelWithdraw(applyId);
                                                    }
                                                })
                                                .setBtnLeft("取消")
                                                .createAndShow(FundFlowActivity.this);
                                    }
                                });
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return view;
        }
    }
    static class ViewHolder{
        TextView tvRecordTime;
        TextView tvRecordState;
        TextView tvRecordMoney;
        TextView tvRecordUsedMoney;
        TextView tvWithdrawState;
        Button btnWithdrawOk;
        LinearLayout llBtnParent;
        LinearLayout llState;
    }

    private void isShowRechargeRecord(int isShowState){
        switch (isShowState){
            case 0:
                title.setText("资金流水");
                showState = 0;
                page = 1;
                allList.clear();
                title_state.setBackgroundResource(R.drawable.arrow_down_state);
//                mHttpService.getFundFlow(""+showState, user.getUserId() + "", null, null, "" + page, "" + PAGE_SIZE);
                startFindRecord(showState, beginTime.getText()+"", endTime.getText()+"");
                break;
            case 1:
                title.setText("充值记录");
                showState = 1;
                page = 1;
                allList.clear();
                title_state.setBackgroundResource(R.drawable.arrow_down_state);
//                mHttpService.getFundFlow(""+showState, user.getUserId() + "", null, null, ""+page, ""+PAGE_SIZE);
                startFindRecord(showState, beginTime.getText()+"", endTime.getText()+"");
                break;
            case 2:
                title.setText("提现记录");
                showState = 2;
                page = 1;
                allList.clear();
                title_state.setBackgroundResource(R.drawable.arrow_down_state);
//                mHttpService.getFundFlow(""+showState, user.getUserId() + "", null, null, "" + page, "" + PAGE_SIZE);
                startFindRecord(showState, beginTime.getText()+"", endTime.getText()+"");
                break;
        }
        dismissMenu();
    }

    private void startFindRecord(int state ,String s, String s1) {
        if (Utils.compareDate(s, s1)){
            Toast.makeText(FundFlowActivity.this,"起始时间大于结束时间",Toast.LENGTH_SHORT).show();
            if (mRefresh.isRefreshing()){
                mRefresh.setRefreshing(false);
            }
            isRefreshing = false;
            return;
        }

        s = "".equals(s) ? null : s ;
        s1 = "".equals(s1) ? null : s1 ;
        mHttpService.getFundFlow("" + state, user.getUserId() + "", s, s1, "" + page, "" + PAGE_SIZE,accountType);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissMenu();
        EventBus.getDefault().unregister(this);
    }
    private void dismissMenu(){
        if (menu != null){
            if (menu.isShowing()){
                menu.dismiss();
            }
        }
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch(v.getId()){
            case R.id.beginTime:
                Utils.showSelectDialog(beginTime, FundFlowActivity.this);
                break;
            case R.id.endTime:
                Utils.showSelectDialog(endTime,FundFlowActivity.this);
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.title_state:
            case R.id.title:
                if(menu != null && menu.isShowing()){
                }else{
                    showPopWindow();
                }
                break;
            case R.id.headBack:
                FundFlowActivity.this.finish();
                break;
            case R.id.beginTime:
                Utils.showSelectDialog(beginTime, FundFlowActivity.this);
                break;
            case R.id.endTime:
                Utils.showSelectDialog(endTime,FundFlowActivity.this);
                break;
            case R.id.find:
                allList.clear();
                page = 1 ;
                startFindRecord(showState,beginTime.getText()+"",endTime.getText()+"");
                break;
        }
    }

    private void showPopWindow() {
        title_state.setBackgroundResource(R.drawable.arrow_up_state);
        title.setText("资金流水");

        popwindow = View.inflate(this, R.layout.popwindow_fund_flow, null);
        popwindow.findViewById(R.id.clickFundFlow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowRechargeRecord(0);
            }
        });

        popwindow.findViewById(R.id.clickRechargeRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowRechargeRecord(1);

            }
        });
        popwindow.findViewById(R.id.clickWithdrawRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowRechargeRecord(2);
            }
        });
        popwindow.findViewById(R.id.alphaView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu != null){
                    if (menu.isShowing()){
                        menu.dismiss();
                    }
                }
            }
        });
        //背景变为半透明
//        backgroundAlpha(0.5f);
        menu = new PopupWindow(popwindow, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        menu.setTouchable(true);
        menu.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        menu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                title_state.setBackgroundResource(R.drawable.arrow_down_state);
                title.setText(showState == 0 ? "资金流水" :
                        showState == 1 ? "充值记录" : "提现记录");
//                backgroundAlpha(1f);
            }
        });
        menu.setBackgroundDrawable(new BitmapDrawable());
        menu.showAsDropDown(title);
    }
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = FundFlowActivity.this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        FundFlowActivity.this.getWindow().setAttributes(lp);
    }
}
