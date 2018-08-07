package cn.vpfinance.vpjr.module.product.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.PersonalInfo;
import cn.vpfinance.vpjr.model.ProjectIntroduction;
import cn.vpfinance.vpjr.util.Common;
import de.greenrobot.event.EventBus;

public class PersonalProductRiskControlFragment extends BaseFragment {

    private static final String ARGS_KEY_LOAN_ID = "loanId";
    private Context mContext = null;
    private HttpService mHttpService = null;
    private ItemBaseAdapter mItemBaseAdapter = null;
    private long mLoanId = 1;

    public static PersonalProductRiskControlFragment newInstance(long loanId){
        PersonalProductRiskControlFragment fragment = new PersonalProductRiskControlFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_KEY_LOAN_ID, loanId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mContext = activity;
        mHttpService = new HttpService(mContext, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        EventBus.getDefault().register(this);
        if (args != null) {
            mLoanId = args.getLong(ARGS_KEY_LOAN_ID, 1);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(PersonalInfo event) {
        if (!isAdded()) return;
        if (event != null && isAdded()) {
            ArrayList<ProjectIntroduction> list = new ArrayList<ProjectIntroduction>();
            if (event.fkInfo != null) {
                for (PersonalInfo.FkInfo fk : event.fkInfo) {
                    if(fk!=null)
                    {
                        ProjectIntroduction item = new ProjectIntroduction();
                        item.setName(fk.name);
                        item.setContent(fk.content);
                        list.add(item);
                    }
                }
            }
            mItemBaseAdapter.setDate(list);
            mItemBaseAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_regular_product_risk_control, container, false);
        ListView listView = (ListView)view.findViewById(R.id.riskControlListView);
        mItemBaseAdapter = new ItemBaseAdapter(mContext);
        listView.setAdapter(mItemBaseAdapter);
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_LOAN_SING_DESC.ordinal() && isAdded()) {
            if (json != null) {
                ArrayList<ProjectIntroduction> list = mHttpService.onGetLoanSignDescRiskControl(json);
                mItemBaseAdapter.setDate(list);
                mItemBaseAdapter.notifyDataSetChanged();
            }
        }
    }

    private class ItemBaseAdapter extends BaseAdapter {

        private Context mContext = null;
        private LayoutInflater mLayoutInflater = null;
        private ArrayList<ProjectIntroduction> mProjectIntroductionList = null;

        public ItemBaseAdapter(Context context){
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public void setDate(ArrayList<ProjectIntroduction> list){
            mProjectIntroductionList = list;
        }

        @Override
        public int getCount() {
            if(null != mProjectIntroductionList){
                return mProjectIntroductionList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(null != mProjectIntroductionList){
                return mProjectIntroductionList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(null == convertView) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.item_regular_product_project_introduction, null);
                viewHolder.titlTextView = (TextView)convertView.findViewById(R.id.item_regular_product_project_introduction_title_textview);
                viewHolder.contentTextView = (TextView)convertView.findViewById(R.id.item_regular_product_project_introduction_content_textview);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.titlTextView.setText(mProjectIntroductionList.get(position).getName());
            viewHolder.contentTextView.setText(mProjectIntroductionList.get(position).getContent());

            convertView.setTag(viewHolder);
            return convertView;
        }

        public class ViewHolder{
            public TextView titlTextView;
            public TextView contentTextView;
        }
    }
}
