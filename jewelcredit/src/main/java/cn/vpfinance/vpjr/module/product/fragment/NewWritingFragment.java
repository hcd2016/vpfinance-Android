package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import cn.vpfinance.vpjr.gson.NewWritingBean;
import cn.vpfinance.vpjr.util.Common;

/**
 * Created by Administrator on 2016/10/24.
 * 标的详情中  纯文字frggment  对应datatype为1
 */
public class NewWritingFragment extends BaseFragment {

    private static final String PRODUCT_ID = "loanId";
    private static final String NET_URL    = "netUrl";

    @Bind(R.id.list_view)
    ListView       mListView;
    @Bind(R.id.lookOtherProduct)
    Button         mLookOtherProduct;
    @Bind(R.id.rl_show_login)
    RelativeLayout mRlShowLogin;


    private long        mLoanId;
    private String      mNetUrl;
    private HttpService mHttpService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHttpService = new HttpService(mContext, this);
        Bundle args = getArguments();
        if (args != null) {
            //            mShowType = args.getString(SHOW_TYPE);
            mNetUrl = args.getString(NET_URL);
        }
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
            Gson gson = new Gson();
            try {
                NewWritingBean newWritingBean = gson.fromJson(json.toString(), NewWritingBean.class);
                if (newWritingBean == null || newWritingBean.data == null)
                    return;

//                if (newWritingBean.dataType == 1) {//1.为纯文字，如风险控制这种
//                }
                ListViewAdapter adapter = new ListViewAdapter(mContext);
                mListView.setAdapter(adapter);
                adapter.setData(newWritingBean.data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_writing_fragment, container, false);
        ButterKnife.bind(this, view);
        mHttpService.getRegularTab(mNetUrl);
        return view;
    }

    public static NewWritingFragment newInstance(String url) {
        NewWritingFragment fragment = new NewWritingFragment();
        Bundle args = new Bundle();
        //        args.putString(SHOW_TYPE, showType);
        args.putString(NET_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.lookOtherProduct)
    public void onClick() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }


    private class ListViewAdapter extends BaseAdapter {

        private Context                       mContext;
        private List<NewWritingBean.DataBean> mData;

        public ListViewAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<NewWritingBean.DataBean> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public NewWritingBean.DataBean getItem(int position) {
            return mData == null ? null : mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_regular_product_project_introduction, null);
                viewHolder.titlTextView = (TextView) convertView.findViewById(R.id.item_regular_product_project_introduction_title_textview);
                viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.item_regular_product_project_introduction_content_textview);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            NewWritingBean.DataBean dataBean = mData.get(position);
            viewHolder.titlTextView.setText(dataBean.key);
            viewHolder.contentTextView.setText("\u3000\u3000" + dataBean.value);

            convertView.setTag(viewHolder);
            return convertView;
        }

        public class ViewHolder {
            public TextView titlTextView;
            public TextView contentTextView;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
