package cn.vpfinance.vpjr.module.voucher.fragment;

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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.adapter.NewPersonInfoListAdapter;
import cn.vpfinance.vpjr.gson.LoanPersonInfo;
import cn.vpfinance.vpjr.view.CoordinatorLayoutListView;

/**
 * Created by Administrator on 2016/10/24.
 * datatype为5的数据格式（个人标） 保持原有的借款人基本信息的格式不变
 */
public class NewPersonInfoFragment extends BaseFragment {

    private static final String PRODUCT_ID       = "loanId";
    //    private static final String SHOW_TYPE        = "showType";
    private static final String NET_URL          = "netUrl";
    private static final int    TITLE_VIEWTYPE   = 1;
    private static final int    CONTENT_VIEWTYPE = 2;
    @Bind(R.id.listView)
    CoordinatorLayoutListView mListView;
    @Bind(R.id.lookOtherProduct)
    Button         mLookOtherProduct;
    @Bind(R.id.rl_show_login)
    RelativeLayout mRlShowLogin;
    private long                     mLoanId;
    private String                   mNetUrl;
    private HttpService              mHttpService;
    private NewPersonInfoListAdapter mMyAdapter;


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
        View view = inflater.inflate(R.layout.new_personinfo_fragment, container, false);
        ButterKnife.bind(this, view);
        mMyAdapter = new NewPersonInfoListAdapter(mContext);
        mListView.setAdapter(mMyAdapter);
        mHttpService.getRegularTab(mNetUrl);
        return view;
    }

    public static NewPersonInfoFragment newInstance(String url) {
        NewPersonInfoFragment fragment = new NewPersonInfoFragment();
        Bundle args = new Bundle();
        //        args.putString(SHOW_TYPE, showType);
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
                LoanPersonInfo loanPersonInfo = gson.fromJson(json + "", LoanPersonInfo.class);
                List<LoanPersonInfo.DataEntity> list = loanPersonInfo.data;
                LoanPersonInfo.DataEntity data = list.get(0);
                mMyAdapter.setData(data);
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
