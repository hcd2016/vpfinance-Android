package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.adapter.NewCarListAdapter;
import cn.vpfinance.vpjr.gson.NewCarInfoBean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.TagCloudView;

/**
 * Created by Administrator on 2016/10/24.
 * 标的详情 datatype为4的数据格式（车贷珠宝贷）
 */
public class NewCarInfoFragment extends BaseFragment {
    private static final String PRODUCT_ID = "loanId";
    //    private static final String SHOW_TYPE  = "showType";
    private static final String NET_URL    = "netUrl";
    @Bind(R.id.carTag)
    TagCloudView   mCarTag;
    @Bind(R.id.listView)
    ListView       mListView;
    @Bind(R.id.lookOtherProduct)
    Button         mLookOtherProduct;
    @Bind(R.id.rl_show_login)
    RelativeLayout mRlShowLogin;
    private long              mLoanId;
    private String            mNetUrl;
    private HttpService       mHttpService;
    private NewCarListAdapter mMyAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHttpService = new HttpService(getActivity(), this);
        Bundle args = getArguments();
        if (args != null) {
            //            mShowType = args.getString(SHOW_TYPE);
            mNetUrl = args.getString(NET_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_carinfo_fragment, container, false);
        ButterKnife.bind(this, view);
        mMyAdapter = new NewCarListAdapter(mContext);
        mListView.setAdapter(mMyAdapter);
        mHttpService.getRegularTab(mNetUrl);
        return view;
    }

    public static NewCarInfoFragment newInstance(String url) {
        NewCarInfoFragment fragment = new NewCarInfoFragment();
        Bundle args = new Bundle();
        args.putString(NET_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        if (AppState.instance().logined()) {
            mRlShowLogin.setVisibility(View.GONE);
        } else {
            mRlShowLogin.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Regular_Tab.ordinal()) {
            try {
                Gson gson = new Gson();
                NewCarInfoBean newCarInfoBean = gson.fromJson(json + "", NewCarInfoBean.class);
                final List<NewCarInfoBean.DataEntity> data = newCarInfoBean.data;
                if (data != null) {
                    mMyAdapter.setData(data.get(0));
                    List<String> tags = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        String tag = data.get(i).title;
                        tags.add(tag);
                    }
                    mCarTag.setTags(tags);
                    mCarTag.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
                        @Override
                        public void onTagClick(int position) {
                            if (position != -1) {
                                NewCarInfoBean.DataEntity dataEntity = data.get(position);
                                mMyAdapter.setData(dataEntity);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @OnClick(R.id.lookOtherProduct)
    public void onClick() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }
}
