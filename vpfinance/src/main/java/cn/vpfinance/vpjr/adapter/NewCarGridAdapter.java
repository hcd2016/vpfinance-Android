package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.NewCarInfoBean;

/**
 * Created by Administrator on 2016/10/26.
 */
public class NewCarGridAdapter extends BaseAdapter {
    private       Context                           mContext;
    private       NewCarInfoBean.DataEntity.DataDes mDataDes;
    private  List<String>                      mKeyList;
    private  List<String> mValueList;

    public NewCarGridAdapter(Context mContext, NewCarInfoBean.DataEntity.DataDes dataDes) {
        super();
        if(dataDes == null) return;

        this.mContext = mContext;
        mDataDes = dataDes;
        mKeyList = dataDes.key;
        mValueList = dataDes.value;
    }



    @Override
    public int getCount() {
        if (mKeyList == null) {
            return 0;
        } else {
            return mKeyList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mKeyList == null) {
            return null;
        } else {
            return mKeyList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from
                    (this.mContext).inflate(R.layout.itme_car_info_content, null, false);
            holder.tvKey = (TextView) convertView.findViewById(R.id.tvKey);
            holder.tvValue = (TextView) convertView.findViewById(R.id.tvValue);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (mKeyList != null) {
            String key = mKeyList.get(position);
                holder.tvKey.setText(key+":");
        }

        if (mValueList != null) {
            String value = mValueList.get(position);
            if (position == 0 && isLongStr(mValueList)) {
                holder.tvValue.setText(value + "\n\n");
            } else {
                holder.tvValue.setText(value);
            }
        }

        return convertView;

    }

    private boolean isLongStr(List<String> list) {
        if (list != null) {
            for (String s : mValueList) {
                if (s.length() > 20) {
                    return true;
                }
            }
        }

        return false;
    }


    private class ViewHolder {
        public TextView tvKey;
        public TextView tvValue;
    }

}
