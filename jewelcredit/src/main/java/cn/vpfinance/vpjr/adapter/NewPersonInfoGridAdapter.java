package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.LoanPersonInfo;

/**
 * Created by Administrator on 2016/10/26.
 */
public class NewPersonInfoGridAdapter extends BaseAdapter {
    private       Context                           mContext;
    private       LoanPersonInfo.DataEntity.DataDes mDataDes;
    private  List<String>                      mKeyList;
    private  List<String> mValueList;

    public NewPersonInfoGridAdapter(Context mContext, LoanPersonInfo.DataEntity.DataDes dataDes) {
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
            holder.tvValue.setText(value);
        }
        return convertView;

    }


    private class ViewHolder {
        public TextView tvKey;
        public TextView tvValue;
    }

}
