package cn.vpfinance.vpjr.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.gson.NewCarInfoBean;
import cn.vpfinance.vpjr.util.PictureZoomHelper;

/**
 * Created by Administrator on 2016/10/26.
 */
public class NewCarListAdapter extends BaseAdapter {
    private  NewCarInfoBean.DataEntity               mBean;
    private       List<NewCarInfoBean.DataEntity.DataDes> mList;
    private       Context                                 mContext;

    private static final int TYPEONE = 0;
    private static final int TYPETWO = 1;

    public NewCarListAdapter(Context mContext) {
        super();
        if (mList == null) {
            mList = new ArrayList<>();
        }
        this.mContext = mContext;
    }

    public void setData(NewCarInfoBean.DataEntity bean) {
        if(bean == null) return;
        mList = bean.data;
        mBean = bean;
//        if (bean.data != null){
//            List<NewCarInfoBean.DataEntity.DataDes> list = bean.data;
//            for (int i=0; i< list.size(); i++){
//                NewCarInfoBean.DataEntity.DataDes dataDes = list.get(i);
//                if (dataDes != null){
//                    dataDes.value
//                }
//            }
//        }

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

        if (getItemViewType(position) == TYPEONE) {
            OneHolder holder = null;
            if (convertView == null) {
                holder = new OneHolder();
                convertView = LayoutInflater.from
                        (mContext).inflate(R.layout.itme_person_info_title, null, false);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                holder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
                holder.gridView = (GridView) convertView.findViewById(R.id.listview_item_gridview);
                convertView.setTag(holder);
            } else {
                holder = (OneHolder) convertView.getTag();
            }


            if (mList != null) {
                NewCarInfoBean.DataEntity.DataDes dataDes = mList.get(position);
                if (dataDes != null) {
                    if (!TextUtils.isEmpty(dataDes.icon)) {
                        ImageLoader.getInstance().displayImage(dataDes.icon,holder.imgIcon);
                    }
                    holder.tvTitle.setText(dataDes.title);
                    NewCarGridAdapter gridViewAdapter = new NewCarGridAdapter(mContext, dataDes);
                    holder.gridView.setAdapter(gridViewAdapter);
                }

            }
        }else if (getItemViewType(position) == TYPETWO) {
            TwoHolder twoHolder = null;
            if (convertView == null) {
                twoHolder = new TwoHolder();
                convertView = LayoutInflater.from
                        (mContext).inflate(R.layout.item_new_many_pic, null, false);
                twoHolder.title = (TextView) convertView.findViewById(R.id.title);
                twoHolder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
                twoHolder.content = (LinearLayout) convertView.findViewById(R.id.content);
                convertView.setTag(twoHolder);
            } else {
                twoHolder = (TwoHolder) convertView.getTag();
            }

            if (mList.get(position) != null) {
                if (!TextUtils.isEmpty(mList.get(position).icon)) {
                    ImageLoader.getInstance().displayImage(mList.get(position).icon,twoHolder.imgIcon);
                }
                twoHolder.title.setText(mList.get(position).title);
                twoHolder.content.removeAllViews();
                List<String> key = mList.get(position).key;
                final ArrayList<String> value = (ArrayList<String>)mList.get(position).value;
                if (key != null) {

                    for (int i = 0; i < key.size(); i++) {
                        String title = key.get(i);
                        final String imgUrl = value.get(i);

                        View view = LayoutInflater.from(mContext).inflate(R.layout.item_car_info, null);
                        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                        ((TextView) view.findViewById(R.id.textView)).setText(title);
                        ImageLoader.getInstance().displayImage(imgUrl, imageView);
                        twoHolder.content.addView(view);
                        view.findViewById(R.id.itemView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new PictureZoomHelper().showPicture(mContext, imgUrl,value);
                            }
                        });
                    }
                }
            }

        }

        return convertView;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getCount()-1) {
            return TYPETWO;
        } else {
            return TYPEONE;
        }
    }

    private class OneHolder {
        TextView tvTitle;
        GridView gridView;
        ImageView imgIcon;
    }

    private class TwoHolder{
        TextView     title;
        LinearLayout content;
        ImageView imgIcon;
    }

}
