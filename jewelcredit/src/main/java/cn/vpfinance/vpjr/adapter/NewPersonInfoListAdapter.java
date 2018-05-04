package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.LoanPersonInfo;

/**
 * Created by Administrator on 2016/10/26.
 */
public class NewPersonInfoListAdapter extends BaseAdapter {
    private  LoanPersonInfo.DataEntity               mBean;
    private       List<LoanPersonInfo.DataEntity.DataDes> mList;
    private       Context                                 mContext;

    public NewPersonInfoListAdapter(Context mContext) {
        super();
        if (mList == null) {
            mList = new ArrayList<>();
        }
        this.mContext = mContext;
    }

    public void setData(LoanPersonInfo.DataEntity bean) {
        if(bean == null) return;
        mList = bean.data;
        mBean = bean;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return this.mList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mList == null) {
            return null;
        } else {
            return this.mList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from
                    (this.mContext).inflate(R.layout.itme_person_info_title, null, false);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
            holder.gridView = (GridView) convertView.findViewById(R.id.listview_item_gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (mList != null) {
                LoanPersonInfo.DataEntity.DataDes dataDes = mList.get(position);
            if (dataDes != null) {
                if (!TextUtils.isEmpty(dataDes.icon)) {
                    ImageLoader.getInstance().displayImage(dataDes.icon,holder.imgIcon);
                }
                holder.tvTitle.setText(dataDes.title);
                NewPersonInfoGridAdapter gridViewAdapter=new NewPersonInfoGridAdapter(mContext, dataDes);
                if (dataDes.title.contains("信用等级")) {
                    holder.gridView.setNumColumns(1);
                } else {
                    holder.gridView.setNumColumns(2);
                }
                holder.gridView.setAdapter(gridViewAdapter);
            }

        }

        return convertView;

    }


    private class ViewHolder {
        TextView tvTitle;
        GridView gridView;
        ImageView imgIcon;
    }

}
