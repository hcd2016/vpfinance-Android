package cn.vpfinance.vpjr.module.user.personal;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.model.AwardRecordRegistInfo;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.util.Common;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2015/11/13.
 * 注册奖励记录
 */
public class AwardRecordForRegistActivity extends BaseActivity {

    private ListView mListView;
    private MyAdapter adapter;
    private HttpService mHttpService;
    private UserDao userDao;
    private User user;

    private SwipeRefreshLayout mRefresh;
    private boolean isRefreshing = false;
    private int page = 1;
    private final static int PAGE_SIZE = 15;
    private List<AwardRecordRegistInfo.AwardRecordRegistInfoItem> allList = new ArrayList<AwardRecordRegistInfo.AwardRecordRegistInfoItem>();
    private String totalPage;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award_record_regist);

        ActionBarLayout titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setTitle("注册奖励记录").setHeadBackVisible(View.VISIBLE);
        mHttpService = new HttpService(this, this);
        getUser();
        mListView = (ListView) findViewById(R.id.mListView);
        textview = (TextView) findViewById(R.id.textview);

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mRefresh.setColorSchemeColors(ContextCompat.getColor(this,R.color.main_color));
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                page = 1;
                allList.clear();
                mRefresh.setRefreshing(isRefreshing);
                mHttpService.getRegistAwardRecord(user.getUserId() + "", "" + page, "" + PAGE_SIZE);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1 && !isRefreshing) {
                    page++;
                    if (page > Integer.parseInt(totalPage)) {
                        //模拟最后一页
                    } else {
                        mHttpService.getRegistAwardRecord(user.getUserId() + "", "" + page, "" + PAGE_SIZE);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });


        adapter = new MyAdapter(this);
        mListView.setAdapter(adapter);

        mHttpService.getRegistAwardRecord(user.getUserId() + "", "" + page, "" + PAGE_SIZE);
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        if (mRefresh.isRefreshing()) {
            mRefresh.setRefreshing(false);
        }
        isRefreshing = false;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (mRefresh.isRefreshing()) {
            mRefresh.setRefreshing(false);
        }
        isRefreshing = false;
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_RegistAwardRecord.ordinal()) {
            AwardRecordRegistInfo info = mHttpService.onGetRegistAwardRecord(json);
            if ("true".equals(info.success)) {
                totalPage = info.totalPage;
                allList.addAll(info.dataList);
                if (allList == null || allList.size() == 0) {
                    textview.setText("暂无数据");
                    textview.setVisibility(View.VISIBLE);
                } else {
                    textview.setVisibility(View.GONE);
                    adapter.setDate(allList);
                }
            } else {
//                Toast.makeText(AwardRecordForRegistActivity.this, "服务器有误！", Toast.LENGTH_SHORT).show();
            }
        }

    }

    class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<AwardRecordRegistInfo.AwardRecordRegistInfoItem> list;

        public MyAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setDate(List<AwardRecordRegistInfo.AwardRecordRegistInfoItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list == null ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;
            if (convertView == null) {
                view = View.inflate(mContext, R.layout.item_award_record_regist, null);
                holder = new ViewHolder();
                holder.phone = (TextView) view.findViewById(R.id.periods);
                holder.inviteTime = (TextView) view.findViewById(R.id.addTime);
                holder.reward = (TextView) view.findViewById(R.id.bonuses);
                holder.period = (TextView) view.findViewById(R.id.period);
                holder.status = (TextView) view.findViewById(R.id.status);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            AwardRecordRegistInfo.AwardRecordRegistInfoItem item = list.get(position);
            holder.phone.setText(item.phone);
            holder.inviteTime.setText(item.inviteTime);
            holder.reward.setText(item.reward + "元");
            holder.status.setText("0".equals(item.status) ? "有效" : "到期");
            holder.period.setText(item.period + "个月");

            return view;
        }
    }

    static class ViewHolder {
        TextView phone;
        TextView inviteTime;
        TextView reward;
        TextView period;
        TextView status;
    }

    private void getUser() {
        if (mHttpService == null) {
            mHttpService = new HttpService(this, this);
        }
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();

        if (AppState.instance().logined()) {
            if (userDao != null) {
                QueryBuilder<User> qb = userDao.queryBuilder();
                List<User> userList = qb.list();
                if (userList != null && userList.size() > 0) {
                    user = userList.get(0);
                }
            }
        } else {
            Toast.makeText(this, "请登录.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
