package cn.vpfinance.vpjr.module.voucher.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.gson.NewWritingBean;
import cn.vpfinance.vpjr.util.PictureZoomHelper;

/**
 * Created by Administrator on 2016/10/24.
 * 标的详情  纯图片fragment  对应datatype为2
 */
public class NewPictureFragment extends BaseFragment {

    private static final String PRODUCT_ID = "loanId";
    //    private static final String SHOW_TYPE  = "showType";
    private static final String NET_URL    = "netUrl";

    @Bind(R.id.grid_view)
    GridView       mGridView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_picture_fragment, container, false);
        ButterKnife.bind(this, view);
        mHttpService.getRegularTab(mNetUrl);
        return view;
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

//                if (newWritingBean.dataType == 2) {//2为纯图片，如资质材料这种
//                }
                GridViewAdapter adapter = new GridViewAdapter(mContext);
                mGridView.setAdapter(adapter);
                adapter.setData(newWritingBean.data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.lookOtherProduct)
    public void onClick() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }

    private class GridViewAdapter extends BaseAdapter {

        private Context                       mContext;
        private List<NewWritingBean.DataBean> mData;
        private ArrayList<String> images = new ArrayList<>();

        public GridViewAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<NewWritingBean.DataBean> data) {
            this.mData = data;
            images.clear();
            for (NewWritingBean.DataBean bean : data) {
                String image = bean.value;
                images.add(image);
            }
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_regular_product_qualification_material, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.pager_regular_product_qualification_material_imageview);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.pager_regular_product_qualification_material_textview);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            NewWritingBean.DataBean dataBean = mData.get(position);
            final String imageUrl = dataBean.value;
            final PictureZoomHelper pictureZoomHelper = new PictureZoomHelper();
            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.imageView, pictureZoomHelper.getDisplayImgOptions());
            viewHolder.textView.setText(dataBean.key);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pictureZoomHelper.showPicture(mContext, imageUrl,images);
                }
            });

            convertView.setTag(viewHolder);
            return convertView;
        }

        public class ViewHolder {
            public ImageView imageView;
            public TextView  textView;
        }
    }

    public static NewPictureFragment newInstance(String url) {
        NewPictureFragment fragment = new NewPictureFragment();
        Bundle args = new Bundle();
        //        args.putString(SHOW_TYPE, showType);
        args.putString(NET_URL, url);
        fragment.setArguments(args);
        return fragment;
    }
}
