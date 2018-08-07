package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;

public abstract class BaseRegularListFragment extends BaseFragment implements AbsListView.OnScrollListener {

    public SwipeRefreshLayout mRefresh;
    public ListView mListView;
    public TextView mClickRefresh;
    public ArrayList mData = null;
    public Context mContext;
    public MyAdapter myAdapter;
    private Pair<Integer, Integer> dateConfig;
    private static int page = 1;
    private int pageSize = 0;
    private boolean isLastPage = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.fragment_base_regular_list, null);

        mRefresh = ((SwipeRefreshLayout) view.findViewById(R.id.refresh));
        mRefresh.setColorSchemeResources(R.color.main_color);
        mListView = ((ListView) view.findViewById(R.id.listView));
        mClickRefresh = ((TextView) view.findViewById(R.id.textview));
        dateConfig = getDateConfig();
        page = dateConfig.first;
        pageSize = dateConfig.second;
        mListView.setOnScrollListener(this);

        initView(view);

        if (mData == null){
            mData = new ArrayList<>();
        }
        getData(page,pageSize);

        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);
        mListView.setDividerHeight(0);
        return view;
    }

    public abstract void getData(int page,int pageSize);

    public abstract void initView(View view);

    /** 获取分页配置，如果pageSize = 0 ，不分页*/
    public abstract Pair<Integer,Integer> getDateConfig();

    public abstract View setViewToList(List mData,int position, View convertView, ViewGroup parent);


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (pageSize == 0)  return;
        if (view.getLastVisiblePosition() == view.getCount() - 1 && !isLastPage) {
            page++;
            getData(page, pageSize);
//            mData.addAll();
            myAdapter.setData(mData);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }


    public class MyAdapter extends BaseAdapter{

        private ArrayList data;
        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        public void setData(ArrayList data){

            if (this.data == null)
                this.data = new ArrayList();
            if (data == null || data.size() == 0)   return;
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return setViewToList(data,position,convertView,parent);
        }
    }

}
