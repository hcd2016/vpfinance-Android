package cn.vpfinance.vpjr.module.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

import java.io.File;
import java.io.IOException;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.IndexPacketBean;
import cn.vpfinance.vpjr.module.common.SharePop;
import cn.vpfinance.vpjr.util.ScreenUtil;

public class IndexRedPacketActivity extends BaseActivity {

    private LinearLayout rootContainer;

    public static void goThis(Context context, String json){
        Intent intent = new Intent(context,IndexRedPacketActivity.class);
        intent.putExtra("json",json);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_red_packet);

        rootContainer = ((LinearLayout) findViewById(R.id.rootContainer));
        ((ActionBarLayout) findViewById(R.id.vActionBarLayout)).reset().setTitle("红包分享").setHeadBackVisible(View.VISIBLE);

        String json = getIntent().getStringExtra("json");
        IndexPacketBean indexPacketBean = new Gson().fromJson(json, IndexPacketBean.class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.vRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(indexPacketBean));

        new HttpService(this,this).setShareRedPacketList();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        private IndexPacketBean indexPacketBean;

        public MyAdapter(IndexPacketBean indexPacketBean) {
            this.indexPacketBean = indexPacketBean;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(IndexRedPacketActivity.this).inflate(R.layout.item_index_red_packet,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            IndexPacketBean.DatasBean bean = indexPacketBean.datas.get(position);
            holder.tvTitle.setText(bean.title);
            final String shareUrl = bean.shareUrl;
            holder.vButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final File imgFile = new File(IndexRedPacketActivity.this.getCacheDir(), "redpack.png");
                    if (imgFile != null && imgFile.exists()) {
                    } else {
                        try {
                            Utils.copyAssetsFileToPath(IndexRedPacketActivity.this, "redpack.png", imgFile.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    SharePop sharePop = new SharePop(IndexRedPacketActivity.this, shareUrl, imgFile == null ? "" : imgFile.getAbsolutePath(),
                            "好友发来微品金融红包，个数有限先到先得，快去碰碰手气吧。", "微品金融拼手气红包");
                    sharePop.showAtLocation(rootContainer, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ScreenUtil.getBottomStatusHeight(IndexRedPacketActivity.this));
                }
            });
        }

        @Override
        public int getItemCount() {
            return indexPacketBean.datas == null ? 0 : indexPacketBean.datas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView tvTitle;
            Button vButton;
            public MyViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
                vButton = (Button)itemView.findViewById(R.id.vButton);
            }
        }
    }
}
