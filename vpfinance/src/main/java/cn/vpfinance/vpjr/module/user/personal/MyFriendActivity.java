package cn.vpfinance.vpjr.module.user.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.PersonalCardBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.user.OtherPersonalCardActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.view.CircleImg;

/**
 * Created by Administrator on 2016/6/23.
 */
public class MyFriendActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.recyclerView)
    RecyclerView    mRecyclerView;
    private LinearLayoutManager             mLinearLayoutManager;
    private HttpService                            mHttpService;
    private List<PersonalCardBean.MyFriendsEntity> mMyFriends;
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fridend);
        ButterKnife.bind(this);

        mHttpService = new HttpService(this, this);
        init();
    }

    private void init() {

        if (AppState.instance().logined()) {
            User user = DBUtils.getUser(this);
            if (user != null) {
                mHttpService.getPersonalCard(user.getUserId() + "");
            }
        } else {
            Toast.makeText(this, "请登录.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mTitleBar.setTitle("我邀请的好友").setHeadBackVisible(View.VISIBLE);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMyAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mMyAdapter);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Personal_Card.ordinal()) {
            PersonalCardBean personalCardBean = mHttpService.onGetPersonalCard(json);
            mMyFriends = personalCardBean.myFriends;
            if (mMyFriends != null) {
                mMyAdapter.setData(mMyFriends);
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyHolder>{

        private Context mContext;
        private List<PersonalCardBean.MyFriendsEntity> mData;

        public MyAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<PersonalCardBean.MyFriendsEntity> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_friend, viewGroup, false);
//            View view = View.inflate(mContext, R.layout.item_my_friend, null);
            //            StaggeredGridLayoutManager.LayoutParams layoutParams = (tFullSpan(false);
//            view.setLayoutParams(layoutParams);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder myHolder, final int i) {
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OtherPersonalCardActivity.class);
                    intent.putExtra(OtherPersonalCardActivity.UID, mData.get(i).userId);
                    gotoActivity(intent);
                }
            });
            ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + mData.get(i).imgUrl, myHolder.userHead);
            myHolder.userName.setText(mData.get(i).userName);
            myHolder.my_describe.setText(TextUtils.isEmpty(mData.get(i).signature)?"未设置个人签名" : mData.get(i).signature);
        }

        @Override
        public int getItemCount() {
            if (mData == null) {
                return  0;
            }
            return mData.size();
        }

    }

    class MyHolder extends RecyclerView.ViewHolder{
        CircleImg userHead;
        TextView  userName;
        TextView  my_describe;
        public MyHolder(View itemView) {
            super(itemView);

            userHead = ((CircleImg) itemView.findViewById(R.id.userHead));
            userName = (TextView) itemView.findViewById(R.id.username);
            my_describe = (TextView) itemView.findViewById(R.id.my_describe);
        }

    }
}
