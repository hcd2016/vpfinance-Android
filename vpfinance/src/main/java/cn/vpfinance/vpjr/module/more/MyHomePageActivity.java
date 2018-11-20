package cn.vpfinance.vpjr.module.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.view.CircleImg;

/**
 * 我的主页
 */
public class MyHomePageActivity extends BaseActivity {
    @Bind(R.id.titleBar)
    ActionBarLayout titleBar;
    @Bind(R.id.iv_avatar)
    CircleImg ivAvatar;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_vip_level)
    TextView tvVipLevel;
    @Bind(R.id.tv_signature)
    TextView tvSignature;
    @Bind(R.id.ll_logined_header_container)
    LinearLayout llLoginedHeaderContainer;
    @Bind(R.id.ll_header_contianer)
    LinearLayout llHeaderContianer;
    @Bind(R.id.line_gray)
    View lineGray;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_home_page);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        titleBar.setTitle("我的主页").setHeadBackVisible(View.VISIBLE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3) ;
        recyclerView.setLayoutManager(gridLayoutManager);
        MyMedalAdapter myMedalAdapter = new MyMedalAdapter(list);
        recyclerView.setAdapter(myMedalAdapter);
    }

    private class MyMedalAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyMedalAdapter(@Nullable List<String> data) {
            super(R.layout.item_medal_list, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setImageResource(R.id.iv_medal, R.drawable.medal_3);
        }
    }
}
